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
import org.eventb.internal.core.tool.types.IFilterModule;
import org.eventb.internal.core.tool.types.IProcessorModule;
import org.eventb.internal.core.tool.types.ISCFilterModule;


/**
 * Default implementation of a static checker filter module. 
 * <p>
 * The two methods <code>getFilterModules()</code> and <code>getProcessorModules()</code>
 * throw an <code>UnsupportedOperationException</code>. Filter modules cannot have
 * child modules.
 *
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public abstract class SCFilterModule extends SCModule implements ISCFilterModule {

	@Override
	protected final IFilterModule[] getFilterModules() {
		throw new UnsupportedOperationException("Attempt to load submodules in filter module");
	}

	@Override
	protected final IProcessorModule[] getProcessorModules() {
		throw new UnsupportedOperationException("Attempt to load submodules in filter module");
	}

	/** 
	 * Default implementation of <code>initModule()</code> does nothing.
	 * 
	 * @see IFilterModule
	 */
	public void initModule(
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		// nothing to do by default
	}
	
	/** 
	 * Default implementation of <code>initModule()</code> does nothing.
	 * 
	 * @see IFilterModule
	 */
	public void endModule(
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		// nothing to do by default
	}
	
}
