/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeChangedListener;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This is the interface for the user support which used to manage the
 *         state of the proof obligation corresponding to a single component.
 *         <p>
 *         This interface is not supposed to be extended or implemented.
 */
public interface IUserSupport extends IElementChangedListener,
		IProofTreeChangedListener {

	/**
	 * Add a listener to the changes of the proof states stored in this user
	 * support.
	 * <p>
	 * 
	 * @param listener
	 *            a proof state change listener
	 */
	public abstract void addStateChangedListeners(
			IProofStateChangedListener listener);

	/**
	 * Remove a listener to the changes of the proof states stored in this user
	 * support.
	 * <p>
	 * 
	 * @param listener
	 *            a proof state change listener
	 */
	public abstract void removeStateChangedListeners(
			IProofStateChangedListener listener);

	/**
	 * Run the operation as a batch operation hence there is only one delta is
	 * fired after running the operation.
	 * <p>
	 * 
	 * @param op
	 *            a runnable operation to be executed
	 */
	public abstract void batchOperation(Runnable op);

	/**
	 * This method return the current Obligation (Proof State). This should be
	 * called at the initialisation of a listener of the User Support. After
	 * that the listeners will update their states by listen to the changes from
	 * the User Support.
	 * <p>
	 * 
	 * @return the current ProofState (can be null).
	 */
	public abstract IProofState getCurrentPO();

	/**
	 * Set the input file for the user support. This should be called only by
	 * the User Support Manager.
	 * <p>
	 * 
	 * @param psFile
	 *            a proof State file (IPSFile)
	 * @param monitor
	 *            a progress monitor
	 * @throws RodinDBException
	 *             a Rodin Exception when there are some errors openning the
	 *             psFile
	 */
	public abstract void setInput(IPSFile psFile, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Set the current PO corresponding to a specific proof obligation. The
	 * proof obligation is represented by a psStatus. If the psStatus is null
	 * then the current proof obligation is set to null. Otherwise, if there is
	 * no proof obligation found then the status of the User Support is
	 * unchanged.
	 * <p>
	 * 
	 * @param psStatus
	 *            a psStatus within the file
	 * @param monitor
	 *            a progress monitor
	 * 
	 * @throws RodinDBException
	 *             a Rodin Exception
	 */
	public abstract void setCurrentPO(IPSStatus psStatus,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Go to the next undischarged proof obligation. If there is no undischarged
	 * obligation and the force flag is <code>false</code> then the User
	 * Support state is unchaged. If the force flag is <code>true</code> and
	 * there is no undischarged obligation then the current PO will be set to
	 * <code>null</code>.
	 * <p>
	 * 
	 * @param force
	 *            a boolean flag
	 * @param monitor
	 *            a progress monitor
	 * @throws RodinDBException
	 *             a Rodin Exception
	 */
	public abstract void nextUndischargedPO(boolean force,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Go to the previous undischarged proof obligation. If there is no
	 * undischarged obligation and the force flag is <code>false</code> then
	 * the User Support state is unchaged. If the force flag is
	 * <code>true</code> and there is no undischarged obligation then the
	 * current PO will be set to <code>null</code>.
	 * <p>
	 * 
	 * @param force
	 *            a boolean flag
	 * @param monitor
	 *            a progress monitor
	 * @throws RodinDBException
	 *             a Rodin Exception
	 */
	public abstract void prevUndischargedPO(boolean force,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Select a node in the current obligation's proof tree.
	 * <p>
	 * 
	 * @param pt
	 *            a proof tree node
	 */
	public abstract void selectNode(IProofTreeNode pt);

	/**
	 * Apply a tactic to the current proof obligation to a set of hypothesis
	 * <p>
	 * 
	 * @param t
	 *            a proof tactic
	 * @param hyps
	 *            a set of hypothesis
	 * @param monitor
	 *            a progress monitor
	 */
	public abstract void applyTacticToHypotheses(final ITactic t,
			final Set<Predicate> hyps, final IProgressMonitor monitor);

	/**
	 * Apply a tactic to the current proof obligation
	 * <p>
	 * 
	 * @param t
	 *            a proof tactic
	 * @param monitor
	 *            a progress monitor
	 */
	public abstract void applyTactic(final ITactic t,
			final IProgressMonitor monitor);

	/**
	 * Applying prune at the current proof tree node of the current proof
	 * obligation
	 * <p>
	 * 
	 * @param monitor
	 *            a progress monitor
	 * @throws RodinDBException
	 *             a Rodin Exception
	 */
	public abstract void prune(final IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Remove a collection of hypothesis from the cache.
	 * <p>
	 * 
	 * @param hyps
	 *            a collection of hypotheses
	 */
	public abstract void removeCachedHypotheses(Collection<Predicate> hyps);

	/**
	 * Remove a collection of hypothesis from the searched hypothesis set
	 * <p>
	 * 
	 * @param hyps
	 *            a collection of hypotheses
	 */
	public abstract void removeSearchedHypotheses(Collection<Predicate> hyps);

	/**
	 * Search the hypothesis set for a string token. In particular, if token is
	 * an empty string, all the hypotheses will be put in the set of searched
	 * hypotheses.
	 * <p>
	 * 
	 * @param token
	 *            a string token
	 */
	public abstract void searchHyps(String token);

	/**
	 * Backtracking from the current proof tree node in the current proof
	 * obligation.
	 * <p>
	 * 
	 * @param monitor
	 *            a progress monitor
	 * @throws RodinDBException
	 *             a Rodin Exception
	 */
	public abstract void back(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Return <code>true</code> or <code>false</code> depending on if there
	 * are some unsaved proof obligations or not.
	 * <p>
	 * 
	 * @return <code>true</code> if there are some unsaved proof obligations
	 *         stored in this User Support
	 */
	public abstract boolean hasUnsavedChanges();

	/**
	 * Get an array of unsaved proof obligations.
	 * <p>
	 * 
	 * @return an array of unsaved proof obligations
	 */
	public abstract IProofState[] getUnsavedPOs();

	/**
	 * Set a comment for at a proof tree node.
	 * <p>
	 * 
	 * @param text
	 *            a Sring (comment)
	 * @param node
	 *            a proof tree node
	 */
	public abstract void setComment(String text, IProofTreeNode node);

	/**
	 * Return all the proof obligations which are managed by this User Support.
	 * <p>
	 * 
	 * @return a collection of proof state stored in this User Support
	 */
	public abstract Collection<IProofState> getPOs();

	/**
	 * Return the current input of the User Support which is a psFile.
	 * <p>
	 * 
	 * @return the input psFile of this User Support
	 */
	public abstract IPSFile getInput();

	/**
	 * Dispose the user support. Should be used only by the User Support
	 * Manager.
	 */
	public abstract void dispose();

}