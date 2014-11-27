/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - moved part of tests to ReasonerDescTests
 *     Systerel - added coherence test of reasoner declaration
 *******************************************************************************/
package org.eventb.core.seqprover.reasonerExtentionTests;

import static java.util.Arrays.asList;
import static org.eventb.core.seqprover.tests.Util.TEST_PLUGIN_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerDesc;
import org.eventb.core.seqprover.IReasonerRegistry;
import org.eventb.core.seqprover.SequentProver;
import org.junit.Test;

/**
 * Unit tests for the reasoner registry
 * 
 * @see org.eventb.core.seqprover.IReasonerRegistry
 * 
 * @author Farhad Mehta
 * @author Laurent Voisin
 */
public class ReasonerRegistryTest {

	private static int count = 0;
	
	// Each call returns a new dummy id, not used before.
	public static String getDummyId() {
		return TEST_PLUGIN_ID + ".dummy_id_" + (++ count);
	}
	
	private static final IReasonerRegistry registry = SequentProver.getReasonerRegistry();

	/**
	 * Asserts that the given reasoner id has been registered. This is checked
	 * using both inquiry methods {@link IReasonerRegistry#isRegistered(String)}
	 * and {@link IReasonerRegistry#getRegisteredIDs()}.
	 * 
	 * @param id
	 *            the reasoner id to check
	 */
	private void assertKnown(String id) {
		assertTrue(registry.isRegistered(id));
		
		final String[] ids = registry.getRegisteredIDs();
		final List<String> idList = asList(ids);
		assertTrue("Missing id " + id + " in list " + idList,
				idList.contains(id));
	}
	
	/**
	 * Asserts that the given reasoner id has not been registered yet. This is
	 * checked using both inquiry methods
	 * {@link IReasonerRegistry#isRegistered(String)} and
	 * {@link IReasonerRegistry#getRegisteredIDs()}.
	 * 
	 * @param id
	 *            the reasoner id to check
	 */
	private void assertNotKnown(String id) {
		assertFalse(registry.isRegistered(id));
		
		final String[] ids = registry.getRegisteredIDs();
		final List<String> idList = asList(ids);
		assertFalse("Id " + id + " occurs in list " + idList,
				idList.contains(id));
	}
	
	/**
	 * Test method for {@link IReasonerRegistry#isRegistered(String)} and
	 * {@link IReasonerRegistry#getRegisteredIDs()}.
	 */
	@Test
	public void testRegisteredReasoners() {
		final String idName = getDummyId();
		final String idInstance = getDummyId();
		final String idOther = getDummyId();
		
		// Initially, contains only registered extensions
		assertKnown(TrueGoal.REASONER_ID);
		assertNotKnown(idName);
		assertNotKnown(idInstance);
		assertNotKnown(idOther);
		
		// After some registry requests, new ids appear
		registry.getReasonerDesc(idName);
		registry.getReasonerDesc(idInstance);
		assertKnown(TrueGoal.REASONER_ID);
		assertKnown(idName);
		assertKnown(idInstance);
		assertNotKnown(idOther);
	}

	/**
	 * Test method for
	 * {@link IReasonerRegistry#isDummyReasoner(IReasoner reasoner)}.
	 */
	@Test
	public void testIsDummyReasoner() {
		IReasoner dummyReasoner = registry.getReasonerDesc(getDummyId()).getInstance();
		assertTrue(registry.isDummyReasoner(dummyReasoner));
		
		IReasoner trueGoal = registry.getReasonerDesc(TrueGoal.REASONER_ID).getInstance();
		assertFalse(registry.isDummyReasoner(trueGoal));
	}

	/**
	 * Ensures that the erroneous id contributed through "plugin.xml" has been
	 * ignored.
	 */
	@Test
	public void testErroneousId() {
		String[] ids = registry.getRegisteredIDs();
		for (String id: ids) {
			assertFalse("Erroneous reasoner id should not be registered",
					id.contains("erroneous"));
		}
	}

	/**
	 * Tests globally that reasoners have an ID which corresponds to the one
	 * declared in their corresponding extension.
	 */
	@Test
	public void testAllReasonersValidIds() {
		String[] ids = registry.getRegisteredIDs();
		for (String id : ids) {
			final String expected = id;
			final IReasonerDesc desc = registry.getReasonerDesc(expected);
			assertNotNull(desc);
			final IReasoner reasoner = desc.getInstance();
			final String actual = reasoner.getReasonerID();
			//Checks if reasoner has the same ID as its contribution
			if (!isFromTest(actual)) {
				assertEquals(
						"Contribution should have the same ID as its desc: "
								+ desc.getId(), expected, actual);
				assertFalse("No dummy reasoner should appear!" + //
						" Reasoner: " + actual + " is dummy!", //
						registry.isDummyReasoner(reasoner));
			}
		}
	}

	private Boolean isFromTest(String reasonerID) {
		return reasonerID.startsWith(TEST_PLUGIN_ID);
	}

}
