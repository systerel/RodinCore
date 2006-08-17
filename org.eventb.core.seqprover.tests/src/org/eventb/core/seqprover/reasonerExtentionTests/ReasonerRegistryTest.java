package org.eventb.core.seqprover.reasonerExtentionTests;

import org.eventb.core.seqprover.IReasonerRegistry;
import org.eventb.core.seqprover.SequentProver;

import junit.framework.TestCase;

public class ReasonerRegistryTest extends TestCase {

	private static final IReasonerRegistry REASONER_REGISTRY = SequentProver.getReasonerRegistry();

	/*
	 * Test method for 'org.eventb.internal.core.seqprover.ReasonerRegistry.isPresent(String)'
	 */
	public void testIsPresent() {
		assertTrue(REASONER_REGISTRY.isPresent(TrueGoal.REASONER_ID));
		assertFalse(REASONER_REGISTRY.isPresent(TrueGoal.REASONER_ID+"other"));
	}

	/*
	 * Test method for 'org.eventb.internal.core.seqprover.ReasonerRegistry.getReasonerIDs()'
	 */
	public void testGetReasonerIDs() {
		assertTrue(REASONER_REGISTRY.getReasonerIDs().contains(TrueGoal.REASONER_ID));
		assertFalse(REASONER_REGISTRY.getReasonerIDs().contains(TrueGoal.REASONER_ID+"other"));
		
	}

	/*
	 * Test method for 'org.eventb.internal.core.seqprover.ReasonerRegistry.getReasonerInstance(String)'
	 */
	public void testGetReasonerInstance() {
		assertTrue(REASONER_REGISTRY.getReasonerInstance(TrueGoal.REASONER_ID) instanceof TrueGoal);
		assertNull(REASONER_REGISTRY.getReasonerInstance(TrueGoal.REASONER_ID+"other"));
	}

	/*
	 * Test method for 'org.eventb.internal.core.seqprover.ReasonerRegistry.getReasonerName(String)'
	 */
	public void testGetReasonerName() {
		assertTrue(REASONER_REGISTRY.getReasonerName(TrueGoal.REASONER_ID).equals("⊤ goal"));
		assertNull(REASONER_REGISTRY.getReasonerName(TrueGoal.REASONER_ID+"other"));
	}

}
