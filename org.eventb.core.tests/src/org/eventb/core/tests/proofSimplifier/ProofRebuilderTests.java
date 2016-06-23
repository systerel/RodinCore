/*******************************************************************************
 * Copyright (c) 2010, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.proofSimplifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.core.tests.pog.EventBPOTest;
import org.eventb.core.tests.pom.ContextDependentReasoner;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

/**
 * @author Nicolas Beauger
 * 
 */
public class ProofRebuilderTests extends EventBPOTest {

	private static final String GOAL = "∀x⦂ℤ·∃y·x=y";

	private static void assertDischargedClosed(IPSStatus status, int confidence) throws CoreException {
		assertFalse(status.isBroken());
		assertEquals(confidence, status.getConfidence());
		final IPRProof proof = status.getProof();
		final IProofTree proofTree = proof.getProofTree(null);
		assertNotNull(proofTree);
		assertTrue(proofTree.isClosed());
	}

	private static void assertNotDischargedNotClosed(IPSStatus status) throws CoreException {
		assertFalse(status.isBroken());
		assertTrue(status.getConfidence() == IConfidence.PENDING);
		final IPRProof proof = status.getProof();
		final IProofTree proofTree = proof.getProofTree(null);
		assertNotNull(proofTree);
		assertFalse(proofTree.isClosed());
	}

	private IPSStatus getOnlyStatus() throws RodinDBException {
		final IPSRoot[] statusFiles = rodinProject.getRootElementsOfType(IPSRoot.ELEMENT_TYPE);
		assertEquals(1, statusFiles.length);
		final IPSRoot statusRoot = statusFiles[0];
		final IPSStatus[] statuses = statusRoot.getStatuses();
		assertEquals(1, statuses.length);
		return statuses[0];
	}

	private IAxiom createTheorem(String thmLabel, String thmString) throws Exception {
		final IContextRoot ctx = createContext("C");
		addAxioms(ctx, makeSList(thmLabel), makeSList(thmString), makeBList(true));
		saveRodinFileOf(ctx);
		return ctx.getAxioms()[0];
	}

	private void prove(boolean assertClosed, ITactic... tactics) throws RodinDBException {
		final IPSStatus status = getOnlyStatus();
		final IUserSupportManager usm = EventBPlugin.getUserSupportManager();
		final IUserSupport us = usm.newUserSupport();
		us.setInput((IPSRoot) status.getRoot());
		us.setCurrentPO(status, null);
		final IProofState po = us.getCurrentPO();
		assertNotNull(po);
		final IProofTreeNode node = po.getCurrentNode();
		assertNotNull(node);
		for (ITactic tactic : tactics) {
			us.applyTactic(tactic, false, null);
		}
		us.doSave(us.getUnsavedPOs(), null);
		if (assertClosed) {
			assertTrue(po.isClosed());
		}
		us.dispose();
	}

	private void doTest(String goal, boolean tacticsClose, boolean applyPostTactic, boolean eventuallyClosed,
			ITactic... tactics) throws Exception {
		// create context and PO for ∀ x oftype ℤ· ∃ y · x=y
		final IAxiom thm = createTheorem("axm", goal);
		// build
		runBuilder();
		// prove (free x, y inst x, true goal, simpl rewrite, true goal)
		prove(tacticsClose, tactics);
		// change predicate into ∀ x · ∃ y · y=x
		thm.setPredicateString("∀x⦂ℤ·∃y·y=x", null);
		saveRodinFileOf(thm);
		// build
		runBuilder();
		// check proof broken
		final IPSStatus status = getOnlyStatus();
		assertTrue(status.isBroken());
		// rebuild
		final boolean success = EventBPlugin.rebuildProof(status.getProof(), applyPostTactic, null);
		// verify that rebuild worked properly
		assertTrue(success);
		if (eventuallyClosed) {
			assertDischargedClosed(status, IConfidence.DISCHARGED_MAX);
		} else {
			assertNotDischargedNotClosed(status);
		}

	}

	@Test
	public void testRebuild() throws Exception {
		// given tactics close the proof tree
		// do NOT apply post tactics
		// eventually, the proof tree is closed
		doTest(GOAL, true, false, true, Tactics.allI(), Tactics.exI("x"), new AutoTactics.TrueGoalTac(),
				new AutoTactics.AutoRewriteTac(), new AutoTactics.TrueGoalTac());
	}

	@Test
	public void testRebuildWithPostTacticDisabled() throws Exception {
		disablePostTactic();
		// given tactics do NOT close the proof tree
		// do not apply post tactics
		// eventually, the proof tree is NOT closed
		doTest(GOAL, false, false, false, Tactics.allI(), Tactics.exI("x"));
	}

	@Test
	public void testRebuildWithPostTacticEnabled() throws Exception {
		enablePostTactic();
		// given tactics do NOT close the proof tree
		// do apply post tactics
		// eventually, the proof tree is closed
		doTest(GOAL, false, true, true, Tactics.allI(), Tactics.exI("x"));
	}

	/**
	 * Verify that proofs with context dependent reasoners are rebuilt as
	 * uncertain when the context is no more valid.
	 */
	@Test
	public void testContextDependentReasoner() throws Exception {
		// create context and PO for ∀ x oftype ℤ· ∃ y · x=y
		final IAxiom thm = createTheorem("axm", GOAL);
		// build
		runBuilder();
		// prove (ContextDependentReasoner, true goal)
		ContextDependentReasoner.setContextValidity(true);
		prove(true, BasicTactics.reasonerTac(new ContextDependentReasoner(), new EmptyInput()),
				new AutoTactics.TrueGoalTac());
		final IPSStatus status = getOnlyStatus();
		assertDischargedClosed(status, IConfidence.DISCHARGED_MAX);
		assertFalse(status.isBroken());

		// context becomes invalid
		ContextDependentReasoner.setContextValidity(false);
		// change a file the proof status depends on, just like
		// org.eventb.theory.internal.core.util.DeployedStatusUpdater does
		((IEventBRoot) thm.getRoot()).getPORoot().getResource().touch(null);
		// build
		runBuilder();
		// check proof broken
		assertTrue(status.isBroken());
		// rebuild
		final boolean successInvalid = EventBPlugin.rebuildProof(status.getProof(), false, null);
		// verify that rebuild worked properly
		assertTrue(successInvalid);
		assertDischargedClosed(getOnlyStatus(), IConfidence.UNCERTAIN_MAX);

		// context becomes valid again
		ContextDependentReasoner.setContextValidity(true);
		// change a file the proof status depends on, just like
		// org.eventb.theory.internal.core.util.DeployedStatusUpdater does
		((IEventBRoot) thm.getRoot()).getPORoot().getResource().touch(null);
		// build
		runBuilder();
		// check proof still broken
		assertTrue(status.isBroken());
		// rebuild
		final boolean successValid = EventBPlugin.rebuildProof(status.getProof(), false, null);
		// verify that rebuild worked properly
		assertTrue(successValid);
		assertDischargedClosed(getOnlyStatus(), IConfidence.DISCHARGED_MAX);
		// check proof no more broken
		assertFalse(status.isBroken());
	}

}
