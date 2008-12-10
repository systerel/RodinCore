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
//package org.eventb.core.tests.pm;
package fr.systerel.eventb.proofpurger.tests;

import static org.eventb.core.ast.Formula.*;
import static org.eventb.core.tests.pom.POUtil.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.tests.pm.AbstractProofTests;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import fr.systerel.eventb.proofpurger.popup.actions.ProofPurger;

public class ProofPurgerTests extends AbstractProofTests {

	private static final Predicate GOAL;
	private static final Predicate GHYP;
	private static final Predicate LHYP;
	private static String PO3 = "PO3";
	private static String PO4 = "PO4";
	private static String PO5 = "PO5";
	private static String PO6 = "PO6";

	static {
		final Expression zero = ff.makeIntegerLiteral(BigInteger.ZERO, null);
		final Expression one = ff.makeIntegerLiteral(BigInteger.ONE, null);
		GOAL = ff.makeLiteralPredicate(BTRUE, null);
		GHYP = ff.makeRelationalPredicate(EQUAL, zero, zero, null);
		LHYP = ff.makeRelationalPredicate(EQUAL, one, one, null);
	}

	/**
	 * Create a PO file in a Project.
	 * 
	 * @param bareName
	 *            File name.
	 * @param rp
	 *            RodinProject to insert the file in.
	 * @return the created File.
	 * @throws RodinDBException
	 */
	protected IPORoot createPORoot(String bareName, IRodinProject rp)
			throws RodinDBException {
		final String fileName = EventBPlugin.getPOFileName(bareName);
		IRodinFile rf = rp.getRodinFile(fileName);
		rf.create(true, null);
		return (IPORoot) rf.getRoot();
	}

	private IPORoot populatePOFile(String barename, IRodinProject rp)
			throws RodinDBException {
		IPORoot poRoot = createPORoot(barename, rp);
		final ITypeEnvironment typenv = mTypeEnvironment();
		final IPOPredicateSet hyp =
				addPredicateSet(poRoot, "hyp", null, typenv, GHYP.toString());
		addSequent(poRoot, PO1, GOAL.toString(), hyp, typenv, LHYP.toString());
		addSequent(poRoot, PO2, GOAL.toString(), hyp, typenv, LHYP.toString());
		poRoot.getRodinFile().save(null, true);
		return poRoot;
	}

	private IPORoot populatePOFile(String barename) throws RodinDBException {
		return populatePOFile(barename, rodinProject);
	}

	/**
	 * Creates a proof that reviews the given PO sequent.
	 * 
	 * @param po
	 * @throws CoreException
	 */
	void createProof(IPOSequent po) throws CoreException {
		final IPORoot poRoot = (IPORoot) po.getRodinFile().getRoot();
		runBuilder(po.getRodinProject());
		IPRRoot prRoot = poRoot.getPRRoot();
		// delete all unintended proofs created by runBuilder
		for (IPRProof pr : prRoot.getProofs()) {
			if (!pr.getElementName().equals(po.getElementName())) {
				pr.delete(true, null);
			}
		}
	}

	private void duplicateProof(IPRRoot prFile, final String existingPO,
			final String prName) throws RodinDBException {
		prFile.getProof(existingPO).copy(prFile, null, prName, false, null);
	}

	private void duplicateProof(IPRRoot prFile, final String existingPO)
			throws RodinDBException {
		int poIndex = 1;
		while (prFile.getProof("PO" + poIndex).exists()) {
			poIndex = poIndex + 1;
		}
		prFile.getProof(existingPO).copy(prFile, null, "PO" + poIndex, false,
				null);
	}

	private void populatePRFileFromPO(IPRRoot prFile, final String existingPO,
			int numberOfProofs) throws RodinDBException {
		for (int i = 0; i < numberOfProofs; i++) {
			duplicateProof(prFile, existingPO);
		}
	}

	private void assertUnusedProofs(IRodinElement[] input,
			IPRProof[] expectedResult) throws RodinDBException {
		IPRProof[] actualResult =
				ProofPurger.getDefault().computeUnusedProofs(input, null);

		assertNotNull("Unexpected null output", actualResult);

		List<IPRProof> expList = Arrays.asList(expectedResult);
		List<IPRProof> actList = Arrays.asList(actualResult);

		assertTrue("missing proofs in selection", actList.containsAll(expList));
		assertTrue("unexpected proofs in selection", expList
				.containsAll(actList));

		assertEquals("result contains several occurrences of the same proof",
				actualResult.length, expectedResult.length);
	}

	@Override
	protected void tearDown() throws Exception {
		for (IRodinElement e : rodinProject.getChildren()) {
			if (e instanceof IRodinFile) {
				((IRodinFile) e).delete(true, null);
			}
		}
		super.tearDown();
	}

	/**
	 * Ensures that an empty input gives an empty result.
	 * 
	 * @throws RodinDBException
	 */
	public void testEmptyInput() throws RodinDBException {
		IRodinElement[] input = {};
		IPRProof[] expectedResult = {};

		assertUnusedProofs(input, expectedResult);
	}

	/**
	 * Simple test with a single file and three POs.
	 */
	public void testFileSimple() throws Exception {
		IPORoot poRoot = populatePOFile("m");
		IPRRoot prRoot = poRoot.getPRRoot();
		createProof(poRoot.getSequent(PO1));
		duplicateProof(prRoot, PO1, PO3);
		prRoot.getRodinFile().save(null, false);

		IRodinElement[] input = { prRoot };
		IPRProof[] expectedResult = { prRoot.getProof(PO3) };

		assertUnusedProofs(input, expectedResult);
	}

	/**
	 * Ensures that it works well with an empty PO file.
	 */
	public void testEmptyPOFile() throws Exception {
		IPORoot poFile = populatePOFile("m");
		IPRRoot prFile = poFile.getPRRoot();
		createProof(poFile.getSequent(PO1));
		duplicateProof(prFile, PO1); // creates PO2
		poFile.getSequent(PO1).delete(true, null);
		poFile.getSequent(PO2).delete(true, null);
		assertFalse(poFile.getSequent(PO1).exists());
		assertFalse(poFile.getSequent(PO2).exists());

		IRodinElement[] input = { prFile };
		IPRProof[] expectedResult =
				{ prFile.getProof(PO1), prFile.getProof(PO2) };

		assertUnusedProofs(input, expectedResult);
	}

	/**
	 * Ensures that it works well when the PO file does not exist.
	 * 
	 * @throws Exception
	 */
	public void testInexistentPOFile() throws Exception {
		IPORoot poFile = populatePOFile("m");
		IPRRoot prFile = poFile.getPRRoot();
		createProof(poFile.getSequent(PO1));
		poFile.delete(true, null);
		assertFalse(poFile.exists());

		IRodinElement[] input = { prFile };
		IPRProof[] expectedResult = { prFile.getProof(PO1) };

		assertUnusedProofs(input, expectedResult);
	}

	// several files selected
	/**
	 * Ensures that it works well when several input files are selected.
	 */
	public void testSeveralFiles() throws Exception {
		IPORoot poFile1 = populatePOFile("m1");
		IPORoot poFile2 = populatePOFile("m2");
		IPORoot poFile3 = populatePOFile("m3");
		IPRRoot prFile1 = poFile1.getPRRoot();
		IPRRoot prFile2 = poFile2.getPRRoot();
		IPRRoot prFile3 = poFile3.getPRRoot();

		createProof(poFile1.getSequent(PO1));
		createProof(poFile2.getSequent(PO2));
		duplicateProof(prFile2, PO2); // creates PO1
		duplicateProof(prFile2, PO2); // creates PO3
		prFile2.getProof(PO1).delete(true, null); // deletes PO1
		createProof(poFile3.getSequent(PO1));
		populatePRFileFromPO(prFile3, PO1, 5);

		prFile1.getRodinFile().save(null, false);
		prFile2.getRodinFile().save(null, false);
		prFile3.getRodinFile().save(null, false);

		IRodinElement[] input = { prFile1, prFile2, prFile3 };
		IPRProof[] expectedResult =
				{ prFile2.getProof(PO3), prFile3.getProof(PO3),
						prFile3.getProof(PO4), prFile3.getProof(PO5),
						prFile3.getProof(PO6) };

		assertUnusedProofs(input, expectedResult);
	}

	// variables for testing with several projects
	private static IRodinProject rp1;
	private static IRodinProject rp2;
	private static IRodinProject rp3;

	private static IPORoot spPOFile1;
	private static IPORoot spPOFile2;
	private static IPORoot spPOFile3;
	private static IPORoot spPOFile4;
	private static IPORoot spPOFile5;

	private static IPRRoot spPRFile1;
	private static IPRRoot spPRFile2;
	private static IPRRoot spPRFile3;
	private static IPRRoot spPRFile4;
	private static IPRRoot spPRFile5;

	private void initSeveralProjects() throws CoreException {
		rp1 = createRodinProject("RP1");
		rp2 = createRodinProject("RP2");
		rp3 = createRodinProject("RP3");

		spPOFile1 = populatePOFile("m1", rp1);
		spPOFile2 = populatePOFile("m2", rp2);
		spPOFile3 = populatePOFile("m3", rp2);
		spPOFile4 = populatePOFile("m4", rp3);
		spPOFile5 = populatePOFile("m5", rp3);

		spPRFile1 = spPOFile1.getPRRoot();
		spPRFile2 = spPOFile2.getPRRoot();
		spPRFile3 = spPOFile3.getPRRoot();
		spPRFile4 = spPOFile4.getPRRoot();
		spPRFile5 = spPOFile5.getPRRoot();

		createProof(spPOFile1.getSequent(PO1));
		createProof(spPOFile2.getSequent(PO1));
		populatePRFileFromPO(spPRFile2, PO1, 3);
		createProof(spPOFile4.getSequent(PO2));
		createProof(spPOFile5.getSequent(PO1));
		populatePRFileFromPO(spPRFile5, PO1, 3);
	}

	/**
	 * Ensures that it works well when several input projects are selected.
	 */
	public void testSeveralProjects() throws CoreException {
		initSeveralProjects();

		IRodinElement[] input = { rp1, rp2, rp3 };
		IPRProof[] expectedResult =
				{ spPRFile2.getProof(PO3), spPRFile2.getProof(PO4),
						spPRFile5.getProof(PO3), spPRFile5.getProof(PO4) };

		assertUnusedProofs(input, expectedResult);
	}

	/**
	 * Ensures that it works well when the input is composed of both projects
	 * and files.
	 */
	public void testMixingProjectsFiles() throws CoreException {
		initSeveralProjects();

		IRodinElement[] input = { spPRFile1, rp2, spPRFile5 };
		IPRProof[] expectedResult =
				{ spPRFile2.getProof(PO3), spPRFile2.getProof(PO4),
						spPRFile5.getProof(PO3), spPRFile5.getProof(PO4) };

		assertUnusedProofs(input, expectedResult);
	}

	/**
	 * Ensures that redundant files are ignored.
	 */
	public void testRedundancyFiles() throws CoreException {
		initSeveralProjects();

		IRodinElement[] input =
				{ spPRFile1, spPRFile1, spPRFile3, spPRFile4, spPRFile5,
						spPRFile5 };
		IPRProof[] expectedResult =
				{ spPRFile5.getProof(PO3), spPRFile5.getProof(PO4) };

		assertUnusedProofs(input, expectedResult);
	}

	/**
	 * Ensures that redundant projects are ignored.
	 */
	public void testRedundancyProjects() throws CoreException {
		initSeveralProjects();

		IRodinElement[] input =
				{ spPRFile1, spPRFile1, spPRFile3, spPRFile4, spPRFile5,
						spPRFile5 };
		IPRProof[] expectedResult =
				{ spPRFile5.getProof(PO3), spPRFile5.getProof(PO4) };

		assertUnusedProofs(input, expectedResult);
	}

	/**
	 * When a file is selected as well as its enclosing project, ensures that no
	 * redundancy occurs in the output.
	 */
	public void testRedundancyProjectsFiles() throws CoreException {
		initSeveralProjects();

		IRodinElement[] input =
				{ rp1, spPRFile1, rp2, spPRFile2, spPRFile4, spPRFile5,
						spPRFile5, rp3 };
		IPRProof[] expectedResult =
				{ spPRFile2.getProof(PO3), spPRFile2.getProof(PO4),
						spPRFile5.getProof(PO3), spPRFile5.getProof(PO4) };

		assertUnusedProofs(input, expectedResult);
	}

	/**
	 * Asserts that all proofs given as input to purgeUnusedProofs were deleted.
	 * 
	 * @throws RodinDBException
	 */
	private void assertPurgeSuccess(IPRProof[] delProofs)
			throws RodinDBException {
		try {
			ProofPurger.getDefault().purgeUnusedProofs(delProofs, null);
		} catch (IllegalArgumentException e) {
			fail("Unexpected exception: " + e.getMessage());
		}
		for (IPRProof pr : delProofs) {
			assertFalse("Some proofs remain: "
					+ pr.getRodinFile().getBareName()
					+ ":"
					+ pr.getElementName(), pr.exists());
		}
	}

	/**
	 * Asserts that IllegalArgumentException is raised.
	 * 
	 * @throws RodinDBException
	 */
	private void assertPurgeFailure(IPRProof[] delProofs)
			throws RodinDBException {
		try {
			ProofPurger.getDefault().purgeUnusedProofs(delProofs, null);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("IllegalArgumentException should have been raised.");
	}

	/**
	 * Asserts that the given proofs exist in the DB.
	 */
	private void assertKeepProofs(IPRProof[] keepProofs) {
		for (IPRProof pr : keepProofs) {
			assertTrue("Some proofs were erased by error: "
					+ pr.getRodinFile().getBareName()
					+ ":"
					+ pr.getElementName(), pr.exists());
		}
	}

	/**
	 * Asserts that the given file has been deleted.
	 * 
	 * @param file
	 *            Tested file.
	 */
	private void assertFileDeleted(IRodinFile file, boolean deleted) {
		assertFalse("File should "
				+ (deleted ? "" : "not ")
				+ "have been deleted: "
				+ file.getBareName(), file.exists() == deleted);
	}

	/**
	 * Ensures that purging with an empty input has no consequence on existing
	 * proofs.
	 */
	public void testEmpty() throws Exception {
		initSeveralProjects();

		IPRProof[] delProofs = new IPRProof[0];
		IPRProof[] keepProofs =
				{ spPRFile1.getProof(PO1), spPRFile2.getProof(PO1),
						spPRFile2.getProof(PO2), spPRFile2.getProof(PO3),
						spPRFile2.getProof(PO4), spPRFile4.getProof(PO2),
						spPRFile5.getProof(PO1), spPRFile5.getProof(PO2),
						spPRFile5.getProof(PO3), spPRFile5.getProof(PO4) };

		assertPurgeSuccess(delProofs);
		assertKeepProofs(keepProofs);
	}

	/**
	 * Basic test case: some proofs are deleted, some others not.
	 */
	public void testBasicPurge() throws Exception {
		initSeveralProjects();

		IPRProof[] delProofs =
				{ spPRFile2.getProof(PO3), spPRFile2.getProof(PO4) };
		IPRProof[] keepProofs =
				{ spPRFile1.getProof(PO1), spPRFile2.getProof(PO1),
						spPRFile2.getProof(PO2), spPRFile4.getProof(PO2),
						spPRFile5.getProof(PO1), spPRFile5.getProof(PO2),
						spPRFile5.getProof(PO3), spPRFile5.getProof(PO4) };

		assertPurgeSuccess(delProofs);
		assertKeepProofs(keepProofs);
	}

	/**
	 * Ensures that the IllegalArgumentException is correctly raised.
	 */
	public void testHavePO() throws Exception {
		initSeveralProjects();

		IPRProof[] delProofs =
				{ spPRFile2.getProof(PO3), spPRFile2.getProof(PO4),
						spPRFile4.getProof(PO2), spPRFile5.getProof(PO1) };
		IPRProof[] keepProofs =
				{ spPRFile1.getProof(PO1), spPRFile2.getProof(PO1),
						spPRFile2.getProof(PO2), spPRFile4.getProof(PO2),
						spPRFile5.getProof(PO1), spPRFile5.getProof(PO2),
						spPRFile5.getProof(PO3), spPRFile5.getProof(PO4) };

		assertPurgeFailure(delProofs);
		assertKeepProofs(delProofs);
		assertKeepProofs(keepProofs);
	}

	/**
	 * Ensures that proof files are correctly deleted when they become empty and
	 * no PS file exists.
	 */
	public void testDeleteEmptyFilesNoPS() throws Exception {
		spPOFile2 = populatePOFile("m2");
		spPRFile2 = spPOFile2.getPRRoot();
		createProof(spPOFile2.getSequent(PO1));
		populatePRFileFromPO(spPRFile2, PO1, 2);
		spPOFile2.delete(true, null);
		spPRFile2.getPSRoot().getRodinFile().delete(true, null);

		IPRProof[] delProofs =
				{ spPRFile2.getProof(PO1), spPRFile2.getProof(PO2),
						spPRFile2.getProof(PO3) };

		assertPurgeSuccess(delProofs);
		assertFileDeleted(spPRFile2.getRodinFile(), true);
	}

	/**
	 * Ensures that proof files are not deleted when they become empty if a PS
	 * file exists.
	 */
	public void testDeleteEmptyFilesWithPS() throws Exception {
		spPOFile2 = populatePOFile("m2");
		spPRFile2 = spPOFile2.getPRRoot();
		createProof(spPOFile2.getSequent(PO1));
		populatePRFileFromPO(spPRFile2, PO1, 2);
		spPOFile2.delete(true, null);
		// spPRFile2.getPSRoot().getRodinFile().delete(true, null);

		IPRProof[] delProofs =
				{ spPRFile2.getProof(PO1), spPRFile2.getProof(PO2),
						spPRFile2.getProof(PO3) };

		assertPurgeSuccess(delProofs);
		assertFileDeleted(spPRFile2.getRodinFile(), false);
	}
}
