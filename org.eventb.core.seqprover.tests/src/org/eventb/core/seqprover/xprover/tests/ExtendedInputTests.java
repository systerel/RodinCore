/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.xprover.tests;

import static org.junit.Assert.assertEquals;

import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.SerializeException;
import org.junit.Test;

/**
 * Tests with a reasoner using an extended input.
 * 
 * @author Laurent Voisin
 */
public class ExtendedInputTests extends XProverInputTests {

	private static IProverSequent sequent = mSequent(mList(px), mList(), px);

	private static void assertNoError(ExtendedInput input, boolean restricted,
			long delay, String param) throws Exception {
		
		assertNoError(input, restricted, delay);
		assertEquals(param, input.param);
	}

	private static void assertEqualsInput(ExtendedInput expected, ExtendedInput actual) {
		assertEquals(expected.restricted, actual.restricted);
		assertEquals(expected.timeOutDelay, actual.timeOutDelay);
		assertEquals(expected.param, actual.param);
	}

	private static void assertSerialization(ExtendedInputReasoner reasoner,
			ExtendedInput input) throws SerializeException {
		Serializer s = new Serializer();
		reasoner.serializeInput(input, s);
		ExtendedInput actual = reasoner.deserializeInput(s);
		assertEqualsInput(input, actual);
	}

	/**
	 * Ensures that the extended input reasoner can be run with a success
	 * param.
	 */
	@Test
	public final void success() throws Exception {
		ExtendedInput input = new ExtendedInput(true, 0, "success");
		assertNoError(input, true, 0, "success");
		ExtendedInputReasoner reasoner = new ExtendedInputReasoner();
		assertSuccess(reasoner.apply(sequent, input, null), mList(), px);
		assertSerialization(reasoner, input);
	}

	/**
	 * Ensures that the extended input reasoner can be run with a failure
	 * param.
	 */
	@Test
	public final void failure() throws Exception {
		ExtendedInput input = new ExtendedInput(true, 0, "failure");
		assertNoError(input, true, 0, "failure");
		ExtendedInputReasoner reasoner = new ExtendedInputReasoner();
		assertFailure(reasoner.apply(sequent, input, null), null);
		assertSerialization(reasoner, input);
	}

	/**
	 * Ensures that the extended input reports an error for a wrong timeout, and
	 * the extended input reasoner thus fails.
	 */
	@Test
	public final void timeoutError() throws Exception {
		ExtendedInput input = new ExtendedInput(true, -1, "success");
		assertError(input);
		ExtendedInputReasoner reasoner = new ExtendedInputReasoner();
		assertFailure(reasoner.apply(sequent, input, null), null);
	}

	/**
	 * Ensures that the extended input reports an error for a wrong parameter, and
	 * the extended input reasoner thus fails.
	 */
	@Test
	public final void paramError() throws Exception {
		ExtendedInput input = new ExtendedInput(true, 0, "illegal");
		assertError(input);
		ExtendedInputReasoner reasoner = new ExtendedInputReasoner();
		assertFailure(reasoner.apply(sequent, input, null), null);
	}

}
