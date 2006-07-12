package org.eventb.core.pm;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.eventb.core.IPRFile;
import org.eventb.core.IPRSequent;
import org.eventb.core.prover.IProofTreeChangedListener;
import org.eventb.core.prover.IProofTreeDelta;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.tactics.ITactic;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.internal.core.pm.ProofStateDelta;
import org.eventb.internal.core.pm.UserSupportUtils;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class UserSupport implements IElementChangedListener,
		IProofTreeChangedListener {

	private IPRFile prFile; // Unique for an instance of UserSupport

	private LinkedList<ProofState> proofStates;

	protected ProofState currentPS;

	private boolean fireDelta;

	private Object information;

	private IProofStateDelta delta = null; // The current delta

	private boolean outOfDate;
	private int c = 0;
	
	/* Creation should be done using UserSupportManager */
	public UserSupport() {
		proofStateChangedListeners = new HashSet<IProofStateChangedListener>();
		RodinCore.addElementChangedListener(this);
		fireDelta = true;
		proofStates = new LinkedList<ProofState>();
		delta = new ProofStateDelta(this); // Clear delta
		outOfDate = false;
	}

	Collection<IProofStateChangedListener> proofStateChangedListeners;

	public void addStateChangedListeners(IProofStateChangedListener listener) {
		proofStateChangedListeners.add(listener);
	}

	public void removeStateChangedListeners(IProofStateChangedListener listener) {
		proofStateChangedListeners.remove(listener);
	}

	public void notifyStateChangedListeners(IProofStateDelta mergeDelta) {
		for (IProofStateChangedListener listener : proofStateChangedListeners) {
			listener.proofStateChanged(mergeDelta);
		}
	}

	private void notifyPendingDelta() {
		if (fireDelta) {
//			UserSupportUtils.debug("Notified: " + delta);
			notifyStateChangedListeners(delta);
			delta = new ProofStateDelta(this); // Clear delta
		}
	}

	public void fireProofStateDelta(IProofStateDelta newDelta) {
//		UserSupportUtils.debug("Fire Delta: " + newDelta);
		delta = mergeDelta(delta, newDelta);
		notifyPendingDelta();
	}

	private IProofStateDelta mergeDelta(IProofStateDelta oldDelta,
			IProofStateDelta newDelta) {
		ProofStateDelta mergedDelta = new ProofStateDelta(this);

		ProofState newProofState = newDelta.getNewProofState();
		if (newProofState != null) {
			mergedDelta.setNewProofState(newProofState);
			return mergedDelta;
		} else {
			ProofState oldProofState = oldDelta.getNewProofState();
			if (oldProofState != null) {
				mergedDelta.setNewProofState(oldProofState);
				return mergedDelta;
			} else {
				// Proof Tree Delta
				IProofTreeDelta newProofTreeDelta = newDelta
						.getProofTreeDelta();
				if (newProofTreeDelta != null) {
					mergedDelta.setProofTreeDelta(newProofTreeDelta);
				} else {
					IProofTreeDelta oldProofTreeDelta = oldDelta
							.getProofTreeDelta();
					if (oldProofTreeDelta != null) {
						mergedDelta.setProofTreeDelta(oldProofTreeDelta);
					}
				}

				// Current Node
				IProofTreeNode newCurrentNode = newDelta.getNewProofTreeNode();
				if (newCurrentNode != null) {
					mergedDelta.setNewCurrentNode(newCurrentNode);
				} else {
					IProofTreeNode oldCurrentNode = oldDelta
							.getNewProofTreeNode();
					if (oldCurrentNode != null) {
						mergedDelta.setNewCurrentNode(oldCurrentNode);
					} else {
						if (newDelta.getNewCache() || oldDelta.getNewCache()) {
							mergedDelta.setNewCache();
						}

						if (newDelta.getNewSearch() || oldDelta.getNewSearch()) {
							mergedDelta.setNewSearch();
						}

						return mergedDelta;
					}
				}

				return mergedDelta;
			}
		}
	}

	public void batchOperation(Runnable op) {
		try {
			fireDelta = false;
			op.run();
		} finally {
			fireDelta = true;
		}
		notifyPendingDelta();
	}

	/**
	 * This method return the current Obligation (Proof State). This should be
	 * called at the initialisation of a listener of the UserSupport. After that
	 * the listeners will update their states by listen to the changes from the
	 * UserSupport
	 * 
	 * @return the current ProofState (can be null).
	 */
	public ProofState getCurrentPO() {
		return currentPS;
	}

	// Should be called by the UserSupportManager?
	public void setInput(IPRFile prFile) throws RodinDBException {
		this.prFile = prFile;
		proofStates = new LinkedList<ProofState>();
		try {
			for (int i = 0; i < prFile.getSequents().length; i++) {
				IPRSequent prSequent = (IPRSequent) prFile.getSequents()[i];
				proofStates.add(new ProofState(prSequent));
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		outOfDate = false;
		nextUndischargedPO();
	}

	public void setCurrentPO(IPRSequent prSequent) throws RodinDBException {
		// saveHypothesisState();
		for (ProofState ps : proofStates) {
			if (ps.getPRSequent().equals(prSequent)) {
				setProofState(ps);
				return;
			}
		}
	}

	public void nextUndischargedPO() throws RodinDBException {
		// saveHypothesisState();
		int index;
		if (currentPS == null) {
			index = -1;
		} else {
			index = proofStates.indexOf(currentPS);
		}
		for (int i = 1; i <= proofStates.size(); i++) {
			ProofState ps = proofStates.get((index + i) % proofStates.size());
			if (!ps.isDischarged()) {
				setProofState(ps);
				return;
			}
		}
		information = "No Un-discharged Proof Obligation Found";
		// currentPS = null;
		// ProofStateDelta newDelta = new ProofStateDelta();
		// newDelta.setNewProofState(null);
		// fireProofStateDelta(newDelta);
	}

	public void prevUndischargedPO() throws RodinDBException {
		// saveHypothesisState();
		int index = proofStates.indexOf(currentPS);
		for (int i = 1; i < proofStates.size(); i++) {
			ProofState ps = proofStates.get((proofStates.size() + index - i)
					% proofStates.size());
			if (!ps.isDischarged()) {
				setProofState(ps);
				return;
			}
		}
		// currentPS = null;
		information = "No Un-discharged Proof Obligation Found";
		// currentPS = null;
		// ProofStateDelta newDelta = new ProofStateDelta();
		// newDelta.setNewProofState(null);
		// fireProofStateDelta(newDelta);
	}

	private void setProofState(ProofState ps) throws RodinDBException {
		if (currentPS != ps) {
			if (currentPS != null)
				currentPS.getProofTree().removeChangeListener(this);
			UserSupportUtils.debug("New Proof Sequent: " + ps);
			currentPS = ps;
			ps.getProofTree().addChangeListener(this);
			ProofStateDelta newDelta = new ProofStateDelta(this);
			newDelta.setNewProofState(ps);
			fireProofStateDelta(newDelta);
		}
		return;
	}

	/**
	 * This is the response of the UserSupport for selecting a node in the
	 * current Proof Tree.
	 */
	public void selectNode(IProofTreeNode pt) {
		if (currentPS.getCurrentNode() != pt) {
			currentPS.setCurrentNode(pt);
			ProofStateDelta newDelta = new ProofStateDelta(this);
			newDelta.setNewProofState(null);
			newDelta.setNewCurrentNode(pt);
			fireProofStateDelta(newDelta);
		}
		return;
	}

	public void applyTacticToHypotheses(final ITactic t,
			final Set<Hypothesis> hyps) throws RodinDBException {

		batchOperation(new Runnable() {

			public void run() {
				addAllToCached(hyps);
				try {
					applyTactic(t);
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
			}

		});

	}

	protected void addAllToCached(Set<Hypothesis> hyps) {
		currentPS.addAllToCached(hyps);
		ProofStateDelta newDelta = new ProofStateDelta(this);
		newDelta.setNewCache();
		fireProofStateDelta(newDelta);
	}

	public void applyTactic(final ITactic t) throws RodinDBException {
		// saveHypothesisState();

		batchOperation(new Runnable() {
			public void run() {
				internalApplyTactic(t);
				IProofTreeNode currentNode = currentPS.getCurrentNode();
				IProofTreeNode newNode = currentPS
						.getNextPendingSubgoal(currentNode);
				if (newNode != null) {
					selectNode(newNode);
				}
			}

		});

		// Node change, selected hypotheses changed
	}

	protected void internalApplyTactic(ITactic t) {
		IProofTreeNode currentNode = currentPS.getCurrentNode();
		information = t.apply(currentNode);
		if (!t.equals(Tactics.prune()))
			Tactics.postProcess().apply(currentNode);
		if (information == null) {
			information = "Tactic applied successfully";
			currentPS.setDirty(true);
			ProofStateDelta newDelta = new ProofStateDelta(this);
			newDelta.setNewCurrentNode(currentNode);
			fireProofStateDelta(newDelta);
		}
	}

	protected void internalPrune() {
		IProofTreeNode currentNode = currentPS.getCurrentNode();
		UserSupportUtils.debug("Internal Prune");
		information = Tactics.prune().apply(currentNode);
		UserSupportUtils.debug("Information: " + information);
		if (information == null) {
			information = "Tactic applied successfully";
			currentPS.setDirty(true);
			ProofStateDelta newDelta = new ProofStateDelta(this);
			newDelta.setNewCurrentNode(currentNode);
			fireProofStateDelta(newDelta);
		}
	}

	public void prune() throws RodinDBException {
		batchOperation(new Runnable() {
			public void run() {
				internalPrune();
				IProofTreeNode currentNode = currentPS.getCurrentNode();
				IProofTreeNode newNode = currentPS
						.getNextPendingSubgoal(currentNode);
				if (newNode != null) {
					selectNode(newNode);
				}

			}
		});
	}

	public void removeCachedHypotheses(Collection<Hypothesis> hyps) {
		currentPS.removeAllFromCached(hyps);
		ProofStateDelta newDelta = new ProofStateDelta(this);
		newDelta.setNewCache();
		fireProofStateDelta(newDelta);
		return;
	}

	public void removeSearchedHypotheses(Collection<Hypothesis> hyps) {
		currentPS.removeAllFromSearched(hyps);
		information = "Hypotheses removed from searched";

		ProofStateDelta newDelta = new ProofStateDelta(this);
		newDelta.setNewSearch();
		fireProofStateDelta(newDelta);
		return;
	}

	public void searchHyps(String token) {
		// Trim off white space from token.
		token = token.trim();

		Set<Hypothesis> hyps = Hypothesis.textSearch(currentPS.getCurrentNode()
				.getSequent().hypotheses(), token);

		currentPS.setSearched(hyps);

		ProofStateDelta newDelta = new ProofStateDelta(this);
		newDelta.setNewSearch();
		fireProofStateDelta(newDelta);
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		c++;
		UserSupportUtils.debug("Element changed " + c + " : ");
		UserSupportUtils.debug("***********************************************");
		UserSupportUtils.debug("Delta " + event.getDelta());
		try {
			processDelta(event.getDelta());
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		if (outOfDate) {
			ProofStateDelta newDelta = new ProofStateDelta(this);
			fireProofStateDelta(newDelta);
		}
	}

	private void processDelta(IRodinElementDelta elementChangedDelta) throws RodinDBException {
		IRodinElement element = elementChangedDelta.getElement();
		// UserSupportUtils.debug("Process Delta " + element);
		if (element instanceof IRodinProject) {
			// UserSupportUtils.debug("Project changed " + kind + " for " +
			// ((IRodinProject) element).getElementName());
			for (IRodinElementDelta d : elementChangedDelta.getAffectedChildren()) {
				processDelta(d);
			}
		} else if (element instanceof IPRFile) {
			// UserSupportUtils.debug("PRFile changed " + kind + " for " +
			// ((IPRFile) element).getElementName());
			if (prFile.equals(element)) {
				
				// Notify
				
				// setInput((IPRFile) element);
				for (IRodinElementDelta d : elementChangedDelta.getAffectedChildren()) {
					processDelta(d);
				}
			}
		} else if (element instanceof IPRSequent) {
			int kind = elementChangedDelta.getKind();
			UserSupportUtils.debug("IPRSequent changed");
		
			if (kind == IRodinElementDelta.ADDED) {
				outOfDate = true;
			} else if (kind == IRodinElementDelta.REMOVED) {
				outOfDate = true;			
			} else if (kind == IRodinElementDelta.CHANGED) {
				IPRSequent prSequent = (IPRSequent) element;
				if (prSequent.isProofBroken())
					outOfDate = true;
			}
		} else if (element instanceof IParent) {
			for (IRodinElementDelta d : elementChangedDelta.getAffectedChildren()) {
				processDelta(d);
			}
		}
	}

	public void back() throws RodinDBException {
		// TODO Batch operation.
		if (currentPS.getCurrentNode().getParent() != null) {
			selectNode(currentPS.getCurrentNode().getParent());
			prune();
		}
	}

	public boolean hasUnsavedChanges() {
		for (ProofState ps : proofStates) {
			if (ps.isDirty())
				return true;
		}
		return false;
	}

	public ProofState[] getUnsavedPOs() {
		Collection<ProofState> unsaved = new HashSet<ProofState>();
		for (ProofState ps : proofStates) {
			if (ps.isDirty())
				unsaved.add(ps);
		}
		return unsaved.toArray(new ProofState[unsaved.size()]);
	}

	public void proofTreeChanged(IProofTreeDelta proofTreeDelta) {
		UserSupportUtils.debug("UserSupport - Proof Tree Changed: "
				+ proofTreeDelta);
		ProofStateDelta newDelta = new ProofStateDelta(this);
		newDelta.setProofTreeDelta(proofTreeDelta);
		fireProofStateDelta(newDelta);
	}

	public void setComment(String text, IProofTreeNode currentNode) {
		currentNode.setComment(text);
		currentPS.setDirty(true);
		ProofStateDelta newDelta = new ProofStateDelta(this);
		newDelta.setNewCurrentNode(currentPS.getCurrentNode());
		fireProofStateDelta(newDelta);
	}

	public Collection<ProofState> getPOs() {
		return proofStates;
	}

	public IPRFile getInput() {
		return prFile;
	}

	public boolean isOutOfDate() {
		return outOfDate;
	}

	public void dispose() {
		RodinCore.removeElementChangedListener(this);
		if (currentPS != null)
			currentPS.getProofTree().removeChangeListener(this);
	}
}