/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.tool.types;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.pog.state.IPOGState;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.rodinp.core.IRodinElement;


/**
 * The processor modules of the proof obligation generator generate 
 * the proof obligations. The use of the methods <code>initModule()</code>,
 * <code>process()</code>, and <code>endModule()</code> are described 
 * in {@link IModule}.
 * <p>
 * The state and the state repository must be of types {@link IPOGState} and
 * {@link IPOGStateRepository} to avoid accidental mixing of static checker and
 * proof obligation generator state.
 * 
 * @see IModule
 * @see IProcessorModule
 * @see IPOGState
 * @see IPOGStateRepository
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IPOGProcessorModule extends IProcessorModule {


	/**
	 * Initialisation code for the module
	 * 
	 * @param element statically checked element
	 * @param repository the state repository to use
	 * @param monitor a progress monitor
	 * @throws CoreException if there was a problem initialising this module
	 */
	public abstract void initModule(
			IRodinElement element,
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Runs the proof obligation generator module: process the element. 
	 * @param element statically checked element
	 * @param repository the state repository to use
	 * @param monitor a progress monitor
	 * @throws CoreException if there was a problem running this module
	 */
	public abstract void process(
			IRodinElement element,
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Termination code for the module
	 * 
	 * @param element statically checked element
	 * @param repository the state repository to use
	 * @param monitor a progress monitor
	 * @throws CoreException if there was a problem terminating this module
	 */
	
	public abstract void endModule(
			IRodinElement element,
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException;

}