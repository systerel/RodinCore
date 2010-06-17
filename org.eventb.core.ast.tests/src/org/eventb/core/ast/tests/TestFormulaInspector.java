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
package org.eventb.core.ast.tests;

import static java.util.Arrays.asList;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mIntegerLiteral;
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;

import java.util.List;

import junit.framework.TestCase;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IFormulaInspector;
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
 * Ensures that finding accumulators work as expected for addition of new
 * findings. The other aspects of formula inspection are actually tested in
 * class {@link TestSubFormulas}.
 */
public class TestFormulaInspector extends TestCase {

	private static final IFormulaInspector<String> inspector = new IFormulaInspector<String>() {

		public void inspect(AssociativeExpression expression,
				IAccumulator<String> accumulator) {
			// do nothing
		}

		public void inspect(AssociativePredicate predicate,
				IAccumulator<String> accumulator) {
			// do nothing
		}

		public void inspect(AtomicExpression expression,
				IAccumulator<String> accumulator) {
			// do nothing
		}

		public void inspect(BinaryExpression expression,
				IAccumulator<String> accumulator) {
			// do nothing
		}

		public void inspect(BinaryPredicate predicate,
				IAccumulator<String> accumulator) {
			// do nothing
		}

		public void inspect(BoolExpression expression,
				IAccumulator<String> accumulator) {
			// do nothing
		}

		public void inspect(BoundIdentDecl decl,
				IAccumulator<String> accumulator) {
			// do nothing
		}

		public void inspect(BoundIdentifier identifier,
				IAccumulator<String> accumulator) {
			accumulator.add(asList("a", "b", "c"));
		}

		public void inspect(FreeIdentifier identifier,
				IAccumulator<String> accumulator) {
			// do nothing
		}

		public void inspect(IntegerLiteral literal,
				IAccumulator<String> accumulator) {
			accumulator.add(new String[] { "1", "2" });
		}

		public void inspect(LiteralPredicate predicate,
				IAccumulator<String> accumulator) {
			accumulator.add("simple");
		}

		public void inspect(MultiplePredicate predicate,
				IAccumulator<String> accumulator) {
			// do nothing
		}

		public void inspect(PredicateVariable predicate,
				IAccumulator<String> accumulator) {
			// do nothing
		}

		public void inspect(QuantifiedExpression expression,
				IAccumulator<String> accumulator) {
			// do nothing
		}

		public void inspect(QuantifiedPredicate predicate,
				IAccumulator<String> accumulator) {
			// do nothing
		}

		public void inspect(RelationalPredicate predicate,
				IAccumulator<String> accumulator) {
			// do nothing
		}

		public void inspect(SetExtension expression,
				IAccumulator<String> accumulator) {
			// do nothing
		}

		public void inspect(SimplePredicate predicate,
				IAccumulator<String> accumulator) {
			// do nothing
		}

		public void inspect(UnaryExpression expression,
				IAccumulator<String> accumulator) {
			// do nothing
		}

		public void inspect(UnaryPredicate predicate,
				IAccumulator<String> accumulator) {
			// do nothing
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

}
