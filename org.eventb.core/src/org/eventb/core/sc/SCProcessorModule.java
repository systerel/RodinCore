/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IFilterModule;
import org.eventb.core.tool.IModule;
import org.eventb.core.tool.IProcessorModule;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;


/**
 * 
 * @author Stefan Hallerstede
 *
 */
public abstract class SCProcessorModule extends SCModule implements ISCProcessorModule {

	private static final String PROCESSOR = "PROCESSOR";
	private static final String FILTER = "FILTER";
	private static final String INI = "INI";
	private static final String RUN = "RUN";
	private static final String END = "END";

	private <M extends IModule> void traceModule(M module, String op, String kind) {
		System.out.println("SC MOD" + op + ": " + module.getModuleType() + " " + kind);
	}

	protected void initFilterModules(
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IFilterModule module : getFilterModules()) {
			if (DEBUG_MODULE)
				traceModule(module, INI, FILTER);
			ISCFilterModule scModule = (ISCFilterModule) module;
			scModule.initModule(repository, monitor);
		}
	}
	
	protected void initProcessorModules(
			IRodinElement element,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IProcessorModule module : getProcessorModules()) {
			if (DEBUG_MODULE)
				traceModule(module, INI, PROCESSOR);
			ISCProcessorModule scModule = (ISCProcessorModule) module;
			scModule.initModule(element, repository, monitor);
		}
	}
	
	protected boolean filterModules(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IFilterModule module : getFilterModules()) {
			if (DEBUG_MODULE)
				traceModule(module, RUN, FILTER);
			ISCFilterModule scModule = (ISCFilterModule) module;
			if (scModule.accept(element, repository, monitor))
				continue;
			return false;
		}
		return true;
	}
	
	protected void processModules(
			IRodinElement element, 
			IInternalParent target,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IProcessorModule module : getProcessorModules()) {
			if (DEBUG_MODULE)
				traceModule(module, RUN, PROCESSOR);
			ISCProcessorModule scModule = (ISCProcessorModule) module;
			scModule.process(element, target, repository, monitor);
		}
	}
	
	protected void endFilterModules(
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IFilterModule module : getFilterModules()) {
			if (DEBUG_MODULE)
				traceModule(module, END, FILTER);
			ISCFilterModule scModule = (ISCFilterModule) module;
			scModule.endModule(repository, monitor);
		}
	}

	protected void endProcessorModules(
			IRodinElement element,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IProcessorModule module : getProcessorModules()) {
			if (DEBUG_MODULE)
				traceModule(module, END, PROCESSOR);
			ISCProcessorModule scModule = (ISCProcessorModule) module;
			scModule.endModule(element, repository, monitor);
		}
	}
	
	/** 
	 * Default implementation of <code>initModule()</code> does nothing.
	 * 
	 * @see IModule
	 */
	public void initModule(
			IRodinElement element,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		// nothing to do by default
	}
	
	/** 
	 * Default implementation of <code>initModule()</code> does nothing.
	 * 
	 * @see IModule
	 */
	public void endModule(
			IRodinElement element,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		// nothing to do by default
	}
	
	
}
