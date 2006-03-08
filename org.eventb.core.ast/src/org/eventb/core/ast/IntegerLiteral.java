/*
 * Created on 20-may-2005
 *
 */
package org.eventb.core.ast;

import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Substitution;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * IntegerLiteral represents a literal integer in an event-B formula.
 * <p>
 * It is a terminal symbol and has only one accessor that returns the
 * corresponding integer.
 * </p>
 * 
 * @author François Terrier
 */
public class IntegerLiteral extends Expression {
	
	// This literal value.  Can never be null.
	private final BigInteger literal;
	
	protected IntegerLiteral(BigInteger literal, int tag, SourceLocation location,
			FormulaFactory ff) {
		super(tag, location, literal.hashCode());
		assert tag == Formula.INTLIT;
		assert literal != null;
		this.literal = literal;
		
		this.freeIdents = NO_FREE_IDENTS;
		this.boundIdents = NO_BOUND_IDENTS;
		setType(ff.makeIntegerType(), null);
	}

	/**
	 * Returns the integer associated with this node.
	 * 
	 * @return an integer associated with this node.
	 */
	public BigInteger getValue() {
		return literal;
	}
	
	@Override
	protected String toString(boolean isRightChild, int parentTag, String[] boundNames) {
		return literal.toString();
	}

	@Override
	protected String toStringFullyParenthesized(String[] boundNames) {
		return literal.toString();
	}

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		return;
	}

	@Override
	protected boolean equals(Formula other, boolean withAlphaConversion) {
		return hasSameType(other)
				&& literal.equals(((IntegerLiteral) other).literal);
	}

	@Override
	public Expression flatten(FormulaFactory factory) {
		return this;
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		setType(result.makeIntegerType(), result);
	}
	
	@Override
	protected boolean solveType(TypeUnifier unifier) {
		return finalizeType(true, unifier);
	}
	
	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String typeName = getType()!=null?" [type: "+getType().toString()+"]":"";
		return tabs + this.getClass().getSimpleName() + " [literal: " + literal + "]" 
				+ typeName + "\n";
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		// Nothing to do
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		// Nothing to do
	}
	
	@Override
	protected Expression bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		return this;
	}
	
	@Override
	public boolean accept(IVisitor visitor) {
		return visitor.visitINTLIT(this);
	}

	@Override
	public Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return formulaFactory.makeLiteralPredicate(BTRUE, null);
	}

	@Override
	protected boolean isWellFormed(int noOfBoundVars) {
		return true;
	}

	@Override
	public IntegerLiteral applySubstitution(Substitution subst, FormulaFactory ff) {
		return this;
	}

}
