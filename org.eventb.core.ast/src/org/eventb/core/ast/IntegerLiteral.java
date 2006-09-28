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
		
		synthesizeType(ff, null);
	}

	@Override
	protected void synthesizeType(FormulaFactory ff, Type givenType) {
		this.freeIdents = NO_FREE_IDENT;
		this.boundIdents = NO_BOUND_IDENT;
		
		setFinalType(ff.makeIntegerType(), givenType);
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
	protected void toString(StringBuilder builder, boolean isRightChild,
			int parentTag, String[] boundNames, boolean withTypes) {

		toStringInternal(builder);
	}

	@Override
	protected void toStringFullyParenthesized(StringBuilder builder,
			String[] boundNames) {

		toStringInternal(builder);
	}

	/**
	 * Change the minus sign if any, so that it conforms to the mathematical
	 * language: \u2212 (minus sign) instead of \u002d (hyphen-minus).
	 */
	private void toStringInternal(StringBuilder builder) {
		final String image = literal.toString();
		if (image.charAt(0) == '-') {
			builder.append('\u2212');
			builder.append(image, 1, image.length());
		} else {
			builder.append(image);
		}
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
		setTemporaryType(result.makeIntegerType(), result);
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		return true;
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
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return formulaFactory.makeLiteralPredicate(BTRUE, null);
	}

	@Override
	public IntegerLiteral applySubstitution(Substitution subst) {
		return this;
	}

}
