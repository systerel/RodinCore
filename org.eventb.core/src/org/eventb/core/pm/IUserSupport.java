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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This is the interface for the user support which used to manage the
 *         state of the proof obligations corresponding to a single component.
 *         <p>
 *         This interface is not supposed to be extended or implemented.
 */
public interface IUserSupport extends IElementChangedListener {

	/**
	 * Set the input file for the user support.
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
	 * Dispose the user support.
	 */
	public abstract void dispose();

	/**
	 * Return the current input of the User Support which is a psFile.
	 * <p>
	 * 
	 * @return the input psFile of this User Support
	 */
	public abstract IPSFile getInput();

	/**
	 * Go to the next undischarged proof obligation. If there is no undischarged
	 * obligation and the force flag is <code>false</code> then the User
	 * Support state is unchaged. If the <code>force</code> flag is
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
	public abstract void nextUndischargedPO(boolean force,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Go to the previous undischarged proof obligation. If there is no
	 * undischarged obligation and the force flag is <code>false</code> then
	 * the User Support state is unchaged. If the <code>force</code> flag is
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
	public abstract void setCurrentPO(final IPSStatus psStatus,
			final IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Return all the proof obligations which are managed by this User Support.
	 * <p>
	 * 
	 * @return a collection of proof state stored in this User Support
	 */
	public abstract IProofState[] getPOs();

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
	public IProofState[] getUnsavedPOs();

	/**
	 * Getting the list of information stored in the user support (including the
	 * output of the applied tactics, messages about the last action in the user
	 * support, etc.)
	 * 
	 * @return a list of objects
	 */
	public abstract Object[] getInformation();

	/**
	 * Remove a collection of hypotheses from the cache.
	 * <p>
	 * 
	 * @param hyps
	 *            a collection of hypotheses
	 */
	public abstract void removeCachedHypotheses(Collection<Predicate> hyps);

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
	 * Remove a collection of predicates from the searched hypothesis set.
	 * <p>
	 * 
	 * @param hyps
	 *            a collection of predicates
	 */
	public abstract void removeSearchedHypotheses(Collection<Predicate> hyps);

	/**
	 * Select a node in the current obligation's proof tree.
	 * <p>
	 * 
	 * @param pt
	 *            a proof tree node
	 * @throws RodinDBException
	 */
	public abstract void selectNode(IProofTreeNode pt) throws RodinDBException;

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
	 * @throws RodinDBException
	 */
	public abstract void applyTacticToHypotheses(final ITactic t,
			final Set<Predicate> hyps, final IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Apply a tactic to the current proof obligation at the current proof tree
	 * node.
	 * <p>
	 * 
	 * @param t
	 *            a proof tactic
	 * @param monitor
	 *            a progress monitor
	 * @throws RodinDBException
	 */
	public abstract void applyTactic(final ITactic t,
			final IProgressMonitor monitor) throws RodinDBException;

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
	 * Set a comment for at a proof tree node.
	 * <p>
	 * 
	 * @param text
	 *            a Sring (comment)
	 * @param node
	 *            a proof tree node
	 * @throws RodinDBException
	 */
	public abstract void setComment(String text, IProofTreeNode node)
			throws RodinDBException;

	/**
	 * Save the set of inputproof obligations to disk.
	 * <p>
	 * @param states
	 *        an array of proof states to be saved
	 * @param monitor
	 *        a Progress Monitor
	 * @throws CoreException
	 *        a Core Exception if some errors occured while saving
	 */
	public abstract void doSave(Object[] states, IProgressMonitor monitor)
			throws CoreException;

}