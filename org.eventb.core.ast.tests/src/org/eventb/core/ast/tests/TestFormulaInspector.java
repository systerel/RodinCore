/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static java.util.Arrays.asList;
import static org.eventb.core.ast.FormulaFactory.makePosition;
import static org.eventb.core.ast.tests.FastFactory.mAssociativePredicate;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mIntegerLiteral;
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultInspector;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IFormulaInspector;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

/**
 * Ensures that finding accumulators work as expected for addition of new
 * findings. Also ensures that skipping nodes during inspections is properly
 * implemented. The other aspects of formula inspection are actually tested in
 * class {@link TestSubFormulas}.
 */
public class TestFormulaInspector extends TestCase {

	private static final IFormulaInspector<String> inspector = new DefaultInspector<String>() {

		@Override
		public void inspect(BoundIdentifier identifier,
				IAccumulator<String> accumulator) {
			accumulator.add(asList("a", "b", "c"));
		}

		@Override
		public void inspect(IntegerLiteral literal,
				IAccumulator<String> accumulator) {
			accumulator.add(new String[] { "1", "2" });
		}

		@Override
		public void inspect(LiteralPredicate predicate,
				IAccumulator<String> accumulator) {
			accumulator.add("simple");
		}

	};

	private static void assertFindings(Formula<?> formula, String... expected) {
		final List<String> actual = formula.inspect(inspector);
		assertEquals(asList(expected), actual);
	}

	/**
	 * Ensures that the add method for one finding works.
	 */
	public void testSimpleAdd() throws Exception {
		assertFindings(mLiteralPredicate(), "simple");
	}

	/**
	 * Ensures that the add method for an array of findings works.
	 */
	public void testArrayAdd() throws Exception {
		assertFindings(mIntegerLiteral(), "1", "2");
	}

	/**
	 * Ensures that the add method for a list of findings works.
	 */
	public void testListAdd() throws Exception {
		assertFindings(mBoundIdentifier(0), "a", "b", "c");
	}

	private static class Skipper implements IFormulaInspector<IPosition> {

		private final IPosition skipChildrenPos;
		private final IPosition skipAllPos;

		public Skipper(IPosition skipChildrenPos, IPosition skipAllPos) {
			this.skipChildrenPos = skipChildrenPos;
			this.skipAllPos = skipAllPos;
		}

		// Common inspection method
		private void doInspect(IAccumulator<IPosition> accumulator) {
			final IPosition pos = accumulator.getCurrentPosition();
			accumulator.add(pos);
			if (pos.equals(skipChildrenPos)) {
				accumulator.skipChildren();
			}
			if (pos.equals(skipAllPos)) {
				accumulator.skipAll();
			}
		}

		@Override
		public void inspect(AssociativeExpression expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(AssociativePredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(AtomicExpression expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(BinaryExpression expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(BinaryPredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(BoolExpression expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(BoundIdentDecl decl,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(BoundIdentifier identifier,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(ExtendedExpression expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(ExtendedPredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(FreeIdentifier identifier,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(IntegerLiteral literal,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(LiteralPredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(MultiplePredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(PredicateVariable predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(QuantifiedExpression expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(QuantifiedPredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(RelationalPredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(SetExtension expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(SimplePredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(UnaryExpression expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(UnaryPredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

	}

	private static class Trace {
		private final List<IPosition> positions;

		public Trace(List<IPosition> positions) {
			this.positions = new LinkedList<IPosition>(positions);
		}

		public void removeChildren(IPosition pos) {
			if (pos.isRoot()) {
				positions.clear();
				positions.add(pos);
				return;
			}
			final IPosition nextSibling = pos.getNextSibling();
			final Iterator<IPosition> iter = positions.iterator();
			while (iter.hasNext()) {
				final IPosition cur = iter.next();
				if (pos.compareTo(cur) < 0 && cur.compareTo(nextSibling) < 0) {
					iter.remove();
				}
			}
		}

		public void removeAfter(IPosition pos) {
			final Iterator<IPosition> iter = positions.iterator();
			while (iter.hasNext()) {
				final IPosition cur = iter.next();
				if (pos.compareTo(cur) < 0) {
					iter.remove();
				}
			}
		}

		public void assertEquals(List<IPosition> actual) {
			Assert.assertEquals(positions, actual);
		}
	}

	private static void assertTrace(Formula<?> formula, String posImage) {
		final Skipper noSkip = new Skipper(null, null);
		final List<IPosition> fullTrace = formula.inspect(noSkip);
		final IPosition pos = makePosition(posImage);

		assertTrace(formula, fullTrace, pos, null);
		assertTrace(formula, fullTrace, null, pos);
		assertTrace(formula, fullTrace, pos, pos);
	}

	private static void assertTrace(Formula<?> formula,
			List<IPosition> fullTrace, IPosition skipChildrenPos,
			IPosition skipAllPos) {
		final Trace expected = new Trace(fullTrace);
		if (skipChildrenPos != null) {
			expected.removeChildren(skipChildrenPos);
		}
		if (skipAllPos != null) {
			expected.removeAfter(skipAllPos);
		}
		final Skipper skipper = new Skipper(skipChildrenPos, skipAllPos);
		final List<IPosition> actual = formula.inspect(skipper);
		expected.assertEquals(actual);
	}

	private static Predicate pA = mLiteralPredicate();
	private static Predicate pB = mLiteralPredicate();
	private static Predicate pC = mLiteralPredicate();
	private static Predicate pD = mLiteralPredicate();

	/**
	 * Ensures that skip methods work as expected on associative predicates.
	 */
	public void testAssociativePredicate() throws Exception {
		final AssociativePredicate child = mAssociativePredicate(pB, pC);
		final Predicate predicate = mAssociativePredicate(pA, child, pD);
		assertTrace(predicate, "1");
	}
}
