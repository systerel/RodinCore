package org.eventb.core.pm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOHypothesis;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRSequent;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.tactics.ITactic;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.internal.core.pm.HypothesisDelta;
import org.eventb.internal.core.pm.ProofStateDelta;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class UserSupport implements IElementChangedListener {

	private IPRFile prFile; // Unique for an instance of UserSupport

	private LinkedList<ProofState> proofStates;

	protected ProofState currentPS;

	private boolean fireDelta;

	private boolean saveHypState;

	private Object information;

	private Collection<Hypothesis> oldSelected;

	private Collection<Hypothesis> oldCached;

	private Collection<Hypothesis> oldSearched;

	private IProofStateDelta delta = null; // The current delta

	public UserSupport() {
		proofStateChangedListeners = new HashSet<IProofStateChangedListener>();
		RodinCore.addElementChangedListener(this);
		fireDelta = true;
		saveHypState = true;
	}

	// private void saveHypothesisState() {
	// if (!saveHypState)
	// return; // Already save
	//
	// IProofTreeNode currentNode = null;
	// if (currentPS != null) {
	// currentNode = currentPS.getCurrentNode();
	// }
	// oldSelected = new HashSet<Hypothesis>();
	// if (currentNode != null)
	// for (Iterator<Hypothesis> i = currentNode.getSequent()
	// .selectedHypotheses().iterator(); i.hasNext();) {
	// oldSelected.add(i.next());
	// }
	//
	// // Need to copy the old hypotheses
	// oldCached = new HashSet<Hypothesis>();
	// if (currentPS != null)
	// for (Iterator<Hypothesis> i = currentPS.getCached().iterator(); i
	// .hasNext();) {
	// oldCached.add(i.next());
	// }
	//
	// oldSearched = new HashSet<Hypothesis>();
	// if (currentPS != null)
	// for (Iterator<Hypothesis> i = currentPS.getSearched().iterator(); i
	// .hasNext();) {
	// oldSearched.add(i.next());
	// }
	//
	// saveHypState = false;
	// }

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
			notifyStateChangedListeners(delta);
			// IProofStateDelta delta;
			// Collection<IHypothesisDelta> hypDelta = calculateHypDelta();
			// IProofTreeNode currentNode = null;
			// if (currentPS != null)
			// currentNode = currentPS.getCurrentNode();
			// IGoalDelta goalDelta = new GoalDelta(currentNode);
			// delta = new ProofStateDelta(goalDelta, hypDelta, information,
			// currentPS);
			// notifyStateChangedListeners(delta);
			// saveHypState = true;
		}
	}

	public void fireProofStateDelta(IProofStateDelta newDelta) {
		delta = mergeDelta(delta, newDelta);
		notifyPendingDelta();
	}

	private IProofStateDelta mergeDelta(IProofStateDelta oldDelta,
			IProofStateDelta newDelta) {
		return newDelta;
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

	private Collection<IHypothesisDelta> calculateHypDelta() {
		IProofTreeNode newNode = null;
		if (currentPS != null)
			newNode = currentPS.getCurrentNode();

		Collection<IHypothesisDelta> delta = new HashSet<IHypothesisDelta>();

		Collection<Hypothesis> newSelected;
		if (newNode == null)
			newSelected = new HashSet<Hypothesis>();
		else
			newSelected = newNode.getSequent().selectedHypotheses();
		for (Iterator<Hypothesis> it = oldSelected.iterator(); it.hasNext();) {
			Hypothesis hp = it.next();
			if (!newSelected.contains(hp)) {
				HypothesisDelta d = new HypothesisDelta(hp);
				d.setRemovedFromSelected();
				// UserSupportUtils.debug("Remove from Selected: " +
				// hp.getPredicate());
				delta.add(d);
			}
		}
		for (Iterator<Hypothesis> it = newSelected.iterator(); it.hasNext();) {
			Hypothesis hp = it.next();
			if (!oldSelected.contains(hp)) {
				HypothesisDelta d = new HypothesisDelta(hp);
				d.setAddedToSelected();
				// UserSupportUtils.debug("Add to Selected: " +
				// hp.getPredicate());
				delta.add(d);
			}
		}

		Collection<Hypothesis> newCached;
		if (currentPS == null)
			newCached = new HashSet<Hypothesis>();
		else
			newCached = currentPS.getCached();
		for (Iterator<Hypothesis> it = oldCached.iterator(); it.hasNext();) {
			Hypothesis hp = it.next();
			// UserSupportUtils.debug("Testing in old Cached: " +
			// hp.getPredicate());
			if (!isValid(hp, newNode) || !newCached.contains(hp)) {
				HypothesisDelta d = new HypothesisDelta(hp);
				d.setRemovedFromCached();
				// UserSupportUtils.debug("Removed from Cached: " +
				// hp.getPredicate());
				delta.add(d);
			}
		}
		for (Iterator<Hypothesis> it = newCached.iterator(); it.hasNext();) {
			Hypothesis hp = it.next();
			// UserSupportUtils.debug("Testing in new Cached: " +
			// hp.getPredicate());
			if (isValid(hp, newNode) && !oldCached.contains(hp)) {
				HypothesisDelta d = new HypothesisDelta(hp);
				d.setAddedToCached();
				// UserSupportUtils.debug("Add to Cached: " +
				// hp.getPredicate());
				delta.add(d);
			}
		}

		Collection<Hypothesis> newSearched;
		if (currentPS == null)
			newSearched = new HashSet<Hypothesis>();
		else
			newSearched = currentPS.getSearched();
		for (Iterator<Hypothesis> it = oldSearched.iterator(); it.hasNext();) {
			Hypothesis hp = it.next();
			// UserSupportUtils.debug("Testing in old Searched: " +
			// hp.getPredicate());
			if (!isValid(hp, newNode) || !newSearched.contains(hp)) {
				HypothesisDelta d = new HypothesisDelta(hp);
				d.setRemovedFromSearched();
				// UserSupportUtils.debug("Remove from Searched: " +
				// hp.getPredicate());
				delta.add(d);
			}
		}
		for (Iterator<Hypothesis> it = newSearched.iterator(); it.hasNext();) {
			Hypothesis hp = it.next();
			if (isValid(hp, newNode) && !oldSearched.contains(hp)) {
				HypothesisDelta d = new HypothesisDelta(hp);
				d.setAddedToSearched();
				// UserSupportUtils.debug("Add to Searched: " +
				// hp.getPredicate());
				delta.add(d);
			}
		}

		return delta;
	}

	private boolean isValid(Hypothesis hyp, IProofTreeNode pt) {
		// UserSupportUtils.debug("Is Valid? " + (pt != null &&
		// pt.getSequent().hypotheses().contains(hyp)));
		return (pt != null && pt.getSequent().hypotheses().contains(hyp));
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
		}
		else {
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
		currentPS = null;
		ProofStateDelta newDelta = new ProofStateDelta();
		newDelta.setNewProofState(null);
		fireProofStateDelta(newDelta);
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
		currentPS = null;
		ProofStateDelta newDelta = new ProofStateDelta();
		newDelta.setNewProofState(null);
		fireProofStateDelta(newDelta);
	}

	private void setProofState(ProofState ps) throws RodinDBException {
		if (currentPS != ps) {
			UserSupportUtils.debug("New Proof Sequent: " + ps);
			currentPS = ps;
			ProofStateDelta newDelta = new ProofStateDelta();
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
		// saveHypothesisState();
		// current node change
		if (currentPS.getCurrentNode() != pt) {
			currentPS.setCurrentNode(pt);
			ProofStateDelta newDelta = new ProofStateDelta();
			newDelta.setNewProofState(null);
			newDelta.setNewCurrentNode(pt);
			fireProofStateDelta(newDelta);
		}
		return;
	}

	public void applyTacticToHypotheses(ITactic t, Set<Hypothesis> hyps)
			throws RodinDBException {
		// saveHypothesisState();
		// Changed cached hypotheses.
		currentPS.addAllToCached(hyps);
		applyTactic(t);
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
		}
	}

	protected void internalPrune() {
		IProofTreeNode currentNode = currentPS.getCurrentNode();
		information = Tactics.prune().apply(currentNode);
		if (information == null) {
			information = "Tactic applied successfully";
			currentPS.setDirty(true);
		}
	}

	public void prune() throws RodinDBException {
		// saveHypothesisState();
		// No delta??
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
		// TODO Removed cached hypotheses.
		// saveHypothesisState();
		currentPS.removeAllFromCached(hyps);
		information = "Hypotheses removed from cached";
		ProofStateDelta newDelta = new ProofStateDelta();
		newDelta.setRemoveFromCached(hyps);
		fireProofStateDelta(newDelta);
		return;
	}

	public void removeSearchedHypotheses(Collection<Hypothesis> hyps) {
		// TODO Removed searched hypotheses.
		// saveHypothesisState();
		currentPS.removeAllFromSearched(hyps);
		information = "Hypotheses removed from searched";
		ProofStateDelta newDelta = new ProofStateDelta();
		newDelta.setRemoveFromSearched(hyps);
		fireProofStateDelta(newDelta);
		return;
	}

	public void searchHyps(String token) {
		// TODO New searched hypotheses.
		// saveHypothesisState();
		// Trim off white space from token.
		token = token.trim();

		Set<Hypothesis> hyps = Hypothesis.textSearch(currentPS.getCurrentNode()
				.getSequent().hypotheses(), token);

		currentPS.setSearched(hyps);

		ProofStateDelta newDelta = new ProofStateDelta();
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
		// UserSupportUtils.debug("Element changed");
		try {
			processDelta(event.getDelta());
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

	private void processDelta(IRodinElementDelta delta) throws RodinDBException {
		int kind = delta.getKind();
		IRodinElement element = delta.getElement();
		// UserSupportUtils.debug("Process Delta " + element);
		if (element instanceof IRodinProject) {
			// UserSupportUtils.debug("Project changed " + kind + " for " +
			// ((IRodinProject) element).getElementName());
			for (IRodinElementDelta d : delta.getAffectedChildren()) {
				processDelta(d);
			}
		} else if (element instanceof IPRFile) {
			// UserSupportUtils.debug("PRFile changed " + kind + " for " +
			// ((IPRFile) element).getElementName());
			if (prFile.equals(element)) {
				// setInput((IPRFile) element);
				for (IRodinElementDelta d : delta.getAffectedChildren()) {
					processDelta(d);
				}
			}
		}
		// else if (element instanceof IProof) {
		// IPRSequent prSequent = (IPRSequent) element.getParent();
		// UserSupportUtils.debug("Change status " + prSequent.toString());
		// //
		// // for (ProofState ps : proofStates) {
		// // if (ps.getPRSequent().equals(element)) {
		// // if (ps.equals())
		// // }
		// // }
		//
		// }
		else if (element instanceof IPRSequent) {

			if (kind == IRodinElementDelta.ADDED) { // No rename
				UserSupportUtils.debug("Added "
						+ ((IPRSequent) element).getElementName());
				ProofState ps = new ProofState((IPRSequent) element);
				proofStates.add(ps);
			} else if (kind == IRodinElementDelta.CHANGED) {
				UserSupportUtils.debug("Changed "
						+ ((IPRSequent) element).getElementName());
				boolean refresh = false;
				for (IRodinElementDelta d : delta.getAffectedChildren()) {
					processDelta(d);
					IRodinElement child = d.getElement();
					if (child instanceof IPOHypothesis
							|| child instanceof IPOPredicate) {
						refresh = true;
					}
				}
				if (refresh) {
					for (ProofState ps : proofStates) {
						if (ps.getPRSequent().equals(element)) {
							UserSupportUtils.debug("Updated "
									+ ((IPRSequent) element).getElementName());
							if (ps.getProofTree() != null)
								ps.loadProofTree();
							if (ps == currentPS)
								setCurrentPO(ps.getPRSequent());
						}
					}
				}
			} else {
				ProofState toBeRemoved = null;
				for (ProofState ps : proofStates) {
					if (ps.getPRSequent().equals(element)) {
						if (kind == IRodinElementDelta.REMOVED) {
							UserSupportUtils.debug("Removed "
									+ ((IPRSequent) element).getElementName());
							toBeRemoved = ps;
						}
					}
				}
				proofStates.remove(toBeRemoved);
			}
			// UserSupportUtils.debug("IPRSequent changed " + kind + " for " +
			// ((IPRSequent) element).getElementName());
			// Collection<ProofState> remove = new HashSet<ProofState>();
			// for (ProofState ps : proofStates) {
			// if (ps.getPRSequent().equals(element)) {
			// if (kind == IRodinElementDelta.ADDED) {
			// UserSupportUtils.debug("Updated " + ((IPRSequent)
			// element).getElementName());
			// ps.initProofTree();
			// if (ps == currentPS) setCurrentPO(ps.getPRSequent());
			// }
			// else if (kind == IRodinElementDelta.REMOVED) {
			// UserSupportUtils.debug("Removed " + ((IPRSequent)
			// element).getElementName());
			// remove.add(ps);
			// }
			// else { // CHANGED
			// UserSupportUtils.debug("Changed " + ((IPRSequent)
			// element).getElementName());
			// }
			// }
			// }
			// proofStates.removeAll(remove);
		} else if (element instanceof IMachineFile) {
			return;
		} else if (element instanceof IContextFile) {
			return;
		} else if (element instanceof IParent) {
			for (IRodinElementDelta d : delta.getAffectedChildren()) {
				processDelta(d);
			}
		}
	}

	// public void addHypothesisChangedListener(IHypothesisChangedListener
	// listener) {
	// hypChangedListeners.add(listener);
	// }
	//	
	// public void removeHypothesisChangedListener(IHypothesisChangedListener
	// listener) {
	// hypChangedListeners.remove(listener);
	// }
	//	
	// private void notifyHypothesisChangedListener(IHypothesisChangeEvent e) {
	// for (Iterator<IHypothesisChangedListener> i =
	// hypChangedListeners.iterator(); i.hasNext();) {
	// IHypothesisChangedListener listener = i.next();
	// listener.hypothesisChanged(e);
	// }
	// return;
	// }
	//	
	// public void addGoalChangedListener(IGoalChangedListener listener) {
	// goalChangedListeners.add(listener);
	// }
	//	
	// public void removeGoalChangedListener(IGoalChangedListener listener) {
	// goalChangedListeners.remove(listener);
	// }
	//	
	// private void notifyGoalChangedListener(IGoalChangeEvent e) {
	// for (Iterator<IGoalChangedListener> i = goalChangedListeners.iterator();
	// i.hasNext();) {
	// IGoalChangedListener listener = i.next();
	// listener.goalChanged(e);
	// }
	// return;
	// }
	//
	// public void addPOChangedListener(IPOChangedListener listener) {
	// poChangedListeners.add(listener);
	// }
	//	
	// public void removePOChangedListener(IPOChangedListener listener) {
	// poChangedListeners.remove(listener);
	// }
	//	
	// private void notifyPOChangedListener(IPOChangeEvent e) {
	// for (Iterator<IPOChangedListener> i = poChangedListeners.iterator();
	// i.hasNext();) {
	// IPOChangedListener listener = i.next();
	// listener.poChanged(e);
	// }
	// return;
	// }
	//
	// public void addProofStatusChangedListener(IProofStatusChangedListener
	// listener) {
	// proofStatusChangedListeners.add(listener);
	// }
	//	
	// public void removeProofStatusChangedListener(IProofStatusChangedListener
	// listener) {
	// proofStatusChangedListeners.remove(listener);
	// }
	//
	// private void notifyProofStatusChangedListener(boolean complete) {
	// for (Iterator<IProofStatusChangedListener> i =
	// proofStatusChangedListeners.iterator(); i.hasNext();) {
	// IProofStatusChangedListener listener = i.next();
	// listener.proofStatusChanged(complete);
	// }
	// return;
	// }
	//	
	// public void addStatusChangedListener(IStatusChangedListener listener) {
	// statusChangedListeners.add(listener);
	// }
	//	
	// public void removeStatusChangedListener(IStatusChangedListener listener)
	// {
	// statusChangedListeners.remove(listener);
	// }
	//
	// private void notifyStatusChangedListener(Object information) {
	// for (Iterator<IStatusChangedListener> i =
	// statusChangedListeners.iterator(); i.hasNext();) {
	// IStatusChangedListener listener = i.next();
	// listener.statusChanged(information);
	// }
	// return;
	// }

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

}