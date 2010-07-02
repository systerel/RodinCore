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
 *     Systerel - mathematical language v2
 *     Systerel - added support for predicate variables
 *******************************************************************************/ 
package org.eventb.core.ast;

import static org.eventb.internal.core.parser.BMath.ATOMIC_EXPR;
import static org.eventb.internal.core.parser.BMath.EMPTY_SET;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.parser.SubParsers.AtomicExpressionParser;
import org.eventb.internal.core.parser.SubParsers.GenExpressionParser;
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
	protected static final int firstTag = FIRST_ATOMIC_EXPRESSION;
	protected static final String tags[] = {
		"\u2124",  // INTEGER
		"\u2115",  // NATURAL
		"\u21151", // NATURAL1
		"BOOL",    // BOOL
		"TRUE",    // TRUE
		"FALSE",   // FALSE
		"\u2205",  // EMPTYSET
		"pred",    // KPRED
		"succ",     // KSUCC
		"prj1",		// KPRJ1_GEN
		"prj2",		// KPRJ2_GEN
		"id",		// KID_GEN
	};
	// For testing purposes
	public static final int TAGS_LENGTH = tags.length;
	private static final String EMPTYSET_ID = "Empty Set";
	private static final String INTEGER_ID = "Integer";
	private static final String TRUE_ID = "True";
	private static final String KID_GEN_ID = "Identity";
	private static final String KPRJ1_GEN_ID = "Projection 1";
	private static final String KPRJ2_GEN_ID = "Projection 2";
	private static final String NATURAL_ID = "Natural";
	private static final String NATURAL1_ID = "Natural1";
	private static final String BOOL_ID = "Bool Type";
	private static final String FALSE_ID = "False";
	private static final String KPRED_ID = "Predecessor";
	private static final String KSUCC_ID = "Successor";

	// TODO separate initV1 and initV2, do not use GenExpressionParser anymore
	/**
	 * @since 2.0
	 */
	@SuppressWarnings("deprecation")
	public static void init(BMath grammar) {
		try {
			grammar.addOperator("\u2124", INTEGER_ID, ATOMIC_EXPR, new AtomicExpressionParser(INTEGER));
			grammar.addOperator("\u2115", NATURAL_ID, ATOMIC_EXPR, new AtomicExpressionParser(NATURAL));
			grammar.addOperator("\u21151", NATURAL1_ID, ATOMIC_EXPR, new AtomicExpressionParser(NATURAL1));
			grammar.addOperator("BOOL", BOOL_ID, ATOMIC_EXPR, new AtomicExpressionParser(Formula.BOOL));
			grammar.addOperator("TRUE", TRUE_ID, ATOMIC_EXPR, new AtomicExpressionParser(TRUE));
			grammar.addOperator("FALSE", FALSE_ID, ATOMIC_EXPR, new AtomicExpressionParser(FALSE));
			grammar.addOperator("\u2205", EMPTYSET_ID, EMPTY_SET, new AtomicExpressionParser(EMPTYSET));
			grammar.addOperator("pred", KPRED_ID, ATOMIC_EXPR, new AtomicExpressionParser(KPRED));
			grammar.addOperator("succ", KSUCC_ID, ATOMIC_EXPR, new AtomicExpressionParser(KSUCC));
			grammar.addOperator("prj1", KPRJ1_GEN_ID, ATOMIC_EXPR, new GenExpressionParser(KPRJ1, KPRJ1_GEN));
			grammar.addOperator("prj2", KPRJ2_GEN_ID, ATOMIC_EXPR, new GenExpressionParser(KPRJ2, KPRJ2_GEN));
			grammar.addOperator("id", KID_GEN_ID, ATOMIC_EXPR, new GenExpressionParser(KID, KID_GEN));
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected AtomicExpression(int tag, SourceLocation location, Type type,
			FormulaFactory factory) {
		super(tag, location, 0);
		assert tag >= firstTag && tag < firstTag+tags.length;

		setPredicateVariableCache();
		synthesizeType(factory, type);
	}

	@Override
	protected void synthesizeType(FormulaFactory ff, Type givenType) {
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

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		return;
	}

	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return formulaFactory.makeLiteralPredicate(BTRUE, null);
	}
	
	@Override
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		return this.getTag() == other.getTag() && hasSameType(other);
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
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		return true;
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String typeName = getType()!=null?" [type: "+getType().toString()+"]":"";
		return tabs + this.getClass().getSimpleName() + " ["
				+ tags[getTag() - firstTag] + "]"  + typeName + "\n";
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
	protected Expression bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		return this;
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
	public Expression rewrite(IFormulaRewriter rewriter) {
		return checkReplacement(rewriter.rewrite(this));
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
	protected void getPositions(IFormulaFilter filter, IntStack indexes,
			List<IPosition> positions) {
		
		if (filter.select(this)) {
			positions.add(new Position(indexes));
		}
	}

	@Override
	protected Formula<?> getChild(int index) {
		return null;
	}

	@Override
	protected IPosition getDescendantPos(SourceLocation sloc, IntStack indexes) {
		return new Position(indexes);
	}

	@Override
	protected Expression rewriteChild(int index, SingleRewriter rewriter) {
		throw new IllegalArgumentException("Position is outside the formula");
	}

}
