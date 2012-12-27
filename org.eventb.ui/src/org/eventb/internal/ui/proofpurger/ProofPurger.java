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

	@Override
	public void computeUnused(IRodinElement[] projectsOrFiles,
			IProgressMonitor monitor, List<IPRProof> unusedProofs,
			List<IPRRoot> unusedFiles) throws RodinDBException {

		final UnusedComputer action =
				new UnusedComputer(projectsOrFiles, unusedProofs,
						unusedFiles);
		RodinCore.run(action, monitor);
	}

	@Override
	public void purgeUnused(List<IPRProof> proofs,
			List<IPRRoot> files, IProgressMonitor monitor)
			throws IllegalArgumentException, RodinDBException {

		final UnusedPurger action =
				new UnusedPurger(proofs, files);
		RodinCore.run(action, monitor);
	}

	public static boolean isToPurge(IPRProof proof) {
		final String name = proof.getElementName();
		final IPORoot poRoot =
				((IPRRoot) proof.getRodinFile().getRoot()).getPORoot();
		return !poRoot.getSequent(name).exists();
	}
	
	public static void debugHook() {
		if (DEBUG) {
			for (int i = 0; i < 80000000; i++) {
				Integer g = new Integer(i);
				g = g + 1;
			}
		}
	}

	public static boolean noProofNoPS(IPRRoot prRoot) throws RodinDBException {
		return prRoot.getProofs().length == 0
				&& !prRoot.getPSRoot().getRodinFile().exists();
	}
}
