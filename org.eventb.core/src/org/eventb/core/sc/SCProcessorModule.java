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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.internal.core.tool.types.IFilterModule;
import org.eventb.internal.core.tool.types.IModule;
import org.eventb.internal.core.tool.types.IProcessorModule;
import org.eventb.internal.core.tool.types.ISCFilterModule;
import org.eventb.internal.core.tool.types.ISCProcessorModule;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;


/**
 * Default implementation of a static checker processor module. 
 * 
 * @see ISCProcessorModule
 * @see SCModule
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public abstract class SCProcessorModule extends SCModule implements ISCProcessorModule {

	@Override
	protected final IFilterModule[] getFilterModules() {
		return super.getFilterModules();
	}

	@Override
	protected final IProcessorModule[] getProcessorModules() {
		return super.getProcessorModules();
	}

	private static final String PROCESSOR = "PROCESSOR";
	private static final String FILTER = "FILTER";
	private static final String INI = "INI";
	private static final String RUN = "RUN";
	private static final String END = "END";

	private <M extends IModule> void traceModule(M module, String op, String kind) {
		System.out.println("SC MOD" + op + ": " + module.getModuleType() + " " + kind);
	}

	/**
	 * Initialise filter modules in the order returned by
	 * <code>getFilterModules()</code>.
	 * 
	 * @param repository
	 *            the state repository to pass to all filter modules
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws CoreException
	 *             if there was a problem during the initialisation of one of
	 *             the modules
	 */
	protected final void initFilterModules(
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IFilterModule module : getFilterModules()) {
			if (DEBUG_MODULE)
				traceModule(module, INI, FILTER);
			ISCFilterModule scModule = (ISCFilterModule) module;
			scModule.initModule(repository, monitor);
		}
	}
	
	/**
	 * Initialise processor modules in the order returned by
	 * <code>getProcessorModules()</code>.
	 * 
	 * @param repository
	 *            the state repository to pass to all processor modules
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws CoreException
	 *             if there was a problem during the initialisation of one of
	 *             the modules
	 */
	protected final void initProcessorModules(
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
	
	/**
	 * Evaluate whether to accept or reject an element using the child filter
	 * modules in the order returned by <code>getFilterModules()</code>.
	 * <p>
	 * The evaluation is stopped as soon as some filter module returns
	 * <code>false</code>.
	 * 
	 * @param element
	 *            the element to accept or reject
	 * @param repository
	 *            the state repository to pass to all filter modules
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return whether to accept the specified element
	 * @throws CoreException
	 *             if there was a problem during the evaluation
	 */
	protected final boolean filterModules(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		boolean ok = true;
		for (IFilterModule module : getFilterModules()) {
			if (DEBUG_MODULE)
				traceModule(module, RUN, FILTER);
			ISCFilterModule scModule = (ISCFilterModule) module;
			if (scModule.accept(element, repository, monitor))
				continue;
			ok = false;
		}
		return ok;
	}
	
	/**
	 * Process an element using the child processor modules in the order
	 * returned by <code>getProcessorModules()</code>.
	 * 
	 * @param element
	 *            the element to process
	 * @param repository
	 *            the state repository to pass to all processor modules
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws CoreException
	 *             if there was a problem during processing
	 */
	protected final void processModules(
			IRodinElement element, 
			IInternalElement target,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IProcessorModule module : getProcessorModules()) {
			if (DEBUG_MODULE)
				traceModule(module, RUN, PROCESSOR);
			ISCProcessorModule scModule = (ISCProcessorModule) module;
			scModule.process(element, target, repository, monitor);
		}
	}
	
	/**
	 * Terminate filter modules in the order returned by
	 * <code>getFilterModules()</code>.
	 * 
	 * @param repository
	 *            the state repository to pass to all filter modules
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws CoreException
	 *             if there was a problem during the termination of one of
	 *             the modules
	 */
	protected final void endFilterModules(
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IFilterModule module : getFilterModules()) {
			if (DEBUG_MODULE)
				traceModule(module, END, FILTER);
			ISCFilterModule scModule = (ISCFilterModule) module;
			scModule.endModule(repository, monitor);
		}
	}

	/**
	 * Terminate processor modules in the order returned by
	 * <code>getProcessorModules()</code>.
	 * 
	 * @param repository
	 *            the state repository to pass to all processor modules
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws CoreException
	 *             if there was a problem during the termination of one of
	 *             the modules
	 */
	protected final void endProcessorModules(
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
	 * @see IProcessorModule
	 */
	@Override
	public void initModule(
			IRodinElement element,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		// nothing to do by default
	}
	
	/** 
	 * Default implementation of <code>initModule()</code> does nothing.
	 * 
	 * @see IProcessorModule
	 */
	@Override
	public void endModule(
			IRodinElement element,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		// nothing to do by default
	}
	
	
}
