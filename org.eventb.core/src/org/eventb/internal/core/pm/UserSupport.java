package org.eventb.internal.core.pm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBPlugin;
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
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class UserSupport implements IElementChangedListener, IUserSupport {

	LinkedHashMap<IPSStatus, IProofState> proofStates;

	protected IPSStatus currentPSStatus;

	private UserSupportManager manager;

	DeltaProcessor deltaProcessor;

	private Collection<Object> information;

	IPSWrapper psWrapper; // Unique for an instance of UserSupport

	public UserSupport() {
		RodinCore.addElementChangedListener(this);
		proofStates = new LinkedHashMap<IPSStatus, IProofState>();
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
		proofStates = new LinkedHashMap<IPSStatus, IProofState>();

		manager.run(new Runnable() {

			public void run() {
				try {
					IPSStatus[] statuses = psWrapper.getPSStatuses();
					for (int i = 0; i < statuses.length; i++) {
						IPSStatus psStatus = statuses[i];
						ProofState state = new ProofState(UserSupport.this,
								psStatus);
						proofStates.put(psStatus, state);
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
		final Set<IPSStatus> keySet = proofStates.keySet();
		final IPSStatus[] keys = keySet.toArray(new IPSStatus[keySet.size()]);
		int tmp;
		if (currentPSStatus == null) {
			tmp = -1;
		} else {
			tmp = -1;
			for (int i = 0; i < keys.length; ++i) {
				if (keys[i].equals(currentPSStatus)) {
					tmp = i;
					break;
				}
			}
		}

		final int index = tmp;

		manager.run(new Runnable() {

			public void run() {
				for (int i = 1; i <= keys.length; i++) {
					int j = (index + i) % keys.length;
					IPSStatus psStatus = keys[j];
					IProofState ps = proofStates.get(psStatus);
					try {
						if (!ps.isClosed()) {
							setProofState(psStatus, monitor);
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
		final Set<IPSStatus> keySet = proofStates.keySet();
		final IPSStatus[] keys = keySet.toArray(new IPSStatus[keySet.size()]);
		int tmp;
		if (currentPSStatus == null) {
			tmp = -1;
		} else {
			tmp = -1;
			for (int i = 0; i < keys.length; ++i) {
				if (keys[i].equals(currentPSStatus)) {
					tmp = i;
					break;
				}
			}
		}

		final int index = tmp;

		manager.run(new Runnable() {

			public void run() {
				for (int i = 1; i <= keys.length; i++) {
					IPSStatus psStatus = keys[(keys.length + index - i)
							% keys.length];
					IProofState ps = proofStates.get(psStatus);
					try {
						if (!ps.isClosed()) {
							setProofState(psStatus, monitor);
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
		return proofStates.get(currentPSStatus);
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
		setProofState(psStatus, monitor);
	}

	void setProofState(final IPSStatus psStatus, final IProgressMonitor monitor)
			throws RodinDBException {
		startInformation();
		if (currentPSStatus == psStatus) {
			// Try to fire the remaining delta
			addInformation("No new obligation");
			deltaProcessor.informationChanged(this);
			return;
		}

		manager.run(new Runnable() {

			public void run() {
				if (UserSupportUtils.DEBUG)
					UserSupportUtils.debug("New Proof Sequent: " + psStatus);
				if (psStatus == null) {
					currentPSStatus = null;
				} else {
					currentPSStatus = psStatus;
					// Load the proof tree if it is not there already
					IProofState proofState = proofStates.get(currentPSStatus);
					assert proofState != null;
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
		return proofStates.values()
				.toArray(new IProofState[proofStates.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#hasUnsavedChanges()
	 */
	public boolean hasUnsavedChanges() {
		for (IProofState ps : proofStates.values()) {
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
		for (IProofState ps : proofStates.values()) {
			if (ps.isDirty())
				unsaved.add(ps);
		}
		return unsaved.toArray(new IProofState[unsaved.size()]);
	}

	public Object[] getInformation() {
		return information.toArray();
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
				proofStates.get(currentPSStatus).removeAllFromCached(hyps);
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

		final IProofState proofState = proofStates.get(currentPSStatus);
		final Set<Predicate> hyps = ProverLib.hypsTextSearch(proofState
				.getCurrentNode().getSequent(), token);
		startInformation();
		manager.run(new Runnable() {

			public void run() {
				proofState.setSearched(hyps);
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
				proofStates.get(currentPSStatus).removeAllFromSearched(hyps);
				addInformation("Removed hypotheses from search");
				deltaProcessor.informationChanged(UserSupport.this);
			}

		});
		return;
	}

	public void selectNode(IProofTreeNode node) throws RodinDBException {
		proofStates.get(currentPSStatus).setCurrentNode(node);
	}

	protected void addAllToCached(Set<Predicate> hyps) {
		proofStates.get(currentPSStatus).addAllToCached(hyps);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#applyTactic(org.eventb.core.seqprover.ITactic,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void applyTactic(final ITactic t, final IProgressMonitor monitor)
			throws RodinDBException {
		final IProofState proofState = proofStates.get(currentPSStatus);
		IProofTreeNode node = proofState.getCurrentNode();
		proofState.applyTactic(t, node, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#applyTacticToHypotheses(org.eventb.core.seqprover.ITactic,
	 *      java.util.Set, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void applyTacticToHypotheses(ITactic t, Set<Predicate> hyps,
			IProgressMonitor monitor) throws RodinDBException {
		final IProofState proofState = proofStates.get(currentPSStatus);
		proofState.applyTacticToHypotheses(t, proofState.getCurrentNode(),
				hyps, monitor);
	}

	IProofState getProofState(int index) {
		IProofState proofState = null;
		if (index < proofStates.size())
			proofState = proofStates.get(index);
		return proofState;
	}

	void refresh() {
		manager.run(new Runnable() {

			public void run() {

				LinkedHashMap<IPSStatus, IProofState> newProofStates;
				// Remove the deleted ones first
				for (IPSStatus psStatus : usDeltaProcessor.getToBeDeleted()) {
					deltaProcessor.removeProofState(UserSupport.this,
							proofStates.get(psStatus));
					proofStates.remove(psStatus);
				}

				// Contruct the Proof States
				IPSStatus[] statuses;
				try {
					statuses = psWrapper.getPSStatuses();
				} catch (RodinDBException e) {
					e.printStackTrace();
					return;
				}

				newProofStates = new LinkedHashMap<IPSStatus, IProofState>(
						statuses.length);

				for (IPSStatus status : statuses) {
					IProofState proofState = proofStates.get(status);

					if (proofState == null) { // A new PS Status
						IProofState state = new ProofState(UserSupport.this,
								status);
						newProofStates.put(status, state);
						deltaProcessor.newProofState(UserSupport.this, state);
					} else {
						newProofStates.put(status, proofState);
					}
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
		final IProofState proofState = proofStates.get(currentPSStatus);
		proofState.back(proofState.getCurrentNode(), monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#setComment(java.lang.String,
	 *      org.eventb.core.seqprover.IProofTreeNode)
	 */
	public void setComment(String text, IProofTreeNode node)
			throws RodinDBException {
		proofStates.get(currentPSStatus).setComment(text, node);
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
				for (IPSStatus psStatus : usDeltaProcessor.getToBeReloaded()) {
					IProofState proofState = proofStates.get(psStatus);

					try {
						proofState.loadProofTree(monitor);
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						continue;
					}
				}

				// Process Reused
				for (IPSStatus psStatus : usDeltaProcessor.getToBeReused()) {
					IProofState proofState = proofStates.get(psStatus);
					try {
						proofState.proofReuse(new ProofMonitor(
								monitor));
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// Process Rebuilt
				for (IPSStatus psStatus : usDeltaProcessor.getToBeRebuilt()) {
					IProofState proofState = proofStates.get(psStatus);
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
			throws CoreException {
		for (IProofState state : states) {
			state.setProofTree(monitor);
			state.getPSStatus().setManualProof(true, monitor);
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
		buffer.append(currentPSStatus);
		buffer.append("\n");
		buffer.append("** Information **\n");
		for (Object info : information) {
			buffer.append("  " + info);
			buffer.append("\n");
		}
		buffer.append("********************************************************\n");
		return buffer.toString();
	}

	
}