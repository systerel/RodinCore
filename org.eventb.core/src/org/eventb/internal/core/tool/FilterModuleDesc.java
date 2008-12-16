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
import org.eventb.internal.core.tool.graph.FilterModuleNode;
import org.eventb.internal.core.tool.graph.ModuleGraph;
import org.eventb.internal.core.tool.graph.Node;
import org.eventb.internal.core.tool.types.IFilterModule;
import org.eventb.internal.core.tool.types.IModule;

/**
 * Description of an extractor registered with the tool manager.
 * <p>
 * Implements lazy class loading for the extractor.
 * </p>
 * 
 * @author Stefan Hallerstede
 */
public class FilterModuleDesc<T extends IFilterModule> extends ModuleDesc<T> {
	
	/**
	 * Creates a new filter decription.
	 * 
	 * @param configElement
	 *            description of this extractor in the Eclipse registry
	 * @throws ModuleLoadingException 
	 */
	public FilterModuleDesc(IConfigurationElement configElement) throws ModuleLoadingException {
		super(configElement);
	}
	
	@Override
	public Node<ModuleDesc<? extends IModule>> createNode(ModuleGraph graph) {
		if (getParent() == null)
			throw new IllegalStateException("filter module without parent " + getId());

		return new FilterModuleNode(this, getId(), getPrereqs(), graph);
	}

	@Override
	public void addToModuleFactory(
			ModuleFactory factory, 
			Map<String, ModuleDesc<? extends IModule>> modules) {
		ModuleDesc<? extends IModule> parent = modules.get(getParent());
		factory.addFilterToFactory(parent, this);
	}
	
}
