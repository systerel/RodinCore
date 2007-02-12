/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool;

import java.util.List;

import org.eventb.core.sc.ISCFilterModule;
import org.eventb.core.sc.ISCProcessorModule;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.tool.graph.ModuleGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class SCModuleFactory extends ModuleFactory<ISCFilterModule, ISCProcessorModule> {

	public SCModuleFactory(ModuleGraph graph, ModuleManager<ISCFilterModule, ISCProcessorModule> manager) {
		super(graph, manager);
	}

	ISCFilterModule[] NO_FILTERS = new ISCFilterModule[0];
	ISCProcessorModule[] NO_PROCESSORS = new ISCProcessorModule[0];

	public ISCFilterModule[] getFilterModules(IModuleType<? extends ISCProcessorModule> parent) {
		List<ModuleDesc<? extends ISCFilterModule>> filters = filterMap.get(parent);
		if (filters == null)
			return NO_FILTERS;
		int size = filters.size();
		ISCFilterModule[] filterModules = new ISCFilterModule[size];
		for (int k=0; k<size; k++) {
			filterModules[k] = filters.get(k).createInstance();
		}
		return filterModules;
	}

	public ISCProcessorModule[] getProcessorModules(IModuleType<? extends ISCProcessorModule> parent) {
		List<ModuleDesc<? extends ISCProcessorModule>> processors = processorMap.get(parent);
		if (processors == null)
			return NO_PROCESSORS;
		int size = processors.size();
		ISCProcessorModule[] processorModules = new ISCProcessorModule[size];
		for (int k=0; k<size; k++) {
			processorModules[k] = processors.get(k).createInstance();
		}
		return processorModules;
	}

}
