/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core.seqprover;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eventb.core.seqprover.IProofTreeChangedListener;
import org.eventb.core.seqprover.IProofTreeDelta;
import org.eventb.core.seqprover.IProofTreeNode;

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

	// Only fire delta if this flag is set
	boolean fireEnable;
	
	/**
	 * Creates a new delta processor for the given proof tree.
	 */
	public DeltaProcessor(ProofTree tree) {
		this.tree = tree;
		this.rootDelta = null;
		this.listeners = new ArrayList<IProofTreeChangedListener>();
		this.firing = false;
		this.fireEnable = true;
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
		
		if (!fireEnable) {
			// Don't fire deltas if the flag is not set
			return;
		}
		// Save the delta tree
		final ProofTreeDelta savedRoot = rootDelta;
		rootDelta = null;
		
		final int length = listeners.size();
		if (length == 0) {
			// No listeners
			return;
		}
		// Save the list of listeners to prevent concurrent modification
		final IProofTreeChangedListener[] savedListeners =
			listeners.toArray(new IProofTreeChangedListener[length]);
		try {
			firing = true;
			for (IProofTreeChangedListener listener: savedListeners) {
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
	
	void ruleChanged(IProofTreeNode node) {
		ProofTreeDelta delta = getDeltaForNode(node);
		if (delta != null)
			delta.setRuleChanged();
	}
	
	void childrenChanged(IProofTreeNode node) {
		ProofTreeDelta delta = getDeltaForNode(node);
		if (delta != null)
			delta.setChildrenChanged();
	}
	
	void confidenceChanged(IProofTreeNode node) {
		ProofTreeDelta delta = getDeltaForNode(node);
		if (delta != null)
			delta.setConfidenceChanged();
	}
	
	void commentChanged(IProofTreeNode node) {
		ProofTreeDelta delta = getDeltaForNode(node);
		if (delta != null)
			delta.setCommentChanged();
	}
	
	private void notifyListener(
			final IProofTreeChangedListener listener,
			final IProofTreeDelta delta) {
		
		// wrap callback with Safe runnable for subsequent listeners
		// to be called when some are causing grief
		SafeRunner.run(new ISafeRunnable() {
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
	
	public void setEnable(boolean fireEnable) {
		this.fireEnable = fireEnable;
	}

	public boolean isEnable() {
		return fireEnable;
	}

}
