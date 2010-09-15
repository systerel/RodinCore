/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added accept for ISimpleVisitor
 *     Systerel - added support for predicate variables
 *     Systerel - generalised getPositions() into inspect()
 *     Systerel - externalized wd lemmas generation
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.core.ast.QuantifiedHelper.addUsedBoundIdentifiers;
import static org.eventb.core.ast.QuantifiedHelper.areAllUsed;
import static org.eventb.core.ast.QuantifiedHelper.areEqualQuantifiers;
import static org.eventb.core.ast.QuantifiedHelper.checkBoundIdentTypes;
import static org.eventb.core.ast.QuantifiedHelper.getBoundIdentsAbove;
import static org.eventb.core.ast.QuantifiedHelper.getSyntaxTreeQuantifiers;
import static org.eventb.core.ast.QuantifiedUtil.catenateBoundIdentLists;
import static org.eventb.internal.core.parser.BMath.QUANTIFIED_PRED;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.BoundIdentDeclRemover;
import org.eventb.internal.core.ast.BoundIdentSubstitution;
import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.parser.IOperatorInfo;
import org.eventb.internal.core.parser.IParserPrinter;
import org.eventb.internal.core.parser.SubParsers.IQuantifiedParser;
import org.eventb.internal.core.parser.SubParsers.QuantifiedPredicateParser;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * QuantifiedPredicate is the class for all quantified predicates in an event-B
 * formula.
 * <p>
 * It can accept tags {FORALL, EXISTS}. The list of quantifiers is inherited
 * from QuantifiedFormula.
 * </p>
 * 
 * @author François Terrier
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class QuantifiedPredicate extends Predicate {
	
	// child
	private final BoundIdentDecl[] quantifiedIdentifiers;
	private final Predicate pred;
	
	// offset in the corresponding tag interval
	private final static int FIRST_TAG = FIRST_QUANTIFIED_PREDICATE;
	
	/**
	 * @since 2.0
	 */
	public static final String FORALL_ID = "for all";
	/**
	 * @since 2.0
	 */
	public static final String EXISTS_ID = "exists";

	private static enum Operators implements IOperatorInfo<QuantifiedPredicate> {
		OP_FORALL("\u2200", FORALL_ID, QUANTIFIED_PRED, FORALL),
		OP_EXISTS("\u2203", EXISTS_ID, QUANTIFIED_PRED, EXISTS),
		;
		
		private final String image;
		private final String id;
		private final String groupId;
		private final int tag;
		
		private Operators(String image, String id, String groupId, int tag) {
			this.image = image;
			this.id = id;
			this.groupId = groupId;
			this.tag = tag;
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
		public IParserPrinter<QuantifiedPredicate> makeParser(int kind) {
			return new QuantifiedPredicateParser(kind, tag);
		}
		
		public IParserPrinter<QuantifiedPredicate> makeParser(int kind,
				String[] localNames) {
			final IParserPrinter<QuantifiedPredicate> parser = makeParser(kind);
			((IQuantifiedParser<QuantifiedPredicate>) parser).setLocalNames(localNames);
			return parser;
		}

		@Override
		public boolean isSpaced() {
			return false;
		}
	}

	// For testing purposes
	public static final int TAGS_LENGTH = Operators.values().length;

	/**
	 * @since 2.0
	 */
	public static void init(BMath grammar) {
		try {		
			for(Operators operInfo: Operators.values()) {
				grammar.addOperator(operInfo);
			}
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected QuantifiedPredicate(Predicate pred, BoundIdentDecl[] boundIdentifiers, int tag,
			SourceLocation location, FormulaFactory ff) {
		super(tag, location,
				combineHashCodes(boundIdentifiers.length, pred.hashCode()));

		this.quantifiedIdentifiers = boundIdentifiers.clone();
		this.pred = pred;
		
		checkPreconditions();
		setPredicateVariableCache(this.pred);
		synthesizeType(ff);
	}

	protected QuantifiedPredicate(Predicate pred,
			Collection<BoundIdentDecl> boundIdentifiers, int tag,
			SourceLocation location, FormulaFactory ff) {
		super(tag, location,
				combineHashCodes(boundIdentifiers.size(), pred.hashCode()));
		
		BoundIdentDecl[] model = new BoundIdentDecl[boundIdentifiers.size()];
		this.quantifiedIdentifiers = boundIdentifiers.toArray(model);
		this.pred = pred;
		
		checkPreconditions();
		setPredicateVariableCache(this.pred);
		synthesizeType(ff);
	}

	private void checkPreconditions() {
		assert getTag() >= FIRST_TAG && getTag() < FIRST_TAG+TAGS_LENGTH;
		assert quantifiedIdentifiers != null;
		assert 1 <= quantifiedIdentifiers.length;
		assert pred != null;
	}

	@Override
	protected void synthesizeType(FormulaFactory ff) {
		this.freeIdents = pred.freeIdents;

		final BoundIdentifier[] boundIdentsBelow = pred.boundIdents; 
		this.boundIdents = 
			getBoundIdentsAbove(boundIdentsBelow, quantifiedIdentifiers, ff);

		// Check types of identifiers bound here.
		if (! checkBoundIdentTypes(boundIdentsBelow, quantifiedIdentifiers)) {
			return;
		}

		if (! pred.isTypeChecked())
			return;
		
		typeChecked = true;
	}
	
	/**
	 * Returns the list of the identifiers which are bound by this formula.
	 * 
	 * @return list of bound identifiers
	 */
	public BoundIdentDecl[] getBoundIdentDecls() {
		return quantifiedIdentifiers.clone();
	}
	
	/**
	 * Returns the predicate which is quantified here.
	 * 
	 * @return the child predicate
	 */
	public Predicate getPredicate() {
		return pred;
	}
	
	private String getOperatorImage() {
		return getOperator().getImage();
	}

	private Operators getOperator() {
		return Operators.values()[getTag()-FIRST_TAG];
	}

	@Override
	protected void toString(IToStringMediator mediator) {
		// Collect names used in subformulas and not locally bound
		final Set<String> usedNames = new HashSet<String>();
		final String[] boundNames = mediator.getBoundNames();
		pred.collectNamesAbove(usedNames, boundNames ,
				quantifiedIdentifiers.length);

		final String[] localNames = QuantifiedUtil.resolveIdents(quantifiedIdentifiers, usedNames);

		final Operators operator = getOperator();
		final int kind = mediator.getKind();
		
		operator.makeParser(kind, localNames).toString(mediator, this);
	}

	@Override
	protected int getKind(KindMediator mediator) {
		return mediator.getKind(getOperatorImage());
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		String[] boundNamesBelow = catenateBoundIdentLists(boundNames, quantifiedIdentifiers);
		return tabs
				+ this.getClass().getSimpleName()
				+ " ["
				+ getOperatorImage()
				+ "]\n"
				+ getSyntaxTreeQuantifiers(boundNamesBelow,tabs + "\t",quantifiedIdentifiers)
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
		for (ASTProblem problem : resultCopy.getProblems()) {
				result.addProblem(problem);
		}
	}
	
	@Override
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		if (this.getTag() != other.getTag()) {
			return false;
		}
		QuantifiedPredicate temp = (QuantifiedPredicate) other;
		return areEqualQuantifiers(quantifiedIdentifiers,
				temp.quantifiedIdentifiers, withAlphaConversion)
				&& pred.equals(temp.pred, withAlphaConversion);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] boundAbove) {
		for (BoundIdentDecl ident : quantifiedIdentifiers) {
			ident.typeCheck(result, boundAbove);
		}
		BoundIdentDecl[] boundBelow = catenateBoundIdentLists(boundAbove, quantifiedIdentifiers);
		pred.typeCheck(result, boundBelow);
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		boolean success = true;
		for (BoundIdentDecl ident: quantifiedIdentifiers) {
			success &= ident.solveType(unifier);
		}
		success &= pred.solveType(unifier);
		return success;
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		pred.collectFreeIdentifiers(freeIdentSet);
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
		pred.collectNamesAbove(result, boundNames, quantifiedIdentifiers.length);
		return result;
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		final int newOffset = offset + quantifiedIdentifiers.length;
		pred.collectNamesAbove(names, boundNames, newOffset);
	}

	@Override
	protected Predicate bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		final int newOffset = offset + quantifiedIdentifiers.length; 
		Predicate newPred = pred.bindTheseIdents(binding, newOffset, factory);
		if (newPred == pred) {
			return this;
		}
		return factory.makeQuantifiedPredicate(getTag(), quantifiedIdentifiers, newPred, getSourceLocation());
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case FORALL: goOn = visitor.enterFORALL(this); break;
		case EXISTS: goOn = visitor.enterEXISTS(this); break;
		default:     assert false;
		}

		for (int i = 0; goOn && i < quantifiedIdentifiers.length; i++) {
			goOn = quantifiedIdentifiers[i].accept(visitor);
			if (goOn) {
				switch (getTag()) {
				case FORALL: goOn = visitor.continueFORALL(this); break;
				case EXISTS: goOn = visitor.continueEXISTS(this); break;
				default:     assert false;
				}
			}
		}
		if (goOn) goOn = pred.accept(visitor);
		
		switch (getTag()) {
		case FORALL: return visitor.exitFORALL(this);
		case EXISTS: return visitor.exitEXISTS(this);
		default:     return true;
		}
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitQuantifiedPredicate(this);		
	}

	/**
	 * Instantiates this quantified predicate with the given expressions.
	 * <p>
	 * Instantiating means substituting all occurrences of some identifiers
	 * bound by this quantified predicate by their corresponding replacement
	 * expressions.
	 * </p>
	 * <p>
	 * The replacements to do are specified by the given array. This array must
	 * have exactly the same length as the array returned by
	 * {@link #getBoundIdentDecls()}. Each element of the given array
	 * specifies the replacement expression for the bound identifier declaration
	 * with the same index. The element can be <code>null</code>, in which
	 * case the corresponding bound identifier declaration will be kept (no
	 * substitution will be done for it).
	 * </p>
	 * <p>
	 * For instance, if this method is applied to the predicate
	 * <pre>
	 *     ∀x,y· x = y + 1
	 * </pre>
	 * with the replacement <code>{null, "a"}</code>, then the result is
	 * <pre>
	 *     ∀x· x = a + 1
	 * </pre>
	 * If, instead, the replacement is <code>{"a", "b"}</code>, then the
	 * result is
	 * <pre>
	 * a = b + 1
	 * </pre>
	 * </p>
	 * 
	 * @param replacements
	 *            an array of replacement expressions. Its length must be the
	 *            number of identifiers bound by this quantified expression.
	 *            Some elements can be <code>null</code>
	 * @param formulaFactory
	 *            formula factory to use for building the result
	 * @return This formula after application of the substitution.
	 */
	public Predicate instantiate(Expression[] replacements, FormulaFactory formulaFactory) {
		BoundIdentSubstitution subst = 
			new BoundIdentSubstitution(quantifiedIdentifiers, replacements, formulaFactory);
		Predicate newPred = pred.rewrite(subst);
		List<BoundIdentDecl> newBoundIdentDecls = subst.getNewDeclarations();
		if (newBoundIdentDecls.isEmpty())
			return newPred;
		return formulaFactory.makeQuantifiedPredicate(getTag(), newBoundIdentDecls, newPred, getSourceLocation());
	}
	
	@Override
	public Predicate rewrite(IFormulaRewriter rewriter) {
		final int nbOfBoundIdentDecls = quantifiedIdentifiers.length;
		
		rewriter.enteringQuantifier(nbOfBoundIdentDecls);
		Predicate newPred = pred.rewrite(rewriter);
		rewriter.leavingQuantifier(nbOfBoundIdentDecls);

		final FormulaFactory ff = rewriter.getFactory();
		BoundIdentDecl[] newDecls = quantifiedIdentifiers;
		if (rewriter.autoFlatteningMode()) {
			final boolean[] used = new boolean[nbOfBoundIdentDecls];
			addUsedBoundIdentifiers(used, newPred);
			if (! areAllUsed(used)) {
				final BoundIdentDeclRemover subst = 
					new BoundIdentDeclRemover(quantifiedIdentifiers, used, ff);
				newPred = newPred.rewrite(subst);
				final List<BoundIdentDecl> newDeclL = subst.getNewDeclarations();
				final int size = newDeclL.size();
				if (size == 0) {
					// Child predicate as already been rewritten
					return newPred;
				} else {
					newDecls = newDeclL.toArray(new BoundIdentDecl[size]);
				}
			}

			if (newPred.getTag() == getTag()) {
				QuantifiedPredicate quantChild = (QuantifiedPredicate) newPred;
				newDecls = catenateBoundIdentLists(
						newDecls, quantChild.quantifiedIdentifiers);
				newPred = quantChild.pred;
			}
		}

		final QuantifiedPredicate before;
		if (newDecls == quantifiedIdentifiers && newPred == pred) {
			before = this;
		} else {
			final SourceLocation sloc = getSourceLocation();
			before = ff.makeQuantifiedPredicate(getTag(),
					newDecls, newPred, sloc);
		}
		return checkReplacement(rewriter.rewrite(before));
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		for (BoundIdentDecl decl: quantifiedIdentifiers) {
			decl.addGivenTypes(set);
		}
		pred.addGivenTypes(set);
	}

	@Override
	protected final <F> void inspect(FindingAccumulator<F> acc) {
		acc.inspect(this);

		acc.enterChildren();
		for (BoundIdentDecl decl: quantifiedIdentifiers) {
			decl.inspect(acc);
			acc.nextChild();
		}
		pred.inspect(acc);
		acc.leaveChildren();
	}

	@Override
	protected Formula<?> getChild(int index) {
		if (index < quantifiedIdentifiers.length) {
			return quantifiedIdentifiers[index];
		}
		if (index == quantifiedIdentifiers.length) {
			return pred;
		}
		return null;
	}

	@Override
	protected IPosition getDescendantPos(SourceLocation sloc, IntStack indexes) {
		IPosition pos;
		indexes.push(0);
		for (BoundIdentDecl decl: quantifiedIdentifiers) {
			pos = decl.getPosition(sloc, indexes);
			if (pos != null)
				return pos;
			indexes.incrementTop();
		}
		pos = pred.getPosition(sloc, indexes);
		if (pos != null)
			return pos;
		indexes.pop();
		return new Position(indexes);
	}

	@Override
	protected Predicate rewriteChild(int index, SingleRewriter rewriter) {
		BoundIdentDecl[] newDecls = quantifiedIdentifiers;
		Predicate newPred = pred;
		final int length = quantifiedIdentifiers.length;
		if (index < length) {
			newDecls = quantifiedIdentifiers.clone();
			newDecls[index] = rewriter.rewrite(quantifiedIdentifiers[index]);
		} else if (index == length) {
			newPred = rewriter.rewrite(pred);
		} else {
			throw new IllegalArgumentException("Position is outside the formula");
		}
		return rewriter.factory.makeQuantifiedPredicate(getTag(),
				newDecls, newPred, getSourceLocation());
	}

}
