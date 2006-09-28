/*
 * Created on 11-may-2005
 *
 */
package org.eventb.core.ast;

import static org.eventb.core.ast.AssociativeHelper.equalsHelper;
import static org.eventb.core.ast.AssociativeHelper.getSubstitutedList;
import static org.eventb.core.ast.AssociativeHelper.toStringFullyParenthesizedHelper;
import static org.eventb.core.ast.AssociativeHelper.toStringHelper;

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
			SourceLocation location, FormulaFactory ff) {
		
		super(tag, location, combineHashCodes(children));
		this.children = new Predicate[children.length];
		System.arraycopy(children, 0, this.children, 0, children.length);
		
		checkPreconditions();
		synthesizeType(ff);
	}

	protected AssociativePredicate(List<Predicate> children, int tag,
			SourceLocation location, FormulaFactory ff) {
		
		super(tag, location, combineHashCodes(children));
		Predicate[] model = new Predicate[children.size()];
		this.children = children.toArray(model);

		checkPreconditions();
		synthesizeType(ff);
	}

	// Common initialization.
	private void checkPreconditions() {
		assert getTag() >= firstTag && getTag() < firstTag+tags.length;
		assert children != null;
		assert children.length >= 2;
	}
	
	@Override
	protected void synthesizeType(FormulaFactory ff) {
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
		typeChecked = true;
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
	protected void toString(StringBuilder builder, boolean isRightChild,
			int parentTag, String[] boundNames, boolean withTypes) {

		toStringHelper(builder, boundNames, needsParenthesis(parentTag),
				children, getTagOperator(), getTag(), withTypes);
	}

	private boolean needsParenthesis(int parentTag) {
		return parenthesesMap[getTag() - firstTag].get(parentTag);
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
		boolean changed = false;
		for (Predicate child: children) {
			Predicate normChild = child.flatten(factory);
			if (normChild.getTag() == getTag()) {
				AssociativePredicate assocNormChild = (AssociativePredicate) normChild;
				newChildren.addAll(Arrays.asList(assocNormChild.children));
				changed = true;
			}
			else {
				newChildren.add(normChild);
				changed |= (child != normChild);
			}
		}
		if (! changed) {
			return this;
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
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		boolean success = true;
		for (Predicate child: children) {
			success &= child.solveType(unifier);
		}
		return success;
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
	protected void toStringFullyParenthesized(StringBuilder builder,
			String[] boundNames) {
		
		toStringFullyParenthesizedHelper(builder, boundNames, children, getTagOperator());
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
			if (i != 0) {
				switch (getTag()) {
				case LAND: goOn = visitor.continueLAND(this); break;
				case LOR:  goOn = visitor.continueLOR(this);  break;
				default:     assert false;
				}
			}
			if (goOn) {
				goOn = children[i].accept(visitor);
			}
		}
		
		switch (getTag()) {
		case LAND: return visitor.exitLAND(this);
		case LOR:  return visitor.exitLOR(this);
		default:   assert false; return true;
		}
	}

	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
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
	public AssociativePredicate applySubstitution(Substitution subst) {
		final FormulaFactory ff = subst.getFactory();
		Predicate[] newChildren = new Predicate[children.length]; 
		boolean equal = getSubstitutedList(children, subst, newChildren, ff);
		if (equal)
			return this;
		return ff.makeAssociativePredicate(getTag(), newChildren, getSourceLocation());
	}

}
