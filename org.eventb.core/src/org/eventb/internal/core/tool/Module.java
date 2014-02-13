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
package org.eventb.internal.core.tool;

import org.eventb.core.tool.IFilterModule;
import org.eventb.core.tool.IModule;
import org.eventb.core.tool.IProcessorModule;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class Module implements IModule {
	
	private IFilterModule[] filterModules;
	private IProcessorModule[] processorModules;
	private ModuleFactory moduleFactory;
	
	/**
	 * Sets the module factory for a particular configuration.
	 * This method must only be called once!
	 * 
	 * @param moduleFactory the module factory 
	 */
	final void setModuleFactory(final ModuleFactory moduleFactory) {
		assert this.moduleFactory == null;
		
		this.moduleFactory = moduleFactory;
	}
	
	protected IProcessorModule[] getProcessorModules() {
		if (processorModules == null) {
			processorModules = moduleFactory.getProcessorModules(getModuleType());
		}
		return processorModules;
	}
	
	protected IFilterModule[] getFilterModules() {
		if (filterModules == null) {
			filterModules = moduleFactory.getFilterModules(getModuleType());
		}
		return filterModules;
	}

}
