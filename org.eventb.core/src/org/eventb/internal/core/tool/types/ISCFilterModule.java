/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool.types;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.sc.state.ISCStateRepository;
import org.rodinp.core.IRodinElement;

/**
 * The processor modules of the static checker are used to accept (or reject)
 * elements of a component. The use of the methods <code>initModule()</code>,
 * <code>process()</code>, and <code>endModule()</code> are described 
 * in {@link IModule}.
 * <p>
 * The state and the state repository must be of types {@link ISCState} and
 * {@link ISCStateRepository} to avoid accidental mixing of static checker and
 * proof obligation generator state.
 * 
 * @see IModule
 * @see IProcessorModule
 * @see ISCState
 * @see ISCStateRepository
 * 
 * @author Stefan Hallerstede
 *
 */
public interface ISCFilterModule extends IFilterModule {

	/**
	 * Runs the static checker module. Returns whether the <code>element</code>
	 * should be accepted. If an error marker was associated with the
	 * <code>element</code> the returned value should usually be
	 * <code>false</code>. Exceptions from this rule are possible, in
	 * particular, if the <code>element</code> has been already marked with an error.
	 * 
	 * @param element
	 *            the input "unchecked" element
	 * @param repository
	 *            the state repository to use
	 * @param monitor
	 *            a progress monitor
	 * @return whether the element should be accepted
	 * @throws CoreException
	 *             if there was a problem running this module
	 */
	public abstract boolean accept(
			IRodinElement element,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException;

	/**
	 * Initialisation code for the module
	 * 
	 * @param repository the state repository to use
	 * @param monitor a progress monitor
	 * @throws CoreException if there was a problem initialising this module
	 */
	public abstract void initModule(
			ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException;

	/**
	 * Termination code for the module
	 * 
	 * @param repository the state repository to use
	 * @param monitor a progress monitor
	 * @throws CoreException if there was a problem terminating this module
	 */
	public abstract void endModule(
			ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException;

}