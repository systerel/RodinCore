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
 * Default implementation of a formula inspector that finds nothing. Provides a
 * basis for implementing inspectors by sub-classing.
 * <p>
 * Clients may extend this class.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 2.1
 */
public class DefaultInspector<F> implements IFormulaInspector<F> {

	@Override
	public void inspect(AssociativeExpression expression,
			IAccumulator<F> accumulator) {
		// find nothing by default
	}

	@Override
	public void inspect(AssociativePredicate predicate,
			IAccumulator<F> accumulator) {
		// find nothing by default
	}

	@Override
	public void inspect(AtomicExpression expression, IAccumulator<F> accumulator) {
		// find nothing by default
	}

	@Override
	public void inspect(BinaryExpression expression, IAccumulator<F> accumulator) {
		// find nothing by default
	}

	@Override
	public void inspect(BinaryPredicate predicate, IAccumulator<F> accumulator) {
		// find nothing by default
	}

	@Override
	public void inspect(BoolExpression expression, IAccumulator<F> accumulator) {
		// find nothing by default
	}

	@Override
	public void inspect(BoundIdentDecl decl, IAccumulator<F> accumulator) {
		// find nothing by default
	}

	@Override
	public void inspect(BoundIdentifier identifier, IAccumulator<F> accumulator) {
		// find nothing by default
	}

	@Override
	public void inspect(ExtendedExpression expression,
			IAccumulator<F> accumulator) {
		// find nothing by default
	}

	@Override
	public void inspect(ExtendedPredicate predicate, IAccumulator<F> accumulator) {
		// find nothing by default
	}

	@Override
	public void inspect(FreeIdentifier identifier, IAccumulator<F> accumulator) {
		// find nothing by default
	}

	@Override
	public void inspect(IntegerLiteral literal, IAccumulator<F> accumulator) {
		// find nothing by default
	}

	@Override
	public void inspect(LiteralPredicate predicate, IAccumulator<F> accumulator) {
		// find nothing by default
	}

	@Override
	public void inspect(MultiplePredicate predicate, IAccumulator<F> accumulator) {
		// find nothing by default
	}

	@Override
	public void inspect(PredicateVariable predicate, IAccumulator<F> accumulator) {
		// find nothing by default
	}

	@Override
	public void inspect(QuantifiedExpression expression,
			IAccumulator<F> accumulator) {
		// find nothing by default
	}

	@Override
	public void inspect(QuantifiedPredicate predicate,
			IAccumulator<F> accumulator) {
		// find nothing by default
	}

	@Override
	public void inspect(RelationalPredicate predicate,
			IAccumulator<F> accumulator) {
		// find nothing by default
	}

	@Override
	public void inspect(SetExtension expression, IAccumulator<F> accumulator) {
		// find nothing by default
	}

	@Override
	public void inspect(SimplePredicate predicate, IAccumulator<F> accumulator) {
		// find nothing by default
	}

	@Override
	public void inspect(UnaryExpression expression, IAccumulator<F> accumulator) {
		// find nothing by default
	}

	@Override
	public void inspect(UnaryPredicate predicate, IAccumulator<F> accumulator) {
		// find nothing by default
	}

}
