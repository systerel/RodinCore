/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests.pm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPORoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.pm.UserSupport;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Unit tests for class {@link IUserSupportManager}
 * 
 * @author htson
 */
public class TestUserSupportDeltas extends TestPMDelta {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// Turn on beginner mode
		EventBPlugin.getPostTacticPreference().setEnabled(false);
		enableAutoProver(true);
	}

	public void testSetInput() throws CoreException {
		IPORoot poFile = createPOFile("x");
		IPSRoot psRoot = poFile.getPSRoot();

		runBuilder();

		IUserSupport userSupport = new UserSupport();

		startDeltas();
		userSupport.setInput(psRoot.getRodinFile());
		assertDeltas("No deltas should have been produced", "");
		
		// The proof states has been created but not initialized for all POs.
		clearDeltas();
		userSupport.loadProofStates();
		assertDeltas("Set input ", "[*] x.bps [STATE]\n" + 
				"  [+] PO1[org.eventb.core.psStatus] []\n" + 
				"  [+] PO2[org.eventb.core.psStatus] []\n" + 
				"  [+] PO3[org.eventb.core.psStatus] []\n" + 
				"  [+] PO4[org.eventb.core.psStatus] []\n" + 
				"  [+] PO5[org.eventb.core.psStatus] []\n" + 
				"  [+] PO6[org.eventb.core.psStatus] []\n" + 
				"  [+] PO7[org.eventb.core.psStatus] []");
		stopDeltas();
		userSupport.dispose();
	}

	public void testNextUndischargedPOUnforce() throws RodinDBException,
			CoreException {
		IPORoot poFile = createPOFile("x");
		IRodinFile psFile = poFile.getPSRoot().getRodinFile();

		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile);

		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);
		
		IProofState[] states = userSupport.getPOs();

		startDeltas();
		userSupport.nextUndischargedPO(false, monitor);
		assertDeltas("Next PO failed ", "[*] x.bps [INFORMATION]\n"
				+ "No new obligation (priority 1)");

		// Prune the first PO
		userSupport.setCurrentPO(states[0].getPSStatus(), monitor);
		userSupport.applyTactic(Tactics.prune(), false, monitor);

		clearDeltas();
		userSupport.nextUndischargedPO(false, monitor);
		assertDeltas("Next PO to the last PO",
				"[*] x.bps [CURRENT|INFORMATION]\n"
						+ "New current obligation (priority 2)");

		clearDeltas();
		userSupport.nextUndischargedPO(false, monitor);
		assertDeltas("Next PO to the first PO",
				"[*] x.bps [CURRENT|INFORMATION]\n"
						+ "New current obligation (priority 2)");
		stopDeltas();
		userSupport.dispose();
	}

	public void testNextUndischargedPOForce() throws RodinDBException,
			CoreException {
		IPORoot poFile = createPOFile("x");
		IRodinFile psFile = poFile.getPSRoot().getRodinFile();

		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		userSupport.applyTactic(Tactics.review(1), false, monitor);

		IProofState[] states = userSupport.getPOs();
		startDeltas();
		userSupport.nextUndischargedPO(true, monitor);

		assertDeltas("Next PO to null", "[*] x.bps [CURRENT|INFORMATION]\n"
				+ "New current obligation (priority 2)\n"
				+ "No un-discharged proof obligation found (priority 2)");

		// Prune the last PO
		userSupport.setCurrentPO(states[states.length - 1].getPSStatus(),
				monitor);
		userSupport.applyTactic(Tactics.prune(), false, monitor);

		clearDeltas();
		userSupport.nextUndischargedPO(true, monitor);

		assertDeltas("Next PO to the last PO (no delta) ",
				"[*] x.bps [INFORMATION]\n" + "No new obligation (priority 1)");

		// Prune the first PO
		userSupport.setCurrentPO(states[0].getPSStatus(), monitor);
		userSupport.applyTactic(Tactics.prune(), false, monitor);

		clearDeltas();
		userSupport.nextUndischargedPO(true, monitor);
		assertDeltas(
				"Next PO to the last PO (with delta) ",
				"[*] x.bps [CURRENT|INFORMATION]\n"
						+ "New current obligation (priority 2)");

		clearDeltas();
		userSupport.nextUndischargedPO(true, monitor);
		assertDeltas("Next PO to the first PO ",
				"[*] x.bps [CURRENT|INFORMATION]\n"
						+ "New current obligation (priority 2)");
		stopDeltas();
		userSupport.dispose();
	}

	public void testUserSupportPrevUndischargedPOUnforce()
			throws RodinDBException, CoreException {
		IPORoot poFile = createPOFile("x");
		IRodinFile psFile = poFile.getPSRoot().getRodinFile();

		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		IProofState[] states = userSupport.getPOs();

		startDeltas();
		userSupport.prevUndischargedPO(false, monitor);
		assertDeltas("Previous PO failed ", "[*] x.bps [INFORMATION]\n"
				+ "No new obligation (priority 1)");

		// Prune the first PO
		userSupport.setCurrentPO(states[0].getPSStatus(), monitor);
		userSupport.applyTactic(Tactics.prune(), false, monitor);

		clearDeltas();
		userSupport.prevUndischargedPO(false, monitor);
		assertDeltas("Previous PO to the last PO",
				"[*] x.bps [CURRENT|INFORMATION]\n"
						+ "New current obligation (priority 2)");

		clearDeltas();
		userSupport.prevUndischargedPO(false, monitor);
		assertDeltas("Previous PO to the first PO",
				"[*] x.bps [CURRENT|INFORMATION]\n"
						+ "New current obligation (priority 2)");
		stopDeltas();
		userSupport.dispose();
	}

	public void testUserSupportPrevUndischargedPOForce()
			throws RodinDBException, CoreException {
		IPORoot poFile = createPOFile("x");
		IRodinFile psFile = poFile.getPSRoot().getRodinFile();

		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		userSupport.applyTactic(Tactics.review(1), false, monitor);

		IProofState[] states = userSupport.getPOs();
		startDeltas();
		userSupport.prevUndischargedPO(true, monitor);

		assertDeltas("Previous PO to null", "[*] x.bps [CURRENT|INFORMATION]\n"
				+ "New current obligation (priority 2)\n"
				+ "No un-discharged proof obligation found (priority 2)");

		// Prune the last PO
		userSupport.setCurrentPO(states[states.length - 1].getPSStatus(),
				monitor);
		userSupport.applyTactic(Tactics.prune(), false, monitor);

		clearDeltas();
		userSupport.prevUndischargedPO(true, monitor);

		assertDeltas("Next PO to the last PO (no delta) ",
				"[*] x.bps [INFORMATION]\n" + "No new obligation (priority 1)");

		// Prune the first PO
		userSupport.setCurrentPO(states[0].getPSStatus(), monitor);
		userSupport.applyTactic(Tactics.prune(), false, monitor);

		clearDeltas();
		userSupport.prevUndischargedPO(true, monitor);
		assertDeltas("Next PO to the last PO (with delta) ",
				"[*] x.bps [CURRENT|INFORMATION]\n"
						+ "New current obligation (priority 2)");

		clearDeltas();
		userSupport.prevUndischargedPO(true, monitor);
		assertDeltas("Next PO to the first PO ",
				"[*] x.bps [CURRENT|INFORMATION]\n"
						+ "New current obligation (priority 2)");
		stopDeltas();
		userSupport.dispose();
	}

	public void testSetAndGetCurrentPO() throws CoreException {
		IPORoot poFile = createPOFile("x");
		IRodinFile psFile = poFile.getPSRoot().getRodinFile();

		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		IProofState[] states = userSupport.getPOs();

		startDeltas();
		userSupport.setCurrentPO(states[states.length - 1].getPSStatus(),
				monitor);
		assertDeltas("No delta if select the same PO ",
				"[*] x.bps [INFORMATION]\n" + "No new obligation (priority 1)");

		// Select first PO
		startDeltas();
		userSupport.setCurrentPO(states[0].getPSStatus(), monitor);
		assertDeltas(
				"Select the first PO ",
				"[*] x.bps [CURRENT|STATE|INFORMATION]\n"
						+ "Select a new proof node (priority 1)\n"
						+ "Proof Tree is reloaded (priority 2)\n"
						+ "New current obligation (priority 2)\n"
						+ "  [*] PO1[org.eventb.core.psStatus] [CACHE|SEARCH|NODE|PROOFTREE]");

		// Select the last PO again
		startDeltas();
		userSupport.setCurrentPO(states[states.length - 1].getPSStatus(),
				monitor);
		assertDeltas("Current PO is the last PO again ",
				"[*] x.bps [CURRENT|INFORMATION]\n"
						+ "New current obligation (priority 2)");

		stopDeltas();
		userSupport.dispose();
	}

	public void testRemoveCachedHypotheses() throws CoreException {
		IPORoot poFile = createPOFile("x");
		IRodinFile psFile = poFile.getPSRoot().getRodinFile();

		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		userSupport.applyTactic(Tactics.lemma("1 = 1"), true, monitor);
		ITactic defaultPostTactic = EventBPlugin.getPostTacticPreference()
				.getDefaultComposedTactic();
		userSupport.applyTactic(defaultPostTactic, true, monitor);
		userSupport.applyTactic(defaultPostTactic, true, monitor);
		userSupport.applyTactic(Tactics.lemma("2 = 2"), true, monitor);
		userSupport.applyTactic(defaultPostTactic, true, monitor);
		userSupport.applyTactic(defaultPostTactic, true, monitor);
		IProofState currentPO = userSupport.getCurrentPO();
		
		IProverSequent sequent = currentPO.getCurrentNode().getSequent();
		Iterable<Predicate> selectedHypIterable = sequent.selectedHypIterable();

		Iterator<Predicate> iterator = selectedHypIterable.iterator();
		Predicate hyp1 = iterator.next();
		Set<Predicate> hyps1 = new HashSet<Predicate>();
		hyps1.add(hyp1);
		userSupport.applyTacticToHypotheses(Tactics.falsifyHyp(hyp1), hyps1, true,
				monitor);

		Set<Predicate> hyps2 = new HashSet<Predicate>();
		Predicate hyp2 = iterator.next();
		hyps2.add(hyp2);
		userSupport.applyTacticToHypotheses(Tactics.falsifyHyp(hyp2), hyps2, true,
				monitor);

		startDeltas();
		userSupport.removeCachedHypotheses(hyps1);
		assertDeltas("First hypothesis has been removed ",
				"[*] x.bps [STATE|INFORMATION]\n"
						+ "Removed hypotheses from cache (priority 2)\n"
						+ "  [*] PO7[org.eventb.core.psStatus] [CACHE]");

		clearDeltas();
		userSupport.removeCachedHypotheses(hyps2);
		assertDeltas("Second hypothesis has been removed ",
				"[*] x.bps [STATE|INFORMATION]\n"
						+ "Removed hypotheses from cache (priority 2)\n"
						+ "  [*] PO7[org.eventb.core.psStatus] [CACHE]");

		stopDeltas();
		userSupport.dispose();
	}

	public void testSearchHypotheses() throws CoreException {
		IPORoot poFile = createPOFile("x");
		IRodinFile psFile = poFile.getPSRoot().getRodinFile();

		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		startDeltas();
		userSupport.searchHyps("=");

		assertDeltas("Search is successful ", "[*] x.bps [STATE|INFORMATION]\n"
				+ "Search hypotheses (priority 2)\n"
				+ "  [*] PO7[org.eventb.core.psStatus] [SEARCH]");
		clearDeltas();
		userSupport.searchHyps("Empty search");

		assertDeltas("Search is unsuccessful ",
				"[*] x.bps [STATE|INFORMATION]\n"
						+ "Search hypotheses (priority 2)\n"
						+ "  [*] PO7[org.eventb.core.psStatus] [SEARCH]");
		stopDeltas();
		userSupport.dispose();
	}

	public void testRemoveSearchedHypotheses() throws CoreException {
		IPORoot poFile = createPOFile("x");
		IRodinFile psFile = poFile.getPSRoot().getRodinFile();

		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		userSupport.searchHyps("=");
		IProofState currentPO = userSupport.getCurrentPO();
		Collection<Predicate> searched = currentPO.getSearched();

		assertTrue("Search has 3 elements ", searched.size() == 3);

		Iterator<Predicate> iterator = searched.iterator();
		Predicate hyp1 = iterator.next();
		Predicate hyp2 = iterator.next();
		Predicate hyp3 = iterator.next();

		Collection<Predicate> hyps2 = new ArrayList<Predicate>();
		hyps2.add(hyp2);
		Collection<Predicate> hyps13 = new ArrayList<Predicate>();
		hyps13.add(hyp1);
		hyps13.add(hyp3);

		startDeltas();
		userSupport.removeSearchedHypotheses(hyps2);
		assertDeltas("Second hypothesis has been removed ",
				"[*] x.bps [STATE|INFORMATION]\n"
						+ "Removed hypotheses from search (priority 2)\n"
						+ "  [*] PO7[org.eventb.core.psStatus] [SEARCH]");

		clearDeltas();
		userSupport.removeSearchedHypotheses(hyps13);
		assertDeltas("First and third hypotheses has been removed ",
				"[*] x.bps [STATE|INFORMATION]\n"
						+ "Removed hypotheses from search (priority 2)\n"
						+ "  [*] PO7[org.eventb.core.psStatus] [SEARCH]");
		stopDeltas();
		userSupport.dispose();
	}

	public void testSelectNode() throws CoreException {
		IPORoot poFile = createPOFile("x");
		IRodinFile psFile = poFile.getPSRoot().getRodinFile();

		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		IProofState currentPO = userSupport.getCurrentPO();

		IProofTreeNode node1 = currentPO.getCurrentNode();

		userSupport.applyTactic(Tactics.lemma("3 = 3"), true, monitor);

		IProofTreeNode node2 = currentPO.getCurrentNode();

		startDeltas();
		userSupport.selectNode(node1);

		assertDeltas("Current node is changed for PO7 ",
				"[*] x.bps [STATE|INFORMATION]\n"
						+ "Select a new proof node (priority 1)\n"
						+ "  [*] PO7[org.eventb.core.psStatus] [NODE]");

		clearDeltas();
		userSupport.selectNode(node2);

		assertDeltas("Current node is changed again for PO7 ",
				"[*] x.bps [STATE|INFORMATION]\n"
						+ "Select a new proof node (priority 1)\n"
						+ "  [*] PO7[org.eventb.core.psStatus] [NODE]");

		clearDeltas();
		userSupport.selectNode(node2);

		assertDeltas("Select the same current node has no effect ",
				"[*] x.bps [INFORMATION]\n"
						+ "Not a new proof node (priority 1)");

		userSupport.applyTactic(EventBPlugin.getPostTacticPreference()
				.getDefaultComposedTactic(), true, monitor);
		clearDeltas();
		userSupport.selectNode(node1);

		assertEquals("Current node is node 1 ", node1, currentPO
				.getCurrentNode());

		assertDeltas("Current node is changed again for PO7 ",
				"[*] x.bps [STATE|INFORMATION]\n"
						+ "Select a new proof node (priority 1)\n"
						+ "  [*] PO7[org.eventb.core.psStatus] [NODE]");

		stopDeltas();
		userSupport.dispose();
	}

	public void testApplyTactic() throws CoreException {
		IPORoot poFile = createPOFile("x");
		IRodinFile psFile = poFile.getPSRoot().getRodinFile();

		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		startDeltas();
		userSupport.applyTactic(Tactics.lemma("3 = 3"), true, monitor);
		assertDeltas(
				"Apply tactic successful ",
				"[*] x.bps [STATE|INFORMATION]\n"
						+ "Tactic applied successfully (priority 2)\n"
						+ "Select a new proof node (priority 1)\n"
						+ "  [*] PO7[org.eventb.core.psStatus] [NODE|PROOFTREE]");
		stopDeltas();
		userSupport.dispose();
	}

	public void testApplyTacticHypothesis() throws CoreException {
		IPORoot poFile = createPOFile("x");
		IRodinFile psFile = poFile.getPSRoot().getRodinFile();

		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		IProofState currentPO = userSupport.getCurrentPO();

		userSupport.searchHyps("=");

		Collection<Predicate> searched = currentPO.getSearched();
		assertTrue("Search size is 3 ", searched.size() == 3);

		Iterator<Predicate> iterator = searched.iterator();
		Predicate hyp1 = iterator.next();

		Set<Predicate> hyps1 = new HashSet<Predicate>();
		hyps1.add(hyp1);
		startDeltas();
		userSupport.applyTacticToHypotheses(Tactics.falsifyHyp(hyp1), hyps1, true,
				monitor);
		assertDeltas(
				"Apply tactic successful ",
				"[*] x.bps [STATE|INFORMATION]\n"
						+ "Tactic applied successfully (priority 2)\n"
						+ "Select a new proof node (priority 1)\n"
						+ "  [*] PO7[org.eventb.core.psStatus] [CACHE|NODE|PROOFTREE]");

		stopDeltas();
		userSupport.dispose();
	}

	public void testBacktrack() throws CoreException {
		IPORoot poFile = createPOFile("x");
		IRodinFile psFile = poFile.getPSRoot().getRodinFile();

		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile);
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		userSupport.applyTactic(Tactics.lemma("3 = 3"), true, monitor);

		startDeltas();
		userSupport.back(monitor);

		assertDeltas(
				"Apply backtrack successful ",
				"[*] x.bps [STATE|INFORMATION]\n"
						+ "Tactic applied successfully (priority 2)\n"
						+ "Select a new proof node (priority 1)\n"
						+ "  [*] PO7[org.eventb.core.psStatus] [NODE|PROOFTREE]");

		stopDeltas();
		userSupport.dispose();
	}

}
