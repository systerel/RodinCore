/*
 * Created on 11-may-2005
 *
 */
package org.eventb.core.ast;

import java.util.BitSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Substitution;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * BinaryPredicate is the base class for all binary predicates in an event-B
 * formula.
 * <p>
 * It can only accept {LIMP, LEQV}.
 * </p>
 * 
 * @author François Terrier
 */
public class BinaryPredicate extends Predicate {
	
	// Left and right children.
	// Are never null by construction.
	private final Predicate left;
	private final Predicate right;
	
	// offset of the corresponding tag-interval in Formula
	protected final static int firstTag = FIRST_BINARY_PREDICATE;
	protected final static String[] tags = {
		"\u21d2", // LIMP
		"\u21d4"  // LEQV
	};
	// For testing purposes
	public static final int TAGS_LENGTH = tags.length;
	
	protected BinaryPredicate(Predicate left, Predicate right, int tag,
			SourceLocation location) {
		super(tag, location, combineHashCodes(left.hashCode(), right.hashCode()));
		this.left = left;
		this.right = right;
		
		assert tag >= firstTag && tag < firstTag+tags.length;
		assert left != null;
		assert right != null;

		synthesizeType();
	}
	
	private void synthesizeType() {
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
		
		finalizeTypeCheck(left.isTypeChecked() && right.isTypeChecked());
	}

	// indicates when toString should put itself inside parentheses
	private static final BitSet[] parenthesesMap = new BitSet[tags.length];
	
	static {
		assert parenthesesMap.length == tags.length;
		
		for (int i = 0; i < parenthesesMap.length; i++) {
			parenthesesMap[i] = new BitSet();
			parenthesesMap[i].set(Formula.LEQV);
			parenthesesMap[i].set(Formula.LIMP);
			parenthesesMap[i].set(Formula.NOT);
			parenthesesMap[i].set(Formula.LAND);
			parenthesesMap[i].set(Formula.LOR);
		}
	}

	@Override
	protected String toString(boolean isRightChild, int parentTag,
			String[] boundNames, boolean withTypes) {

		// parenthteses if AND OR or NOT
		final String str = 
			left.toString(false, getTag(), boundNames, withTypes) +
			getTagOperator() +
			right.toString(true, getTag(), boundNames, withTypes);
		if (parenthesesMap[getTag()-firstTag].get(parentTag)) {
			return "(" + str + ")";
		}
		return str;
	}

	// Tag operator.
	protected String getTagOperator() {
		return tags[getTag()-firstTag];
	}
	
	@Override
	protected boolean equals(Formula other, boolean withAlphaConversion) {
		BinaryPredicate temp = (BinaryPredicate) other;
		return left.equals(temp.left, withAlphaConversion)
				&& right.equals(temp.right, withAlphaConversion);
	}

	@Override
	public Predicate flatten(FormulaFactory factory) {
		final Predicate newLeft = left.flatten(factory);
		final Predicate newRight = right.flatten(factory);
		if (newLeft == left && newRight == right) {
			return this;
		}
		return factory.makeBinaryPredicate(getTag(), newLeft, newRight, getSourceLocation());
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		left.typeCheck(result, quantifiedIdentifiers);
		right.typeCheck(result, quantifiedIdentifiers);
	}
	
	@Override
	protected boolean solveType(TypeUnifier unifier) {
		boolean success = left.solveType(unifier) & right.solveType(unifier);
		return finalizeTypeCheck(success);
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return tabs + this.getClass().getSimpleName() + " [" + getTagOperator() + "]\n"
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
	 * Returns the predicate on the left-hand side of this node.
	 * 
	 * @return the predicate on the left-hand side
	 */
	public Predicate getLeft() {
		return left;
	}

	/**
	 * Returns the predicate on the right-hand side of this node.
	 * 
	 * @return the predicate on the right-hand side
	 */
	public Predicate getRight() {
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
	protected Predicate bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		Predicate newLeft = left.bindTheseIdents(binding, offset, factory);
		Predicate newRight = right.bindTheseIdents(binding, offset, factory);
		if (newLeft == left && newRight == right) {
			return this;
		}
		return factory.makeBinaryPredicate(getTag(), newLeft, newRight, getSourceLocation());
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case LIMP: goOn = visitor.enterLIMP(this); break;
		case LEQV: goOn = visitor.enterLEQV(this); break;
		default:   assert false;
		}

		if (goOn) goOn = left.accept(visitor);
		
		if (goOn) {
			switch (getTag()) {
			case LIMP: goOn = visitor.continueLIMP(this); break;
			case LEQV: goOn = visitor.continueLEQV(this); break;
			default:   assert false;
			}
		}
		
		if (goOn) goOn = right.accept(visitor);
		
		switch (getTag()) {
		case LIMP: return visitor.exitLIMP(this);
		case LEQV: return visitor.exitLEQV(this);
		default:   return true;
		}
	}

	private Predicate getWDPredicateLIMP(FormulaFactory formulaFactory) {
		Predicate conj0 = left.getWDPredicateRaw(formulaFactory);
		Predicate conj1 = getWDSimplifyI(formulaFactory, left, right.getWDPredicateRaw(formulaFactory));
		return getWDSimplifyC(formulaFactory, conj0, conj1);
	}
	
	private Predicate getWDPredicateLEQV(FormulaFactory formulaFactory) {
		return getWDConjunction(formulaFactory, left, right);
	}
	
	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		switch (getTag()) {
		case LIMP: return getWDPredicateLIMP(formulaFactory);
		case LEQV: return getWDPredicateLEQV(formulaFactory);
		default:   assert false; return null;
		}
	}

	@Override
	protected boolean isWellFormed(int noOfBoundVars) {
		return left.isWellFormed(noOfBoundVars) && right.isWellFormed(noOfBoundVars);
	}

	@Override
	public BinaryPredicate applySubstitution(Substitution subst) {
		final FormulaFactory ff = subst.getFactory();
		Predicate newLeft = left.applySubstitution(subst);
		Predicate newRight = right.applySubstitution(subst);
		if(newLeft == left && newRight == right)
			return this;
		return ff.makeBinaryPredicate(getTag(), newLeft, newRight, getSourceLocation());
	}

}
