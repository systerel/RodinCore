package org.eventb.internal.core.pm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.IPSWrapper;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.internal.core.PSWrapper;
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

public class UserSupport implements IElementChangedListener, IUserSupport {

	// private IPSFile psFile; // Unique for an instance of UserSupport

	LinkedList<IProofState> proofStates;

	protected IProofState currentPS;

	private UserSupportManager manager;

	DeltaProcessor deltaProcessor;

	private Collection<Object> information;

	IPSWrapper psWrapper;

	public UserSupport() {
		RodinCore.addElementChangedListener(this);
		proofStates = new LinkedList<IProofState>();
		manager = (UserSupportManager) EventBPlugin.getDefault()
				.getUserSupportManager();
		deltaProcessor = manager.getDeltaProcessor();
		manager.addUserSupport(this);

		deltaProcessor.newUserSupport(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#setInput(org.eventb.core.IPSFile,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setInput(final IPSFile psFile, final IProgressMonitor monitor)
			throws RodinDBException {

		psWrapper = new PSWrapper(psFile);

		// this.psFile = psFile;
		proofStates = new LinkedList<IProofState>();

		manager.run(new Runnable() {

			public void run() {
				try {
					IPSStatus[] statuses = psWrapper.getPSStatuses();
					for (int i = 0; i < statuses.length; i++) {
						IPSStatus prSequent = statuses[i];
						ProofState state = new ProofState(UserSupport.this,
								prSequent);
						proofStates.add(state);
						deltaProcessor.newProofState(UserSupport.this, state);
					}
					// Do not fire delta. The delta will be fire in
					// nextUndischargedPO()
					nextUndischargedPO(true, monitor);
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
			}

		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#dispose()
	 */
	public void dispose() {
		startInformation();
		RodinCore.removeElementChangedListener(this);

		manager.removeUserSupport(this);
		deltaProcessor.removeUserSupport(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#getInput()
	 */
	public IPSFile getInput() {
		if (psWrapper != null)
			return psWrapper.getPSFile();
		return null;
	}

	void startInformation() {
		information = new ArrayList<Object>();
	}

	void addInformation(Object obj) {
		assert (information != null);
		information.add(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#nextUndischargedPO(boolean,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void nextUndischargedPO(final boolean force,
			final IProgressMonitor monitor) throws RodinDBException {
		startInformation();
		final int index;
		if (currentPS == null) {
			index = -1;
		} else {
			index = proofStates.indexOf(currentPS);
		}
		manager.run(new Runnable() {

			public void run() {
				for (int i = 1; i <= proofStates.size(); i++) {
					IProofState ps = proofStates.get((index + i)
							% proofStates.size());
					try {
						if (!ps.isClosed()) {
							setProofState(ps, monitor);
							return;
						}
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (force) {
					try {
						setProofState(null, monitor);
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				addInformation("No un-discharged proof obligation found");
				deltaProcessor.informationChanged(UserSupport.this);
			}

		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#prevUndischargedPO(boolean,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void prevUndischargedPO(final boolean force,
			final IProgressMonitor monitor) throws RodinDBException {
		startInformation();
		final int index;
		if (currentPS == null) {
			index = -1;
		} else {
			index = proofStates.indexOf(currentPS);
		}
		manager.run(new Runnable() {

			public void run() {
				for (int i = 1; i <= proofStates.size(); i++) {
					IProofState ps = proofStates.get((proofStates.size()
							+ index - i)
							% proofStates.size());
					try {
						if (!ps.isClosed()) {
							setProofState(ps, monitor);
							return;
						}
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (force) {
					try {
						setProofState(null, monitor);
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				addInformation("No un-discharged proof obligation found");
				deltaProcessor.informationChanged(UserSupport.this);
			}

		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#getCurrentPO()
	 */
	public IProofState getCurrentPO() {
		return currentPS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#setCurrentPO(org.eventb.core.IPSstatus,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setCurrentPO(IPSStatus psStatus, IProgressMonitor monitor)
			throws RodinDBException {
		if (psStatus == null) {
			setProofState(null, monitor);
			return;
		}
		for (IProofState ps : proofStates) {
			if (ps.getPRSequent().equals(psStatus)) {
				setProofState(ps, monitor);
				return;
			}
		}
	}

	void setProofState(final IProofState ps, final IProgressMonitor monitor)
			throws RodinDBException {
		startInformation();
		if (currentPS == ps) {
			// Try to fire the remaining delta
			addInformation("No new obligation");
			deltaProcessor.informationChanged(this);
			return;
		}

		manager.run(new Runnable() {

			public void run() {
				UserSupportUtils.debug("New Proof Sequent: " + ps);
				if (ps == null) {
					currentPS = null;
				} else {
					currentPS = ps;
					// Load the proof tree if it is not there already
					if (ps.getProofTree() == null) {
						try {
							ps.loadProofTree(monitor);
						} catch (RodinDBException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				deltaProcessor.currentProofStateChange(UserSupport.this);
				addInformation("Obligation changed");
				deltaProcessor.informationChanged(UserSupport.this);
			}

		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#getPOs()
	 */
	public IProofState[] getPOs() {
		return proofStates.toArray(new IProofState[proofStates.size()]);
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

	public Collection<Object> getInformation() {
		return information;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#removeCachedHypotheses(java.util.Collection)
	 */
	public void removeCachedHypotheses(final Collection<Predicate> hyps) {
		startInformation();
		manager.run(new Runnable() {

			public void run() {
				currentPS.removeAllFromCached(hyps);
				addInformation("Removed hypotheses from cache");
				deltaProcessor.informationChanged(UserSupport.this);
			}

		});
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#searchHyps(java.lang.String)
	 */
	public void searchHyps(String token) {
		token = token.trim();

		final Set<Predicate> hyps = ProverLib.hypsTextSearch(currentPS
				.getCurrentNode().getSequent(), token);
		startInformation();
		manager.run(new Runnable() {

			public void run() {
				currentPS.setSearched(hyps);
				addInformation("Search hypotheses");
				deltaProcessor.informationChanged(UserSupport.this);
			}

		});

		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#removeSearchedHypotheses(java.util.Collection)
	 */
	public void removeSearchedHypotheses(final Collection<Predicate> hyps) {
		startInformation();
		manager.run(new Runnable() {

			public void run() {
				currentPS.removeAllFromSearched(hyps);
				addInformation("Removed hypotheses from search");
				deltaProcessor.informationChanged(UserSupport.this);
			}

		});
		return;
	}

	public void selectNode(IProofTreeNode node) throws RodinDBException {
		currentPS.setCurrentNode(node);
	}

	protected void addAllToCached(Set<Predicate> hyps) {
		currentPS.addAllToCached(hyps);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#applyTactic(org.eventb.core.seqprover.ITactic,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void applyTactic(final ITactic t, final IProgressMonitor monitor)
			throws RodinDBException {
		IProofTreeNode node = currentPS.getCurrentNode();
		currentPS.applyTactic(t, node, new ProofMonitor(monitor));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#applyTacticToHypotheses(org.eventb.core.seqprover.ITactic,
	 *      java.util.Set, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void applyTacticToHypotheses(ITactic t, Set<Predicate> hyps,
			IProgressMonitor monitor) throws RodinDBException {
		currentPS.applyTacticToHypotheses(t, currentPS.getCurrentNode(), hyps,
				monitor);
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
			IProofState state = new ProofState(this, prSequent);
			proofStates.remove(state);
		}

		int index = 0;
		IProofState proofState = getProofState(index);
		IPSStatus[] statuses;
		try {
			statuses = psWrapper.getPSStatuses();
		} catch (RodinDBException e) {
			e.printStackTrace();
			return;
		}
		for (IPSStatus prSequent : statuses) {
			UserSupportUtils.debug("Trying: " + prSequent.getElementName());
			UserSupportUtils.debug("Index: " + index);
			if (proofState != null) {
				if (prSequent.equals(proofState.getPRSequent())) {
					index++;
					proofState = getProofState(index);
					continue;
				}
			}
			IProofState state = new ProofState(this, prSequent);
			UserSupportUtils.debug("Added at position " + index);
			proofStates.add(index++, state);
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
			if (this.getInput().equals(element)) {
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
						state.loadProofTree(monitor);
						if (state == currentPS) {
							UserSupportUtils.debug("Is the current node");
							// ProofStateDelta newDelta = new ProofStateDelta(
							// UserSupport.this);
							// newDelta.setNewProofState(currentPS);
							// newDelta
							// .addInformation("Current proof has been reused");
							// fireDelta(newDelta);
						}
					}

					else if (state.isProofReusable()) {
						// TODO Fixed this
						// state.proofReuse();
						if (state == currentPS) {
							UserSupportUtils.debug("Is the current node");
							// ProofStateDelta newDelta = new ProofStateDelta(
							// UserSupport.this);
							// newDelta.setNewProofState(currentPS);
							// newDelta
							// .addInformation("Current proof has been reused");
							// fireDelta(newDelta);
						}

					} else {
						UserSupportUtils.debug("Cannot be reused");
						// Trash the current proof tree and then re-build
						// state.unloadProofTree();
						if (!state.isDirty()) {
							if (state != currentPS) {
								state.unloadProofTree();
								// ProofStateDelta newDelta = new
								// ProofStateDelta(
								// UserSupport.this);
								// newDelta.setNewProofState(state);

								// newDelta
								// .addInformation("Current proof cannot be
								// reused");
								// fireDelta(newDelta);
							} else {
								// state.getProofTree().removeChangeListener(this);
								// state.reloadProofTree();
								// state.getProofTree().addChangeListener(this);
								// if (state == currentPS) {
								UserSupportUtils.debug("Is the current node");
								state.unloadProofTree();
								// ProofStateDelta newDelta = new
								// ProofStateDelta(
								// UserSupport.this);
								// newDelta.setNewProofState(currentPS);
								//
								// newDelta
								// .addInformation("Current proof cannot be
								// reused");
								// fireDelta(newDelta);
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
			IPSStatus status = this.getInput().getStatus(
					proofTree.getElementName());

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

				state.loadProofTree(monitor);

				if (state == currentPS) {
					UserSupportUtils.debug("Is the current node");
					// ProofStateDelta newDelta = new ProofStateDelta(
					// UserSupport.this);
					// newDelta.setNewProofState(currentPS);
					// newDelta.addInformation("Current proof has been reused");
					// fireDelta(newDelta);
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
		currentPS.back(currentPS.getCurrentNode(), monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#setComment(java.lang.String,
	 *      org.eventb.core.seqprover.IProofTreeNode)
	 */
	public void setComment(String text, IProofTreeNode node)
			throws RodinDBException {
		currentPS.setComment(text, node);
	}

	boolean reload;

	public void elementChanged(final ElementChangedEvent event) {
		final IProgressMonitor monitor = new NullProgressMonitor();
		reload = false;
		deleted = new ArrayList<IPSStatus>();
		try {
			UserSupportManager.getDefault().run(new Runnable() {

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
							// ProofStateDelta newDelta = new ProofStateDelta(
							// UserSupport.this);

							// newDelta.setDeletedProofState(currentPS);
							// newDelta.addInformation("Current PO has been
							// deleted");
							// fireDelta(newDelta);
						}
					}
					// if (outOfDate) {
					// ProofStateDelta newDelta = new ProofStateDelta(this);
					// newDelta.addInformation("Underlying model has changed");
					// fireProofStateDelta(newDelta);
					// }
				}

			});
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public IPSWrapper getPSWrapper() {
		return psWrapper;
	}

	public void doSave(Object[] states, IProgressMonitor monitor) throws CoreException {
		for (Object state : states) {
			assert (state instanceof IProofState);
			((IProofState) state).setProofTree(monitor);
		}
		this.getPSWrapper().save(monitor, true);
		for (Object state : states) {
			((IProofState) state).setDirty(false);
		}
	}

}