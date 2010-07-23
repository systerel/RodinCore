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
package org.eventb.internal.core.ast;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IFormulaFilter;
import org.eventb.core.ast.IFormulaFilter2;
import org.eventb.core.ast.IFormulaInspector;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

/**
 * This class encapsulates the formula filter used to retrieve the positions of
 * a sub-formula that satisfies the given filter criterion. It ensures backward
 * compatibility with {@link Formula#getPositions(IFormulaFilter)} in the
 * initial AST API.
 */
public class FilteringInspector implements IFormulaInspector<IPosition> {

	private final IFormulaFilter filter;

	public FilteringInspector(IFormulaFilter filter) {
		this.filter = filter;
	}

	private static void addCurrentPosition(IAccumulator<IPosition> acc) {
		acc.add(acc.getCurrentPosition());
	}

	@Override
	public void inspect(AssociativeExpression expression, IAccumulator<IPosition> acc) {
		if (filter.select(expression)) {
			addCurrentPosition(acc);
		}
	}

	@Override
	public void inspect(AssociativePredicate predicate, IAccumulator<IPosition> acc) {
		if (filter.select(predicate)) {
			addCurrentPosition(acc);
		}
	}

	@Override
	public void inspect(AtomicExpression expression, IAccumulator<IPosition> acc) {
		if (filter.select(expression)) {
			addCurrentPosition(acc);
		}
	}

	@Override
	public void inspect(BinaryExpression expression, IAccumulator<IPosition> acc) {
		if (filter.select(expression)) {
			addCurrentPosition(acc);
		}
	}

	@Override
	public void inspect(BinaryPredicate predicate, IAccumulator<IPosition> acc) {
		if (filter.select(predicate)) {
			addCurrentPosition(acc);
		}
	}

	@Override
	public void inspect(BoolExpression expression, IAccumulator<IPosition> acc) {
		if (filter.select(expression)) {
			addCurrentPosition(acc);
		}
	}

	@Override
	public void inspect(BoundIdentDecl decl, IAccumulator<IPosition> acc) {
		if (filter.select(decl)) {
			addCurrentPosition(acc);
		}
	}

	@Override
	public void inspect(BoundIdentifier identifier, IAccumulator<IPosition> acc) {
		if (filter.select(identifier)) {
			addCurrentPosition(acc);
		}
	}

	@Override
	public void inspect(ExtendedExpression expr, IAccumulator<IPosition> acc) {
		if (filter.select(expr)) {
			addCurrentPosition(acc);
		}
	}

	@Override
	public void inspect(ExtendedPredicate predicate, IAccumulator<IPosition> acc) {
		if (filter.select(predicate)) {
			addCurrentPosition(acc);
		}
	}

	@Override
	public void inspect(FreeIdentifier identifier, IAccumulator<IPosition> acc) {
		if (filter.select(identifier)) {
			addCurrentPosition(acc);
		}
	}

	@Override
	public void inspect(IntegerLiteral literal, IAccumulator<IPosition> acc) {
		if (filter.select(literal)) {
			addCurrentPosition(acc);
		}
	}

	@Override
	public void inspect(LiteralPredicate predicate, IAccumulator<IPosition> acc) {
		if (filter.select(predicate)) {
			addCurrentPosition(acc);
		}
	}

	@Override
	public void inspect(MultiplePredicate predicate, IAccumulator<IPosition> acc) {
		if (filter.select(predicate)) {
			addCurrentPosition(acc);
		}
	}

	@Override
	public void inspect(PredicateVariable predicate, IAccumulator<IPosition> acc) {
		if (filter instanceof IFormulaFilter2) {
			final IFormulaFilter2 filter2 = (IFormulaFilter2) filter;
			if (filter2.select(predicate)) {
				addCurrentPosition(acc);
			}
		} else {
			throw new IllegalArgumentException(
					"The given filter does not support predicate variables");
		}
	}

	@Override
	public void inspect(QuantifiedExpression expression, IAccumulator<IPosition> acc) {
		if (filter.select(expression)) {
			addCurrentPosition(acc);
		}
	}

	@Override
	public void inspect(QuantifiedPredicate predicate, IAccumulator<IPosition> acc) {
		if (filter.select(predicate)) {
			addCurrentPosition(acc);
		}
	}

	@Override
	public void inspect(RelationalPredicate predicate, IAccumulator<IPosition> acc) {
		if (filter.select(predicate)) {
			addCurrentPosition(acc);
		}
	}

	@Override
	public void inspect(SetExtension expression, IAccumulator<IPosition> acc) {
		if (filter.select(expression)) {
			addCurrentPosition(acc);
		}
	}

	@Override
	public void inspect(SimplePredicate predicate, IAccumulator<IPosition> acc) {
		if (filter.select(predicate)) {
			addCurrentPosition(acc);
		}
	}

	@Override
	public void inspect(UnaryExpression expression, IAccumulator<IPosition> acc) {
		if (filter.select(expression)) {
			addCurrentPosition(acc);
		}
	}

	@Override
	public void inspect(UnaryPredicate predicate, IAccumulator<IPosition> acc) {
		if (filter.select(predicate)) {
			addCurrentPosition(acc);
		}
	}

}
