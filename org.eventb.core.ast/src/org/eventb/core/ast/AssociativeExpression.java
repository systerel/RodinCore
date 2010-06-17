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
 *     Systerel - fixed bug in synthesizeType()
 *     Systerel - mathematical language v2
 *     Systerel - added support for predicate variables
 *     Systerel - generalised getPositions() into inspect()
 *******************************************************************************/ 
package org.eventb.core.ast;

import static org.eventb.core.ast.AssociativeHelper.equalsHelper;
import static org.eventb.core.ast.AssociativeHelper.getSyntaxTreeHelper;
import static org.eventb.core.ast.AssociativeHelper.toStringFullyParenthesizedHelper;
import static org.eventb.core.ast.AssociativeHelper.toStringHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
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
 * @since 1.0
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
			SourceLocation location, FormulaFactory factory) {

		super(tag, location, combineHashCodes(children));
		this.children = children.clone();
		checkPreconditions();
		setPredicateVariableCache(this.children);
		synthesizeType(factory, null);
	}

	protected AssociativeExpression(Collection<? extends Expression> children,
			int tag, SourceLocation location, FormulaFactory factory) {

		super(tag, location, combineHashCodes(children));
		Expression[] model = new Expression[children.size()];
		this.children = children.toArray(model);
		checkPreconditions();
		setPredicateVariableCache(this.children);
		synthesizeType(factory, null);
	}

	private void checkPreconditions() {
		assert getTag() >= firstTag && getTag() < firstTag+tags.length;
		assert children != null;
		assert children.length >= 2;
	}

	@Override
	protected void synthesizeType(FormulaFactory ff, Type givenType) {
		IdentListMerger freeIdentMerger = mergeFreeIdentifiers(children);
		this.freeIdents = freeIdentMerger.getFreeMergedArray();

		IdentListMerger boundIdentMerger = mergeBoundIdentifiers(children);
		this.boundIdents = boundIdentMerger.getBoundMergedArray();

		if (freeIdentMerger.containsError() || boundIdentMerger.containsError()) {
			// Incompatible type environments, don't bother going further.
			return;
		}
		
		// Fast exit if first child is not typed
		// (the most common case where type synthesis can't be done)
		if (! children[0].isTypeChecked()) {
			return;
		}
		
		Type resultType;
		Type partType, sourceType, targetType;
		final int last = children.length - 1;
		switch (getTag()) {
		case Formula.BUNION:
		case Formula.BINTER:
			resultType = children[0].getType();
			if (! (resultType instanceof PowerSetType)) {
				return;
			}
			for (int i = 1; i <= last; i++) {
				if (! resultType.equals(children[i].getType())) {
					return;
				}
			}
			break;
		case Formula.BCOMP:
			partType = children[0].getType().getSource();
			if (partType == null) {
				return;
			}
			for (int i = 1; i <= last; i++) {
				final Type childType = children[i].getType();
				if (childType == null) {
					return;
				}
				if (! partType.equals(childType.getTarget())) {
					return;
				}
				partType = childType.getSource();
			}
			sourceType = children[last].getType().getSource();
			targetType = children[0].getType().getTarget();
			resultType = ff.makeRelationalType(sourceType, targetType);
			break;
		case Formula.FCOMP:
			partType = children[0].getType().getTarget();
			if (partType == null) {
				return;
			}
			for (int i = 1; i <= last; i++) {
				final Type childType = children[i].getType();
				if (childType == null) {
					return;
				}
				if (! partType.equals(childType.getSource())) {
					return;
				}
				partType = childType.getTarget();
			}
			sourceType = children[0].getType().getSource();
			targetType = children[last].getType().getTarget();
			resultType = ff.makeRelationalType(sourceType, targetType);
			break;
		case Formula.OVR:
			resultType = children[0].getType();
			if (! resultType.isRelational()) {
				return;
			}
			for (int i = 1; i <= last; i++) {
				if (! resultType.equals(children[i].getType())) {
					return;
				}
			}
			break;
		case Formula.PLUS:
		case Formula.MUL:
			resultType = children[0].getType();
			for (Expression child: children) {
				final Type childType = child.getType();
				if (! (childType instanceof IntegerType)) {
					return;
				}
			}
			break;
		default:
			assert false;
			return;
		}
		setFinalType(resultType, givenType);
	}
	
	// indicates when the toString method should put parentheses
	private final static BitSet[] leftNoParenthesesMap = new BitSet[tags.length];
	private final static BitSet[] rightNoParenthesesMap = new BitSet[tags.length];

	// fills the parentheses maps 
	static {
		assert tags.length == leftNoParenthesesMap.length;
		assert tags.length == rightNoParenthesesMap.length;

		final BitSet propagateLeft = new BitSet();
		final BitSet commonTempLeft, commonTempRight;
		BitSet temp;
		
		propagateLeft.set(Formula.NO_TAG);

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
		addDeprecatedUnaryTags(propagateLeft);
		propagateLeft.set(Formula.KMIN);
		propagateLeft.set(Formula.KMAX);
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
		
		final BitSet propagateRight = (BitSet)propagateLeft.clone();
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

	@SuppressWarnings("deprecation")
	private static void addDeprecatedUnaryTags(final BitSet bitset) {
		bitset.set(Formula.KPRJ1);
		bitset.set(Formula.KPRJ2);
		bitset.set(Formula.KID);
	}
	
	/**
	 * Returns the children of this node.
	 * 
	 * @return the children of this node. Can never be <code>null</code> or
	 *         empty.
	 */
	public Expression[] getChildren() {
		return children.clone();
	}

	@Override
	protected void toString(StringBuilder builder, boolean isRightChild,
			int parentTag, String[] boundNames, boolean withTypes) {

		toStringHelper(builder, boundNames, needsParenthesis(isRightChild,
				parentTag), children, getTagOperator(), getTag(), withTypes);
	}

	protected String getTagOperator() {
		return tags[getTag()-firstTag];
	}

	private boolean needsParenthesis(boolean isRightChild, int parentTag) {
		final int relativeTag = getTag() - firstTag;
		if (isRightChild) {
			return ! rightNoParenthesesMap[relativeTag].get(parentTag);
		}
		return ! leftNoParenthesesMap[relativeTag].get(parentTag);
	}
	
	@Override
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		if (this.getTag() != other.getTag()) {
			return false;
		}
		return hasSameType(other)
				&& equalsHelper(children,
						((AssociativeExpression) other).children,
						withAlphaConversion);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		final Type resultType;
		switch (getTag()) {
		case Formula.BUNION:
		case Formula.BINTER:
			TypeVariable alpha = result.newFreshVariable(null);
			resultType = result.makePowerSetType(alpha);
			for (int i = 0; i < children.length; i++) {
				children[i].typeCheck(result,quantifiedIdentifiers);
				result.unify(children[i].getType(), resultType, this);
			}
			break;
		case Formula.BCOMP:
			TypeVariable[] tv = new TypeVariable[children.length+1];
			tv[0] = result.newFreshVariable(null);
			for (int i = 0; i < children.length; i++) {
				tv[i+1] = result.newFreshVariable(null);
				children[i].typeCheck(result,quantifiedIdentifiers);
				result.unify(children[i].getType(), result.makeRelationalType(tv[i+1], tv[i]), this);
			}
			resultType = result.makeRelationalType(tv[children.length], tv[0]);
			break;
		case Formula.FCOMP:
			tv = new TypeVariable[children.length+1];
			tv[0] = result.newFreshVariable(null);
			for (int i = 0; i < children.length; i++) {
				tv[i+1] = result.newFreshVariable(null);
				children[i].typeCheck(result,quantifiedIdentifiers);
				result.unify(children[i].getType(), result.makeRelationalType(tv[i], tv[i+1]), this);
			}
			resultType = result.makeRelationalType(tv[0], tv[children.length]);
			break;
		case Formula.OVR:
			alpha = result.newFreshVariable(null);
			TypeVariable beta = result.newFreshVariable(null);
			resultType = result.makeRelationalType(alpha, beta);
			for (int i = 0; i < children.length; i++) {
				children[i].typeCheck(result,quantifiedIdentifiers);
				result.unify(children[i].getType(), resultType, this);
			}
			break;
		case Formula.PLUS:
		case Formula.MUL:
			resultType = result.makeIntegerType();
			for (int i = 0; i < children.length; i++) {
				children[i].typeCheck(result,quantifiedIdentifiers);
				result.unify(children[i].getType(), resultType, this);
			}
			break;
		default:
			assert false;
			return;
		}
		setTemporaryType(resultType, result);
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		boolean success = true;
		for (Expression child : children) {
			success &= child.solveType(unifier);
		}
		return success;
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
	protected void toStringFullyParenthesized(StringBuilder builder,
			String[] boundNames) {
		
		toStringFullyParenthesizedHelper(builder, boundNames, children, getTagOperator());
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return getSyntaxTreeHelper(boundNames, tabs,
				children, getTagOperator(), getTypeName(), this.getClass()
						.getSimpleName());
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		for (Expression child: children) {
			child.collectFreeIdentifiers(freeIdentSet);
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
			if (i != 0) {
				switch (getTag()) {
				case BUNION: goOn = visitor.continueBUNION(this); break;
				case BINTER: goOn = visitor.continueBINTER(this); break;
				case BCOMP:  goOn = visitor.continueBCOMP(this);  break;
				case FCOMP:  goOn = visitor.continueFCOMP(this);  break;
				case OVR:    goOn = visitor.continueOVR(this);    break;
				case PLUS:   goOn = visitor.continuePLUS(this);   break;
				case MUL:    goOn = visitor.continueMUL(this);    break;
				default:     assert false;
				}
			}
			if (goOn) {
				goOn = children[i].accept(visitor);
			}
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
	public void accept(ISimpleVisitor visitor) {
		visitor.visitAssociativeExpression(this);		
	}

	@Override
	public Expression rewrite(IFormulaRewriter rewriter) {
		final boolean flatten = rewriter.autoFlatteningMode();
		final ArrayList<Expression> newChildren =
			new ArrayList<Expression>(children.length + 11); 
		boolean changed = false;
		for (Expression child: children) {
			Expression newChild = child.rewrite(rewriter);
			if (flatten && getTag() == newChild.getTag()) {
				final Expression[] grandChildren =
					((AssociativeExpression) newChild).children;
				newChildren.addAll(Arrays.asList(grandChildren));
				changed = true;
			} else {
				newChildren.add(newChild);
				changed |= newChild != child;
			}
		}
		final AssociativeExpression before;
		if (! changed) {
			before = this;
		} else {
			before = rewriter.getFactory().makeAssociativeExpression(getTag(),
					newChildren, getSourceLocation());
		}
		return checkReplacement(rewriter.rewrite(before));
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		for (Expression child: children) {
			child.addGivenTypes(set);
		}
	}

	@Override
	protected final <F> void inspect(FindingAccumulator<F> acc) {
		acc.inspect(this);
		acc.enterChildren();
		for (Expression child: children) {
			child.inspect(acc);
			acc.nextChild();
		}
		acc.leaveChildren();
	}

	@Override
	protected Formula<?> getChild(int index) {
		if (index < children.length) {
			return children[index];
		}
		return null;
	}

	@Override
	public IPosition getDescendantPos(SourceLocation sloc, IntStack indexes) {
		indexes.push(0);
		for (Expression child: children) {
			IPosition pos = child.getPosition(sloc, indexes);
			if (pos != null)
				return pos;
			indexes.incrementTop();
		}
		indexes.pop();
		return new Position(indexes);
	}

	@Override
	protected Expression rewriteChild(int index, SingleRewriter rewriter) {
		if (index < 0 || children.length <= index) 
			throw new IllegalArgumentException("Position is outside the formula");
		Expression[] newChildren = children.clone();
		newChildren[index] = rewriter.rewrite(children[index]);
		return rewriter.factory.makeAssociativeExpression(
				getTag(), newChildren, getSourceLocation());
	}

}
