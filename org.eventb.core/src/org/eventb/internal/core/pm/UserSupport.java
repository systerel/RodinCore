package org.eventb.internal.core.pm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SafeRunner;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateChangedListener;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeChangedListener;
import org.eventb.core.seqprover.IProofTreeDelta;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.ProofMonitor;
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
		IProofTreeChangedListener, IUserSupport {

	private IPSFile psFile; // Unique for an instance of UserSupport

	private LinkedList<IProofState> proofStates;

	protected IProofState currentPS;

	private boolean fireDelta;

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
		proofStates = new LinkedList<IProofState>();
		delta = new ThreadLocal<IProofStateDelta>(); // Clear delta
		// outOfDate = false;
	}

	IProofStateDelta getDelta() {
		IProofStateDelta localDelta = delta.get();
		if (localDelta == null) {
			localDelta = new ProofStateDelta(this);
			delta.set(localDelta);
		}
		return localDelta;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#addStateChangedListeners(org.eventb.core.pm.IProofStateChangedListener)
	 */
	public void addStateChangedListeners(IProofStateChangedListener listener) {
		synchronized (proofStateChangedListeners) {
			if (!proofStateChangedListeners.contains(listener)) {
				proofStateChangedListeners.add(listener);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#removeStateChangedListeners(org.eventb.core.pm.IProofStateChangedListener)
	 */
	public void removeStateChangedListeners(IProofStateChangedListener listener) {
		synchronized (proofStateChangedListeners) {
			if (proofStateChangedListeners.contains(listener)) {
				proofStateChangedListeners.remove(listener);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#notifyStateChangedListeners()
	 */
	private void notifyStateChangedListeners() {
		IProofStateChangedListener[] safeCopy;
		synchronized (proofStateChangedListeners) {
			safeCopy = proofStateChangedListeners
					.toArray(new IProofStateChangedListener[proofStateChangedListeners
							.size()]);
		}
		for (final IProofStateChangedListener listener : safeCopy) {
			SafeRunner.run(new ISafeRunnable() {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#fireProofStateDelta(org.eventb.core.pm.IProofStateDelta)
	 */
	void fireProofStateDelta(IProofStateDelta newDelta) {
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

		IProofState newProofState = newDelta.getProofState();
		if (newDelta.isDeleted()) {
			mergedDelta.setDeletedProofState(newProofState);
			return mergedDelta;
		} else if (newDelta.isNewProofState()) {
			mergedDelta.setNewProofState(newProofState);
			return mergedDelta;
		} else {
			IProofState oldProofState = oldDelta.getProofState();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#batchOperation(java.lang.Runnable)
	 */
	public void batchOperation(Runnable op) {
		try {
			fireDelta = false;
			op.run();
		} finally {
			fireDelta = true;
		}
		notifyPendingDelta();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#getCurrentPO()
	 */
	public IProofState getCurrentPO() {
		return currentPS;
	}

	// Should be called by the UserSupportManager?
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#setInput(org.eventb.core.IPSFile,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setInput(IPSFile psFile, IProgressMonitor monitor)
			throws RodinDBException {
		this.psFile = psFile;
		proofStates = new LinkedList<IProofState>();
		try {
			for (int i = 0; i < psFile.getStatuses().length; i++) {
				IPSStatus prSequent = psFile.getStatuses()[i];
				proofStates.add(new ProofState(prSequent));
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		// outOfDate = false;
		nextUndischargedPO(true, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#setCurrentPO(org.eventb.core.IPSstatus,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setCurrentPO(IPSStatus psStatus, IProgressMonitor monitor)
			throws RodinDBException {
		if (psStatus == null)
			setProofState(null, monitor);
		for (IProofState ps : proofStates) {
			if (ps.getPRSequent().equals(psStatus)) {
				setProofState(ps, monitor);
				return;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#nextUndischargedPO(boolean,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void nextUndischargedPO(boolean force, IProgressMonitor monitor)
			throws RodinDBException {
		int index;
		if (currentPS == null) {
			index = -1;
		} else {
			index = proofStates.indexOf(currentPS);
		}
		for (int i = 1; i <= proofStates.size(); i++) {
			IProofState ps = proofStates.get((index + i) % proofStates.size());
			if (!ps.isClosed()) {
				setProofState(ps, monitor);
				return;
			}
		}

		Object info = "No Un-discharged Proof Obligation Found";
		ProofStateDelta newDelta = new ProofStateDelta(this);
		newDelta.addInformation(info);
		if (force) {
			setCurrentPO(null, monitor);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#prevUndischargedPO(boolean,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void prevUndischargedPO(boolean force, IProgressMonitor monitor)
			throws RodinDBException {
		int index;
		if (currentPS == null) {
			index = -1;
		} else {
			index = proofStates.indexOf(currentPS);
		}

		for (int i = 1; i < proofStates.size(); i++) {
			IProofState ps = proofStates.get((proofStates.size() + index - i)
					% proofStates.size());
			if (!ps.isClosed()) {
				setProofState(ps, monitor);
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

	private void setProofState(IProofState ps, IProgressMonitor monitor)
			throws RodinDBException {
		// if (currentPS != ps) {
		if (currentPS != null && !currentPS.isUninitialised())
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
				ps.loadProofTree(monitor);
			}
			ps.getProofTree().addChangeListener(this);

			ProofStateDelta newDelta = new ProofStateDelta(this);
			newDelta.setNewProofState(ps);
			newDelta.addInformation("Select a new proof obligation");
			fireProofStateDelta(newDelta);

		}
		// }
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#selectNode(org.eventb.core.seqprover.IProofTreeNode)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#applyTacticToHypotheses(org.eventb.core.seqprover.ITactic,
	 *      java.util.Set, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void applyTacticToHypotheses(final ITactic t,
			final Set<Hypothesis> hyps, final IProgressMonitor monitor) {
		batchOperation(new Runnable() {
			public void run() {
				addAllToCached(hyps);
				applyTactic(t, monitor);
			}
		});

	}

	protected void addAllToCached(Set<Hypothesis> hyps) {
		currentPS.addAllToCached(hyps);
		ProofStateDelta newDelta = new ProofStateDelta(this);
		newDelta.setNewCache();
		fireProofStateDelta(newDelta);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#applyTactic(org.eventb.core.seqprover.ITactic,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void applyTactic(final ITactic t, final IProgressMonitor monitor) {
		batchOperation(new Runnable() {
			public void run() {
				internalApplyTactic(t, new ProofMonitor(monitor));
				IProofTreeNode currentNode = currentPS.getCurrentNode();
				IProofTreeNode newNode = currentPS
						.getNextPendingSubgoal(currentNode);
				if (newNode != null) {
					selectNode(newNode);
				}
			}

		});

	}

	protected void internalApplyTactic(ITactic t, IProofMonitor pm) {
		IProofTreeNode currentNode = currentPS.getCurrentNode();
		Object info = t.apply(currentNode, pm);
		if (!t.equals(Tactics.prune())) {
			IUserSupportManager usManager = EventBPlugin.getDefault()
					.getUserSupportManager();
			if (usManager.getProvingMode().isExpertMode()) {
				Tactics.postProcessExpert().apply(currentNode, pm);
			} else {
				Tactics.postProcessBeginner().apply(currentNode, pm);
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

	protected void internalPrune(IProofMonitor pm) {
		IProofTreeNode currentNode = currentPS.getCurrentNode();
		UserSupportUtils.debug("Internal Prune");
		Object info = Tactics.prune().apply(currentNode, pm);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#prune(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void prune(final IProgressMonitor monitor) throws RodinDBException {
		batchOperation(new Runnable() {
			public void run() {
				internalPrune(new ProofMonitor(monitor));
				IProofTreeNode currentNode = currentPS.getCurrentNode();
				IProofTreeNode newNode = currentPS
						.getNextPendingSubgoal(currentNode);
				if (newNode != null) {
					selectNode(newNode);
				}

			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#removeCachedHypotheses(java.util.Collection)
	 */
	public void removeCachedHypotheses(Collection<Hypothesis> hyps) {
		currentPS.removeAllFromCached(hyps);
		ProofStateDelta newDelta = new ProofStateDelta(this);
		newDelta.setNewCache();
		newDelta.addInformation("Hypotheses removed from cache");
		fireProofStateDelta(newDelta);
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#removeSearchedHypotheses(java.util.Collection)
	 */
	public void removeSearchedHypotheses(Collection<Hypothesis> hyps) {
		currentPS.removeAllFromSearched(hyps);
		Object info = "Hypotheses removed from searched";

		ProofStateDelta newDelta = new ProofStateDelta(this);
		newDelta.setNewSearch();
		newDelta.addInformation(info);
		fireProofStateDelta(newDelta);
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#searchHyps(java.lang.String)
	 */
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
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(final ElementChangedEvent event) {
		final IProgressMonitor monitor = new NullProgressMonitor();
		c++;
		UserSupportUtils.debug("Element changed " + c + " : "
				+ event.getDelta());
		reload = false;
		deleted = new ArrayList<IPSStatus>();
		batchOperation(new Runnable() {

			public void run() {
				try {
					processDelta(event.getDelta(), monitor);
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
					for (IPSStatus sequent : deleted) {
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
		for (IProofState state : proofStates) {
			UserSupportUtils.debug("Goal: "
					+ state.getPRSequent().getElementName());
		}
		UserSupportUtils.debug("******************************");
	}

	Collection<IPSStatus> deleted;

	private IProofState getProofState(int index) {
		IProofState proofState = null;
		if (index < proofStates.size())
			proofState = proofStates.get(index);
		return proofState;
	}

	void reloadPRSequent() {
		// Remove the deleted ones first
		for (IPSStatus prSequent : deleted) {
			IProofState state = new ProofState(prSequent);
			proofStates.remove(state);
		}

		try {
			int index = 0;
			IProofState proofState = getProofState(index);
			for (IPSStatus prSequent : psFile.getStatuses()) {
				UserSupportUtils.debug("Trying: " + prSequent.getElementName());
				UserSupportUtils.debug("Index: " + index);
				if (proofState != null) {
					if (prSequent.equals(proofState.getPRSequent())) {
						index++;
						proofState = getProofState(index);
						continue;
					}
				}
				IProofState state = new ProofState(prSequent);
				UserSupportUtils.debug("Added at position " + index);
				proofStates.add(index++, state);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}

	}

	protected void processDelta(IRodinElementDelta elementChangedDelta,
			IProgressMonitor monitor) throws RodinDBException {
		IRodinElement element = elementChangedDelta.getElement();
		if (element instanceof IRodinProject) {
			for (IRodinElementDelta d : elementChangedDelta
					.getAffectedChildren()) {
				processDelta(d, monitor);
			}
		} else if (element instanceof IPSFile) {
			if (psFile.equals(element)) {
				for (IRodinElementDelta d : elementChangedDelta
						.getAffectedChildren()) {
					processDelta(d, monitor);
				}
			}
		} else if (element instanceof IPSStatus) {
			int kind = elementChangedDelta.getKind();

			if (kind == IRodinElementDelta.ADDED) {
				UserSupportUtils.debug("IPRSequent changed: "
						+ element.getElementName() + " is added");

				reload = true;
			} else if (kind == IRodinElementDelta.REMOVED) {
				UserSupportUtils.debug("IPRSequent changed: "
						+ element.getElementName() + " is removed");
				deleted.add((IPSStatus) element);
				reload = true;
			} else if (kind == IRodinElementDelta.CHANGED) {

				int flag = elementChangedDelta.getFlags();
				UserSupportUtils.debug("Flag: " + flag);

				// Trying to reuse only if the children of the PRSequent has
				// changed or if the prsequent has been replaced.
				if ((flag & RodinElementDelta.F_CHILDREN) != 0
						|| (flag & RodinElementDelta.F_REPLACED) != 0) {
					IPSStatus prSequent = (IPSStatus) element;

					IProofState state = getProofState(prSequent);

					UserSupportUtils.debug("Testing: "
							+ state.getPRSequent().getElementName());

					if (state.isUninitialised())
						return;

					else if (state.isSequentDischarged()) {
						UserSupportUtils.debug("Proof Discharged in file");
						state.getProofTree().removeChangeListener(this);
						state.loadProofTree(monitor);
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

					else if (state.isProofReusable()) {
						state.getProofTree().removeChangeListener(this);
						// TODO Fixed this
						// state.proofReuse();
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

					} else {
						UserSupportUtils.debug("Cannot be reused");
						// Trash the current proof tree and then re-build
						// state.unloadProofTree();
						if (!state.isDirty()) {
							if (state != currentPS) {
								state.getProofTree().removeChangeListener(this);
								state.unloadProofTree();
								ProofStateDelta newDelta = new ProofStateDelta(
										UserSupport.this);
								newDelta.setNewProofState(state);

								newDelta
										.addInformation("Current proof cannot be reused");
								fireProofStateDelta(newDelta);
							} else {
								// state.getProofTree().removeChangeListener(this);
								// state.reloadProofTree();
								// state.getProofTree().addChangeListener(this);
								// if (state == currentPS) {
								UserSupportUtils.debug("Is the current node");
								state.getProofTree().removeChangeListener(this);
								state.unloadProofTree();
								ProofStateDelta newDelta = new ProofStateDelta(
										UserSupport.this);
								newDelta.setNewProofState(currentPS);

								newDelta
										.addInformation("Current proof cannot be reused");
								fireProofStateDelta(newDelta);
								// }

							}
						}
					}

				}

			}
		}

		else if (element instanceof IPRProof) {
			IPRProof proofTree = (IPRProof) element;
			// IPRSequent prSequent = proofTree.getSequent();
			IPSStatus status = psFile.getStatus(proofTree.getElementName());

			IProofState state = getProofState(status);

			// do nothing if there is no state corresponding to this
			if (state == null)
				return;

			if (state.isUninitialised())
				return;

			// TODO : Son, why is this next check done? Farhad
			if (status.hasManualProof())
				return;

			if (state.isSequentDischarged()) {
				UserSupportUtils.debug("Proof Discharged in file");

				state.getProofTree().removeChangeListener(this);
				state.loadProofTree(monitor);
				state.getProofTree().addChangeListener(this);

				if (state == currentPS) {
					UserSupportUtils.debug("Is the current node");
					ProofStateDelta newDelta = new ProofStateDelta(
							UserSupport.this);
					newDelta.setNewProofState(currentPS);
					newDelta.addInformation("Current proof has been reused");
					fireProofStateDelta(newDelta);
				}
			}

		}

		else if (element instanceof IParent) {
			for (IRodinElementDelta d : elementChangedDelta
					.getAffectedChildren()) {
				processDelta(d, monitor);
			}
		}
	}

	private IProofState getProofState(IPSStatus prSequent) {
		for (IProofState state : proofStates) {
			if (state.getPRSequent().equals(prSequent))
				return state;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#back(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void back(IProgressMonitor monitor) throws RodinDBException {
		// TODO Batch operation.
		if (currentPS.getCurrentNode().getParent() != null) {
			selectNode(currentPS.getCurrentNode().getParent());
			prune(monitor);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#hasUnsavedChanges()
	 */
	public boolean hasUnsavedChanges() {
		for (IProofState ps : proofStates) {
			if (ps.isDirty())
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#getUnsavedPOs()
	 */
	public IProofState[] getUnsavedPOs() {
		Collection<IProofState> unsaved = new HashSet<IProofState>();
		for (IProofState ps : proofStates) {
			if (ps.isDirty())
				unsaved.add(ps);
		}
		return unsaved.toArray(new IProofState[unsaved.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#proofTreeChanged(org.eventb.core.seqprover.IProofTreeDelta)
	 */
	public void proofTreeChanged(IProofTreeDelta proofTreeDelta) {
		UserSupportUtils.debug("UserSupport - Proof Tree Changed: "
				+ proofTreeDelta);
		ProofStateDelta newDelta = new ProofStateDelta(this);
		newDelta.setProofTreeDelta(proofTreeDelta);
		newDelta.addInformation("Proof Tree Changed");
		fireProofStateDelta(newDelta);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#setComment(java.lang.String,
	 *      org.eventb.core.seqprover.IProofTreeNode)
	 */
	public void setComment(String text, IProofTreeNode node) {
		node.setComment(text);
		currentPS.setDirty(true);
		ProofStateDelta newDelta = new ProofStateDelta(this);
		newDelta.setNewCurrentNode(currentPS.getCurrentNode());
		newDelta.addInformation("Comment has been set");
		fireProofStateDelta(newDelta);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#getPOs()
	 */
	public Collection<IProofState> getPOs() {
		return proofStates;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#getInput()
	 */
	public IPSFile getInput() {
		return psFile;
	}

	//
	// public boolean isOutOfDate() {
	// return outOfDate;
	// }

	// Should be used by the UserSupportManager only
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#dispose()
	 */
	public void dispose() {
		RodinCore.removeElementChangedListener(this);
		if (currentPS != null)
			currentPS.getProofTree().removeChangeListener(this);
	}

}