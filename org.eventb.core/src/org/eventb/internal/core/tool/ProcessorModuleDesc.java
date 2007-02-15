/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eventb.core.tool.IModule;
import org.eventb.core.tool.IProcessorModule;
import org.eventb.internal.core.tool.graph.Node;
import org.eventb.internal.core.tool.graph.ProcessorModuleNode;

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
	 */
	public ProcessorModuleDesc(IConfigurationElement configElement) {
		super(configElement);
	}

	@Override
	public Node<ModuleDesc<? extends IModule>> createNode() {
		return new ProcessorModuleNode(this, getId(), getPrereqs());
	}

	@Override
	public void addToModuleFactory(ModuleFactory factory, ModuleManager manager) {
		ModuleDesc<? extends IModule> parent = manager.getModuleDesc(getParent());
		factory.addProcessorToFactory(parent, this);
	}

}
