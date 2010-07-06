package org.eventb.core.seqprover.autoTacticExtentionTests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.autoTacticExtentionTests.IdentityTactic.FailTactic;
import org.junit.Test;

/**
 * Unit tests for the tactic registry
 * 
 * @see org.eventb.core.seqprover.IAutoTacticRegistry
 * 
 * @author Farhad Mehta
 * @author Laurent Voisin
 */
public class AutoTacticRegistryTest {

	private static final String unrigisteredId = "unregistered";
	
	private final IAutoTacticRegistry registry = SequentProver.getAutoTacticRegistry();

	/**
	 * Asserts that the given tactic id has been registered. This is checked
	 * using both inquiry methods {@link IAutoTacticRegistry#isRegistered(String)}
	 * and {@link IAutoTacticRegistry#getRegisteredIDs()}.
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
	 * {@link IAutoTacticRegistry#isRegistered(String)} and
	 * {@link IAutoTacticRegistry#getRegisteredIDs()}.
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
	 * Test method for {@link IAutoTacticRegistry#isRegistered(String)} and
	 * {@link IAutoTacticRegistry#getRegisteredIDs()}.
	 */
	@Test
	public void testRegisteredTactics() {		
		// Initially, contains only registered extensions
		assertKnown(IdentityTactic.TACTIC_ID);
		assertKnown(FailTactic.TACTIC_ID);
		assertNotKnown(unrigisteredId);		
	}

	/**
	 * Test method for {@link IAutoTacticRegistry#getTacticDescriptor(String)}.
	 * 
	 * Registered IDs
	 */
	@Test
	public void testGetTacticDescriptorRegistered() {
		ITacticDescriptor tacticDescIdentity = registry.getTacticDescriptor(IdentityTactic.TACTIC_ID);
		assertNotNull(tacticDescIdentity);
				
		ITacticDescriptor tacticDescFail = registry.getTacticDescriptor(FailTactic.TACTIC_ID);
		assertNotNull(tacticDescFail);
	}
	
	/**
	 * Test method for {@link IAutoTacticRegistry#getTacticDescriptor(String)}.
	 * 
	 * Unregistered ID
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetTacticDescriptorUnregistered() {
		// Should throw an exception
		registry.getTacticDescriptor(unrigisteredId);
	}
	
	/**
	 * Test method for {@link ITacticDescriptor#getTacticID()}.
	 * 
	 */
	@Test
	public void testGetTacticDecsrID() {
		ITacticDescriptor tacticDescIdentity = registry.getTacticDescriptor(IdentityTactic.TACTIC_ID);				
		ITacticDescriptor tacticDescFail = registry.getTacticDescriptor(FailTactic.TACTIC_ID);

		String tacticID;
		
		tacticID = tacticDescIdentity.getTacticID();
		assertTrue(tacticID.equals(IdentityTactic.TACTIC_ID));

		tacticID = tacticDescFail.getTacticID();
		assertTrue(tacticID.equals(FailTactic.TACTIC_ID));
	}

	/**
	 * Test method for {@link ITacticDescriptor#getTacticName()}.
	 * 
	 */
	@Test
	public void testGetTacticDecsrName() {
		ITacticDescriptor tacticDescIdentity = registry.getTacticDescriptor(IdentityTactic.TACTIC_ID);				
		ITacticDescriptor tacticDescFail = registry.getTacticDescriptor(FailTactic.TACTIC_ID);

		String tacticName;
		
		tacticName = tacticDescIdentity.getTacticName();
		assertTrue(tacticName.equals(IdentityTactic.TACTIC_NAME));

		tacticName = tacticDescFail.getTacticName();
		assertTrue(tacticName.equals(FailTactic.TACTIC_NAME));
	}
	
	/**
	 * Test method for {@link ITacticDescriptor#getTacticDescription()}.
	 * 
	 */
	@Test
	public void testGetTacticDecsrDesc() {
		ITacticDescriptor tacticDescIdentity = registry.getTacticDescriptor(IdentityTactic.TACTIC_ID);				
		ITacticDescriptor tacticDescFail = registry.getTacticDescriptor(FailTactic.TACTIC_ID);

		String tacticName;
		
		tacticName = tacticDescIdentity.getTacticDescription();
		assertTrue(tacticName.equals(IdentityTactic.TACTIC_DESC));

		tacticName = tacticDescFail.getTacticDescription();
		assertTrue(tacticName.equals(""));
	}

	
	/**
	 * Test method for {@link ITacticDescriptor#getTacticInstance()}.
	 * 
	 */
	@Test
	public void testGetTacticDecsrInstance() {
		ITacticDescriptor tacticDescIdentity = registry.getTacticDescriptor(IdentityTactic.TACTIC_ID);				
		ITacticDescriptor tacticDescFail = registry.getTacticDescriptor(FailTactic.TACTIC_ID);

		ITactic tactic = tacticDescIdentity.getTacticInstance();
		assertTrue(tactic instanceof IdentityTactic);
		
		tactic = tacticDescFail.getTacticInstance();
		assertTrue(tactic instanceof FailTactic);
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
