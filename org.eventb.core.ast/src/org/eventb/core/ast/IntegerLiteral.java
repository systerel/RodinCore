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
 *     Systerel - added support for predicate variables
 *******************************************************************************/
package org.eventb.core.ast;

import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
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
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
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
		
		setPredicateVariableCache();
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

		final boolean bracketed;
		if (literal.signum() < 0) {
			// A negative literal behaves like a unary minus.
			bracketed = UnaryExpression.needsParentheses(UNMINUS, isRightChild,
					parentTag);
		} else {
			bracketed = parentTag == UNMINUS;
		}
		
		if (bracketed) {
			builder.append('(');
		}
		toStringInternal(builder);
		if (bracketed) {
			builder.append(')');
		}
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
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		if (this.getTag() != other.getTag()) {
			return false;
		}
		return hasSameType(other)
				&& literal.equals(((IntegerLiteral) other).literal);
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
	public void accept(ISimpleVisitor visitor) {
		visitor.visitIntegerLiteral(this);
	}

	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return formulaFactory.makeLiteralPredicate(BTRUE, null);
	}

	@Override
	public Expression rewrite(IFormulaRewriter rewriter) {
		return checkReplacement(rewriter.rewrite(this));
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		// Nothing to do
	}

	@Override
	protected void getPositions(IFormulaFilter filter, IntStack indexes,
			List<IPosition> positions) {

		if (filter.select(this)) {
			positions.add(new Position(indexes));
		}
	}

	@Override
	protected Formula<?> getChild(int index) {
		return null;
	}

	@Override
	protected IPosition getDescendantPos(SourceLocation sloc, IntStack indexes) {
		return new Position(indexes);
	}

	@Override
	protected Expression rewriteChild(int index, SingleRewriter rewriter) {
		throw new IllegalArgumentException("Position is outside the formula");
	}

}
