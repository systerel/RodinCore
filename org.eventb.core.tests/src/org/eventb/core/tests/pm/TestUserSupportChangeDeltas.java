/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - mathematical language V2
 *******************************************************************************/
package org.eventb.core.tests.pm;

import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.tests.pom.POUtil;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Unit tests for class {@link IUserSupportManager}
 * 
 * @author htson
 */
public class TestUserSupportChangeDeltas extends TestPMDelta {

	IPORoot poRoot;

	IPSRoot psRoot;

	IPRRoot prRoot;

	IUserSupport userSupport;

	private static String originalPO = "original PO";

	private static String dischargedPO = "discharged PO";

	private static String copyOriginalPO = "copy original PO";

	private static String reusablePO = "reusable PO";

	private static String rebuiltPO = "rebuilt PO";

	private static IPOPredicateSet hyp0;

	private static IPOPredicateSet hyp1;

	@Before
	public void createProofFiles() throws Exception {
		poRoot = createPOFile("x");
		psRoot = poRoot.getPSRoot();
		prRoot = poRoot.getPRRoot();

		hyp0 = POUtil.addPredicateSet(poRoot, "hyp0", null,
				mTypeEnvironment("x=ℤ; f=ℤ↔ℤ"), "1=1", "2=2", "x∈ℕ",
				"f∈ℕ ⇸ ℕ");
		hyp1 = POUtil.addPredicateSet(poRoot, "hyp1", null,
				mTypeEnvironment("x=ℤ; f=ℤ↔ℤ"), "1=1", "2=2", "x∈ℕ",
				"f∈ℕ ⇸ ℕ", "f(x)∈ℕ", "x∈dom(f)");

		disablePostTactic();
		enableTestAutoProver();
	}

	@Override
	protected IPORoot createPOFile(String fileName) throws RodinDBException {
		IRodinFile poFile = rodinProject.getRodinFile(fileName + ".bpo");
		poFile.create(true, null);
		return (IPORoot) poFile.getRoot();
	}

	@Test
	public void testRemoveCurrentPO() throws Exception {
		POUtil
				.addSequent(poRoot, originalPO, "1 = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poRoot, dischargedPO, "1 = 1", hyp1,
				mTypeEnvironment());
		saveRodinFileOf(poRoot);

		runBuilder();
		userSupport = newUserSupport(psRoot);
		userSupport.loadProofStates();

		startDeltas();
		PSWrapperUtil.removePO(poRoot, psRoot, prRoot, originalPO);
		// Check the delta here
		assertDeltas("Original PO has been removed first ",
				"[*] x.bps [STATE]\n"
						+ "  [-] original PO[org.eventb.core.psStatus] []");
		clearDeltas();
		PSWrapperUtil.removePO(poRoot, psRoot, prRoot, dischargedPO);
		// Check the status of the User Support here
		assertDeltas("Discharged PO has been removed ", "[*] x.bps [STATE]\n"
				+ "  [-] discharged PO[org.eventb.core.psStatus] []");
		stopDeltas();
	}

	@Test
	public void testRemoveOtherPO() throws Exception {
		POUtil
				.addSequent(poRoot, originalPO, "1 = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poRoot, dischargedPO, "1 = 1", hyp1,
				mTypeEnvironment());
		saveRodinFileOf(poRoot);
		runBuilder();
		userSupport = newUserSupport(psRoot);
		userSupport.loadProofStates();

		startDeltas();
		PSWrapperUtil.removePO(poRoot, psRoot, prRoot, dischargedPO);
		// Check the delta here
		assertDeltas("Dicharged PO has been removed first ",
				"[*] x.bps [STATE]\n"
						+ "  [-] discharged PO[org.eventb.core.psStatus] []");
		clearDeltas();
		PSWrapperUtil.removePO(poRoot, psRoot, prRoot, originalPO);
		// Check the status of the User Support here
		assertDeltas("Original PO has been removed ", "[*] x.bps [STATE]\n"
				+ "  [-] original PO[org.eventb.core.psStatus] []");
		stopDeltas();
	}

	@Test
	public void testAddPO() throws Exception {
		POUtil
				.addSequent(poRoot, originalPO, "1 = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poRoot, dischargedPO, "1 = 1", hyp1,
				mTypeEnvironment());
		saveRodinFileOf(poRoot);
		runBuilder();
		userSupport = newUserSupport(psRoot);
		userSupport.loadProofStates();

		startDeltas();
		PSWrapperUtil
				.copyPO(poRoot, psRoot, prRoot, originalPO, copyOriginalPO);
		assertDeltas("Copied original PO ", "[*] x.bps [STATE]\n"
				+ "  [+] copy original PO[org.eventb.core.psStatus] []");
		stopDeltas();
	}

	/**
	 * This corresponding to the case where the orinal PO is not loaded. The
	 * User Support does nothing (no deltas).
	 * <p>
	 * TODO associated with document REQUIREMENTS
	 */
	@Test
	public void testChangePONotLoaded() throws Exception {
		POUtil
				.addSequent(poRoot, originalPO, "1 = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poRoot, dischargedPO, "1 = 1", hyp1,
				mTypeEnvironment());
		saveRodinFileOf(poRoot);
		runBuilder();
		userSupport = newUserSupport(psRoot);
		startDeltas();
		PSWrapperUtil.copyPO(poRoot, psRoot, prRoot, originalPO, dischargedPO);
		assertDeltas("Changed: PO is not loaded ", "");
		stopDeltas();
	}

	/**
	 * This corresponding to the case where the orinal PO is loaded but not
	 * modified. The proof tree is RELOADED from the database, hence the CACHE,
	 * SEARCH has been reset to empty, a new PROOFTREE is create with the new
	 * current NODE.
	 * <p>
	 * TODO associated with document REQUIREMENTS
	 */
	@Test
	public void testChangePONotModified() throws Exception {
		POUtil
				.addSequent(poRoot, originalPO, "1 = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poRoot, dischargedPO, "1 = 1", hyp1,
				mTypeEnvironment());
		saveRodinFileOf(poRoot);
		runBuilder();
		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport = newUserSupport(psRoot);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);
		startDeltas();
		PSWrapperUtil.copyPO(poRoot, psRoot, prRoot, dischargedPO, originalPO);

		assertDeltas(
				"Change: PO is loaded and NOT modified ",
				"[*] x.bps [STATE|INFORMATION]\n"
						+ "Select a new proof node (priority 1)\n"
						+ "Proof Tree is reloaded (priority 2)\n" 
						+ "  [*] original PO[org.eventb.core.psStatus] [CACHE|SEARCH|NODE|PROOFTREE]");
		stopDeltas();
	}

	/**
	 * This corresponding to the case where the orinal PO is modified but the
	 * new PO is discharged automatically in DB. The proof tree is TRASHED and
	 * RELOADED from the database, hence the CACHE, SEARCH has been reset to
	 * empty, a new PROOFTREE is create with the new current NODE.
	 * <p>
	 * TODO associated with document REQUIREMENTS
	 */
	@Test
	public void testChangePOModifiedAndDischargedAutoInDB() throws Exception {
		POUtil
				.addSequent(poRoot, originalPO, "1 = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poRoot, dischargedPO, "1 = 1", hyp1,
				mTypeEnvironment());
		saveRodinFileOf(poRoot);
		runBuilder();
		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport = newUserSupport(psRoot);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		userSupport.applyTactic(Tactics.review(1), false,
				new NullProgressMonitor());
		startDeltas();
		PSWrapperUtil.copyPO(poRoot, psRoot, prRoot, dischargedPO, originalPO);
		assertDeltas(
				"Change: PO is modified and discharged automatically in DB ",
				"[*] x.bps [STATE|INFORMATION]\n"
						+ "Select a new proof node (priority 1)\n"
						+ "Proof Tree is reloaded (priority 2)\n"
						+ "  [*] original PO[org.eventb.core.psStatus] [CACHE|SEARCH|NODE|PROOFTREE]");
		stopDeltas();
	}

	/**
	 * This corresponding to the case where the orinal PO is modified but it is
	 * still reusable with the new PO. The proof tree is REUSED hence a new
	 * PROOFTREE is create with the new current NODE.
	 * <p>
	 * TODO associated with document REQUIREMENTS
	 */
	@Test
	public void testChangePOModifiedAndReusable() throws Exception {
		POUtil
				.addSequent(poRoot, originalPO, "1 = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poRoot, dischargedPO, "1 = 1", hyp1,
				mTypeEnvironment());
		POUtil
				.addSequent(poRoot, reusablePO, "1 = 2", hyp1,
						mTypeEnvironment());
		saveRodinFileOf(poRoot);
		runBuilder();
		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport = newUserSupport(psRoot);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);
		userSupport.applyTactic(Tactics.review(1), false,
				new NullProgressMonitor());
		startDeltas();
		PSWrapperUtil.copyPO(poRoot, psRoot, prRoot, reusablePO, originalPO);
		assertDeltas(
				"Change: PO is modified and reusable ",
				"[*] x.bps [STATE|INFORMATION]\n"
						+ "Select a new proof node (priority 1)\n"
						+ "  [*] original PO[org.eventb.core.psStatus] [NODE|PROOFTREE]");
		stopDeltas();
	}
	
	/**
	 * This corresponding to the case where the orinal PO is modified but it is
	 * NOT reusable with the new PO. The proof tree is TRASHED then REBUILT, hence a new
	 * PROOFTREE is create with the new current NODE.
	 * <p>
	 * TODO associated with document REQUIREMENTS
	 */
	@Test
	public void testChangePOModifiedAndNotReusable() throws Exception {
		POUtil
				.addSequent(poRoot, originalPO, "x∈dom(f)∧f∼;({x} ◁ f)⊆id", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poRoot, dischargedPO, "1 = 1", hyp1,
				mTypeEnvironment());
		POUtil
		.addSequent(poRoot, rebuiltPO, "1 = 2", hyp1,
				mTypeEnvironment());
		POUtil
				.addSequent(poRoot, reusablePO, "1 = 2", hyp1,
						mTypeEnvironment());
		saveRodinFileOf(poRoot);
		runBuilder();
		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport = newUserSupport(psRoot);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);
		userSupport.applyTactic(Tactics.review(1), false,
				new NullProgressMonitor());
		PSWrapperUtil.copyPO(poRoot, psRoot, prRoot, rebuiltPO, originalPO);
		startDeltas();
		PSWrapperUtil.copyPO(poRoot, psRoot, prRoot, reusablePO, originalPO);
		assertDeltas(
				"Change: PO is modified and NOT reusable ",
				"[*] x.bps [STATE|INFORMATION]\n"
						+ "Select a new proof node (priority 1)\n"
						+ "  [*] original PO[org.eventb.core.psStatus] [NODE|PROOFTREE]");
		stopDeltas();
	}
}
