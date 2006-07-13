/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.core.pm;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPRSequent;
import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.sequent.Hypothesis;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class keep the proof state for one proof obligation including
 *         the proof tree, the current proof node, the set of cached and
 *         searched hypotheses.
 */
public class ProofState {

	// The PR sequent associated with this proof obligation.
	private IPRSequent ps;

	// The current proof tree, this might be different from the proof tree in
	// the disk, must be not null.
	private IProofTree pt;

	// The current proof node (must be non-null).
	private IProofTreeNode current;

	// The set of cached hypotheses.
	private Collection<Hypothesis> cached;

	// The set of searched hypotheses.
	private Collection<Hypothesis> searched;

	// The dirty flag to indicate if there are some unsaved changes with this
	// proof obligation.
	private boolean dirty;

	public ProofState(IPRSequent ps) throws RodinDBException {
		this.ps = ps;
		loadProofTree();
	}

	public void loadProofTree() throws RodinDBException {
		pt = ps.makeProofTree(); // Construct the proof tree from the file.

		// Current node is the next pending subgoal or the root of the proof
		// tree if there are no pending subgoal.
		current = getNextPendingSubgoal();
		if (current == null) {
			current = pt.getRoot();
		}
		
		// if the proof tree was previously broken then the rebuild would 
		// fix the proof, making it dirty.
		dirty = ps.isProofBroken();
		cached = new HashSet<Hypothesis>();
		searched = new HashSet<Hypothesis>();
	}

	public boolean isDischarged() throws RodinDBException {
		return pt.isClosed();
	}

	public IPRSequent getPRSequent() {
		return ps;
	}

	public IProofTree getProofTree() {
		return pt;
	}

	public IProofTreeNode getCurrentNode() {
		return current;
	}

	public void setCurrentNode(IProofTreeNode newNode) {
		current = newNode;
	}

	public IProofTreeNode getNextPendingSubgoal(IProofTreeNode node) {
		IProofTreeNode subGoal = node.getFirstOpenDescendant();
		if (subGoal != null)
			return subGoal;
		return pt.getRoot().getFirstOpenDescendant();
	}

	public IProofTreeNode getNextPendingSubgoal() {
		return pt.getRoot().getFirstOpenDescendant();
	}

	public void addAllToCached(Collection<Hypothesis> hyps) {
		cached.addAll(hyps);
	}

	public void removeAllFromCached(Collection<Hypothesis> hyps) {
		cached.removeAll(hyps);
	}

	public Collection<Hypothesis> getCached() {
		return cached;
	}

	public void removeAllFromSearched(Collection<Hypothesis> hyps) {
		searched.removeAll(hyps);
	}

	public Collection<Hypothesis> getSearched() {
		return searched;
	}

	public void setSearched(Collection<Hypothesis> searched) {
		this.searched = searched;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void doSave() throws CoreException {
		ps.updateStatus(pt);
		dirty = false;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

}
