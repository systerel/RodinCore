/*******************************************************************************
 * Copyright (c) 2006, 2023 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added config in message for problem LoadingRootModuleError
 *     Systerel - added warnings for unknown configuration ids
 *     Systerel - added removal of temporary file
 *******************************************************************************/
package org.eventb.core.sc;

import static org.eventb.core.sc.GraphProblem.RepositoryFactoryLoadingError;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eventb.core.IConfigurationElement;
import org.eventb.core.IEventBRoot;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.SCStateRepository;
import org.eventb.internal.core.sc.SCUtil;
import org.eventb.internal.core.tool.IModuleFactory;
import org.eventb.internal.core.tool.SCModuleManager;
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

		try {
			SCStateRepository repository = new SCStateRepository((IEventBRoot) file.getRoot());

			if (SCUtil.DEBUG_STATE)
				repository.debug();

			return repository;
		} catch (CoreException e) {
			SCUtil.createProblemMarker(file, RepositoryFactoryLoadingError, e.getLocalizedMessage());
			if (SCUtil.DEBUG_STATE)
				System.out.println("SC: failed to load factory of " + file);
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IAutomaticTool#clean(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
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
		
		rootModule.initModule(file, repository, monitor);
	
		rootModule.process(file, target, repository, monitor);
		
		rootModule.endModule(file, repository, monitor);	
	
	}

	private static void deleteAllRodinMarkers(IRodinFile file) throws CoreException {
		file.getResource().deleteMarkers(
				RodinMarkerUtil.RODIN_PROBLEM_MARKER, 
				true, 
				IResource.DEPTH_INFINITE);
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
	// Consumes at most two ticks of the given monitor.
	private boolean compareAndSave(IRodinFile scFile, IRodinFile scTmpFile,
			IProgressMonitor monitor) throws RodinDBException {
		final SubMonitor sMonitor = SubMonitor.convert(monitor, 2);
		IEventBRoot scRoot = (IEventBRoot) scFile.getRoot();
		IEventBRoot scTmpRoot = (IEventBRoot) scTmpFile.getRoot();
		if (scTmpRoot.hasSameAttributes(scRoot)
				&& scTmpRoot.hasSameChildren(scRoot)) {
			scTmpFile.delete(true, sMonitor.split(2));
			return false;
		}
		scTmpFile.save(sMonitor.split(1), true, false);
		final IRodinElement project = scFile.getParent();
		final String name = scFile.getElementName();
		scTmpFile.move(project, null, name, true, sMonitor.split(1));
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
	
		addUnknownConfigWarnings(rodinFile, config);
		
		final IInternalElementType<?> elementType = rodinFile
				.getRootElementType();
		final ISCProcessorModule rootModule = (ISCProcessorModule) moduleFactory
				.getRootModule(elementType);
		if (rootModule == null) {
			SCUtil.createProblemMarker(rodinFile,
					GraphProblem.LoadingRootModuleError, config);
		}
		return rootModule;
	}
	
	private static void addUnknownConfigWarnings(IRodinFile sourceFile, String config) throws RodinDBException {
		for (String configId : SCModuleManager.getInstance().getUnknownConfigIds(config)) {
			SCUtil.createProblemMarker(sourceFile,
					GraphProblem.UnknownConfigurationWarning, configId);
		}
	}
	
	@Override
	public final boolean run(IFile source, IFile file, IProgressMonitor monitor) throws CoreException {

		final IRodinFile scFile = RodinCore.valueOf(file);
		final IRodinFile sourceFile = RodinCore.valueOf(source).getSnapshot();
		final IRodinFile scTmpFile = getTmpSCFile(scFile);

		final int childCount = sourceFile.getRoot().getChildren().length;
		final int totalWork = childCount + 6;
		final SubMonitor sMonitor = SubMonitor.convert(monitor,
				Messages.bind(Messages.build_runningSC, ((IEventBRoot) scFile.getRoot()).getComponentName()),
				totalWork);

		try {

			scTmpFile.create(true, sMonitor.split(1));

			sourceFile.open(sMonitor.split(1));
			scTmpFile.open(sMonitor.split(1));

			String config = getConfiguration(sourceFile);

			if (config != null) {

				setSCTmpConfiguration((IEventBRoot) scTmpFile.getRoot(), config);

				deleteAllRodinMarkers(sourceFile);

				ISCStateRepository repository = createRepository(sourceFile, sMonitor.split(1));

				if (repository != null) {
					final ISCProcessorModule rootModule = getRootModule(sourceFile, config);

					if (rootModule != null) {
						runProcessorModules(rootModule, sourceFile, scTmpFile.getRoot(), repository,
								sMonitor.split(childCount));
					}
				}

			}

			return compareAndSave(scFile, scTmpFile, sMonitor.split(1));

		} finally {
			// Ensure that the temporary file gets deleted
			if (scTmpFile.exists()) {
				scTmpFile.delete(true, sMonitor.split(1));
			}
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
