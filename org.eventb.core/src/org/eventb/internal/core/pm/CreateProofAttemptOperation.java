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
package org.eventb.internal.core.pm;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

class CreateProofAttemptOperation implements IWorkspaceRunnable {

	private final ProofComponent pc;
	private final String poName;
	private final String owner;

	private ProofAttempt pa;

	CreateProofAttemptOperation(ProofComponent pc, String poName, String owner) {
		this.pc = pc;
		this.poName = poName;
		this.owner = owner;
	}

	public ProofAttempt getResult() {
		return pa;
	}

	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		pa = pc.get(poName, owner);
		if (pa != null) {
			throw new IllegalStateException(pa + " already exists");
		}
		pa = new ProofAttempt(pc, poName, owner);
		pc.put(pa);
	}

}