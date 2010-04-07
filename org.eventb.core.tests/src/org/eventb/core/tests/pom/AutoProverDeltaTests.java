/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.pom;

import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.NATURAL;
import static org.eventb.core.seqprover.IConfidence.DISCHARGED_MAX;
import static org.eventb.core.seqprover.IConfidence.PENDING;
import static org.eventb.core.seqprover.IConfidence.REVIEWED_MAX;
import static org.eventb.core.seqprover.IConfidence.UNATTEMPTED;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.pm.IProofManager;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.tests.BuilderTest;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Tests ensuring that the automated prover is run only when needed.
 * 
 * @author Laurent Voisin
 */
public class AutoProverDeltaTests extends BuilderTest {

	private static final String PO_NAME = "1";

	private static final FormulaFactory ff = FormulaFactory.getDefault();

	/* A goal that is provable automatically. */
	private static final Predicate PROVABLE = ff.makeRelationalPredicate(IN, ff
			.makeIntegerLiteral(BigInteger.ZERO, null), ff
			.makeAtomicExpression(NATURAL, null), null);

	/* A goal that is not provable. */
	private static final Predicate UNPROVABLE = ff.makeLiteralPredicate(BFALSE,
			null);

	/* A goal where the auto-prover will start a proof, but will not finish it. */
	private static final Predicate ATTEMPTABLE = ff.makeAssociativePredicate(
			LAND, new Predicate[] { PROVABLE, UNPROVABLE }, null);
	
	private IPORoot poRoot;
	private IPSRoot psRoot;

	private void createPOFile() throws RodinDBException {
		poRoot = createPOFile("x");
		psRoot = poRoot.getPSRoot();
	}

	private void setPO(Predicate goal, int stamp) throws RodinDBException {
		final IPOSequent poSequent = poRoot.getSequent(PO_NAME);
		if (!poSequent.exists()) {
			poSequent.create(null, null);
		}
		poSequent.setAccuracy(true, null);
		poSequent.setPOStamp(stamp, null);
		final IPOPredicate poGoal = poSequent.getGoal("G");
		if (!poGoal.exists()) {
			poGoal.create(null, null);
		}
		poGoal.setPredicate(goal, null);
		
		// Update the stamp of the file, if necessary
		if (! poRoot.hasPOStamp() || stamp > poRoot.getPOStamp()) {
			poRoot.setPOStamp(stamp, null);
		}
	}

	/**
	 * Creates a proof of the PO making it reviewed.
	 */
	private void setReviewed() throws RodinDBException {
		final IProofManager pm = EventBPlugin.getProofManager();
		final IProofComponent pc = pm.getProofComponent(poRoot);
		final IProofAttempt pa = pc.createProofAttempt(PO_NAME, "test", null);
		final IProofTree proofTree = pa.getProofTree();
		final IProofTreeNode root = proofTree.getRoot();
		Tactics.review(REVIEWED_MAX).apply(root, null);
		assertTrue(proofTree.isClosed());
		pa.commit(true, null);
		pa.dispose();
	}

	/**
	 * Checks that the PS file contains exactly one proof status and with the
	 * expected attributes.
	 */
	private void checkPSFile(int confidence, boolean manualProof, boolean broken)
			throws RodinDBException {
		assertTrue(psRoot.exists());
		final IPSStatus[] psStatuses = psRoot.getStatuses();
		assertEquals(1, psStatuses.length);
		final IPSStatus psStatus = psStatuses[0];
		assertEquals(confidence, psStatus.getConfidence());
		assertEquals(manualProof, psStatus.getHasManualProof());
		assertEquals(broken, psStatus.isBroken());
	}

	/**
	 * Saves the PO file, then runs the builder and check the generated /
	 * modified PS file.
	 */
	protected void runBuilder(boolean attempted, int confidence,
			boolean manualProof, boolean broken) throws CoreException {
		final IRodinFile poFile = poRoot.getRodinFile();
		if (poFile.hasUnsavedChanges()) {
			poFile.save(null, false, false);
		}
		try {
			TracingReasoner.startTracing();
			super.runBuilder();
			assertEquals(attempted, TracingReasoner.getTraces().length != 0);
		} finally {
			TracingReasoner.stopTracing();
		}
		checkPSFile(confidence, manualProof, broken);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// Change the auto-tactic and enable it.
		final IAutoTacticPreference autoPref = EventBPlugin
				.getAutoTacticPreference();
		final List<ITacticDescriptor> descrs = new ArrayList<ITacticDescriptor>();
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		descrs.add(reg.getTacticDescriptor(TracingReasoner.TACTIC_ID));
		descrs
				.add(reg
						.getTacticDescriptor("org.eventb.core.seqprover.clarifyGoalTac"));
		autoPref.setSelectedDescriptors(descrs);
		autoPref.setEnabled(true);
	}

	/**
	 * Ensures that the auto-prover is attempted and can succeed on a new PO.
	 */
	public final void testNewProvable() throws CoreException {
		createPOFile();
		setPO(PROVABLE, 1);
		runBuilder(true, DISCHARGED_MAX, false, false);
	}

	/**
	 * Ensures that the auto-prover is attempted and can fail on a new PO.
	 */
	public final void testNewUnprovable() throws CoreException {
		createPOFile();
		setPO(UNPROVABLE, 1);
		runBuilder(true, UNATTEMPTED, false, false);
	}

	/**
	 * Ensures that the auto-prover is not attempted on a discharged PO that
	 * didn't change (same stamp).
	 */
	public final void testSameStampDischarged() throws CoreException {
		createPOFile();
		setPO(PROVABLE, 1);
		runBuilder(true, DISCHARGED_MAX, false, false);
		runBuilder(false, DISCHARGED_MAX, false, false);
	}

	/**
	 * Ensures that the auto-prover is not attempted on a reviewed PO that
	 * didn't change (same stamp).
	 */
	public final void testSameStampReviewed() throws CoreException {
		createPOFile();
		setPO(UNPROVABLE, 1);
		runBuilder(true, UNATTEMPTED, false, false);
		setReviewed();
		runBuilder(false, REVIEWED_MAX, true, false);
	}

	/**
	 * Ensures that the auto-prover is not attempted on a pending PO that didn't
	 * change (same stamp).
	 */
	public final void testSameStampPending() throws CoreException {
		createPOFile();
		setPO(ATTEMPTABLE, 1);
		runBuilder(true, PENDING, false, false);
		runBuilder(false, PENDING, false, false);
	}

	/**
	 * Ensures that the auto-prover is not attempted on a yet unattempted PO
	 * that didn't change (same stamp).
	 */
	public final void testSameStampUnattempted() throws CoreException {
		createPOFile();
		setPO(UNPROVABLE, 1);
		runBuilder(true, UNATTEMPTED, false, false);
		runBuilder(false, UNATTEMPTED, false, false);
	}

	/**
	 * Ensures that the auto-prover is attempted on a discharged PO that has
	 * changed (different stamp).
	 */
	public final void testChangedStampDischarged() throws CoreException {
		createPOFile();
		setPO(PROVABLE, 1);
		runBuilder(true, DISCHARGED_MAX, false, false);
		setPO(UNPROVABLE, 2);
		runBuilder(true, DISCHARGED_MAX, false, true);
	}

	/**
	 * Ensures that the auto-prover is attempted on a reviewed PO that has
	 * changed (different stamp).
	 */
	public final void testChangedStampReviewed() throws CoreException {
		createPOFile();
		setPO(UNPROVABLE, 1);
		runBuilder(true, UNATTEMPTED, false, false);
		setReviewed();
		runBuilder(false, REVIEWED_MAX, true, false);
		setPO(PROVABLE, 2);
		runBuilder(true, DISCHARGED_MAX, false, false);
	}

	/**
	 * Ensures that the auto-prover is not attempted on a pending PO that has
	 * changed (different stamp).
	 */
	public final void testChangedStampPending() throws CoreException {
		createPOFile();
		setPO(ATTEMPTABLE, 1);
		runBuilder(true, PENDING, false, false);
		runBuilder(false, PENDING, false, false);
		setPO(UNPROVABLE, 2);
		runBuilder(true, PENDING, false, true);
	}

	/**
	 * Ensures that the auto-prover is not attempted on a yet unattempted PO
	 * that has changed (different stamp).
	 */
	public final void testChangedStampUnattempted() throws CoreException {
		createPOFile();
		setPO(UNPROVABLE, 1);
		runBuilder(true, UNATTEMPTED, false, false);
		setPO(ATTEMPTABLE, 2);
		runBuilder(true, PENDING, false, false);
	}

	/**
	 * Ensures that the auto-prover is not attempted on a discharged PO
	 * that has changed (different stamp), but where the proof is reusable.
	 */
	public final void testChangedStampDischargedReusable() throws CoreException {
		createPOFile();
		setPO(PROVABLE, 1);
		runBuilder(true, DISCHARGED_MAX, false, false);
		setPO(PROVABLE, 2);
		runBuilder(false, DISCHARGED_MAX, false, false);
	}

	/**
	 * Ensures that the auto-prover is not attempted on a reviewed PO
	 * that has changed (different stamp), but where the proof is reusable.
	 */
	public final void testChangedStampReviewedReusable() throws CoreException {
		createPOFile();
		setPO(UNPROVABLE, 1);
		runBuilder(true, UNATTEMPTED, false, false);
		setReviewed();
		checkPSFile(REVIEWED_MAX, true, false);
		setPO(UNPROVABLE, 2);
		runBuilder(false, REVIEWED_MAX, true, false);
	}

}
