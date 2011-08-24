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

import org.eventb.core.preferences.IPrefElementTranslator;
import org.eventb.core.preferences.autotactics.TacticPreferenceFactory;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;
import org.eventb.core.seqprover.IParamTacticDescriptor;
import org.eventb.core.seqprover.IParameterSetting;
import org.eventb.core.seqprover.IParameterizerDescriptor;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.tests.preferences.TestingAutoTactics.CombTestAutoTac;
import org.eventb.core.tests.preferences.TestingAutoTactics.CombinedTestAutoTac;
import org.eventb.core.tests.preferences.TestingAutoTactics.ParamTestAutoTac;
import org.eventb.core.tests.preferences.TestingAutoTactics.ParamerizerTestAutoTac;
import org.eventb.core.tests.preferences.TestingAutoTactics.SimpleTestAutoTac;

/**
 * @author Nicolas Beauger
 * 
 */
public class TacticPreferenceTests extends TestCase {

	private static ITacticDescriptor injectExtract(ITacticDescriptor desc) {
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
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final IParameterizerDescriptor parameterizer = reg
				.getParameterizerDescriptor(ParamerizerTestAutoTac.PARAMETERIZER_ID);
		final IParameterSetting params = parameterizer.makeParameterSetting();
		params.setBoolean("b", false);
		params.setInt("i", 52);
		params.setLong("l", 10L);
		params.setString("s", "str");
		return parameterizer.instantiate(params, "param id");
	}

	private static void assertParam(ITacticDescriptor desc) {
		assertTrue(desc instanceof IParamTacticDescriptor);
		
		assertEquals(ParamerizerTestAutoTac.PARAMETERIZER_ID,
				((IParamTacticDescriptor) desc).getParameterizerId());
		assertEquals("param id", desc.getTacticID());
		final ITactic inst = desc.getTacticInstance();
		assertTrue(inst instanceof ParamTestAutoTac);
		final ParamTestAutoTac prm = (ParamTestAutoTac) inst;
		assertEquals(false, prm.getBool());
		assertEquals(52, prm.getInteger());
		assertEquals(10L, prm.getLongInt());
		assertEquals("str", prm.getString());
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
		assertTrue(actual instanceof ICombinedTacticDescriptor);
		final ICombinedTacticDescriptor act = (ICombinedTacticDescriptor) actual;
		assertEquals(CombTestAutoTac.COMBINATOR_ID, act.getCombinatorId());
		final ITactic inst = actual.getTacticInstance();
		assertTrue(inst instanceof CombinedTestAutoTac);
		final List<ITacticDescriptor> actualTactics = act.getCombinedTactics();
		assertEquals(tactics.length, actualTactics.size());
		for (int i = 0; i < tactics.length; i++) {
			final ITacticDescriptor actComb = actualTactics.get(i);
			if (tactics[i] instanceof ICombinedTacticDescriptor) {
				final List<ITacticDescriptor> combinedTactics = ((ICombinedTacticDescriptor) tactics[i])
						.getCombinedTactics();
				final ITacticDescriptor[] combs = combinedTactics
						.toArray(new ITacticDescriptor[combinedTactics.size()]);
				assertCombined(actComb, combs);
			} else if (tactics[i] instanceof IParamTacticDescriptor) {
				assertParam(actComb);
			} else {
				assertSimple(actComb);
			}
		}
	}

	public void testSimpleTactic() throws Exception {
		final ITacticDescriptor desc = makeSimple();
		final ITacticDescriptor actual = injectExtract(desc);
		
		assertSimple(actual);
	}

	public void testParamTactic() throws Exception {
		final IParamTacticDescriptor desc = makeParam();
		
		final ITacticDescriptor actual = injectExtract(desc);
		
		assertParam(actual);
	}

	public void testCombinedTactic() throws Exception {
		
		final ITacticDescriptor simple = makeSimple();
		final ICombinedTacticDescriptor desc = makeCombined(simple);
		
		final ITacticDescriptor actual = injectExtract(desc);
		
		assertCombined(actual, simple);
	}
	
	public void testCombinedParam() throws Exception {
		final IParamTacticDescriptor param = makeParam();
		final ICombinedTacticDescriptor desc = makeCombined(param);
		final ITacticDescriptor actual = injectExtract(desc);
		
		assertCombined(actual, param);
	}
	
	public void testCombinedSeveralMixed() throws Exception {
		final ITacticDescriptor simple = makeSimple();
		final IParamTacticDescriptor param = makeParam();
		final ICombinedTacticDescriptor desc = makeCombined(simple, param);
		final ITacticDescriptor actual = injectExtract(desc);
		
		assertCombined(actual, simple, param);
	}
	
	public void testCombinedOfCombined() throws Exception {
		final ITacticDescriptor simple = makeSimple();
		final ICombinedTacticDescriptor comb = makeCombined(simple);
		final ICombinedTacticDescriptor desc = makeCombined(comb);
		final ITacticDescriptor actual = injectExtract(desc);
		
		assertCombined(actual, comb);
	}
	
}
