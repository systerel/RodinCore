/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.tests.pm;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPRFile;
import org.eventb.core.IPSFile;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.tests.pom.POUtil;
import org.eventb.internal.core.pm.UserSupport;
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

	IPOFile poFile;

	IPSFile psFile;

	IPRFile prFile;

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
		poFile = createPOFile("x");
		psFile = poFile.getPSFile();
		prFile = poFile.getPRFile();

		hyp0 = POUtil.addPredicateSet(poFile, "hyp0", null, mTypeEnvironment(
				"x", "ℤ", "f", "ℙ(ℤ×ℤ)"), "2=2", "x=1", "x∈ℕ", "f∈ℕ ⇸ ℕ");
		hyp1 = POUtil.addPredicateSet(poFile, "hyp1", null, mTypeEnvironment(
				"x", "ℤ", "f", "ℙ(ℤ×ℤ)"), "2=2", "x=1", "x∈ℕ", "f∈ℕ ⇸ ℕ",
				"f(x)∈ℕ", "x∈dom(f)");

		// Turn on beginner mode
		EventBPlugin.getPostTacticPreference().setEnabled(false);
		enableAutoProver(true);
		userSupport = new UserSupport();
	}

	@Override
	IPOFile createPOFile(String fileName) throws RodinDBException {
		IPOFile poFile = (IPOFile) rodinProject.getRodinFile(fileName + ".bpo");
		poFile.create(true, null);
		return poFile;
	}

	@Override
	protected void tearDown() throws Exception {
		userSupport.dispose();
		poFile.delete(true, null);
		prFile.delete(true, null);
		psFile.delete(true, null);
		super.tearDown();
	}

	public void testRemoveCurrentPO() throws Exception {
		POUtil
				.addSequent(poFile, originalPO, "x = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poFile, dischargedPO, "x = 1", hyp1,
				mTypeEnvironment());
		poFile.save(null, true);
		runBuilder();
		userSupport.setInput(psFile, null);

		PSWrapperUtil.removePO(poFile, psFile, prFile, originalPO);
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
		PSWrapperUtil.removePO(poFile, psFile, prFile, dischargedPO);
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
				.addSequent(poFile, originalPO, "x = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poFile, dischargedPO, "x = 1", hyp1,
				mTypeEnvironment());
		poFile.save(null, true);
		runBuilder();
		userSupport.setInput(psFile, null);
		PSWrapperUtil.removePO(poFile, psFile, prFile, dischargedPO);
		// Check the status of the User Support here
		assertString(
				"Dicharged PO has been removed first ",
				userSupport.toString(),
				"****** User Support for: x ******\n"
						+ "** Proof States **\n"
						+ "****** Proof Status for: original PO[org.eventb.core.psStatus] ******\n"
						+ "Is dirty? false\n"
						+ "** Proof Tree **\n"
						+ "{f=ℙ(ℤ×ℤ), x=ℤ}[][2=2, x=1, x∈ℕ, f∈ℕ ⇸ ℕ][] |- x=2		- =>\n"
						+ "1 pending subgoals\n"
						+ "\n"
						+ "** Cached **\n"
						+ "** Searched **\n"
						+ "Current node: {f=ℙ(ℤ×ℤ), x=ℤ}[][2=2, x=1, x∈ℕ, f∈ℕ ⇸ ℕ][] |- x=2		- =>\n"
						+ "1 pending subgoals\n"
						+ "\n"
						+ "****************************\n"
						+ "Current psSatus: original PO[org.eventb.core.psStatus]\n"
						+ "********************************************************\n");
		PSWrapperUtil.removePO(poFile, psFile, prFile, originalPO);
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
				.addSequent(poFile, originalPO, "x = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poFile, dischargedPO, "x = 1", hyp1,
				mTypeEnvironment());
		poFile.save(null, true);
		runBuilder();
		userSupport.setInput(psFile, null);
		PSWrapperUtil
				.copyPO(poFile, psFile, prFile, originalPO, copyOriginalPO);
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
						+ "{f=ℙ(ℤ×ℤ), x=ℤ}[][2=2, x=1, x∈ℕ, f∈ℕ ⇸ ℕ][] |- x=2		- =>\n"
						+ "1 pending subgoals\n"
						+ "\n"
						+ "** Cached **\n"
						+ "** Searched **\n"
						+ "Current node: {f=ℙ(ℤ×ℤ), x=ℤ}[][2=2, x=1, x∈ℕ, f∈ℕ ⇸ ℕ][] |- x=2		- =>\n"
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
				.addSequent(poFile, originalPO, "x = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poFile, dischargedPO, "x = 1", hyp1,
				mTypeEnvironment());
		poFile.save(null, true);
		runBuilder();
		userSupport.setInput(psFile, null);
		String original = userSupport.toString();
		PSWrapperUtil.copyPO(poFile, psFile, prFile, originalPO, dischargedPO);
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
				.addSequent(poFile, originalPO, "x = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poFile, dischargedPO, "x = 1", hyp1,
				mTypeEnvironment());
		poFile.save(null, true);
		runBuilder();
		userSupport.setInput(psFile, null);
		PSWrapperUtil.copyPO(poFile, psFile, prFile, dischargedPO, originalPO);
		assertString(
				"Change: PO is loaded and NOT modified ",
				userSupport.toString(),
				"****** User Support for: x ******\n"
						+ "** Proof States **\n"
						+ "****** Proof Status for: original PO[org.eventb.core.psStatus] ******\n"
						+ "Is dirty? false\n"
						+ "** Proof Tree **\n"
						+ "{f=ℙ(ℤ×ℤ), x=ℤ}[][2=2, x=1, x∈ℕ, f∈ℕ ⇸ ℕ, f(x)∈ℕ, x∈dom(f), f∈ℤ ⇸ ℤ][] |- x=1		hyp <>\n"
						+ "No pending subgoals!\n"
						+ "\n"
						+ "** Cached **\n"
						+ "** Searched **\n"
						+ "Current node: {f=ℙ(ℤ×ℤ), x=ℤ}[][2=2, x=1, x∈ℕ, f∈ℕ ⇸ ℕ, f(x)∈ℕ, x∈dom(f), f∈ℤ ⇸ ℤ][] |- x=1		hyp <>\n"
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
				.addSequent(poFile, originalPO, "x = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poFile, dischargedPO, "x = 1", hyp1,
				mTypeEnvironment());
		poFile.save(null, true);
		runBuilder();
		userSupport.setInput(psFile, null);
		// Modified current PO
		try {
			userSupport.applyTactic(Tactics.review(1), false,
					new NullProgressMonitor());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PSWrapperUtil.copyPO(poFile, psFile, prFile, dischargedPO, originalPO);
		assertString(
				"Change: PO is modified and discharged automatically in DB ",
				userSupport.toString(),
				"****** User Support for: x ******\n"
						+ "** Proof States **\n"
						+ "****** Proof Status for: original PO[org.eventb.core.psStatus] ******\n"
						+ "Is dirty? false\n"
						+ "** Proof Tree **\n"
						+ "{f=ℙ(ℤ×ℤ), x=ℤ}[][2=2, x=1, x∈ℕ, f∈ℕ ⇸ ℕ, f(x)∈ℕ, x∈dom(f), f∈ℤ ⇸ ℤ][] |- x=1		hyp <>\n"
						+ "No pending subgoals!\n"
						+ "\n"
						+ "** Cached **\n"
						+ "** Searched **\n"
						+ "Current node: {f=ℙ(ℤ×ℤ), x=ℤ}[][2=2, x=1, x∈ℕ, f∈ℕ ⇸ ℕ, f(x)∈ℕ, x∈dom(f), f∈ℤ ⇸ ℤ][] |- x=1		hyp <>\n"
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
				.addSequent(poFile, originalPO, "x = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poFile, dischargedPO, "x = 1", hyp1,
				mTypeEnvironment());
		POUtil
				.addSequent(poFile, reusablePO, "x = 2", hyp1,
						mTypeEnvironment());
		poFile.save(null, true);
		runBuilder();
		userSupport.setInput(psFile, null);
		// Modified current PO
		try {
			userSupport.applyTactic(Tactics.review(1), false,
					new NullProgressMonitor());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PSWrapperUtil.copyPO(poFile, psFile, prFile, reusablePO, originalPO);
		assertString(
				"Change: PO is modified and reusable ",
				userSupport.toString(),
				"****** User Support for: x ******\n"
						+ "** Proof States **\n"
						+ "****** Proof Status for: original PO[org.eventb.core.psStatus] ******\n"
						+ "Is dirty? true\n"
						+ "** Proof Tree **\n"
						+ "{f=ℙ(ℤ×ℤ), x=ℤ}[][2=2, x=1, x∈ℕ, f∈ℕ ⇸ ℕ, f(x)∈ℕ, x∈dom(f), f∈ℤ ⇸ ℤ][] |- x=2		rv (1) (x=2) <>\n"
						+ "No pending subgoals!\n"
						+ "\n"
						+ "** Cached **\n"
						+ "** Searched **\n"
						+ "Current node: {f=ℙ(ℤ×ℤ), x=ℤ}[][2=2, x=1, x∈ℕ, f∈ℕ ⇸ ℕ, f(x)∈ℕ, x∈dom(f), f∈ℤ ⇸ ℤ][] |- x=2		rv (1) (x=2) <>\n"
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
				.addSequent(poFile, originalPO, "x∈dom(f)∧f∼;({x} ◁ f)⊆id(ℤ)", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poFile, dischargedPO, "x = 1", hyp1,
				mTypeEnvironment());
		POUtil
		.addSequent(poFile, rebuiltPO, "x = 2", hyp1,
				mTypeEnvironment());
		POUtil
				.addSequent(poFile, reusablePO, "x = 2", hyp1,
						mTypeEnvironment());
		poFile.save(null, true);
		runBuilder();
		userSupport.setInput(psFile, null);
		// Modified current PO
		try {
			userSupport.applyTactic(Tactics.review(1), false,
					new NullProgressMonitor());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PSWrapperUtil.copyPO(poFile, psFile, prFile, rebuiltPO, originalPO);
		assertString(
				"Change: PO is modified and NOT reusable ",
				userSupport.toString(), "****** User Support for: x ******\n" + 
				"** Proof States **\n" + 
				"****** Proof Status for: original PO[org.eventb.core.psStatus] ******\n" + 
				"Is dirty? true\n" + 
				"** Proof Tree **\n" + 
				"{f=ℙ(ℤ×ℤ), x=ℤ}[][2=2, x=1, x∈ℕ, f∈ℕ ⇸ ℕ, f(x)∈ℕ, x∈dom(f), f∈ℤ ⇸ ℤ][] |- x=2		- =>\n" + 
				"1 pending subgoals\n" + 
				"\n" + 
				"** Cached **\n" + 
				"** Searched **\n" + 
				"Current node: {f=ℙ(ℤ×ℤ), x=ℤ}[][2=2, x=1, x∈ℕ, f∈ℕ ⇸ ℕ, f(x)∈ℕ, x∈dom(f), f∈ℤ ⇸ ℤ][] |- x=2		- =>\n" + 
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
