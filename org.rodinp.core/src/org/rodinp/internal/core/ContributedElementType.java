/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.internal.core;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.basis.RodinElement;

/**
 * Base class for contributed element types.
 * 
 * @author Laurent Voisin
 */
public abstract class ContributedElementType<T extends RodinElement> extends
		ElementType {

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
	
	private static String readId(IConfigurationElement configElement) {
		String nameSpace = configElement.getNamespaceIdentifier();
		return nameSpace + "." + configElement.getAttribute("id");
	}
	
	private static String readName(IConfigurationElement configElement) {
		return configElement.getAttribute("name");
	}

	public ContributedElementType(IConfigurationElement configurationElement) {
		super(readId(configurationElement), readName(configurationElement));
		this.bundleName = configurationElement.getContributor().getName();
		this.className = configurationElement.getAttribute("class");
	}

	protected abstract void computeClass();
	
	protected abstract void computeConstructor();

	@Override
	@SuppressWarnings("unchecked")
	public T[] getArray(int length) {
		if (classObject == null) {
			computeClass();
		}
		return (T[]) Array.newInstance(classObject, length);
	}

	protected String getBundleName() {
		return bundleName;
	}

	protected String getClassName() {
		return className;
	}

}
