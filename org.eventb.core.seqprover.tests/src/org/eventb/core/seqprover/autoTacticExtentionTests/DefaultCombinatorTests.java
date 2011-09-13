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
import static org.eventb.core.seqprover.autoTacticExtentionTests.DefaultCombinatorTests.TracingFailure.FAILURE;
import static org.eventb.core.seqprover.autoTacticExtentionTests.DefaultCombinatorTests.TracingSuccess.SUCCESS;
import static org.eventb.core.seqprover.autoTacticExtentionTests.DefaultCombinatorTests.TracingSuccess3.SUCCESS_3;
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
import org.eventb.core.seqprover.eventbExtensions.TacticCombinators.ComposeUntilFailure;
import org.eventb.core.seqprover.eventbExtensions.TacticCombinators.ComposeUntilSuccess;
import org.eventb.core.seqprover.eventbExtensions.TacticCombinators.Loop;
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

		public static String SUCCESS = "org.eventb.core.seqprover.tests.tracingSuccess";

		public TracingSuccess() {
			super(SUCCESS, null);
		}
		
	}

	public static class TracingSuccess3 extends AbstractTracingTactic {

		public static String SUCCESS_3 = "org.eventb.core.seqprover.tests.tracingSuccess3";

		private static final int LIMIT = 3;
		private int current = 0;

		public TracingSuccess3() {
			super(SUCCESS_3, null);
		}

		@Override
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			if (current == LIMIT) {
				return "failure after " + LIMIT + " applications";
			}
			current++;
			return super.apply(ptNode, pm);
		}

	}
	
	
	public static class TracingFailure extends AbstractTracingTactic {

		public static String FAILURE = "org.eventb.core.seqprover.tests.tracingFailure";

		public TracingFailure() {
			super(FAILURE, "fail");
		}

	}

	@Before
	public void init() {
		trace.setLength(0);
	}

	private static void assertApply(ITactic tac, boolean successExpected,
			String... traceIds) {
		final IProverSequent sequent = genSeq("|- ⊥");
		final IProofTree tree = makeProofTree(sequent, "test");
		final IProofTreeNode root = tree.getRoot();
		final Object result = tac.apply(root, null);
		if (successExpected) {
			assertNull(result);
		} else {
			assertNotNull(result);
		}
		assertTrace(traceIds);
	}

	private static ICombinatorDescriptor getCombDesc(String combId) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ICombinatorDescriptor combDesc = reg
				.getCombinatorDescriptor(combId);
		assertNotNull(combDesc);
		return combDesc;
	}
	
	private static ITactic combine(String combId, String... tacIds) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ICombinatorDescriptor combDesc = getCombDesc(combId);
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
		final ITactic seqFail = combine(Sequence.COMBINATOR_ID, SUCCESS);
		assertApply(seqFail, true, SUCCESS);
	}

	@Test
	public void testSequenceFail() throws Exception {
		final ITactic seqFail = combine(Sequence.COMBINATOR_ID, FAILURE);
		assertApply(seqFail, false, FAILURE);
	}

	@Test
	public void testSequenceSuccFail() throws Exception {
		final ITactic seqSuccFail = combine(Sequence.COMBINATOR_ID, SUCCESS,
				FAILURE);
		assertApply(seqSuccFail, true, SUCCESS, FAILURE);
	}

	@Test
	public void testSequenceFailSucc() throws Exception {
		final ITactic seqFailSucc = combine(Sequence.COMBINATOR_ID, FAILURE,
				SUCCESS);
		assertApply(seqFailSucc, true, FAILURE, SUCCESS);
	}

	@Test
	public void testCompSucc_Succ() throws Exception {
		final ITactic compSucc = combine(ComposeUntilSuccess.COMBINATOR_ID,
				SUCCESS);
		assertApply(compSucc, true, SUCCESS);
	}

	@Test
	public void testCompSucc_Fail() throws Exception {
		final ITactic compFail = combine(ComposeUntilSuccess.COMBINATOR_ID,
				FAILURE);
		assertApply(compFail, false, FAILURE);
	}

	@Test
	public void testCompSucc_SuccFail() throws Exception {
		final ITactic compSuccFail = combine(ComposeUntilSuccess.COMBINATOR_ID,
				SUCCESS, FAILURE);
		assertApply(compSuccFail, true, SUCCESS);
	}

	@Test
	public void testCompSucc_FailSucc() throws Exception {
		final ITactic compFailSucc = combine(ComposeUntilSuccess.COMBINATOR_ID,
				FAILURE, SUCCESS);
		assertApply(compFailSucc, true, FAILURE, SUCCESS);
	}

	@Test
	public void testCompFail_Succ() throws Exception {
		final ITactic compSucc = combine(ComposeUntilFailure.COMBINATOR_ID,
				SUCCESS);
		assertApply(compSucc, true, SUCCESS);
	}

	@Test
	public void testCompFail_Fail() throws Exception {
		final ITactic compFail = combine(ComposeUntilFailure.COMBINATOR_ID,
				FAILURE);
		assertApply(compFail, false, FAILURE);
	}

	@Test
	public void testCompFail_SuccFail() throws Exception {
		final ITactic compSuccFail = combine(ComposeUntilFailure.COMBINATOR_ID,
				SUCCESS, FAILURE);
		assertApply(compSuccFail, true, SUCCESS, FAILURE);
	}

	@Test
	public void testCompFail_FailSucc() throws Exception {
		final ITactic compFailSucc = combine(ComposeUntilFailure.COMBINATOR_ID,
				FAILURE, SUCCESS);
		assertApply(compFailSucc, false, FAILURE);
	}

	@Test
	public void testLoop_Succ() throws Exception {
		final ITactic loopSucc = combine(Loop.COMBINATOR_ID,
				SUCCESS_3);
		assertApply(loopSucc, true, SUCCESS_3, SUCCESS_3, SUCCESS_3);
	}

	@Test
	public void testLoop_Fail() throws Exception {
		final ITactic loopFail = combine(Loop.COMBINATOR_ID,
				FAILURE);
		assertApply(loopFail, false, FAILURE);
	}

}
