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
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextFile;
import org.eventb.core.IExtendsContext;
import org.eventb.core.ISCContextFile;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.IProcessorModule;
import org.eventb.core.sc.state.IStateSC;
import org.eventb.core.state.IStateRepository;
import org.eventb.internal.core.sc.symbolTable.ContextLabelSymbolTable;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextStaticChecker extends StaticChecker {
	
	private final static int LABEL_SYMTAB_SIZE = 2047;

	public static final String CONTEXT_SC_TOOL_ID = EventBPlugin.PLUGIN_ID + ".contextSC"; //$NON-NLS-1$
	public static final String CONTEXT_SC_EXTENDS_ID = EventBPlugin.PLUGIN_ID + ".contextSCExtends"; //$NON-NLS-1$

	public static final String CONTEXT_PROCESSOR = EventBPlugin.PLUGIN_ID + ".contextProcessor"; //$NON-NLS-1$
	
	private final IModuleManager manager;
	
	private IProcessorModule[] contextProcessorModules = null;
	
	public ContextStaticChecker() {
		manager = ModuleManager.getModuleManager();
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IAutomaticTool#run(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean run(IFile file, IProgressMonitor monitor)
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

			if (contextProcessorModules == null) {
			
				contextProcessorModules = manager.getProcessorModules(CONTEXT_PROCESSOR);
			
			}
		
			scContextFile.create(true, monitor);

			IStateRepository<IStateSC> repository = createRepository(contextFile, monitor);
		
			contextFile.open(new SubProgressMonitor(monitor, 1));
			scContextFile.open(new SubProgressMonitor(monitor, 1));
		
			runProcessorModules(
					contextFile, 
					scContextFile,
					contextProcessorModules, 
					repository,
					monitor);
		
			scContextFile.save(new SubProgressMonitor(monitor, 1), true);
		
			// TODO delta checking
			// return repository.targetHasChanged();
		
			return true;
			
		} finally {
			monitor.done();
		}
	}

	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		
		try {
			
			monitor.beginTask(Messages.bind(Messages.build_extracting, file.getName()), 1);
		
			IContextFile source = (IContextFile) RodinCore.valueOf(file);
			ISCContextFile target = source.getSCContextFile();
		
			graph.openGraph();
			graph.addNode(target.getResource(), CONTEXT_SC_TOOL_ID);
			graph.addToolDependency(
					source.getResource(), 
					target.getResource(), CONTEXT_SC_TOOL_ID, true);
		
			IExtendsContext[] extendsContexts = source.getExtendsClauses();
			for(IExtendsContext extendsContext : extendsContexts) {
				graph.addUserDependency(
						source.getResource(), 
						extendsContext.getAbstractSCContext().getResource(), 
						target.getResource(), 
						CONTEXT_SC_EXTENDS_ID, 
						false);
			}
		
			graph.closeGraph();
			
		} finally {
			monitor.done();
		}

	}

	@Override
	protected IStateRepository<IStateSC> createRepository(
			IRodinFile file, 
			IProgressMonitor monitor) throws CoreException {
		IStateRepository<IStateSC> repository = super.createRepository(file, monitor);
		final ContextLabelSymbolTable labelSymbolTable = 
			new ContextLabelSymbolTable(LABEL_SYMTAB_SIZE);
		repository.setState(labelSymbolTable);		
		return repository;
	}

}
