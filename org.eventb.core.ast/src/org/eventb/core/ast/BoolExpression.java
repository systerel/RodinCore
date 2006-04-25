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

/**
 * BoolExpression represents the bool keyword of an event-B formula.
 * <p>
 * Can only accept {KBOOL}.
 * </p>
 * 
 * @author François Terrier
 */
public class BoolExpression extends Expression {

	// child
	private final Predicate child;
	
	protected BoolExpression(Predicate child, int tag, SourceLocation location,
			FormulaFactory ff) {
		
		super(tag, location, child.hashCode());
		assert tag == KBOOL;
		this.child = child;
		this.freeIdents = child.freeIdents;
		this.boundIdents = child.boundIdents;
		if (child.isTypeChecked()) {
			setType(ff.makeBooleanType(), null);
		}
	}

	/**
	 * Returns the predicate child of this node, that is the predicate whose
	 * truth value is transformed into a boolean expression by this operator.
	 * 
	 * @return the predicate child
	 */
	public Predicate getPredicate() {
		return child;
	}
	
	@Override
	protected String toString(boolean isRightChild, int parentTag,
			String[] boundNames, boolean withTypes) {

		// does not put parentheses when parent is: TILDE
		return "bool("+child.toString(false, getTag(), boundNames, withTypes)+")";
	}

	@Override
	protected String toStringFullyParenthesized(String[] boundNames) {
		return "bool("+child.toStringFullyParenthesized(boundNames)+")";
	}

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		child.isLegible(result, quantifiedIdents);
	}

	@Override
	protected boolean equals(Formula other, boolean withAlphaConversion) {
		return hasSameType(other)
				&& child.equals(((BoolExpression) other).child, withAlphaConversion);
	}

	@Override
	public Expression flatten(FormulaFactory factory) {
		final Predicate newChild = child.flatten(factory);
		if (newChild == child) {
			return this;
		}
		return factory.makeBoolExpression(newChild,getSourceLocation());
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		child.typeCheck(result, quantifiedIdentifiers);
		setType(result.makeBooleanType(), result);
	}
	
	@Override
	protected boolean solveType(TypeUnifier unifier) {
		boolean success = child.solveType(unifier);
		return finalizeType(success, unifier);
	}
	
	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String typeName = getType()!=null?" [type: "+getType().toString()+"]":"";
		return tabs + this.getClass().getSimpleName() + " [bool]" + typeName
				+ "\n" + child.getSyntaxTree(boundNames, tabs + "\t");
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
		Predicate newChild = child.bindTheseIdents(binding, offset, factory);
		if (newChild == child) {
			return this;
		}
		return factory.makeBoolExpression(newChild, getSourceLocation());
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case KBOOL: goOn = visitor.enterKBOOL(this); break;
		default:    assert false;
		}

		if (goOn) goOn = child.accept(visitor);
		
		switch (getTag()) {
		case KBOOL: return visitor.exitKBOOL(this);
		default:    return true;
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
	public BoolExpression applySubstitution(Substitution subst) {
		final FormulaFactory ff = subst.getFactory();
		Predicate newChild = child.applySubstitution(subst);
		if (newChild == child)
			return this;
		return ff.makeBoolExpression(newChild, getSourceLocation());
	}

}
