package org.eventb.core.seqprover.tacticExtentionTests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticRegistry;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.tacticExtentionTests.IdentityTactic.FailTactic;
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

	private static final String unrigisteredId = "unregistered";
	
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
		// Initially, contains only registered extensions
		assertKnown(IdentityTactic.TACTIC_ID);
		assertKnown(FailTactic.TACTIC_ID);
		assertNotKnown(unrigisteredId);		
	}

	/**
	 * Test method for {@link ITacticRegistry#getTacticInstance(String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetTacticInstance() {
		ITactic tactic = registry.getTacticInstance(IdentityTactic.TACTIC_ID);
		assertTrue(tactic instanceof IdentityTactic);
		
		tactic = registry.getTacticInstance(FailTactic.TACTIC_ID);
		assertTrue(tactic instanceof FailTactic);
		
		// Should throw an exception
		tactic = registry.getTacticInstance(unrigisteredId);
	}
	

	/**
	 * Test method for {@link ITacticRegistry#getTacticName(String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetTacticName() {
		assertTrue(registry.getTacticName(IdentityTactic.TACTIC_ID).equals("Identity Tactic"));
		assertTrue(registry.getTacticName(FailTactic.TACTIC_ID).equals("Fail Tactic"));
		// Should throw an exception
		registry.getTacticName(unrigisteredId);
	}
	
	/**
	 * Test method for {@link ITacticRegistry#getTacticDescription(String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetTacticDescription() {
		assertTrue(registry.getTacticDescription(IdentityTactic.TACTIC_ID).equals("This tactic does nothing but succeeds"));
		assertNotNull(registry.getTacticDescription(FailTactic.TACTIC_ID));
		assertTrue(registry.getTacticDescription(FailTactic.TACTIC_ID).equals(""));
		// Should throw an exception
		registry.getTacticDescription(unrigisteredId);
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
