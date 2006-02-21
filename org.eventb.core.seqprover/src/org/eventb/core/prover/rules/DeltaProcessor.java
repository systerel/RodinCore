/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.prover.rules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.prover.IProofTreeChangedListener;
import org.eventb.core.prover.IProofTreeDelta;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.internal.core.prover.Util;

/**
 * Processor for proof tree deltas.
 * 
 * This class manages listeners and maintains a current delta.
 * 
 * @author Laurent Voisin
 */
public class DeltaProcessor {
	
	// True iff we're currently notifying listeners
	boolean firing;

	// Registered listeners
	List<IProofTreeChangedListener> listeners;
	
	// Root of delta tree to notify.  Might be null.
	ProofTreeDelta rootDelta;
	
	// Proof tree for which we process deltas
	ProofTree tree;

	/**
	 * Creates a new delta processor for the given proof tree.
	 */
	public DeltaProcessor(ProofTree tree) {
		this.tree = tree;
		this.rootDelta = null;
		this.listeners = new ArrayList<IProofTreeChangedListener>();
		this.firing = false;
	}

	public void addChangeListener(IProofTreeChangedListener listener) {
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
		// Save the delta tree
		ProofTreeDelta savedRoot = rootDelta;
		rootDelta = null;
		
		final int length = listeners.size();
		if (length == 0) {
			// No listeners
			return;
		}
		// Save the list of listeners
		IProofTreeChangedListener[] savedListeners =
			new IProofTreeChangedListener[length];
		listeners.toArray(savedListeners);
		try {
			firing = true;
			for (IProofTreeChangedListener listener: listeners) {
				notifyListener(listener, savedRoot);
			}
		} finally {
			firing = false;
		}
	}
	
	private ProofTreeDelta getDeltaForNode(IProofTreeNode node) {
		final IProofTreeNode parent = node.getParent();
		if (parent == null) {
			if (rootDelta == null)
				rootDelta = new ProofTreeDelta(node);
			return rootDelta;
		}
		final ProofTreeDelta parentDelta = getDeltaForNode(parent);
		if (parentDelta == null)
			return null;
		return parentDelta.addChild(node);
	}
	
	void childrenChanged(IProofTreeNode node) {
		ProofTreeDelta delta = getDeltaForNode(node);
		if (delta != null)
			delta.setChildrenChanged();
	}
	
	void statusChanged(IProofTreeNode node) {
		ProofTreeDelta delta = getDeltaForNode(node);
		if (delta != null)
			delta.setStatusChanged();
	}
	
	private void notifyListener(
			final IProofTreeChangedListener listener,
			final IProofTreeDelta delta) {
		
		// wrap callback with Safe runnable for subsequent listeners
		// to be called when some are causing grief
		Platform.run(new ISafeRunnable() {
			public void handleException(Throwable exception) {
				Util.log(exception, 
						"Exception within proof tree change notification"); //$NON-NLS-1$
			}
			public void run() throws Exception {
				listener.proofTreeChanged(delta);
			}
		});
	}

	public void removeChangeListener(IProofTreeChangedListener listener) {
		listeners.remove(listener);
	}
	
}
