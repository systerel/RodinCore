/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
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
 *     Systerel - generalised getPositions() into inspect()
 *     Systerel - externalized wd lemmas generation
 *     Systerel - added child indexes
 *     Systerel - add given sets to free identifier cache
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.core.ast.extension.StandardGroup.ATOMIC_EXPR;
import static org.eventb.internal.core.ast.GivenTypeHelper.getGivenTypeIdentifiers;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.extension.StandardGroup;
import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.ITypeCheckingRewriter;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.parser.IOperatorInfo;
import org.eventb.internal.core.parser.IParserPrinter;
import org.eventb.internal.core.parser.SubParsers.AtomicExpressionParser;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;
import org.eventb.internal.core.typecheck.TypeVariable;

/**
 * AtomicExpression is the class for all atomic expressions in an event-B
 * formula.
 * <p>
 * It is a terminal expression and therefore has no children. It can only accept
 * {INTEGER, NATURAL, NATURAL1, BOOL, TRUE, FALSE, EMPTYSET, KPRED, KSUCC,
 * KPRJ1_GEN, KPRJ2_GEN, KID_GEN}.
 * </p>
 * 
 * @author François Terrier
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class AtomicExpression extends Expression {
	
	// offset of the corresponding tag-interval in Formula
	private static final int FIRST_TAG = FIRST_ATOMIC_EXPRESSION;
	
	// For testing purposes
	public static final int TAGS_LENGTH = Operators.values().length;
	
	private static final String INTEGER_ID = "Integer";
	private static final String NATURAL_ID = "Natural";
	private static final String NATURAL1_ID = "Natural1";
	private static final String BOOL_ID = "Bool Type";
	private static final String TRUE_ID = "True";
	private static final String FALSE_ID = "False";
	private static final String EMPTYSET_ID = "Empty Set";
	private static final String KPRED_ID = "Predecessor";
	private static final String KSUCC_ID = "Successor";
	private static final String KPRJ1_GEN_ID = "Projection 1";
	private static final String KPRJ2_GEN_ID = "Projection 2";
	private static final String KID_GEN_ID = "Identity";

	private static enum Operators implements IOperatorInfo<AtomicExpression> {
		OP_INTEGER("\u2124", INTEGER_ID, ATOMIC_EXPR, INTEGER),
		OP_NATURAL("\u2115", NATURAL_ID, ATOMIC_EXPR, NATURAL),
		OP_NATURAL1("\u21151", NATURAL1_ID, ATOMIC_EXPR, NATURAL1),
		OP_BOOL("BOOL", BOOL_ID, ATOMIC_EXPR, BOOL),
		OP_TRUE("TRUE", TRUE_ID, ATOMIC_EXPR, TRUE),
		OP_FALSE("FALSE", FALSE_ID, ATOMIC_EXPR, FALSE),
		OP_EMPTYSET("\u2205", EMPTYSET_ID, ATOMIC_EXPR, EMPTYSET),
		OP_KPRED("pred", KPRED_ID, ATOMIC_EXPR, KPRED),
		OP_KSUCC("succ", KSUCC_ID, ATOMIC_EXPR, KSUCC),
		OP_KPRJ1_GEN("prj1", KPRJ1_GEN_ID, ATOMIC_EXPR, KPRJ1_GEN),
		OP_KPRJ2_GEN("prj2", KPRJ2_GEN_ID, ATOMIC_EXPR, KPRJ2_GEN),
		OP_KID_GEN("id", KID_GEN_ID, ATOMIC_EXPR, KID_GEN),
		;
		
		private final String image;
		private final String id;
		private final String groupId;
		private final int tag;
		
		private Operators(String image, String id, StandardGroup group, int tag) {
			this.image = image;
			this.id = id;
			this.groupId = group.getId();
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
		public IParserPrinter<AtomicExpression> makeParser(int kind) {
			return new AtomicExpressionParser(kind, tag);
		}

		@Override
		public boolean isSpaced() {
			return false;
		}
	}
	
	/**
	 * @since 2.0
	 */
	public static void initV1(BMath grammar) {
		try {
			grammar.addOperator(Operators.OP_INTEGER);
			grammar.addOperator(Operators.OP_NATURAL);
			grammar.addOperator(Operators.OP_NATURAL1);
			grammar.addOperator(Operators.OP_BOOL);
			grammar.addOperator(Operators.OP_TRUE);
			grammar.addOperator(Operators.OP_FALSE);
			grammar.addOperator(Operators.OP_EMPTYSET);
			grammar.addOperator(Operators.OP_KPRED);
			grammar.addOperator(Operators.OP_KSUCC);
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * @since 2.0
	 */
	public static void initV2(BMath grammar) {
		try {
			initV1(grammar);
			grammar.addOperator(Operators.OP_KPRJ1_GEN);
			grammar.addOperator(Operators.OP_KPRJ2_GEN);
			grammar.addOperator(Operators.OP_KID_GEN);
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected AtomicExpression(int tag, SourceLocation location, Type type,
			FormulaFactory factory) {
		super(tag, location, 0);
		assert tag >= FIRST_TAG && tag < FIRST_TAG+TAGS_LENGTH;

		setPredicateVariableCache();
		synthesizeType(factory, type);

		// ensures that type was coherent (final type cannot be null if given
		// type was not)
		assert type == null || type == this.getType();
	}
	
	@Override
	protected void synthesizeType(FormulaFactory ff, Type givenType) {
		// May change at the end of this method
		this.freeIdents = NO_FREE_IDENT;
		this.boundIdents = NO_BOUND_IDENT;

		final Type resultType;
		switch (getTag()) {
		case Formula.INTEGER:
		case Formula.NATURAL:
		case Formula.NATURAL1:
			resultType = ff.makePowerSetType(ff.makeIntegerType());
			break;
		case Formula.BOOL:
			resultType = ff.makePowerSetType(ff.makeBooleanType());
			break;
		case Formula.TRUE:
		case Formula.FALSE:
			resultType = ff.makeBooleanType();
			break;
		case Formula.EMPTYSET:
			assert givenType == null || givenType instanceof PowerSetType;
			resultType = givenType;
			break;
		case Formula.KPRED:
		case Formula.KSUCC:
			resultType = ff.makeRelationalType(
					ff.makeIntegerType(),
					ff.makeIntegerType()
			);
			break;
		case Formula.KPRJ1_GEN:
			if (givenType != null) {
				assertPrjType(givenType, true);
			}
			resultType = givenType;
			break;
		case Formula.KPRJ2_GEN:
			if (givenType != null) {
				assertPrjType(givenType, false);
			}
			resultType = givenType;
			break;
		case Formula.KID_GEN:
			if (givenType != null) {
				final Type source = givenType.getSource();
				assert source != null && source.equals(givenType.getTarget());
			}
			resultType = givenType;
			break;
		default:
			assert false;
			return;
		}
		if (resultType != null) {
			this.freeIdents = getGivenTypeIdentifiers(resultType, ff);
			setFinalType(resultType, givenType);
		}
	}

	private static void assertPrjType(Type givenType, boolean left) {
		final Type source = givenType.getSource();
		final Type target = givenType.getTarget();
		assert target != null && source instanceof ProductType;

		final ProductType prodSource = (ProductType) source;
		final Type child;
		if (left) {
			child = prodSource.getLeft();
		} else {
			child = prodSource.getRight();
		}
		assert target.equals(child);
	}

	/**
	 * @since 2.0
	 */
	@Override
	protected void isLegible(LegibilityResult result) {
		return;
	}

	@Override
	boolean equalsInternalExpr(Expression expr) {
		return true;
	}

	@Override
	protected void typeCheck(TypeCheckResult result,
			BoundIdentDecl[] quantifiedIdentifiers) {

		final TypeVariable alpha, beta;
		final Type srcType;
		final Type resultType;
		switch (getTag()) {
		case Formula.INTEGER:
		case Formula.NATURAL:
		case Formula.NATURAL1:
			resultType = result.makePowerSetType(result.makeIntegerType());
			break;
		case Formula.BOOL:
			resultType = result.makePowerSetType(result.makeBooleanType());
			break;
		case Formula.TRUE:
		case Formula.FALSE:
			resultType = result.makeBooleanType();
			break;
		case Formula.EMPTYSET:
			alpha = result.newFreshVariable(getSourceLocation());
			resultType = result.makePowerSetType(alpha);
			break;
		case Formula.KPRED:
		case Formula.KSUCC:
			resultType = result.makeRelationalType(
					result.makeIntegerType(),
					result.makeIntegerType()
			);
			break;
		case Formula.KPRJ1_GEN:
			alpha = result.newFreshVariable(getSourceLocation());
			beta = result.newFreshVariable(getSourceLocation());
			srcType = result.makeProductType(alpha, beta);
			resultType = result.makeRelationalType(srcType, alpha);
			break;
		case Formula.KPRJ2_GEN:
			alpha = result.newFreshVariable(getSourceLocation());
			beta = result.newFreshVariable(getSourceLocation());
			srcType = result.makeProductType(alpha, beta);
			resultType = result.makeRelationalType(srcType, beta);
			break;
		case Formula.KID_GEN:
			alpha = result.newFreshVariable(getSourceLocation());
			resultType = result.makeRelationalType(alpha, alpha);
			break;
		default:
			assert false;
			return;
		}
		setTemporaryType(resultType, result);
		result.analyzeExpression(this);
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		return true;
	}

	@Override
	protected void toString(IToStringMediator mediator) {
		final Operators operator = getOperator();
		final int kind = mediator.getKind();
		
		operator.makeParser(kind).toString(mediator, this);
	}

	@Override
	protected int getKind(KindMediator mediator) {
		return mediator.getKind(getOperatorImage());
	}

	private String getOperatorImage() {
		return getOperator().getImage();
	}

	private Operators getOperator() {
		return Operators.values()[getTag()-FIRST_TAG];
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String typeName = getType()!=null?" [type: "+getType().toString()+"]":"";
		return tabs + this.getClass().getSimpleName() + " ["
				+ getOperatorImage() + "]"  + typeName + "\n";
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		// Nothing to do
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		// Nothing to do
	}
	
	@Override
	public boolean accept(IVisitor visitor) {
		switch (getTag()) {
		case INTEGER:  return visitor.visitINTEGER(this);
		case NATURAL:  return visitor.visitNATURAL(this);
		case NATURAL1: return visitor.visitNATURAL1(this);
		case BOOL:     return visitor.visitBOOL(this);
		case TRUE:     return visitor.visitTRUE(this);
		case FALSE:    return visitor.visitFALSE(this);
		case EMPTYSET: return visitor.visitEMPTYSET(this);
		case KPRED:    return visitor.visitKPRED(this);
		case KSUCC:    return visitor.visitKSUCC(this);
		case KPRJ1_GEN:return visitor.visitKPRJ1_GEN(this);
		case KPRJ2_GEN:return visitor.visitKPRJ2_GEN(this);
		case KID_GEN:  return visitor.visitKID_GEN(this);
		default:       return true;
		}
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitAtomicExpression(this);
	}

	@Override
	protected Expression rewrite(ITypeCheckingRewriter rewriter) {
		return rewriter.rewrite(this);
	}

	@Override
	public boolean isATypeExpression() {
		int tag = getTag();
		return tag == INTEGER || tag == BOOL;
	}

	@Override
	public Type toType(FormulaFactory factory) throws InvalidExpressionException {
		switch (getTag()) {
		case INTEGER:
			return factory.makeIntegerType();
		case BOOL:
			return factory.makeBooleanType();
		default:
			throw new InvalidExpressionException();
		}
	}
	
	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		getType().addGivenTypes(set);
	}

	@Override
	protected final <F> void inspect(FindingAccumulator<F> acc) {
		acc.inspect(this);
		if (acc.childrenSkipped()) {
			return;
		}
	}

	@Override
	public Formula<?> getChild(int index) {
		throw invalidIndex(index);
	}

	@Override
	public int getChildCount() {
		return 0;
	}

	@Override
	protected IPosition getDescendantPos(SourceLocation sloc, IntStack indexes) {
		return new Position(indexes);
	}

	@Override
	protected Expression rewriteChild(int index, SingleRewriter rewriter) {
		throw new IllegalArgumentException("Position is outside the formula");
	}

	@Override
	public boolean isWDStrict() {
		return true;
	}

}
