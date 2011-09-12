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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.eventb.core.seqprover.ProverFactory.makeProofTree;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.TacticCombinators.Sequence;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Nicolas Beauger
 * 
 */
public class DefaultCombinatorTests {

	private static final StringBuilder trace = new StringBuilder();

	private static class AbstractTracingTactic implements ITactic {
		private final String id;
		private final Object result;

		public AbstractTracingTactic(String id, Object result) {
			this.id = id;
			this.result = result;
		}

		@Override
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			trace.append(id);
			trace.append(';');
			return result;
		}

	}

	private static void assertTrace(String... ids) {
		final StringBuilder sb = new StringBuilder();
		for (String id : ids) {
			sb.append(id);
			sb.append(';');
		}
		assertEquals(sb.toString(), trace.toString());
	}

	public static class TracingSuccess extends AbstractTracingTactic {

		public static String TACTIC_ID = "org.eventb.core.seqprover.tests.tracingSuccess";

		public TracingSuccess() {
			super(TACTIC_ID, null);
		}

	}

	public static class TracingFailure extends AbstractTracingTactic {

		public static String TACTIC_ID = "org.eventb.core.seqprover.tests.tracingFailure";

		public TracingFailure() {
			super(TACTIC_ID, "fail");
		}

	}

	@Before
	public void init() {
		trace.setLength(0);
	}

	private static void assertApply(ITactic tac, String seqStr,
			boolean successExpected) {
		final IProverSequent sequent = genSeq(seqStr);
		final IProofTree tree = makeProofTree(sequent, "test");
		final IProofTreeNode root = tree.getRoot();
		final Object result = tac.apply(root, null);
		if (successExpected) {
			assertNull(result);
		} else {
			assertNotNull(result);
		}

	}

	private static ITactic combine(String combId, String... tacIds) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ICombinatorDescriptor combDesc = reg
				.getCombinatorDescriptor(combId);
		assertNotNull(combDesc);
		final List<ITacticDescriptor> tacs = new ArrayList<ITacticDescriptor>();
		for (String tacId : tacIds) {
			final ITacticDescriptor tacDesc = reg.getTacticDescriptor(tacId);
			tacs.add(tacDesc);
		}
		final ICombinedTacticDescriptor combined = combDesc.combine(tacs,
				"combined");
		return combined.getTacticInstance();
	}

	@Test
	public void testSequenceSucceed() throws Exception {
		final ITactic seqFail = combine(Sequence.COMBINATOR_ID,
				TracingSuccess.TACTIC_ID);
		assertApply(seqFail, "|- ⊥", true);
		assertTrace(TracingSuccess.TACTIC_ID);
	}

	@Test
	public void testSequenceFail() throws Exception {
		final ITactic seqFail = combine(Sequence.COMBINATOR_ID,
				TracingFailure.TACTIC_ID);
		assertApply(seqFail, "|- ⊥", false);
		assertTrace(TracingFailure.TACTIC_ID);
	}

	@Test
	public void testSequenceSuccFail() throws Exception {
		final ITactic seqSuccFail = combine(Sequence.COMBINATOR_ID,
				TracingSuccess.TACTIC_ID, TracingFailure.TACTIC_ID);
		assertApply(seqSuccFail, "|- ⊥", true);
		assertTrace(TracingSuccess.TACTIC_ID, TracingFailure.TACTIC_ID);
	}

	@Test
	public void testSequenceFailSucc() throws Exception {
		final ITactic seqFailSucc = combine(Sequence.COMBINATOR_ID,
				TracingFailure.TACTIC_ID, TracingSuccess.TACTIC_ID);
		assertApply(seqFailSucc, "|- ⊥", true);
		assertTrace(TracingFailure.TACTIC_ID, TracingSuccess.TACTIC_ID);
	}

}
