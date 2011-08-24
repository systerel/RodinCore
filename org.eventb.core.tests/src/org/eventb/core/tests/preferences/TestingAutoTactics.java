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

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticCombinator;
import org.eventb.core.seqprover.ITacticParameterizer;

/**
 * @author Nicolas Beauger
 * 
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
}
