/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog;

import org.eventb.core.tool.IFilterModule;
import org.eventb.core.tool.IModule;
import org.eventb.core.tool.IProcessorModule;
import org.eventb.internal.core.tool.Module;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class POGModule extends Module {
	
	@Override
	protected IFilterModule[] getFilterModules() {
		IFilterModule[] filterModules = super.getFilterModules();
		traceModules(filterModules);
		return filterModules;
	}

	@Override
	protected IProcessorModule[] getProcessorModules() {
		IProcessorModule[] processorModules = super.getProcessorModules();
		traceModules(processorModules);
		return processorModules;
	}

	private <M extends IModule> void traceModules(M[] modules) {
		if (DEBUG_MODULE) {
			for (IModule module : modules) {
				System.out.println("POG ACCESS: " + module.getModuleType());
			}
		}
	}

	public static boolean DEBUG_MODULE = false;

}
