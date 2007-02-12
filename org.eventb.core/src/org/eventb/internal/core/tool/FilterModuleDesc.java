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
import org.eventb.internal.core.tool.graph.FilterModuleNode;
import org.eventb.internal.core.tool.graph.Node;
import org.osgi.framework.Bundle;

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
		assert getParent() != null;
	}
	
	@Override
	protected void computeClass() {
		Bundle bundle = Platform.getBundle(getBundleName());
		try {
			Class<?> clazz = bundle.loadClass(getClassName());
			classObject = (Class<? extends T>) clazz.asSubclass(IFilterModule.class);
		} catch (Exception e) {
			throw new IllegalStateException(
					"Cannot load filter module class " + getId(), e);
		}
	}

	@Override
	public Node<ModuleDesc<? extends IModule>> createNode() {
		return new FilterModuleNode(this, getId(), getPrereqs());
	}

	@Override
	public <FM extends IFilterModule, PM extends IProcessorModule> 
	void addToModuleFactory(ModuleFactory<FM, PM> factory, ModuleManager<FM, PM> manager) {
		ModuleDesc<? extends PM> parent = (ModuleDesc<? extends PM>) manager.getModuleDesc(getParent());
		ModuleDesc<? extends FM> filter = (ModuleDesc<? extends FM>) this;
		factory.addFilterToFactory(parent, filter);
	}
	
}
