/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.sc;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.sc.StaticChecker;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineStaticChecker extends StaticChecker {

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IExtractor#extract(org.eclipse.core.resources.IFile, org.rodinp.core.builder.IGraph)
	 */
	@Override
	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		
		try {
			
			monitor.beginTask(Messages.bind(Messages.build_extracting, file.getName()), 1);
		
			IRodinFile source = RodinCore.valueOf(file);
			IMachineRoot sourceRoot = (IMachineRoot) source.getRoot();
			IRodinFile target = sourceRoot.getSCMachineRoot().getRodinFile();
			ISeesContext[] seen = sourceRoot.getSeesClauses();
			IRefinesMachine[] abstractMachines = sourceRoot.getRefinesClauses();

			graph.addTarget(target.getResource());
			graph.addToolDependency(
					source.getResource(), 
					target.getResource(), true);	
		
			for (ISeesContext seesContext : seen) {
				if (seesContext.hasSeenContextName()) {
					IRodinFile seenSCContext = seesContext.getSeenSCContext();
					graph.addUserDependency(
							source.getResource(), 
							seenSCContext.getResource(), 
							target.getResource(), 
							true);
				}
			}
		
			if (abstractMachines.length != 0 && abstractMachines[0].hasAbstractMachineName()) {
				IRodinFile abstractSCMachine = abstractMachines[0].getAbstractSCMachine();
				graph.addUserDependency(
						source.getResource(), 
						abstractSCMachine.getResource(), 
						target.getResource(), 
						true);
			}
		
		} finally {
			monitor.done();
		}

	}

}
