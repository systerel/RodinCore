/*******************************************************************************
 * Copyright (c) 2007, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - implemented parameterized auto tactics
 *     Systerel - implemented tactic combinators
 *******************************************************************************/
package org.eventb.core.seqprover.autoTacticExtentionTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ICombinedTacticDescriptor;
import org.eventb.core.seqprover.IAutoTacticRegistry.IParamTacticDescriptor;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.IParameterDesc;
import org.eventb.core.seqprover.IParameterDesc.ParameterType;
import org.eventb.core.seqprover.IParameterSetting;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.autoTacticExtentionTests.IdentityTactic.FailTactic;
import org.eventb.core.seqprover.autoTacticExtentionTests.ParameterizedTactics.FakeTactic;
import org.eventb.core.seqprover.autoTacticExtentionTests.ParameterizedTactics.TacWithParams;
import org.eventb.core.seqprover.autoTacticExtentionTests.TacticCombinators.FakeTacComb;
import org.eventb.core.seqprover.autoTacticExtentionTests.TacticCombinators.NoParseable;
import org.eventb.core.seqprover.autoTacticExtentionTests.TacticCombinators.OneOrMore;
import org.eventb.core.seqprover.autoTacticExtentionTests.TacticCombinators.Two;
import org.eventb.core.seqprover.autoTacticExtentionTests.TacticCombinators.Zero;
import org.junit.Test;

/**
 * Unit tests for the tactic registry
 * 
 * @see org.eventb.core.seqprover.IAutoTacticRegistry
 * 
 * @author Farhad Mehta
 * @author Laurent Voisin
 * @author Nicolas Beauger
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
	
	private void assertParameterizerLoadingFailure(String id) {
		assertKnown(id);
		final ITacticDescriptor desc = registry.getTacticDescriptor(id);
		// Illegal Argument Expected
		try {
			desc.getTacticInstance();
			fail("illegal argument exception expected");
		} catch (IllegalArgumentException e) {
			// as expected
		}
	}
	
	private void assertTacticInstantiatingFailure(String id) {
		assertKnown(id);
		final ITacticDescriptor desc = registry.getTacticDescriptor(id);
		final ITactic tactic = desc.getTacticInstance();
		assertNotNull(tactic.apply(null, null));
	}
	
	private void assertInstanceLoadingSuccess(String id) {
		assertKnown(id);
		final ITacticDescriptor desc = registry.getTacticDescriptor(id);
		// no exception expected
		final ITactic tactic = desc.getTacticInstance();
		assertNull(tactic.apply(null, null));
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

	// check an extension with each type of parameters and verify the descriptors
	@Test
	public void testParameterDesc() {
		final IParamTacticDescriptor tacDesc = (IParamTacticDescriptor) registry
				.getTacticDescriptor(TacWithParams.TACTIC_ID);
		final Collection<IParameterDesc> paramDescs = tacDesc.getParameterDescs();
		assertParamDesc(paramDescs, "bool1", "BOOL", "true",
		 "bool2", "BOOL", "false",
		 "int1", "INT", "314"
		, "long1", "LONG", "9223372036854775807"
		, "string1", "STRING", "formulæ");
		
	}

	@Test
	public void testDefaultParameterValues() throws Exception {
		final IParamTacticDescriptor tacDesc = (IParamTacticDescriptor) registry
				.getTacticDescriptor(TacWithParams.TACTIC_ID);
		final IParameterSetting parameters = tacDesc.makeParameterSetting();

		// unmodified parameters
		final FakeTactic unmodifiedTactic = (FakeTactic) tacDesc
				.getTacticInstance(parameters);
		unmodifiedTactic.assertParameterValues(true, false, 314,
				0x7fffffffffffffffL, "formulæ");
		
		// same if we ask for the default instance
		final FakeTactic defaultInstance = (FakeTactic) tacDesc
				.getTacticInstance();
		defaultInstance.assertParameterValues(true, false, 314,
				0x7fffffffffffffffL, "formulæ");
	}
	
	@Test
	public void testSetParameterValues() throws Exception {
		final IParamTacticDescriptor tacDesc = (IParamTacticDescriptor) registry
				.getTacticDescriptor(TacWithParams.TACTIC_ID);
		final IParameterSetting parameters = tacDesc.makeParameterSetting();
		
		parameters.setBoolean("bool1", false);
		parameters.setBoolean("bool2", true);
		parameters.setInt("int1", 51);
		parameters.setLong("long1", Long.MIN_VALUE);
		parameters.setString("string1", "blue");

		final FakeTactic customTactic = (FakeTactic) tacDesc
				.getTacticInstance(parameters);
		customTactic.assertParameterValues(false, true, 51,
				Long.MIN_VALUE, "blue");
	}
	
	// label, type, default value
	private static void assertParamDesc(Collection<IParameterDesc> paramDesc,
			String... labelTypeDefault) {
		for (IParameterDesc desc : paramDesc) {
			final int i = indexOf(desc.getLabel(), labelTypeDefault);
			assertTrue(i>=0);
			assertEquals(labelTypeDefault[i], desc.getLabel());
			final ParameterType paramType = ParameterType.valueOf(labelTypeDefault[i + 1]);
			assertEquals(paramType, desc.getType());
			assertEquals(paramType.parse(labelTypeDefault[i + 2]),
					desc.getDefaultValue());
		}
	}

	private static int indexOf(String label, String[] labelTypeDefault) {
		for (int i = 0; i < labelTypeDefault.length; i += 3) {
			if (labelTypeDefault[i].equals(label)) {
				return i;
			}
		}
		return -1;
	}

	// class not instance of ITactic nor ITacticParameterizer
	@Test
	public void testBadInstanceNoImplement() throws Exception {
		assertParameterizerLoadingFailure("org.eventb.core.seqprover.tests.badInstance");
	}
	
	// class instance of ITactic with parameters => error
	@Test
	public void testSimpleTacticWithParams() throws Exception {
		assertParameterizerLoadingFailure("org.eventb.core.seqprover.tests.tacWithParam");
	}
	
	// class instance of ITacticParameterizer without parameters => error
	@Test
	public void testParameterizerWithoutParam() throws Exception {
		assertParameterizerLoadingFailure("org.eventb.core.seqprover.tests.noParam");
	}

	@Test
	public void testParameterizerNullInstance() throws Exception {
		assertTacticInstantiatingFailure("org.eventb.core.seqprover.tests.paramNullInstance");
	}
	
	@Test
	public void testParameterizerThrowsException() throws Exception {
		assertTacticInstantiatingFailure("org.eventb.core.seqprover.tests.paramThrowsException");
	}
	
	// class instance of both ITactic and ITacticParameterizer => accepted, only
	// the relevant interface is used
	@Test
	public void testInstanceImplementsBoth() throws Exception {
		assertInstanceLoadingSuccess("org.eventb.core.seqprover.tests.both");
	}

	// default value does not parse with given type => error
	@Test
	public void testNotParseableDefaultValues() throws Exception {
		// boolean is true if equalsIgnoreCase("true") else false => always parseable
		assertNotKnown("org.eventb.core.seqprover.tests.notParseableInt");
		assertNotKnown("org.eventb.core.seqprover.tests.notParseableLong");
		// a string is always parseable
	}
	
	// duplicate label => error
	@Test
	public void testDuplicateLabel() throws Exception {
		assertNotKnown("org.eventb.core.seqprover.tests.duplLabel");
	}
	
	@Test
	public void testCombinedTacticDescriptor() throws Exception {
		assertKnown(FakeTacComb.TACTIC_ID);
		final ICombinedTacticDescriptor desc = (ICombinedTacticDescriptor) registry
				.getTacticDescriptor(FakeTacComb.TACTIC_ID);
		ITacticDescriptor tacticDescIdentity = registry
				.getTacticDescriptor(IdentityTactic.TACTIC_ID);
		final List<ITacticDescriptor> combined = Arrays
				.asList(tacticDescIdentity);
		final ITactic instance = desc.getTacticInstance(combined);

		final Object result = instance.apply(null, null);
		assertEquals(FakeTacComb.MESSAGE, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCombinedGetInstanceNoArg() throws Exception {
		final ICombinedTacticDescriptor desc = (ICombinedTacticDescriptor) registry
				.getTacticDescriptor(FakeTacComb.TACTIC_ID);
		// must throw illegal argument exception
		desc.getTacticInstance();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCombinedOneOrMore() throws Exception {
		final ICombinedTacticDescriptor desc = (ICombinedTacticDescriptor) registry
				.getTacticDescriptor(OneOrMore.TACTIC_ID);
		// must throw illegal argument exception
		desc.getTacticInstance(Collections.<ITacticDescriptor> emptyList());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCombinedTwo() throws Exception {
		final ICombinedTacticDescriptor desc = (ICombinedTacticDescriptor) registry
				.getTacticDescriptor(Two.TACTIC_ID);
		// must throw illegal argument exception
		desc.getTacticInstance(Arrays.<ITacticDescriptor> asList(desc, desc));
	}
	
	@Test
	public void testCombinedZero() throws Exception {
		assertNotKnown(Zero.TACTIC_ID);
	}
	
	@Test
	public void testCombinedNoParseable() throws Exception {
		assertNotKnown(NoParseable.TACTIC_ID);
	}
	
	
}
