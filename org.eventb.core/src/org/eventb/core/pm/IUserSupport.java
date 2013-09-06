/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.pm;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProofTreeNodeFilter;
import org.eventb.core.seqprover.ITactic;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for manipulating the state of a proof and its proof tree.
 * <p>
 * A user support instance is associated with a Proof Status file, which is set
 * with the {@link #setInput(IRodinFile)} method. This method
 * should be called only once, and prior to any other method call (except for
 * {@link #dispose()} which can be called at any time).
 * </p>
 * <p>
 * Once the input set, there is no proof obligation associated to a user support
 * instance yet. A proof obligation will get associated when one of the
 * following method is called:
 * <ul>
 * <li>{@link #setCurrentPO(IPSStatus, IProgressMonitor)}</li>
 * <li>{@link #nextUndischargedPO(boolean, IProgressMonitor)}</li>
 * <li>{@link #prevUndischargedPO(boolean, IProgressMonitor)}</li>
 * </ul>
 * For the latter two, one needs in addition that there exists an undischarged
 * proof obligation in the associated proof file.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Thai Son Hoang
 * @author Laurent Voisin
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IUserSupport extends IElementChangedListener {

	/**
	 * Sets the input file to associate with this user support.
	 * 
	 * @param psFile
	 *            a proof state file (IPSFile)
	 */
	void setInput(IRodinFile psFile);

	/**
	 * Disconnects this user support from the Rodin Database and the user
	 * support manager.
	 */
	void dispose();

	/**
	 * Returns the current input of the User Support which is a psFile.
	 * 
	 * @return the input psFile of this User Support
	 */
	IRodinFile getInput();
	
	/**
	 * Loads the proof statuses from the proof status file.
	 */
	void loadProofStates() throws RodinDBException;

	/**
	 * Go to the next undischarged proof obligation. If there is no undischarged
	 * obligation and the force flag is <code>false</code> then the User
	 * Support state is unchanged. If the <code>force</code> flag is
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
	void nextUndischargedPO(boolean force, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Go to the previous undischarged proof obligation. If there is no
	 * undischarged obligation and the force flag is <code>false</code> then
	 * the User Support state is unchanged. If the <code>force</code> flag is
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
	void prevUndischargedPO(boolean force, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * This method return the current Obligation (Proof State). This should be
	 * called at the initialization of a listener of the User Support. After
	 * that the listeners will update their states by listen to the changes from
	 * the User Support.
	 * <p>
	 * 
	 * @return the current ProofState (can be null).
	 */
	IProofState getCurrentPO();

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
	void setCurrentPO(IPSStatus psStatus, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Return all the proof obligations which are managed by this User Support.
	 * <p>
	 * 
	 * @return a collection of proof state stored in this User Support
	 */
	IProofState[] getPOs();

	/**
	 * Return <code>true</code> or <code>false</code> depending on if there
	 * are some unsaved proof obligations or not.
	 * <p>
	 * 
	 * @return <code>true</code> if there are some unsaved proof obligations
	 *         stored in this User Support
	 */
	boolean hasUnsavedChanges();

	/**
	 * Returns the formula factory to use.
	 * 
	 * @return the formula factory to use according to the input PS file
	 * @since 2.0
	 */
	FormulaFactory getFormulaFactory();

	/**
	 * Get an array of unsaved proof obligations.
	 * <p>
	 * 
	 * @return an array of unsaved proof obligations
	 */
	IProofState[] getUnsavedPOs();

	/**
	 * Remove a collection of hypotheses from the cache.
	 * <p>
	 * 
	 * @param hyps
	 *            a collection of hypotheses
	 */
	void removeCachedHypotheses(Collection<Predicate> hyps);

	/**
	 * Search the hypothesis set for a string token. In particular, if token is
	 * an empty string, all the hypotheses will be put in the set of searched
	 * hypotheses.
	 * <p>
	 * 
	 * @param token
	 *            a string token
	 */
	void searchHyps(String token);

	/**
	 * Remove a collection of predicates from the searched hypothesis set.
	 * <p>
	 * 
	 * @param hyps
	 *            a collection of predicates
	 */
	void removeSearchedHypotheses(Collection<Predicate> hyps);

	/**
	 * Select a node in the current obligation's proof tree.
	 * <p>
	 * 
	 * @param pt
	 *            a proof tree node
	 */
	void selectNode(IProofTreeNode pt);

	/**
	 * Apply a tactic to the current proof obligation to a set of hypothesis
	 * <p>
	 * 
	 * @param t
	 *            a proof tactic
	 * @param hyps
	 *            a set of hypothesis
	 * @param applyPostTactic
	 *            indicating if post tactic should be applied or not
	 * @param monitor
	 *            a progress monitor
	 */
	void applyTacticToHypotheses(ITactic t, Set<Predicate> hyps,
			boolean applyPostTactic, IProgressMonitor monitor);

	/**
	 * Apply a tactic to the current proof obligation at the current proof tree
	 * node.
	 * <p>
	 * 
	 * @param t
	 *            a proof tactic
	 * @param applyPostTactic
	 *            indicating if post tactic should be applied or not
	 * @param monitor
	 *            a progress monitor
	 */
	void applyTactic(ITactic t, boolean applyPostTactic,
			IProgressMonitor monitor);

	/**
	 * Backtracking from the current proof tree node in the current proof
	 * obligation.
	 * <p>
	 * 
	 * @param monitor
	 *            a progress monitor
	 */
	public abstract void back(IProgressMonitor monitor);

	/**
	 * Set a comment for at a proof tree node.
	 * <p>
	 * 
	 * @param text
	 *            a String (comment)
	 * @param node
	 *            a proof tree node
	 */
	void setComment(String text, IProofTreeNode node);

	/**
	 * Save the set of input proof obligations to disk.
	 * <p>
	 * 
	 * @param states
	 *            an array of proof states to be saved
	 * @param monitor
	 *            a Progress Monitor
	 * @throws RodinDBException
	 *             if some error occurred while saving
	 */
	void doSave(IProofState[] states, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * For the current proof state, select the next subgoal satisfies the
	 * filter. The flag <code>rootIncluded</code> to indicate that if the
	 * current node should be considered or not. If there is no such subgoal, do
	 * nothing.
	 * <p>
	 * 
	 * @param rootIncluded
	 *            to include the current node or not
	 * @param filter
	 *            a proof tree node filter
	 * @return <code>true</code> if there is such a node satisfies the filter.
	 *         Return <code>false</code> otherwise.
	 */
	boolean selectNextSubgoal(boolean rootIncluded, IProofTreeNodeFilter filter);

}