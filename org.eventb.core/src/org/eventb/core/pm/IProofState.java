/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added more getters
 *******************************************************************************/
package org.eventb.core.pm;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeChangedListener;
import org.eventb.core.seqprover.IProofTreeNode;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for proof states that correspond to proofs of an obligation.
 * This contains the proof tree, the set of cached hypotheses, the set of
 * searched hypotheses.
 * 
 * @author htson
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IProofState extends IProofTreeChangedListener {

	/**
	 * Load the corresponding proof tree from the disk. The current proof tree
	 * will be discarded. The current post-tactic will be applied at the root of
	 * the new proof tree. The current node will be set to the first pending
	 * sub-goal or the root if there is no pending subgoals. Finally, both the
	 * cached and searched hypotheses are set to empty.
	 * <p>
	 * 
	 * @param monitor
	 *            a progress monitor
	 * @throws RodinDBException
	 *             if some problems occurred during the loading of the proof
	 *             tree.
	 */
	public abstract void loadProofTree(IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Check if the current proof is closed (there is no pending subgoal left).
	 * The current proof is closed if:
	 * <ul>
	 * <li>There is a proof tree and the proof tree is closed.
	 * <li>If there is no proof trees, the status on disk for the corresponding
	 * obligation is closed.
	 * </ul>
	 * <p>
	 * 
	 * @return <code>true</code> if the current proof is closed. Return
	 *         <code>false</code> otherwise.
	 * @throws RodinDBException
	 *             is some problems occurred.
	 */
	public abstract boolean isClosed() throws RodinDBException;

	/**
	 * Get the corresponding proof state status {@link IPSStatus}. This must
	 * not be null.
	 * <p>
	 * 
	 * @return a {@link IPSStatus}
	 */
	public abstract IPSStatus getPSStatus();

	/**
	 * Get the current proof tree.
	 * <p>
	 * 
	 * @return the current proof tree if any. Return <code>null</code>
	 *         otherwise.
	 */
	public abstract IProofTree getProofTree();

	/**
	 * Get the current proof tree node.
	 * <p>
	 * 
	 * @return the current proof tree node if any. Return <code>null</code> in
	 *         the case where there is no proof tree or no current proof tree
	 *         node.
	 */
	public abstract IProofTreeNode getCurrentNode();

	/**
	 * Set the current proof tree node. Do nothing if the new node is not
	 * contained in the current proof tree.
	 * <p>
	 * 
	 * @param newNode
	 *            a proof tree node.
	 * @throws RodinDBException
	 *             if some problems occurred.
	 */
	public abstract void setCurrentNode(IProofTreeNode newNode)
			throws RodinDBException;

	/**
	 * Get the next pending subgoal from the input node. This method does not
	 * change the current proof tree node.
	 * <p>
	 * 
	 * @param node
	 *            a proof tree node
	 * @return a next pending proof tree node if any. Return <code>null</code>
	 *         otherwise.
	 */
	public abstract IProofTreeNode getNextPendingSubgoal(IProofTreeNode node);

	/**
	 * Return an iterable on the selected hypotheses at the current proof tree
	 * node. If there is no proof tree, returns an iterable on an empty
	 * collection.
	 * 
	 * @return an iterable on a collection of selected hypotheses at the current
	 *         proof tree node
	 * @since 2.4
	 */
	public abstract Iterable<Predicate> getSelected();

	/**
	 * Filters out predicates that are not actual hypotheses of the current
	 * proof tree node. If there is no proof tree, returns an empty collection.
	 * 
	 * @param preds
	 *            a collection of predicates to filter
	 * @return a sub-collection of the given collection containing only
	 *         hypotheses at the current proof tree node
	 * @since 2.4
	 */
	public abstract Collection<Predicate> filterHypotheses(
			Collection<Predicate> preds);

	/**
	 * Add a collection of hypotheses to the cache.
	 * <p>
	 * 
	 * @param hyps
	 *            a collection of hypotheses.
	 */
	public abstract void addAllToCached(Collection<Predicate> hyps);

	/**
	 * Remove a collection of hypotheses from the cache.
	 * <p>
	 * 
	 * @param hyps
	 *            a collection of hypotheses
	 */
	public abstract void removeAllFromCached(Collection<Predicate> hyps);

	/**
	 * Return the collection of cached hypotheses.
	 * <p>
	 * 
	 * @return a collection of cached hypotheses.
	 */
	public abstract Collection<Predicate> getCached();

	/**
	 * Remove a collection of hypotheses from the set of searched hypotheses.
	 * <p>
	 * 
	 * @param hyps
	 *            a collection of hypotheses
	 */
	public abstract void removeAllFromSearched(Collection<Predicate> hyps);

	/**
	 * Return the collection of searched hypotheses.
	 * <p>
	 * 
	 * @return a collection of searched hypotheses.
	 */
	public abstract Collection<Predicate> getSearched();

	/**
	 * Set the set of searched hypotheses.
	 * <p>
	 * 
	 * @param searched
	 *            the set of new searched hypotheses.
	 */
	public abstract void setSearched(Collection<Predicate> searched);

	/**
	 * Check if the proof has some unsaved changes, i.e. the proof tree has been
	 * modified compared with the one on disk.
	 * <p>
	 * 
	 * @return <code>true</code> if the proof has some unsaved changes. Return
	 *         <code>false</code> otherwise.
	 */
	public abstract boolean isDirty();

	/**
	 * Set the current proof tree to the disk. The dirty flag is set to
	 * <code>false</code>.
	 * <p>
	 * 
	 * @param monitor
	 *            a progress monitor.
	 * @throws RodinDBException
	 *             if some problem occurred during the saving.
	 */
	public abstract void setProofTree(IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Set the dirty flag of the proof.
	 * <p>
	 * 
	 * @param dirty
	 *            <code>true</code> to indicate that there are some unsaved
	 *            changes. <code>false</code> otherwise.
	 */
	public abstract void setDirty(boolean dirty);

	/**
	 * Reuse the current proof tree with the obligation. The proof tree must exist.
	 * <p>
	 * 
	 * @param monitor
	 *            a progress monitor
	 * @throws RodinDBException
	 *             if some problems occurred
	 */
	public abstract void proofReuse(IProofMonitor monitor)
			throws RodinDBException;

	/**
	 * Rebuilt the current proof tree with the obligation. The proof tree must
	 * exist.
	 * <p>
	 * 
	 * @param monitor
	 *            a progress monitor.
	 * @throws RodinDBException
	 *             if some problems occurred.
	 */
	public abstract void proofRebuilt(IProofMonitor monitor)
			throws RodinDBException;

	/**
	 * Check if the proof state is uninitialised, i.e. the proof tree has not
	 * been loaded.
	 * <p>
	 * 
	 * @return <code>true</code> if uninitialised. Return <code>false</code>
	 *         otherwise.
	 */
	public abstract boolean isUninitialised();

	/**
	 * Check if the status of sequent on disk is discharged.
	 * <p>
	 * 
	 * @return <code>true</code> if the status sequent on disk is discharged.
	 *         Return <code>false</code> otherwise.
	 * @throws RodinDBException
	 *             if some problems occurred
	 */
	public abstract boolean isSequentDischarged() throws RodinDBException;

	/**
	 * Check if the current proof is reusable with the proof obligation.
	 * <p>
	 * 
	 * @return <code>>true</code> if the proof is reusable. Return
	 *         <code>false</code> otherwise.
	 * @throws RodinDBException
	 *             if some problems occurred.
	 */
	public abstract boolean isProofReusable() throws RodinDBException;

	/**
	 * Unload the current proof tree, i.e. the proof state will become
	 * uninitialised.
	 */
	public abstract void unloadProofTree();

	/**
	 * Backtrack from the given node. This is equivalent to pruning at the parent
	 * of the input node.
	 * <p>
	 * 
	 * @param node
	 *            a proof tree node.
	 * @param monitor
	 *            a proof monitor.
	 * @throws RodinDBException
	 *             if some problems occurred.
	 */
	public abstract void back(IProofTreeNode node,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Setting the comment at a proof tree node.
	 * <p>
	 * 
	 * @param text
	 *            the comment string.
	 * @param node
	 *            a proof tree node.
	 * @throws RodinDBException
	 *             if some problems occurred.
	 */
	public abstract void setComment(String text, IProofTreeNode node) throws RodinDBException;

}