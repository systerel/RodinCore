/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - added getFormulaFactory()
 *******************************************************************************/
package org.eventb.core.pm;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.IProofTree;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for proof attempts. A proof attempt represents a proof
 * obligation that some client is attempting to discharge. Proof attempts are
 * created from proof components with the <code>createProofAttempt</code>
 * method.
 * <p>
 * From a proof attempt, a proof tree can be obtained. Just after creation, the
 * proof tree is empty and its root is the sequent of the corresponding proof
 * obligation. Then, various tactics can be applied to this proof tree. When the
 * proof attempt is committed, the proof is extracted from the proof tree and
 * saved to the proof file. The corresponding proof status is updated
 * accordingly. A proof attempt can be committed several times.
 * </p>
 * <p>
 * If the proof obligation corresponding with a proof attempt changes during a
 * proof attempt lifetime, then the proof attempt is considered as broken.
 * However, this does not prevent the proof attempt from being committed (the
 * only impact is that the resulting proof status will be marked as broken).
 * </p>
 * <p>
 * Once a proof attempt is not needed anymore, it must be disposed so that the
 * memory used by it and its proof tree can be recovered.
 * </p>
 * <p>
 * A proof attempt can be in one of the two following state:
 * <dl>
 * <dt><em>LIVE</em></dt>
 * <dd>This is the initial state in which a proof attempt can be safely
 * manipulated and committed to the proof files</dd>
 * <dt><em>DISPOSED</em></dt>
 * <dd>This is the final state in which a proof attempt cannot be committed
 * anymore. All changes to the proof tree will thus be lost.</dd>
 * </dl>
 * A proof attempt state can only move from LIVE to DISPOSED. There is no way
 * back.
 * </p>
 * <p>
 * Within a proof component, a proof attempt is uniquely identified by the name
 * of the corresponding proof obligation and its owner.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see IProofComponent#createProofAttempt(String, String, IProgressMonitor)
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IProofAttempt {

	/**
	 * Commits this proof attempts to the proof and proof status files of this
	 * proof component. If this proof attempt is broken, then proof status after
	 * commit will also be marked as broken.
	 * <p>
	 * Note that committing a proof attempt only modifies the in-memory copy of
	 * the corresponding files. These files will then be considered as
	 * containing pending changes by the Rodin database and will need to be
	 * saved to make the change persistent. They can be saved using the
	 * <code>save</code> method of the proof component.
	 * </p>
	 * <p>
	 * This proof attempt must not have been disposed yet.
	 * </p>
	 * <p>
	 * This is a long running operation which is scheduled using the rule of the
	 * proof component of this proof attempt.
	 * </p>
	 * <p>
	 * This is equivalent to calling
	 * {@link #commit(boolean, boolean, IProgressMonitor)} with a simplify
	 * argument set to <code>false</code>.
	 * </p>
	 * 
	 * @param manual
	 *            <code>true</code> iff this proof attempt is manual
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * 
	 * @throws IllegalStateException
	 *             if this proof attempt has been disposed prior to this call
	 * @throws RodinDBException
	 *             if there is some problem committing this proof attempt to the
	 *             Rodin database
	 * 
	 * @see IProofComponent#getSchedulingRule()
	 * @see IProofComponent#save(IProgressMonitor, boolean)
	 * @see IPRProof#getHasManualProof()
	 * @see IPSStatus#isBroken()
	 */
	void commit(boolean manual, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Commits this proof attempts to the proof and proof status files of this
	 * proof component. If this proof attempt is broken, then proof status after
	 * commit will also be marked as broken.
	 * <p>
	 * Note that committing a proof attempt only modifies the in-memory copy of
	 * the corresponding files. These files will then be considered as
	 * containing pending changes by the Rodin database and will need to be
	 * saved to make the change persistent. They can be saved using the
	 * <code>save</code> method of the proof component.
	 * </p>
	 * <p>
	 * This proof attempt must not have been disposed yet.
	 * </p>
	 * <p>
	 * This is a long running operation which is scheduled using the rule of the
	 * proof component of this proof attempt.
	 * </p>
	 * 
	 * @param manual
	 *            <code>true</code> iff this proof attempt is manual
	 * @param simplify
	 *            whether to simplify the proof tree before saving it
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * 
	 * @throws IllegalStateException
	 *             if this proof attempt has been disposed prior to this call
	 * @throws RodinDBException
	 *             if there is some problem committing this proof attempt to the
	 *             Rodin database
	 * 
	 * @see IProofComponent#getSchedulingRule()
	 * @see IProofComponent#save(IProgressMonitor, boolean)
	 * @see IPRProof#getHasManualProof()
	 * @see IPSStatus#isBroken()
	 */
	void commit(boolean manual, boolean simplify, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Disposes this proof attempt. Once disposed, this proof attempt cannot be
	 * committed anymore. Disposing a proof attempt which has already been
	 * disposed has no effect.
	 */
	void dispose();

	/**
	 * Returns the proof component from which this proof attempt was created.
	 * 
	 * @return the proof component of this proof attempt
	 */
	IProofComponent getComponent();

	/**
	 * Returns the name of the proof obligation for which this proof attempt was
	 * created.
	 * 
	 * @return the name of this proof attempt
	 */
	String getName();

	/**
	 * Returns the owner of this proof attempt, i.e., the string that was passed
	 * to <code>createProofAttempt()</code> when creating this proof attempt..
	 * 
	 * @return the owner of this proof attempt
	 */
	String getOwner();

	/**
	 * Returns the formula factory for this proof attempt.
	 * 
	 * @return a formula factory
	 * @since 1.4
	 */
	FormulaFactory getFormulaFactory();

	/**
	 * Returns the proof tree of this proof attempt. The proof tree can be
	 * manipulated freely by clients. Upon creation, the proof tree only
	 * contains a root node, the sequent of which is the proof obligation of
	 * this proof attempt.
	 * 
	 * @return the proof tree of this proof attempt
	 */
	IProofTree getProofTree();

	/**
	 * Returns a handle to the proof status associated with this proof attempt.
	 * The proof status is the database element that will be updated when this
	 * proof attempt gets committed.
	 * 
	 * @return the proof status associated with this proof attempt.
	 */
	IPSStatus getStatus();

	/**
	 * Tells whether this proof attempt is still in sync with the corresponding
	 * proof obligation. This proof attempt is considered as broken if the
	 * corresponding proof obligation has changed since the creation of this
	 * proof attempt.
	 * 
	 * @return <code>true</code> iff the proof obligation has changed
	 * 
	 * @throws RodinDBException
	 *             if there is some problem accessing the proof obligation
	 *             (other than the fact that the proof obligation does not exist
	 *             anymore)
	 */
	boolean isBroken() throws RodinDBException;

	/**
	 * Tells whether this proof attempt has been disposed.
	 * 
	 * @return <code>true</code> iff this proof attempt has been disposed
	 */
	boolean isDisposed();

}
