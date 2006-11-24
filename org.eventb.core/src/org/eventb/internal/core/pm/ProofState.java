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

package org.eventb.internal.core.pm;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pm.IProofState;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.proofBuilder.IProofSkeleton;
import org.eventb.core.seqprover.proofBuilder.ProofBuilder;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.pom.AutoPOM;
import org.eventb.internal.core.pom.POLoader;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class keep the proof state for one proof obligation including
 *         the proof tree, the current proof node, the set of cached and
 *         searched hypotheses.
 */
public class ProofState implements IProofState {

	// The PR sequent associated with this proof obligation.
	IPSStatus status;

	// The current proof tree, this might be different from the proof tree in
	// the disk, can be null when it is not initialised.
	IProofTree pt;

	// The current proof node, can be null when the proof tree is uninitialised.
	private IProofTreeNode current;

	// The set of cached hypotheses.
	private Collection<Hypothesis> cached;

	// The set of searched hypotheses.
	private Collection<Hypothesis> searched;

	// The dirty flag to indicate if there are some unsaved changes with this
	// proof obligation.
	private boolean dirty;

	public ProofState(IPSStatus ps) {
		this.status = ps;
		// loadProofTree();
	}

	/*
	 * Creates the initial proof tree for this proof obligation. 
	 */
	private IProofTree createProofTree() throws RodinDBException {
		final IPOSequent poSequent = status.getPOSequent();
		IProverSequent newSeq = POLoader.readPO(poSequent);
		return ProverFactory.makeProofTree(newSeq, poSequent);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#loadProofTree(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void loadProofTree(IProgressMonitor monitor) throws RodinDBException {

		// Construct the proof tree from the PO file.
		pt = createProofTree();
		
		// If a proof exists in the PR file rebuild it.
		final IPRProof prProof = status.getProof();
		if (prProof.exists())
		{
			final IProofSkeleton proofSkeleton = prProof.getSkeleton(FormulaFactory.getDefault(), monitor);
			if (proofSkeleton != null){
				ProofBuilder.rebuild(pt.getRoot(),proofSkeleton);
			}
		}
		
		// Current node is the next pending subgoal or the root of the proof
		// tree if there are no pending subgoal.
		current = getNextPendingSubgoal();
		if (current == null) {
			current = pt.getRoot();
		}

		// if the proof tree was previously broken then the rebuild would
		// fix the proof, making it dirty.
		dirty = status.isBroken();
		cached = new HashSet<Hypothesis>();
		searched = new HashSet<Hypothesis>();
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#isClosed()
	 */
	public boolean isClosed() throws RodinDBException {
		if (pt != null)
			return pt.isClosed();
		
		final IPRProof prProof = status.getProof();
		return (prProof.exists() && (prProof.getConfidence() > IConfidence.PENDING));
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#getPRSequent()
	 */
	public IPSStatus getPRSequent() {
		return status;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#getProofTree()
	 */
	public IProofTree getProofTree() {
		return pt;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#getCurrentNode()
	 */
	public IProofTreeNode getCurrentNode() {
		return current;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#setCurrentNode(org.eventb.core.seqprover.IProofTreeNode)
	 */
	public void setCurrentNode(IProofTreeNode newNode) {
		current = newNode;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#getNextPendingSubgoal(org.eventb.core.seqprover.IProofTreeNode)
	 */
	public IProofTreeNode getNextPendingSubgoal(IProofTreeNode node) {
		IProofTreeNode subGoal = node.getFirstOpenDescendant();
		if (subGoal != null)
			return subGoal;
		return pt.getRoot().getFirstOpenDescendant();
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#getNextPendingSubgoal()
	 */
	public IProofTreeNode getNextPendingSubgoal() {
		return pt.getRoot().getFirstOpenDescendant();
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#addAllToCached(java.util.Collection)
	 */
	public void addAllToCached(Collection<Hypothesis> hyps) {
		cached.addAll(hyps);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#removeAllFromCached(java.util.Collection)
	 */
	public void removeAllFromCached(Collection<Hypothesis> hyps) {
		cached.removeAll(hyps);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#getCached()
	 */
	public Collection<Hypothesis> getCached() {
		return cached;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#removeAllFromSearched(java.util.Collection)
	 */
	public void removeAllFromSearched(Collection<Hypothesis> hyps) {
		searched.removeAll(hyps);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#getSearched()
	 */
	public Collection<Hypothesis> getSearched() {
		return searched;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#setSearched(java.util.Collection)
	 */
	public void setSearched(Collection<Hypothesis> searched) {
		this.searched = searched;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#isDirty()
	 */
	public boolean isDirty() {
		return dirty;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) throws CoreException {
		UserSupportUtils.debug("Saving: " + status.getElementName());
		
		// TODO add lock for po and pr file
		
		RodinCore.run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor mon) throws CoreException {
				try
				{
					mon.beginTask("Saving Proof", 2);
					status.getProof().setProofTree(pt, new SubProgressMonitor(mon,1));
					AutoPOM.updateStatus(
							((IPSStatus) status.getMutableCopy()),
							new SubProgressMonitor(mon,1));
				}
				finally 
				{
					mon.done();
				}
			}
		}, status.getSchedulingRule() , monitor);	
		
		dirty = false;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#setDirty(boolean)
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ProofState))
			return false;
		else {
			IProofState proofState = (IProofState) obj;
			return proofState.getPRSequent().equals(status);
		}

	}

	// Pre: Must be initalised and not currently saving.
	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#proofReuse(org.eventb.core.seqprover.IProofMonitor)
	 */
	public void proofReuse(IProofMonitor monitor) throws RodinDBException {
		// if (isSavingOrUninitialised()) return false;
		// if (pt == null) return false; // No proof tree, no reusable.
		
		IProofTree newTree = createProofTree();
		IProverSequent newSeq = newTree.getSequent();
		if (ProverLib.proofReusable(pt.getProofDependencies(), newSeq)) {
			(BasicTactics.pasteTac(pt.getRoot())).apply(newTree.getRoot(), monitor);
			pt = newTree;
			current = getNextPendingSubgoal();
			if (current == null) {
				current = pt.getRoot();
			}
			dirty = true;
			return;
		}
		// If NOT, then mark the Proof State as dirty. Send delta to the
		// user
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#isUninitialised()
	 */
	public boolean isUninitialised() {
		return (pt == null);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#isSequentDischarged()
	 */
	public boolean isSequentDischarged() throws RodinDBException {
		final IPRProof prProof = status.getProof();
		return (prProof.exists() && (prProof.getConfidence() > IConfidence.PENDING));
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#isProofReusable()
	 */
	public boolean isProofReusable() throws RodinDBException {
		IProverSequent seq = POLoader.readPO(status.getPOSequent());
		return ProverLib.proofReusable(pt.getProofDependencies(), seq);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#reloadProofTree()
	 */
	public void reloadProofTree() throws RodinDBException {
		
		// Construct the proof tree from the file.
		pt = createProofTree();

		// Current node is the next pending subgoal or the root of the proof
		// tree if there are no pending subgoal.
		current = getNextPendingSubgoal();
		if (current == null) {
			current = pt.getRoot();
		}

		// if the proof tree was previously broken then the rebuild would
		// fix the proof, making it dirty.
		dirty = status.isBroken();
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#unloadProofTree()
	 */
	public void unloadProofTree() {
		pt = null;
		current = null;
	}

}
