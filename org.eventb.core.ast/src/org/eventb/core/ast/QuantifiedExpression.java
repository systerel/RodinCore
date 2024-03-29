/*******************************************************************************
 * Copyright (c) 2005, 2024 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added accept for ISimpleVisitor
 *     Systerel - mathematical language v2
 *     Systerel - added support for predicate variables
 *     Systerel - added form filtering
 *     Systerel - generalised getPositions() into inspect()
 *     Systerel - externalized wd lemmas generation
 *     Systerel - added child indexes
 *     Systerel - add given sets to free identifier cache
 *     Systerel - store factory used to build a formula
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.core.ast.QuantifiedExpression.Form.Explicit;
import static org.eventb.core.ast.QuantifiedHelper.areEqualDecls;
import static org.eventb.core.ast.QuantifiedHelper.checkBoundIdentTypes;
import static org.eventb.core.ast.QuantifiedHelper.getBoundIdentsAbove;
import static org.eventb.core.ast.QuantifiedHelper.getSyntaxTreeQuantifiers;
import static org.eventb.core.ast.QuantifiedHelper.rewriteDecls;
import static org.eventb.core.ast.QuantifiedUtil.catenateBoundIdentLists;
import static org.eventb.core.ast.extension.StandardGroup.BRACE_SETS;
import static org.eventb.core.ast.extension.StandardGroup.QUANTIFICATION;
import static org.eventb.internal.core.ast.FormulaChecks.ensureMinLength;
import static org.eventb.internal.core.ast.FormulaChecks.ensureTagInRange;
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.LBRACE;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.extension.StandardGroup;
import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.ITypeCheckingRewriter;
import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.parser.IOperatorInfo;
import org.eventb.internal.core.parser.IParserPrinter;
import org.eventb.internal.core.parser.SubParsers.CSetExplicit;
import org.eventb.internal.core.parser.SubParsers.CSetImplicit;
import org.eventb.internal.core.parser.SubParsers.CSetLambda;
import org.eventb.internal.core.parser.SubParsers.ExplicitQuantExpr;
import org.eventb.internal.core.parser.SubParsers.IQuantifiedParser;
import org.eventb.internal.core.parser.SubParsers.ImplicitQuantExpr;
import org.eventb.internal.core.parser.SubParsers.QuantExpr;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;
import org.eventb.internal.core.typecheck.TypeVariable;

/**
 * QuantifiedExpression is the class for all quantified expressions in an
 * event-B formula.
 * <p>
 * It can accept tags {QUNION, QINTER, CSET}. The list of quantifiers is
 * inherited from QuantifiedFormula.
 * </p>
 * 
 * @author François Terrier
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class QuantifiedExpression extends Expression {
	
	// children + form
	private final BoundIdentDecl[] quantifiedIdentifiers;
	private final Expression expr;
	private final Predicate pred;
	private final Form form;
	
	/**
	 * Enumerations of the possible forms that a quantified expression can take.
	 * <p>
	 * There are several equivalent notations for quantified expressions. This
	 * enumerates all the possible forms it can take.
	 * </p>
	 */
	public static enum Form {
		/**
		 * Formula is a lambda abstraction.
		 */
		Lambda,
		/**
		 * Formula is in its implicit form (i.e. { E | P } where E is an
		 * expression and P is a predicate)
		 */
		Implicit,
		/**
		 * Formula is in its explicit for (i.e. { L \u00b7 P | E } where L
		 * is a list of identifier, E is an expression and P is a predicate.)
		 */
		Explicit
	}

	/**
	 * Implements a checker for lambda patterns. From the end-user point of view, a
	 * lambda pattern must be made of pairwise distinct identifiers conjoined into
	 * maplets. Internally, this translates to having a pattern made of bound
	 * identifiers whose indexes are sorted in decreasing order when traversing the
	 * maplets from left to right.
	 * <p>
	 * This class implements an algorithm for checking that a lambda pattern indeed
	 * fulfills this constraint.
	 * </p>
	 */
	private static class PatternChecker {

		private int expectedIndex;

		public PatternChecker(int nbBoundIdentDecls) {
			this.expectedIndex = nbBoundIdentDecls - 1;
		}

		public boolean verify(Expression expr) {
			if (expr.getTag() != MAPSTO) {
				return false;
			}
			final Expression pattern = ((BinaryExpression) expr).getLeft();
			return traverse(pattern) && expectedIndex == -1;
		}

		private boolean traverse(Expression pattern) {
			switch (pattern.getTag()) {
			case MAPSTO:
				final BinaryExpression maplet = (BinaryExpression) pattern;
				return traverse(maplet.getLeft()) && traverse(maplet.getRight());
			case BOUND_IDENT:
				final BoundIdentifier ident = (BoundIdentifier) pattern;
				return ident.getBoundIndex() == expectedIndex--;
			}
			return false;
		}

	}
	
	// offset of the tag interval in Formula
	private static final int FIRST_TAG = FIRST_QUANTIFIED_EXPRESSION;
	
	private static final String CSET_ID = "Comprehension Set";
	private static final String LAMBDA_ID = "Lambda";
	private static final String QUNION_ID = "Quantified Union";
	private static final String QINTER_ID = "Quantified Intersection";

	private static enum Operators implements IOperatorInfo<QuantifiedExpression> {
		OP_QUNION_EXPL("\u22c3", QUNION_ID, QUANTIFICATION) {
			@Override
			public IQuantifiedParser<QuantifiedExpression> makeParser(int kind) {
				return new ExplicitQuantExpr(kind, QUNION);
			}
		},
		OP_QUNION_IMPL("\u22c3", QUNION_ID, QUANTIFICATION) {
			@Override
			public IQuantifiedParser<QuantifiedExpression> makeParser(int kind) {
				return new ImplicitQuantExpr(kind, QUNION);
			}
		},
		OP_QINTER_EXPL("\u22c2", QINTER_ID, QUANTIFICATION) {
			@Override
			public IQuantifiedParser<QuantifiedExpression> makeParser(int kind) {
				return new ExplicitQuantExpr(kind, QINTER);
			}
		},
		OP_QINTER_IMPL("\u22c2", QINTER_ID, QUANTIFICATION) {
			@Override
			public IQuantifiedParser<QuantifiedExpression> makeParser(int kind) {
				return new ImplicitQuantExpr(kind, QINTER);
			}
		},
		OP_CSET_EXPL(LBRACE.getImage(), CSET_ID, BRACE_SETS) {
			@Override
			public IQuantifiedParser<QuantifiedExpression> makeParser(int kind) {
				return new CSetExplicit(kind);
			}
		},
		OP_CSET_IMPL(LBRACE.getImage(), CSET_ID, BRACE_SETS) {
			@Override
			public IQuantifiedParser<QuantifiedExpression> makeParser(int kind) {
				return new CSetImplicit(kind);
			}
		},
		OP_CSET_LAMBDA("\u03bb", LAMBDA_ID, QUANTIFICATION) {
			@Override
			public IQuantifiedParser<QuantifiedExpression> makeParser(int kind) {
				return new CSetLambda(kind);
			}
		},
		;
		
		private final String image;
		private final String id;
		private final String groupId;
		
		private Operators(String image, String id, StandardGroup group) {
			this.image = image;
			this.id = id;
			this.groupId = group.getId();
		}

		@Override
		public String getImage() {
			return image;
		}
		
		@Override
		public String getId() {
			return id;
		}
		
		@Override
		public String getGroupId() {
			return groupId;
		}

		@Override
		public boolean isSpaced() {
			return false;
		}
		
		public IParserPrinter<QuantifiedExpression> makeParser(int kind,
				String[] localNames) {
			final IParserPrinter<QuantifiedExpression> parser = makeParser(kind);
			((IQuantifiedParser<QuantifiedExpression>) parser).setLocalNames(localNames);
			return parser;
		}
	}

	// For testing purposes
	public static final int TAGS_LENGTH = 3; // FIXME cannot be (easily) computed from Operators

	private static IOperatorInfo<QuantifiedExpression> makeOperInfo(final int tag, final Operators operator) {
		return new IOperatorInfo<QuantifiedExpression>() {

			@Override
			public IParserPrinter<QuantifiedExpression> makeParser(int kind) {
				return new QuantExpr(kind, tag);
			}

			@Override
			public String getImage() {
				return operator.getImage();
			}

			@Override
			public String getId() {
				return operator.getId();
			}

			@Override
			public String getGroupId() {
				return operator.getGroupId();
			}

			@Override
			public boolean isSpaced() {
				return operator.isSpaced();
			}
		};
	}
	
	/**
	 * @since 2.0
	 */
	public static void init(BMath grammar) {
		try {
			grammar.addOperator(makeOperInfo(QUNION, Operators.OP_QUNION_EXPL));
			grammar.addOperator(makeOperInfo(QINTER, Operators.OP_QINTER_EXPL));
			grammar.addOperator(Operators.OP_CSET_LAMBDA);
		
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getOperatorImage() {
		return getOperator().getImage();
	}

	private Operators getOperator() {
		return getOperator(getTag(), form);
	}

	private static Operators getOperator(int tagOvr, Form formOvr) {
		switch (tagOvr) {
		case CSET:
			switch (formOvr) {
			case Explicit:
				return Operators.OP_CSET_EXPL;
			case Implicit:
				return Operators.OP_CSET_IMPL;
			case Lambda:
				return Operators.OP_CSET_LAMBDA;
			default:
				throw newIllegalForm(formOvr);
			}
		case QUNION:
			switch (formOvr) {
			case Explicit:
				return Operators.OP_QUNION_EXPL;
			case Implicit:
				return Operators.OP_QUNION_IMPL;
			default:
				throw newIllegalForm(formOvr);
			}
		case QINTER:
			switch (formOvr) {
			case Explicit:
				return Operators.OP_QINTER_EXPL;
			case Implicit:
				return Operators.OP_QINTER_IMPL;
			default:
				throw newIllegalForm(formOvr);
			}
		default:
			throw newIllegalForm(formOvr);
		}

	}

	@Override
	protected void toString(IToStringMediator mediator) {
		// Collect names used in subformulas and not locally bound
		final Set<String> usedNames = new HashSet<String>();
		final String[] boundNames = mediator.getBoundNames();
		expr.collectNamesAbove(usedNames, boundNames ,
				quantifiedIdentifiers.length);
		final boolean exprIsClosed = usedNames.size() == 0;
		pred.collectNamesAbove(usedNames, boundNames,
				quantifiedIdentifiers.length);
		final String[] localNames = QuantifiedUtil.resolveIdents(
				quantifiedIdentifiers, usedNames, getFactory());

		final Operators operator;
		switch (form) {
		case Lambda:
			operator = getOperator();
			break;
		case Implicit:
			if (exprIsClosed && !mediator.isWithTypes()) {
				// Still OK to use implicit form.
				operator = getOperator();
			} else {
				operator = getOperator(getTag(), Explicit);
			}
			break;
		case Explicit:
			operator = getOperator(getTag(), Explicit);
			break;
		default:
			assert false;
			operator = null;
			break;
		}
		
		final int kind = mediator.getKind();
		
		operator.makeParser(kind, localNames).toString(mediator, this);
	}

	@Override
	protected int getKind(KindMediator mediator) {
		return mediator.getKind(getOperatorImage());
	}

	private static IllegalStateException newIllegalForm(Form form) {
		return new IllegalStateException(
				"Illegal form for quantified expression: " + form);
	}
	
	/**
	 * Must never be called directly: use the factory method instead.
	 * 
	 * @see FormulaFactory#makeQuantifiedExpression(int, BoundIdentDecl[],
	 *      Predicate, Expression, SourceLocation, Form)
	 * @see FormulaFactory#makeQuantifiedExpression(int, java.util.Collection,
	 *      Predicate, Expression, SourceLocation, Form)
	 */
	protected QuantifiedExpression(Expression expr, Predicate pred,
			BoundIdentDecl[] boundIdentifiers, int tag,
			SourceLocation location, Form form, FormulaFactory ff) {
		super(tag, ff, location, combineHashCodes(
				boundIdentifiers.length, 
				pred.hashCode(), 
				expr.hashCode())
		);
		this.quantifiedIdentifiers = boundIdentifiers;
		this.expr = expr;
		this.pred = pred;
		ensureTagInRange(tag, FIRST_TAG, TAGS_LENGTH);
		ensureMinLength(boundIdentifiers, 1);
		ensureSameFactory(this.quantifiedIdentifiers);
		ensureSameFactory(this.pred, this.expr);
		setPredicateVariableCache(this.pred, this.expr);
		synthesizeType(null);

		// Must be after synthesizeType()
		this.form = filterForm(form);
	}

	@Override
	protected void synthesizeType(Type givenType) {
		final int length = quantifiedIdentifiers.length;
		final Formula<?>[] children = new Formula<?>[length + 2];
		System.arraycopy(quantifiedIdentifiers, 0, children, 0, length);
		children[length] = pred;
		children[length + 1] = expr;

		final IdentListMerger freeIdentMerger = mergeFreeIdentifiers(children);
		this.freeIdents = freeIdentMerger.getFreeMergedArray();

		final IdentListMerger boundIdentMerger = 
			IdentListMerger.makeMerger(pred.boundIdents, expr.boundIdents);
		final BoundIdentifier[] boundIdentsBelow = 
			boundIdentMerger.getBoundMergedArray(); 
		this.boundIdents = getBoundIdentsAbove(boundIdentsBelow,
				quantifiedIdentifiers, getFactory());

		if (freeIdentMerger.containsError() || boundIdentMerger.containsError()) {
			// Incompatible type environments, don't bother going further.
			return;
		}
		
		// Check types of identifiers bound here.
		if (! checkBoundIdentTypes(boundIdentsBelow, quantifiedIdentifiers)) {
			return;
		}
		
		// Fast exit if children are not typed
		// (the most common case where type synthesis can't be done)
		if (! pred.isTypeChecked() || ! expr.isTypeChecked()) {
			return;
		}
		
		final Type exprType = expr.getType();
		final Type resultType;
		switch (getTag()) {
		case Formula.QUNION:
		case Formula.QINTER:
			final Type alpha = exprType.getBaseType();
			if (alpha != null) {
				resultType = exprType;
			} else {
				return;
			}
			break;
		case Formula.CSET:
			resultType = getFactory().makePowerSetType(exprType);
			break;
		default:
			assert false;
			return;
		}
		
		setFinalType(resultType, givenType);
	}
	
	private Form filterForm(Form inputForm) {
		switch (inputForm) {
		case Lambda:
			final PatternChecker checker = new PatternChecker(
					quantifiedIdentifiers.length);
			if (getTag() == CSET && checker.verify(expr)) {
				return Form.Lambda;
			}
			// Fall through
		case Implicit:
			if (isValidImplicitExpr()) {
				return Form.Implicit;
			}
			// Fall through
		case Explicit:
			// Fall through
		}
		return Form.Explicit;
	}
	
	/*
	 * Tells whether the expression is valid for implicit form, that is it
	 * contains no free identifiers, all locally bound identifiers and no
	 * externally bound identifier.
	 * 
	 * Strictly speaking, we should also check that the bound identifiers occur
	 * in decreasing order, but this would be too much time consuming. So, we
	 * accept expressions that would give a different order of bound identifiers
	 * when unparsed then parsed back.
	 */
	private boolean isValidImplicitExpr() {
		if (expr.getSyntacticallyFreeIdentifiers().length != 0) {
			return false;
		}

		final BoundIdentifier[] bids = expr.getBoundIdentifiers();
		return bids.length == quantifiedIdentifiers.length
				&& bids[bids.length - 1].getBoundIndex() == bids.length - 1;
	}

	/**
	 * Returns the form of this expression. This form corresponds to the way the
	 * expression was initially parsed. It doesn't have any impact on the
	 * mathematical meaning of this expression, which is always the same,
	 * whatever the form.
	 * 
	 * @return the form of this expression.
	 */
	public Form getForm() {
		return form;
	}
	
	/**
	 * Returns the list of the identifiers which are declared as bound by this formula.
	 * 
	 * @return list of bound identifier declarations
	 */
	public BoundIdentDecl[] getBoundIdentDecls() {
		return quantifiedIdentifiers.clone();
	}
	
	/**
	 * Returns the expression of this node.
	 * 
	 * @return the expression of the quantified formula
	 */
	public Expression getExpression() {
		return expr;
	}
	
	/**
	 * Returns the predicate of this node.
	 * 
	 * @return the predicate of the quantified formula
	 */
	public Predicate getPredicate() {
		return pred;
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String typeName = getType()!=null?" [type: "+getType().toString()+"]":"";
		final String[] boundNamesBelow = catenateBoundIdentLists(boundNames, quantifiedIdentifiers);
		
		return tabs
				+ this.getClass().getSimpleName()
				+ " ["
				+ getOperatorImage() 
				+ ", " + form.toString()
				+ "]" 
				+ typeName
				+ "\n"
				+ getSyntaxTreeQuantifiers(boundNames, tabs + "\t", quantifiedIdentifiers)
				+ expr.getSyntaxTree(boundNamesBelow,tabs + "\t")
				+ pred.getSyntaxTree(boundNamesBelow,tabs + "\t");
	}
	
	/**
	 * @since 2.0
	 */
	@Override
	protected void isLegible(LegibilityResult result) {
		final LegibilityResult resultCopy = new LegibilityResult(result);
		for (BoundIdentDecl decl : quantifiedIdentifiers) {
			decl.isLegible(resultCopy);
		}
		pred.isLegible(resultCopy);
		expr.isLegible(resultCopy);
		for (ASTProblem problem : resultCopy.getProblems()) {
			result.addProblem(problem);
		}
	}
	
	@Override
	boolean equalsInternalExpr(Expression expression) {
		final QuantifiedExpression other = (QuantifiedExpression) expression;
		return areEqualDecls(quantifiedIdentifiers, other.quantifiedIdentifiers)
				&& expr.equals(other.expr) && pred.equals(other.pred);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdents) {
		for (BoundIdentDecl decl: quantifiedIdentifiers) {
			decl.typeCheck(result, quantifiedIdents);
		}
		
		final BoundIdentDecl[] newQuantifiers = 
			catenateBoundIdentLists(quantifiedIdents, quantifiedIdentifiers);
		pred.typeCheck(result,newQuantifiers);
		expr.typeCheck(result,newQuantifiers);

		Type resultType;
		switch (getTag()) {
		case Formula.QUNION:
		case Formula.QINTER:
			final TypeVariable alpha = result.newFreshVariable(null);
			resultType = result.makePowerSetType(alpha);
			result.unify(expr.getType(), resultType, this);
			break;
		case Formula.CSET:
			resultType = result.makePowerSetType(expr.getType());
			break;
		default:
			assert false;
			return;
		}
		setTemporaryType(resultType, result);
	}
	
	@Override
	protected void solveChildrenTypes(TypeUnifier unifier) {
		for (BoundIdentDecl ident: quantifiedIdentifiers) {
			ident.solveType(unifier);
		}
		expr.solveType(unifier);
		pred.solveType(unifier);
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		// Take care to go from left to right
		switch (form) {
		case Lambda:
		case Explicit:
			pred.collectFreeIdentifiers(freeIdentSet);
			expr.collectFreeIdentifiers(freeIdentSet);
			break;

		case Implicit:
			expr.collectFreeIdentifiers(freeIdentSet);
			pred.collectFreeIdentifiers(freeIdentSet);
			break;

		default:
			assert false;
		}
	}

	/**
	 * Returns the list of all names that either occur free in this formula, or
	 * have been quantified somewhere above this node (that is closer to the
	 * root of the tree).
	 * 
	 * @param boundNames
	 *            array of names that are declared above this formula. These
	 *            names must be stored in the order in which they appear when
	 *            the formula is written from left to right
	 * @return the list of all names that occur in this formula and are not
	 *         declared within.
	 */
	public Set<String> collectNamesAbove(String[] boundNames) {
		Set<String> result = new HashSet<String>();
		expr.collectNamesAbove(result, boundNames, quantifiedIdentifiers.length);
		pred.collectNamesAbove(result, boundNames, quantifiedIdentifiers.length);
		return result;
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		final int newOffset = offset + quantifiedIdentifiers.length;
		pred.collectNamesAbove(names, boundNames, newOffset);
		expr.collectNamesAbove(names, boundNames, newOffset);
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case QUNION: goOn = visitor.enterQUNION(this); break;
		case QINTER: goOn = visitor.enterQINTER(this); break;
		case CSET:   goOn = visitor.enterCSET(this);   break;
		default:     assert false;
		}

		for (int i = 0; goOn && i < quantifiedIdentifiers.length; i++) {
			goOn = quantifiedIdentifiers[i].accept(visitor);
			if (goOn) goOn = acceptContinue(visitor);
		}
		if (goOn) goOn = pred.accept(visitor);
		if (goOn) goOn = acceptContinue(visitor);
		if (goOn) expr.accept(visitor);
		
		switch (getTag()) {
		case QUNION: return visitor.exitQUNION(this);
		case QINTER: return visitor.exitQINTER(this);
		case CSET:   return visitor.exitCSET(this);
		default:     return true;
		}
	}
	
	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitQuantifiedExpression(this);
	}

	private boolean acceptContinue(IVisitor visitor) {
		switch (getTag()) {
		case QUNION: return visitor.continueQUNION(this);
		case QINTER: return visitor.continueQINTER(this);
		case CSET:   return visitor.continueCSET(this);   
		default:     assert false; return true;
		}
	}

	@Override
	protected Expression rewrite(ITypeCheckingRewriter rewriter) {
		BoundIdentDecl[] newDecls = rewriteDecls(quantifiedIdentifiers, rewriter);
		final int nbOfBoundIdentDecls = quantifiedIdentifiers.length;
		rewriter.enteringQuantifier(nbOfBoundIdentDecls);
		final Predicate newPred = pred.rewrite(rewriter);
		final Expression newExpr = expr.rewrite(rewriter);
		rewriter.leavingQuantifier(nbOfBoundIdentDecls);

		// TODO: implement cleanup of unused bound ident decls.

		final QuantifiedExpression before;
		if (newDecls == quantifiedIdentifiers && newPred == pred
				&& newExpr == expr) {
			before = this;
		} else {
			before = rewriter.getFactory().makeQuantifiedExpression(getTag(),
					newDecls, newPred, newExpr, getSourceLocation(), form);
		}
		return rewriter.rewrite(this, before);
	}

	// TODO add instantiation of condition

	// TODO add instantiation of subexpression

	@Override
	protected final <F> void inspect(FindingAccumulator<F> acc) {
		acc.inspect(this);
		if (acc.childrenSkipped()) {
			return;
		}
		acc.enterChildren();
		for (BoundIdentDecl decl: quantifiedIdentifiers) {
			decl.inspect(acc);
			if (acc.allSkipped()) {
				break;
			}
			acc.nextChild();
		}
		if (!acc.allSkipped()) {
			pred.inspect(acc);
		}
		acc.nextChild();
		if (!acc.allSkipped()) {
			expr.inspect(acc);
		}
		acc.leaveChildren();
	}
	
	@Override
	public Formula<?> getChild(int index) {
		if (index < quantifiedIdentifiers.length) {
			return quantifiedIdentifiers[index];
		}
		index = index - quantifiedIdentifiers.length;
		switch (index) {
		case 0:
			return pred;
		case 1:
			return expr;
		default:
			throw invalidIndex(index);
		}
	}

	@Override
	public int getChildCount() {
		return quantifiedIdentifiers.length + 2;
	}

	@Override
	protected IPosition getDescendantPos(SourceLocation sloc, IntStack indexes) {
		IPosition pos;
		if (form == Form.Explicit) {
			indexes.push(0);
			for (BoundIdentDecl decl: quantifiedIdentifiers) {
				pos = decl.getPosition(sloc, indexes);
				if (pos != null)
					return pos;
				indexes.incrementTop();
			}
		} else {
			indexes.push(quantifiedIdentifiers.length);
		}
		pos = pred.getPosition(sloc, indexes);
		if (pos != null)
			return pos;
		indexes.incrementTop();
		if (form != Form.Lambda) {
			pos = expr.getPosition(sloc, indexes);
			if (pos != null)
				return pos;
		} else {
			// For a lambda expression, we have to skip over the maplet expression
			BinaryExpression maplet = (BinaryExpression) expr;
			indexes.push(0);
			pos = maplet.getLeft().getPosition(sloc, indexes);
			if (pos != null)
				return pos;
			indexes.incrementTop();
			pos = maplet.getRight().getPosition(sloc, indexes);
			if (pos != null)
				return pos;
			indexes.pop();
		}
		indexes.pop();
		return new Position(indexes);
	}

	@Override
	protected Expression rewriteChild(int index, SingleRewriter rewriter) {
		BoundIdentDecl[] newDecls = quantifiedIdentifiers;
		Predicate newPred = pred;
		Expression newExpr = expr;
		final int length = quantifiedIdentifiers.length;
		if (index < length) {
			newDecls = quantifiedIdentifiers.clone();
			newDecls[index] = rewriter.rewrite(quantifiedIdentifiers[index]);
		} else if (index == length) {
			newPred = rewriter.rewrite(pred);
		} else if (index == length + 1) {
			newExpr = rewriter.rewrite(expr);
		} else {
			throw new IllegalArgumentException("Position is outside the formula");
		}
		return getFactory().makeQuantifiedExpression(getTag(), newDecls,
				newPred, newExpr, getSourceLocation(), form);
	}

	@Override
	public boolean isWDStrict() {
		return false;
	}

}
