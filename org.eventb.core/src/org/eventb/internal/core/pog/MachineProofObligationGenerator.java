/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added formula extensions
 *******************************************************************************/
package org.eventb.internal.core.pog;

import static org.eventb.internal.core.Util.addExtensionDependencies;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCRefinesMachine;
import org.eventb.core.pog.ProofObligationGenerator;
import org.rodinp.core.IRodinFile;
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
	@Override
	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		
		IRodinFile source = RodinCore.valueOf(file);
		ISCMachineRoot sourceRoot = (ISCMachineRoot) source.getRoot();
		IMachineRoot mch = sourceRoot.getMachineRoot();
		final IPORoot targetRoot = mch.getPORoot();
		IRodinFile target = targetRoot.getRodinFile();
		
		graph.addTarget(target.getResource());
		graph.addToolDependency(
				source.getResource(), 
				target.getResource(), true);
		
		addExtensionDependencies(graph, targetRoot);
	
		ISCRefinesMachine[] refinesMachines = sourceRoot.getSCRefinesClauses();
		if (refinesMachines.length != 0) {
			final IRodinFile absSC = refinesMachines[0].getAbstractSCMachine();
			graph.addUserDependency(mch.getResource(), absSC.getResource(),
					target.getResource(), false);
		}

	}

}
