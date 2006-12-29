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
import org.eventb.core.sc.state.IStateRepository;
import org.eventb.core.tool.IToolFilterModule;
import org.rodinp.core.IRodinElement;

/**
 * Common protocol for filter modules.
 * The protocol has two variants:
 * <li>
 * <ul> The ONCE protocol. Method <code>run()</code> is called exactly once
 * as follows:
 * <p>
 * <code>
 * m.initModule(element, repository, monitor);
 * a = m.accept(element, repository, monitor);
 * ...
 * m.endModule(element, repository, monitor);
 * </code>
 * </p>
 * </ul>
 * <ul> The LOOP protocol. Method <code>run()</code> is called in a loop traversing a list of elements
 * as follows:
 * <p>
 * <code>
 * m.initModule(element, repository, monitor);
 * while (more elements) {
 *    a = m.accept(element, repository, monitor);
 *    ...
 * }
 * m.endModule(element, repository, monitor);
 * </code>
 * </p>
 * </ul>
 * </li>
 * <p>
 * It must be guaranteed by all implementors that the
 * methods are called in the specified order.
 * 
 * @see org.eventb.core.tool.IToolModule
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IFilterModule extends IToolFilterModule {

	/**
	 * Runs the static checker module.
	 * Returns wether the element <code>element</code> should be accepted.
	 * If an error marker was associated with the element the returned value should usually be 
	 * <code>false</code>. Exceptions from this rule are possible, in particular, if a file
	 * has been marked with an error.
	 * @param element the input "unchecked" element
	 * @param repository the state repository to use
	 * @param monitor a progress monitor
	 * @return wether the element should be accepted
	 * @throws CoreException if there was a problem running this module
	 */
	public abstract boolean accept(
			IRodinElement element,
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException;

	/**
	 * Initialisation code for the module
	 * 
	 * @param repository the state repository to use
	 * @param monitor a progress monitor
	 * @throws CoreException if there was a problem initialising this module
	 */
	public abstract void initModule(
			IStateRepository repository,
			IProgressMonitor monitor) throws CoreException;

	/**
	 * Termination code for the module
	 * 
	 * @param repository the state repository to use
	 * @param monitor a progress monitor
	 * @throws CoreException if there was a problem terminating this module
	 */
	public abstract void endModule(
			IStateRepository repository,
			IProgressMonitor monitor) throws CoreException;

}