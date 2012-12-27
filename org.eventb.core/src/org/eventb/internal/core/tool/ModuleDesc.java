/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.tool;

import java.lang.reflect.Constructor;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.tool.graph.ModuleGraph;
import org.eventb.internal.core.tool.graph.Node;
import org.eventb.internal.core.tool.types.IModule;
import org.osgi.framework.Bundle;

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
	private Class<? extends T> classObject;

	// Constructor to use to create modules
	// (cached value)
	protected Constructor<? extends T> constructor;

	/**
	 * Creates a new module decription.
	 * 
	 * @param configElement
	 *            description of this module in the Eclipse registry
	 * @throws ModuleLoadingException 
	 */
	public ModuleDesc(IConfigurationElement configElement) throws ModuleLoadingException {
		super(configElement);
		this.parent = getAttribute(configElement, "parent");
		IConfigurationElement[] prereqElements = getChildren(configElement, "prereq");
		prereqs = new String[prereqElements.length];
		for (int i=0; i<prereqElements.length; i++) {
			prereqs[i] = getAttribute(prereqElements[i], "id");
		}
	}
	
	// support for graph analysis
	public abstract Node<ModuleDesc<? extends IModule>> createNode(ModuleGraph graph);

	@SuppressWarnings("unchecked")
	protected void computeClass() throws ModuleLoadingException {
		Bundle bundle = Platform.getBundle(getBundleName());
		try {
			Class<?> clazz = bundle.loadClass(getClassName());
			classObject = (Class<? extends T>) clazz.asSubclass(IModule.class);
		} catch (Throwable e) {
			throw new ModuleLoadingException(e);
		} 
	}
	
	protected Class<? extends T> getClassObject() throws ModuleLoadingException {
		if (classObject == null) {
			computeClass();
		}
		return classObject;
	}

	protected void computeConstructor() throws ModuleLoadingException {
		if (classObject == null) {
			computeClass();
		}
		try {
			constructor = classObject.getConstructor();
		} catch (Throwable e) {
			throw new ModuleLoadingException(e);
		}
	}
	
	public T createInstance() throws ModuleLoadingException {
		if (constructor == null) {
			computeConstructor();
		}
		if (constructor == null) {
			return null;
		}
		try {
			return constructor.newInstance();
		} catch (Throwable e) {
			throw new ModuleLoadingException(e);
		}
	}

	public String[] getPrereqs() {
		return prereqs;
	}

	public String getParent() {
		return parent;
	}
	
	public abstract void addToModuleFactory(
			ModuleFactory factory, 
			Map<String, ModuleDesc<? extends IModule>> modules);

}
