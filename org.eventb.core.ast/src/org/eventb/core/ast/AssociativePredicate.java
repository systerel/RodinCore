/*
 * Created on 11-may-2005
 *
 */
package org.eventb.core.ast;

import static org.eventb.core.ast.AssociativeHelper.equalsHelper;
import static org.eventb.core.ast.AssociativeHelper.getSubstitutedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Substitution;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * AssociativePredicate is the AST class for the associative expressions in an
 * event-B formula.
 * <p>
 * It can have several children which can only be Predicate objects. Can only
 * accept {LAND, LOR, LEQV}.
 * </p>
 * 
 * @author François Terrier
 */
public class AssociativePredicate extends Predicate {
	
	// The children of this associative predicate.
	// Is never null and contains at least two elements by construction.
	private final Predicate[] children;
	
	// offset of the corresponding interval in Formula
	protected static int firstTag = FIRST_ASSOCIATIVE_PREDICATE;
	protected static String[] tags = {
		"\u2227", // LAND
		"\u2228"  // LOR
	};
	// For testing purposes
	public static final int TAGS_LENGTH = tags.length;
	
	protected AssociativePredicate(Predicate[] children, int tag,
			SourceLocation location) {
		
		super(tag, location, combineHashCodes(children));
		this.children = new Predicate[children.length];
		System.arraycopy(children, 0, this.children, 0, children.length);
		
		checkPreconditions();
		synthesizeType();
	}

	protected AssociativePredicate(List<Predicate> children, int tag,
			SourceLocation location) {
		
		super(tag, location, combineHashCodes(children));
		Predicate[] model = new Predicate[children.size()];
		this.children = children.toArray(model);

		checkPreconditions();
		synthesizeType();
	}

	// Common initialization.
	private void checkPreconditions() {
		assert getTag() >= firstTag && getTag() < firstTag+tags.length;
		assert children != null;
		assert children.length >= 2;
	}
	
	private void synthesizeType() {
		IdentListMerger freeIdentMerger = mergeFreeIdentifiers(children);
		this.freeIdents = freeIdentMerger.getFreeMergedArray();

		IdentListMerger boundIdentMerger = mergeBoundIdentifiers(children);
		this.boundIdents = boundIdentMerger.getBoundMergedArray();

		if (freeIdentMerger.containsError() || boundIdentMerger.containsError()) {
			// Incompatible type environments, don't bother going further.
			return;
		}
		
		for (Predicate child: children) {
			if (! child.isTypeChecked()) {
				return;
			}
		}
		finalizeTypeCheck(true);
	}
	
	// indicates when the toString method should put parentheses
	private static final BitSet[] parenthesesMap = new BitSet[tags.length];

	static {
		assert parenthesesMap.length == tags.length;
		
		for (int i = 0; i < parenthesesMap.length; i++) {
			parenthesesMap[i] = new BitSet();
			parenthesesMap[i].set(Formula.LOR);
			parenthesesMap[i].set(Formula.LAND);
			parenthesesMap[i].set(Formula.NOT);
		}
	}
	
	/**
	 * Returns the children of this node.
	 * 
	 * @return the children of this node. Can never be <code>null</code> or
	 *         empty.
	 */
	public Predicate[] getChildren() {
		Predicate[] temp = new Predicate[children.length];
		System.arraycopy(children, 0, temp, 0, children.length);
		return temp;
	}
	
	@Override
	protected String toString(boolean isRightChild, int parentTag,
			String[] boundNames, boolean withTypes) {
		
		StringBuffer str = new StringBuffer();
		str.append(children[0].toString(false, getTag(), boundNames, withTypes));
		for (int i=1; i<children.length;i++) {
			str.append(getTagOperator());
			str.append(children[i].toString(false,getTag(),boundNames,withTypes));
		}
		if (parenthesesMap[getTag()-firstTag].get(parentTag)) {
			return "("+str.toString()+")";
		}
		return str.toString();
	}

	protected String getTagOperator() {
		return tags[getTag()-firstTag];
	}

	@Override
	protected boolean equals(Formula other, boolean withAlphaConversion) {
		return equalsHelper(children, ((AssociativePredicate) other).children,
				withAlphaConversion);
	}
	
	@Override
	public Predicate flatten(FormulaFactory factory) {
		List<Predicate> newChildren = new ArrayList<Predicate>();
		for (Predicate child: children) {
			Predicate normChild = child.flatten(factory);
			if (normChild.getTag() == getTag()) {
				AssociativePredicate temp = (AssociativePredicate) normChild;
				newChildren.addAll(Arrays.asList(temp.getChildren()));
			}
			else {
				newChildren.add(normChild);
			}
		}
		return factory.makeAssociativePredicate(getTag(), newChildren, getSourceLocation());
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		for (Predicate child: children) {
			child.typeCheck(result,quantifiedIdentifiers);
		}
	}
	
	@Override
	protected boolean solveType(TypeUnifier unifier) {
		boolean success = true;
		for (Predicate child: children) {
			success &= child.solveType(unifier);
		}
		return finalizeTypeCheck(success);
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		StringBuffer str = new StringBuffer();
		str.append(tabs + this.getClass().getSimpleName() + " ["
				+ getTagOperator() + "]\n");
		for (Predicate child: children) {
			str.append(child.getSyntaxTree(boundNames, tabs+"\t"));
		}
		return str.toString();
	}

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		AssociativeHelper.isLegibleList(children, result, quantifiedIdents);
	}

	@Override
	protected String toStringFullyParenthesized(String[] boundNames) {
		StringBuffer str = new StringBuffer();
		str.append("(" + children[0].toStringFullyParenthesized(boundNames)
				+ ")");
		for (int i = 1; i < children.length; i++) {
			str.append(getTagOperator()
					+ "("
					+ children[i].toStringFullyParenthesized(boundNames)
					+ ")");
		}
		return str.toString();
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		for (Predicate child: children) {
			child.collectFreeIdentifiers(freeIdentSet);
		}
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		for (Predicate child: children) {
			child.collectNamesAbove(names, boundNames, offset);
		}
	}

	@Override
	protected Predicate bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		boolean changed = false;
		Predicate[] newChildren = new Predicate[children.length];
		for (int i = 0; i < children.length; i++) {
			newChildren[i] = children[i].bindTheseIdents(binding, offset, factory);
			changed |= newChildren[i] != children[i];
		}
		if (! changed) {
			return this;
		}
		return factory.makeAssociativePredicate(getTag(), newChildren, getSourceLocation());
	}
	

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case LAND: goOn = visitor.enterLAND(this); break;
		case LOR:  goOn = visitor.enterLOR(this);  break;
		default:   assert false;
		}

		for (int i = 0; goOn && i < children.length; i++) {
			goOn = children[i].accept(visitor);
		}
		
		switch (getTag()) {
		case LAND: return visitor.exitLAND(this);
		case LOR:  return visitor.exitLOR(this);
		default:   assert false; return true;
		}
	}

	@Override
	public Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		switch (getTag()) {
		case LAND: return getWDPredicateLAND(0, formulaFactory);
		case LOR:  return getWDPredicateLOR(0, formulaFactory);
		default:   assert false; return null;
		}
	}
	
	private Predicate getWDPredicateLOR(int pred, FormulaFactory formulaFactory) {
		if(pred + 1 == children.length) {
			return children[pred].getWDPredicateRaw(formulaFactory);
		} else {
			Predicate conj0 = children[pred].getWDPredicateRaw(formulaFactory);
			Predicate conj1disj0 = children[pred];
			Predicate conj1disj1 =getWDPredicateLOR(pred+1, formulaFactory);
			Predicate conj1 = getWDSimplifyD(formulaFactory, conj1disj0, conj1disj1);
			return getWDSimplifyC(formulaFactory, conj0, conj1);
		}
	}

	private Predicate getWDPredicateLAND(int pred, FormulaFactory formulaFactory) {
		if(pred + 1 == children.length) {
			return children[pred].getWDPredicateRaw(formulaFactory);
		} else {
			Predicate conj0 = children[pred].getWDPredicateRaw(formulaFactory);
			Predicate conj1ante = children[pred];
			Predicate conj1cons = getWDPredicateLAND(pred+1, formulaFactory);
			Predicate conj1 = getWDSimplifyI(formulaFactory, conj1ante, conj1cons);
			return getWDSimplifyC(formulaFactory, conj0, conj1);
		}	
	}

	@Override
	protected boolean isWellFormed(int noOfBoundVars) {
		for(int i=0; i<children.length; i++)
			if(!children[i].isWellFormed(noOfBoundVars))
				return false;
		return true;
	}

	@Override
	public AssociativePredicate applySubstitution(Substitution subst) {
		final FormulaFactory ff = subst.getFactory();
		Predicate[] newChildren = new Predicate[children.length]; 
		boolean equal = getSubstitutedList(children, subst, newChildren, ff);
		if (equal)
			return this;
		return ff.makeAssociativePredicate(getTag(), newChildren, getSourceLocation());
	}

}
