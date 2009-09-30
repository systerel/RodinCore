/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added accept for ISimpleVisitor
 *     Systerel - mathematical language v2
 *******************************************************************************/
package org.eventb.core.ast;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;
import org.eventb.internal.core.typecheck.TypeVariable;

/**
 * UnaryExpression is the base class for all unary expressions in an event-B
 * formula.
 * <p>
 * It can accept tags {KCARD, POW, POW1, KUNION, KINTER, KDOM, KRAN, KPRJ1,
 * KPRJ2, KID, KMIN, KMAX, CONVERSE, UNMINUS}.
 * </p>
 * 
 * @author François Terrier
 * @since 1.0
 */
public class UnaryExpression extends Expression {

	
	protected final Expression child;

	// offset in the corresponding tag interval
	protected static final int firstTag = FIRST_UNARY_EXPRESSION;
	protected static final String[] tags = {
		"card",   // KCARD
		"\u2119", // POW
		"\u21191",// POW1
		"union",  // KUNION
		"inter",  // KINTER
		"dom",    // KDOM
		"ran",    // KRAN
		"prj1",   // KPRJ1
		"prj2",   // KPRJ2
		"id",     // KID
		"min",    // KMIN
		"max",    // KMAX
		"\u223c", // CONVERSE
		"\u2212"  // UNMINUS
	};
	// For testing purposes
	public static final int TAGS_LENGTH = tags.length;

	// indicates whether the corresponding operator has to be
	// written before or after the operand
	private static final boolean[] isPrefix = {
		true, // KCARD
		true, // POW
		true, // POW1
		true, // KUNION
		true, // KINTER
		true, // KDOM
		true, // KRAN
		true, // KPRJ1
		true, // KPRJ2
		true, // KID
		true, // KMIN
		true, // KMAX
		false,// CONVERSE
		true  // UNMINUS
	};
	
	// indicates when the operand has to be parenthesized
	// this is used by method toString
	private static final boolean[] alwaysParenthesized = {
		true, // KCARD
		true, // POW
		true, // POW1
		true, // KUNION
		true, // KINTER
		true, // KDOM
		true, // KRAN
		true, // KPRJ1
		true, // KPRJ2
		true, // KID
		true, // KMIN
		true, // KMAX
		false,// CONVERSE
		false // UNMINUS
	};
	                             
	protected UnaryExpression(Expression child, int tag, SourceLocation location,
			FormulaFactory factory) {
		
		super(tag, location, child.hashCode());
		this.child = child;

		assert tag >= firstTag && tag < firstTag+tags.length;
		assert child != null;
		
		synthesizeType(factory, null);
	}

	@Override
	@SuppressWarnings("deprecation")
	protected void synthesizeType(FormulaFactory ff, Type givenType) {
		this.freeIdents = child.freeIdents;
		this.boundIdents = child.boundIdents;

		// Fast exit if children are not typed
		// (the most common case where type synthesis can't be done)
		if (! child.isTypeChecked()) {
			return;
		}
		
		Type childType = child.getType();
		final Type resultType;
		final Type alpha, beta;
		switch (getTag()) {
		case Formula.UNMINUS:
			if (childType instanceof IntegerType) {
				resultType = ff.makeIntegerType();
			} else {
				resultType = null;
			}
			break;
		case Formula.CONVERSE:
			alpha = childType.getSource();
			beta = childType.getTarget();
			if (alpha != null) {
				resultType = ff.makeRelationalType(beta, alpha);
			} else {
				resultType = null;
			}
			break;
		case Formula.KCARD:
			alpha = childType.getBaseType();
			if (alpha != null) {
				resultType = ff.makeIntegerType();
			} else {
				resultType = null;
			}
			break;
		case Formula.POW:
		case Formula.POW1:
			alpha = childType.getBaseType();
			if (alpha != null) {
				resultType = ff.makePowerSetType(childType);
			} else {
				resultType = null;
			}
			break;
		case Formula.KUNION:
		case Formula.KINTER:
			final Type baseType = childType.getBaseType();
			if (baseType != null && baseType.getBaseType() != null) {
				resultType = baseType;
			} else {
				resultType = null;
			}
			break;
		case Formula.KDOM:
			alpha = childType.getSource();
			if (alpha != null) {
				resultType = ff.makePowerSetType(alpha);
			} else {
				resultType = null;
			}
			break;
		case Formula.KRAN:
			beta = childType.getTarget();
			if (beta != null) {
				resultType = ff.makePowerSetType(beta);
			} else {
				resultType = null;
			}
			break;
		case Formula.KPRJ1:
			alpha = childType.getSource();
			beta = childType.getTarget();
			if (alpha != null) {
				resultType = ff.makeRelationalType(
						ff.makeProductType(alpha, beta),
						alpha);
			} else {
				resultType = null;
			}
			break;
		case Formula.KPRJ2:
			alpha = childType.getSource();
			beta = childType.getTarget();
			if (alpha != null) {
				resultType = ff.makeRelationalType(
						ff.makeProductType(alpha, beta),
						beta);
			} else {
				resultType = null;
			}
			break;
		case Formula.KID:
			alpha = childType.getBaseType();
			if (alpha != null) {
				resultType = ff.makeRelationalType(alpha, alpha);
			} else {
				resultType = null;
			}
			break;
		case Formula.KMIN:
		case Formula.KMAX:
			alpha = childType.getBaseType();
			if (alpha instanceof IntegerType) {
				resultType = alpha;
			} else {
				resultType = null;
			}
			break;
		default:
			assert false;
			resultType = null;
		}
		if (resultType == null) {
			return;
		}
		setFinalType(resultType, givenType);
	}

	// for the operands that do not always need to be parenthesized,
	// indicates when parentheses should be output in the toString method
	private static BitSet[] leftParenthesesMap = new BitSet[tags.length];
	private static BitSet[] rightParenthesesMap = new BitSet[tags.length];
	
	
	static {
		assert isPrefix.length == tags.length;
		assert alwaysParenthesized.length == tags.length;
		assert leftParenthesesMap.length == tags.length;
		assert rightParenthesesMap.length == tags.length;

		for (int i=0; i<tags.length; i++) {
			leftParenthesesMap[i] = new BitSet();
			rightParenthesesMap[i] = new BitSet();
		}

		leftParenthesesMap[UNMINUS-firstTag].set(UNMINUS);
		leftParenthesesMap[UNMINUS-firstTag].set(MUL);
		leftParenthesesMap[UNMINUS-firstTag].set(DIV);
		leftParenthesesMap[UNMINUS-firstTag].set(MOD);
		leftParenthesesMap[UNMINUS-firstTag].set(CONVERSE);
		leftParenthesesMap[UNMINUS-firstTag].set(EXPN);
		leftParenthesesMap[UNMINUS-firstTag].set(FUNIMAGE);
		leftParenthesesMap[UNMINUS-firstTag].set(RELIMAGE);
		
		rightParenthesesMap[UNMINUS-firstTag].set(UNMINUS);
		rightParenthesesMap[UNMINUS-firstTag].set(PLUS);
		rightParenthesesMap[UNMINUS-firstTag].set(MINUS);
		rightParenthesesMap[UNMINUS-firstTag].set(MUL);
		rightParenthesesMap[UNMINUS-firstTag].set(DIV);
		rightParenthesesMap[UNMINUS-firstTag].set(MOD);
		rightParenthesesMap[UNMINUS-firstTag].set(CONVERSE);
		rightParenthesesMap[UNMINUS-firstTag].set(EXPN);
	}
	
	protected static boolean needsParentheses(int tag, boolean isRightChild,
			int parentTag) {
		if (isRightChild) {
			return rightParenthesesMap[tag - firstTag].get(parentTag);
		}
		return leftParenthesesMap[tag - firstTag].get(parentTag);
	}

	/**
	 * Returns the unique child of this node.
	 * 
	 * @return child of this node.
	 */
	public Expression getChild() {
		return child;
	}
	
	@Override
	protected void toString(StringBuilder builder, boolean isRightChild,
			int parentTag, String[] boundNames, boolean withTypes) {

		if (isPrefix()) {
			if (isAlwaysParenthesized()) {
				builder.append(getTagOperator());
				builder.append('(');
				child.toString(builder, false, getTag(), boundNames, withTypes);
				builder.append(')');
			} else if (needsParentheses(isRightChild, parentTag)) {
				builder.append('(');
				builder.append(getTagOperator());
				child.toString(builder, false, getTag(), boundNames, withTypes);
				builder.append(')');
			} else {
				builder.append(getTagOperator());
				child.toString(builder, false, getTag(), boundNames, withTypes);
			}
		} else {
			if (isAlwaysParenthesized()) {
				// for now this is never the case
				builder.append('(');
				child.toString(builder, false, getTag(), boundNames, withTypes);
				builder.append(')');
				builder.append(getTagOperator());
			} else if (needsParentheses(isRightChild, parentTag)) {
				builder.append('(');
				child.toString(builder, false, getTag(), boundNames, withTypes);
				builder.append(getTagOperator());
				builder.append(')');
			} else {
				child.toString(builder, false, getTag(), boundNames, withTypes);
				builder.append(getTagOperator());
			}
		}
	}

	private boolean isPrefix() {
		return isPrefix[getTag() - firstTag];
	}

	private boolean needsParentheses(boolean isRightChild, int parentTag) {
		return needsParentheses(getTag(), isRightChild, parentTag);
	}

	protected String getTagOperator() {
		return tags[getTag()-firstTag];
	}
	
	// true if always needs parentheses
	protected boolean isAlwaysParenthesized() {
		return alwaysParenthesized[getTag()-firstTag];
	}

	@Override
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		if (this.getTag() != other.getTag()) {
			return false;
		}
		UnaryExpression otherExpr = (UnaryExpression) other;
		return hasSameType(other)
				&& child.equals(otherExpr.child, withAlphaConversion);
	}

	@Override
	@SuppressWarnings("deprecation")
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		child.typeCheck(result,quantifiedIdentifiers);
		
		final TypeVariable alpha, beta;
		final Type resultType;
		switch (getTag()) {
		case Formula.UNMINUS:
			resultType = result.makeIntegerType();
			result.unify(child.getType(), resultType, this);
			break;
		case Formula.CONVERSE:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			result.unify(child.getType(), result.makeRelationalType(alpha, beta), this);
			resultType = result.makeRelationalType(beta, alpha);
			break;
		case Formula.KCARD:
			alpha = result.newFreshVariable(null);
			result.unify(child.getType(), result.makePowerSetType(alpha), this);
			resultType = result.makeIntegerType();
			break;
		case Formula.POW:
		case Formula.POW1:
			alpha = result.newFreshVariable(null);
			Type childPattern = result.makePowerSetType(alpha);
			result.unify(child.getType(), childPattern, this);
			resultType = result.makePowerSetType(childPattern);
			break;
		case Formula.KUNION:
		case Formula.KINTER:
			alpha = result.newFreshVariable(null);
			resultType = result.makePowerSetType(alpha);
			result.unify(child.getType(), result.makePowerSetType(resultType), this);
			break;
		case Formula.KDOM:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			result.unify(child.getType(), result.makeRelationalType(alpha, beta), this);
			resultType = result.makePowerSetType(alpha);
			break;
		case Formula.KRAN:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			result.unify(child.getType(), result.makeRelationalType(alpha, beta), this);
			resultType = result.makePowerSetType(beta);
			break;
		case Formula.KPRJ1:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			result.unify(child.getType(), result.makeRelationalType(alpha, beta), this);
			resultType = result.makeRelationalType(result.makeProductType(alpha, beta), alpha);
			break;
		case Formula.KPRJ2:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			result.unify(child.getType(), result.makeRelationalType(alpha, beta), this);
			resultType = result.makeRelationalType(result.makeProductType(alpha, beta), beta);
			break;
		case Formula.KID:
			alpha = result.newFreshVariable(null);
			result.unify(child.getType(), result.makePowerSetType(alpha), this);
			resultType = result.makeRelationalType(alpha, alpha);
			break;
		case Formula.KMIN:
		case Formula.KMAX:
			resultType = result.makeIntegerType();
			result.unify(child.getType(), result.makePowerSetType(resultType), this);
			break;
		default:
			assert false;
			return;
		}
		setTemporaryType(resultType, result);
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		return child.solveType(unifier);
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return tabs + this.getClass().getSimpleName() + " [" + getTagOperator()
				+ "]" + getTypeName() + "\n"
				+ child.getSyntaxTree(boundNames, tabs + "\t");
	}

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		child.isLegible(result, quantifiedIdents);
	}

	@Override
	protected void toStringFullyParenthesized(StringBuilder builder,
			String[] boundNames) {

		if (isPrefix()) {
			builder.append(getTagOperator());
			builder.append('(');
			child.toStringFullyParenthesized(builder, boundNames);
			builder.append(')');
		} else {
			builder.append('(');
			child.toStringFullyParenthesized(builder, boundNames);
			builder.append(')');
			builder.append(getTagOperator());
		}
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		child.collectFreeIdentifiers(freeIdentSet);
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		child.collectNamesAbove(names, boundNames, offset);
	}
	
	@Override
	protected Expression bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		Expression newChild = child.bindTheseIdents(binding, offset, factory);
		if (newChild == child) {
			return this;
		}
		return factory.makeUnaryExpression(getTag(), newChild, getSourceLocation());
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case KCARD:    goOn = visitor.enterKCARD(this);    break;
		case POW:      goOn = visitor.enterPOW(this);      break;
		case POW1:     goOn = visitor.enterPOW1(this);     break;
		case KUNION:   goOn = visitor.enterKUNION(this);   break;
		case KINTER:   goOn = visitor.enterKINTER(this);   break;
		case KDOM:     goOn = visitor.enterKDOM(this);     break;
		case KRAN:     goOn = visitor.enterKRAN(this);     break;
		case KPRJ1:    goOn = visitor.enterKPRJ1(this);    break;
		case KPRJ2:    goOn = visitor.enterKPRJ2(this);    break;
		case KID:      goOn = visitor.enterKID(this);      break;
		case KMIN:     goOn = visitor.enterKMIN(this);     break;
		case KMAX:     goOn = visitor.enterKMAX(this);     break;
		case CONVERSE: goOn = visitor.enterCONVERSE(this); break;
		case UNMINUS:  goOn = visitor.enterUNMINUS(this);  break;
		default:       assert false;
		}

		if (goOn) goOn = child.accept(visitor);
		
		switch (getTag()) {
		case KCARD:    return visitor.exitKCARD(this);
		case POW:      return visitor.exitPOW(this);
		case POW1:     return visitor.exitPOW1(this);
		case KUNION:   return visitor.exitKUNION(this);
		case KINTER:   return visitor.exitKINTER(this);
		case KDOM:     return visitor.exitKDOM(this);
		case KRAN:     return visitor.exitKRAN(this);
		case KPRJ1:    return visitor.exitKPRJ1(this);
		case KPRJ2:    return visitor.exitKPRJ2(this);
		case KID:      return visitor.exitKID(this);
		case KMIN:     return visitor.exitKMIN(this);
		case KMAX:     return visitor.exitKMAX(this);
		case CONVERSE: return visitor.exitCONVERSE(this);
		case UNMINUS:  return visitor.exitUNMINUS(this);
		default:       return true;
		}
	}
	
	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitUnaryExpression(this);
	}

	private Predicate getWDPredicateKCARD(FormulaFactory formulaFactory) {
		Predicate conj0 = child.getWDPredicateRaw(formulaFactory);
		Predicate conj1 = 
			formulaFactory.makeSimplePredicate(KFINITE, child, null);
		return getWDSimplifyC(formulaFactory, conj0, conj1);
	}

	private Predicate getWDPredicateKINTER(FormulaFactory formulaFactory) {
		Predicate conj0 = child.getWDPredicateRaw(formulaFactory);
		Expression emptyset = formulaFactory.makeEmptySet(child.getType(), null);
		Predicate conj1 = formulaFactory.makeRelationalPredicate(NOTEQUAL, child, emptyset, null);
		return getWDSimplifyC(formulaFactory, conj0, conj1);
	}

	private Predicate getWDPredicateKMINMAX(FormulaFactory ff) {
		Predicate conj0 = child.getWDPredicateRaw(ff);
		Expression emptyset = ff.makeEmptySet(child.getType(), null);
		Predicate conj1 = ff.makeRelationalPredicate(NOTEQUAL, child, emptyset, null);
		Type INT = new IntegerType();
		Predicate impl = ff.makeBinaryPredicate(LIMP,
				ff.makeRelationalPredicate(IN,
						ff.makeBoundIdentifier(0, null, INT),
						child.shiftBoundIdentifiers(2, ff),
						null),
				ff.makeRelationalPredicate(
						(getTag() == KMIN ? LE : GE),
						ff.makeBoundIdentifier(1, null, INT),
						ff.makeBoundIdentifier(0, null, INT),
						null
				),
				null
		);
		BoundIdentDecl[] b = new BoundIdentDecl[] {
				ff.makeBoundIdentDecl("b", null, INT)
		}; 
		BoundIdentDecl[] x = new BoundIdentDecl[] {
				ff.makeBoundIdentDecl("x", null, INT)
		}; 
		Predicate conj2 = ff.makeQuantifiedPredicate(EXISTS,
				b,
				ff.makeQuantifiedPredicate(FORALL,
						x,
						impl,
						null
				),
				null
		);
		if (conj0.getTag() == BTRUE) {
			return ff.makeAssociativePredicate(LAND, 
					new Predicate[] {conj1, conj2}, null);
		}
		return ff.makeAssociativePredicate(LAND, 
				new Predicate[] {conj0, conj1, conj2}, null);
	}

	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		switch (getTag()) {
		case KCARD:
			return getWDPredicateKCARD(formulaFactory);
		case KMIN:
		case KMAX:
			return getWDPredicateKMINMAX(formulaFactory);
		case KINTER:
			return getWDPredicateKINTER(formulaFactory);
		default:
			return child.getWDPredicateRaw(formulaFactory);
		}
		
	}

	@Override
	public Expression rewrite(IFormulaRewriter rewriter) {
		final Expression newChild = child.rewrite(rewriter);

		final FormulaFactory ff = rewriter.getFactory();
		final SourceLocation sloc = getSourceLocation();
		if (getTag() == Formula.UNMINUS && newChild.getTag() == Formula.INTLIT
				&& rewriter.autoFlatteningMode()) {
			BigInteger value = ((IntegerLiteral) newChild).getValue();
			IntegerLiteral before = ff.makeIntegerLiteral(value.negate(), sloc);
			return checkReplacement(rewriter.rewrite(before));
		}

		final UnaryExpression before;
		if (newChild == child) {
			before = this;
		} else {
			before = ff.makeUnaryExpression(getTag(), newChild, sloc);
		}
		return checkReplacement(rewriter.rewrite(before));
	}

	@Override
	public boolean isATypeExpression() {
		return getTag() == POW && child.isATypeExpression();
	}

	@Override
	public Type toType(FormulaFactory factory) throws InvalidExpressionException {
		if (getTag() != POW)
			throw new InvalidExpressionException();
		Type childAsType = child.toType(factory);
		return factory.makePowerSetType(childAsType);
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		child.addGivenTypes(set);
	}

	@Override
	protected void getPositions(IFormulaFilter filter, IntStack indexes,
			List<IPosition> positions) {
		
		if (filter.select(this)) {
			positions.add(new Position(indexes));
		}

		indexes.push(0);
		child.getPositions(filter, indexes, positions);
		indexes.pop();
	}

	@Override
	protected Formula<?> getChild(int index) {
		if (index == 0) {
			return child;
		}
		return null;
	}

	@Override
	protected IPosition getDescendantPos(SourceLocation sloc, IntStack indexes) {
		indexes.push(0);
		IPosition pos = child.getPosition(sloc, indexes);
		if (pos != null)
			return pos;
		indexes.pop();
		return new Position(indexes);
	}

	@Override
	protected Expression rewriteChild(int index, SingleRewriter rewriter) {
		if (index != 0) 
			throw new IllegalArgumentException("Position is outside the formula");
		Expression newChild = rewriter.rewrite(child);
		return rewriter.factory.makeUnaryExpression(getTag(), newChild,
				getSourceLocation());
	}

}
