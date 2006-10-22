package org.eventb.core.seqprover.reasonerExtentionTests;

import java.util.Arrays;

import junit.framework.TestCase;

import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IReasonerRegistry;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.tests.TestLib;

/**
 * Unit tests for the reasoner registry
 * 
 * @see org.eventb.core.seqprover.IReasonerRegistry
 * 
 * @author Farhad Mehta
 * @author Laurent Voisin
 */
public class ReasonerRegistryTest extends TestCase {

	private static int count = 0;
	
	// Each call returns a new dummy id, not used before.
	private static String getDummyId() {
		return "dummy_id_" + (++ count);
	}
	
	private final IReasonerRegistry registry = SequentProver.getReasonerRegistry();

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
		assertTrue("Missing id " + id + " in list " + ids,
				Arrays.asList(ids).contains(id));
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
		assertFalse("Id " + id + " occurs in list " + ids,
				Arrays.asList(ids).contains(id));
	}
	
	/**
	 * Test method for {@link IReasonerRegistry#isRegistered(String)} and
	 * {@link IReasonerRegistry#getRegisteredIDs()}.
	 */
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
		registry.getReasonerName(idName);
		registry.getReasonerInstance(idInstance);
		assertKnown(TrueGoal.REASONER_ID);
		assertKnown(idName);
		assertKnown(idInstance);
		assertNotKnown(idOther);
	}

	/**
	 * Test method for {@link IReasonerRegistry#getReasonerInstance(String)}.
	 */
	public void testGetReasonerInstance() {
		IReasoner reasoner = registry.getReasonerInstance(TrueGoal.REASONER_ID);
		assertTrue(reasoner instanceof TrueGoal);
		
		reasoner = registry.getReasonerInstance(getDummyId());
		assertTrue(reasoner instanceof IReasoner);
	}

	/**
	 * Test method for {@link IReasonerRegistry#getReasonerName(String)}.
	 */
	public void testGetReasonerName() {
		assertTrue(registry.getReasonerName(TrueGoal.REASONER_ID).equals("⊤ goal"));
		assertNotNull(registry.getReasonerName(getDummyId()));
	}
	
	/**
	 * Ensures that a dummy reasoner always fails.
	 */
	public void testDummyReasoner() {
		String id = getDummyId();
		IReasoner dummyReasoner = registry.getReasonerInstance(id);
		assertEquals(dummyReasoner.getReasonerID(), id);
		IReasonerOutput reasonerOutput = dummyReasoner.apply(
				TestLib.genSeq(" 1=1 |- 1=1"),
				new EmptyInput(),
				null
		);
		assertTrue(reasonerOutput instanceof IReasonerFailure);
	}
	
	/**
	 * Test method for {@link IReasonerRegistry#isDummyReasoner(String)}.
	 */
	public void testIsDummyReasoner() {
		IReasoner dummyReasoner = registry.getReasonerInstance(getDummyId());
		assertTrue(registry.isDummyReasoner(dummyReasoner));
		
		IReasoner trueGoal = registry.getReasonerInstance(TrueGoal.REASONER_ID);
		assertFalse(registry.isDummyReasoner(trueGoal));
	}

}
