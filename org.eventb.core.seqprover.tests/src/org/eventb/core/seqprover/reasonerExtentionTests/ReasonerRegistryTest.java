package org.eventb.core.seqprover.reasonerExtentionTests;

import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IReasonerRegistry;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.tests.TestLib;

import com.sun.corba.se.spi.ior.MakeImmutable;

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
	
	/*
	 * Test method for 'org.eventb.internal.core.seqprover.ReasonerRegistry.makeDummyReasoner(String)'
	 */
	public void testMakeDummyReasoner() {
		IReasoner dummyReasoner = REASONER_REGISTRY.makeDummyReasoner("dummy");
		assertEquals(dummyReasoner.getReasonerID(),"dummy");
		IReasonerOutput reasonerOutput = dummyReasoner.apply(TestLib.genSeq(" 1=1 |- 1=1"),new EmptyInput(),null);
		assertTrue(reasonerOutput instanceof IReasonerFailure);
	}
	
	/*
	 * Test method for 'org.eventb.internal.core.seqprover.ReasonerRegistry.isDummyReasoner(String)'
	 */
	public void testIsDummyReasoner() {
		IReasoner dummyReasoner = REASONER_REGISTRY.makeDummyReasoner("dummy");
		assertTrue(REASONER_REGISTRY.isDummyReasoner(dummyReasoner));
		assertFalse(REASONER_REGISTRY.isDummyReasoner(REASONER_REGISTRY.getReasonerInstance(TrueGoal.REASONER_ID)));
	}

}
