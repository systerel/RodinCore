/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 ******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.pm.IProofManager;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for manipulating proofs of an event-B component. This
 * interface provides the methods needed for accessing and changing the contents
 * of the ".bps" and ".bpr" files. The underlying implementation ensures that
 * both files are kept in sync, so that proof statuses indeed reflect the
 * current status of a proof obligation.
 * <p>
 * Instances of this interface can be obtained by using the factory method
 * {@link EventBPlugin#getPSWrapper(IRodinFile)}.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @deprecated This API is now deprecated and will be removed in a subsequent
 *             version. Please use the proof manager interface instead.
 * 
 * @see IProofManager
 * 
 * @author Thai Son Hoang
 * @author Laurent Voisin
 * @since 1.0
 */
@Deprecated
public interface IPSWrapper {

	/**
	 * Returns the proof status file encapsulated by this interface.
	 * 
	 * @return the wrapped-up PS file
	 */
	IRodinFile getPSFile();

	/**
	 * Returns the proof file encapsulated by this interface.
	 * 
	 * @return the wrapped-up PR file
	 */
	IRodinFile getPRFile();

	/**
	 * Returns the status of the proof obligation with the given name.
	 * 
	 * @param name
	 *            name of a proof obligation
	 * @return the status of the given PO in the wrapped-up PS file
	 * @throws RodinDBException
	 *             if an error occurs accessing the Rodin database
	 */
	IPSStatus getPSStatus(String name) throws RodinDBException;

	/**
	 * Returns all PS statuses of the wrapped-up PS file.
	 * 
	 * @return all PS statuses of the wrapped-up PS file
	 * @throws RodinDBException
	 *             if an error occurs accessing the Rodin database
	 */
	IPSStatus[] getPSStatuses() throws RodinDBException;

	/**
	 * Returns a fresh proof tree for the given proof obligation.
	 * 
	 * @param psStatus
	 *            handle to the proof obligation for which a new proof tree is
	 *            wanted
	 * @return a new proof tree for the given proof obligation
	 * @throws RodinDBException
	 *             if an error occurs accessing the Rodin database
	 * @see #updateStatus(IPSStatus, boolean, IProgressMonitor)
	 */
	// TODO add progress monitor as this is a long-running operation
	// TODO split into two methods: createFreshProofTree() and getProofTree()
	IProofTree getFreshProofTree(IPSStatus psStatus) throws RodinDBException;

	/**
	 * Returns the current proof skeleton of the given proof obligation.
	 * 
	 * @param psStatus
	 *            handle to the proof obligation for which a new proof tree is
	 *            wanted
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return the proof skeleton associated to the given proof obligation
	 * @throws RodinDBException
	 *             if an error occurs accessing the Rodin database
	 */
	IProofSkeleton getProofSkeleton(IPSStatus psStatus, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * @deprecated use
	 *             {@link #updateStatus(IPSStatus, boolean, IProgressMonitor)}
	 *             instead.
	 */
	@Deprecated
	void setProofTree(IPSStatus status, IProofTree pt, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Serializes the given proof tree into the corresponding {@link IPRProof}
	 * of the given status and sets the hasManualProof attribute with the given
	 * value.
	 * 
	 * @param status
	 * @param pt
	 * @param hasManualProof
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if an error occurs accessing the Rodin database
	 * @deprecated use
	 *             {@link #updateStatus(IPSStatus, boolean, IProgressMonitor)}
	 *             instead.
	 */
	@Deprecated
	void setProofTree(IPSStatus status, IProofTree pt, boolean hasManualProof,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Updates the given status and associated proof using the state of the last
	 * computed proof tree and the manual proof indicator.
	 * <p>
	 * The given status is updated using the status of the last proof tree that
	 * was returned by {@link #getFreshProofTree(IPSStatus)}. Also, the
	 * associated proof is extracted from the same tree and stored in the
	 * corresponding PR file.
	 * </p>
	 * <p>
	 * Note that the changes to the PS and PR files are not saved by this
	 * method. One needs to explicitly call
	 * {@link #save(IProgressMonitor, boolean)}.
	 * </p>
	 * 
	 * @param psStatus
	 *            proof obligation to update
	 * @param hasManualProof
	 *            <code>true</code> iff the proof tree was modified by the end
	 *            user (rather than the automated prover)
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if an error occurred accessing the Rodin database
	 */
	void updateStatus(IPSStatus psStatus, boolean hasManualProof,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Save both the PR and PS files encapsulated by this wrapper.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @param force
	 *            controls how this method deals with cases where the workbench
	 *            is not completely in sync with the local file system
	 * @throws RodinDBException
	 *             if an error occurred accessing the Rodin database
	 * @see IOpenable#save(IProgressMonitor, boolean, boolean)
	 */
	void save(IProgressMonitor monitor, boolean force) throws RodinDBException;

}
