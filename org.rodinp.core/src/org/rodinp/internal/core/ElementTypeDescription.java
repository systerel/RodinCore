/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.internal.core;

import java.lang.reflect.Constructor;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.RodinElement;

/**
 * Implements the internal description of an element type contributed by a plugin.
 * 
 * @author Laurent Voisin
 */
public abstract class ElementTypeDescription<T extends RodinElement> {

	// Name of the plugin which contributes this element type
	private final String bundleName;

	// Name of the class implementing elements of this element type
	private final String className;

	// Class implementing elements of this element type
	// (cached value)
	protected Class<? extends T> classObject;

	// Constructor to use to create elements of this element type
	// (cached value)
	protected Constructor<? extends T> constructor;

	// Unique identifier of this element type (fully qualified identifier)
	private final String id;

	// Human-readable name of this element type
	private final String name;

	public ElementTypeDescription(IConfigurationElement configurationElement) {
		this.bundleName = configurationElement.getNamespace();
		this.id = configurationElement.getAttributeAsIs("id");
		this.name = configurationElement.getAttribute("name");
		this.className = configurationElement.getAttributeAsIs("class");
	}

	protected abstract void computeConstructor();

	public String getBundleName() {
		return bundleName;
	}

	public String getClassName() {
		return className;
	}

	public Class<? extends T> getClassObject() {
		if (classObject == null) {
			computeConstructor();
		}
		return classObject;
	}

	public Constructor<? extends T> getConstructor() {
		if (constructor == null) {
			computeConstructor();
		}
		return constructor;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
