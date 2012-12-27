/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofpurger;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * The ProofPurger provides proof purging facilities. Being given projects or
 * files, it allows to determine potentially unused proofs (some of them might
 * be intended to be used in the future). It can also perform actual deletion of
 * unused proofs.
 * 
 * @author Nicolas Beauger
 * 
 */
public interface IProofPurger {

	/**
	 * Computes an array of potentially unused proofs. Actually, it filters on
	 * proofs which have no associated PO.
	 * 
	 * @param projectsOrFiles
	 *            Selection of projects or files to be searched in. Its elements
	 *            types should be either IRodinProject or IEventBFile.
	 * @param monitor
	 *            the progress monitor to use for reporting progress to the
	 *            user. It is the caller's responsibility to call done() on the
	 *            given monitor. Accepts <code>null</code>, indicating that no
	 *            progress should be reported and that the operation cannot be
	 *            canceled.
	 * @throws RodinDBException
	 */
	void computeUnused(IRodinElement[] projectsOrFiles,
			IProgressMonitor monitor, List<IPRProof> unusedProofs,
			List<IPRRoot> unusedProofFiles) throws RodinDBException;

	/**
	 * Deletes all given unused proofs. If any of these proofs are actually
	 * used, throws IllegalArgumentException.
	 * 
	 * @param proofs
	 *            An array containing proofs to delete.
	 * @param monitor
	 *            the progress monitor to use for reporting progress to the
	 *            user. It is the caller's responsibility to call done() on the
	 *            given monitor. Accepts <code>null</code>, indicating that no
	 *            progress should be reported and that the operation cannot be
	 *            canceled.
	 * @throws IllegalArgumentException
	 * @throws RodinDBException
	 */
	void purgeUnused(List<IPRProof> proofs, List<IPRRoot> files,
			IProgressMonitor monitor) throws RodinDBException;

}
