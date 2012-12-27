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
package org.eventb.internal.core.pm;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.IProofSkeleton;

class ReadProofOperation implements IWorkspaceRunnable {

	private final ProofComponent pc;
	private final String poName;
	private final FormulaFactory ff;

	private IProofSkeleton result;

	ReadProofOperation(ProofComponent pc, String poName, FormulaFactory ff) {
		this.pc = pc;
		this.poName = poName;
		this.ff = ff;
	}

	public IProofSkeleton getResult() {
		return result;
	}

	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		result = pc.getProof(poName).getSkeleton(ff, monitor);
	}

}