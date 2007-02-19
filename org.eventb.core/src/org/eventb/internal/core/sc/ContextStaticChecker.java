/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.IContextFile;
import org.eventb.core.IExtendsContext;
import org.eventb.core.ISCContextFile;
import org.eventb.core.sc.ISCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.internal.core.sc.modules.ContextModule;
import org.eventb.internal.core.tool.IModuleFactory;
import org.eventb.internal.core.tool.SCModuleManager;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextStaticChecker extends StaticChecker {

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IAutomaticTool#run(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean run(IFile source, IFile file, IProgressMonitor monitor)
			throws CoreException {
		
		ISCContextFile scContextFile = (ISCContextFile) RodinCore.valueOf(file).getMutableCopy();
		IContextFile contextFile = (IContextFile) scContextFile.getContextFile().getSnapshot();
		
		int size = contextFile.getChildren().length + 3;
		
		try {
			
			monitor.beginTask(
					Messages.bind(
							Messages.build_runningMSC, 
							StaticChecker.getStrippedComponentName(file.getName())), 
					size);

			scContextFile.create(true, monitor);

			ISCStateRepository repository = createRepository(contextFile, monitor);
		
			contextFile.open(new SubProgressMonitor(monitor, 1));
			scContextFile.open(new SubProgressMonitor(monitor, 1));
			
			IModuleFactory moduleFactory = 
				SCModuleManager.getInstance().getModuleFactory(DEFAULT_CONFIG);
			
			ISCProcessorModule rootModule = 
				(ISCProcessorModule) moduleFactory.getRootModule(ContextModule.MODULE_TYPE);
		
			runProcessorModules(
					rootModule,
					contextFile, 
					scContextFile,
					repository,
					monitor);
		
			scContextFile.save(new SubProgressMonitor(monitor, 1), true);
		
			// TODO delta checking
			// return repository.targetHasChanged();
		
			return true;
			
		} finally {
			monitor.done();
			scContextFile.makeConsistent(null);
		}
	}

	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		
		try {
			
			monitor.beginTask(Messages.bind(Messages.build_extracting, file.getName()), 1);
		
			IContextFile source = (IContextFile) RodinCore.valueOf(file);
			ISCContextFile target = source.getSCContextFile();
		
			graph.addTarget(target.getResource());
			graph.addToolDependency(
					source.getResource(), 
					target.getResource(), true);
		
			IExtendsContext[] extendsContexts = source.getExtendsClauses();
			for(IExtendsContext extendsContext : extendsContexts) {
				graph.addUserDependency(
						source.getResource(), 
						extendsContext.getAbstractSCContext().getResource(), 
						target.getResource(), 
						false);
			}
		
		} finally {
			monitor.done();
		}

	}

}
