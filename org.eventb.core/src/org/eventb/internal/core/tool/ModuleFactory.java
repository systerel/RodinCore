/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.tool.graph.ModuleGraph;
import org.eventb.internal.core.tool.graph.Node;
import org.eventb.internal.core.tool.types.IFilterModule;
import org.eventb.internal.core.tool.types.IModule;
import org.eventb.internal.core.tool.types.IProcessorModule;
import org.rodinp.core.IInternalElementType;

/**
 * @author Stefan Hallerstede
 *
 */
public class ModuleFactory implements IModuleFactory {
	
	protected Map<ModuleDesc<? extends IModule>, List<ModuleDesc<? extends IModule>>> filterMap;
	protected Map<ModuleDesc<? extends IModule>, List<ModuleDesc<? extends IModule>>> processorMap;
	private Map<IInternalElementType<?>, ModuleDesc<? extends IModule>> rootMap;

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
	
	protected void addRootToFactory(
			IInternalElementType<?> key, 
			ModuleDesc<? extends IModule> root) {
		ModuleDesc<? extends IModule> oldRoot = rootMap.put(key, root);
		if (oldRoot != null)
			throw new IllegalStateException("Non-unique root module for file element " + key.getId());
	}
	
	public ModuleFactory(ModuleGraph graph, Map<String, ModuleDesc<? extends IModule>> modules) {
		filterMap = 
			new HashMap<ModuleDesc<? extends IModule>, List<ModuleDesc<? extends IModule>>>();
		processorMap = 
			new HashMap<ModuleDesc<? extends IModule>, List<ModuleDesc<? extends IModule>>>();
		rootMap = 
			new HashMap<IInternalElementType<?>, ModuleDesc<? extends IModule>>();
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

	public IProcessorModule getRootModule(IInternalElementType<?> type) {
		ModuleDesc<? extends IModule> desc = rootMap.get(type);
		if (desc == null)
			throw new IllegalArgumentException("No root module for " + type.getId());
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
	
	// debugging support
	public String printModuleTree(IInternalElementType<?> type) {
		StringBuffer buffer = new StringBuffer();
		ModuleDesc<? extends IModule> desc = rootMap.get(type);
		if (desc == null) {
			return "Module-tree look-up failed!\n";
		}
		printModuleTree(buffer, desc, 0, 'R');
		return buffer.toString();
	}

	private void printModuleTree(
			StringBuffer buffer, 
			ModuleDesc<? extends IModule> desc, 
			int offset,
			char type) {
		
		for (int i=0; i<offset; i++)
			buffer.append(' ');
		buffer.append(type);
		buffer.append(desc.getId());
		buffer.append('\n');
		
		offset += 2;
		
		printModuleTree(buffer, filterMap.get(desc), offset, 'F');
		printModuleTree(buffer, processorMap.get(desc), offset, 'P');
	}

	private void printModuleTree(
			StringBuffer buffer, 
			List<ModuleDesc<? extends IModule>> descs, 
			int offset, 
			char type) {
		if (descs == null)
			return;
		for (ModuleDesc<? extends IModule> desc : descs)
			printModuleTree(buffer, desc, offset, type);
	}

}
