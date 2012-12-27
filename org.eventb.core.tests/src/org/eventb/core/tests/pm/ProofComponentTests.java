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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.eventb.core.tests.pom.POUtil.addSequent;
import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IProofComponent;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Unit tests for Proof Components.
 * 
 * @author Laurent Voisin
 */
public class ProofComponentTests extends AbstractProofTests {

	private static final String OTHER = "other"; //$NON-NLS-1$

	private static final String PO3 = "PO3"; //$NON-NLS-1$

	protected static final String NO_PO = "NO_PO"; //$NON-NLS-1$

	private static final String[] PO_NAMES = new String[] { PO1, PO2, PO3,
			NO_PO };
	private static final String[] OWNERS = new String[] { TEST, OTHER };

	private IMachineRoot mchroot;
	private ISCMachineRoot scRoot;
	private IPORoot poRoot;
	private IPRRoot prRoot;
	private IPSRoot psRoot;

	private IProofComponent pc;

	private static void assertEqualsPA(Set<IProofAttempt> expSet, IProofAttempt[] actual) {
		assertEquals(expSet, mSet(actual));
	}

	private void assertLivePAs(IProofAttempt... expected) {
		final Set<IProofAttempt> expSet = mSet(expected);
		assertEqualsPA(expSet, pm.getProofAttempts());
		assertEqualsPA(expSet, pc.getProofAttempts());
		for (final IProofAttempt pa : expected) {
			final String name = pa.getName();
			assertEqualsPA(filter(expSet, name), pc.getProofAttempts(name));
		}
		for (final String poName : PO_NAMES) {
			for (final String owner : OWNERS) {
				assertEquals(filter(expSet, poName, owner),//
						pc.getProofAttempt(poName, owner));
			}
		}
	}

	private void createPOFile() throws RodinDBException {
		poRoot.getRodinFile().create(true, null);
		addSequent(poRoot, PO1, "⊤", null, mTypeEnvironment()); //$NON-NLS-1$
		addSequent(poRoot, PO2, "⊥", null, mTypeEnvironment()); //$NON-NLS-1$
		saveRodinFileOf(poRoot);
	}

	private Set<IProofAttempt> filter(Set<IProofAttempt> set, String poName) {
		final Set<IProofAttempt> res = new HashSet<IProofAttempt>(set);
		final Iterator<IProofAttempt> it = res.iterator();
		while (it.hasNext()) {
			if (!poName.equals(it.next().getName())) {
				it.remove();
			}
		}
		return res;
	}

	private IProofAttempt filter(Set<IProofAttempt> set, String poName,
			String owner) {
		for (final IProofAttempt pa : set) {
			if (poName.equals(pa.getName()) && owner.equals(pa.getOwner()))
				return pa;
		}
		return null;
	}

	@Before
	public void createProofComponent() throws Exception {
		mchroot = eventBProject.getMachineRoot("m");
		scRoot = mchroot.getSCMachineRoot();
		poRoot = mchroot.getPORoot();
		prRoot = mchroot.getPRRoot();
		psRoot = mchroot.getPSRoot();
		pc = pm.getProofComponent(mchroot);
	}

	/**
	 * Ensures that one can create twice the same proof attempt, when the first
	 * is disposed in the meantime.
	 */
	@Test
	public void testCreateDisposeCreate() throws Exception {
		createPOFile();
		runBuilder();
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		pa.dispose();
		final IProofAttempt pa2 = pc.createProofAttempt(PO1, TEST, null);
		assertLivePAs(pa2);
	}

	/**
	 * Ensures that one can create a proof attempt for an existing PO.
	 */
	@Test
	public void testCreateProofAttempt() throws Exception {
		createPOFile();
		runBuilder();
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		assertNotNull(pa);
		assertLivePAs(pa);
	}

	/**
	 * Ensures that one can not create a proof attempt when there is no PO file.
	 */
	@Test
	public void testCreateProofAttemptNoPOFile() throws Exception {
		try {
			pc.createProofAttempt(PO1, TEST, null);
			fail("Should have raised a Rodin exception");
		} catch (RodinDBException e) {
			assertTrue(e.isDoesNotExist());
			final IRodinElement[] elems = e.getRodinDBStatus().getElements();
			assertEquals(mSet(poRoot.getRodinFile()), mSet(elems));
		}
	}

	/**
	 * Ensures that one can not create a proof attempt when there is no PO
	 * sequent.
	 */
	@Test
	public void testCreateProofAttemptNoPOSequent() throws Exception {
		createPOFile();
		runBuilder();
		try {
			pc.createProofAttempt(NO_PO, TEST, null);
			fail("Should have raised a Rodin exception");
		} catch (RodinDBException e) {
			assertTrue(e.isDoesNotExist());
			final IRodinElement[] elems = e.getRodinDBStatus().getElements();
			assertEquals(mSet(poRoot.getSequent(NO_PO)), mSet(elems));
		}
	}

	/**
	 * Ensures that one can create a proof attempt for an existing PO, then the
	 * proof attempt is still there even if the PO disappears.
	 */
	@Test
	public void testCreateProofAttemptThenRemovePO() throws Exception {
		createPOFile();
		runBuilder();
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		assertLivePAs(pa);

		poRoot.getSequent(PO1).delete(false, null);
		saveRodinFileOf(poRoot);
		runBuilder();
		assertLivePAs(pa);
	}

	/**
	 * Ensures that one can not create twice the same proof attempt.
	 */
	@Test
	public void testCreateSameTwice() throws Exception {
		createPOFile();
		runBuilder();
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		try {
			pc.createProofAttempt(PO1, TEST, null);
			fail("Should have raised an exception");
		} catch (IllegalStateException e) {
			// OK
		}
		assertLivePAs(pa);
	}

	/**
	 * Ensures that one can create two proof attempts for two existing POs with
	 * the same owners.
	 */
	@Test
	public void testCreateTwoSameOwner() throws Exception {
		createPOFile();
		runBuilder();
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		final IProofAttempt pa2 = pc.createProofAttempt(PO2, TEST, null);
		assertNotNull(pa2);
		assertNotSame(pa, pa2);
		assertLivePAs(pa, pa2);
	}

	/**
	 * Ensures that one can create two proof attempts for an existing PO with
	 * different owners.
	 */
	@Test
	public void testCreateTwoSamePO() throws Exception {
		createPOFile();
		runBuilder();
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		final IProofAttempt pa2 = pc.createProofAttempt(PO1, OTHER, null);
		assertNotNull(pa2);
		assertNotSame(pa, pa2);
		assertLivePAs(pa, pa2);
	}

	/**
	 * Ensures that the three files associated to a proof component can be
	 * retrieved.
	 */
	@Test
	public void testProofFiles() throws Exception {
		assertEquals(psRoot, pc.getPSRoot());
		assertEquals(poRoot, pc.getPORoot());
		assertEquals(prRoot, pc.getPRRoot());
	}

	/**
	 * Ensures that one can access to a saved proof (here an empty one).
	 */
	@Test
	public void testProofSkeleton() throws Exception {
		createPOFile();
		runBuilder();
		assertEmptyProof(pc.getProofSkeleton(PO1, ff, null));
	}

	/**
	 * Ensures that one can access to a proof status.
	 */
	@Test
	public void testStatus() throws Exception {
		createPOFile();
		runBuilder();
		final String anyPO = "anyPO";
		final IPSStatus status = pc.getStatus(anyPO);
		assertEquals(psRoot.getStatus(anyPO), status);
	}

	/**
	 * Ensures that the scheduling rule for a Proof Component is correctly
	 * constructed.
	 */
	@Test
	public void testSchedulingRule() throws Exception {
		final ISchedulingRule rule = pc.getSchedulingRule();
		assertFalse(rule.contains(mchroot.getSchedulingRule()));
		assertFalse(rule.contains(scRoot.getSchedulingRule()));
		assertTrue(rule.contains(poRoot.getSchedulingRule()));
		assertTrue(rule.contains(prRoot.getSchedulingRule()));
		assertTrue(rule.contains(psRoot.getSchedulingRule()));
	}

	/**
	 * Ensures that a PR file can be saved through its proof component.
	 */
	@Test
	public void testSavePR() throws Exception {
		createPOFile();
		runBuilder();
		assertFalse(pc.hasUnsavedChanges());
		modifyPRFile();
		assertTrue(pc.hasUnsavedChanges());
		pc.save(null, false);
		assertFalse(pc.hasUnsavedChanges());
		assertSavedPRFile();
	}

	/**
	 * Ensures that a PS file can be saved through its proof component.
	 */
	@Test
	public void testSavePS() throws Exception {
		createPOFile();
		runBuilder();
		assertFalse(pc.hasUnsavedChanges());
		modifyPSFile();
		assertTrue(pc.hasUnsavedChanges());
		pc.save(null, false);
		assertFalse(pc.hasUnsavedChanges());
		assertSavedPSFile();
	}

	/**
	 * Ensures that both the PR and PS file can be saved through a proof
	 * component.
	 */
	@Test
	public void testSavePRPS() throws Exception {
		createPOFile();
		runBuilder();
		assertFalse(pc.hasUnsavedChanges());
		modifyPRFile();
		modifyPSFile();
		assertTrue(pc.hasUnsavedChanges());
		pc.save(null, false);
		assertFalse(pc.hasUnsavedChanges());
		assertSavedPRFile();
		assertSavedPSFile();
	}

	/**
	 * Ensures that a PR file can be reverted (made consistent) through its
	 * proof component.
	 */
	@Test
	public void testRevertPR() throws Exception {
		createPOFile();
		runBuilder();
		assertFalse(pc.hasUnsavedChanges());
		modifyPRFile();
		assertTrue(pc.hasUnsavedChanges());
		pc.makeConsistent(null);
		assertFalse(pc.hasUnsavedChanges());
		assertRevertedPRFile();
	}

	/**
	 * Ensures that a PS file can be reverted (made consistent) through its
	 * proof component.
	 */
	@Test
	public void testRevertPS() throws Exception {
		createPOFile();
		runBuilder();
		assertFalse(pc.hasUnsavedChanges());
		modifyPSFile();
		assertTrue(pc.hasUnsavedChanges());
		pc.makeConsistent(null);
		assertFalse(pc.hasUnsavedChanges());
		assertRevertedPSFile();
	}

	/**
	 * Ensures that both the PR and PS file can be reverted (made consistent)
	 * through their proof component.
	 */
	@Test
	public void testRevertPRPS() throws Exception {
		createPOFile();
		runBuilder();
		assertFalse(pc.hasUnsavedChanges());
		modifyPRFile();
		modifyPSFile();
		assertTrue(pc.hasUnsavedChanges());
		pc.makeConsistent(null);
		assertFalse(pc.hasUnsavedChanges());
		assertRevertedPRFile();
		assertRevertedPSFile();
	}

	private void modifyPRFile() throws RodinDBException {
		prRoot.getProof(PO3).create(null, null);
	}

	private void modifyPSFile() throws RodinDBException {
		psRoot.getStatus(PO3).create(null, null);
	}

	private void assertSavedPRFile() {
		assertTrue(prRoot.getProof(PO3).exists());
		final IPRRoot snapshot = (IPRRoot) prRoot.getSnapshot();
		assertTrue(snapshot.getProof(PO3).exists());
	}

	private void assertRevertedPRFile() {
		assertFalse(prRoot.getProof(PO3).exists());
		final IPRRoot snapshot = (IPRRoot) prRoot.getSnapshot();
		assertFalse(snapshot.getProof(PO3).exists());
	}

	private void assertSavedPSFile() {
		assertTrue(psRoot.getStatus(PO3).exists());
		final IPSRoot snapshot = (IPSRoot) psRoot.getSnapshot();
		assertTrue(snapshot.getStatus(PO3).exists());
	}

	private void assertRevertedPSFile() {
		assertFalse(psRoot.getStatus(PO3).exists());
		final IPSRoot snapshot = (IPSRoot) psRoot.getSnapshot();
		assertFalse(snapshot.getStatus(PO3).exists());
	}

}
