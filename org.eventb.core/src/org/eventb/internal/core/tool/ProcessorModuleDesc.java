/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.tool.IFilterModule;
import org.eventb.core.tool.IModule;
import org.eventb.core.tool.IProcessorModule;
import org.eventb.internal.core.tool.graph.Node;
import org.eventb.internal.core.tool.graph.ProcessorModuleNode;
import org.osgi.framework.Bundle;

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

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.tool.ModuleDesc#computeClass()
	 */
	@Override
	protected void computeClass() {
		Bundle bundle = Platform.getBundle(getBundleName());
		try {
			Class<?> clazz = bundle.loadClass(getClassName());
			classObject = (Class<? extends T>) clazz.asSubclass(IFilterModule.class);
		} catch (Exception e) {
			throw new IllegalStateException(
					"Cannot load processor module class " + getId(), e);
		}
	}

	@Override
	public Node<ModuleDesc<? extends IModule>> createNode() {
		return new ProcessorModuleNode(this, getId(), getPrereqs());
	}

	@Override
	public <FM extends IFilterModule, PM extends IProcessorModule> 
	void addToModuleFactory(ModuleFactory<FM, PM> factory, ModuleManager<FM, PM> manager) {
		ModuleDesc<? extends PM> parent = (ModuleDesc<? extends PM>) manager.getModuleDesc(getParent());
		ModuleDesc<? extends PM> filter = (ModuleDesc<? extends PM>) this;
		factory.addProcessorToFactory(parent, filter);
	}

}
