package org.eventb.core.seqprover.reasonerExtentionTests;

import java.util.Arrays;

import junit.framework.TestCase;

import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IReasonerRegistry;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.tests.TestLib;

public class ReasonerRegistryTest extends TestCase {

	private final IReasonerRegistry REGISTRY = SequentProver.getReasonerRegistry();

	private int count = 0;
	
	// Each call returns a new dummy id, not used before.
	private String getDummyId() {
		return "dummy_id_" + (++ count);
	}
	
	/*
	 * Test method for 'org.eventb.internal.core.seqprover.ReasonerRegistry.isPresent(String)'
	 */
	public void testIsPresent() {
		assertTrue(REGISTRY.isPresent(TrueGoal.REASONER_ID));
		assertFalse(REGISTRY.isPresent(getDummyId()));
	}

	/*
	 * Test method for 'org.eventb.internal.core.seqprover.ReasonerRegistry.getReasonerIDs()'
	 */
	public void testGetReasonerIDs() {
		final String[] ids = REGISTRY.getReasonerIDs();
		assertTrue(Arrays.asList(ids).contains(TrueGoal.REASONER_ID));
		assertFalse(Arrays.asList(ids).contains(getDummyId()));
	}

	/*
	 * Test method for 'org.eventb.internal.core.seqprover.ReasonerRegistry.getReasonerInstance(String)'
	 */
	public void testGetReasonerInstance() {
		IReasoner reasoner = REGISTRY.getReasonerInstance(TrueGoal.REASONER_ID);
		assertTrue(reasoner instanceof TrueGoal);
		
		reasoner = REGISTRY.getReasonerInstance(getDummyId());
		assertTrue(reasoner instanceof IReasoner);
	}

	/*
	 * Test method for 'org.eventb.internal.core.seqprover.ReasonerRegistry.getReasonerName(String)'
	 */
	public void testGetReasonerName() {
		assertTrue(REGISTRY.getReasonerName(TrueGoal.REASONER_ID).equals("⊤ goal"));
		assertNotNull(REGISTRY.getReasonerName(getDummyId()));
	}
	
	/*
	 * Ensures that a dummy reasone always fails.
	 */
	public void testDummyReasoner() {
		String id = getDummyId();
		IReasoner dummyReasoner = REGISTRY.getReasonerInstance(id);
		assertEquals(dummyReasoner.getReasonerID(), id);
		IReasonerOutput reasonerOutput = dummyReasoner.apply(
				TestLib.genSeq(" 1=1 |- 1=1"),
				new EmptyInput(),
				null
		);
		assertTrue(reasonerOutput instanceof IReasonerFailure);
	}
	
	/*
	 * Test method for 'org.eventb.internal.core.seqprover.ReasonerRegistry.isDummyReasoner(String)'
	 */
	public void testIsDummyReasoner() {
		IReasoner dummyReasoner = REGISTRY.getReasonerInstance(getDummyId());
		assertTrue(REGISTRY.isDummyReasoner(dummyReasoner));
		
		IReasoner trueGoal = REGISTRY.getReasonerInstance(TrueGoal.REASONER_ID);
		assertFalse(REGISTRY.isDummyReasoner(trueGoal));
	}

}
