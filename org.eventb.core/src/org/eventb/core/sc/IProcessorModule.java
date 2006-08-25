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
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;

/**
 * Common protocol for processor modules.
 * The protocol has two variants:
 * <li>
 * <ul> The ONCE protocol. Method <code>run()</code> is called exactly once
 * as follows:
 * <p>
 * <code>
 * m.initModule(repository, monitor);
 * m.process(element, repository, monitor);
 * ...
 * m.endModule(repository, monitor);
 * </code>
 * </p>
 * </ul>
 * <ul> The LOOP protocol. Method <code>run()</code> is called in a loop traversing a list of elements
 * as follows:
 * <p>
 * <code>
 * m.initModule(repository, monitor);
 * while (more elements) {
 *    m.process(element, repository, monitor);
 *    ...
 * }
 * m.endModule(repository, monitor);
 * </code>
 * </p>
 * </ul>
 * </li>
 * <p>
 * It must be guaranteed by all implementors that the
 * methods are called in the specified order.
 * 
 * @see org.eventb.core.sc.IModule
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IProcessorModule {

	/**
	 * Initialisation code for the module
	 * 
	 * @param element the input "unchecked" element
	 * @param repository the state repository to use
	 * @param monitor a progress monitor
	 * @throws CoreException if there was a problem initialising this module
	 */
	public abstract void initModule(
			IRodinElement element,
			IStateRepository repository,
			IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Runs the static checker module: process the element. 
	 * The element itself has already been accepted.
	 * @param element the input "unchecked" element
	 * @param target the target element (this may be a file or any other internal element)
	 * @param repository the state repository to use
	 * @param monitor a progress monitor
	 * @throws CoreException if there was a problem running this module
	 */
	public abstract void process(
			IRodinElement element,
			IInternalParent target,
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Termination code for the module
	 * 
	 * @param element the input "unchecked" element
	 * @param repository the state repository to use
	 * @param monitor a progress monitor
	 * @throws CoreException if there was a problem terminating this module
	 */
	
	public abstract void endModule(
			IRodinElement element,
			IStateRepository repository,
			IProgressMonitor monitor) throws CoreException;

}