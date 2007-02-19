/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core.tool;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eventb.core.tool.IFilterModule;
import org.eventb.core.tool.IModule;
import org.eventb.internal.core.tool.graph.FilterModuleNode;
import org.eventb.internal.core.tool.graph.Node;

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
	 */
	public FilterModuleDesc(IConfigurationElement configElement) {
		super(configElement);
	}
	
	@Override
	public Node<ModuleDesc<? extends IModule>> createNode() {
		if (getParent() == null)
			throw new IllegalStateException("filter module without parent " + getId());

		return new FilterModuleNode(this, getId(), getPrereqs());
	}

	@Override
	public void addToModuleFactory(ModuleFactory factory, ModuleManager manager) {
		ModuleDesc<? extends IModule> parent = manager.getModuleDesc(getParent());
		factory.addFilterToFactory(parent, this);
	}
	
}
