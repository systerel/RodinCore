/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - removed unnamed internal elements
 *     Systerel - separation of file and root element
 *     Systerel - added relations API
 *******************************************************************************/
package org.rodinp.internal.core.relations;

import static java.util.Arrays.asList;

import java.lang.reflect.Array;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.internal.core.ContributedElementType;
import org.rodinp.internal.core.relations.api.IInternalElementType2;
import org.rodinp.internal.core.util.Util;

/**
 * Based on the {@code InternalElementType} class, this class supports type
 * relations.
 * <p>
 * <em> RODIN 3.0 - for testing purpose only </em>
 * <br>
 * TODO retrofit into {@code InternalElementType} 
 * </p>
 * 
 * @author Laurent Voisin
 */
public class InternalElementTypeExt<T extends IInternalElement> extends
		ContributedElementType<T> implements IInternalElementType2<T> {

	// Name of the class implementing elements of this element type
	private final String className;

	// Class implementing elements of this element type
	// (cached value)
	protected Class<? extends T> classObject;

	private IInternalElementType<?>[] parentTypes = null;
	private IInternalElementType<?>[] childTypes = null;
	private IAttributeType[] attributeTypes = null;

	public InternalElementTypeExt(IConfigurationElement configurationElement) {
		super(configurationElement);
		this.className = configurationElement.getAttribute("class");
	}

	@SuppressWarnings("unchecked")
	protected void computeClass() {
		Bundle bundle = Platform.getBundle(getBundleName());
		try {
			Class<?> clazz = bundle.loadClass(getClassName());
			classObject = (Class<? extends T>) clazz
					.asSubclass(InternalElement.class);
		} catch (Exception e) {
			String message = "Can't find constructor for element type "
					+ getId();
			Util.log(null, message);
			throw new IllegalStateException(message, e);
		}
	}

	protected void computeConstructor() {
		if (classObject == null) {
			computeClass();
		}
		try {
			constructor = classObject.getConstructor(String.class,
					IRodinElement.class);
		} catch (Exception e) {
			String message = "Can't find constructor for element type "
					+ getId();
			Util.log(null, message);
			throw new IllegalStateException(message, e);
		}
	}

	/**
	 * Creates a new internal element handle.
	 * 
	 * @param elementName
	 *            the name of the element to create
	 * @param parent
	 *            the new element's parent
	 * @return a handle on the internal element or <code>null</code> if the
	 *         element type is unknown
	 */
	public T createInstance(String elementName, IRodinElement parent) {
		if (constructor == null) {
			computeConstructor();
		}
		if (constructor == null) {
			return null;
		}
		try {
			return constructor.newInstance(elementName, parent);
		} catch (Exception e) {
			String message = "Can't create an element of type " + getId();
			Util.log(null, message);
			throw new IllegalStateException(message, e);
		}
	}

	String getClassName() {
		return className;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T[] getArray(int length) {
		if (classObject == null) {
			computeClass();
		}
		return (T[]) Array.newInstance(classObject, length);
	}

	@Override
	public boolean canParent(IInternalElementType<?> childType) {
		return asList(childTypes).contains(childType);
	}

	@Override
	public IInternalElementType<?>[] getChildTypes() {
		return childTypes;
	}

	@Override
	public IInternalElementType<?>[] getParentTypes() {
		return parentTypes;
	}

	@Override
	public IAttributeType[] getAttributeTypes() {
		return attributeTypes;
	}

	@Override
	public boolean isElementOf(IAttributeType attributeType) {
		return asList(attributeTypes).contains(attributeType);
	}

	public void setRelation(IInternalElementType<?>[] pTypes,
			IInternalElementType<?>[] cTypes, IAttributeType[] aTypes) {
		if (parentTypes == null && childTypes == null && attributeTypes == null) {
			this.parentTypes = pTypes;
			this.childTypes = cTypes;
			this.attributeTypes = aTypes;
			return;
		}
		throw new IllegalAccessError(
				"Illegal attempt to set relations for the type " + getName());
	}

}