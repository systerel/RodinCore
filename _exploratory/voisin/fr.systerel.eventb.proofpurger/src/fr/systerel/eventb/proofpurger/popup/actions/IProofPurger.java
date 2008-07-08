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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRProof;
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

	IPRProof[] computeUnusedProofs(IRodinElement[] projectsOrFiles,
			IProgressMonitor monitor) throws RodinDBException;

	void purgeUnusedProofs(IPRProof[] proofs, IProgressMonitor monitor)
			throws RodinDBException;
}
