/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added getUnknownConfigIds()
 *******************************************************************************/
package org.eventb.internal.core.tool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.EventBPlugin;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.tool.BasicDesc.ModuleLoadingException;
import org.eventb.internal.core.tool.graph.ModuleGraph;
import org.eventb.internal.core.tool.graph.ParentGraph;
import org.eventb.internal.core.tool.types.IFilterModule;
import org.eventb.internal.core.tool.types.IModule;
import org.eventb.internal.core.tool.types.IProcessorModule;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ModuleManager extends SortingUtil {
	
	private static final String ROOT_TYPE = "rootType";

	private static final String FILTER_TYPE = "filterType";

	private static final String PROCESSOR_TYPE = "processorType";

	// Debug flag set from tracing options 
	public static boolean VERBOSE = false;

	// Local id of the state types extension point of this plugin
	private final String modules_id;
	
	// Access to modules using their unique id
	// This table contains all filter and processor modules
	private HashMap<String, ModuleDesc<? extends IModule>> modules;

	private void register(String id, ModuleDesc<? extends IModule> desc) throws ModuleLoadingException {
		final ModuleDesc<? extends IModule> oldType = modules.put(id, desc);
		if (oldType != null) {
			modules.put(id, oldType);
			throw new ModuleLoadingException(new IllegalStateException(
					"Attempt to create twice module type " + id));
		}
	}
	
	private void computeModules() {
		modules =
			new HashMap<String, ModuleDesc<? extends IModule>>();
		
		// Read the extension point extensions.
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = 
			registry.getConfigurationElementsFor(EventBPlugin.PLUGIN_ID, modules_id);
		
		loadModules(elements);		
		
		if (VERBOSE) {
			System.out.println("---------------------------------------------------");
			System.out.println(modules_id + " registered:");
			for (String id: getSortedIds(modules)) {
				ModuleDesc<? extends IModule> type = modules.get(id);
				System.out.println("  " + type.getId());
				System.out.println("    name: " + type.getName());
				System.out.println("    class: " + type.getClassName());
			}
			System.out.println("---------------------------------------------------");
		}
	}
	
	protected abstract List<ModuleDesc<? extends IModule>> getModuleListForConfig(String configId);

	public abstract List<String> getUnknownConfigIds(String configIds);
	
	protected abstract void verifyProcessor(ProcessorModuleDesc<? extends IProcessorModule> moduleDesc) throws ModuleLoadingException;
	
	protected abstract void verifyFilter(FilterModuleDesc<? extends IFilterModule> moduleDesc) throws ModuleLoadingException;
	
	protected abstract String getName();

	private void loadModules(IConfigurationElement[] elements) {
		
		for (IConfigurationElement element: elements) {
			String name = element.getName();
			try {
				if (name.equals(FILTER_TYPE)) {
					registerFilterModule(element);
				} else if (name.equals(PROCESSOR_TYPE)) {
					registerProcessorModule(element);
				} else if (name.equals(ROOT_TYPE)) { 
					registerRootModule(element);
				} else {
					Util.log(null, "Unknown module declaration: " + name);
				}
			} catch (ModuleLoadingException e) {
				Util.log(e.getCause(), " while loading module "
						+ element.getName()
						+ " in "
						+ element.getNamespaceIdentifier());
				// ignore module
			}
		}
	}

	private void registerRootModule(IConfigurationElement element)
			throws ModuleLoadingException {
		RootModuleDesc<IProcessorModule> root =
			new RootModuleDesc<IProcessorModule>(element);
		verifyProcessor(root);
		register(root.getId(), root);
	}

	private void registerProcessorModule(IConfigurationElement element)
			throws ModuleLoadingException {
		ProcessorModuleDesc<? extends IProcessorModule> processor = 
			new ProcessorModuleDesc<IProcessorModule>(element);
		verifyProcessor(processor);
		register(processor.getId(), processor);
	}

	private void registerFilterModule(IConfigurationElement element)
			throws ModuleLoadingException {
		FilterModuleDesc<? extends IFilterModule> filter = 
			new FilterModuleDesc<IFilterModule>(element);
		verifyFilter(filter);
		register(filter.getId(), filter);
	}

	/**
	 * Returns the module desc with the given id.
	 * 
	 * @param id
	 *            the id of the module desc to retrieve
	 * @return the module desc or <code>null</code> if this
	 *         module desc id is unknown.
	 */
	public ModuleDesc<? extends IModule> getModuleDesc(String id) {

		if (modules == null) {
			computeModules();
		}
		return modules.get(id);
	}

	protected ModuleManager(final String modules_id) {
		this.modules_id = modules_id;
		factoryMap = new HashMap<String, ModuleFactory>();
	}
	
	Map<String, ModuleFactory> factoryMap;

	public IModuleFactory getModuleFactory(String configId) {
		
		ModuleFactory factory = factoryMap.get(configId);
		if (factory != null)
			return factory;
		
		List<ModuleDesc<? extends IModule>> moduleList = getModuleListForConfig(configId);
		ModuleGraph moduleGraph = sortModules(moduleList, getName());
		
		factory = computeModuleFactory(moduleGraph);
		factoryMap.put(configId, factory);
		return factory;
	}

	// this method is part of the testing interface 
	public static ModuleGraph sortModules(List<ModuleDesc<? extends IModule>> moduleList, String creator) {
		ParentGraph parentGraph = new ParentGraph(creator);
		ModuleGraph moduleGraph = new ModuleGraph(creator);
		for (ModuleDesc<? extends IModule> moduleDesc : moduleList) {
			parentGraph.add(moduleDesc);
			moduleGraph.add(moduleDesc);
		}
		parentGraph.analyse();
		moduleGraph.analyse(parentGraph);
		return moduleGraph;
	}

	protected ModuleFactory computeModuleFactory(ModuleGraph moduleGraph) {
		return new ModuleFactory(moduleGraph, modules);
	}
}
