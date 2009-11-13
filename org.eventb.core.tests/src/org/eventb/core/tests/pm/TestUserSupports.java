/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
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

import static java.util.Collections.singleton;

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
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.pm.UserSupport;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Unit tests for class {@link IUserSupport}
 * 
 * @author Laurent Voisin
 */
public class TestUserSupports extends TestPM {

	private static final NullProgressMonitor monitor = new NullProgressMonitor();
	
	private IPORoot poRoot;

	private IUserSupport userSupport;

	private IPSRoot psRoot;

	private void assertDischarged(IProofState state) throws RodinDBException {
		assertTrue("PR " + state.getPSStatus().getElementName()
				+ " should be closed", state.isClosed());
	}

	private void assertNotDischarged(IProofState state) throws RodinDBException {
		assertFalse("PR " + state.getPSStatus().getElementName()
				+ " should not be closed", state.isClosed());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// Turn on beginner mode
		EventBPlugin.getPostTacticPreference().setEnabled(false);
		enableAutoProver(true);
		
		poRoot = createPOFile("x");
		psRoot = poRoot.getPSRoot();
		runBuilder();
		userSupport = newUserSupport(psRoot);
	}
	
	@Override
	protected void tearDown() throws Exception {
		userSupport.dispose();
		super.tearDown();
	}

	public void testSetInput() throws CoreException {
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		// Checks that all POs are discharged except the last one.
		IProofState[] states = userSupport.getPOs();
		for (int i = 0; i < states.length - 1; i++) {
			IProofState state = states[i];
			assertDischarged(state);
		}
		assertNotDischarged(states[states.length - 1]);

		assertEquals("Current PO is the last PO", states[states.length - 1],
				userSupport.getCurrentPO());
	}

	public void testGetInput() throws CoreException {
		final IUserSupport fresh = new UserSupport();
		assertNull("Input for user support has not been set ", fresh.getInput());
		fresh.dispose();

		final IRodinFile input = userSupport.getInput();
		assertEquals("Input for user support has been set ", psRoot, input.getRoot());
	}

	public void testNextUndischargedPOUnforce() throws CoreException {
		userSupport.loadProofStates();

		// Checks that all POs are discharged except the last one.

		IProofState[] states = userSupport.getPOs();

		userSupport.nextUndischargedPO(false, monitor);

		assertEquals("Current PO is still the last PO",
				states[states.length - 1], userSupport.getCurrentPO());

		// Prune the first PO
		userSupport.setCurrentPO(states[0].getPSStatus(), monitor);
		userSupport.applyTactic(Tactics.prune(), false, monitor);

		userSupport.nextUndischargedPO(false, monitor);

		assertEquals("Current Proof State is now the last PO",
				states[states.length - 1], userSupport.getCurrentPO());

		userSupport.nextUndischargedPO(false, monitor);

		assertEquals("Current Proof State is now the first PO", states[0],
				userSupport.getCurrentPO());
	}

	public void testNextUndischargedPOForce() throws CoreException {
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		// Checks that all POs are discharged except the last one.

		userSupport.applyTactic(Tactics.review(1), false, monitor);

		IProofState[] states = userSupport.getPOs();

		userSupport.nextUndischargedPO(true, monitor);

		assertNull("Current PO is null", userSupport.getCurrentPO());

		// Prune the last PO
		userSupport.setCurrentPO(states[states.length - 1].getPSStatus(),
				monitor);
		userSupport.applyTactic(Tactics.prune(), false, monitor);

		userSupport.nextUndischargedPO(true, monitor);

		assertEquals("Current Proof State is now the last PO",
				states[states.length - 1], userSupport.getCurrentPO());

		// Prune the first PO
		userSupport.setCurrentPO(states[0].getPSStatus(), monitor);
		userSupport.applyTactic(Tactics.prune(), false, monitor);

		userSupport.nextUndischargedPO(true, monitor);

		assertEquals("Current Proof State is now the last PO",
				states[states.length - 1], userSupport.getCurrentPO());

		userSupport.nextUndischargedPO(true, monitor);

		assertEquals("Current Proof State is now the first PO", userSupport
				.getCurrentPO(), states[0]);
	}

	public void testPrevUndischargedPOUnforce() throws CoreException {
		userSupport.loadProofStates();

		IProofState[] states = userSupport.getPOs();

		userSupport.prevUndischargedPO(false, monitor);

		assertEquals("Current PO is still the last PO",
				states[states.length - 1], userSupport.getCurrentPO());

		// Prune the first PO
		userSupport.setCurrentPO(states[0].getPSStatus(), monitor);
		userSupport.applyTactic(Tactics.prune(), false, monitor);

		userSupport.prevUndischargedPO(false, monitor);

		assertEquals("Current Proof State is now the last PO",
				states[states.length - 1], userSupport.getCurrentPO());

		userSupport.prevUndischargedPO(false, monitor);

		assertEquals("Current Proof State is now the first PO", states[0],
				userSupport.getCurrentPO());
	}

	public void testPrevUndischargedPOForce() throws CoreException {
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		userSupport.applyTactic(Tactics.review(1), false, monitor);
		userSupport.searchHyps("");
		// Check delta

		Collection<Predicate> hypotheses = userSupport.getCurrentPO()
				.getSearched();
		IProofState[] states = userSupport.getPOs();
		userSupport.prevUndischargedPO(true, monitor);

		assertNull("Current PO is null", userSupport.getCurrentPO());

		Set<Predicate> hyps = new HashSet<Predicate>();
		Predicate hypothesis = hypotheses.iterator().next();
		hyps.add(hypothesis);

		// Prune the last PO
		userSupport.setCurrentPO(states[states.length - 1].getPSStatus(),
				monitor);
		userSupport.applyTactic(Tactics.prune(), false, monitor);

		userSupport.prevUndischargedPO(true, monitor);

		assertEquals("Current Proof State is now the last PO",
				states[states.length - 1], userSupport.getCurrentPO());

		// Prune the first PO
		userSupport.setCurrentPO(states[0].getPSStatus(), monitor);
		userSupport.applyTactic(Tactics.prune(), false, monitor);

		userSupport.prevUndischargedPO(true, monitor);

		assertEquals("Current Proof State is now the last PO",
				states[states.length - 1], userSupport.getCurrentPO());

		userSupport.prevUndischargedPO(true, monitor);

		assertEquals("Current Proof State is now the first PO", userSupport
				.getCurrentPO(), states[0]);
	}

	public void testSetAndGetCurrentPO() throws CoreException {
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		IProofState[] states = userSupport.getPOs();

		assertEquals("Current PO is the last PO ", states[states.length - 1],
				userSupport.getCurrentPO());

		// Select first PO
		userSupport.setCurrentPO(states[0].getPSStatus(), monitor);

		assertEquals("Current PO is the first PO ", states[0], userSupport
				.getCurrentPO());

		// Select the last PO again
		userSupport.setCurrentPO(states[states.length - 1].getPSStatus(),
				monitor);

		assertEquals("Current PO is the last PO again ",
				states[states.length - 1], userSupport.getCurrentPO());
	}

	public void testGetPOs() throws CoreException {
		// Check that the POs are not yet loaded
		assertEquals("There should be no PO loaded ", 0,
				userSupport.getPOs().length);

		// Checks that all POs are consistent discharged except the last one.
		userSupport.loadProofStates();
		IProofState[] states = userSupport.getPOs();
		assertEquals("There should be 7 POs ", 7, states.length);
		// TODO add test on PO statuses?
	}

	public void testHasUnsavedChanges() throws CoreException {
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		assertFalse("Initially, there are no unsaved changes ", userSupport
				.hasUnsavedChanges());

		// Checks that all POs are discharged except the last one.

		userSupport.applyTactic(Tactics.review(1), false, monitor);

		assertTrue("There are unsaved changes after applying a tactic ",
				userSupport.hasUnsavedChanges());

		IProofState[] states = userSupport.getPOs();

		userSupport.getCurrentPO().setProofTree(monitor);

		assertFalse("After saving, there are no unsaved changes ", userSupport
				.hasUnsavedChanges());

		// Prune the last PO
		userSupport.setCurrentPO(states[states.length - 1].getPSStatus(),
				monitor);
		userSupport.applyTactic(Tactics.prune(), false, monitor);

		assertTrue("There are unsaved changes after pruning a proof ",
				userSupport.hasUnsavedChanges());
	}

	public void testGetUnsavedPOs() throws CoreException {
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		IProofState[] unsavedPOs = userSupport.getUnsavedPOs();
		assertEquals("Initially, there are no unsaved PO ", 0,
				unsavedPOs.length);

		userSupport.applyTactic(Tactics.review(1), false, monitor);
		unsavedPOs = userSupport.getUnsavedPOs();
		assertEquals("There are 1 unsaved changes after applying a tactic ", 1,
				unsavedPOs.length);
		IProofState[] states = userSupport.getPOs();

		assertEquals("The unsavedPO is the last one ",
				states[states.length - 1], unsavedPOs[0]);

		IProofState currentPO = userSupport.getCurrentPO();
		currentPO.setProofTree(monitor);
		unsavedPOs = userSupport.getUnsavedPOs();
		assertEquals("After saving, there are no unsaved changes ", 0,
				unsavedPOs.length);

		// Prune the last PO
		userSupport.setCurrentPO(states[states.length - 1].getPSStatus(),
				monitor);
		userSupport.applyTactic(Tactics.prune(), false, monitor);

		// Prune the first PO
		userSupport.setCurrentPO(states[0].getPSStatus(), monitor);
		userSupport.applyTactic(Tactics.prune(), false, monitor);

		unsavedPOs = userSupport.getUnsavedPOs();
		assertEquals("there are 2 unsaved PO ", 2, unsavedPOs.length);

		assertContain("The first PO is unsaved ", unsavedPOs, states[0]);
		assertContain("The last PO is unsaved ", unsavedPOs,
				states[states.length - 1]);
	}

	private void assertContain(String msg, IProofState[] unsavedPOs,
			IProofState state) {
		boolean found = false;
		for (IProofState unsavedPO : unsavedPOs) {
			if (unsavedPO == state) {
				found = true;
				break;
			}
		}

		assertTrue(msg, found);
	}

	public void testRemoveCachedHypotheses() throws CoreException {
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		userSupport.applyTactic(Tactics.lemma("1 = 1"), true, monitor);
		ITactic defaultPostTactic = EventBPlugin.getPostTacticPreference().getDefaultComposedTactic();
		userSupport.applyTactic(defaultPostTactic, true, monitor); // Discharge true goal
		userSupport.applyTactic(defaultPostTactic, true, monitor); // Discharge 1 = 1
		userSupport.applyTactic(Tactics.lemma("2 = 2"), true, monitor);
		userSupport.applyTactic(defaultPostTactic, true, monitor); // Discharge true goal
		userSupport.applyTactic(defaultPostTactic, true, monitor); // Discharge 2 = 2
		IProofState currentPO = userSupport.getCurrentPO();
		Iterable<Predicate> selectedHyps = currentPO.getCurrentNode().getSequent()
				.selectedHypIterable();

		Iterator<Predicate> iterator = selectedHyps.iterator();
		Predicate hyp1 = iterator.next();
		Set<Predicate> hyps1 = new HashSet<Predicate>();
		hyps1.add(hyp1);
		userSupport.applyTacticToHypotheses(Tactics.falsifyHyp(hyp1), hyps1, true,
				monitor);

		Collection<Predicate> cached = currentPO.getCached();
		assertTrue("Cache has 1 element ", cached.size() == 1);

		Set<Predicate> hyps2 = new HashSet<Predicate>();
		Predicate hyp2 = iterator.next();
		hyps2.add(hyp2);
		userSupport.applyTacticToHypotheses(Tactics.falsifyHyp(hyp2), hyps2, true,
				monitor);

		cached = currentPO.getCached();
		assertTrue("Cache has 2 elements ", cached.size() == 2);

		userSupport.removeCachedHypotheses(hyps1);

		cached = currentPO.getCached();
		assertTrue("Cache has 1 element ", cached.size() == 1);
		assertTrue("Cache contains the second hyp ", cached.contains(hyp2));

		userSupport.removeCachedHypotheses(hyps2);
		cached = currentPO.getCached();
		assertTrue("Cache is now empty ", cached.size() == 0);
	}

	public void testSearchHypotheses() throws CoreException {
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		IProofState currentPO = userSupport.getCurrentPO();

		userSupport.searchHyps("=");

		Collection<Predicate> searched = currentPO.getSearched();
		assertEquals("Unexpected search size", 2, searched.size());

		userSupport.searchHyps("Empty search");

		searched = currentPO.getSearched();
		assertTrue("Search should be empty ", searched.isEmpty());
	}

	public void testRemoveSearchedHypotheses() throws CoreException {
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		userSupport.searchHyps("=");

		IProofState currentPO = userSupport.getCurrentPO();
		Collection<Predicate> searched = currentPO.getSearched();

		assertEquals("Unexpected search size", 2, searched.size());

		Iterator<Predicate> iterator = searched.iterator();
		Predicate hyp1 = iterator.next();
		Predicate hyp2 = iterator.next();

		userSupport.removeSearchedHypotheses(singleton(hyp2));
		searched = currentPO.getSearched();
		assertFalse("Second hypothesis has been removed ", searched
				.contains(hyp2));
		assertEquals("Unexpected search size", 1, searched.size());

		userSupport.removeSearchedHypotheses(singleton(hyp1));
		searched = currentPO.getSearched();
		assertTrue("Search should be empty", searched.isEmpty());
	}

	public void testSelectNode() throws CoreException {
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		IProofState currentPO = userSupport.getCurrentPO();

		IProofTreeNode node1 = currentPO.getCurrentNode();

		userSupport.applyTactic(Tactics.lemma("3 = 3"), true, monitor);

		IProofTreeNode node2 = currentPO.getCurrentNode();

		userSupport.selectNode(node1);

		assertEquals("Current node is node 1 ", node1, currentPO
				.getCurrentNode());

		userSupport.selectNode(node2);

		assertEquals("Current node is node 2 ", node2, currentPO
				.getCurrentNode());

		userSupport.selectNode(node2);

		assertEquals("Select node 2 again has no effect ", node2, currentPO
				.getCurrentNode());

		userSupport.applyTactic(EventBPlugin.getPostTacticPreference()
				.getDefaultComposedTactic(), true, monitor);

		userSupport.selectNode(node1);

		assertEquals("Current node is node 1 ", node1, currentPO
				.getCurrentNode());
	}

	public void testApplyTactic() throws CoreException {
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		IProofState currentPO = userSupport.getCurrentPO();

		IProofTreeNode node1 = currentPO.getCurrentNode();

		userSupport.applyTactic(Tactics.lemma("2 = 3"), true, monitor);

		IProofTreeNode node2 = currentPO.getCurrentNode();
		assertTrue("Node 2 is open ", node2.isOpen());
		assertTrue("Node 2 is a child of node 1 ", node2.getParent() == node1);
	}

	public void testApplyTacticToHypothesis() throws CoreException {
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		IProofState currentPO = userSupport.getCurrentPO();

		userSupport.searchHyps("=");

		Collection<Predicate> searched = currentPO.getSearched();
		assertEquals("Unexpected search size", 2, searched.size());

		Iterator<Predicate> iterator = searched.iterator();
		Predicate hyp1 = iterator.next();

		Set<Predicate> hyps1 = new HashSet<Predicate>();
		hyps1.add(hyp1);

		userSupport.applyTacticToHypotheses(Tactics.falsifyHyp(hyp1), hyps1, true,
				monitor);

		Collection<Predicate> cached = currentPO.getCached();
		assertTrue("Hypothesis is added to the cache ", cached.contains(hyp1));
	}

	public void testBack() throws CoreException {
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		IProofState currentPO = userSupport.getCurrentPO();

		IProofTreeNode node1 = currentPO.getCurrentNode();

		userSupport.applyTactic(Tactics.lemma("3 = 3"), true, monitor);
		userSupport.back(monitor);
		assertEquals("Back to node 1 ", node1, currentPO.getCurrentNode());
		assertTrue("Node 1 is open again ", node1.isOpen());
	}

	public void testSearchConsiderHiddenHypotheses() throws Exception {
		// Select the first undischarged PO.
		userSupport.nextUndischargedPO(false, monitor);

		IProofState currentPO = userSupport.getCurrentPO();

		userSupport.searchHyps("=");

		final Collection<Predicate> searched = currentPO.getSearched();
		assertEquals("Unexpected search size", 2, searched.size());
		
		// consider hidden hypotheses
		manager.setConsiderHiddenHypotheses(true);
		
		userSupport.searchHyps("=");

		final Collection<Predicate> searchedWithHidden = currentPO.getSearched();
		assertEquals("Unexpected search size", 3, searchedWithHidden.size());
	}
}
