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
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.ISCContextFile;
import org.eventb.core.pog.IPOGModuleManager;
import org.eventb.core.pog.IPOGProcessorModule;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextPOGenerator extends ProofObligationGenerator {

	public static final String CONTEXT_MODULE = EventBPlugin.PLUGIN_ID + ".contextModule"; //$NON-NLS-1$
	
	private IPOGModuleManager manager;
	
	private IPOGProcessorModule[] machineModules = null;
	
	public ContextPOGenerator() {
		manager = ModuleManager.getModuleManager();
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IAutomaticTool#run(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean run(IFile source, IFile target, IProgressMonitor monitor)
			throws CoreException {
		
		// TODO implement context static checker
		
		IPOFile poFile = (IPOFile) RodinCore.valueOf(target).getMutableCopy();
		ISCContextFile scContextFile = (ISCContextFile) poFile.getSCContextFile().getSnapshot();

		try {
		
			monitor.beginTask(
					Messages.bind(
							Messages.build_runningCPO, 
							poFile.getComponentName()),
					1);
			
			if (machineModules == null) {
				
				machineModules = manager.getProcessorModules(CONTEXT_MODULE);
			
			}
		
			poFile.create(true, monitor);
		
			IPOGStateRepository repository = createRepository(poFile, monitor);
			
			runModules(
					scContextFile, 
					poFile,
					machineModules, 
					repository,
					monitor);
		
			poFile.save(monitor, true);
			
			return true;
		} finally {
			monitor.done();
			poFile.makeConsistent(null);
		}
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IExtractor#extract(org.eclipse.core.resources.IFile, org.rodinp.core.builder.IGraph)
	 */
	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		ISCContextFile source = (ISCContextFile) RodinCore.valueOf(file);
		IPOFile target = source.getContextFile().getPOFile();
				
		graph.addTarget(target.getResource());
		graph.addToolDependency(
				source.getResource(), 
				target.getResource(), true);

	}

}
