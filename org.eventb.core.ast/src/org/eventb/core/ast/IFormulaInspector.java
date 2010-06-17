/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

/**
 * Common protocol for inspecting formulas. An inspector will be called on each
 * sub-formula of a given formula. For each sub-formula, the inspector can
 * report findings through an accumulator.
 * <p>
 * This interface contains one method for each of the sub-classes of
 * <code>Formula</code>, except assignments which are not covered.
 * </p>
 * <p>
 * Clients may implement this interface.
 * </p>
 * 
 * @param <F>
 *            type of the findings reported by this inspector
 * 
 * @see Formula#inspect(IFormulaInspector)
 * @see IAccumulator
 * 
 * @author Laurent Voisin
 * @since 1.3
 */
public interface IFormulaInspector<F> {

	/**
	 * Inspects the given associative expression and reports any finding to the
	 * given accumulator.
	 * 
	 * @param expression
	 *            associative expression to inspect
	 * @param accumulator
	 *            accumulator for reporting findings
	 */
	void inspect(AssociativeExpression expression, IAccumulator<F> accumulator);

	/**
	 * Inspects the given associative predicate and reports any finding to the
	 * given accumulator.
	 * 
	 * @param predicate
	 *            associative predicate to inspect
	 * @param accumulator
	 *            accumulator for reporting findings
	 */
	void inspect(AssociativePredicate predicate, IAccumulator<F> accumulator);

	/**
	 * Inspects the given atomic expression and reports any finding to the given
	 * accumulator.
	 * 
	 * @param expression
	 *            atomic expression to inspect
	 * @param accumulator
	 *            accumulator for reporting findings
	 */
	void inspect(AtomicExpression expression, IAccumulator<F> accumulator);

	/**
	 * Inspects the given binary expression and reports any finding to the given
	 * accumulator.
	 * 
	 * @param expression
	 *            binary expression to inspect
	 * @param accumulator
	 *            accumulator for reporting findings
	 */
	void inspect(BinaryExpression expression, IAccumulator<F> accumulator);

	/**
	 * Inspects the given bianry predicate and reports any finding to the given
	 * accumulator.
	 * 
	 * @param predicate
	 *            binary predicate to inspect
	 * @param accumulator
	 *            accumulator for reporting findings
	 */
	void inspect(BinaryPredicate predicate, IAccumulator<F> accumulator);

	/**
	 * Inspects the given boolean expression and reports any finding to the
	 * given accumulator.
	 * 
	 * @param expression
	 *            boolean expression to inspect
	 * @param accumulator
	 *            accumulator for reporting findings
	 */
	void inspect(BoolExpression expression, IAccumulator<F> accumulator);

	/**
	 * Inspects the given bound identifier declaration and reports any finding
	 * to the given accumulator.
	 * 
	 * @param decl
	 *            bound identifier declaration to inspect
	 * @param accumulator
	 *            accumulator for reporting findings
	 */
	void inspect(BoundIdentDecl decl, IAccumulator<F> accumulator);

	/**
	 * Inspects the given bound identifier and reports any finding to the given
	 * accumulator.
	 * 
	 * @param identifier
	 *            bound identifier to inspect
	 * @param accumulator
	 *            accumulator for reporting findings
	 */
	void inspect(BoundIdentifier identifier, IAccumulator<F> accumulator);

	/**
	 * Inspects the given free identifier and reports any finding to the given
	 * accumulator.
	 * 
	 * @param identifier
	 *            free identifier to inspect
	 * @param accumulator
	 *            accumulator for reporting findings
	 */
	void inspect(FreeIdentifier identifier, IAccumulator<F> accumulator);

	/**
	 * Inspects the given integer literal and reports any finding to the given
	 * accumulator.
	 * 
	 * @param literal
	 *            integer literal to inspect
	 * @param accumulator
	 *            accumulator for reporting findings
	 */
	void inspect(IntegerLiteral literal, IAccumulator<F> accumulator);

	/**
	 * Inspects the given literal predicate and reports any finding to the given
	 * accumulator.
	 * 
	 * @param predicate
	 *            literal predicate to inspect
	 * @param accumulator
	 *            accumulator for reporting findings
	 */
	void inspect(LiteralPredicate predicate, IAccumulator<F> accumulator);

	/**
	 * Inspects the given multiple predicate and reports any finding to the
	 * given accumulator.
	 * 
	 * @param predicate
	 *            multiple predicate to inspect
	 * @param accumulator
	 *            accumulator for reporting findings
	 */
	void inspect(MultiplePredicate predicate, IAccumulator<F> accumulator);

	/**
	 * Inspects the given predicate variable and reports any finding to the
	 * given accumulator.
	 * 
	 * @param predicate
	 *            predicate variable to inspect
	 * @param accumulator
	 *            accumulator for reporting findings
	 */
	void inspect(PredicateVariable predicate, IAccumulator<F> accumulator);

	/**
	 * Inspects the given quantified expression and reports any finding to the
	 * given accumulator.
	 * 
	 * @param expression
	 *            quantified expression to inspect
	 * @param accumulator
	 *            accumulator for reporting findings
	 */
	void inspect(QuantifiedExpression expression, IAccumulator<F> accumulator);

	/**
	 * Inspects the given quantified predicate and reports any finding to the
	 * given accumulator.
	 * 
	 * @param predicate
	 *            quantified predicate to inspect
	 * @param accumulator
	 *            accumulator for reporting findings
	 */
	void inspect(QuantifiedPredicate predicate, IAccumulator<F> accumulator);

	/**
	 * Inspects the given relational predicate and reports any finding to the
	 * given accumulator.
	 * 
	 * @param predicate
	 *            relational predicate to inspect
	 * @param accumulator
	 *            accumulator for reporting findings
	 */
	void inspect(RelationalPredicate predicate, IAccumulator<F> accumulator);

	/**
	 * Inspects the given set extension expression and reports any finding to
	 * the given accumulator.
	 * 
	 * @param expression
	 *            set extension expression to inspect
	 * @param accumulator
	 *            accumulator for reporting findings
	 */
	void inspect(SetExtension expression, IAccumulator<F> accumulator);

	/**
	 * Inspects the given simple predicate and reports any finding to the given
	 * accumulator.
	 * 
	 * @param predicate
	 *            simple predicate to inspect
	 * @param accumulator
	 *            accumulator for reporting findings
	 */
	void inspect(SimplePredicate predicate, IAccumulator<F> accumulator);

	/**
	 * Inspects the given unary expression and reports any finding to the given
	 * accumulator.
	 * 
	 * @param expression
	 *            unary expression to inspect
	 * @param accumulator
	 *            accumulator for reporting findings
	 */
	void inspect(UnaryExpression expression, IAccumulator<F> accumulator);

	/**
	 * Inspects the given unary predicate and reports any finding to the given
	 * accumulator.
	 * 
	 * @param predicate
	 *            unary predicate to inspect
	 * @param accumulator
	 *            accumulator for reporting findings
	 */
	void inspect(UnaryPredicate predicate, IAccumulator<F> accumulator);

}
