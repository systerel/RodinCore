/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language v2
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
public class DefaultRewriter implements IFormulaRewriter {
	
	private final boolean autoFlattening;
	
	protected final FormulaFactory ff;
	
	private int bindingDepth;
	
	public DefaultRewriter(boolean autoFlattening, FormulaFactory ff) {
		this.autoFlattening = autoFlattening;
		this.ff = ff;
	}

	public final boolean autoFlatteningMode() {
		return autoFlattening;
	}

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
	
	public final FormulaFactory getFactory() {
		return ff;
	}

	public final void leavingQuantifier(int nbOfDeclarations) {
		bindingDepth -= nbOfDeclarations;
	}

	public Expression rewrite(AssociativeExpression expression) {
		return expression;
	}

	public Predicate rewrite(AssociativePredicate predicate) {
		return predicate;
	}

	public Expression rewrite(AtomicExpression expression) {
		return expression;
	}

	public Expression rewrite(BinaryExpression expression) {
		return expression;
	}

	public Predicate rewrite(BinaryPredicate predicate) {
		return predicate;
	}

	public Expression rewrite(BoolExpression expression) {
		return expression;
	}

	public Expression rewrite(BoundIdentifier identifier) {
		return identifier;
	}

	public Expression rewrite(FreeIdentifier identifier) {
		return identifier;
	}

	public Expression rewrite(IntegerLiteral literal) {
		return literal;
	}

	public Predicate rewrite(LiteralPredicate predicate) {
		return predicate;
	}

	public Predicate rewrite(MultiplePredicate predicate) {
		return predicate;
	}

	public Expression rewrite(QuantifiedExpression expression) {
		return expression;
	}

	public Predicate rewrite(QuantifiedPredicate predicate) {
		return predicate;
	}

	public Predicate rewrite(RelationalPredicate predicate) {
		return predicate;
	}

	public Expression rewrite(SetExtension expression) {
		return expression;
	}

	public Predicate rewrite(SimplePredicate predicate) {
		return predicate;
	}

	public Expression rewrite(UnaryExpression expression) {
		return expression;
	}

	public Predicate rewrite(UnaryPredicate predicate) {
		return predicate;
	}

}
