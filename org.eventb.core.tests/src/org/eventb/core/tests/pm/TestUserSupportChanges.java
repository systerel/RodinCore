/*******************************************************************************
 * Copyright (c) 2007, 2011 ETH Zurich and others.
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
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.tests.pom.POUtil;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         Unit tests for class {@link IUserSupport}. These tests depend on the
 *         toString() methods of the user support and the proof state. Changing
 *         these toString() method needs to change theses tests.
 * 
 */
public class TestUserSupportChanges extends TestPM {

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

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		poRoot = createPOFile("x");
		psRoot = poRoot.getPSRoot();
		prRoot = poRoot.getPRRoot();

		hyp0 = POUtil.addPredicateSet(poRoot, "hyp0", null, mTypeEnvironment(
				"x", "ℤ", "f", "ℙ(ℤ×ℤ)"), "x=1", "x∈ℕ", "f∈ℕ ⇸ ℕ");
		hyp1 = POUtil.addPredicateSet(poRoot, "hyp1", null, mTypeEnvironment(
				"x", "ℤ", "f", "ℙ(ℤ×ℤ)"), "x=1", "x∈ℕ", "f∈ℕ ⇸ ℕ",
				"f(x)∈ℕ", "x∈dom(f)");

		// Turn on beginner mode
		EventBPlugin.getAutoPostTacticManager().getPostTacticPreference()
				.setEnabled(false);
		enableAutoProver(true);
	}

	@Override
	IPORoot createPOFile(String fileName) throws RodinDBException {
		IRodinFile poFile = rodinProject.getRodinFile(fileName + ".bpo");
		poFile.create(true, null);
		return (IPORoot) poFile.getRoot();
	}

	@Override
	protected void tearDown() throws Exception {
		if (userSupport != null) {
			userSupport.dispose();
		}
		super.tearDown();
	}

	public void testRemoveCurrentPO() throws Exception {
		POUtil
				.addSequent(poRoot, originalPO, "x = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poRoot, dischargedPO, "x = 1", hyp1,
				mTypeEnvironment());
		saveRodinFileOf(poRoot);
		runBuilder();
		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport = newUserSupport(psRoot);

		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		PSWrapperUtil.removePO(poRoot, psRoot, prRoot, originalPO);
		// Check the status of the User Support here
		assertString(
				"Original PO has been removed first ",
				userSupport.toString(),
				"****** User Support for: x ******\n"
						+ "** Proof States **\n"
						+ "****** Proof Status for: discharged PO[org.eventb.core.psStatus] ******\n"
						+ "Is dirty? false\n"
						+ "** Proof Tree **\n"
						+ "null\n"
						+ "** Cached **\n"
						+ "** Searched **\n"
						+ "Current node: null\n"
						+ "****************************\n"
						+ "Current psSatus: original PO[org.eventb.core.psStatus]\n"
						+ "********************************************************\n");
		PSWrapperUtil.removePO(poRoot, psRoot, prRoot, dischargedPO);
		// Check the status of the User Support here
		assertString(
				"Discharged PO has been removed ",
				userSupport.toString(),
				"****** User Support for: x ******\n"
						+ "** Proof States **\n"
						+ "Current psSatus: original PO[org.eventb.core.psStatus]\n"
						+ "********************************************************\n");
	}

	public void testRemoveOtherPO() throws Exception {
		POUtil
				.addSequent(poRoot, originalPO, "x = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poRoot, dischargedPO, "x = 1", hyp1,
				mTypeEnvironment());
		saveRodinFileOf(poRoot);
		runBuilder();
		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport = newUserSupport(psRoot);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		PSWrapperUtil.removePO(poRoot, psRoot, prRoot, dischargedPO);
		// Check the status of the User Support here
		assertString(
				"Dicharged PO has been removed first ",
				userSupport.toString(),
				"****** User Support for: x ******\n"
						+ "** Proof States **\n"
						+ "****** Proof Status for: original PO[org.eventb.core.psStatus] ******\n"
						+ "Is dirty? false\n"
						+ "** Proof Tree **\n"
						+ "{f=ℙ(ℤ×ℤ), x=ℤ}[][x=1, x∈ℕ, f∈ℕ ⇸ ℕ][] |- x=2		- =>\n"
						+ "1 pending subgoals\n"
						+ "\n"
						+ "** Cached **\n"
						+ "** Searched **\n"
						+ "Current node: {f=ℙ(ℤ×ℤ), x=ℤ}[][x=1, x∈ℕ, f∈ℕ ⇸ ℕ][] |- x=2		- =>\n"
						+ "1 pending subgoals\n"
						+ "\n"
						+ "****************************\n"
						+ "Current psSatus: original PO[org.eventb.core.psStatus]\n"
						+ "********************************************************\n");
		PSWrapperUtil.removePO(poRoot, psRoot, prRoot, originalPO);
		// Check the status of the User Support here
		assertString(
				"Original PO has been removed ",
				userSupport.toString(),
				"****** User Support for: x ******\n"
						+ "** Proof States **\n"
						+ "Current psSatus: original PO[org.eventb.core.psStatus]\n"
						+ "********************************************************\n");
	}

	public void testAddPO() throws Exception {
		POUtil
				.addSequent(poRoot, originalPO, "x = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poRoot, dischargedPO, "x = 1", hyp1,
				mTypeEnvironment());
		saveRodinFileOf(poRoot);
		runBuilder();
		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport = newUserSupport(psRoot);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);
		PSWrapperUtil
				.copyPO(poRoot, psRoot, prRoot, originalPO, copyOriginalPO);
		assertString(
				"Copied original PO ",
				userSupport.toString(),
				"****** User Support for: x ******\n"
						+ "** Proof States **\n"
						+ "****** Proof Status for: copy original PO[org.eventb.core.psStatus] ******\n"
						+ "Is dirty? false\n"
						+ "** Proof Tree **\n"
						+ "null\n"
						+ "** Cached **\n"
						+ "** Searched **\n"
						+ "Current node: null\n"
						+ "****************************\n"
						+ "****** Proof Status for: original PO[org.eventb.core.psStatus] ******\n"
						+ "Is dirty? false\n"
						+ "** Proof Tree **\n"
						+ "{f=ℙ(ℤ×ℤ), x=ℤ}[][x=1, x∈ℕ, f∈ℕ ⇸ ℕ][] |- x=2		- =>\n"
						+ "1 pending subgoals\n"
						+ "\n"
						+ "** Cached **\n"
						+ "** Searched **\n"
						+ "Current node: {f=ℙ(ℤ×ℤ), x=ℤ}[][x=1, x∈ℕ, f∈ℕ ⇸ ℕ][] |- x=2		- =>\n"
						+ "1 pending subgoals\n"
						+ "\n"
						+ "****************************\n"
						+ "****** Proof Status for: discharged PO[org.eventb.core.psStatus] ******\n"
						+ "Is dirty? false\n"
						+ "** Proof Tree **\n"
						+ "null\n"
						+ "** Cached **\n"
						+ "** Searched **\n"
						+ "Current node: null\n"
						+ "****************************\n"
						+ "Current psSatus: original PO[org.eventb.core.psStatus]\n"
						+ "********************************************************\n");
	}

	/**
	 * This corresponding to the case where the orinal PO is not loaded. The
	 * User Support does nothing.
	 * <p>
	 * TODO associated with document REQUIREMENTS
	 */
	public void testChangePONotLoaded() throws Exception {
		POUtil
				.addSequent(poRoot, originalPO, "x = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poRoot, dischargedPO, "x = 1", hyp1,
				mTypeEnvironment());
		saveRodinFileOf(poRoot);
		runBuilder();
		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport = newUserSupport(psRoot);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);
		String original = userSupport.toString();
		PSWrapperUtil.copyPO(poRoot, psRoot, prRoot, originalPO, dischargedPO);
		assertString("Changed: PO is not loaded ", userSupport.toString(),
				original);
	}

	/**
	 * This corresponding to the case where the orinal PO is loaded but not
	 * modified. The proof tree is RELOADED from the database, hence the CACHE,
	 * SEARCH has been reset to empty, a new PROOFTREE is create with the new
	 * current NODE.
	 * <p>
	 * TODO associated with document REQUIREMENTS
	 */
	public void testChangePONotModified() throws Exception {
		POUtil
				.addSequent(poRoot, originalPO, "x = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poRoot, dischargedPO, "x = 1", hyp1,
				mTypeEnvironment());
		saveRodinFileOf(poRoot);
		runBuilder();
		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport = newUserSupport(psRoot);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);
		PSWrapperUtil.copyPO(poRoot, psRoot, prRoot, dischargedPO, originalPO);
		assertString(
				"Change: PO is loaded and NOT modified ",
				userSupport.toString(),
				"****** User Support for: x ******\n"
						+ "** Proof States **\n"
						+ "****** Proof Status for: original PO[org.eventb.core.psStatus] ******\n"
						+ "Is dirty? false\n"
						+ "** Proof Tree **\n"
						+ "{f=ℙ(ℤ×ℤ), x=ℤ}[][x=1, x∈ℕ, f∈ℕ ⇸ ℕ, f(x)∈ℕ, x∈dom(f), f∈ℤ ⇸ ℤ][] |- x=1		hyp <>\n"
						+ "No pending subgoals!\n"
						+ "\n"
						+ "** Cached **\n"
						+ "** Searched **\n"
						+ "Current node: {f=ℙ(ℤ×ℤ), x=ℤ}[][x=1, x∈ℕ, f∈ℕ ⇸ ℕ, f(x)∈ℕ, x∈dom(f), f∈ℤ ⇸ ℤ][] |- x=1		hyp <>\n"
						+ "No pending subgoals!\n"
						+ "\n"
						+ "****************************\n"
						+ "****** Proof Status for: discharged PO[org.eventb.core.psStatus] ******\n"
						+ "Is dirty? false\n"
						+ "** Proof Tree **\n"
						+ "null\n"
						+ "** Cached **\n"
						+ "** Searched **\n"
						+ "Current node: null\n"
						+ "****************************\n"
						+ "Current psSatus: original PO[org.eventb.core.psStatus]\n"
						+ "********************************************************\n");
	}

	/**
	 * This corresponding to the case where the orinal PO is modified but the
	 * new PO is discharged automatically in DB. The proof tree is TRASHED and
	 * RELOADED from the database, hence the CACHE, SEARCH has been reset to
	 * empty, a new PROOFTREE is create with the new current NODE.
	 * <p>
	 * TODO associated with document REQUIREMENTS
	 */
	public void testChangePOModifiedAndDischargedAutoInDB() throws Exception {
		POUtil
				.addSequent(poRoot, originalPO, "x = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poRoot, dischargedPO, "x = 1", hyp1,
				mTypeEnvironment());
		saveRodinFileOf(poRoot);
		runBuilder();
		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport = newUserSupport(psRoot);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);
		// Modified current PO
		try {
			userSupport.applyTactic(Tactics.review(1), false,
					new NullProgressMonitor());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PSWrapperUtil.copyPO(poRoot, psRoot, prRoot, dischargedPO, originalPO);
		assertString(
				"Change: PO is modified and discharged automatically in DB ",
				userSupport.toString(),
				"****** User Support for: x ******\n"
						+ "** Proof States **\n"
						+ "****** Proof Status for: original PO[org.eventb.core.psStatus] ******\n"
						+ "Is dirty? false\n"
						+ "** Proof Tree **\n"
						+ "{f=ℙ(ℤ×ℤ), x=ℤ}[][x=1, x∈ℕ, f∈ℕ ⇸ ℕ, f(x)∈ℕ, x∈dom(f), f∈ℤ ⇸ ℤ][] |- x=1		hyp <>\n"
						+ "No pending subgoals!\n"
						+ "\n"
						+ "** Cached **\n"
						+ "** Searched **\n"
						+ "Current node: {f=ℙ(ℤ×ℤ), x=ℤ}[][x=1, x∈ℕ, f∈ℕ ⇸ ℕ, f(x)∈ℕ, x∈dom(f), f∈ℤ ⇸ ℤ][] |- x=1		hyp <>\n"
						+ "No pending subgoals!\n"
						+ "\n"
						+ "****************************\n"
						+ "****** Proof Status for: discharged PO[org.eventb.core.psStatus] ******\n"
						+ "Is dirty? false\n"
						+ "** Proof Tree **\n"
						+ "null\n"
						+ "** Cached **\n"
						+ "** Searched **\n"
						+ "Current node: null\n"
						+ "****************************\n"
						+ "Current psSatus: original PO[org.eventb.core.psStatus]\n"
						+ "********************************************************\n");
	}

	/**
	 * This corresponding to the case where the orinal PO is modified but it is
	 * still reusable with the new PO. The proof tree is REUSED hence a new
	 * PROOFTREE is create with the new current NODE.
	 * <p>
	 * TODO associated with document REQUIREMENTS
	 */
	public void testChangePOModifiedAndReusable() throws Exception {
		POUtil
				.addSequent(poRoot, originalPO, "x = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poRoot, dischargedPO, "x = 1", hyp1,
				mTypeEnvironment());
		POUtil
				.addSequent(poRoot, reusablePO, "x = 2", hyp1,
						mTypeEnvironment());
		saveRodinFileOf(poRoot);
		runBuilder();
		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport = newUserSupport(psRoot);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);
		// Modified current PO
		try {
			userSupport.applyTactic(Tactics.review(1), false,
					new NullProgressMonitor());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PSWrapperUtil.copyPO(poRoot, psRoot, prRoot, reusablePO, originalPO);
		assertString(
				"Change: PO is modified and reusable ",
				userSupport.toString(),
				"****** User Support for: x ******\n"
						+ "** Proof States **\n"
						+ "****** Proof Status for: original PO[org.eventb.core.psStatus] ******\n"
						+ "Is dirty? true\n"
						+ "** Proof Tree **\n"
						+ "{f=ℙ(ℤ×ℤ), x=ℤ}[][x=1, x∈ℕ, f∈ℕ ⇸ ℕ, f(x)∈ℕ, x∈dom(f), f∈ℤ ⇸ ℤ][] |- x=2		rv (1) (x=2) <>\n"
						+ "No pending subgoals!\n"
						+ "\n"
						+ "** Cached **\n"
						+ "** Searched **\n"
						+ "Current node: {f=ℙ(ℤ×ℤ), x=ℤ}[][x=1, x∈ℕ, f∈ℕ ⇸ ℕ, f(x)∈ℕ, x∈dom(f), f∈ℤ ⇸ ℤ][] |- x=2		rv (1) (x=2) <>\n"
						+ "No pending subgoals!\n"
						+ "\n"
						+ "****************************\n"
						+ "****** Proof Status for: discharged PO[org.eventb.core.psStatus] ******\n"
						+ "Is dirty? false\n"
						+ "** Proof Tree **\n"
						+ "null\n"
						+ "** Cached **\n"
						+ "** Searched **\n"
						+ "Current node: null\n"
						+ "****************************\n"
						+ "****** Proof Status for: reusable PO[org.eventb.core.psStatus] ******\n"
						+ "Is dirty? false\n"
						+ "** Proof Tree **\n"
						+ "null\n"
						+ "** Cached **\n"
						+ "** Searched **\n"
						+ "Current node: null\n"
						+ "****************************\n"
						+ "Current psSatus: original PO[org.eventb.core.psStatus]\n"
						+ "********************************************************\n");
	}

	/**
	 * This corresponding to the case where the orinal PO is modified but it is
	 * NOT reusable with the new PO. The proof tree is TRASHED then REBUILT, hence a new
	 * PROOFTREE is create with the new current NODE.
	 * <p>
	 * TODO associated with document REQUIREMENTS
	 */
	public void testChangePOModifiedAndNotReusable() throws Exception {
		POUtil
				.addSequent(poRoot, originalPO, "x∈dom(f)∧f∼;({x} ◁ f)⊆id", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poRoot, dischargedPO, "x = 1", hyp1,
				mTypeEnvironment());
		POUtil
		.addSequent(poRoot, rebuiltPO, "x = 2", hyp1,
				mTypeEnvironment());
		POUtil
				.addSequent(poRoot, reusablePO, "x = 2", hyp1,
						mTypeEnvironment());
		saveRodinFileOf(poRoot);
		runBuilder();
		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport = newUserSupport(psRoot);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);
		// Modified current PO
		try {
			userSupport.applyTactic(Tactics.review(1), false,
					new NullProgressMonitor());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PSWrapperUtil.copyPO(poRoot, psRoot, prRoot, rebuiltPO, originalPO);
		assertString(
				"Change: PO is modified and NOT reusable ",
				userSupport.toString(), "****** User Support for: x ******\n" + 
				"** Proof States **\n" + 
				"****** Proof Status for: original PO[org.eventb.core.psStatus] ******\n" + 
				"Is dirty? true\n" + 
				"** Proof Tree **\n" + 
				"{f=ℙ(ℤ×ℤ), x=ℤ}[][x=1, x∈ℕ, f∈ℕ ⇸ ℕ, f(x)∈ℕ, x∈dom(f), f∈ℤ ⇸ ℤ][] |- x=2		- =>\n" + 
				"1 pending subgoals\n" + 
				"\n" + 
				"** Cached **\n" + 
				"** Searched **\n" + 
				"Current node: {f=ℙ(ℤ×ℤ), x=ℤ}[][x=1, x∈ℕ, f∈ℕ ⇸ ℕ, f(x)∈ℕ, x∈dom(f), f∈ℤ ⇸ ℤ][] |- x=2		- =>\n" + 
				"1 pending subgoals\n" + 
				"\n" + 
				"****************************\n" + 
				"****** Proof Status for: discharged PO[org.eventb.core.psStatus] ******\n" + 
				"Is dirty? false\n" + 
				"** Proof Tree **\n" + 
				"null\n" + 
				"** Cached **\n" + 
				"** Searched **\n" + 
				"Current node: null\n" + 
				"****************************\n" + 
				"****** Proof Status for: rebuilt PO[org.eventb.core.psStatus] ******\n" + 
				"Is dirty? false\n" + 
				"** Proof Tree **\n" + 
				"null\n" + 
				"** Cached **\n" + 
				"** Searched **\n" + 
				"Current node: null\n" + 
				"****************************\n" + 
				"****** Proof Status for: reusable PO[org.eventb.core.psStatus] ******\n" + 
				"Is dirty? false\n" + 
				"** Proof Tree **\n" + 
				"null\n" + 
				"** Cached **\n" + 
				"** Searched **\n" + 
				"Current node: null\n" + 
				"****************************\n" + 
				"Current psSatus: original PO[org.eventb.core.psStatus]\n" + 
				"********************************************************\n");
	}

}
