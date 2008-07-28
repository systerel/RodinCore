/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCRefinesMachine;
import org.eventb.core.pog.ProofObligationGenerator;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineProofObligationGenerator extends ProofObligationGenerator {
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IExtractor#extract(org.eclipse.core.resources.IFile, org.rodinp.core.builder.IGraph)
	 */
	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		
		ISCMachineFile source = (ISCMachineFile) RodinCore.valueOf(file);
		IPOFile target = source.getMachineFile().getPOFile();
		
		graph.addTarget(target.getResource());
		graph.addToolDependency(
				source.getResource(), 
				target.getResource(), true);
		
		ISCRefinesMachine[] refinesMachines = source.getSCRefinesClauses();
		if (refinesMachines.length != 0) {
			graph.addUserDependency(
					source.getMachineFile().getResource(), 
					refinesMachines[0].getAbstractSCMachine().getResource(), 
					target.getResource(), false);
		}

	}

}
