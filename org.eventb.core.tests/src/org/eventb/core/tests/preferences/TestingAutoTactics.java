/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.preferences;

import static org.eventb.core.preferences.autotactics.TacticPreferenceFactory.makeProfileReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.IParameterSetting;
import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.core.seqprover.IParameterizerDescriptor;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticCombinator;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.core.seqprover.ITacticParameterizer;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.TacticCombinators.LoopOnAllPending;
import org.eventb.core.seqprover.tactics.BasicTactics;

/**
 * Classes contributing to auto tactic preference extension points.
 * 
 * @author Nicolas Beauger
 */
public class TestingAutoTactics {
	private static final String PLUGIN_ID = "org.eventb.core.tests";

	public static class SimpleTestAutoTac implements ITactic {

		public static final String TACTIC_ID = PLUGIN_ID + ".simple";

		@Override
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return null;
		}

	}

	public static class ParamTestAutoTac implements ITactic {

		private final boolean bool;
		private final int integer;
		private final long longInt;
		private final String string;

		public ParamTestAutoTac(boolean bool, int integer, long longInt,
				String string) {
			this.bool = bool;
			this.integer = integer;
			this.longInt = longInt;
			this.string = string;
		}

		@Override
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return null;
		}

		public boolean getBool() {
			return bool;
		}

		public int getInteger() {
			return integer;
		}

		public long getLongInt() {
			return longInt;
		}

		public String getString() {
			return string;
		}
	}

	public static class ParamerizerTestAutoTac implements ITacticParameterizer {

		public static final String PARAMETERIZER_ID = PLUGIN_ID + ".param";

		@Override
		public ITactic getTactic(IParameterValuation parameters) {
			return new ParamTestAutoTac(parameters.getBoolean("b"),
					parameters.getInt("i"), parameters.getLong("l"),
					parameters.getString("s"));
		}
	}

	public static class CombinedTestAutoTac implements ITactic {

		private final List<ITactic> tactics;

		public CombinedTestAutoTac(List<ITactic> tactics) {
			this.tactics = new ArrayList<ITactic>(tactics);
		}

		@Override
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return null;
		}

		public List<ITactic> getTactics() {
			return tactics;
		}
	}

	public static class CombTestAutoTac implements ITacticCombinator {
		public static final String COMBINATOR_ID = PLUGIN_ID + ".combinator";

		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			return new CombinedTestAutoTac(tactics);
		}

	}

	public static class TestProfile1 implements ITactic, ITacticDescriptor {

		@Override
		public String getTacticID() {
			return "org.event.core.tests.testProfile1";
		}

		@Override
		public String getTacticName() {
			return "Test Tactic #1";
		}

		@Override
		public String getTacticDescription() {
			return "A killer tactic !";
		}

		@Override
		public ITactic getTacticInstance() throws IllegalArgumentException {
			return this;
		}

		@Override
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return BasicTactics.failTac("Too bad ...").apply(ptNode, pm);
		}

		@Override
		public boolean isInstantiable() {
			return true;
		}

	}

	public static class TestProfile2 implements IExecutableExtensionFactory {

		@Override
		public Object create() throws CoreException {
			return makeDescriptor(false);
		}
		
		public static ITacticDescriptor makeResolvedDescriptor() {
			return makeDescriptor(true);
		}

		private static ITacticDescriptor makeDescriptor(boolean resolved) {
			final IAutoTacticRegistry reg = SequentProver
					.getAutoTacticRegistry();
			final ICombinatorDescriptor loop = reg
					.getCombinatorDescriptor(LoopOnAllPending.COMBINATOR_ID);
			final ITacticDescriptor profile1;
			if (resolved) {
				profile1 = new TestProfile1();
			} else {
				profile1 = makeProfileReference("Test Profile #1");
			}

			return loop
					.combine(
							Arrays.asList(
									reg.getTacticDescriptor("org.eventb.core.seqprover.autoRewriteTac"),
									profile1,
									reg.getTacticDescriptor("org.eventb.core.seqprover.eqHypTac")),
							"combinedProfile2");
		}

	}

	public static class TestProfile3 implements IExecutableExtensionFactory {

		@Override
		public Object create() throws CoreException {
			final IAutoTacticRegistry reg = SequentProver
					.getAutoTacticRegistry();
			final IParameterizerDescriptor param = reg
					.getParameterizerDescriptor(TestingAutoTactics.ParamerizerTestAutoTac.PARAMETERIZER_ID);
			final IParameterSetting setting = param.makeParameterSetting();
			setting.setInt("i", 111);
			setting.setLong("l", 555L);
			setting.setBoolean("b", true);
			setting.setString("s", "test profile 3");
			return param.instantiate(setting, "testProfile3");
		}

	}

}
