/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.preferences;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eventb.core.preferences.CachedPreferenceMap;
import org.eventb.core.preferences.IPrefElementTranslator;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.preferences.autotactics.TacticPreferenceFactory;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;
import org.eventb.core.seqprover.IParamTacticDescriptor;
import org.eventb.core.seqprover.IParameterSetting;
import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.core.seqprover.IParameterizerDescriptor;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.tests.preferences.TestingAutoTactics.CombTestAutoTac;
import org.eventb.core.tests.preferences.TestingAutoTactics.CombinedTestAutoTac;
import org.eventb.core.tests.preferences.TestingAutoTactics.ParamTestAutoTac;
import org.eventb.core.tests.preferences.TestingAutoTactics.ParamerizerTestAutoTac;
import org.eventb.core.tests.preferences.TestingAutoTactics.SimpleTestAutoTac;
import org.eventb.internal.core.preferences.ITacticDescriptorRef;

/**
 * @author Nicolas Beauger
 * 
 */
public class TacticPreferenceTests extends TestCase {

	private static ITacticDescriptor extractInject(ITacticDescriptor desc) {
		final IPrefElementTranslator<ITacticDescriptor> prefElemTrans = TacticPreferenceFactory
				.getTacticPrefElement();
		final String extracted = prefElemTrans.extract(desc);
		final ITacticDescriptor injected = prefElemTrans.inject(extracted);
		return injected;
	}

	private static ITacticDescriptor makeSimple() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(SimpleTestAutoTac.TACTIC_ID);
	}

	private static void assertSimple(ITacticDescriptor desc) {
		assertNotNull(desc);
		assertEquals(SimpleTestAutoTac.TACTIC_ID, desc.getTacticID());
		final ITactic inst = desc.getTacticInstance();
		assertTrue(inst instanceof SimpleTestAutoTac);
	}
	
	private static IParamTacticDescriptor makeParam() {
		return makeParam(false, 52, 10L, "str");
	}
	
	private static void assertParam(ITacticDescriptor desc) {
		assertParam(desc, false, 52, 10L, "str");
	}
	
	private static IParamTacticDescriptor makeParam(boolean b, int i, long l, String s) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final IParameterizerDescriptor parameterizer = reg
				.getParameterizerDescriptor(ParamerizerTestAutoTac.PARAMETERIZER_ID);
		final IParameterSetting params = parameterizer.makeParameterSetting();
		params.setBoolean("b", b);
		params.setInt("i", i);
		params.setLong("l", l);
		params.setString("s", s);
		return parameterizer.instantiate(params, "param id");
	}

	private static void assertParam(ITacticDescriptor desc, boolean b, int i, long l, String s) {
		assertTrue(desc instanceof IParamTacticDescriptor);
		
		assertEquals(ParamerizerTestAutoTac.PARAMETERIZER_ID,
				((IParamTacticDescriptor) desc).getParameterizerId());
		assertEquals("param id", desc.getTacticID());
		final ITactic inst = desc.getTacticInstance();
		assertTrue(inst instanceof ParamTestAutoTac);
		final ParamTestAutoTac prm = (ParamTestAutoTac) inst;
		assertEquals(b, prm.getBool());
		assertEquals(i, prm.getInteger());
		assertEquals(l, prm.getLongInt());
		assertEquals(s, prm.getString());
	}
	
	private static void assertParam(ITacticDescriptor desc, IParameterValuation valuation) {
		assertTrue(desc instanceof IParamTacticDescriptor);
		assertEquals(valuation, ((IParamTacticDescriptor) desc).getValuation());
	}
	
	private static ICombinedTacticDescriptor makeCombined(ITacticDescriptor... tactics) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ICombinatorDescriptor combinator = reg
				.getCombinatorDescriptor(CombTestAutoTac.COMBINATOR_ID);
		return combinator.instantiate(Arrays.asList(tactics),
				"combined id");
	}

	private static void assertCombined(ITacticDescriptor actual,
			ITacticDescriptor... tactics) {
		assertNotNull(actual);
		assertTrue(actual instanceof ICombinedTacticDescriptor);
		final ICombinedTacticDescriptor act = (ICombinedTacticDescriptor) actual;
		assertEquals(CombTestAutoTac.COMBINATOR_ID, act.getCombinatorId());
		final ITactic inst = actual.getTacticInstance();
		assertTrue(inst instanceof CombinedTestAutoTac);
		final List<ITacticDescriptor> actualTactics = act.getCombinedTactics();
		assertEquals(tactics.length, actualTactics.size());
		for (int i = 0; i < tactics.length; i++) {
			final ITacticDescriptor expected = tactics[i];
			final ITacticDescriptor actDesc = actualTactics.get(i);
			assertGenericDesc(expected, actDesc);
		}
	}

	private static ITacticDescriptorRef makeRef(CachedPreferenceMap<ITacticDescriptor> map, String name) {
		final ITacticDescriptor ref = map.getReference(name);
		assertNotNull(ref);
		return (ITacticDescriptorRef) ref;
	}
	
	private static void assertRef(ITacticDescriptorRef ref,
			ITacticDescriptor desc) {
		assertTrue(ref.isValidReference());
		assertSame(desc.getTacticID(), ref.getTacticID());
		assertSame(desc.getTacticName(), ref.getTacticName());
		assertSame(desc.getTacticDescription(), ref.getTacticDescription());
		assertSame(desc.getTacticInstance(), ref.getTacticInstance());
	}

	private static void assertGenericDesc(ITacticDescriptor expected,
			ITacticDescriptor actual) {
		assertNotNull(actual);
		if (actual instanceof ITacticDescriptorRef) {
			assertRef((ITacticDescriptorRef)actual, expected);
			return;
		}
	
		if (expected instanceof ICombinedTacticDescriptor) {
			final List<ITacticDescriptor> combinedTactics = ((ICombinedTacticDescriptor) expected)
					.getCombinedTactics();
			final ITacticDescriptor[] combs = combinedTactics
					.toArray(new ITacticDescriptor[combinedTactics.size()]);
			assertCombined(actual, combs);
		} else if (expected instanceof IParamTacticDescriptor) {
				assertParam(actual,
					((IParamTacticDescriptor) expected).getValuation());
		} else {
			assertSimple(actual);
		}
	}

	private static CachedPreferenceMap<ITacticDescriptor> makeCachedPrefMap() {
		return new CachedPreferenceMap<ITacticDescriptor>(
				TacticPreferenceFactory.getTacticPrefElement(),
				TacticPreferenceFactory.getTacticRefMaker());
	}

	// TODO test mapEntry.setKey
	
	private static void assertExtractInject(CachedPreferenceMap<ITacticDescriptor> map) {
		final String extracted = map.extract();
		final CachedPreferenceMap<ITacticDescriptor> injected = makeCachedPrefMap();
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
			assertGenericDesc(expDesc, actDesc);
		}
	}

	public void testSimpleTactic() throws Exception {
		final ITacticDescriptor desc = makeSimple();
		final ITacticDescriptor actual = extractInject(desc);
		
		assertSimple(actual);
	}

	public void testParamTactic() throws Exception {
		final IParamTacticDescriptor desc = makeParam();
		
		final ITacticDescriptor actual = extractInject(desc);
		
		assertParam(actual);
	}

	public void testCombinedTactic() throws Exception {
		
		final ITacticDescriptor simple = makeSimple();
		final ICombinedTacticDescriptor desc = makeCombined(simple);
		
		final ITacticDescriptor actual = extractInject(desc);
		
		assertCombined(actual, simple);
	}
	
	public void testCombinedParam() throws Exception {
		final IParamTacticDescriptor param = makeParam();
		final ICombinedTacticDescriptor desc = makeCombined(param);
		final ITacticDescriptor actual = extractInject(desc);
		
		assertCombined(actual, param);
	}
	
	public void testCombinedSeveralMixed() throws Exception {
		final ITacticDescriptor simple = makeSimple();
		final IParamTacticDescriptor param = makeParam();
		final ICombinedTacticDescriptor desc = makeCombined(simple, param);
		final ITacticDescriptor actual = extractInject(desc);
		
		assertCombined(actual, simple, param);
	}
	
	public void testCombinedOfCombined() throws Exception {
		final ITacticDescriptor simple = makeSimple();
		final ICombinedTacticDescriptor comb = makeCombined(simple);
		final ICombinedTacticDescriptor desc = makeCombined(comb);
		final ITacticDescriptor actual = extractInject(desc);
		
		assertCombined(actual, comb);
	}

	public void testCombinedRef() throws Exception {
		final CachedPreferenceMap<ITacticDescriptor> map = new CachedPreferenceMap<ITacticDescriptor>(
				TacticPreferenceFactory.getTacticPrefElement(),
				TacticPreferenceFactory.getTacticRefMaker());
		final String paramName = "param";
		final IParamTacticDescriptor param = makeParam(true, 5, 92L,
				"original");
		map.add(paramName, param);
	
		final String combinedName = "combined";
		map.add(combinedName, makeCombined(makeRef(map, paramName)));

		assertExtractInject(map);
	}

	// TODO test mapEntry.setKey
	
	// verify that the reference always points to the good descriptor
	// even if it changes
	public void testRef() throws Exception {
		final CachedPreferenceMap<ITacticDescriptor> map = new CachedPreferenceMap<ITacticDescriptor>(
				TacticPreferenceFactory.getTacticPrefElement(),
				TacticPreferenceFactory.getTacticRefMaker());
		final String paramName = "param";
		final IParamTacticDescriptor original = makeParam(true, 5, 92L,
				"original");
		map.add(paramName, original);
		final String combinedName = "combined";
		map.add(combinedName, makeCombined(makeRef(map, paramName)));
		final IPrefMapEntry<ITacticDescriptor> combinedEntry = map
				.getEntry(combinedName);
		assertCombined(combinedEntry.getValue(), original);
	
		final IParamTacticDescriptor modified = makeParam(false, 6, 0,
				"modified");
		final IPrefMapEntry<ITacticDescriptor> paramEntry = map
				.getEntry(paramName);
		paramEntry.setValue(modified);
	
		assertCombined(combinedEntry.getValue(), modified);
	}

	public void testSelfRef() throws Exception {
		final CachedPreferenceMap<ITacticDescriptor> map = makeCachedPrefMap();
		final String selfName = "self";
		final ITacticDescriptor simple = makeSimple();
		map.add(selfName, simple);
		final ITacticDescriptor selfRef = makeRef(map, selfName);
		final IPrefMapEntry<ITacticDescriptor> selfEntry = map
				.getEntry(selfName);
		try {
			selfEntry.setValue(selfRef);
			fail("expected illegal argument exception because of self reference");
		} catch (IllegalArgumentException e) {
			// as expected
		}
	}

	public void testSelfRefRemoveAdd() throws Exception {
		final CachedPreferenceMap<ITacticDescriptor> map = makeCachedPrefMap();
		final String selfName = "self";
		final ITacticDescriptor simple = makeSimple();
		map.add(selfName, simple);
		final ITacticDescriptor selfRef = makeRef(map, selfName);

		map.remove(selfName);
		try {
			map.add(selfName, selfRef);
			fail("expected illegal argument exception because of self reference");
		} catch (IllegalArgumentException e) {
			// as expected
		}
	}

	public void testCombinedSelfRef() throws Exception {
		final CachedPreferenceMap<ITacticDescriptor> map = makeCachedPrefMap();
		final String selfName = "self";
		final ICombinedTacticDescriptor combined = makeCombined(makeSimple());
		map.add(selfName, combined);
		final ITacticDescriptor selfRef = makeRef(map, selfName);
		final ICombinedTacticDescriptor selfCombined = makeCombined(selfRef);
		// TODO test map.add(selfName, selfRef)
		final IPrefMapEntry<ITacticDescriptor> selfEntry = map
				.getEntry(selfName);
		try {
			selfEntry.setValue(selfCombined);
			fail("expected illegal argument exception because of self reference");
		} catch (IllegalArgumentException e) {
			// as expected
		}
	}
}
