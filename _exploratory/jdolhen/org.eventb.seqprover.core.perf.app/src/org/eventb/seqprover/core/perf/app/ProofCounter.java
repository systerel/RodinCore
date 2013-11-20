/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.seqprover.core.perf.app;

import static org.eventb.core.seqprover.IConfidence.DISCHARGED_MAX;

import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of counting of discharged proofs.
 * 
 * @author Laurent Voisin
 */
public class ProofCounter {

	private final IRodinProject rodinProject;

	private int nbProofs;
	private int nbDischarged;

	public ProofCounter(IRodinProject rodinProject) {
		super();
		this.rodinProject = rodinProject;
	}

	/**
	 * Counts the total number of proofs and the number of discharged ones.
	 * 
	 * @throws RodinDBException
	 */
	public void count() throws RodinDBException {
		nbProofs = 0;
		nbDischarged = 0;
		for (final IPSRoot psRoot : getPSRoots()) {
			countRoot(psRoot);
		}
	}

	private IPSRoot[] getPSRoots() throws RodinDBException {
		return rodinProject.getRootElementsOfType(IPSRoot.ELEMENT_TYPE);
	}

	private void countRoot(final IPSRoot psRoot) throws RodinDBException {
		final IPSStatus[] statuses = psRoot.getStatuses();
		nbProofs += statuses.length;
		for (final IPSStatus status : statuses) {
			if (isDischarged(status)) {
				nbDischarged++;
			}
		}
	}

	private static boolean isDischarged(final IPSStatus status)
			throws RodinDBException {
		return !status.isBroken() && status.getConfidence() == DISCHARGED_MAX;
	}

	public int getNbProofs() {
		return nbProofs;
	}

	public int getNbDischarged() {
		return nbDischarged;
	}

}
