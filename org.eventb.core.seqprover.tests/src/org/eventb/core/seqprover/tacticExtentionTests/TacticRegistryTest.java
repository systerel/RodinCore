package org.eventb.core.seqprover.tacticExtentionTests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticRegistry;
import org.eventb.core.seqprover.SequentProver;
import org.junit.Test;

/**
 * Unit tests for the tactic registry
 * 
 * @see org.eventb.core.seqprover.ITacticRegistry
 * 
 * @author Farhad Mehta
 * @author Laurent Voisin
 */
public class TacticRegistryTest {

	private static int count = 0;
	
	// Each call returns a new dummy id, not used before.
	private static String getDummyId() {
		return "dummy_id_" + (++ count);
	}
	
	private final ITacticRegistry registry = SequentProver.getTacticRegistry();

	/**
	 * Asserts that the given tactic id has been registered. This is checked
	 * using both inquiry methods {@link ITacticRegistry#isRegistered(String)}
	 * and {@link ITacticRegistry#getRegisteredIDs()}.
	 * 
	 * @param id
	 *            the tactic id to check
	 */
	private void assertKnown(String id) {
		assertTrue(registry.isRegistered(id));
		
		final String[] ids = registry.getRegisteredIDs();
		assertTrue("Missing id " + id + " in list " + ids,
				Arrays.asList(ids).contains(id));
	}
	
	/**
	 * Asserts that the given tactic id has not been registered yet. This is
	 * checked using both inquiry methods
	 * {@link ITacticRegistry#isRegistered(String)} and
	 * {@link ITacticRegistry#getRegisteredIDs()}.
	 * 
	 * @param id
	 *            the tactic id to check
	 */
	private void assertNotKnown(String id) {
		assertFalse(registry.isRegistered(id));
		
		final String[] ids = registry.getRegisteredIDs();
		assertFalse("Id " + id + " occurs in list " + ids,
				Arrays.asList(ids).contains(id));
	}
	
	/**
	 * Test method for {@link ITacticRegistry#isRegistered(String)} and
	 * {@link ITacticRegistry#getRegisteredIDs()}.
	 */
	@Test
	public void testRegisteredTactics() {
		final String idName = getDummyId();
		final String idInstance = getDummyId();
		final String idOther = getDummyId();
		
		// Initially, contains only registered extensions
		assertKnown(IdentityTactic.TACTIC_ID);
		assertNotKnown(idName);
		assertNotKnown(idInstance);
		assertNotKnown(idOther);
		
		// After some registry requests, new ids appear
		registry.getTacticName(idName);
		registry.getTacticInstance(idInstance);
		assertKnown(IdentityTactic.TACTIC_ID);
		assertKnown(idName);
		assertKnown(idInstance);
		assertNotKnown(idOther);
	}

	/**
	 * Test method for {@link ITacticRegistry#getTacticInstance(String)}.
	 */
	@Test
	public void testGetTacticInstance() {
		ITactic tactic = registry.getTacticInstance(IdentityTactic.TACTIC_ID);
		assertTrue(tactic instanceof IdentityTactic);
		
		tactic = registry.getTacticInstance(getDummyId());
		assertTrue(tactic instanceof ITactic);
	}

	/**
	 * Test method for {@link ITacticRegistry#getTacticName(String)}.
	 */
	@Test
	public void testGetTacticName() {
		assertTrue(registry.getTacticName(IdentityTactic.TACTIC_ID).equals("Identity Tactic"));
		assertNotNull(registry.getTacticName(getDummyId()));
	}
	
//	/**
//	 * Ensures that a dummy tactic always fails.
//	 */
//	@Test
//	public void testDummyTactic() {
//		String id = getDummyId();
//		ITactic dummyTactic = registry.getTacticInstance(id);
//		assertEquals(dummyTactic.getTacticID(), id);
//		ITacticOutput tacticOutput = dummyTactic.apply(
//				TestLib.genSeq(" 1=1 |- 1=1"),
//				new EmptyInput(),
//				null
//		);
//		assertTrue(tacticOutput instanceof ITacticFailure);
//	}
	
	/**
	 * Test method for {@link ITacticRegistry#isDummyTactic(String)}.
	 */
	@Test
	public void testIsDummyTactic() {
		ITactic dummyTactic = registry.getTacticInstance(getDummyId());
		assertTrue(registry.isDummyTactic(dummyTactic));
		
		ITactic trueGoal = registry.getTacticInstance(IdentityTactic.TACTIC_ID);
		assertFalse(registry.isDummyTactic(trueGoal));
	}

	/**
	 * Ensures that the erroneous id contributed through "plugin.xml" has been
	 * ignored.
	 */
	@Test
	public void testErroneousId() {
		String[] ids = registry.getRegisteredIDs();
		for (String id: ids) {
			assertFalse("Erroneous tactic id should not be registered",
					id.contains("erroneous"));
		}
	}
	
}
