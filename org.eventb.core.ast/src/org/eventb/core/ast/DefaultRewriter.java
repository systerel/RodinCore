/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language v2
 *     Systerel - added support for predicate variables
 *     Systerel - added support for mathematical extensions
 *******************************************************************************/
package org.eventb.core.ast;

/**
 * Implementation of a default rewriter that does not perform any rewriting.
 * Provides a basis for implementing simple rewriters by sub-classing.
 * <p>
 * Clients may extend this class.
 * </p> 
 *
 * @author Laurent Voisin
 * @since 1.0
 */
public class DefaultRewriter implements IFormulaRewriter2 {
	
	private final boolean autoFlattening;
	
	private int bindingDepth;
	
	/**
	 * @since 3.0
	 */
	public DefaultRewriter(boolean autoFlattening) {
		this.autoFlattening = autoFlattening;
	}

	@Override
	public final boolean autoFlatteningMode() {
		return autoFlattening;
	}

	@Override
	public final void enteringQuantifier(int nbOfDeclarations) {
		bindingDepth += nbOfDeclarations;
	}

	/**
	 * Returns the number of bound identifier declarations between the current
	 * position and the root of the formula which is rewritten.
	 * 
	 * @return the number of bound identifier declarations from the root
	 */
	protected final int getBindingDepth() {
		return bindingDepth;
	}
	
	@Override
	public final void leavingQuantifier(int nbOfDeclarations) {
		bindingDepth -= nbOfDeclarations;
	}

	@Override
	public Expression rewrite(AssociativeExpression expression) {
		return expression;
	}

	@Override
	public Predicate rewrite(AssociativePredicate predicate) {
		return predicate;
	}

	@Override
	public Expression rewrite(AtomicExpression expression) {
		return expression;
	}

	@Override
	public Expression rewrite(BinaryExpression expression) {
		return expression;
	}

	@Override
	public Predicate rewrite(BinaryPredicate predicate) {
		return predicate;
	}

	@Override
	public Expression rewrite(BoolExpression expression) {
		return expression;
	}

	@Override
	public Expression rewrite(BoundIdentifier identifier) {
		return identifier;
	}

	@Override
	public Expression rewrite(FreeIdentifier identifier) {
		return identifier;
	}

	@Override
	public Expression rewrite(IntegerLiteral literal) {
		return literal;
	}

	@Override
	public Predicate rewrite(LiteralPredicate predicate) {
		return predicate;
	}

	@Override
	public Predicate rewrite(MultiplePredicate predicate) {
		return predicate;
	}

	/**
	 * @since 1.2
	 */
	@Override
	public Predicate rewrite(PredicateVariable predVar) {
		return predVar;
	}

	@Override
	public Expression rewrite(QuantifiedExpression expression) {
		return expression;
	}

	@Override
	public Predicate rewrite(QuantifiedPredicate predicate) {
		return predicate;
	}

	@Override
	public Predicate rewrite(RelationalPredicate predicate) {
		return predicate;
	}

	@Override
	public Expression rewrite(SetExtension expression) {
		return expression;
	}

	@Override
	public Predicate rewrite(SimplePredicate predicate) {
		return predicate;
	}

	@Override
	public Expression rewrite(UnaryExpression expression) {
		return expression;
	}

	@Override
	public Predicate rewrite(UnaryPredicate predicate) {
		return predicate;
	}

	/**
	 * @since 2.0
	 */
	@Override
	public Expression rewrite(ExtendedExpression expression) {
		return expression;
	}

	/**
	 * @since 2.0
	 */
	@Override
	public Predicate rewrite(ExtendedPredicate predicate) {
		return predicate;
	}

}
