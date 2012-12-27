/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.pm;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportInformation;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.pm.IUserSupportManagerChangedListener;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.core.seqprover.IProofTreeDelta;
import org.eventb.internal.core.Util;

/**
 * @author htson
 *         <p>
 *         Processor for user support manager deltas. This class manages
 *         listeners and maintains a current delta.
 *         <p>
 *         This based on
 *         <code>org.eventb.internal.core.seqprover.DeltaProcessor</code> by
 *         Laurent Voisin
 */
public class DeltaProcessor {
	
	// True iff we're currently notifying listeners
	boolean firing;

	// Registered listeners
	List<IUserSupportManagerChangedListener> listeners;
	
	// Root of delta tree to notify.  Might be null.
	UserSupportManagerDelta rootDelta;
	
	// The User Support manager for which we process deltas
	IUserSupportManager manager;

	// Only fire delta if this flag is set
	boolean fireEnable;
	
	/**
	 * Creates a new delta processor for the given proof tree.
	 */
	public DeltaProcessor(IUserSupportManager manager) {
		this.manager = manager;
		this.rootDelta = null;
		this.listeners = new ArrayList<IUserSupportManagerChangedListener>();
		this.firing = false;
		this.fireEnable = true;
	}

	public void addChangeListener(IUserSupportManagerChangedListener listener) {
		if (listeners.contains(listener)) {
			return;
		}
		listeners.add(listener);
	}

	public void fireDeltas() {
		if (rootDelta == null) {
			// Nothing to fire.
			return;
		}
		if (firing) {
			// Don't fire again deltas while already doing it.
			return;
		}
		
		if (!fireEnable) {
			// Don't fire deltas if the flag is not set
			return;
		}
		// Save the delta tree
		IUserSupportManagerDelta savedRoot = rootDelta;
		rootDelta = null;
		
		final int length = listeners.size();
		if (length == 0) {
			// No listeners
			return;
		}
		// Save the list of listeners
		final IUserSupportManagerChangedListener[] savedListeners = listeners
				.toArray(new IUserSupportManagerChangedListener[length]);
		try {
			firing = true;
			if (UserSupportUtils.DEBUG)
				UserSupportUtils.debug("Start firing delta");
			for (IUserSupportManagerChangedListener listener : savedListeners) {
				notifyListener(listener, savedRoot);
			}
			if (UserSupportUtils.DEBUG)
				UserSupportUtils.debug("End firing delta");
		} finally {
			firing = false;
		}
	}

	private void notifyListener(
			final IUserSupportManagerChangedListener listener,
			final IUserSupportManagerDelta delta) {
		
		// wrap callback with Safe runnable for subsequent listeners
		// to be called when some are causing grief
		SafeRunner.run(new ISafeRunnable() {
			@Override
			public void handleException(Throwable exception) {
				Util.log(exception, 
						"Exception within user support manager change notification"); //$NON-NLS-1$
			}
			@Override
			public void run() throws Exception {
				listener.userSupportManagerChanged(delta);
			}
		});
	}

	public void removeChangeListener(IUserSupportManagerChangedListener listener) {
		listeners.remove(listener);
	}
	
	public void setEnable(boolean fireEnable) {
		this.fireEnable = fireEnable;
	}

	public boolean isEnable() {
		return fireEnable;
	}

	public void addAffectedUserSupport(UserSupportDelta affectedUserSupport) {
		if (rootDelta == null)
			rootDelta = new UserSupportManagerDelta();
		
		rootDelta.addAffectedUserSupport(affectedUserSupport);
		return;
	}

	public void newUserSupport(UserSupport userSupport) {
		if (rootDelta == null)
			rootDelta = new UserSupportManagerDelta();

		// Create a new delta
		UserSupportDelta affectedUserSupport = new UserSupportDelta(userSupport);
		affectedUserSupport.setKind(IUserSupportDelta.ADDED);
		
		// Added to the deltas
		rootDelta.addAffectedUserSupport(affectedUserSupport);		
		// Fire the deltas
		this.fireDeltas();
	}

	public void removeUserSupport(UserSupport userSupport) {
		if (rootDelta == null)
			rootDelta = new UserSupportManagerDelta();

		UserSupportDelta affectedUserSupport = new UserSupportDelta(userSupport);
		affectedUserSupport.setKind(IUserSupportDelta.REMOVED);
		// Added to the delta
		rootDelta.addAffectedUserSupport(affectedUserSupport);
		
		// Fire the delta
		this.fireDeltas();
	}

	public void currentProofStateChange(UserSupport userSupport) {
		if (rootDelta == null)
			rootDelta = new UserSupportManagerDelta();

		UserSupportDelta affectedUserSupport = new UserSupportDelta(userSupport);
		affectedUserSupport.setKind(IUserSupportDelta.CHANGED);
		affectedUserSupport.setFlags(IUserSupportDelta.F_CURRENT
				| IUserSupportDelta.F_INFORMATION);
		affectedUserSupport
				.addInformation(new UserSupportInformation(
						"New current obligation",
						IUserSupportInformation.MAX_PRIORITY));
		// Added to the delta
		rootDelta.addAffectedUserSupport(affectedUserSupport);
		
		// Fire the delta
		this.fireDeltas();
	}

	public void newProofState(IUserSupport userSupport, IProofState state) {
		if (rootDelta == null)
			rootDelta = new UserSupportManagerDelta();
		
		ProofStateDelta affectedState = new ProofStateDelta(state);
		affectedState.setKind(IProofStateDelta.ADDED);
		
		rootDelta.addAffectedProofState(userSupport, affectedState);
		this.fireDeltas();
	}


	public void setNewCurrentNode(IUserSupport userSupport, IProofState state) {
		if (rootDelta == null)
			rootDelta = new UserSupportManagerDelta();
		
		ProofStateDelta affectedState = new ProofStateDelta(state);
		affectedState.setKind(IProofStateDelta.CHANGED);
		affectedState.setFlags(IProofStateDelta.F_NODE);
		
		rootDelta.addAffectedProofState(userSupport, affectedState);
		this.fireDeltas();
	}

	public void cacheChanged(IUserSupport userSupport, IProofState state) {
		if (rootDelta == null)
			rootDelta = new UserSupportManagerDelta();
		
		ProofStateDelta affectedState = new ProofStateDelta(state);
		affectedState.setKind(IProofStateDelta.CHANGED);
		affectedState.setFlags(IProofStateDelta.F_CACHE);
		
		rootDelta.addAffectedProofState(userSupport, affectedState);
		this.fireDeltas();
	}


	public void searchChanged(IUserSupport userSupport, ProofState state) {
		if (rootDelta == null)
			rootDelta = new UserSupportManagerDelta();
		
		ProofStateDelta affectedState = new ProofStateDelta(state);
		affectedState.setKind(IProofStateDelta.CHANGED);
		affectedState.setFlags(IProofStateDelta.F_SEARCH);
		
		rootDelta.addAffectedProofState(userSupport, affectedState);
		this.fireDeltas();
	}

	public void informationChanged(IUserSupport userSupport, IUserSupportInformation info) {
		if (rootDelta == null)
			rootDelta = new UserSupportManagerDelta();

		UserSupportDelta affectedUserSupport = new UserSupportDelta(userSupport);
		affectedUserSupport.setKind(IUserSupportDelta.CHANGED);
		affectedUserSupport.setFlags(IUserSupportDelta.F_INFORMATION);
		affectedUserSupport.addInformation(info);
		
		// Added to the delta
		rootDelta.addAffectedUserSupport(affectedUserSupport);
		
		// Fire the delta
		this.fireDeltas();			
	}

	public void proofTreeChanged(UserSupport userSupport, ProofState state, IProofTreeDelta proofTreeDelta) {
		if (rootDelta == null)
			rootDelta = new UserSupportManagerDelta();
		
		ProofStateDelta affectedState = new ProofStateDelta(state);
		affectedState.setKind(IProofStateDelta.CHANGED);
		if ((affectedState.getFlags() & IProofStateDelta.F_PROOFTREE) == 0) {
			affectedState.setFlags(IProofStateDelta.F_PROOFTREE);
			affectedState.setProofTreeDelta(proofTreeDelta);
		}
		
		rootDelta.addAffectedProofState(userSupport, affectedState);
		this.fireDeltas();
	}

	public void newProofTree(UserSupport userSupport, ProofState state) {
		if (rootDelta == null)
			rootDelta = new UserSupportManagerDelta();
		
		ProofStateDelta affectedState = new ProofStateDelta(state);
		affectedState.setKind(IProofStateDelta.CHANGED);
		affectedState.setFlags(IProofStateDelta.F_PROOFTREE);
		affectedState.setProofTreeDelta(null);
		
		rootDelta.addAffectedProofState(userSupport, affectedState);
		this.fireDeltas();
	}

	public void removeProofState(IUserSupport userSupport, IProofState state) {
		if (rootDelta == null)
			rootDelta = new UserSupportManagerDelta();
		
		ProofStateDelta affectedState = new ProofStateDelta(state);
		affectedState.setKind(IProofStateDelta.REMOVED);
		
		rootDelta.addAffectedProofState(userSupport, affectedState);
		this.fireDeltas();
	}

}
