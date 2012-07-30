/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.seqprover.xprover.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.xprover.XProverInput;
import org.eventb.core.seqprover.xprover.XProverReasoner;
import org.junit.Test;

/**
 * Unit tests for class XProverInput.
 * 
 * @author Laurent Voisin
 */
@SuppressWarnings("deprecation")
public class XProverInputTests extends XProverTests {
	
	static class Serializer implements IReasonerInputReader, IReasonerInputWriter {
		
		HashMap<String, String> stringMap = new HashMap<String, String>();

		public FormulaFactory getFormulaFactory() {
			return FormulaFactory.getDefault();
		}
		
		public IAntecedent[] getAntecedents() {
			// Ignore
			return null;
		}

		public int getConfidence() {
			// Ignore
			return 0;
		}

		public String getDisplayName() {
			// Ignore
			return null;
		}

		public Expression[] getExpressions(String key) throws SerializeException {
			// Ignore
			return null;
		}

		public Predicate getGoal() {
			// Ignore
			return null;
		}

		public Set<Predicate> getNeededHyps() {
			// Ignore
			return null;
		}

		public Predicate[] getPredicates(String key) throws SerializeException {
			// Ignore
			return null;
		}

		public String getString(String key) throws SerializeException {
			return stringMap.get(key);
		}

		public void putExpressions(String key, Expression... expressions) throws SerializeException {
			// Ignore
		}

		public void putPredicates(String key, Predicate... predicates) throws SerializeException {
			// Ignore
		}

		public void putString(String key, String string) throws SerializeException {
			stringMap.put(key, string);
		}
		
	}
	
	static void assertNoError(XProverInput input, boolean restricted,
			long delay) throws Exception {
		assertEquals(restricted, input.restricted);
		assertEquals(delay, input.timeOutDelay);
		assertFalse(input.hasError());
		assertNull(input.getError());

		// Also check that it can be serialized and deserialized
		final Serializer serializer = new Serializer();
		final XProverReasoner reasoner = new TestReasoner();
		reasoner.serializeInput(input, serializer);
		XProverInput actual = (XProverInput) reasoner
				.deserializeInput(serializer);
		assertEqualsInput(input, actual);
	}

	static void assertError(XProverInput input) {
		assertTrue(input.hasError());
		assertNotNull(input.getError());
	}

	private static void assertEqualsInput(XProverInput expected, XProverInput actual) {
		assertEquals(expected.restricted, actual.restricted);
		assertEquals(expected.timeOutDelay, actual.timeOutDelay);
	}

	/**
	 * Ensures that a reasoner input can be created for restricted and a null timeout.
	 */
	@Test
	public final void createR0() throws Exception {
		XProverInput input = new XProverInput(true, 0);
		assertNoError(input, true, 0);
	}

	/**
	 * Ensures that a reasoner input can be created for restricted and a positive timeout.
	 */
	@Test
	public final void createR10() throws Exception {
		XProverInput input = new XProverInput(true, 10);
		assertNoError(input, true, 10);
	}

	/**
	 * Ensures that a reasoner input can be created for restricted and a negative timeout.
	 */
	@Test
	public final void createR_10() throws Exception {
		XProverInput input = new XProverInput(true, -10);
		assertError(input);
	}

	/**
	 * Ensures that a reasoner input can be created for non-restricted and a null timeout.
	 */
	@Test
	public final void createN0() throws Exception {
		XProverInput input = new XProverInput(false, 0);
		assertNoError(input, false, 0);
	}

	/**
	 * Ensures that a reasoner input can be created for non-restricted and a positive timeout.
	 */
	@Test
	public final void createN10() throws Exception {
		XProverInput input = new XProverInput(false, 10);
		assertNoError(input, false, 10);
	}

	/**
	 * Ensures that a reasoner input can be created for non-restricted and a negative timeout.
	 */
	@Test
	public final void createN_10() throws Exception {
		XProverInput input = new XProverInput(false, -10);
		assertError(input);
	}

}
