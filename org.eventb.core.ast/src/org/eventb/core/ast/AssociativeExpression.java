/*
 * Created on 11-may-2005
 *
 */
package org.eventb.core.ast;

import static org.eventb.core.ast.AssociativeHelper.equalsHelper;
import static org.eventb.core.ast.AssociativeHelper.getSubstitutedList;
import static org.eventb.core.ast.AssociativeHelper.getSyntaxTreeHelper;
import static org.eventb.core.ast.AssociativeHelper.toStringFullyParenthesizedHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Replacement;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;
import org.eventb.internal.core.typecheck.TypeVariable;

/**
 * AssociativeExpression is the AST type for associative expressions in an
 * event-B formula.
 * <p>
 * It can have several children which can only be Expression objects. Can only
 * accept {BUNION, BINTER, BCOMP, FCOMP, OVR, PLUS, MUL}.
 * </p>
 * 
 * @author François Terrier
 */
public class AssociativeExpression extends Expression {

	// offset of the corresponding tag-interval in Formula
	protected static final int firstTag = Formula.FIRST_ASSOCIATIVE_EXPRESSION;
	protected static final String[] tags = {
		"\u222a", // BUNION
		"\u2229", // BINTER
		"\u2218", // BCOMP
		";",      // FCOMP
		"\ue103", // OVR
		"+",      // PLUS
		"\u2217"  // MUL
	};
	// For testing purposes
	public static final int TAGS_LENGTH = tags.length;
	
	// The children of this associative expression.
	// Is never null and contains at least two elements by construction.
	private final Expression[] children;
	
	protected AssociativeExpression(Expression[] children, int tag,
			SourceLocation location) {
		super(tag, location, combineHashCodes(children));
		this.children = new Expression[children.length];
		System.arraycopy(children, 0, this.children, 0, children.length);
		checkPreconditions();
	}

	protected AssociativeExpression(List<? extends Expression> children, int tag, SourceLocation location) {
		super(tag, location, combineHashCodes(children));
		Expression[] model = new Expression[children.size()];
		this.children = children.toArray(model);
		checkPreconditions();
	}

	private void checkPreconditions() {
		assert getTag() >= firstTag && getTag() < firstTag+tags.length;
		assert children != null;
		assert children.length >= 2;
	}

	// indicates when the toString method should put parentheses
	private final static BitSet[] leftNoParenthesesMap = new BitSet[tags.length];
	private final static BitSet[] rightNoParenthesesMap = new BitSet[tags.length];

	// fills the parentheses maps 
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
		propagateLeft.set(Formula.REL);
		// is below
		propagateLeft.set(Formula.TREL);
		propagateLeft.set(Formula.SREL);
		propagateLeft.set(Formula.STREL);
		propagateLeft.set(Formula.PFUN);
		propagateLeft.set(Formula.TFUN);
		propagateLeft.set(Formula.PINJ);
		propagateLeft.set(Formula.TINJ);
		propagateLeft.set(Formula.PSUR);
		propagateLeft.set(Formula.TSUR);
		propagateLeft.set(Formula.TBIJ);
		propagateLeft.set(Formula.MAPSTO);
		
		propagateRight = (BitSet)propagateLeft.clone();
		// is not below but reachable without parentheses only right child
		propagateRight.set(Formula.FUNIMAGE);
		propagateRight.set(Formula.RELIMAGE);
		
		// associative set-expr
		// BUNION BCOMP OVR
		temp = new BitSet();
		temp.set(Formula.BUNION);
		temp.set(Formula.BCOMP);
		temp.set(Formula.OVR);
		for(int i=temp.nextSetBit(0); i>=0; i=temp.nextSetBit(i+1)) {
			leftNoParenthesesMap[i-firstTag] = (BitSet)propagateLeft.clone();
			rightNoParenthesesMap[i-firstTag] = (BitSet)propagateRight.clone();
		}
		
		// relation-expr
		// FCOMP
		commonTempRight = (BitSet)propagateRight.clone();
		rightNoParenthesesMap[Formula.FCOMP-firstTag] = commonTempRight;
		rightNoParenthesesMap[Formula.BINTER-firstTag] = commonTempRight;
		commonTempLeft = (BitSet)propagateLeft.clone();
		commonTempLeft.set(Formula.RANRES);
		commonTempLeft.set(Formula.RANSUB);
		leftNoParenthesesMap[Formula.FCOMP-firstTag] = commonTempLeft;
		leftNoParenthesesMap[Formula.BINTER-firstTag] = (BitSet)commonTempLeft.clone();
		leftNoParenthesesMap[Formula.BINTER-firstTag].set(Formula.SETMINUS);
		
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
		temp.set(Formula.UPTO);
		propagateLeft.or(temp);
		propagateRight.or(temp);
		
		// arithmetic-expr
		leftNoParenthesesMap[Formula.PLUS-firstTag] = (BitSet)propagateLeft.clone();
		leftNoParenthesesMap[Formula.PLUS-firstTag].set(Formula.MINUS);
		rightNoParenthesesMap[Formula.PLUS-firstTag] = (BitSet)propagateRight.clone();
		
		// term
		propagateLeft.set(Formula.PLUS);
		propagateRight.set(Formula.PLUS);
		propagateLeft.set(Formula.MINUS);
		propagateRight.set(Formula.MINUS);
		propagateLeft.set(Formula.UNMINUS);
		propagateRight.set(Formula.UNMINUS);
		leftNoParenthesesMap[Formula.MUL-firstTag] = (BitSet)propagateLeft.clone();
		rightNoParenthesesMap[Formula.MUL-firstTag] = (BitSet)propagateRight.clone();
	}
	
	/**
	 * Returns the children of this node.
	 * 
	 * @return the children of this node. Can never be <code>null</code> or
	 *         empty.
	 */
	public Expression[] getChildren() {
		Expression[] temp = new Expression[children.length];
		System.arraycopy(children, 0, temp, 0, children.length);
		return temp;
	}

	@Override
	protected String toString(boolean isRightChild, int parentTag, String[] boundNames) {
		StringBuffer str = new StringBuffer();
		str.append(children[0].toString(false,getTag(),boundNames));
		for (int i=1; i<children.length;i++) {
			str.append(getTagOperator()+children[i].toString(true,getTag(),boundNames));
		}
		if ((isRightChild && rightNoParenthesesMap[getTag()-firstTag].get(parentTag)) ||
			(!isRightChild && leftNoParenthesesMap[getTag()-firstTag].get(parentTag))) {
			return str.toString();
		}
		return "("+str.toString()+")";
	}
	
	protected String getTagOperator() {
		return tags[getTag()-firstTag];
	}

	
	@Override
	protected boolean equals(Formula other, boolean withAlphaConversion) {
		return hasSameType(other)
				&& equalsHelper(children,
						((AssociativeExpression) other).children,
						withAlphaConversion);
	}

	@Override
	public Expression flatten(FormulaFactory factory) {
		List<Expression> newChildren = new ArrayList<Expression>();
		for (int i = 0; i < children.length; i++) {
			Expression normChild = children[i].flatten(factory);
			if (normChild.getTag() == getTag()) {
				AssociativeExpression assocExprChild = (AssociativeExpression) normChild;
				newChildren.addAll(Arrays.asList(assocExprChild.getChildren()));
			}
			else {
				newChildren.add(normChild);
			}
		}
		return factory.makeAssociativeExpression(getTag(), newChildren, getSourceLocation());
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		final SourceLocation loc = getSourceLocation();
		Type resultType;
		
		switch (getTag()) {
		case Formula.BUNION:
		case Formula.BINTER:
			TypeVariable alpha = result.newFreshVariable(null);
			resultType = result.makePowerSetType(alpha);
			for (int i = 0; i < children.length; i++) {
				children[i].typeCheck(result,quantifiedIdentifiers);
				result.unify(children[i].getType(), resultType, getSourceLocation());
			}
			break;
		case Formula.BCOMP:
			TypeVariable[] tv = new TypeVariable[children.length+1];
			tv[0] = result.newFreshVariable(null);
			for (int i = 0; i < children.length; i++) {
				tv[i+1] = result.newFreshVariable(null);
				children[i].typeCheck(result,quantifiedIdentifiers);
				result.unify(children[i].getType(), result.makeRelationalType(tv[i+1], tv[i]), loc);
			}
			resultType = result.makeRelationalType(tv[children.length], tv[0]);
			break;
		case Formula.FCOMP:
			tv = new TypeVariable[children.length+1];
			tv[0] = result.newFreshVariable(null);
			for (int i = 0; i < children.length; i++) {
				tv[i+1] = result.newFreshVariable(null);
				children[i].typeCheck(result,quantifiedIdentifiers);
				result.unify(children[i].getType(), result.makeRelationalType(tv[i], tv[i+1]), loc);
			}
			resultType = result.makeRelationalType(tv[0], tv[children.length]);
			break;
		case Formula.OVR:
			alpha = result.newFreshVariable(null);
			TypeVariable beta = result.newFreshVariable(null);
			resultType = result.makeRelationalType(alpha, beta);
			for (int i = 0; i < children.length; i++) {
				children[i].typeCheck(result,quantifiedIdentifiers);
				result.unify(children[i].getType(), resultType,loc);
			}
			break;
		case Formula.PLUS:
		case Formula.MUL:
			resultType = result.makeIntegerType();
			for (int i = 0; i < children.length; i++) {
				children[i].typeCheck(result,quantifiedIdentifiers);
				result.unify(children[i].getType(), resultType, loc);
			}
			break;
		default:
			assert false;
			resultType = null;
		}
		setType(resultType, result);
	}
	
	@Override
	protected boolean solveType(TypeUnifier unifier) {
		boolean success = true;
		for (Expression child : children) {
			success &= child.solveType(unifier);
		}
		return finalizeType(success, unifier);
	}

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		AssociativeHelper.isLegibleList(children, result, quantifiedIdents);	
	}
	
	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return getWDConjunction(formulaFactory, children);
	}

	@Override
	protected String toStringFullyParenthesized(String[] boundNames) {
		return toStringFullyParenthesizedHelper(boundNames, children, getTagOperator());
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return getSyntaxTreeHelper(boundNames, tabs,
				children, getTagOperator(), getTypeName(), this.getClass()
						.getSimpleName());
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdents) {
		for (Expression child: children) {
			child.collectFreeIdentifiers(freeIdents);
		}
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		for (Expression child: children) {
			child.collectNamesAbove(names, boundNames, offset);
		}
	}

	@Override
	protected Expression bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		boolean changed = false;
		Expression[] newChildren = new Expression[children.length];
		for (int i = 0; i < children.length; i++) {
			newChildren[i] = children[i].bindTheseIdents(binding, offset, factory);
			changed |= newChildren[i] != children[i];
		}
		if (! changed) {
			return this;
		}
		return factory.makeAssociativeExpression(getTag(), newChildren, getSourceLocation());
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case BUNION: goOn = visitor.enterBUNION(this); break;
		case BINTER: goOn = visitor.enterBINTER(this); break;
		case BCOMP:  goOn = visitor.enterBCOMP(this);  break;
		case FCOMP:  goOn = visitor.enterFCOMP(this);  break;
		case OVR:    goOn = visitor.enterOVR(this);    break;
		case PLUS:   goOn = visitor.enterPLUS(this);   break;
		case MUL:    goOn = visitor.enterMUL(this);    break;
		default:     assert false;
		}

		for (int i = 0; goOn && i < children.length; i++) {
			goOn = children[i].accept(visitor);
		}
		
		switch (getTag()) {
		case BUNION: return visitor.exitBUNION(this);
		case BINTER: return visitor.exitBINTER(this);
		case BCOMP:  return visitor.exitBCOMP(this);
		case FCOMP:  return visitor.exitFCOMP(this);
		case OVR:    return visitor.exitOVR(this);
		case PLUS:   return visitor.exitPLUS(this);
		case MUL:    return visitor.exitMUL(this);
		default:     return true;
		}
	}

	@Override
	protected boolean isWellFormed(int noOfBoundVars) {
		for(int i=0; i<children.length; i++) {
			if(!children[i].isWellFormed(noOfBoundVars))
				return false;
		}
		return true;
	}

	@Override
	protected AssociativeExpression substituteAll(int noOfBoundVars, Replacement replacement, FormulaFactory formulaFactory) {
		Expression[] newChildren = new Expression[children.length]; 
		boolean equal = getSubstitutedList(noOfBoundVars, children, replacement, newChildren, formulaFactory);
		if (equal) {
			return this;
		} else {
			return formulaFactory.makeAssociativeExpression(getTag(), newChildren, getSourceLocation());
		}
	}

}
