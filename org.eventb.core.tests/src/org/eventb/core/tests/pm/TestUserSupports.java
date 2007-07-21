/**
 * 
 */
package org.eventb.core.tests.pm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.IPSFile;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.pm.UserSupport;
import org.eventb.internal.core.pom.AutoProver;
import org.rodinp.core.RodinDBException;

/**
 * Unit tests for class {@link IUserSupport}
 * 
 * @author Laurent Voisin
 */
public class TestUserSupports extends TestPM {

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
		manager.getPostTacticContainer().setEnable(false);
	}

	public void testSetInput() throws CoreException {
		IPOFile poFile = createPOFile("x");
		IPSFile psFile = poFile.getPSFile();

		AutoProver.enable();
		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile, monitor);

		// Checks that all POs are discharged except the last one.
		IProofState[] states = userSupport.getPOs();
		for (int i = 0; i < states.length - 1; i++) {
			IProofState state = states[i];
			assertDischarged(state);
		}
		assertNotDischarged(states[states.length - 1]);

		assertEquals("Current PO is the last PO", states[states.length - 1],
				userSupport.getCurrentPO());
		userSupport.dispose();
	}

	public void testGetInput() throws CoreException {
		IPOFile poFile = createPOFile("x");
		IPSFile psFile = poFile.getPSFile();

		AutoProver.enable();
		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();

		IPSFile input = userSupport.getInput();

		assertNull("Input for user support has not been set ", input);

		userSupport.setInput(psFile, monitor);

		input = userSupport.getInput();

		assertEquals("Input for user support has been set ", psFile, input);

		userSupport.dispose();
	}

	public void testNextUndischargedPOUnforce() throws CoreException {
		IPOFile poFile = createPOFile("x");
		IPSFile psFile = poFile.getPSFile();

		AutoProver.enable();
		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile, monitor);

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

		userSupport.dispose();
	}

	public void testNextUndischargedPOForce() throws CoreException {
		IPOFile poFile = createPOFile("x");
		IPSFile psFile = poFile.getPSFile();

		AutoProver.enable();
		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile, monitor);

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

		userSupport.dispose();
	}

	public void testPrevUndischargedPOUnforce() throws CoreException {
		IPOFile poFile = createPOFile("x");
		IPSFile psFile = poFile.getPSFile();

		AutoProver.enable();
		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile, monitor);

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

		userSupport.dispose();
	}

	public void testPrevUndischargedPOForce() throws CoreException {
		IPOFile poFile = createPOFile("x");
		IPSFile psFile = poFile.getPSFile();

		AutoProver.enable();
		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile, monitor);

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

		userSupport.dispose();
	}

	public void testSetAndGetCurrentPO() throws CoreException {
		IPOFile poFile = createPOFile("x");
		IPSFile psFile = poFile.getPSFile();

		AutoProver.enable();
		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile, monitor);

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

		userSupport.dispose();
	}

	public void testGetPOs() throws CoreException {
		IPOFile poFile = createPOFile("x");
		IPSFile psFile = poFile.getPSFile();

		AutoProver.enable();
		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile, monitor);

		// Checks that all POs are consistent discharged except the last one.
		IProofState[] states = userSupport.getPOs();

		assertEquals("There should be 7 POs ", 7, states.length);

		userSupport.dispose();
	}

	public void testHasUnsavedChanges() throws CoreException {
		IPOFile poFile = createPOFile("x");
		IPSFile psFile = poFile.getPSFile();

		AutoProver.enable();
		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile, monitor);

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

		userSupport.dispose();
	}

	public void testGetUnsavedPOs() throws CoreException {
		IPOFile poFile = createPOFile("x");
		IPSFile psFile = poFile.getPSFile();

		AutoProver.enable();
		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile, monitor);

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

		userSupport.dispose();

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
		IPOFile poFile = createPOFile("x");
		IPSFile psFile = poFile.getPSFile();

		AutoProver.enable();
		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile, monitor);

		userSupport.applyTactic(Tactics.lemma("1 = 1"), true, monitor);
		userSupport.applyTactic(Tactics.postProcessExpert(), true, monitor); // Discharge true goal
		userSupport.applyTactic(Tactics.postProcessExpert(), true, monitor); // Discharge 1 = 1
		userSupport.applyTactic(Tactics.lemma("2 = 2"), true, monitor);
		userSupport.applyTactic(Tactics.postProcessExpert(), true, monitor); // Discharge true goal
		userSupport.applyTactic(Tactics.postProcessExpert(), true, monitor); // Discharge 2 = 2
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

		userSupport.dispose();
	}

	public void testSearchHypotheses() throws CoreException {
		IPOFile poFile = createPOFile("x");
		IPSFile psFile = poFile.getPSFile();

		AutoProver.enable();
		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile, monitor);

		IProofState currentPO = userSupport.getCurrentPO();

		userSupport.searchHyps("=");

		Collection<Predicate> searched = currentPO.getSearched();
		assertTrue("Search size is 3 ", searched.size() == 3);

		userSupport.searchHyps("Empty search");

		searched = currentPO.getSearched();
		assertTrue("Search is empty ", searched.size() == 0);

		userSupport.dispose();
	}

	public void testRemoveSearchedHypotheses() throws CoreException {
		IPOFile poFile = createPOFile("x");
		IPSFile psFile = poFile.getPSFile();

		AutoProver.enable();
		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile, monitor);

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

		userSupport.removeSearchedHypotheses(hyps2);
		searched = currentPO.getSearched();
		assertFalse("Second hypothesis has been removed ", searched
				.contains(hyp2));
		assertTrue("Search has 2 elements ", searched.size() == 2);

		userSupport.removeSearchedHypotheses(hyps13);
		searched = currentPO.getSearched();
		assertTrue("Search has no elements ", searched.size() == 0);

		userSupport.dispose();
	}

	public void testSelectNode() throws CoreException {
		IPOFile poFile = createPOFile("x");
		IPSFile psFile = poFile.getPSFile();

		AutoProver.enable();
		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile, monitor);

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

		userSupport.applyTactic(Tactics.postProcessExpert(), true, monitor);

		userSupport.selectNode(node1);

		assertEquals("Current node is node 1 ", node1, currentPO
				.getCurrentNode());

		userSupport.dispose();
	}

	public void testApplyTactic() throws CoreException {
		IPOFile poFile = createPOFile("x");
		IPSFile psFile = poFile.getPSFile();

		AutoProver.enable();
		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile, monitor);

		IProofState currentPO = userSupport.getCurrentPO();

		IProofTreeNode node1 = currentPO.getCurrentNode();

		userSupport.applyTactic(Tactics.lemma("2 = 3"), true, monitor);

		IProofTreeNode node2 = currentPO.getCurrentNode();
		assertTrue("Node 2 is open ", node2.isOpen());
		assertTrue("Node 2 is a child of node 1 ", node2.getParent() == node1);

		userSupport.dispose();
	}

	public void testApplyTacticToHypothesis() throws CoreException {
		IPOFile poFile = createPOFile("x");
		IPSFile psFile = poFile.getPSFile();

		AutoProver.enable();
		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile, monitor);

		IProofState currentPO = userSupport.getCurrentPO();

		userSupport.searchHyps("=");

		Collection<Predicate> searched = currentPO.getSearched();
		assertTrue("Search size is 3 ", searched.size() == 3);

		Iterator<Predicate> iterator = searched.iterator();
		Predicate hyp1 = iterator.next();

		Set<Predicate> hyps1 = new HashSet<Predicate>();
		hyps1.add(hyp1);

		userSupport.applyTacticToHypotheses(Tactics.falsifyHyp(hyp1), hyps1, true,
				monitor);

		Collection<Predicate> cached = currentPO.getCached();
		assertTrue("Hypothesis is added to the cache ", cached.contains(hyp1));

		userSupport.dispose();
	}

	public void testBack() throws CoreException {
		IPOFile poFile = createPOFile("x");
		IPSFile psFile = poFile.getPSFile();

		AutoProver.enable();
		runBuilder();

		IUserSupport userSupport = new UserSupport();

		NullProgressMonitor monitor = new NullProgressMonitor();
		userSupport.setInput(psFile, monitor);

		IProofState currentPO = userSupport.getCurrentPO();

		IProofTreeNode node1 = currentPO.getCurrentNode();

		userSupport.applyTactic(Tactics.lemma("3 = 3"), true, monitor);
		userSupport.back(monitor);
		assertEquals("Back to node 1 ", node1, currentPO.getCurrentNode());
		assertTrue("Node 1 is open again ", node1.isOpen());

		userSupport.dispose();
	}

}
