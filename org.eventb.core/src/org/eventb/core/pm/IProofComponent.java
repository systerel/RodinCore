/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.pm;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.IProofSkeleton;
import org.rodinp.core.IOpenable;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for accessing to the proofs of an event-B component. This
 * interface provides a unique facade for manipulating proofs in a consistent
 * manner.
 * <p>
 * Proof components are created by calling the <code>getProofComponent</code>
 * method of the proof manager. From a proof component, one can create a new
 * proof attempt for a proof obligation.
 * </p>
 * <p>
 * The proof related data of an event-B component are stored in three different
 * files:
 * <ul>
 * <li>the PO file contains the lemmas of the proof obligations generated for
 * the component;</li>
 * <li>the PR file contains the proofs created by the user or the auto-prover;</li>
 * <li>the PS file contains the statuses of the proof obligations, i.e.,
 * whether the proofs stored in the PR file do discharge the lemmas contained in
 * the PO file.</li>
 * </ul>
 * </p>
 * <p>
 * This class provides utility method that allow to manipulate both the proof
 * file and the proof status file of a proof component together.
 * </p>
 * <p>
 * All interactions with the Rodin database are done with a scheduling rule
 * locking these three Rodin files.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 * 
 * @see IProofManager#getProofComponent(IEventBRoot)
 * @see IProofAttempt
 * @since 1.0
 */
public interface IProofComponent {

	/**
	 * Creates a new proof attempt for the given proof obligation and owner.
	 * <p>
	 * This is a long running operation which is scheduled using the rule of
	 * this proof component.
	 * </p>
	 * 
	 * @param poName
	 *            name of the proof obligation to load for the proof attempt
	 * @param owner
	 *            name of the owner of the proof attempt to create
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @return a new proof attempt
	 * @throws IllegalStateException
	 *             if a proof attempt with the same characteristics has already
	 *             been created and not disposed since
	 * @throws RodinDBException
	 *             if there is some problem loading the corresponding proof
	 *             obligation
	 * @see #getSchedulingRule()
	 */
	IProofAttempt createProofAttempt(String poName, String owner,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns the Rodin root associated with this proof component and
	 * containing its proof obligations.
	 * 
	 * @return the proof obligation root of this component.
	 */
	IPORoot getPORoot();

	/**
	 * Returns the Rodin root associated with this proof component and
	 * containing its proofs.
	 * 
	 * @return the proof root of this component.
	 */
	IPRRoot getPRRoot();

	/**
	 * Returns an array of all proof attempts that have been created for this
	 * component and not disposed so far.
	 * 
	 * @return an array of all live proof attempts of this component
	 */
	IProofAttempt[] getProofAttempts();

	/**
	 * Returns an array of all proof attempts that have been created for the
	 * given proof obligation and not disposed so far. No attempt is made to
	 * check whether the given proof obligation actually exists.
	 * 
	 * @param poName
	 *            name of a proof obligation of this component
	 * 
	 * @return an array of all live proof attempts for the given proof
	 *         obligation
	 */
	IProofAttempt[] getProofAttempts(String poName);

	/**
	 * Returns the proof corresponding to the given proof obligation as a proof
	 * skeleton. The result is read from the proof file of this component.
	 * <p>
	 * This is a long running operation which is scheduled using the rule of
	 * this proof component.
	 * </p>
	 * 
	 * @param poName
	 *            name of a proof obligation
	 * @param factory
	 *            the formula factory to use for building the proof skeleton
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @return the proof skeleton for the given proof obligation
	 * @throws RodinDBException
	 *             if there is some problem loading the corresponding proof
	 * @see #getSchedulingRule()
	 */
	IProofSkeleton getProofSkeleton(String poName, FormulaFactory factory,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns the Rodin root associated with this proof component and
	 * containing its proof statuses.
	 * 
	 * @return the proof status root of this component.
	 */
	IPSRoot getPSRoot();

	/**
	 * Returns the scheduling rule used by this component for interacting with
	 * the Rodin database.
	 * 
	 * @return the scheduling rule for this component
	 */
	ISchedulingRule getSchedulingRule();

	/**
	 * Returns a handle to the proof status for the proof obligation with the
	 * given name.
	 * <p>
	 * This is a handle-only method. The status element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param poName
	 *            name of a proof obligation
	 * @return the proof status for the given proof obligation
	 */
	IPSStatus getStatus(String poName);

	/**
	 * Tells whether the proof file or the proof status file of this component
	 * has unsaved changes.
	 * 
	 * @exception RodinDBException
	 *                if one of the proof file or the proof status file does not
	 *                exist or if an exception occurs while accessing their
	 *                corresponding resources.
	 * @return <code>true</code> if the proof file or the proof status file of
	 *         this component has unsaved changes.
	 * 
	 * @see IOpenable#hasUnsavedChanges()
	 */
	boolean hasUnsavedChanges() throws RodinDBException;

	/**
	 * Makes the proof file and the proof status file of this component
	 * consistent with their underlying resources.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @exception RodinDBException
	 *                if one of the proof file or proof status file is unable to
	 *                access the contents of its underlying resource. Reasons
	 *                include:
	 *                <ul>
	 *                <li>This Rodin element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                </ul>
	 * @see IOpenable#makeConsistent(IProgressMonitor)
	 */
	void makeConsistent(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Save both the proof and proof status file of this proof component. The
	 * parameters of this method are exactly the same as those of the
	 * {@link IOpenable#save(IProgressMonitor, boolean)} method.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @param force
	 *            it controls how this method deals with cases where the
	 *            workbench is not completely in sync with the local file system
	 * @exception RodinDBException
	 *                if an error occurs accessing the contents of the
	 *                underlying resource of the proof or proof status file.
	 *                Reasons include:
	 *                <ul>
	 *                <li>This Rodin element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li>This Rodin element is read-only (READ_ONLY)</li>
	 *                </ul>
	 * 
	 * @see IOpenable#save(IProgressMonitor, boolean)
	 */
	void save(IProgressMonitor monitor, boolean force) throws RodinDBException;

}
