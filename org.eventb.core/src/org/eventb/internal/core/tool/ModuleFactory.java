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
import org.eventb.core.tool.IProcessorModule;
import org.eventb.internal.core.tool.graph.ModuleGraph;
import org.eventb.internal.core.tool.graph.Node;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ModuleFactory<FM extends IFilterModule, PM extends IProcessorModule> 
implements IModuleFactory<FM, PM>{
	
	protected List<ModuleDesc<? extends PM>> rootList;
	protected Map<ModuleDesc<? extends PM>, List<ModuleDesc<? extends FM>>> filterMap;
	protected Map<ModuleDesc<? extends PM>, List<ModuleDesc<? extends PM>>> processorMap;

	protected <F extends FM> void addFilterToFactory(
			ModuleDesc<? extends PM> key, 
			ModuleDesc<? extends FM> filter) {
		List<ModuleDesc<? extends FM>> filters = filterMap.get(key);
		if (filters == null) {
			filters = new ArrayList<ModuleDesc<? extends FM>>();
		}
		filters.add(filter);
	}
	
	protected <F extends FM> void addProcessorToFactory(
			ModuleDesc<? extends PM> key, 
			ModuleDesc<? extends PM> processor) {
		List<ModuleDesc<? extends PM>> processors = processorMap.get(key);
		if (processors == null) {
			processors = new ArrayList<ModuleDesc<? extends PM>>();
		}
		processors.add(processor);
	}
	
	public ModuleFactory(ModuleGraph graph, ModuleManager<FM, PM> manager) {
		filterMap = new HashMap<ModuleDesc<? extends PM>, List<ModuleDesc<? extends FM>>>();
		processorMap = new HashMap<ModuleDesc<? extends PM>, List<ModuleDesc<? extends PM>>>();
		for (Node<ModuleDesc<? extends IModule>> node : graph.getSorted())
			node.getObject().addToModuleFactory(this, manager);
	}
}
