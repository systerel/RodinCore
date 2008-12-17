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
package fr.systerel.eventb.proofpurger.popup.actions;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPORoot;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Provides proof purging facilities. Being given projects or files, it allows
 * to determine potentially unused proofs (some of them might be intended to be
 * used in the future). It can also perform actual deletion of unused proofs.
 * 
 * @author Nicolas Beauger
 * 
 */
public class ProofPurger implements IProofPurger {

	public static final boolean DEBUG = false;

	private static ProofPurger instance;

	private ProofPurger() {
		// Singleton: Private default constructor
	}

	public static IProofPurger getDefault() {
		if (instance == null)
			instance = new ProofPurger();
		return instance;
	}

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
	public void computeUnusedProofsOrFiles(IRodinElement[] projectsOrFiles,
			IProgressMonitor monitor, List<IPRProof> unusedProofs,
			List<IPRRoot> unusedProofFiles) throws RodinDBException {
		
		RodinCore.run(new UnusedComputer(projectsOrFiles, unusedProofs,
				unusedProofFiles), monitor);
	}

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
	public void purgeUnusedProofsOrFiles(List<IPRProof> proofs,
			List<IPRRoot> files, IProgressMonitor monitor)
			throws IllegalArgumentException, RodinDBException {
		
		RodinCore.run(new UnusedPurger(proofs, files), monitor);
	}

	public static void debugHook() {
		if (DEBUG) {
			for (int i = 0; i < 80000000; i++) {
				Integer g = new Integer(i);
				g = g + 1;
			}
		}
	}

	public static boolean isUsed(IPRProof pr) {
		final String name = pr.getElementName();
		final IPORoot poRoot =
				((IPRRoot) pr.getRodinFile().getRoot()).getPORoot();
		return poRoot.getSequent(name).exists();
	}

	public static boolean noProofNoPS(IPRRoot prRoot) throws RodinDBException {
		return prRoot.getProofs().length == 0
				&& !prRoot.getPSRoot().getRodinFile().exists();
	}
}
