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
package org.eventb.core.seqprover.autoTacticExtentionTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticCombinator;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.junit.Assert;

/**
 * @author Nicolas Beauger
 * 
 */
public class TestTacticCombinators {

	private static final ITactic FAIL_TAC = BasicTactics
			.failTac("it's just a fake tactic");

	public static class FakeTacComb implements ITacticCombinator {

		public static final String COMBINATOR_ID = "org.eventb.core.seqprover.tests.tacComb";
		public static final String MESSAGE = "FakeTacComb applied";

		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			return BasicTactics.failTac(MESSAGE);
		}

	}

	public static class OneOrMore implements ITacticCombinator {

		public static final String COMBINATOR_ID = "org.eventb.core.seqprover.tests.combOneOrMore";

		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			assertTrue(tactics.size() >= 1);

			return FAIL_TAC;
		}

	}

	public static class Two implements ITacticCombinator {

		public static final String COMBINATOR_ID = "org.eventb.core.seqprover.tests.combTwo";

		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			assertTrue(tactics.size() == 2);

			return FAIL_TAC;
		}

	}

	public static class Zero implements ITacticCombinator {

		public static final String COMBINATOR_ID = "org.eventb.core.seqprover.tests.combZero";

		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			assertEquals(0, tactics.size());

			return FAIL_TAC;
		}

	}

	public static class NoParseable implements ITacticCombinator {

		public static final String COMBINATOR_ID = "org.eventb.core.seqprover.tests.noParseArity";

		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			Assert.fail("arity is not parseable: should not be callable");

			return FAIL_TAC;
		}

	}

}
