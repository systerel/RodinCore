/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool;

import java.util.List;

import org.eventb.core.pog.IPOGFilterModule;
import org.eventb.core.pog.IPOGProcessorModule;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.tool.graph.ModuleGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class POGModuleFactory extends ModuleFactory<IPOGFilterModule, IPOGProcessorModule> {
	
	public POGModuleFactory(ModuleGraph graph, ModuleManager<IPOGFilterModule, IPOGProcessorModule> manager) {
		super(graph, manager);
	}

	IPOGFilterModule[] NO_FILTERS = new IPOGFilterModule[0];
	IPOGProcessorModule[] NO_PROCESSORS = new IPOGProcessorModule[0];

	public IPOGFilterModule[] getFilterModules(IModuleType<? extends IPOGProcessorModule> parent) {
		List<ModuleDesc<? extends IPOGFilterModule>> filters = filterMap.get(parent);
		if (filters == null)
			return NO_FILTERS;
		int size = filters.size();
		IPOGFilterModule[] filterModules = new IPOGFilterModule[size];
		for (int k=0; k<size; k++) {
			filterModules[k] = filters.get(k).createInstance();
		}
		return filterModules;
	}

	public IPOGProcessorModule[] getProcessorModules(IModuleType<? extends IPOGProcessorModule> parent) {
		List<ModuleDesc<? extends IPOGProcessorModule>> processors = processorMap.get(parent);
		if (processors == null)
			return NO_PROCESSORS;
		int size = processors.size();
		IPOGProcessorModule[] processorModules = new IPOGProcessorModule[size];
		for (int k=0; k<size; k++) {
			processorModules[k] = processors.get(k).createInstance();
		}
		return processorModules;
	}

}
