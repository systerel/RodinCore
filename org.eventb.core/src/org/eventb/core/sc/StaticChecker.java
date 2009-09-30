/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.sc;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.IConfigurationElement;
import org.eventb.core.IEventBRoot;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.SCStateRepository;
import org.eventb.internal.core.sc.SCUtil;
import org.eventb.internal.core.tool.IModuleFactory;
import org.eventb.internal.core.tool.SCModuleManager;
import org.eventb.internal.core.tool.types.ISCProcessorModule;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;

/**
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public abstract class StaticChecker implements IAutomaticTool, IExtractor {

	private ISCStateRepository createRepository(
			IRodinFile file, 
			IProgressMonitor monitor) throws CoreException {
		
		final SCStateRepository repository = new SCStateRepository();
		
		if (SCUtil.DEBUG_STATE)
			repository.debug();
		
		return repository;
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IAutomaticTool#clean(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void clean(IFile source, IFile file, IProgressMonitor monitor)
			throws CoreException {
		
		try {
			
			IRodinFile eventbFile = RodinCore.valueOf(file);
		
			monitor.beginTask(Messages.bind(Messages.build_cleaning, file.getName()), 1);
			
			if (eventbFile.exists())
				eventbFile.delete(true, monitor);
			
			monitor.worked(1);
			
		} finally {
			monitor.done();
		}

	}

	private void runProcessorModules(
			ISCProcessorModule rootModule,
			IRodinFile file, 
			IInternalElement target, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		file.getResource().deleteMarkers(
				RodinMarkerUtil.RODIN_PROBLEM_MARKER, 
				true, 
				IResource.DEPTH_INFINITE);
		
		rootModule.initModule(file, repository, monitor);
	
		rootModule.process(file, target, repository, monitor);
		
		rootModule.endModule(file, repository, monitor);	
	
	}

	private void printModuleTree(String config, IRodinFile file, IModuleFactory moduleFactory) {
		if (SCUtil.DEBUG_MODULECONF) {
			System.out.println("+++ STATIC CHECKER MODULES +++");
			System.out.println("INPUT " + file.getPath());
			System.out.println("      " + file.getRoot().getElementType());
			System.out.println("CONFIG " + config);
			System.out.print(moduleFactory
					.printModuleTree(file.getRoot().getElementType()));
			System.out.println("++++++++++++++++++++++++++++++++++++++");
		}
	}

	private IRodinFile getTmpSCFile(IRodinFile scFile) {
		final IRodinProject project = (IRodinProject) scFile.getParent();
		final String name = scFile.getElementName();
		return project.getRodinFile(name + "_tmp");
	}

	// Compare the temporary file with the current statically checked file.
	// If they're equal, then don't do anything and return false. Otherwise,
	// copy the temporary file to the statically-checked file, save it and
	// return true.
	//
	// Consumes at most to ticks of the given monitor.
	private boolean compareAndSave(IRodinFile scFile, IRodinFile scTmpFile,
			IProgressMonitor monitor) throws RodinDBException {
		IEventBRoot scRoot = (IEventBRoot) scFile.getRoot();
		IEventBRoot scTmpRoot = (IEventBRoot) scTmpFile.getRoot();
		if (scTmpRoot.hasSameAttributes(scRoot)
				&& scTmpRoot.hasSameChildren(scRoot)) {
			scTmpFile.delete(true, new SubProgressMonitor(monitor, 1));
			return false;
		}
		scTmpFile.save(new SubProgressMonitor(monitor, 1), true, false);
		final IRodinElement project = scFile.getParent();
		final String name = scFile.getElementName();
		final SubProgressMonitor subPM = new SubProgressMonitor(monitor, 1);
		scTmpFile.move(project, null, name, true, subPM);
		return true;
	}
	
	private String getConfiguration(final IRodinFile rodinFile) throws CoreException {
		
		IConfigurationElement confElement = (IConfigurationElement) rodinFile.getRoot();
		
		if (confElement.hasConfiguration()) {
	
			return confElement.getConfiguration();
	
		} else {
			SCUtil.createProblemMarker(confElement, 
					GraphProblem.ConfigurationMissingError, 
					rodinFile.getBareName());
			return null;
		}
	}

	private ISCProcessorModule getRootModule(final IRodinFile rodinFile, String config) throws CoreException {
	
		IModuleFactory moduleFactory = SCModuleManager.getInstance().getModuleFactory(config);
	
		printModuleTree(config, rodinFile, moduleFactory);
	
		final IInternalElementType<?> elementType = rodinFile
				.getRootElementType();
		final ISCProcessorModule rootModule = (ISCProcessorModule) moduleFactory
				.getRootModule(elementType);
		if (rootModule == null) {
			SCUtil.createProblemMarker(rodinFile,
					GraphProblem.LoadingRootModuleError);
		}
		return rootModule;
	}
	
	public final boolean run(IFile source, IFile file, IProgressMonitor monitor)
			throws CoreException {
			
				final IRodinFile scFile = RodinCore.valueOf(file);
				final IRodinFile sourceFile = RodinCore.valueOf(source).getSnapshot();
				final IRodinFile scTmpFile = getTmpSCFile(scFile);
				
				final int totalWork = sourceFile.getRoot().getChildren().length + 5;
			
				try {
			
					monitor.beginTask(
					Messages.bind(Messages.build_runningSC,
							((IEventBRoot) scFile.getRoot())
									.getComponentName()), totalWork);
			
					scTmpFile.create(true, new SubProgressMonitor(monitor, 1));
			
					ISCStateRepository repository = createRepository(sourceFile,
							monitor);
			
					sourceFile.open(new SubProgressMonitor(monitor, 1));
					scTmpFile.open(new SubProgressMonitor(monitor, 1));
					
					String config = getConfiguration(sourceFile);
			
					if (config != null) {
						
						setSCTmpConfiguration((IEventBRoot) scTmpFile.getRoot(), config);
						
						final ISCProcessorModule rootModule = getRootModule(sourceFile, config);
					
						if (rootModule != null) {
							runProcessorModules(rootModule, sourceFile, scTmpFile.getRoot(),
									repository, monitor);
						}
						
					}
			
					return compareAndSave(scFile, scTmpFile, monitor);
			
				} finally {
					monitor.done();
					scFile.makeConsistent(null);
				}
			}

	private void setSCTmpConfiguration(final IEventBRoot scTmpRoot, String config)
			throws RodinDBException {
				
				IConfigurationElement confElement = (IConfigurationElement) scTmpRoot;
				confElement.setConfiguration(config, null);
			}	

}
