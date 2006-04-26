/*
 * Created on 11-may-2005
 *
 */
package org.eventb.core.ast;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Substitution;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;
import org.eventb.internal.core.typecheck.TypeVariable;

/**
 * BinaryExpression is the class for all binary expressions in an event-B
 * formula.
 * <p>
 * It has 2 children and can only accept {MAPSTO, REL, TREL, SREL, STREL, PFUN,
 * TFUN, PINJ, TINJ, PSUR, TSUR, TBIJ, SETMINUS, CPROD, DPROD, PPROD, DOMRES,
 * DOMSUB, RANRES, RANSUB, UPTO, MINUS, DIV, MOD, EXPN, FUNIMAGE, RELIMAGE}
 * </p>
 * 
 * @author François Terrier
 */
public class BinaryExpression extends Expression {

	// Left and right children.
	// Are never null by construction.
	private final Expression left;
	private final Expression right;
	
	// offset of the corresponding tag-interval in Formula
	protected final static int firstTag = FIRST_BINARY_EXPRESSION;
	protected final static String[] tags = {
		"\u21a6",  // MAPSTO
		"\u2194",  // REL
		"\ue100",  // TREL
		"\ue101",  // SREL
		"\ue102",  // STREL
		"\u21f8",  // PFUN
		"\u2192",  // TFUN
		"\u2914",  // PINJ
		"\u21a3",  // TINJ
		"\u2900",  // PSUR
		"\u21a0",  // TSUR
		"\u2916",  // TBIJ
		"\u2216",  // SETMINUS
		"\u00d7",  // CPROD
		"\u2297",  // DPROD
		"\u2225",  // PPROD
		"\u25c1",  // DOMRES
		"\u2a64",  // DOMSUB
		"\u25b7",  // RANRES
		"\u2a65",  // RANSUB
		"\u2025",  // UPTO
		"\u2212",  // MINUS
		"\u00f7",  // DIV
		"mod",     // MOD
		"\u005e",  // EXPN
		"FUNIMAGE",// FUNIMAGE
		"RELIMAGE" // RELIMAGE
	};
	// For testing purposes
	public static final int TAGS_LENGTH = tags.length;

	protected BinaryExpression(Expression left, Expression right, int tag,
			SourceLocation location, FormulaFactory factory) {
		super (tag, location, 
				combineHashCodes(left.hashCode(), right.hashCode()));
		this.left = left;
		this.right = right;
		
		assert tag >= firstTag && tag < firstTag+tags.length;
		assert left != null;
		assert right != null;
		
		synthesizeType(factory);
	}
	
	private void synthesizeType(FormulaFactory ff) {
		IdentListMerger freeIdentMerger = 
			IdentListMerger.makeMerger(left.freeIdents, right.freeIdents);
		this.freeIdents = freeIdentMerger.getFreeMergedArray();

		IdentListMerger boundIdentMerger = 
			IdentListMerger.makeMerger(left.boundIdents, right.boundIdents);
		this.boundIdents = boundIdentMerger.getBoundMergedArray();

		if (freeIdentMerger.containsError() || boundIdentMerger.containsError()) {
			// Incompatible type environments, don't bother going further.
			return;
		}

		Type leftType = left.getType();
		Type rightType = right.getType();
		
		// Fast exit if children are not typed
		// (the most common case where type synthesis can't be done)
		if (leftType == null || rightType == null) {
			return;
		}
		
		final Type resultType;
		Type alpha, beta, gamma, delta;
		switch (getTag()) {
		case Formula.FUNIMAGE:
			alpha = leftType.getSource();
			if (alpha != null && alpha.equals(rightType)) {
				resultType = leftType.getTarget();
			} else {
				resultType = null;
			}
			break;
		case Formula.RELIMAGE:
			alpha = leftType.getSource();
			beta = leftType.getTarget();
			if (alpha != null && alpha.equals(rightType.getBaseType())) {
					resultType = ff.makePowerSetType(beta);
			} else {
				resultType = null;
			}
			break;
		case Formula.MAPSTO:
			resultType = ff.makeProductType(leftType, rightType);
			break;
		case Formula.REL:
		case Formula.TREL:
		case Formula.SREL:
		case Formula.STREL:
		case Formula.PFUN:
		case Formula.TFUN:
		case Formula.PINJ:
		case Formula.TINJ:
		case Formula.PSUR:
		case Formula.TSUR:
		case Formula.TBIJ:
			alpha = leftType.getBaseType();
			beta = rightType.getBaseType();
			if (alpha != null && beta != null) {
				resultType = ff.makePowerSetType(
						ff.makeRelationalType(alpha, beta)
				);
			} else {
				resultType = null;
			}
			break;
		case Formula.SETMINUS:
			alpha = leftType.getBaseType();
			if (alpha != null && leftType.equals(rightType)) {
				resultType = leftType;
			} else {
				resultType = null;
			}
			break;
		case Formula.CPROD:
			alpha = leftType.getBaseType();
			beta = rightType.getBaseType();
			if (alpha != null && beta != null) {
				resultType =ff.makeRelationalType(alpha, beta);
			} else {
				resultType = null;
			}
			break;
		case Formula.DPROD:
			alpha = leftType.getSource();
			beta = leftType.getTarget();
			gamma = rightType.getTarget();
			if (alpha != null && beta != null && gamma != null
					&& alpha.equals(rightType.getSource())) {
				resultType = ff.makeRelationalType(alpha, ff.makeProductType(beta, gamma));
			} else {
				resultType = null;
			}
			break;
		case Formula.PPROD:
			alpha = leftType.getSource();
			beta = rightType.getSource();
			gamma = leftType.getTarget();
			delta = rightType.getTarget();
			if (alpha != null && beta != null && gamma != null && delta != null) {
				resultType = ff.makeRelationalType(
						ff.makeProductType(alpha, beta),
						ff.makeProductType(gamma, delta));
			} else {
				resultType = null;
			}
			break;
		case Formula.DOMRES:
		case Formula.DOMSUB:
			alpha = leftType.getBaseType();
			if (alpha != null && alpha.equals(rightType.getSource())) {
				resultType = rightType;
			} else {
				resultType = null;
			}
			break;
		case Formula.RANRES:
		case Formula.RANSUB:
			beta = rightType.getBaseType();
			if (beta != null && beta.equals(leftType.getTarget())) {
				resultType = leftType;
			} else {
				resultType = null;
			}
			break;
		case Formula.UPTO:
			if (leftType instanceof IntegerType && rightType instanceof IntegerType) {
				resultType = ff.makePowerSetType(leftType);
			} else {
				resultType = null;
			}
			break;
		case Formula.MINUS:
		case Formula.DIV:
		case Formula.MOD:
		case Formula.EXPN:
			if (leftType instanceof IntegerType && rightType instanceof IntegerType) {
				resultType = leftType;
			} else {
				resultType = null;
			}
			break;
		default:
			assert false;
			resultType = null;
		}
		setType(resultType, null);
	}
	
	
	// indicates when the toString method should put parentheses
	private static final BitSet[] leftNoParenthesesMap = new BitSet[tags.length];
	private static final BitSet[] rightNoParenthesesMap = new BitSet[tags.length];
	
	// fills the parentheses map
	static {
		assert tags.length == leftNoParenthesesMap.length;
		assert tags.length == rightNoParenthesesMap.length;
		
		BitSet propagateLeft = new BitSet();
		BitSet propagateRight = new BitSet();
		BitSet commonTempLeft, commonTempRight;
		BitSet temp;
		
		propagateLeft.set(Formula.STARTTAG);

		propagateLeft.set(Formula.CSET);
		propagateLeft.set(Formula.QUNION);
		propagateLeft.set(Formula.QINTER);
		propagateLeft.set(Formula.SETEXT);
		// is not below but reachable without parentheses
		propagateLeft.set(Formula.KBOOL);
		propagateLeft.set(Formula.KCARD);
		propagateLeft.set(Formula.KFINITE);
		propagateLeft.set(Formula.POW);
		propagateLeft.set(Formula.POW1);
		propagateLeft.set(Formula.KUNION);
		propagateLeft.set(Formula.KINTER);
		propagateLeft.set(Formula.KDOM);
		propagateLeft.set(Formula.KRAN);
		propagateLeft.set(Formula.KPRJ1);
		propagateLeft.set(Formula.KPRJ2);
		// is a relop
		propagateLeft.set(Formula.EQUAL);
		propagateLeft.set(Formula.NOTEQUAL);
		propagateLeft.set(Formula.IN);
		propagateLeft.set(Formula.NOTIN);
		propagateLeft.set(Formula.SUBSET);
		propagateLeft.set(Formula.NOTSUBSET);
		propagateLeft.set(Formula.SUBSETEQ);
		propagateLeft.set(Formula.NOTSUBSETEQ);
		propagateLeft.set(Formula.LT);
		propagateLeft.set(Formula.LE);
		propagateLeft.set(Formula.GT);
		propagateLeft.set(Formula.GE);
		propagateRight = (BitSet)propagateLeft.clone();
		// is not below but reachable without parentheses only right child
		propagateRight.set(Formula.FUNIMAGE);
		propagateRight.set(Formula.RELIMAGE);
		
		// pair-expression
		leftNoParenthesesMap[Formula.MAPSTO-firstTag] = (BitSet)propagateLeft.clone();
		rightNoParenthesesMap[Formula.MAPSTO-firstTag] = (BitSet)propagateRight.clone();
		leftNoParenthesesMap[Formula.MAPSTO-firstTag].set(Formula.MAPSTO);
		
		// relation-set-expr
		// REL,TREL,SREL,STREL,PFUN,TFUN,PINJ,TINJ,PSUR,TSUR,TBIJ
		propagateLeft.set(Formula.MAPSTO);
		propagateRight.set(Formula.MAPSTO);
		temp = new BitSet();
		temp.set(Formula.REL);
		temp.set(Formula.TREL);
		temp.set(Formula.SREL);
		temp.set(Formula.STREL);
		temp.set(Formula.PFUN);
		temp.set(Formula.TFUN);
		temp.set(Formula.PINJ);
		temp.set(Formula.TINJ);
		temp.set(Formula.PSUR);
		temp.set(Formula.TSUR);
		temp.set(Formula.TBIJ);
		commonTempRight = (BitSet)propagateRight.clone();
		commonTempLeft = (BitSet)propagateLeft.clone();
		commonTempLeft.or(temp);
		
		for(int i=temp.nextSetBit(0); i>=0; i=temp.nextSetBit(i+1)) {
			leftNoParenthesesMap[i-firstTag] = commonTempLeft;
			rightNoParenthesesMap[i-firstTag] = commonTempRight;
		}
		
		// set-expr
		// BUNION,CPROD,OVR,BCOMP,PPROD
		propagateLeft.or(temp);
		propagateRight.or(temp);
		// PPROD
		leftNoParenthesesMap[Formula.PPROD-firstTag] = (BitSet)propagateLeft.clone();
		rightNoParenthesesMap[Formula.PPROD-firstTag] = (BitSet)propagateRight.clone();
		// CPROD
		leftNoParenthesesMap[Formula.CPROD-firstTag] = (BitSet)propagateLeft.clone();
		leftNoParenthesesMap[Formula.CPROD-firstTag].set(Formula.CPROD);
		rightNoParenthesesMap[Formula.CPROD-firstTag] = (BitSet)propagateRight.clone();
		
		// domain-modifier
		leftNoParenthesesMap[Formula.DOMRES-firstTag] = (BitSet)propagateLeft.clone();
		leftNoParenthesesMap[Formula.DOMRES-firstTag].set(Formula.SETMINUS);
		leftNoParenthesesMap[Formula.DOMRES-firstTag].set(Formula.DPROD);
		leftNoParenthesesMap[Formula.DOMRES-firstTag].set(Formula.FCOMP);
		leftNoParenthesesMap[Formula.DOMRES-firstTag].set(Formula.RANRES);
		leftNoParenthesesMap[Formula.DOMRES-firstTag].set(Formula.RANSUB);
		leftNoParenthesesMap[Formula.DOMRES-firstTag].set(Formula.BINTER);
		rightNoParenthesesMap[Formula.DOMRES-firstTag] = (BitSet)propagateRight.clone();
		
		leftNoParenthesesMap[Formula.DOMSUB-firstTag] = leftNoParenthesesMap[Formula.DOMRES-firstTag];
		rightNoParenthesesMap[Formula.DOMSUB-firstTag] = rightNoParenthesesMap[Formula.DOMRES-firstTag];
		
		// relation-expr
		commonTempRight = (BitSet)propagateRight.clone();
		commonTempRight.set(Formula.DOMRES);
		commonTempRight.set(Formula.DOMSUB);
		commonTempLeft = (BitSet)propagateLeft.clone();
		// SETMINUS, CPROD, RANRES, RANSUB
		leftNoParenthesesMap[Formula.DPROD-firstTag] = commonTempLeft;
		rightNoParenthesesMap[Formula.DPROD-firstTag] = commonTempRight;
		leftNoParenthesesMap[Formula.SETMINUS-firstTag] = commonTempLeft;
		rightNoParenthesesMap[Formula.SETMINUS-firstTag] = commonTempRight;
		rightNoParenthesesMap[Formula.SETMINUS-firstTag].clear(Formula.DOMRES);
		rightNoParenthesesMap[Formula.SETMINUS-firstTag].clear(Formula.DOMSUB);
		leftNoParenthesesMap[Formula.RANRES-firstTag] = commonTempLeft;
		rightNoParenthesesMap[Formula.RANRES-firstTag] = commonTempRight;
		leftNoParenthesesMap[Formula.RANSUB-firstTag] = commonTempLeft;
		rightNoParenthesesMap[Formula.RANSUB-firstTag] = commonTempRight;

		// interval-expr
		temp = new BitSet();
		temp.set(Formula.BUNION);
		temp.set(Formula.BCOMP);
		temp.set(Formula.OVR);
		temp.set(Formula.CPROD);
		temp.set(Formula.PPROD);
		temp.set(Formula.SETMINUS);
		temp.set(Formula.CPROD);
		temp.set(Formula.FCOMP);
		temp.set(Formula.BINTER);
		temp.set(Formula.DOMRES);
		temp.set(Formula.DOMSUB);
		temp.set(Formula.RANRES);
		temp.set(Formula.RANSUB);
		propagateLeft.or(temp);
		propagateRight.or(temp);
		leftNoParenthesesMap[Formula.UPTO-firstTag] = (BitSet)propagateLeft.clone();
		rightNoParenthesesMap[Formula.UPTO-firstTag] = (BitSet)propagateRight.clone();
		
		// arithmetic-expr
		leftNoParenthesesMap[Formula.MINUS-firstTag] = (BitSet)propagateLeft.clone();
		leftNoParenthesesMap[Formula.MINUS-firstTag].set(Formula.MINUS);
		leftNoParenthesesMap[Formula.MINUS-firstTag].set(Formula.PLUS);
		rightNoParenthesesMap[Formula.MINUS-firstTag] = (BitSet)propagateRight.clone();
		
		// term
		propagateLeft.set(Formula.PLUS);
		propagateRight.set(Formula.PLUS);
		propagateLeft.set(Formula.MINUS);
		propagateRight.set(Formula.MINUS);
		propagateLeft.set(Formula.UNMINUS);
		propagateRight.set(Formula.UNMINUS);
		temp = new BitSet();
		temp.set(Formula.DIV);
		temp.set(Formula.MOD);
		commonTempRight = (BitSet)propagateRight.clone();
		for(int i=temp.nextSetBit(0); i>=0; i=temp.nextSetBit(i+1)) {
			leftNoParenthesesMap[i-firstTag] = (BitSet)propagateLeft.clone();
			rightNoParenthesesMap[i-firstTag] = commonTempRight;
		}

		// factor
		propagateLeft.or(temp);
		propagateRight.or(temp);
		leftNoParenthesesMap[Formula.EXPN-firstTag] = (BitSet)propagateLeft.clone();
		rightNoParenthesesMap[Formula.EXPN-firstTag] = (BitSet)propagateRight.clone();
		
		// image
		propagateLeft.set(Formula.EXPN);
		propagateRight.set(Formula.EXPN);
		leftNoParenthesesMap[Formula.RELIMAGE-firstTag] = (BitSet)propagateLeft.clone();
		rightNoParenthesesMap[Formula.RELIMAGE-firstTag] = (BitSet)propagateRight.clone();
		leftNoParenthesesMap[Formula.FUNIMAGE-firstTag] = (BitSet)propagateLeft.clone();
		rightNoParenthesesMap[Formula.FUNIMAGE-firstTag] = (BitSet)propagateRight.clone();
		
	}
	
	@Override
	protected String toString(boolean isRightChild, int parentTag,
			String[] boundNames, boolean withTypes) {

		String str;
		switch (getTag()) {
			case FUNIMAGE:
				str = left.toString(false,getTag(),boundNames,withTypes)+"("+
		           right.toString(true,getTag(),boundNames,withTypes)+")";
				break;
			case RELIMAGE:
				str = left.toString(false,getTag(),boundNames,withTypes)+"["+
		           right.toString(true,getTag(),boundNames,withTypes)+"]";
				break;
			default:
				str = left.toString(false,getTag(),boundNames,withTypes)+" "+
					getTagOperator()+" "+
					right.toString(true,getTag(),boundNames,withTypes);
		}
		if (needsNoParenthesis(isRightChild, parentTag)) {
			return str;
		}
		return "("+str+")";
	}

	// Tag operator
	protected String getTagOperator() {
		return tags[getTag()-firstTag];
	}

	private boolean needsNoParenthesis(boolean isRightChild, int parentTag) {
		final int relativeTag = getTag() - firstTag;
		if (isRightChild) {
			return rightNoParenthesesMap[relativeTag].get(parentTag);
		}
		return leftNoParenthesesMap[relativeTag].get(parentTag);
	}
	
	@Override
	protected boolean equals(Formula other, boolean withAlphaConversion) {
		BinaryExpression otherExpr = (BinaryExpression) other;
		return hasSameType(other)
				&& left.equals(otherExpr.left, withAlphaConversion)
				&& right.equals(otherExpr.right, withAlphaConversion);
	}

	@Override
	public Expression flatten(FormulaFactory factory) {
		final Expression newLeft = left.flatten(factory);
		final Expression newRight = right.flatten(factory);
		if (newLeft == left && newRight == right) {
			return this;
		}
		return factory.makeBinaryExpression(getTag(), newLeft, newRight, getSourceLocation());
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		final SourceLocation loc = getSourceLocation();
		TypeVariable alpha, beta, gamma, delta;
		Type resultType;
		
		left.typeCheck(result, quantifiedIdentifiers);
		right.typeCheck(result, quantifiedIdentifiers);
		
		switch (getTag()) {
		case Formula.FUNIMAGE:
			beta = result.newFreshVariable(null);
			result.unify(left.getType(), result.makeRelationalType(right.getType(), beta), loc);
			resultType = beta;
			break;
		case Formula.RELIMAGE:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			result.unify(left.getType(), result.makeRelationalType(alpha, beta), loc);
			result.unify(right.getType(), result.makePowerSetType(alpha),loc);
			resultType = result.makePowerSetType(beta);
			break;
		case Formula.MAPSTO:
			resultType = result.makeProductType(left.getType(), right.getType());
			break;
		case Formula.REL:
		case Formula.TREL:
		case Formula.SREL:
		case Formula.STREL:
		case Formula.PFUN:
		case Formula.TFUN:
		case Formula.PINJ:
		case Formula.TINJ:
		case Formula.PSUR:
		case Formula.TSUR:
		case Formula.TBIJ:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			result.unify(left.getType(), result.makePowerSetType(alpha),loc);
			result.unify(right.getType(), result.makePowerSetType(beta),loc);
			resultType = result.makePowerSetType(result.makeRelationalType(alpha, beta));
			break;
		case Formula.SETMINUS:
			alpha = result.newFreshVariable(null);
			resultType = result.makePowerSetType(alpha);
			result.unify(left.getType(), resultType, loc);
			result.unify(right.getType(), resultType, loc);
			break;
		case Formula.CPROD:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			result.unify(left.getType(), result.makePowerSetType(alpha),loc);
			result.unify(right.getType(), result.makePowerSetType(beta),loc);
			resultType = result.makeRelationalType(alpha, beta);
			break;
		case Formula.DPROD:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			gamma = result.newFreshVariable(null);
			result.unify(left.getType(),result.makeRelationalType(alpha, beta), loc);
			result.unify(right.getType(),result. makeRelationalType(alpha, gamma), loc);
			resultType = result.makeRelationalType(alpha, result.makeProductType(beta, gamma));
			break;
		case Formula.PPROD:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			gamma = result.newFreshVariable(null);
			delta = result.newFreshVariable(null);
			result.unify(left.getType(), result.makeRelationalType(alpha, gamma), loc);
			result.unify(right.getType(), result.makeRelationalType(beta, delta), loc);
			resultType = result.makeRelationalType(
					result.makeProductType(alpha, beta),
					result.makeProductType(gamma, delta));
			break;
		case Formula.DOMRES:
		case Formula.DOMSUB:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			resultType = result.makeRelationalType(alpha, beta);
			result.unify(left.getType(), result.makePowerSetType(alpha), loc);
			result.unify(right.getType(), resultType,loc);
			break;
		case Formula.RANRES:
		case Formula.RANSUB:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			resultType = result.makeRelationalType(alpha, beta);
			result.unify(left.getType(), resultType,loc);
			result.unify(right.getType(), result.makePowerSetType(beta), loc);
			break;
		case Formula.UPTO:
			resultType = result.makeIntegerType();
			result.unify(left.getType(), resultType, loc);
			result.unify(right.getType(), resultType, loc);
			resultType = result.makePowerSetType(resultType);
			break;
		case Formula.MINUS:
		case Formula.DIV:
		case Formula.MOD:
		case Formula.EXPN:
			resultType = result.makeIntegerType();
			result.unify(left.getType(), resultType, loc);
			result.unify(right.getType(), resultType, loc);
			break;
		default:
			assert false;
			resultType = null;
		}
		setType(resultType, result);
	}
	
	@Override
	protected boolean solveType(TypeUnifier unifier) {
		boolean success = left.solveType(unifier) & right.solveType(unifier);
		return finalizeType(success, unifier);
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return tabs + this.getClass().getSimpleName() + " [" + getTagOperator() + "]" 
				+ getTypeName() + "\n"
				+ left.getSyntaxTree(boundNames, tabs + "\t")
				+ right.getSyntaxTree(boundNames, tabs + "\t");
	}

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		left.isLegible(result, quantifiedIdents);
		if (result.isSuccess()) {
			right.isLegible(result, quantifiedIdents);
		}
	}

	@Override
	protected String toStringFullyParenthesized(String[] boundNames) {
		switch (getTag()) {
		case (Formula.FUNIMAGE):
			return "("+left.toStringFullyParenthesized(boundNames)+")"+"("+
	           right.toStringFullyParenthesized(boundNames)+")";
		case (Formula.RELIMAGE):
			return "("+left.toStringFullyParenthesized(boundNames)+")"+"["+
	           right.toStringFullyParenthesized(boundNames)+"]";
		default:
			return "("+left.toStringFullyParenthesized(boundNames)+")"+getTagOperator()+"("+right.toStringFullyParenthesized(boundNames)+")";
	}
}

	/**
	 * Returns the expression on the left-hand side of this node.
	 * 
	 * @return the expression on the left-hand side
	 */
	public Expression getLeft() {
		return left;
	}

	/**
	 * Returns the expression on the right-hand side of this node.
	 * 
	 * @return the expression on the right-hand side
	 */
	public Expression getRight() {
		return right;
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		left.collectFreeIdentifiers(freeIdentSet);
		right.collectFreeIdentifiers(freeIdentSet);
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		left.collectNamesAbove(names, boundNames, offset);
		right.collectNamesAbove(names, boundNames, offset);
	}
	
	@Override
	protected Expression bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		Expression newLeft = left.bindTheseIdents(binding, offset, factory);
		Expression newRight = right.bindTheseIdents(binding, offset, factory);
		if (newLeft == left && newRight == right) {
			return this;
		}
		return factory.makeBinaryExpression(getTag(), newLeft, newRight, getSourceLocation());
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case MAPSTO:   goOn = visitor.enterMAPSTO(this);   break;
		case REL:      goOn = visitor.enterREL(this);      break;
		case TREL:     goOn = visitor.enterTREL(this);     break;
		case SREL:     goOn = visitor.enterSREL(this);     break;
		case STREL:    goOn = visitor.enterSTREL(this);    break;
		case PFUN:     goOn = visitor.enterPFUN(this);     break;
		case TFUN:     goOn = visitor.enterTFUN(this);     break;
		case PINJ:     goOn = visitor.enterPINJ(this);     break;
		case TINJ:     goOn = visitor.enterTINJ(this);     break;
		case PSUR:     goOn = visitor.enterPSUR(this);     break;
		case TSUR:     goOn = visitor.enterTSUR(this);     break;
		case TBIJ:     goOn = visitor.enterTBIJ(this);     break;
		case SETMINUS: goOn = visitor.enterSETMINUS(this); break;
		case CPROD:    goOn = visitor.enterCPROD(this);    break;
		case DPROD:    goOn = visitor.enterDPROD(this);    break;
		case PPROD:    goOn = visitor.enterPPROD(this);    break;
		case DOMRES:   goOn = visitor.enterDOMRES(this);   break;
		case DOMSUB:   goOn = visitor.enterDOMSUB(this);   break;
		case RANRES:   goOn = visitor.enterRANRES(this);   break;
		case RANSUB:   goOn = visitor.enterRANSUB(this);   break;
		case UPTO:     goOn = visitor.enterUPTO(this);     break;
		case MINUS:    goOn = visitor.enterMINUS(this);    break;
		case DIV:      goOn = visitor.enterDIV(this);      break;
		case MOD:      goOn = visitor.enterMOD(this);      break;
		case EXPN:     goOn = visitor.enterEXPN(this);     break;
		case FUNIMAGE: goOn = visitor.enterFUNIMAGE(this); break;
		case RELIMAGE: goOn = visitor.enterRELIMAGE(this); break;
		default:       assert false;
		}

		if (goOn) goOn = left.accept(visitor);
		
		if (goOn) {
			switch (getTag()) {
			case MAPSTO:   goOn = visitor.continueMAPSTO(this);   break;
			case REL:      goOn = visitor.continueREL(this);      break;
			case TREL:     goOn = visitor.continueTREL(this);     break;
			case SREL:     goOn = visitor.continueSREL(this);     break;
			case STREL:    goOn = visitor.continueSTREL(this);    break;
			case PFUN:     goOn = visitor.continuePFUN(this);     break;
			case TFUN:     goOn = visitor.continueTFUN(this);     break;
			case PINJ:     goOn = visitor.continuePINJ(this);     break;
			case TINJ:     goOn = visitor.continueTINJ(this);     break;
			case PSUR:     goOn = visitor.continuePSUR(this);     break;
			case TSUR:     goOn = visitor.continueTSUR(this);     break;
			case TBIJ:     goOn = visitor.continueTBIJ(this);     break;
			case SETMINUS: goOn = visitor.continueSETMINUS(this); break;
			case CPROD:    goOn = visitor.continueCPROD(this);    break;
			case DPROD:    goOn = visitor.continueDPROD(this);    break;
			case PPROD:    goOn = visitor.continuePPROD(this);    break;
			case DOMRES:   goOn = visitor.continueDOMRES(this);   break;
			case DOMSUB:   goOn = visitor.continueDOMSUB(this);   break;
			case RANRES:   goOn = visitor.continueRANRES(this);   break;
			case RANSUB:   goOn = visitor.continueRANSUB(this);   break;
			case UPTO:     goOn = visitor.continueUPTO(this);     break;
			case MINUS:    goOn = visitor.continueMINUS(this);    break;
			case DIV:      goOn = visitor.continueDIV(this);      break;
			case MOD:      goOn = visitor.continueMOD(this);      break;
			case EXPN:     goOn = visitor.continueEXPN(this);     break;
			case FUNIMAGE: goOn = visitor.continueFUNIMAGE(this); break;
			case RELIMAGE: goOn = visitor.continueRELIMAGE(this); break;
			default:       assert false;
			}
		}
		
		if (goOn) goOn = right.accept(visitor);
		
		switch (getTag()) {
		case MAPSTO:   return visitor.exitMAPSTO(this);
		case REL:      return visitor.exitREL(this);
		case TREL:     return visitor.exitTREL(this);
		case SREL:     return visitor.exitSREL(this);
		case STREL:    return visitor.exitSTREL(this);
		case PFUN:     return visitor.exitPFUN(this);
		case TFUN:     return visitor.exitTFUN(this);
		case PINJ:     return visitor.exitPINJ(this);
		case TINJ:     return visitor.exitTINJ(this);
		case PSUR:     return visitor.exitPSUR(this);
		case TSUR:     return visitor.exitTSUR(this);
		case TBIJ:     return visitor.exitTBIJ(this);
		case SETMINUS: return visitor.exitSETMINUS(this);
		case CPROD:    return visitor.exitCPROD(this);
		case DPROD:    return visitor.exitDPROD(this);
		case PPROD:    return visitor.exitPPROD(this);
		case DOMRES:   return visitor.exitDOMRES(this);
		case DOMSUB:   return visitor.exitDOMSUB(this);
		case RANRES:   return visitor.exitRANRES(this);
		case RANSUB:   return visitor.exitRANSUB(this);
		case UPTO:     return visitor.exitUPTO(this);
		case MINUS:    return visitor.exitMINUS(this);
		case DIV:      return visitor.exitDIV(this);
		case MOD:      return visitor.exitMOD(this);
		case EXPN:     return visitor.exitEXPN(this);
		case FUNIMAGE: return visitor.exitFUNIMAGE(this);
		case RELIMAGE: return visitor.exitRELIMAGE(this);
		default:       return true;
		}
	}
	
	private Predicate getWDPredicateDIV(FormulaFactory formulaFactory) {
		Predicate leftConjunct = left.getWDPredicateRaw(formulaFactory);
		Predicate rightConjunct = right.getWDPredicateRaw(formulaFactory);
		Expression zero =  formulaFactory.makeIntegerLiteral(BigInteger.ZERO, null);
		Predicate extraConjunct = formulaFactory.makeRelationalPredicate(NOTEQUAL, right, zero, null);
		return 
		getWDSimplifyC(formulaFactory,
				getWDSimplifyC(formulaFactory, leftConjunct, rightConjunct),
				extraConjunct);
	}

	private Predicate getWDPredicateEXPN(FormulaFactory ff) {
		Predicate leftConjunct = left.getWDPredicateRaw(ff);
		Predicate rightConjunct = right.getWDPredicateRaw(ff);
		Expression zero = ff.makeIntegerLiteral(BigInteger.ZERO, null);
		Predicate leftNotZero = ff.makeRelationalPredicate(LE, zero, left, null);
		Predicate rightNotZero = ff.makeRelationalPredicate(LE, zero, right, null);
		return
		getWDSimplifyC(ff,
				getWDSimplifyC(ff,
						getWDSimplifyC(ff, leftConjunct, rightConjunct),
						leftNotZero),
						rightNotZero);
	}

	private Predicate getWDPredicateFUNIMAGE(FormulaFactory ff) {
		Predicate leftConjunct = left.getWDPredicateRaw(ff);
		Predicate rightConjunct = right.getWDPredicateRaw(ff);
		Expression leftDom = ff.makeUnaryExpression(KDOM, left, null);
		Predicate leftInRightDom = ff.makeRelationalPredicate(IN, right, leftDom, null);
		Expression singleton = ff.makeSetExtension(right, null);
		Expression comp0 = ff.makeUnaryExpression(CONVERSE, left, null);
		Expression comp1 = ff.makeBinaryExpression(DOMRES, singleton, left, null);
		Expression comp = ff.makeAssociativeExpression(FCOMP, new Expression[]{comp0, comp1}, null);
		Expression ran = ff.makeUnaryExpression(KRAN, left, null);
		Expression id = ff.makeUnaryExpression(KID, ran, null);
		Predicate rightFct = ff.makeRelationalPredicate(SUBSETEQ, comp, id, null);
		return 
		getWDSimplifyC(ff,
				getWDSimplifyC(ff,
						getWDSimplifyC(ff, leftConjunct, rightConjunct),
						leftInRightDom),
						rightFct);
	}

	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		switch (getTag()) {
		case DIV:      
		case MOD:      return getWDPredicateDIV(formulaFactory);
		case EXPN:     return getWDPredicateEXPN(formulaFactory);
		case FUNIMAGE: return getWDPredicateFUNIMAGE(formulaFactory);
		default:
			Predicate leftConjunct = left.getWDPredicateRaw(formulaFactory);
			Predicate rightConjunct = right.getWDPredicateRaw(formulaFactory);
			return getWDSimplifyC(formulaFactory, leftConjunct, rightConjunct);
		}
	}

	@Override
	protected boolean isWellFormed(int noOfBoundVars) {
		return left.isWellFormed(noOfBoundVars) && right.isWellFormed(noOfBoundVars);
	}

	@Override
	public BinaryExpression applySubstitution(Substitution subst) {
		final FormulaFactory ff = subst.getFactory();
		Expression newLeft = left.applySubstitution(subst);
		Expression newRight = right.applySubstitution(subst);
		if(newLeft == left && newRight == right)
			return this;
		return ff.makeBinaryExpression(getTag(), newLeft, newRight, getSourceLocation());
	}

	@Override
	public boolean isATypeExpression() {
		int tag = getTag();
		return (tag == CPROD || tag == REL)
				&& left.isATypeExpression()
				&& right.isATypeExpression();
	}

	@Override
	public Type toType(FormulaFactory factory) throws InvalidExpressionException {
		Type leftAsType = left.toType(factory);
		Type rightAsType = right.toType(factory);
		Type result = factory.makeProductType(leftAsType, rightAsType);
		switch (getTag()) {
		case CPROD:
			return result;
		case REL:
			return factory.makePowerSetType(result);
		default:
			throw new InvalidExpressionException();
		}
	}
	
}
