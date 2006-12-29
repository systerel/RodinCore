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
import org.eventb.core.tool.IToolModule;


/**
 *
 * @author Stefan Hallerstede
 *
 */
public abstract class FilterModule extends Module implements IFilterModule {

	/** 
	 * Default implementation of <code>initModule()</code> does nothing.
	 * 
	 * @see IToolModule
	 */
	public void initModule(
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		// nothing to do by default
	}
	
	/** 
	 * Default implementation of <code>initModule()</code> does nothing.
	 * 
	 * @see IToolModule
	 */
	public void endModule(
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		// nothing to do by default
	}
	
}
