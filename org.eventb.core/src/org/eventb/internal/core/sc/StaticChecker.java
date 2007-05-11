/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core.sc;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBFile;
import org.eventb.core.sc.ISCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.internal.core.tool.IModuleFactory;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinMarkerUtil;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class StaticChecker implements IAutomaticTool, IExtractor {

	public static boolean DEBUG = false;
	public static boolean DEBUG_STATE = false;
	public static boolean DEBUG_MARKERS = false;
	public static boolean DEBUG_MODULECONF = false;
	
	protected static final String DEFAULT_CONFIG = EventBPlugin.PLUGIN_ID + ".fwd";

	protected final ISCStateRepository createRepository(
			IRodinFile file, 
			IProgressMonitor monitor) throws CoreException {
		
		final SCStateRepository repository = new SCStateRepository();
		
		if (DEBUG_STATE)
			repository.debug();
		
		return repository;
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IAutomaticTool#clean(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void clean(IFile source, IFile file, IProgressMonitor monitor)
			throws CoreException {
		
		try {
			
			IEventBFile eventbFile = (IEventBFile) RodinCore.valueOf(file);
		
			monitor.beginTask(Messages.bind(Messages.build_cleaning, file.getName()), 1);
			
			if (eventbFile.exists())
				eventbFile.delete(true, monitor);
			
			monitor.worked(1);
			
		} finally {
			monitor.done();
		}

	}

	protected void runProcessorModules(
			ISCProcessorModule rootModule,
			IRodinFile file, 
			IInternalParent target, 
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

	protected void printModuleTree(IRodinFile file, IModuleFactory moduleFactory) {
		if (DEBUG_MODULECONF) {
			System.out.println("+++ STATIC CHECKER MODULES +++");
			System.out.println("INPUT " + file.getPath());
			System.out.println("      " + file.getElementType());
			System.out.println("CONFIG " + DEFAULT_CONFIG);
			System.out.print(moduleFactory
					.printModuleTree(file.getElementType()));
			System.out.println("++++++++++++++++++++++++++++++++++++++");
		}
	}

}
