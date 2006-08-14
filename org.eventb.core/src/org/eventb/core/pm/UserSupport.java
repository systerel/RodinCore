package org.eventb.core.pm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.IPOSequent;
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
import org.rodinp.internal.core.RodinElementDelta;

public class UserSupport implements IElementChangedListener,
		IProofTreeChangedListener {

	private IPRFile prFile; // Unique for an instance of UserSupport

	private LinkedList<ProofState> proofStates;

	protected ProofState currentPS;

	private boolean fireDelta;

	private static boolean expertMode;

	/*
	 * The delta for the current thread.
	 */
	ThreadLocal<IProofStateDelta> delta;

	// private boolean outOfDate;

	private int c = 0;

	private Collection<IProofStateChangedListener> proofStateChangedListeners;

	/* Creation should be done using UserSupportManager */
	public UserSupport() {
		proofStateChangedListeners = new ArrayList<IProofStateChangedListener>();
		RodinCore.addElementChangedListener(this);
		fireDelta = true;
		proofStates = new LinkedList<ProofState>();
		delta = new ThreadLocal<IProofStateDelta>(); // Clear delta
		// outOfDate = false;
		// TODO Set expertMode from the Preferences?
		expertMode = false;
	}

	IProofStateDelta getDelta() {
		IProofStateDelta localDelta = delta.get();
		if (localDelta == null) {
			localDelta = new ProofStateDelta(this);
			delta.set(localDelta);
		}
		return localDelta;
	}

	public void addStateChangedListeners(IProofStateChangedListener listener) {
		synchronized (proofStateChangedListeners) {
			if (!proofStateChangedListeners.contains(listener)) {
				proofStateChangedListeners.add(listener);
			}
		}
	}

	public void removeStateChangedListeners(IProofStateChangedListener listener) {
		synchronized (proofStateChangedListeners) {
			if (proofStateChangedListeners.contains(listener)) {
				proofStateChangedListeners.remove(listener);
			}
		}
	}

	public void notifyStateChangedListeners() {
		IProofStateChangedListener[] safeCopy;
		synchronized (proofStateChangedListeners) {
			safeCopy = proofStateChangedListeners
					.toArray(new IProofStateChangedListener[proofStateChangedListeners
							.size()]);
		}
		for (final IProofStateChangedListener listener : safeCopy) {
			Platform.run(new ISafeRunnable() {
				public void handleException(Throwable exception) {
					// do nothing, will be logged by the platform
				}

				public void run() throws Exception {
					listener.proofStateChanged(getDelta());
				}
			});
		}
	}

	private void notifyPendingDelta() {
		if (fireDelta) {
			UserSupportUtils.debug("Notified "
					+ this.getInput().getElementName() + getDelta());
			notifyStateChangedListeners();
			delta = new ThreadLocal<IProofStateDelta>(); // Clear delta
		}
	}

	public void fireProofStateDelta(IProofStateDelta newDelta) {
		// UserSupportUtils.debug("Fire Delta: " + newDelta);
		delta.set(mergeDelta(getDelta(), newDelta));
		notifyPendingDelta();
	}

	private IProofStateDelta mergeDelta(IProofStateDelta oldDelta,
			IProofStateDelta newDelta) {
		ProofStateDelta mergedDelta = new ProofStateDelta(this);

		List<Object> oldInformation = oldDelta.getInformation();
		List<Object> newInformation = newDelta.getInformation();

		mergedDelta.addAllInformation(oldInformation);
		mergedDelta.addAllInformation(newInformation);

		ProofState newProofState = newDelta.getProofState();
		if (newDelta.isDeleted()) {
			mergedDelta.setDeletedProofState(newProofState);
			return mergedDelta;
		} else if (newDelta.isNewProofState()) {
			mergedDelta.setNewProofState(newProofState);
			return mergedDelta;
		} else {
			ProofState oldProofState = oldDelta.getProofState();
			if (oldDelta.isDeleted()) {
				mergedDelta.setDeletedProofState(oldProofState);
				return mergedDelta;
			} else if (oldDelta.isNewProofState()) {
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
		// outOfDate = false;
		nextUndischargedPO(true);
	}

	public void setCurrentPO(IPRSequent prSequent) throws RodinDBException {
		if (prSequent == null)
			setProofState(null);
		for (ProofState ps : proofStates) {
			if (ps.getPRSequent().equals(prSequent)) {
				setProofState(ps);
				return;
			}
		}
	}

	public void nextUndischargedPO(boolean force) throws RodinDBException {
		int index;
		if (currentPS == null) {
			index = -1;
		} else {
			index = proofStates.indexOf(currentPS);
		}
		for (int i = 1; i <= proofStates.size(); i++) {
			ProofState ps = proofStates.get((index + i) % proofStates.size());
			if (!ps.isClosed()) {
				setProofState(ps);
				return;
			}
		}

		Object info = "No Un-discharged Proof Obligation Found";
		ProofStateDelta newDelta = new ProofStateDelta(this);
		newDelta.addInformation(info);
		if (force) {
			setCurrentPO(null);
		}
	}

	public void prevUndischargedPO(boolean force) throws RodinDBException {
		int index;
		if (currentPS == null) {
			index = -1;
		} else {
			index = proofStates.indexOf(currentPS);
		}

		for (int i = 1; i < proofStates.size(); i++) {
			ProofState ps = proofStates.get((proofStates.size() + index - i)
					% proofStates.size());
			if (!ps.isClosed()) {
				setProofState(ps);
				return;
			}
		}
		Object info = "No Un-discharged Proof Obligation Found";
		ProofStateDelta newDelta = new ProofStateDelta(this);
		if (force)
			newDelta.setNewProofState(null);
		newDelta.addInformation(info);
		fireProofStateDelta(newDelta);
	}

	private void setProofState(ProofState ps) throws RodinDBException {
		if (currentPS != ps) {
			if (currentPS != null)
				currentPS.getProofTree().removeChangeListener(this);

			UserSupportUtils.debug("New Proof Sequent: " + ps);
			if (ps == null) {
				currentPS = null;

				ProofStateDelta newDelta = new ProofStateDelta(this);
				newDelta.setNewProofState(null);
				// newDelta.addInformation("Select a new proof obligation");
				fireProofStateDelta(newDelta);

			} else {
				currentPS = ps;
				if (ps.getProofTree() == null) {
					ps.loadProofTree();
				}
				ps.getProofTree().addChangeListener(this);

				ProofStateDelta newDelta = new ProofStateDelta(this);
				newDelta.setNewProofState(ps);
				newDelta.addInformation("Select a new proof obligation");
				fireProofStateDelta(newDelta);

			}
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
			newDelta.setNewCurrentNode(pt);
			fireProofStateDelta(newDelta);
		}
		return;
	}

	public void applyTacticToHypotheses(final ITactic t,
			final Set<Hypothesis> hyps) {
		batchOperation(new Runnable() {
			public void run() {
				addAllToCached(hyps);
				applyTactic(t);
			}
		});

	}

	protected void addAllToCached(Set<Hypothesis> hyps) {
		currentPS.addAllToCached(hyps);
		ProofStateDelta newDelta = new ProofStateDelta(this);
		newDelta.setNewCache();
		fireProofStateDelta(newDelta);
	}

	public void applyTactic(final ITactic t) {
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

	}

	protected void internalApplyTactic(ITactic t) {
		IProofTreeNode currentNode = currentPS.getCurrentNode();
		Object info = t.apply(currentNode);
		if (!t.equals(Tactics.prune())) {
			if (expertMode) {
				Tactics.postProcessExpert().apply(currentNode);
			} else {
				Tactics.postProcessBeginner().apply(currentNode);
			}
		}
		if (info == null) {
			info = "Tactic applied successfully";
			currentPS.setDirty(true);
			ProofStateDelta newDelta = new ProofStateDelta(this);
			newDelta.setNewCurrentNode(currentNode);
			newDelta.addInformation(info);
			fireProofStateDelta(newDelta);
		} else {
			ProofStateDelta newDelta = new ProofStateDelta(this);
			newDelta.addInformation(info);
			fireProofStateDelta(newDelta);
		}
	}

	protected void internalPrune() {
		IProofTreeNode currentNode = currentPS.getCurrentNode();
		UserSupportUtils.debug("Internal Prune");
		Object info = Tactics.prune().apply(currentNode);
		UserSupportUtils.debug("Information: " + info);
		if (info == null) {
			info = "Tactic applied successfully";
			currentPS.setDirty(true);
			ProofStateDelta newDelta = new ProofStateDelta(this);
			newDelta.setNewCurrentNode(currentNode);
			newDelta.addInformation(info);
			fireProofStateDelta(newDelta);
		} else {
			ProofStateDelta newDelta = new ProofStateDelta(this);
			newDelta.addInformation(info);
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
		newDelta.addInformation("Hypotheses removed from cache");
		fireProofStateDelta(newDelta);
		return;
	}

	public void removeSearchedHypotheses(Collection<Hypothesis> hyps) {
		currentPS.removeAllFromSearched(hyps);
		Object info = "Hypotheses removed from searched";

		ProofStateDelta newDelta = new ProofStateDelta(this);
		newDelta.setNewSearch();
		newDelta.addInformation(info);
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
		newDelta.addInformation("Search hypotheses");
		fireProofStateDelta(newDelta);
		return;
	}

	boolean reload;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(final ElementChangedEvent event) {
		c++;
		UserSupportUtils.debug("Element changed " + c + " : "
				+ event.getDelta());
		reload = false;
		deleted = new ArrayList<IPRSequent>();
		batchOperation(new Runnable() {

			public void run() {
				try {
					processDelta(event.getDelta());
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
				if (reload) {
					debugProofState();
					reloadPRSequent();
					UserSupportUtils.debug("****** After ******");
					debugProofState();
				}

				if (currentPS != null) {
					UserSupportUtils.debug("CurrentPS: "
							+ currentPS.getPRSequent().getElementName());
					for (IPRSequent sequent : deleted) {
						UserSupportUtils.debug("Deleted: "
								+ sequent.getElementName());
					}
					if (deleted.contains(currentPS.getPRSequent())) {
						ProofStateDelta newDelta = new ProofStateDelta(
								UserSupport.this);

						newDelta.setDeletedProofState(currentPS);
						newDelta.addInformation("Current PO has been deleted");
						fireProofStateDelta(newDelta);
					}
				}
				// if (outOfDate) {
				// ProofStateDelta newDelta = new ProofStateDelta(this);
				// newDelta.addInformation("Underlying model has changed");
				// fireProofStateDelta(newDelta);
				// }
			}

		});
	}

	void debugProofState() {
		UserSupportUtils.debug("******** Proof States **********");
		for (ProofState state : proofStates) {
			UserSupportUtils.debug("Goal: "
					+ state.getPRSequent().getElementName());
		}
		UserSupportUtils.debug("******************************");
	}

	Collection<IPRSequent> deleted;

	private ProofState getProofState(int index) {
		ProofState proofState = null;
		if (index < proofStates.size())
			proofState = proofStates.get(index);
		return proofState;
	}

	void reloadPRSequent() {
		// Remove the deleted ones first
		for (IPRSequent prSequent : deleted) {
			ProofState state = new ProofState(prSequent);
			proofStates.remove(state);
		}

		try {
			int index = 0;
			ProofState proofState = getProofState(index);
			for (IPOSequent prSequent : prFile.getSequents()) {
				UserSupportUtils.debug("Trying: " + prSequent.getElementName());
				UserSupportUtils.debug("Index: " + index);
				if (proofState != null) {
					if (prSequent.equals(proofState.getPRSequent())) {
						index++;
						proofState = getProofState(index);
						continue;
					}
				}
				ProofState state = new ProofState((IPRSequent) prSequent);
				UserSupportUtils.debug("Added at position " + index);
				proofStates.add(index++, state);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}

	}

	protected void processDelta(IRodinElementDelta elementChangedDelta)
			throws RodinDBException {
		IRodinElement element = elementChangedDelta.getElement();
		// UserSupportUtils.debug("Process Delta " + element);
		if (element instanceof IRodinProject) {
			// UserSupportUtils.debug("Project changed " + kind + " for " +
			// ((IRodinProject) element).getElementName());
			for (IRodinElementDelta d : elementChangedDelta
					.getAffectedChildren()) {
				processDelta(d);
			}
		} else if (element instanceof IPRFile) {
			// UserSupportUtils.debug("PRFile changed " + kind + " for " +
			// ((IPRFile) element).getElementName());
			if (prFile.equals(element)) {
				// Notify

				// setInput((IPRFile) element);
				for (IRodinElementDelta d : elementChangedDelta
						.getAffectedChildren()) {
					processDelta(d);
				}
			}
		} else if (element instanceof IPRSequent) {
			int kind = elementChangedDelta.getKind();

			if (kind == IRodinElementDelta.ADDED) {
				UserSupportUtils.debug("IPRSequent changed: "
						+ element.getElementName() + " is added");

				reload = true;
			} else if (kind == IRodinElementDelta.REMOVED) {
				UserSupportUtils.debug("IPRSequent changed: "
						+ element.getElementName() + " is removed");
				deleted.add((IPRSequent) element);
				reload = true;
			} else if (kind == IRodinElementDelta.CHANGED) {

				int flag = elementChangedDelta.getFlags();
				UserSupportUtils.debug("Flag: " + flag);

				// Trying to reuse only if the children of the PRSequent has
				// changed or if the prsequent has been replaced.
				if ((flag & RodinElementDelta.F_CHILDREN) != 0
						|| (flag & RodinElementDelta.F_REPLACED) != 0) {
					IPRSequent prSequent = (IPRSequent) element;

					ProofState state = getProofState(prSequent);

					UserSupportUtils.debug("Testing: "
							+ state.getPRSequent().getElementName());

					if (state.isUninitialised())
						return;
					
					if (state.proofDischarged()) {
						UserSupportUtils.debug("Proof Discharged in file");
						state.getProofTree().removeChangeListener(this);
						state.loadProofTree();
						state.getProofTree().addChangeListener(this);
						if (state == currentPS) {
							UserSupportUtils.debug("Is the current node");
							ProofStateDelta newDelta = new ProofStateDelta(
									UserSupport.this);
							newDelta.setNewProofState(currentPS);
							newDelta
									.addInformation("Current proof has been reused");
							fireProofStateDelta(newDelta);
						}
					}

					if (state.proofReusable()) {
						UserSupportUtils.debug("Can be reused");
						state.getProofTree().addChangeListener(this);
						if (state == currentPS) {
							UserSupportUtils.debug("Is the current node");
							ProofStateDelta newDelta = new ProofStateDelta(
									UserSupport.this);
							newDelta.setNewProofState(currentPS);
							newDelta
									.addInformation("Current proof has been reused");
							fireProofStateDelta(newDelta);
						}

						// Otherwise reuse "silently"
					} else {
						UserSupportUtils.debug("Cannot be reused");
						if (state == currentPS) {
							UserSupportUtils.debug("Is the current node");
							ProofStateDelta newDelta = new ProofStateDelta(
									UserSupport.this);
							newDelta.setNewProofState(currentPS);

							newDelta
									.addInformation("Current proof cannot be reused");
							fireProofStateDelta(newDelta);
						}
					}

				}

			}
		} else if (element instanceof IParent) {
			for (IRodinElementDelta d : elementChangedDelta
					.getAffectedChildren()) {
				processDelta(d);
			}
		}
	}

	private ProofState getProofState(IPRSequent prSequent) {
		for (ProofState state : proofStates) {
			if (state.getPRSequent().equals(prSequent))
				return state;
		}
		return null;
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
		newDelta.addInformation("Proof Tree Changed");
		fireProofStateDelta(newDelta);
	}

	public void setComment(String text, IProofTreeNode currentNode) {
		currentNode.setComment(text);
		currentPS.setDirty(true);
		ProofStateDelta newDelta = new ProofStateDelta(this);
		newDelta.setNewCurrentNode(currentPS.getCurrentNode());
		newDelta.addInformation("Comment has been set");
		fireProofStateDelta(newDelta);
	}

	public Collection<ProofState> getPOs() {
		return proofStates;
	}

	public IPRFile getInput() {
		return prFile;
	}

	//
	// public boolean isOutOfDate() {
	// return outOfDate;
	// }

	// Should be used by the UserSupportManager only
	public void dispose() {
		RodinCore.removeElementChangedListener(this);
		if (currentPS != null)
			currentPS.getProofTree().removeChangeListener(this);
	}

	public static boolean isExpertMode() {
		return expertMode;
	}

	public static void setExpertMode(boolean mode) {
		expertMode = mode;
	}
}