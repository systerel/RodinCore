/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool;

import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eventb.internal.core.tool.graph.ModuleGraph;
import org.eventb.internal.core.tool.graph.Node;
import org.eventb.internal.core.tool.graph.ProcessorModuleNode;
import org.eventb.internal.core.tool.types.IModule;
import org.eventb.internal.core.tool.types.IProcessorModule;

/**
 * @author Stefan Hallerstede
 *
 */
public class ProcessorModuleDesc<T extends IProcessorModule> extends ModuleDesc<T> {

	/**
	 * Creates a new processor decription.
	 * 
	 * @param configElement
	 *            description of this extractor in the Eclipse registry
	 * @throws ModuleLoadingException 
	 */
	public ProcessorModuleDesc(IConfigurationElement configElement) throws ModuleLoadingException {
		super(configElement);
	}

	@Override
	public Node<ModuleDesc<? extends IModule>> createNode(ModuleGraph graph) {
		return new ProcessorModuleNode(this, getId(), getPrereqs(), graph);
	}

	@Override
	public void addToModuleFactory(
			ModuleFactory factory, 
			Map<String, ModuleDesc<? extends IModule>> modules) {
		ModuleDesc<? extends IModule> parent = modules.get(getParent());
		factory.addProcessorToFactory(parent, this);
	}

}
