/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import static org.junit.Assert.fail;

import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.core.seqprover.tests.Util;
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
	 *            The string image of expected applicable positions
	 * @see #getPositions(Predicate)
	 */
	protected void testGetPosition(String predicateImage, String expected) {
		Predicate predicate = TestLib.genPred(predicateImage, ff);
		List<IPosition> positions = getPositions(predicate);
		assertPositions("Applicable positions are incorrect for "
				+ predicateImage, expected, positions);
	}

	/**
	 * Tests for applicable positions
	 */
	@Test
	public void testGetPositions() {
		String [] tests = getTestGetPositions();
		assert tests.length % 2 == 0;
		for (int i = 0; i < tests.length; i += 2) {
			testGetPosition(tests[i], tests[i + 1]);
		}
	}

	protected abstract String[] getTestGetPositions();
	
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
	private void assertPositions(String message, String expected,
			List<IPosition> positions) {
		StringBuilder builder = new StringBuilder();
		String sep = "";
		for (IPosition position : positions) {
			builder.append(sep);
			if (position.isRoot()) {
				builder.append("ROOT");
			} else {
				builder.append(position);
			}
			sep = "\n";
		}
		String actual = builder.toString();
		if (!expected.equals(actual)) {
			System.out.println(Util.displayString(actual));
			fail(message + ":\n" + actual);
		}
	}

//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}
//	

}
