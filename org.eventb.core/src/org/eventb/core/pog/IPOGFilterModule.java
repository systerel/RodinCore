/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.pog;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IFilterModule;
import org.eventb.core.tool.IModule;

/**
 * The filter modules of the proof obligation generator can stop certain proof
 * obligations from being generated. The use of the methods
 * <code>initModule()</code>, <code>accept()</code>, and
 * <code>endModule()</code> are described in {@link IModule}. For each processor
 * module the associated filter modules are executed before each proof
 * obligation to be generated. If the filter returns <code>false</code>, the
 * proof obligation is not generated. The execution of the processor module is
 * not affected otherwise so that all side-effects can take place, in
 * particular, updates of the state repository.
 * 
 * @see POGFilterModule
 * @see POGProcessorModule
 * 
 * @author Stefan Hallerstede
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 *              Extend {@link POGFilterModule} instead.
 */
public interface IPOGFilterModule extends IFilterModule {


	/**
	 * Initialisation code for the module
	 * 
	 * @param repository the state repository to use
	 * @param monitor a progress monitor
	 * @throws CoreException if there was a problem initialising this module
	 */
	public abstract void initModule(
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Runs the proof obligation generator module. This is executed once for each proof 
	 * obligation.
	 * @param poName the name of the proof obligation to be generated
	 * @param monitor a progress monitor
	 * @return whether the proof obligation with the given name should be generated
	 * @throws CoreException if there was a problem running this module
	 */
	public abstract boolean accept(
			String poName,
			IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Termination code for the module
	 * 
	 * @param repository the state repository to use
	 * @param monitor a progress monitor
	 * @throws CoreException if there was a problem terminating this module
	 */
	
	public abstract void endModule(
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException;

}
