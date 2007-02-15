/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.tool.IFilterModule;
import org.eventb.core.tool.IModule;
import org.eventb.core.tool.IModuleType;
import org.eventb.core.tool.IProcessorModule;
import org.eventb.internal.core.tool.graph.ModuleGraph;
import org.eventb.internal.core.tool.graph.Node;

/**
 * @author Stefan Hallerstede
 *
 */
public class ModuleFactory implements IModuleFactory {
	
	protected Map<ModuleDesc<? extends IModule>, List<ModuleDesc<? extends IModule>>> filterMap;
	protected Map<ModuleDesc<? extends IModule>, List<ModuleDesc<? extends IModule>>> processorMap;

	protected void addFilterToFactory(
			ModuleDesc<? extends IModule> key, 
			ModuleDesc<? extends IModule> filter) {
		List<ModuleDesc<? extends IModule>> filters = filterMap.get(key);
		if (filters == null) {
			filters = new ArrayList<ModuleDesc<? extends IModule>>();
		}
		filters.add(filter);
	}
	
	protected void addProcessorToFactory(
			ModuleDesc<? extends IModule> key, 
			ModuleDesc<? extends IModule> processor) {
		List<ModuleDesc<? extends IModule>> processors = processorMap.get(key);
		if (processors == null) {
			processors = new ArrayList<ModuleDesc<? extends IModule>>();
		}
		processors.add(processor);
	}
	
	public ModuleFactory(ModuleGraph graph, ModuleManager manager) {
		filterMap = 
			new HashMap<ModuleDesc<? extends IModule>, List<ModuleDesc<? extends IModule>>>();
		processorMap = 
			new HashMap<ModuleDesc<? extends IModule>, List<ModuleDesc<? extends IModule>>>();
		for (Node<ModuleDesc<? extends IModule>> node : graph.getSorted())
			node.getObject().addToModuleFactory(this, manager);
	}
	
	IFilterModule[] NO_FILTERS = new IFilterModule[0];
	IProcessorModule[] NO_PROCESSORS = new IProcessorModule[0];

	public IFilterModule[] getFilterModules(IModuleType<? extends IProcessorModule> parent) {
		List<ModuleDesc<? extends IModule>> filters = filterMap.get(parent);
		if (filters == null)
			return NO_FILTERS;
		int size = filters.size();
		IFilterModule[] filterModules = new IFilterModule[size];
		for (int k=0; k<size; k++) {
			filterModules[k] = (IFilterModule) filters.get(k).createInstance();
		}
		return filterModules;
	}

	public IProcessorModule[] getProcessorModules(IModuleType<? extends IProcessorModule> parent) {
		List<ModuleDesc<? extends IModule>> processors = processorMap.get(parent);
		if (processors == null)
			return NO_PROCESSORS;
		int size = processors.size();
		IProcessorModule[] processorModules = new IProcessorModule[size];
		for (int k=0; k<size; k++) {
			processorModules[k] = (IProcessorModule) processors.get(k).createInstance();
		}
		return processorModules;
	}

}
