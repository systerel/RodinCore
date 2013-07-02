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
package org.eventb.core.seqprover.autoTacticExtentionTests;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.eventb.core.seqprover.ProverFactory.makeProofTree;
import static org.eventb.core.seqprover.autoTacticExtentionTests.DefaultCombinatorTests.TracingDischarge.DISCHARGE;
import static org.eventb.core.seqprover.autoTacticExtentionTests.DefaultCombinatorTests.TracingFailure.FAILURE;
import static org.eventb.core.seqprover.autoTacticExtentionTests.DefaultCombinatorTests.TracingSuccess.SUCCESS;
import static org.eventb.core.seqprover.autoTacticExtentionTests.DefaultCombinatorTests.TracingSuccess3.SUCCESS_3;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.TacticCombinators.Attempt;
import org.eventb.core.seqprover.eventbExtensions.TacticCombinators.ComposeUntilFailure;
import org.eventb.core.seqprover.eventbExtensions.TacticCombinators.ComposeUntilSuccess;
import org.eventb.core.seqprover.eventbExtensions.TacticCombinators.Loop;
import org.eventb.core.seqprover.eventbExtensions.TacticCombinators.OnAllPending;
import org.eventb.core.seqprover.eventbExtensions.TacticCombinators.Sequence;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;
import org.eventb.core.seqprover.tactics.BasicTactics;
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
		private static int current = 0;

		public TracingSuccess3() {
			super(SUCCESS_3, null);
		}

		@Override
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			final Object result = super.apply(ptNode, pm);
			if (current >= LIMIT) {
				return "failure after " + LIMIT + " applications";
			}
			current++;
			return result;
		}

		public static void reset() {
			current = 0;
		}
	}

	public static class TracingFailure extends AbstractTracingTactic {

		public static String FAILURE = "org.eventb.core.seqprover.tests.tracingFailure";

		public TracingFailure() {
			super(FAILURE, "fail");
		}

	}
	
	public static class TracingDischarge extends AbstractTracingTactic {
		
		private static class DischargeReasoner extends EmptyInputReasoner {

			@Override
			public String getReasonerID() {
				return "discharge";
			}

			@Override
			public IReasonerOutput apply(IProverSequent seq,
					IReasonerInput input, IProofMonitor pm) {
				return ProverFactory.makeProofRule(this,
						input, null, "discharge anything");
			}
			
		}
		
		public static String DISCHARGE = "org.eventb.core.seqprover.tests.tracingDischarge";

		public TracingDischarge() {
			super(DISCHARGE, null);
		}

		@Override
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			final ITactic tac = BasicTactics.reasonerTac(new DischargeReasoner(), new EmptyInput());
			tac.apply(ptNode, pm);
			return super.apply(ptNode, pm);
		}
	}
	
	@Before
	public void init() {
		trace.setLength(0);
		TracingSuccess3.reset();
	}

	private static IProofTreeNode makeSimpleNode() {
		return makeSimpleNode("⊥");
	}

	private static IProofTreeNode makeSimpleNode(String goal) {
		final IProverSequent sequent = genSeq("|- " + goal);
		final IProofTree tree = makeProofTree(sequent, "test");
		final IProofTreeNode root = tree.getRoot();
		return root;
	}

	private static IProofTreeNode makeSplit4Node() {
		final IProofTreeNode node = makeSimpleNode("a=TRUE ∧ b=FALSE ∧ c=TRUE ∧ d=FALSE");
		final Object result = Tactics.conjI().apply(node, null);
		assertNull(result);
		return node;
	}

	private static void assertApply(ITactic tac, boolean successExpected,
			String... traceIds) {
		final IProofTreeNode root = makeSimpleNode();
		assertApply(tac, root, successExpected, traceIds);
	}

	private static void assertApply(ITactic tac, IProofTreeNode node,
			boolean successExpected, String... traceIds) {
		final Object result = tac.apply(node, null);
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

	private static ITacticDescriptor getTacDesc(String tacId) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ITacticDescriptor tacDesc = reg.getTacticDescriptor(tacId);
		assertNotNull(tacDesc);
		return tacDesc;
	}

	private static ITactic combine(String combId, String... tacIds) {
		final ICombinatorDescriptor combDesc = getCombDesc(combId);
		final List<ITacticDescriptor> tacs = new ArrayList<ITacticDescriptor>();
		for (String tacId : tacIds) {
			final ITacticDescriptor tacDesc = getTacDesc(tacId);
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
		final ITactic loopSucc = combine(Loop.COMBINATOR_ID, SUCCESS_3);
		assertApply(loopSucc, true, SUCCESS_3, SUCCESS_3, SUCCESS_3, SUCCESS_3);
	}

	@Test
	public void testLoop_Fail() throws Exception {
		final ITactic loopFail = combine(Loop.COMBINATOR_ID, FAILURE);
		assertApply(loopFail, false, FAILURE);
	}

	@Test
	public void testOnAllPending_Success() throws Exception {
		final IProofTreeNode node = makeSplit4Node();
		final ITactic onAllPendingSuccess = combine(OnAllPending.COMBINATOR_ID,
				SUCCESS_3);
		assertApply(onAllPendingSuccess, node, true, SUCCESS_3, SUCCESS_3,
				SUCCESS_3, SUCCESS_3);
	}

	@Test
	public void testOnAllPending_Failure() throws Exception {
		final IProofTreeNode node = makeSplit4Node();
		final ITactic onAllPendingSuccess = combine(OnAllPending.COMBINATOR_ID,
				FAILURE);
		assertApply(onAllPendingSuccess, node, false, FAILURE, FAILURE,
				FAILURE, FAILURE);
	}
	
	@Test
	public void testAttempt_Success() throws Exception {
		final ITactic attemptSucc = combine(Attempt.COMBINATOR_ID, SUCCESS);
		// SUCCESS lets the tree unchanged, so the node is still undischarged
		assertApply(attemptSucc, false, SUCCESS);
	}
	
	@Test
	public void testAttempt_Failure() throws Exception {
		final ITactic attemptFail = combine(Attempt.COMBINATOR_ID, FAILURE);
		// FAILURE lets the tree unchanged, so the node is still undischarged
		assertApply(attemptFail, false, FAILURE);
	}
	
	@Test
	public void testAttempt_Discharge() throws Exception {
		final ITactic attemptDisch = combine(Attempt.COMBINATOR_ID, DISCHARGE);
		assertApply(attemptDisch, true, DISCHARGE);
	}
	
	@Test
	public void testAttempt_OnAllPendingDischarge() throws Exception {
		final IProofTreeNode node = makeSplit4Node();
		final ICombinatorDescriptor attempt = getCombDesc(Attempt.COMBINATOR_ID);
		final ICombinatorDescriptor onAllPending = getCombDesc(OnAllPending.COMBINATOR_ID);
		final ITacticDescriptor discharge = getTacDesc(DISCHARGE);
		final ICombinedTacticDescriptor onAllPendingDischarge = onAllPending
				.combine(singletonList(discharge), "onAllPendingDischarge");
		final ICombinedTacticDescriptor attemptOnAllPendingDischarge = attempt
				.combine(
						Collections
								.<ITacticDescriptor> singletonList(onAllPendingDischarge),
						"attemptOnAllPendingDischarge");
		assertApply(attemptOnAllPendingDischarge.getTacticInstance(), node,
				true, DISCHARGE, DISCHARGE, DISCHARGE, DISCHARGE);
	}

	@Test
	public void testAttempt_OnAllPendingSuccess() throws Exception {
		final IProofTreeNode node = makeSplit4Node();
		final ICombinatorDescriptor attempt = getCombDesc(Attempt.COMBINATOR_ID);
		final ICombinatorDescriptor onAllPending = getCombDesc(OnAllPending.COMBINATOR_ID);
		final ITacticDescriptor success = getTacDesc(SUCCESS);
		final ICombinedTacticDescriptor onAllPendingDischarge = onAllPending
				.combine(singletonList(success), "onAllPendingSuccess");
		final ICombinedTacticDescriptor attemptOnAllPendingSuccess = attempt
				.combine(
						Collections
								.<ITacticDescriptor> singletonList(onAllPendingDischarge),
						"attemptOnAllPendingSuccess");
		assertApply(attemptOnAllPendingSuccess.getTacticInstance(), node,
				false, SUCCESS, SUCCESS, SUCCESS, SUCCESS);
	}
}
