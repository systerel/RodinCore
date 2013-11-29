/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.pm;

import static org.junit.Assert.*;
import static org.eventb.core.EventBAttributes.POSTAMP_ATTRIBUTE;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.seqprover.IConfidence.DISCHARGED_MAX;
import static org.eventb.core.seqprover.IConfidence.UNATTEMPTED;
import static org.eventb.core.tests.extension.PrimeFormulaExtensionProvider.EXT_FACTORY;
import static org.eventb.core.tests.pom.POUtil.addPredicateSet;
import static org.eventb.core.tests.pom.POUtil.addSequent;
import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import java.math.BigInteger;

import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.tests.DeltaListener;
import org.eventb.core.tests.extension.PrimeFormulaExtensionProvider;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

/**
 * Unit tests for Proof Components.
 * 
 * @author Laurent Voisin
 */
public class ProofAttemptTests extends AbstractProofTests {

	private static final Predicate GOAL;
	private static final Predicate GHYP;
	private static final Predicate LHYP;

	static {
		final Expression zero = ff.makeIntegerLiteral(BigInteger.ZERO, null);
		final Expression one = ff.makeIntegerLiteral(BigInteger.ONE, null);
		GOAL = ff.makeLiteralPredicate(BTRUE, null);
		GHYP = ff.makeRelationalPredicate(EQUAL, zero, zero, null);
		LHYP = ff.makeRelationalPredicate(EQUAL, one, one, null);
	}

	private IPORoot poRoot;

	private IProofComponent pc;

	@Before
	public void createProofComponent() throws Exception {
		createPOFile();
		runBuilder();
		pc = pm.getProofComponent(poRoot);
	}

	/**
	 * Ensures that creating a proof attempt for an existing PO returns an
	 * object with the appropriate properties and that the proof obligation has
	 * been properly loaded.
	 */
	@Test
	public void testAccessors() throws Exception {
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		assertEquals(pc, pa.getComponent());
		assertEquals(PO1, pa.getName());
		assertEquals(TEST, pa.getOwner());
		assertEquals(pc.getStatus(PO1), pa.getStatus());

		final IProofTree pt = pa.getProofTree();
		final IProverSequent sequent = pt.getSequent();
		assertEquals(GOAL, sequent.goal());
		assertEquals(mSet(GHYP, LHYP), mSet(sequent.hypIterable()));
	}

	/**
	 * Ensures that the isDisposed() method works properly.
	 */
	@Test
	public void testIsDisposed() throws Exception {
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		assertFalse(pa.isDisposed());
		pa.dispose();
		assertTrue(pa.isDisposed());
	}

	/**
	 * Ensures that a proof attempt is not broken if the PO didn't change.
	 */
	@Test
	public void testNotBroken() throws Exception {
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		assertFalse(pa.isBroken());
	}

	/**
	 * Ensures that a proof attempt is not broken if the PO didn't change when
	 * the PO didn't have a stamp initially.
	 */
	@Test
	public void testNotBrokenNoStamp() throws Exception {
		removePOStamps();
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		assertFalse(pa.isBroken());
	}

	/**
	 * Ensures that a proof attempt is not broken if another PO changes.
	 */
	@Test
	public void testNotBrokenOtherPOChanges() throws Exception {
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		increasePOStamp(PO2);
		assertFalse(pa.isBroken());
	}

	/**
	 * Ensures that a proof attempt is broken if the PO changed.
	 */
	@Test
	public void testBroken() throws Exception {
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		increasePOStamp(PO1);
		runBuilder();
		assertTrue(pa.isBroken());
	}

	/**
	 * Ensures that a proof attempt is broken if the PO changed when the PO
	 * didn't have a stamp initially.
	 */
	@Test
	public void testBrokenNoStamp() throws Exception {
		removePOStamps();
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		increasePOStamp(PO1);
		runBuilder();
		assertTrue(pa.isBroken());
	}

	/**
	 * Ensures that a proof attempt is broken if the PO disappeared.
	 */
	@Test
	public void testBrokenNoPO() throws Exception {
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		poRoot.getSequent(PO1).delete(false, null);
		saveRodinFileOf(poRoot);
		runBuilder();
		assertTrue(pa.isBroken());
	}

	/**
	 * Ensures that a proof attempt is broken if the project has been cleaned.
	 */
	@Test
	public void testBrokenClean() throws Exception {
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		poRoot.getRodinFile().delete(false, null);
		createPOFile();
		runBuilder();
		assertTrue(pa.isBroken());
	}

	/**
	 * Ensures that one can commit an empty proof attempt successfully.
	 */
	@Test
	public void testCommitEmpty() throws Exception {
		final DeltaListener dl = new DeltaListener();
		try {
			dl.start();
			final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
			pa.commit(true, null);
			dl.assertDeltas("Unexpected deltas for proof commit",
					"P[*]: {CHILDREN}\n" + 
					"	m.bpr[*]: {CHILDREN}\n" + 
					"		m[org.eventb.core.prFile][*]: {CHILDREN}\n" + 
					"			PO1[org.eventb.core.prProof][*]: {ATTRIBUTE}\n" + 
					"	m.bps[*]: {CHILDREN}\n" + 
					"		m[org.eventb.core.psFile][*]: {CHILDREN}\n" + 
					"			PO1[org.eventb.core.psStatus][*]: {ATTRIBUTE}"
			);
			assertEmptyProof(pc.getProofSkeleton(PO1, ff, null));
			assertStatus(UNATTEMPTED, false, true, pc.getStatus(PO1));
		} finally {
			dl.stop();
		}
	}

	/**
	 * Ensures that one can commit a discharging proof attempt successfully.
	 */
	@Test
	public void testCommitDischarge() throws Exception {
		final DeltaListener dl = new DeltaListener();
		try {
			dl.start();
			final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
			dischargeTrueGoal(pa);
			pa.commit(true, null);
			dl.assertDeltas(
					"Unexpected deltas for proof commit",
					"P[*]: {CHILDREN}\n" + 
					"	m.bpr[*]: {CHILDREN}\n" + 
					"		m[org.eventb.core.prFile][*]: {CHILDREN}\n" + 
					"			PO1[org.eventb.core.prProof][*]: {CHILDREN | ATTRIBUTE}\n" + 
					"				L[org.eventb.core.lang][+]: {}\n" +
					"				r0[org.eventb.core.prRule][+]: {}\n" + 
					"				p0[org.eventb.core.prPred][+]: {}\n" + 
					"				r0[org.eventb.core.prReas][+]: {}\n" + 
					"	m.bps[*]: {CHILDREN}\n" + 
					"		m[org.eventb.core.psFile][*]: {CHILDREN}\n" + 
					"			PO1[org.eventb.core.psStatus][*]: {ATTRIBUTE}");
			assertNonEmptyProof(pc.getProofSkeleton(PO1, ff, null));
			assertStatus(DISCHARGED_MAX, false, true, pc.getStatus(PO1));
		} finally {
			dl.stop();
		}
	}

	/**
	 * Ensures that one can commit a discharging proof attempt successfully,
	 * even if the proof attempt is broken.
	 */
	@Test
	public void testCommitBroken() throws Exception {
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		dischargeTrueGoal(pa);
		increasePOStamp(PO1);
		pa.commit(true, null);
		assertNonEmptyProof(pc.getProofSkeleton(PO1, ff, null));
		assertStatus(DISCHARGED_MAX, true, true, pc.getStatus(PO1));
	}

	/**
	 * Ensures that one can commit a discharging proof attempt successfully,
	 * even if the proof obligation has no stamp.
	 */
	@Test
	public void testCommitNoStamp() throws Exception {
		removePOStamps();
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		dischargeTrueGoal(pa);
		pa.commit(true, null);
		assertNonEmptyProof(pc.getProofSkeleton(PO1, ff, null));
		assertStatus(DISCHARGED_MAX, false, true, pc.getStatus(PO1));
	}

	/**
	 * Ensures that one can commit a proof attempt and save the proof file, even
	 * if the formula factory has changed since the creation of the attempt.
	 * However, the proof is considered broken.
	 */
	@Test
	public void commitPOFactoryChanged() throws Exception {
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		dischargeTrueGoal(pa);
		
		PrimeFormulaExtensionProvider.add(poRoot);
		assertTrue(pa.isBroken());
		
		pa.commit(true, null);
		assertClosedBrokenManualProof(pc, PO1);
		
		pc.save(null, false);
		assertClosedBrokenManualProof(pc, PO1);
	}

	/**
	 * Ensures that one cannot read the skeleton of a proof with a different
	 * formula factory and that a proof attempt does not read it either.
	 */
	@Test
	public void cannotReadSkeletonFactoryChanged() throws Exception {
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		dischargeTrueGoal(pa);
		pa.commit(true, null);
		pa.dispose();
		
		PrimeFormulaExtensionProvider.add(poRoot);
		assertNull(pc.getProofSkeleton(PO1, EXT_FACTORY, null));

		final IProofAttempt pa2 = pc.createProofAttempt(PO1, TEST, null);
		assertSame(EXT_FACTORY , pa2.getFormulaFactory());
		final IProofTree tree = pa2.getProofTree();
		assertFalse(tree.proofAttempted());
	}

	private void createPOFile() throws RodinDBException {
		poRoot = createPOFile("m");
		final ITypeEnvironment typenv = mTypeEnvironment();
		final IPOPredicateSet hyp = addPredicateSet(poRoot, "hyp", null,
				typenv, GHYP.toString());
		addSequent(poRoot, PO1, GOAL.toString(), hyp, typenv, LHYP.toString());
		addSequent(poRoot, PO2, GOAL.toString(), hyp, typenv, LHYP.toString());
		saveRodinFileOf(poRoot);
	}

	private void dischargeTrueGoal(final IProofAttempt pa) {
		final IProofTreeNode root = pa.getProofTree().getRoot();
		ITactic tactic = new AutoTactics.TrueGoalTac();
		tactic.apply(root, null);
		assertTrue(root.isClosed());
	}

	private void increasePOStamp(String poName) throws RodinDBException {
		final IPOSequent sequent = poRoot.getSequent(poName);
		final long stamp;
		if (sequent.hasPOStamp()) {
			stamp = sequent.getPOStamp();
		} else {
			stamp = 0;
		}
		sequent.setPOStamp(stamp + 1, null);
		poRoot.setPOStamp(stamp + 1, null);
	}

	private void removePOStamps() throws RodinDBException {
		poRoot.removeAttribute(POSTAMP_ATTRIBUTE, null);
		for (IPOSequent s : poRoot.getSequents()) {
			s.removeAttribute(POSTAMP_ATTRIBUTE, null);
		}
	}

}
