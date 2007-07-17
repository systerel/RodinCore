package org.eventb.internal.core.pm;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.IPSWrapper;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportInformation;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProofTreeNodeFilter;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.internal.core.PSWrapper;
import org.eventb.internal.core.ProofMonitor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class UserSupport implements IElementChangedListener, IUserSupport {

	LinkedHashSet<IProofState> proofStates;

	protected ProofState currentPS;

	private UserSupportManager manager;

	DeltaProcessor deltaProcessor;

	IPSWrapper psWrapper; // Unique for an instance of UserSupport

	public UserSupport() {
		RodinCore.addElementChangedListener(this);
		proofStates = new LinkedHashSet<IProofState>();
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

		proofStates = new LinkedHashSet<IProofState>();

		manager.run(new Runnable() {

			public void run() {
				try {
					IPSStatus[] statuses = psWrapper.getPSStatuses();
					for (int i = 0; i < statuses.length; i++) {
						IPSStatus psStatus = statuses[i];
						ProofState state = new ProofState(UserSupport.this,
								psStatus);
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

//	void startInformation() {
//		information = new ArrayList<IUserSupportInformation>();
//	}

//	void addInformation(Object obj, int priority) {
//		assert (information != null);
//		assert (IUserSupportInformation.MIN_PRIORITY <= priority);
//		assert (priority <= IUserSupportInformation.MAX_PRIORITY);
//		information.add(new UserSupportInformation(obj, priority));
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#nextUndischargedPO(boolean,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void nextUndischargedPO(final boolean force,
			final IProgressMonitor monitor) throws RodinDBException {
		boolean found = false;
		IProofState newProofState = null;
		IProofState firstOpenedProofState = null;
		for (IProofState proofState : proofStates) {
			if (firstOpenedProofState == null && !proofState.isClosed()) {
				firstOpenedProofState = proofState;
			}
			if (found) {
				if (!proofState.isClosed()) {
					newProofState = proofState;
					break;
				}
			}
			else {
				if (proofState.equals(currentPS)) {
					found = true;
				}
			}
		}
		
		if (found && newProofState == null)  {// Have not found new proof State yet
			newProofState = firstOpenedProofState;
		}
		else if (!found) {
			newProofState = firstOpenedProofState;
		}
		
		final IProofState proofState = newProofState;

		manager.run(new Runnable() {

			public void run() {
				try {
					if (proofState != null)
						setProofState(proofState, monitor);
					else if (force) {
						setProofState(null, monitor);
						deltaProcessor
								.informationChanged(
										UserSupport.this,
										new UserSupportInformation(
												"No un-discharged proof obligation found",
												IUserSupportInformation.MAX_PRIORITY));
					}
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
		boolean found = false;
		IProofState newProofState = null;
		IProofState lastOpenedProofState = null;
		for (IProofState proofState : proofStates) {
			if (!found) {
				if (proofState.equals(currentPS)) {
					if (lastOpenedProofState != null) {
						newProofState = lastOpenedProofState;
						break;
					}
					found = true;
				}
			}
			if (!proofState.isClosed()) {
				lastOpenedProofState = proofState;
			}
		}
		
		if (found && newProofState == null)  {// Have not found new proof State yet
			newProofState = lastOpenedProofState;
		}
		else if (!found) {
			newProofState = lastOpenedProofState;
		}
		
		final IProofState proofState = newProofState;

		manager.run(new Runnable() {

			public void run() {
				try {
					if (proofState != null)
						setProofState(proofState, monitor);
					else if (force) {
						setProofState(null, monitor);
						deltaProcessor
								.informationChanged(
										UserSupport.this,
										new UserSupportInformation(
												"No un-discharged proof obligation found",
												IUserSupportInformation.MAX_PRIORITY));
					}
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
		for (IProofState proofState : proofStates) {
			if (proofState.getPSStatus().equals(psStatus))
				setProofState(proofState, monitor);			
		}
	}

	void setProofState(final IProofState proofState, final IProgressMonitor monitor)
			throws RodinDBException {
		if (currentPS == null && proofState == null) {
			// Try to fire the remaining delta
			deltaProcessor.informationChanged(this, new UserSupportInformation(
					"No new obligation", IUserSupportInformation.MIN_PRIORITY));
			return;
		}

		if (currentPS != null && currentPS.equals(proofState)) {
			// Try to fire the remaining delta
			deltaProcessor.informationChanged(this, new UserSupportInformation(
					"No new obligation", IUserSupportInformation.MIN_PRIORITY));
			return;			
		}
		
		manager.run(new Runnable() {

			public void run() {
				if (UserSupportUtils.DEBUG)
					UserSupportUtils.debug("New Proof Sequent: " + proofState);
				if (proofState == null) {
					currentPS = null;
				} else {
					currentPS = (ProofState) proofState;
					// Load the proof tree if it is not there already
					if (proofState.getProofTree() == null) {
						try {
							proofState.loadProofTree(monitor);
						} catch (RodinDBException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				deltaProcessor.currentProofStateChange(UserSupport.this);
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
		for (IProofState proofState : proofStates) {
			if (proofState.isDirty())
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
		for (IProofState proofState : proofStates) {
			if (proofState.isDirty())
				unsaved.add(proofState);
		}
		return unsaved.toArray(new IProofState[unsaved.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IUserSupport#getInformation()
	 */
	@Deprecated
	public Object[] getInformation() {
		return new Object[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#removeCachedHypotheses(java.util.Collection)
	 */
	public void removeCachedHypotheses(final Collection<Predicate> hyps) {
		manager.run(new Runnable() {

			public void run() {
				currentPS.removeAllFromCached(hyps);
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
		manager.run(new Runnable() {

			public void run() {
				currentPS.setSearched(hyps);
				deltaProcessor.informationChanged(UserSupport.this,
						new UserSupportInformation("Search hypotheses",
								IUserSupportInformation.MAX_PRIORITY));
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
		manager.run(new Runnable() {

			public void run() {
				currentPS.removeAllFromSearched(hyps);
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
	@Deprecated
	public void applyTactic(final ITactic t, final IProgressMonitor monitor)
			throws RodinDBException {
		applyTactic(t, true, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#applyTactic(org.eventb.core.seqprover.ITactic,
	 *      boolean, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void applyTactic(final ITactic t, boolean applyPostTactic,
			final IProgressMonitor monitor) throws RodinDBException {
		IProofTreeNode node = currentPS.getCurrentNode();
		currentPS.applyTactic(t, node, applyPostTactic, monitor);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#applyTacticToHypotheses(org.eventb.core.seqprover.ITactic,
	 *      java.util.Set, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Deprecated
	public void applyTacticToHypotheses(ITactic t, Set<Predicate> hyps,
			IProgressMonitor monitor) throws RodinDBException {
		applyTacticToHypotheses(t, hyps, true, monitor);
	}

	public void applyTacticToHypotheses(ITactic t, Set<Predicate> hyps,
			boolean applyPostTactic, IProgressMonitor monitor)
			throws RodinDBException {
		currentPS.applyTacticToHypotheses(t, currentPS.getCurrentNode(), hyps,
				applyPostTactic, monitor);
	}

	void refresh() {
		manager.run(new Runnable() {

			public void run() {

				LinkedHashSet<IProofState> newProofStates;
				// Remove the deleted ones first
				for (IProofState proofState : usDeltaProcessor.getToBeDeleted()) {
					deltaProcessor.removeProofState(UserSupport.this,
							proofState);
					proofStates.remove(proofState);
				}
				
				// Contruct the Proof States
				IPSStatus[] psStatuses;
				try {
					psStatuses = psWrapper.getPSStatuses();
				} catch (RodinDBException e) {
					e.printStackTrace();
					return;
				}

				newProofStates = new LinkedHashSet<IProofState>(
						psStatuses.length);

				for (IPSStatus psStatus : psStatuses) {
					IProofState proofState = UserSupport.this.getProofState(psStatus);

					if (proofState == null) { // A new PS Status
						proofState = new ProofState(UserSupport.this,
								psStatus);
						deltaProcessor.newProofState(UserSupport.this, proofState);
					}
					newProofStates.add(proofState);
				}

				proofStates = newProofStates;
			}
		});
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

	UserSupportDeltaProcessor usDeltaProcessor;
	
	public void elementChanged(final ElementChangedEvent event) {
		final IProgressMonitor monitor = new NullProgressMonitor();
		usDeltaProcessor = new UserSupportDeltaProcessor(this);
		IRodinElementDelta delta = event.getDelta();
		if (UserSupportUtils.DEBUG) {
			UserSupportUtils.debug("Delta: " + delta);
		}
		usDeltaProcessor.processDelta(delta, monitor);
		if (UserSupportUtils.DEBUG) {
			UserSupportUtils.debug(usDeltaProcessor.toString());
		}
		
		manager.run(new Runnable() {

			public void run() {
				// Process trashed proofs first

				// Then refresh to get all the proof states
				if (usDeltaProcessor.needRefreshed()) {
					refresh();
				}

				// Process reloaded
				for (IProofState proofState : usDeltaProcessor.getToBeReloaded()) {
					try {
						proofState.loadProofTree(monitor);
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						continue;
					}
				}

				// Process Reused
				for (IProofState proofState : usDeltaProcessor.getToBeReused()) {
					try {
						proofState.proofReuse(new ProofMonitor(
								monitor));
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// Process Rebuilt
				for (IProofState proofState : usDeltaProcessor.getToBeRebuilt()) {
					try {
						proofState.proofRebuilt(new ProofMonitor(
								new NullProgressMonitor()));
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		});

	}

	public IPSWrapper getPSWrapper() {
		return psWrapper;
	}

	public void doSave(IProofState[] states, IProgressMonitor monitor)
			throws RodinDBException {
		for (IProofState state : states) {
			state.setProofTree(monitor);
			// state.getPSStatus().setManualProof(true, monitor);
		}
		this.getPSWrapper().save(monitor, true);
		for (IProofState state : states) {
			state.setDirty(false);
		}
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("****** User Support for: ");
		buffer.append(this.getInput().getBareName() + " ******\n");
		buffer.append("** Proof States **\n");
		for (IProofState proofState : getPOs()) {
			buffer.append(proofState.toString());
			buffer.append("\n");
		}
		buffer.append("Current psSatus: ");
		buffer.append(currentPS.getPSStatus());
		buffer.append("\n");
		buffer.append("********************************************************\n");
		return buffer.toString();
	}

	public IProofState getProofState(IPSStatus psStatus) {
		for (IProofState proofState : proofStates) {
			if (proofState.getPSStatus().equals(psStatus))
				return proofState;
		}
		return null;
	}

	public void selectNextSubgoal(boolean rootIncluded,
			IProofTreeNodeFilter filter) throws RodinDBException {
		if (currentPS == null)
			return;
		currentPS.selectNextSubGoal(currentPS.getCurrentNode(), rootIncluded,
				filter);
	}

	
}