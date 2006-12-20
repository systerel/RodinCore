/**
 * 
 */
package org.eventb.core.tests.pm;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPSFile;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateChangedListener;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofTreeDelta;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.pm.TypeEnvironmentSorter;
import org.rodinp.core.RodinDBException;

import com.b4free.rodin.core.B4freeCore;

/**
 * Unit tests for class {@link TypeEnvironmentSorter}
 * 
 * @author Laurent Voisin
 */
public class TestUserSupports extends BasicTest {

	IUserSupportManager manager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = EventBPlugin.getPlugin().getUserSupportManager();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testUserSupportFullProve() throws RodinDBException, CoreException {
		IMachineFile machine = createMachine("m0");
		addVariables(machine, "v0");
		addInvariants(machine, makeSList("inv0"), makeSList("v0 ∈ ℕ"));
		addEvent(machine, "INITIALISATION", makeSList(), makeSList(),
				makeSList(), makeSList("act1"), makeSList("v0 ≔ 0"));
		machine.save(null, true);

		runBuilder();
		IPSFile psFile = (IPSFile) rodinProject.getRodinFile(EventBPlugin
				.getPSFileName("m0"));

		IUserSupport userSupport = manager.newUserSupport();
		manager.setInput(userSupport, psFile, new NullProgressMonitor());

		Collection<IProofState> proofStates = userSupport.getPOs();

		// The number of Proof State is the same as the number of Proof Statuses
		assertEquals("Wrong number of POs", proofStates.size(), psFile
				.getStatuses().length);

		// Initially if the proof is close then it is uninitialise
		// Only the current PO is initialised
		IProofState currentPO = userSupport.getCurrentPO();
		for (IProofState state : proofStates) {
			if (state != currentPO)
				assertEquals("Uninitialised ", true, state.isUninitialised());
			else
				assertEquals("Uninitialised ", false, state.isUninitialised());
		}

		// Current PO is not closed and not discharge
		if (currentPO != null) {
			assertEquals("Sequent discharged: ", false, currentPO
					.isSequentDischarged());
			assertEquals("Proof State closed: ", false, currentPO.isClosed());
		}

		// The current node is open (no rule applied)
		IProofTreeNode node = currentPO.getCurrentNode();
		assertEquals("Current node is open ", true, node.isOpen());

		// Test apply ah, there will be 3 children, the first one close and the
		// last 2 is open
		ITactic ah = Tactics.lemma("1 = 1");
		userSupport.applyTactic(ah, new NullProgressMonitor());

		// Node now has a rule applied to it
		assertEquals("Node now is not open ", false, node.isOpen());
		// Node is still not close
		assertEquals("Node is not close", false, node.isClosed());

		// The new node is the first open children
		IProofTreeNode newNode = currentPO.getCurrentNode();
		assertEquals("New current node is open ", true, newNode.isOpen());
		assertEquals("New node is a child of old node ", node, newNode
				.getParent());

		// Try to review the node
		ITactic review = Tactics.review(IConfidence.REVIEWED_MAX);
		userSupport.applyTactic(review, new NullProgressMonitor());

		// Node now is not open
		assertEquals("New node now is not open ", false, newNode.isOpen());

		// Node is now closed (reviewed)
		assertEquals("New node is now close ", true, newNode.isClosed());

		// Check the confidence level
		int confidence = newNode.getConfidence();
		assertEquals("Confident level ", IConfidence.REVIEWED_MAX, confidence);
		// There is no children when review
		assertEquals("New node has children ", 0,
				newNode.getChildNodes().length);

		// The current node should be the second children of the "ah" node
		IProofTreeNode currNode = currentPO.getCurrentNode();
		assertEquals("Current node is a children of the original node ", node,
				currNode.getParent());

		// Apply ml to prove
		final int forces = B4freeCore.ML_FORCE_0 | B4freeCore.ML_FORCE_1
				| B4freeCore.ML_FORCE_2 | B4freeCore.ML_FORCE_3;
		ITactic ml = B4freeCore.externalML(forces);

		userSupport.applyTactic(ml, new NullProgressMonitor());

		// ml should be successful
		assertEquals("New node now is not open ", false, currNode.isOpen());
		assertEquals("New node is now close ", true, currNode.isClosed());
		assertEquals("New node has children ", 0,
				currNode.getChildNodes().length);

		// No more open node, the current node must be the same
		assertEquals("Current node is unchaged ", currNode, currentPO
				.getCurrentNode());

		// Proof is done
		assertEquals("Proof is done ", true, currentPO.isClosed());

		// Dispose the user Support
		manager.disposeUserSupport(userSupport);
	}

	public void testUserSupportApplyTactic() throws RodinDBException,
			CoreException {
		IMachineFile machine = createMachine("m0");
		addVariables(machine, "v0");
		addInvariants(machine, makeSList("inv0"), makeSList("v0 ∈ ℕ"));
		addEvent(machine, "INITIALISATION", makeSList(), makeSList(),
				makeSList(), makeSList("act1"), makeSList("v0 ≔ 0"));
		machine.save(null, true);

		runBuilder();
		IPSFile psFile = (IPSFile) rodinProject.getRodinFile(EventBPlugin
				.getPSFileName("m0"));

		IUserSupport userSupport = manager.newUserSupport();
		manager.setInput(userSupport, psFile, new NullProgressMonitor());

		IProofStateChangedListener listener = new UserSupportListener();
		userSupport.addStateChangedListeners(listener);

		// Test apply ah, there will be 3 children, the first one close and the
		// last 2 is open
		ITactic ah = Tactics.lemma("1 = 1");
		userSupport.applyTactic(ah, new NullProgressMonitor());

		IProofState currentPO = userSupport.getCurrentPO();
		// Check delta

		assertEquals("Source is the current User Support ", userSupport,
				actualUserSupport);
		assertNotNull("ProofTree is changed ", actualProofTreeDelta);
		assertNotNull("Proof Tree Node is changed ", actualProofTreeNode);
		assertEquals("New Proof Tree Node", currentPO.getCurrentNode(),
				actualProofTreeNode);
		assertEquals("No new cache ", false, actualCache);
		assertEquals("No new search ", false, actualSearch);
		assertNull("No new proof state ", actualState);
		assertNotSame("Information is not empty ", 0, actualInformation.size());
	}

	public void testUserSupportSearchHypothesis() throws RodinDBException,
			CoreException {
		IMachineFile machine = createMachine("m0");
		addVariables(machine, "v0");
		addInvariants(machine, makeSList("inv0"), makeSList("v0 ∈ ℕ"));
		addEvent(machine, "INITIALISATION", makeSList(), makeSList(),
				makeSList(), makeSList("act1"), makeSList("v0 ≔ 0"));
		machine.save(null, true);

		runBuilder();
		IPSFile psFile = (IPSFile) rodinProject.getRodinFile(EventBPlugin
				.getPSFileName("m0"));

		IUserSupport userSupport = manager.newUserSupport();
		manager.setInput(userSupport, psFile, new NullProgressMonitor());

		IProofStateChangedListener listener = new UserSupportListener();
		userSupport.addStateChangedListeners(listener);

		userSupport.searchHyps("");

		// Check delta

		assertEquals("Source is the current User Support ", userSupport,
				actualUserSupport);
		assertNull("ProofTree is unchanged ", actualProofTreeDelta);
		assertNull("Proof Tree Node unchanged", actualProofTreeNode);
		assertEquals("No new cache ", false, actualCache);
		assertEquals("New search ", true, actualSearch);
		assertNull("No new proof state ", actualState);
		assertNotSame("Information is not empty ", 0, actualInformation.size());

	}

	public void testUserSupportApplyToHypothesis() throws RodinDBException,
			CoreException {
		IMachineFile machine = createMachine("m0");
		addVariables(machine, "v0");
		addInvariants(machine, makeSList("inv0"), makeSList("v0 ∈ ℕ"));
		addEvent(machine, "INITIALISATION", makeSList(), makeSList(),
				makeSList(), makeSList("act1"), makeSList("v0 ≔ 0"));
		machine.save(null, true);

		runBuilder();
		IPSFile psFile = (IPSFile) rodinProject.getRodinFile(EventBPlugin
				.getPSFileName("m0"));

		IUserSupport userSupport = manager.newUserSupport();
		manager.setInput(userSupport, psFile, new NullProgressMonitor());

		IProofStateChangedListener listener = new UserSupportListener();
		userSupport.addStateChangedListeners(listener);

		userSupport.searchHyps("");
		// Check delta

		Collection<Predicate> hypotheses = userSupport.getCurrentPO()
				.getSearched();

		if (hypotheses.size() == 0)
			return;

		ITactic contradictHyp = Tactics.contradiction();
		Set<Predicate> hyps = new HashSet<Predicate>();
		Predicate hypothesis = (Predicate) hypotheses.toArray()[0];
		hyps.add(hypothesis);

		userSupport.applyTacticToHypotheses(contradictHyp, hyps,
				new NullProgressMonitor());

		IProofState currentPO = userSupport.getCurrentPO();

		assertEquals("Source is the current User Support ", userSupport,
				actualUserSupport);
		assertNotNull("ProofTree is changed ", actualProofTreeDelta);
		assertNotNull("Proof Tree Node changed", actualProofTreeNode);
		assertEquals("New Proof Tree Node", currentPO.getCurrentNode(),
				actualProofTreeNode);
		// The hypothesis is added to cache
		assertEquals("New cache ", true, actualCache);
		assertEquals("Hypothesis is added to cache ", true, currentPO
				.getCached().contains(hypothesis));

		assertEquals("No new search ", false, actualSearch);
		assertNull("No new proof state ", actualState);
		assertNotSame("Information is not empty ", 0, actualInformation.size());
	}

	public void testUserSupportBackTrack() throws RodinDBException,
			CoreException {
		IMachineFile machine = createMachine("m0");
		addVariables(machine, "v0");
		addInvariants(machine, makeSList("inv0"), makeSList("v0 ∈ ℕ"));
		addEvent(machine, "INITIALISATION", makeSList(), makeSList(),
				makeSList(), makeSList("act1"), makeSList("v0 ≔ 0"));
		machine.save(null, true);

		runBuilder();
		IPSFile psFile = (IPSFile) rodinProject.getRodinFile(EventBPlugin
				.getPSFileName("m0"));

		IUserSupport userSupport = manager.newUserSupport();
		manager.setInput(userSupport, psFile, new NullProgressMonitor());

		IProofStateChangedListener listener = new UserSupportListener();
		userSupport.addStateChangedListeners(listener);

		// Test apply ah, there will be 3 children, the first one close and the
		// last 2 is open
		ITactic ah = Tactics.lemma("1 = 1");
		userSupport.applyTactic(ah, new NullProgressMonitor());

		IProofState currentPO = userSupport.getCurrentPO();

		IProofTreeNode currentNode = currentPO.getCurrentNode();
		
		IProofTreeNode parent = currentNode.getParent();

		userSupport.back(new NullProgressMonitor());
		
		// Check delta

		assertEquals("Source is the current User Support ", userSupport,
				actualUserSupport);
		assertNotNull("ProofTree is changed ", actualProofTreeDelta);
		assertNotNull("Proof Tree Node changed", actualProofTreeNode);
		// The node now is the parent of the last node
		assertEquals("New Proof Tree Node", parent,
				actualProofTreeNode);
		// The hypothesis is added to cache
		assertEquals("New cache ", false, actualCache);
		assertEquals("No new search ", false, actualSearch);
		assertNull("No new proof state ", actualState);
		assertNotSame("Information is not empty ", 0, actualInformation.size());
	}

	IProofState actualState;

	IUserSupport actualUserSupport;

	IProofTreeDelta actualProofTreeDelta;

	List<Object> actualInformation;

	IProofTreeNode actualProofTreeNode;

	boolean actualCache;

	boolean actualSearch;

	private class UserSupportListener implements IProofStateChangedListener {

		public void proofStateChanged(IProofStateDelta delta) {
			actualState = delta.getProofState();
			actualUserSupport = delta.getSource();
			actualProofTreeDelta = delta.getProofTreeDelta();
			actualInformation = delta.getInformation();
			actualProofTreeNode = delta.getNewProofTreeNode();
			actualCache = delta.getNewCache();
			actualSearch = delta.getNewSearch();
		}

	}
}
