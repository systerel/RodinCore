/*
 * Created on 11-may-2005
 *
 */
package org.eventb.core.ast;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Replacement;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * UnaryPredicate is the base class for all unary predicates in an event-B formula.
 * <p>
 * It can accept tags {NOT}.
 * </p>
 * 
 * @author François Terrier
 */
public class UnaryPredicate extends Predicate {
	
	protected final Predicate child;

	// offset in the corresponding tag interval
	protected static final int firstTag = FIRST_UNARY_PREDICATE;
	protected static final String[] tags = {
		"\u00ac" // NOT
	};
	// For testing purposes
	public static final int TAGS_LENGTH = tags.length;

	protected UnaryPredicate(Predicate child, int tag, SourceLocation location) {
		super(tag, location, child.hashCode());
		this.child = child;
		
		assert tag >= firstTag && tag < firstTag+tags.length;
		assert child != null;
	}
	
	/**
	 * Returns the child of this node.
	 * 
	 * @return the child predicate of this node
	 */
	public Predicate getChild() {
		return child;
	}
	
	@Override
	protected String toString(boolean isRightChild, int parentTag, String[] boundNames) {
		return getTagOperator()+child.toString(false,getTag(),boundNames);
	}

	protected String getTagOperator() {
		return tags[getTag()-firstTag];
	}
	
	@Override
	protected boolean equals(Formula other, boolean withAlphaConversion) {
		UnaryPredicate temp = (UnaryPredicate) other;
		return child.equals(temp.child, withAlphaConversion);
	}

	@Override
	public Predicate flatten(FormulaFactory factory) {
		return factory.makeUnaryPredicate(getTag(),child.flatten(factory),getSourceLocation());
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		child.typeCheck(result, quantifiedIdentifiers);
	}
	
	@Override
	protected boolean solveType(TypeUnifier unifier) {
		boolean success = child.solveType(unifier);
		return finalizeTypeCheck(success);
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return tabs + this.getClass().getSimpleName() + " [" + getTagOperator()	+ "]\n"
		+ child.getSyntaxTree(boundNames, tabs + "\t");
	}

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		child.isLegible(result, quantifiedIdents);
	}

	@Override
	protected String toStringFullyParenthesized(String[] boundNames) {
		return getTagOperator()+"("+child.toStringFullyParenthesized(boundNames)+")";
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdents) {
		child.collectFreeIdentifiers(freeIdents);
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		child.collectNamesAbove(names, boundNames, offset);
	}
	
	@Override
	protected Predicate bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		Predicate newChild = child.bindTheseIdents(binding, offset, factory);
		if (newChild == child) {
			return this;
		}
		return factory.makeUnaryPredicate(getTag(), newChild, getSourceLocation());
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case NOT: goOn = visitor.enterNOT(this); break;
		default:  assert false;
		}

		if (goOn) goOn = child.accept(visitor);
		
		switch (getTag()) {
		case NOT: return visitor.exitNOT(this);
		default:  return true;
		}
	}

	@Override
	public Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return child.getWDPredicateRaw(formulaFactory);
	}

	@Override
	protected boolean isWellFormed(int noOfBoundVars) {
		return child.isWellFormed(noOfBoundVars);
	}

	@Override
	protected Predicate substituteAll(int noOfBoundVars, Replacement replacement, FormulaFactory formulaFactory) {
		Predicate newChild = child.substituteAll(noOfBoundVars, replacement,formulaFactory);
		if(newChild == child)
			return this;
		else
			return formulaFactory.makeUnaryPredicate(getTag(), newChild, getSourceLocation());
	}

}
