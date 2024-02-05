/*******************************************************************************
 * Copyright (c) 2007, 2024 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.junit.Assert.assertArrayEquals;

import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.AbstractManualInference;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractManualRewrites;
import org.junit.Test;

/**
 * @author htson
 *         <p>
 *         Abstract Unit tests for the Manual reasoner either
 *         {@link AbstractManualInference} or {@link AbstractManualRewrites}.
 *         This added the basis for testing applicable positions
 */
public abstract class AbstractManualReasonerTests extends AbstractReasonerTests {

	public AbstractManualReasonerTests() {
		super();
	}
	
	public AbstractManualReasonerTests(FormulaFactory ff) {
		super(ff);
	}

	/**
	 * Test the get applicable position for the reasoner.
	 * <p>
	 * 
	 * @param predicateImage
	 *            The string image of the predicate
	 * @param expected
	 *            The string images of expected applicable positions
	 * @see #getPositions(Predicate)
	 */
	protected void assertGetPositions(String predicateImage, String... expected) {
		Predicate predicate = TestLib.genPred(predicateImage, ff);
		List<IPosition> positions = getPositions(predicate);
		assertPositions("Applicable positions are incorrect for "
				+ predicateImage, expected, positions);
	}

	/**
	 * Test the get applicable position for the reasoner.
	 *
	 * @param typeEnv        type environment used to type check predicateImage
	 * @param predicateImage The string image of the predicate
	 * @param expected       The string images of expected applicable positions
	 * @see #getPositions(Predicate)
	 */
	protected void assertGetPositions(ITypeEnvironmentBuilder typeEnv, String predicateImage, String... expected) {
		Predicate predicate = TestLib.genPred(typeEnv, predicateImage);
		List<IPosition> positions = getPositions(predicate);
		assertPositions("Applicable positions are incorrect for " + predicateImage, expected, positions);
	}

	/**
	 * Tests for applicable positions
	 *
	 * @deprecated use {@link #assertGetPositions(String, String...)} in a new test method
	 */
	@Test
	@Deprecated
	public void testGetPositions() {
		String [] tests = getTestGetPositions();
		assert tests.length % 2 == 0;
		for (int i = 0; i < tests.length; i += 2) {
			// The input format was: empty string for no positions,
			// newline-separated values for multiple positions
			if (tests[i + 1].isEmpty()) {
				assertGetPositions(tests[i]);
			} else {
				assertGetPositions(tests[i], tests[i + 1].split("\n"));
			}
		}
	}

	private static final String[] TEST_GET_POSITIONS_EMPTY = new String[0];

	/**
	 * Return a list of pairs <predicate, applicable positions>.
	 *
	 * @return list of predicates and positions to test
	 * @deprecated create a new test method and call
	 *             {@link #assertGetPositions(String, String...)} directly
	 */
	@Deprecated
	protected String[] getTestGetPositions() {
		return TEST_GET_POSITIONS_EMPTY;
	}
	
	/**
	 * Client must implement this method to return the applicable positions for
	 * any predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return the applicable positions
	 */
	protected abstract List<IPosition> getPositions(Predicate predicate);

	/**
	 * An utility method used for testing applicable positions.
	 * <p>
	 * 
	 * @param message
	 *            a message
	 * @param expected
	 *            the expected string representation
	 * @param positions
	 *            a list of position ({@link IPosition}
	 */
	private void assertPositions(String message, String[] expected,
			List<IPosition> positions) {
		String[] actual = positions.stream().map(p -> p.isRoot() ? "ROOT" : p.toString()).toArray(String[]::new);
		assertArrayEquals(message, expected, actual);
	}

//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}
//	

}
