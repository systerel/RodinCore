/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool;

import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.tool.state.IToolState;
import org.eventb.core.tool.state.IToolStateType;
import org.osgi.framework.Bundle;

/**
 * @author Stefan Hallerstede
 *
 */
public class ToolStateType<T extends IToolState> implements IToolStateType<T> {

	// Name of the plugin which contributes this element type
	private final String bundleName;

	// Name of the class implementing elements of this element type
	private final String className;

	// Class implementing elements of this element type
	// (cached value)
	protected Class<? extends T> classObject;

	// Unique identifier of this element type
	protected final String id;
	
	// Human-readable name of this element type
	protected final String name;
	
	private static String readId(IConfigurationElement configElement) {
		String nameSpace = configElement.getNamespaceIdentifier();
		return nameSpace + "." + configElement.getAttribute("id");
	}
	
	private static String readName(IConfigurationElement configElement) {
		return configElement.getAttribute("name");
	}

	private static final HashMap<String, ToolStateType<? extends IToolState>>
		registry = new HashMap<String, ToolStateType<? extends IToolState>>();

	private static void register(String id, ToolStateType<? extends IToolState> type) {
		final ToolStateType<? extends IToolState> oldType = registry.put(id, type);
		if (oldType != null) {
			registry.put(id, oldType);
			throw new IllegalStateException(
					"Attempt to create twice element type " + id);
		}
	}
	
	public static IToolStateType<? extends IToolState> getElementType(String id) {
		return registry.get(id);
	}
	
	public ToolStateType(IConfigurationElement configurationElement) {
		this.id = readId(configurationElement);
		this.name = readName(configurationElement);
		register(id, this);
		this.bundleName = configurationElement.getContributor().getName();
		this.className = configurationElement.getAttribute("class");
		computeClass();
	}

	@SuppressWarnings("unchecked")
	protected void computeClass() {
		Bundle bundle = Platform.getBundle(getBundleName());
		try {
			Class<?> clazz = bundle.loadClass(getClassName());
			classObject = (Class<? extends T>) clazz.asSubclass(IToolState.class);
		} catch (Exception e) {
			throw new IllegalStateException(
					"Class is not a tool state: " + getId(), e);
		}
	}
	
	public final String getId() {
		return id;
	}

	public final String getName() {
		return name;
	}

	@Override
	public final String toString() {
		return id;
	}
	
	protected String getBundleName() {
		return bundleName;
	}

	protected String getClassName() {
		return className;
	}

}
