/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
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
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModule;
import org.eventb.core.tool.IProcessorModule;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * The processor modules of the static checker generate the statically checked
 * output. The use of the methods <code>initModule()</code>,
 * <code>process()</code>, and <code>endModule()</code> are described in
 * {@link IModule}.
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
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 *              Extend {@link SCProcessorModule} instead.
 */
public interface ISCProcessorModule extends IProcessorModule {

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
			ISCStateRepository repository,
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
			IInternalElement target,
			ISCStateRepository repository, 
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
			ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException;

}