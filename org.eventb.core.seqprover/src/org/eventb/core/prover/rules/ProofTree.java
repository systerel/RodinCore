/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.prover.rules;

import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.IProofTreeChangedListener;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

/**
 * Implementation of a proof tree, with observer design pattern.
 * 
 * @author Laurent Voisin
 */
public final class ProofTree implements IProofTree {

	// Delta processor for this tree
	final DeltaProcessor deltaProcessor;
	
	// Root node
	final ProofTreeNode root;

	/**
	 * Creates a new proof tree for the given sequent.
	 * 
	 * Clients must not call this constructor, but rather the factory method in
	 * {@link org.eventb.core.prover.SequentProver}.
	 */
	public ProofTree(IProverSequent sequent) {
		root = new ProofTreeNode(this, sequent);
		deltaProcessor = new DeltaProcessor(this);
	}
	
	/**
	 * Creates a new proof tree for the given (disconnected) IProofTreeNode.
	 * 
	 * Clients must not call this constructor, but rather the factory method in
	 * {@link org.eventb.core.prover.SequentProver}.
	 */
	protected ProofTree(ProofTreeNode node) {
		node.setProofTree(this);
		root = node;
		deltaProcessor = new DeltaProcessor(this);
	}

	public void addChangeListener(IProofTreeChangedListener listener) {
		deltaProcessor.addChangeListener(listener);
	}

	public ProofTreeNode getRoot() {
		return root;
	}

	public IProverSequent getSequent() {
		return getRoot().getSequent();
	}

	public boolean isClosed() {
		return getRoot().isClosed();
	}

	public void removeChangeListener(IProofTreeChangedListener listener) {
		deltaProcessor.removeChangeListener(listener);
	}

	public Set<Hypothesis> getUsedHypotheses() {
		return root.getUsedHypotheses();
	}

	public Set<FreeIdentifier> getUsedFreeIdents() {
		return root.getUsedFreeIdents();
	}

	public int getConfidence() {
		return getRoot().getConfidence();
	}

}