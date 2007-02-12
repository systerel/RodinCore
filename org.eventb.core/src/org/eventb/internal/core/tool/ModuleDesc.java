/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core.tool;

import java.lang.reflect.Constructor;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eventb.core.tool.IFilterModule;
import org.eventb.core.tool.IModule;
import org.eventb.core.tool.IModuleType;
import org.eventb.core.tool.IProcessorModule;
import org.eventb.internal.core.tool.graph.Node;

/**
 * Description of a module (filter or processor) registered with
 * the module manager.
 * <p>
 * Implements lazy class loading for the executable extension.
 * </p>
 * 
 * @author Laurent Voisin
 * @author Stefan Hallerstede
 */
public abstract class ModuleDesc<T extends IModule> extends BasicDescWithClass implements IModuleType<T> {

	// parent processor module, or <code>null</code> if this is a root module
	private final String parent;
	
	// Unique ids of modules that are required to be executed before this module 
	private final String[] prereqs;
	
	// Class implementing this module
	// (cached value)
	protected Class<? extends T> classObject;

	// Constructor to use to create modules
	// (cached value)
	protected Constructor<? extends T> constructor;

	/**
	 * Creates a new module decription.
	 * 
	 * @param configElement
	 *            description of this module in the Eclipse registry
	 */
	public ModuleDesc(IConfigurationElement configElement) {
		super(configElement);
		this.parent = configElement.getAttribute("parent");
		IConfigurationElement[] prereqElements = configElement.getChildren("prereq");
		prereqs = new String[prereqElements.length];
		for (int i=0; i<prereqElements.length; i++) {
			prereqs[i] = prereqElements[i].getAttribute("id");
		}
	}
	
	// support for graph analysis
	public abstract Node<ModuleDesc<? extends IModule>> createNode();

	protected abstract void computeClass();

	protected void computeConstructor() {
		if (classObject == null) {
			computeClass();
		}
		try {
			constructor = classObject.getConstructor();
		} catch (Exception e) {
			throw new IllegalStateException(
					"Can't find constructor for element type " + getId(), e);
		}
	}
	
	public T createInstance() {
		if (constructor == null) {
			computeConstructor();
		}
		if (constructor == null) {
			return null;
		}
		try {
			return constructor.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(
					"Can't create module " + getId(), e);
		}
	}

	public String[] getPrereqs() {
		return prereqs;
	}

	public String getParent() {
		return parent;
	}
	
	public abstract <FM extends IFilterModule, PM extends IProcessorModule> 
	void addToModuleFactory(ModuleFactory<FM, PM> factory, ModuleManager<FM, PM> manager);

}
