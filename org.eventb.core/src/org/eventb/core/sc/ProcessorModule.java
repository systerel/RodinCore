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
import org.rodinp.core.IRodinElement;


/**
 * 
 * @author Stefan Hallerstede
 *
 */
public abstract class ProcessorModule extends Module implements IProcessorModule {

	/** 
	 * Default implementation of <code>initModule()</code> does nothing.
	 * 
	 * @see IModule
	 */
	public void initModule(
			IRodinElement element,
			IStateRepository repository, 
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
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		// nothing to do by default
	}
	
	
}
