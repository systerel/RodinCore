/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - no exception when root module not found
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
import org.eventb.internal.core.Util;
import org.eventb.internal.core.tool.BasicDesc.ModuleLoadingException;
import org.eventb.internal.core.tool.graph.ModuleGraph;
import org.eventb.internal.core.tool.graph.Node;
import org.rodinp.core.IInternalElementType;

/**
 * @author Stefan Hallerstede
 *
 */
public class ModuleFactory implements IModuleFactory {
	
	protected final Map<ModuleDesc<?>, List<ModuleDesc<? extends IFilterModule>>> filterMap = new HashMap<ModuleDesc<?>, List<ModuleDesc<? extends IFilterModule>>>();
	protected final Map<ModuleDesc<?>, List<ModuleDesc<? extends IProcessorModule>>> processorMap = new HashMap<ModuleDesc<?>, List<ModuleDesc<? extends IProcessorModule>>>();
	private final Map<IInternalElementType<?>, ModuleDesc<?>> rootMap = new HashMap<IInternalElementType<?>, ModuleDesc<?>>();
	
	protected void addFilterToFactory(
			ModuleDesc<?> key, 
			ModuleDesc<? extends IFilterModule> filter) {
		addModuleToFactory(filterMap, key, filter);
	}
	
	protected void addProcessorToFactory(
			ModuleDesc<?> key, 
			ModuleDesc<? extends IProcessorModule> processor) {
		addModuleToFactory(processorMap, key, processor);
	}

	private static <T extends IModule> void addModuleToFactory(
			Map<ModuleDesc<?>, List<ModuleDesc<? extends T>>> map, ModuleDesc<?> key,
			ModuleDesc<? extends T> desc) {
		List<ModuleDesc<? extends T>> descs = map.get(key);
		if (descs == null) {
			descs = new ArrayList<ModuleDesc<? extends T>>();
			map.put(key, descs);
		}
		descs.add(desc);

	}
	
	protected void addRootToFactory(
			IInternalElementType<?> key, 
			ModuleDesc<?> root) {
		ModuleDesc<?> oldRoot = rootMap.put(key, root);
		if (oldRoot != null)
			throw new IllegalStateException("Non-unique root module for file element " + key.getId());
	}
	
	public ModuleFactory(ModuleGraph graph, Map<String, ModuleDesc<?>> modules) {
		for (Node<ModuleDesc<?>> node : graph.getSorted())
			node.getObject().addToModuleFactory(this, modules);
	}
	
	private static final IFilterModule[] NO_FILTERS = new IFilterModule[0];
	private static final IProcessorModule[] NO_PROCESSORS = new IProcessorModule[0];
	
	@Override
	public IFilterModule[] getFilterModules(IModuleType<?> parent) {
		List<IFilterModule> list = getModules(filterMap, parent, "filter");
		return list.toArray(NO_FILTERS);
	}

	@Override
	public IProcessorModule[] getProcessorModules(IModuleType<?> parent) {
		List<IProcessorModule> list = getModules(processorMap, parent, "processor");
		return list.toArray(NO_PROCESSORS);
	}

	private <T extends IModule> List<T> getModules(
			Map<ModuleDesc<?>, List<ModuleDesc<? extends T>>> map,
			IModuleType<?> parent, String kind) {
		final List<T> result = new ArrayList<T>();
		final List<ModuleDesc<? extends T>> descs = map.get(parent);
		if (descs == null)
			return result;
		for (ModuleDesc<? extends T> desc : descs) {
			try {
				final T instance = desc.createInstance();
				setModuleFactory(instance);
				result.add(instance);
			} catch (ModuleLoadingException e) {
				// ignore module
				Util.log(e.getCause(), " while getting "
						+ kind
						+ " module "
						+ desc.getId());
			}
		}
		return result;
	}
	
	private void setModuleFactory(IModule module) {
		if (module instanceof Module) {
			Module mm = (Module) module;
			mm.setModuleFactory(this);
		}
	}

	@Override
	public IProcessorModule getRootModule(IInternalElementType<?> type) {
		ModuleDesc<?> desc = rootMap.get(type);
		if (desc == null)
			// may be a configuration from a missing plug-in
			return null;
		try {
			final IProcessorModule module = (IProcessorModule) desc.createInstance();
			setModuleFactory(module);
			return module;
		} catch (ModuleLoadingException e) {
			return null;
		}
	}
	
	// for testing purposes
	private List<String> postfixOrder(ModuleDesc<?> root) {
		List<String> ids = new LinkedList<String>();
		ids.addAll(postfixList(root, filterMap));
		ids.addAll(postfixList(root, processorMap));
		ids.add(root.getId());
		return ids;
	}

	private <T extends IModule> List<String> postfixList(
			ModuleDesc<?> root, 
			Map<ModuleDesc<?>, List<ModuleDesc<? extends T>>> map) {
		List<String> mIds = new LinkedList<String>();
		List<ModuleDesc<? extends T>> modules = map.get(root);
		if (modules == null)
			return mIds;
		for (ModuleDesc<?> module : modules) {
			mIds.addAll(postfixOrder(module));
		}
		return mIds;
	}
	
	public String toString(ModuleDesc<?> root) {
		return postfixOrder(root).toString();
	}
	
	// debugging support
	@Override
	public String printModuleTree(IInternalElementType<?> type) {
		StringBuffer buffer = new StringBuffer();
		ModuleDesc<?> desc = rootMap.get(type);
		if (desc == null) {
			return "Module-tree look-up failed!\n";
		}
		printModuleTree(buffer, desc, 0, 'R');
		return buffer.toString();
	}

	private void printModuleTree(
			StringBuffer buffer, 
			ModuleDesc<?> desc, 
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

	private <T extends IModule> void printModuleTree(
			StringBuffer buffer, 
			List<ModuleDesc<? extends T>> descs, 
			int offset, 
			char type) {
		if (descs == null)
			return;
		for (ModuleDesc<?> desc : descs)
			printModuleTree(buffer, desc, offset, type);
	}

}
