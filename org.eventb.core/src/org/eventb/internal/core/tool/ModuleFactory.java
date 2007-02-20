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
import java.util.LinkedList;
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
			filterMap.put(key, filters);
		}
		filters.add(filter);
	}
	
	protected void addProcessorToFactory(
			ModuleDesc<? extends IModule> key, 
			ModuleDesc<? extends IModule> processor) {
		List<ModuleDesc<? extends IModule>> processors = processorMap.get(key);
		if (processors == null) {
			processors = new ArrayList<ModuleDesc<? extends IModule>>();
			processorMap.put(key, processors);
		}
		processors.add(processor);
	}
	
	public ModuleFactory(ModuleGraph graph, Map<String, ModuleDesc<? extends IModule>> modules) {
		filterMap = 
			new HashMap<ModuleDesc<? extends IModule>, List<ModuleDesc<? extends IModule>>>();
		processorMap = 
			new HashMap<ModuleDesc<? extends IModule>, List<ModuleDesc<? extends IModule>>>();
		for (Node<ModuleDesc<? extends IModule>> node : graph.getSorted())
			node.getObject().addToModuleFactory(this, modules);
	}
	
	IFilterModule[] NO_FILTERS = new IFilterModule[0];
	IProcessorModule[] NO_PROCESSORS = new IProcessorModule[0];

	public IFilterModule[] getFilterModules(IModuleType<? extends IModule> parent) {
		List<ModuleDesc<? extends IModule>> filters = filterMap.get(parent);
		if (filters == null)
			return NO_FILTERS;
		int size = filters.size();
		IFilterModule[] filterModules = new IFilterModule[size];
		for (int k=0; k<size; k++) {
			filterModules[k] = (IFilterModule) filters.get(k).createInstance();
			setModuleFactory(filterModules[k]);
		}
		return filterModules;
	}

	public IProcessorModule[] getProcessorModules(IModuleType<? extends IModule> parent) {
		List<ModuleDesc<? extends IModule>> processors = processorMap.get(parent);
		if (processors == null)
			return NO_PROCESSORS;
		int size = processors.size();
		IProcessorModule[] processorModules = new IProcessorModule[size];
		for (int k=0; k<size; k++) {
			processorModules[k] = (IProcessorModule) processors.get(k).createInstance();
			setModuleFactory(processorModules[k]);
		}
		return processorModules;
	}
	
	private void setModuleFactory(IModule module) {
		if (module instanceof Module) {
			Module mm = (Module) module;
			mm.setModuleFactory(this);
		}
	}

	public IProcessorModule getRootModule(IModuleType<? extends IModule> type) {
		ModuleDesc<? extends IModule> desc = (ModuleDesc<? extends IModule>) type;
		if (desc.getParent() != null)
			throw new IllegalArgumentException("Not a root module " + type.getId());
		IProcessorModule module = (IProcessorModule) desc.createInstance();
		setModuleFactory(module);
		return module;
	}
	
	// for testing purposes
	private List<String> postfixOrder(ModuleDesc<? extends IModule> root) {
		List<String> ids = new LinkedList<String>();
		ids.addAll(postfixList(root, filterMap));
		ids.addAll(postfixList(root, processorMap));
		ids.add(root.getId());
		return ids;
	}

	private List<String> postfixList(
			ModuleDesc<? extends IModule> root, 
			Map<ModuleDesc<? extends IModule>, List<ModuleDesc<? extends IModule>>> map) {
		List<String> mIds = new LinkedList<String>();
		List<ModuleDesc<? extends IModule>> modules = map.get(root);
		if (modules == null)
			return mIds;
		for (ModuleDesc<? extends IModule> module : modules) {
			mIds.addAll(postfixOrder(module));
		}
		return mIds;
	}
	
	public String toString(ModuleDesc<? extends IModule> root) {
		return postfixOrder(root).toString();
	}

}
