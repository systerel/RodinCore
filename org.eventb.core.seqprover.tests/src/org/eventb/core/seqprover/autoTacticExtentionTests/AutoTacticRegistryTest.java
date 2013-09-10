/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
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

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.IParamTacticDescriptor;
import org.eventb.core.seqprover.IParameterDesc;
import org.eventb.core.seqprover.IParameterDesc.ParameterType;
import org.eventb.core.seqprover.IParameterSetting;
import org.eventb.core.seqprover.IParameterizerDescriptor;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.autoTacticExtentionTests.IdentityTactic.FailTactic;
import org.eventb.core.seqprover.autoTacticExtentionTests.ParameterizedTactics.FakeTactic;
import org.eventb.core.seqprover.autoTacticExtentionTests.ParameterizedTactics.TacParameterizer;
import org.eventb.core.seqprover.autoTacticExtentionTests.TestTacticCombinators.FakeTacComb;
import org.eventb.core.seqprover.autoTacticExtentionTests.TestTacticCombinators.NoParseable;
import org.eventb.core.seqprover.autoTacticExtentionTests.TestTacticCombinators.OneOrMore;
import org.eventb.core.seqprover.autoTacticExtentionTests.TestTacticCombinators.Two;
import org.eventb.core.seqprover.autoTacticExtentionTests.TestTacticCombinators.Zero;
import org.eventb.internal.core.seqprover.Placeholders.CombinatorDescriptorPlaceholder;
import org.eventb.internal.core.seqprover.Placeholders.ParameterizerPlaceholder;
import org.eventb.internal.core.seqprover.Placeholders.TacticPlaceholder;
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
		final List<String> idList = asList(ids);
		assertTrue("Missing id " + id + " in list " + idList,
				idList.contains(id));
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
		final List<String> idList = asList(ids);
		assertFalse("Id " + id + " occurs in list " + idList,
				idList.contains(id));
		assertTrue(registry.getTacticDescriptor(id) instanceof TacticPlaceholder);
		
		assertTrue(registry.getCombinatorDescriptor(id) instanceof CombinatorDescriptorPlaceholder);

		assertTrue(registry.getParameterizerDescriptor(id) instanceof ParameterizerPlaceholder);
		
	}
	
	private void assertParameterizerLoadingFailure(String id) {
		final IParameterizerDescriptor parameterizer = findParam(id);
		final IParameterSetting defaultValuation = parameterizer
				.makeParameterSetting();
		try {
			// Illegal State Expected
			parameterizer.instantiate(defaultValuation, "id");
			fail("illegal state exception expected");
		} catch (IllegalStateException e) {
			// as expected
		}
	}
	
	private void assertTacticInstantiatingFailure(String id) {
		final IParameterizerDescriptor param = registry.getParameterizerDescriptor(id);
		assertNotNull(param);
		final IParamTacticDescriptor desc = param.instantiate(
				param.makeParameterSetting(), id + "_instantiated");
		try {
			desc.getTacticInstance();
			fail("expected illegal state exception");
		} catch (IllegalStateException e) {
			// as expected
		}

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
	public void testGetTacticDescriptorUnregistered() {
		// Should not throw an exception
		final ITacticDescriptor desc = registry.getTacticDescriptor(unrigisteredId);
		assertTrue(desc instanceof TacticPlaceholder);
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
	
	// class not instance of ITactic
	@Test(expected = IllegalStateException.class)
	public void testBadInstance() throws Exception {
		final ITacticDescriptor desc = registry.getTacticDescriptor("org.eventb.core.seqprover.tests.badInstance");
		assertNotNull(desc);
		desc.getTacticInstance();
	}

	// check an extension with each type of parameters and verify the descriptors
	@Test
	public void testParameterDesc() {
		final IParameterizerDescriptor parameterizer = registry.getParameterizerDescriptor(TacParameterizer.PARAMETERIZER_ID);
		final Collection<IParameterDesc> paramDescs = parameterizer
				.makeParameterSetting().getParameterDescs();
		assertParamDesc(paramDescs, "bool1", "BOOL", "true",
		 "bool2", "BOOL", "false",
		 "int1", "INT", "314"
		, "long1", "LONG", "9223372036854775807"
		, "string1", "STRING", "formulæ");
		
	}

	private IParameterizerDescriptor findParam(String id) {
		final IParameterizerDescriptor[] parameterizers = registry
				.getParameterizerDescriptors();
		for (IParameterizerDescriptor param : parameterizers) {
			if (param.getTacticDescriptor().getTacticID().equals(id)) {
				return param;
			}
		}
		return null;
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testGetInstanceUninstantiatedParamDesc() throws Exception {
		final IParameterizerDescriptor parameterizer = registry
				.getParameterizerDescriptor(TacParameterizer.PARAMETERIZER_ID);
		final ITacticDescriptor desc = parameterizer.getTacticDescriptor();
		desc.getTacticInstance();
	}

	/*
	 * Ensures that an instantiated tactic carries the appropriate fields when
	 * using the simple instantiation function.
	 */
	@Test
	public void testInstantiationSimple() {
		final String id = "id1";
		final IParameterizerDescriptor parameterizer = registry
				.getParameterizerDescriptor(TacParameterizer.PARAMETERIZER_ID);
		final ITacticDescriptor desc = parameterizer.getTacticDescriptor();
		final IParameterSetting parameters = makeParameters(parameterizer);
		final IParamTacticDescriptor instanceDesc = parameterizer.instantiate(
				parameters, id);
		assertEquals(id, instanceDesc.getTacticID());
		assertEquals(desc.getTacticName(), instanceDesc.getTacticName());
		assertEquals(desc.getTacticDescription(), instanceDesc.getTacticDescription());
		final FakeTactic customTactic = (FakeTactic) instanceDesc.getTacticInstance();
		customTactic.assertParameterValues(parameters);
	}

	/*
	 * Ensures that an instantiated tactic carries the appropriate fields when
	 * using the complex instantiation function.
	 */
	@Test
	public void testInstantiationComplex() {
		final String id = "id2";
		final String name = "Some name";
		final String description = "Some description";
		final IParameterizerDescriptor parameterizer = registry
				.getParameterizerDescriptor(TacParameterizer.PARAMETERIZER_ID);
		final IParameterSetting parameters = makeParameters(parameterizer);
		final IParamTacticDescriptor instanceDesc = parameterizer.instantiate(
				id, name, description, parameters);
		assertEquals(id, instanceDesc.getTacticID());
		assertEquals(name, instanceDesc.getTacticName());
		assertEquals(description, instanceDesc.getTacticDescription());
		final FakeTactic customTactic = (FakeTactic) instanceDesc.getTacticInstance();
		customTactic.assertParameterValues(parameters);
	}

	private IParameterSetting makeParameters(
			final IParameterizerDescriptor parameterizer) {
		final IParameterSetting parameters = parameterizer.makeParameterSetting();
		parameters.setBoolean("bool1", false);
		parameters.setBoolean("bool2", true);
		parameters.setInt("int1", 51);
		parameters.setLong("long1", Long.MIN_VALUE);
		parameters.setString("string1", "blue");
		return parameters;
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

	// class not instance of ITacticParameterizer
	@Test(expected = IllegalStateException.class)
	public void testBadInstanceNoImplement() throws Exception {
		final ITacticDescriptor desc = registry.getTacticDescriptor("org.eventb.core.seqprover.tests.badInstance");
		desc.getTacticInstance();
	}
	
	// class instance of ITactic with parameters => error
	@Test
	public void testSimpleTacticWithParams() throws Exception {
		assertParameterizerLoadingFailure("org.eventb.core.seqprover.tests.tacWithParam");
	}
	
	// class instance of ITacticParameterizer without parameters => no error expected
	@Test
	public void testParameterizerWithoutParam() throws Exception {
		final ITacticDescriptor desc = registry.getTacticDescriptor("org.eventb.core.seqprover.tests.noParam");
		desc.getTacticInstance();
	}

	@Test
	public void testParameterizerNullInstance() throws Exception {
		assertTacticInstantiatingFailure("org.eventb.core.seqprover.tests.paramNullInstance");
	}
	
	@Test
	public void testParameterizerThrowsException() throws Exception {
		assertTacticInstantiatingFailure("org.eventb.core.seqprover.tests.paramThrowsException");
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

	private ICombinatorDescriptor findComb(String id) {
		for (ICombinatorDescriptor comb : registry.getCombinatorDescriptors()) {
			if (comb.getTacticDescriptor().getTacticID().equals(id)) {
				return comb;
			}
		}
		return null;
	}

	@Test
	public void testCombinedTacticDescriptor() throws Exception {
		final ICombinatorDescriptor comb = findComb(FakeTacComb.COMBINATOR_ID);
		final List<ITacticDescriptor> combined = Collections
				.singletonList(registry
						.getTacticDescriptor(IdentityTactic.TACTIC_ID));
		final ITactic instance = comb.combine(combined, "id").getTacticInstance();

		final Object result = instance.apply(null, null);
		assertEquals(FakeTacComb.MESSAGE, result);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testgetInstanceUninstantiatedCombinedDesc() throws Exception {
		final ICombinatorDescriptor combDesc = registry
				.getCombinatorDescriptor(OneOrMore.COMBINATOR_ID);
		final ITacticDescriptor desc = combDesc.getTacticDescriptor();
		desc.getTacticInstance();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCombinedOneOrMore() throws Exception {
		final ICombinatorDescriptor desc = registry
				.getCombinatorDescriptor(OneOrMore.COMBINATOR_ID);
		// must throw illegal argument exception
		desc.combine(Collections.<ITacticDescriptor> emptyList(), "id");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCombinedTwo() throws Exception {
		final ICombinatorDescriptor desc = registry
				.getCombinatorDescriptor(Two.COMBINATOR_ID);
		// must throw illegal argument exception
		final List<ITacticDescriptor> combined = Collections
				.<ITacticDescriptor> singletonList(registry.getTacticDescriptor(IdentityTactic.TACTIC_ID));
		desc.combine(combined, "id");
	}
	
	@Test
	public void testCombinedZero() throws Exception {
		// combinator specifying 0 as arity
		assertNotNull(registry.getCombinatorDescriptor(Zero.COMBINATOR_ID));
	}
	
	@Test
	public void testCombinedNoParseable() throws Exception {
		// combinator specifying an unparseable arity
		assertNotKnown(NoParseable.COMBINATOR_ID);
	}
	
	@Test
	public void testValuationEquals() throws Exception {
		final IParameterizerDescriptor parameterizer = registry.getParameterizerDescriptor(TacParameterizer.PARAMETERIZER_ID);
		final IParameterSetting setting1 = parameterizer.makeParameterSetting();
		final IParameterSetting setting2 = parameterizer.makeParameterSetting();
		assertEquals(setting1, setting2);
	}
}
