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

import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultInspector;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IFormulaInspector;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;

/**
 * Ensures that finding accumulators work as expected for addition of new
 * findings. The other aspects of formula inspection are actually tested in
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

}
