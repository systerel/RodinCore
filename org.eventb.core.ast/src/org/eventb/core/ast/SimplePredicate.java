/*
 * Created on 20-may-2005
 *
 */
package org.eventb.core.ast;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Substitution;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;
import org.eventb.internal.core.typecheck.TypeVariable;

/**
 * SimplePredicate represents a predicate builds from a single expression in an
 * event-B formula.
 * <p>
 * It can accept tag {KFINITE}.
 * </p>
 * 
 * @author François Terrier
 */
public class SimplePredicate extends Predicate {
	
	// child
	private final Expression child;
	
	// offset in the corresponding tag interval
	private static final int firstTag = FIRST_SIMPLE_PREDICATE;
	private static final String[] tags = {
		"finite" // KFINITE
	};
	
	protected SimplePredicate(Expression child, int tag, SourceLocation location) {
		super(tag, location, child.hashCode());
		assert tag >= firstTag && tag < firstTag+tags.length;
		
		this.child = child;
	}
	
	/**
	 * Returns the child expression of this node.
	 * 
	 * @return the expression of this node 
	 */
	public Expression getExpression() {
		return child;
	}

	@Override
	protected String toString(boolean isRightChild, int parentTag, String[] boundNames) {
		return tags[getTag()-firstTag]+"("+child.toString(false, getTag(), boundNames)+")";
	}

	@Override
	protected String toStringFullyParenthesized(String[] boundNames) {
		return tags[getTag()-firstTag]+"("+child.toStringFullyParenthesized(boundNames)+")";
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return tabs + this.getClass().getSimpleName() + " ["
				+ tags[getTag() - firstTag] + "]\n"
				+ child.getSyntaxTree(boundNames, tabs + "\t");
	}

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		child.isLegible(result, quantifiedIdents);
	}
	
	@Override
	protected boolean equals(Formula other, boolean withAlphaConversion) {
		SimplePredicate temp = (SimplePredicate) other;
		return child.equals(temp.child, withAlphaConversion);
	}

	@Override
	public Predicate flatten(FormulaFactory factory) {
		return factory.makeSimplePredicate(getTag(),child.flatten(factory),getSourceLocation());
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		TypeVariable alpha = result.newFreshVariable(null);
		child.typeCheck(result, quantifiedIdentifiers);
		result.unify(child.getType(), result.makePowerSetType(alpha), getSourceLocation());
	}
	
	@Override
	protected boolean solveType(TypeUnifier unifier) {
		boolean success = child.solveType(unifier);
		return finalizeTypeCheck(success);
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
		Expression newChild = child.bindTheseIdents(binding, offset, factory);
		if (newChild == child) {
			return this;
		}
		return factory.makeSimplePredicate(getTag(), newChild, getSourceLocation());
	}
	
	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case KFINITE: goOn = visitor.enterKFINITE(this); break;
		default:      assert false;
		}

		if (goOn) goOn = child.accept(visitor);
		
		switch (getTag()) {
		case KFINITE: return visitor.exitKFINITE(this);
		default:      return true;
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
	public SimplePredicate applySubstitution(Substitution subst, FormulaFactory ff) {
		Expression newChild = child.applySubstitution(subst, ff);
		if (newChild == child)
			return this;
		return ff.makeSimplePredicate(getTag(), newChild, getSourceLocation());
	}

}
