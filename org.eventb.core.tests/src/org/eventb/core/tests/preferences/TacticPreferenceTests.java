/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.preferences;

import static java.util.Arrays.asList;
import static org.eventb.core.preferences.autotactics.TacticPreferenceFactory.makeTacticPreferenceMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.preferences.CachedPreferenceMap;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.preferences.IPreferenceCheckResult;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;
import org.eventb.core.seqprover.IParamTacticDescriptor;
import org.eventb.core.seqprover.IParameterSetting;
import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.core.seqprover.IParameterizerDescriptor;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.tests.preferences.TestingAutoTactics.CombTestAutoTac;
import org.eventb.core.tests.preferences.TestingAutoTactics.ParamerizerTestAutoTac;
import org.eventb.core.tests.preferences.TestingAutoTactics.SimpleTestAutoTac;
import org.eventb.internal.core.preferences.ITacticDescriptorRef;
import org.junit.Test;

/**
 * @author Nicolas Beauger
 * 
 */
public class TacticPreferenceTests {

	private static void assertExtractInject(ITacticDescriptor desc) {
		final CachedPreferenceMap<ITacticDescriptor> map = makeTacticPreferenceMap();
		map.add("test", desc);
		assertExtractInject(map);
	}

	private static ITacticDescriptor makeSimple() {
		return makeSimple(SimpleTestAutoTac.TACTIC_ID);
	}

	private static ITacticDescriptor makeSimple(String tacticId) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(tacticId);
	}

	private static void assertSimple(ITacticDescriptor desc, String expectedId) {
		assertNotNull(desc);
		assertEquals(expectedId, desc.getTacticID());
		// no exception thrown
		desc.getTacticInstance();
	}
	
	private static IParamTacticDescriptor makeParam() {
		return makeParam(false, 52, 10L, "str");
	}
	
	private static IParamTacticDescriptor makeParam(boolean b, int i, long l, String s) {
		return makeParam(ParamerizerTestAutoTac.PARAMETERIZER_ID, b, i, l, s);
	}

	private static IParamTacticDescriptor makeParam(String parameterizerId, boolean b, int i, long l, String s) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final IParameterizerDescriptor parameterizer = reg
				.getParameterizerDescriptor(parameterizerId);
		final IParameterSetting params = parameterizer.makeParameterSetting();
		params.setBoolean("b", b);
		params.setInt("i", i);
		params.setLong("l", l);
		params.setString("s", s);
		return parameterizer.instantiate(params, "param id");
	}

	private static void assertParam(ITacticDescriptor desc, IParameterValuation valuation) {
		assertNotNull(desc);
		assertTrue(desc instanceof IParamTacticDescriptor);
		assertEquals(valuation, ((IParamTacticDescriptor) desc).getValuation());
	}
	
	private static ICombinedTacticDescriptor makeCombined(ITacticDescriptor... tactics) {
		return makeCombined(CombTestAutoTac.COMBINATOR_ID, tactics);
	}

	private static ICombinedTacticDescriptor makeCombined(String combinatorId, ITacticDescriptor... tactics) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ICombinatorDescriptor combinator = reg
				.getCombinatorDescriptor(combinatorId);
		return combinator.combine(Arrays.asList(tactics),
				"combined id");
	}

	private static void assertCombined(ITacticDescriptor actual,
			boolean sameMap, ITacticDescriptor... tactics) {
		assertCombined(actual, sameMap, CombTestAutoTac.COMBINATOR_ID, tactics);
	}

	private static void assertCombined(ITacticDescriptor actual,
			boolean sameMap, String combinatorId, ITacticDescriptor... tactics) {
		assertNotNull(actual);
		assertTrue(actual instanceof ICombinedTacticDescriptor);
		final ICombinedTacticDescriptor act = (ICombinedTacticDescriptor) actual;
		assertEquals(combinatorId, act.getCombinatorId());
		// no exception
		actual.getTacticInstance();
		final List<ITacticDescriptor> actualTactics = act.getCombinedTactics();
		assertEquals(tactics.length, actualTactics.size());
		for (int i = 0; i < tactics.length; i++) {
			final ITacticDescriptor expected = tactics[i];
			final ITacticDescriptor actDesc = actualTactics.get(i);
			assertGenericDesc(expected, actDesc, sameMap);
		}
	}

	private static ITacticDescriptorRef makeRef(CachedPreferenceMap<ITacticDescriptor> map, String name) {
		final IPrefMapEntry<ITacticDescriptor> entry = map.getEntry(name);
		assertNotNull(entry);
		final ITacticDescriptor ref = entry.getReference();
		assertNotNull(ref);
		return (ITacticDescriptorRef) ref;
	}
	
	private static void assertRef(ITacticDescriptorRef expected,
			ITacticDescriptor actual, boolean sameInstance) {
		assertTrue(actual instanceof ITacticDescriptorRef);
		final ITacticDescriptorRef actRef = (ITacticDescriptorRef) actual;
		assertTrue(actRef.isValidReference());
		assertEquals(expected.getTacticID(), actRef.getTacticID());
		assertEquals(expected.getTacticName(), actRef.getTacticName());
		assertEquals(expected.getTacticDescription(), actRef.getTacticDescription());
		if(sameInstance) {
			assertSame(expected.getTacticInstance(), actRef.getTacticInstance());
		} else {
			final ITacticDescriptor expDesc = expected.getPrefEntry().getValue();
			final ITacticDescriptor actualDesc = actRef.getPrefEntry().getValue();
			assertGenericDesc(expDesc, actualDesc, sameInstance);
		}
	}

	private static void assertGenericDesc(ITacticDescriptor expected,
			ITacticDescriptor actual, boolean sameMap) {
		assertNotNull(actual);
		if (expected instanceof ITacticDescriptorRef) {
			assertRef((ITacticDescriptorRef)expected, actual, sameMap);
			return;
		}
	
		if (expected instanceof ICombinedTacticDescriptor) {
			final ICombinedTacticDescriptor expectedComb = (ICombinedTacticDescriptor) expected;
			final List<ITacticDescriptor> combinedTactics = expectedComb
					.getCombinedTactics();
			final ITacticDescriptor[] combs = combinedTactics
					.toArray(new ITacticDescriptor[combinedTactics.size()]);
			assertCombined(actual, sameMap, expectedComb.getCombinatorId(),
					combs);
		} else if (expected instanceof IParamTacticDescriptor) {
			assertParam(actual,
					((IParamTacticDescriptor) expected).getValuation());
		} else {
			assertSimple(actual, expected.getTacticID());
		}
	}

	private static void assertExtractInject(CachedPreferenceMap<ITacticDescriptor> map) {
		final String extracted = map.extract();
		final CachedPreferenceMap<ITacticDescriptor> injected = makeTacticPreferenceMap();
		injected.inject(extracted);
		assertCachedPreferenceMap(map, injected);
	}

	private static void assertCachedPreferenceMap(
			CachedPreferenceMap<ITacticDescriptor> expected,
			CachedPreferenceMap<ITacticDescriptor> actual) {
		final List<IPrefMapEntry<ITacticDescriptor>> expEntries = expected.getEntries();
		final List<IPrefMapEntry<ITacticDescriptor>> actEntries = actual.getEntries();
		assertEquals(expEntries.size(), actEntries.size());
		for (IPrefMapEntry<ITacticDescriptor> expEntry : expEntries) {
			final String expKey = expEntry.getKey();
			final IPrefMapEntry<ITacticDescriptor> actEntry = actual.getEntry(expKey);
			assertNotNull(actEntry);
			assertEquals(expEntry.getKey(), actEntry.getKey());
			final ITacticDescriptor expDesc = expEntry.getValue();
			final ITacticDescriptor actDesc = actEntry.getValue();
			assertGenericDesc(expDesc, actDesc, false);
		}
	}

	@Test
	public void testSimpleTactic() throws Exception {
		final ITacticDescriptor desc = makeSimple();
		assertExtractInject(desc);
	}

	@Test
	public void testUnknownTactic() throws Exception {
		final ITacticDescriptor desc = makeSimple("unknownTestTactic");
		assertExtractInject(desc);
	}

	@Test
	public void testParamTactic() throws Exception {
		final IParamTacticDescriptor desc = makeParam();
		
		assertExtractInject(desc);
	}

	@Test
	public void testUnknownParameterizer() throws Exception {
		final IParamTacticDescriptor desc = makeParam("unknownTestParameterizer", false, 123, 314L, "unknownParam");
		assertExtractInject(desc);
	}

	@Test
	public void testCombinedTactic() throws Exception {
		
		final ITacticDescriptor simple = makeSimple();
		final ICombinedTacticDescriptor desc = makeCombined(simple);
		
		assertExtractInject(desc);
	}
	
	@Test
	public void testUnknownCombinator() throws Exception {
		final ITacticDescriptor simple = makeSimple();
		final ICombinedTacticDescriptor desc = makeCombined("unknownCombinator", simple);
		assertExtractInject(desc);
	}

	@Test
	public void testCombinedParam() throws Exception {
		final IParamTacticDescriptor param = makeParam();
		final ICombinedTacticDescriptor desc = makeCombined(param);
		assertExtractInject(desc);
	}
	
	@Test
	public void testCombinedSeveralMixed() throws Exception {
		final ITacticDescriptor simple = makeSimple();
		final IParamTacticDescriptor param = makeParam();
		final ICombinedTacticDescriptor desc = makeCombined(simple, param);
		assertExtractInject(desc);
	}
	
	@Test
	public void testCombinedOfCombined() throws Exception {
		final ITacticDescriptor simple = makeSimple();
		final ICombinedTacticDescriptor comb = makeCombined(simple);
		final ICombinedTacticDescriptor desc = makeCombined(comb);
		assertExtractInject(desc);
	}

	@Test
	public void testCombinedRef() throws Exception {
		final CachedPreferenceMap<ITacticDescriptor> map = makeTacticPreferenceMap();
		final String paramName = "param";
		final IParamTacticDescriptor param = makeParam(true, 5, 92L,
				"param");
		map.add(paramName, param);
	
		final String combinedName = "combined";
		final ICombinedTacticDescriptor combined = makeCombined(makeRef(map, paramName));
		map.add(combinedName, combined);

		assertExtractInject(map);
	}

	// verify that the reference always points to the good descriptor
	// even if it changes (key and/or value)
	@Test
	public void testRef() throws Exception {
		final CachedPreferenceMap<ITacticDescriptor> map = makeTacticPreferenceMap();
		final String paramName = "param";
		final IParamTacticDescriptor original = makeParam(true, 5, 92L,
				"original");
		map.add(paramName, original);
		final String combinedName = "combined";
		map.add(combinedName, makeCombined(makeRef(map, paramName)));
		final IPrefMapEntry<ITacticDescriptor> combinedEntry = map
				.getEntry(combinedName);
		assertCombined(combinedEntry.getValue(), true, makeRef(map, paramName));
	
		// change descriptor
		final IParamTacticDescriptor modified = makeParam(false, 6, 0,
				"modified");
		final IPrefMapEntry<ITacticDescriptor> paramEntry = map
				.getEntry(paramName);
		paramEntry.setValue(modified);

		// change preference key
		final String modifiedParamName = paramName + " modified";
		paramEntry.setKey(modifiedParamName);
	
		assertCombined(combinedEntry.getValue(), true, makeRef(map, modifiedParamName));
	}

	@Test
	public void testSelfRef() throws Exception {
		final CachedPreferenceMap<ITacticDescriptor> map = makeTacticPreferenceMap();
		final String selfName = "self";
		final ITacticDescriptor simple = makeSimple();
		map.add(selfName, simple);
		final ITacticDescriptor selfRef = makeRef(map, selfName);
		final IPrefMapEntry<ITacticDescriptor> selfEntry = map
				.getEntry(selfName);
		try {
			// this should throw IllegalArgumentException
			selfEntry.setValue(selfRef);
			// this is what happens if the previous does not throw exception
			selfEntry.getValue().getTacticInstance();
			// just in case, but the above should raise StackOverflowError
			fail("expected illegal argument exception because of self reference");
		} catch (IllegalArgumentException e) {
			// as expected
		}
	}

	@Test
	public void testSelfRefRemoveAdd() throws Exception {
		final CachedPreferenceMap<ITacticDescriptor> map = makeTacticPreferenceMap();
		final String selfName = "self";
		final ITacticDescriptor simple = makeSimple();
		map.add(selfName, simple);
		final ITacticDescriptor selfRef = makeRef(map, selfName);

		map.remove(selfName);
		
		final IPreferenceCheckResult result = map.preAddCheck(selfName, selfRef);
		assertTrue(result.hasError());
		final List<String> cycle = result.getCycle();
		assertEquals(Collections.singletonList(selfName), cycle);
		try {
			map.add(selfName, selfRef);
			map.getEntry(selfName).getValue().getTacticInstance();
			fail("expected illegal argument exception because of self reference");
		} catch (IllegalArgumentException e) {
			// as expected
		}
	}

	@Test
	public void testCombinedSelfRef() throws Exception {
		final CachedPreferenceMap<ITacticDescriptor> map = makeTacticPreferenceMap();
		final String selfName = "self";
		final ICombinedTacticDescriptor combined = makeCombined(makeSimple());
		map.add(selfName, combined);
		final ITacticDescriptor selfRef = makeRef(map, selfName);
		final ICombinedTacticDescriptor selfCombined = makeCombined(selfRef);
		final IPrefMapEntry<ITacticDescriptor> selfEntry = map
				.getEntry(selfName);
		try {
			selfEntry.setValue(selfCombined);
			selfEntry.getValue().getTacticInstance();
			fail("expected illegal argument exception because of self reference");
		} catch (IllegalArgumentException e) {
			// as expected
		}
	}
	
	@Test
	public void testCrossRef() throws Exception {
		final CachedPreferenceMap<ITacticDescriptor> map = makeTacticPreferenceMap();
		final String cross1Name = "cross1";
		final ITacticDescriptor simple = makeSimple();
		map.add(cross1Name, simple);
		final ITacticDescriptor cross1Ref = makeRef(map, cross1Name);
		final String cross2Name = "cross2";
		map.add(cross2Name, cross1Ref);
		final ITacticDescriptorRef cross2Ref = makeRef(map, cross2Name);
		final IPrefMapEntry<ITacticDescriptor> cross1Entry = map
				.getEntry(cross1Name);
		try {
			cross1Entry.setValue(cross2Ref);
			cross1Entry.getValue().getTacticInstance();
			fail("expected illegal argument exception because of self reference");
		} catch (IllegalArgumentException e) {
			// as expected
		}

	}
	@Test
	public void testCrossRefPreAddCheck() throws Exception {
		final CachedPreferenceMap<ITacticDescriptor> map = makeTacticPreferenceMap();
		final String cross1Name = "cross1";
		final ITacticDescriptor simple = makeSimple();
		map.add(cross1Name, simple);
		final ITacticDescriptor cross1Ref = makeRef(map, cross1Name);
		final String cross2Name = "cross2";
		map.add(cross2Name, cross1Ref);
		final ITacticDescriptorRef cross2Ref = makeRef(map, cross2Name);
		
		map.remove(cross1Name);
		
		final IPreferenceCheckResult result = map.preAddCheck(cross1Name, cross2Ref);
		assertTrue(result.hasError());
		final List<String> cycle = result.getCycle();
		assertEqualsAnyOrder(asList(cross1Name, cross2Name), cycle);
	}

	private static <T> void assertEqualsAnyOrder(List<T> expList, List<T> actList) {
		final Set<T> expSet = new HashSet<T>(expList);
		final Set<T> actSet = new HashSet<T>(actList);
		assertEquals(expSet, actSet);
	}
}
