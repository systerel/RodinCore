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

import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.IProofTreeChangedListener;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.sequent.IProverSequent;

/**
 * Implementation of a proof tree.
 * 
 * @author Laurent Voisin
 */
public final class ProofTree implements IProofTree {

	ProofTreeNode root;

	List<IProofTreeChangedListener> listeners;

	/**
	 * Creates a new proof tree for the given sequent.
	 * 
	 * Clients must not call this constructor, but rather the factory method in
	 * {@link org.eventb.core.prover.SequentProver}.
	 */
	public ProofTree(IProverSequent sequent) {
		root = new ProofTreeNode(this, sequent);
		listeners = new ArrayList<IProofTreeChangedListener>();
	}

	public IProverSequent getSequent() {
		return getRoot().getSequent();
	}

	public boolean isDischarged() {
		return getRoot().isDischarged();
	}

	public IProofTreeNode getRoot() {
		return root;
	}

	public void addChangedListener(IProofTreeChangedListener listener) {
		if (listeners.contains(listener)) {
			return;
		}
		listeners.add(listener);
	}

	public void removeChangedListener(IProofTreeChangedListener listener) {
		listeners.remove(listener);
	}

}