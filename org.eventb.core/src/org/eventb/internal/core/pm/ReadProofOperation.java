/*******************************************************************************
 * Copyright (c) 2008, 2014 Systerel and others.
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
import org.eclipse.core.runtime.SubMonitor;
import org.eventb.core.IPRProof;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.ProverLib;

/**
 * Implements reading of a proof from the proof file. Instances must be run
 * while locking all files of the corresponding proof component.
 * 
 * @author Laurent Voisin
 */
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
		final SubMonitor sm = SubMonitor.convert(monitor, 100);
		final IPRProof proof = pc.getProof(poName);
		FormulaFactory prFac;
		try {
			prFac = proof.getFormulaFactory(sm.newChild(10));
		} catch (CoreException e) {
			// exception already logged
			prFac = pc.getFormulaFactory();
		}
		
		IProofSkeleton skeleton = proof.getSkeleton(prFac, sm.newChild(90));
		if (ff != prFac) {
			skeleton = ProverLib.translate(skeleton, ff);
		}
		result = skeleton;
	}

}
