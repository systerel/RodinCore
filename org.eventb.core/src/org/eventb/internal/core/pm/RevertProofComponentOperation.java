/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.pm;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * Implements reverting of a proof file. Instances must be run while locking all
 * files of the corresponding proof component.
 * 
 * @author Laurent Voisin
 */
class RevertProofComponentOperation implements IWorkspaceRunnable {

	private final ProofComponent pc;

	RevertProofComponentOperation(ProofComponent pc) {
		this.pc = pc;
	}

	@Override
	public void run(IProgressMonitor pm) throws CoreException {
		try {
			pm.beginTask("Reverting proof files", 2);
			pc.getPRRoot().getRodinFile().makeConsistent(new SubProgressMonitor(pm, 1));
			pc.getPSRoot().getRodinFile().makeConsistent(new SubProgressMonitor(pm, 1));
		} finally {
			pm.done();
		}
	}

}