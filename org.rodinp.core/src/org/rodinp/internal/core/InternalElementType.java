/*******************************************************************************
 * Copyright (c) 2005, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - removed unnamed internal elements
 *     Systerel - separation of file and root element
 *     Systerel - add database relations
 *******************************************************************************/
package org.rodinp.internal.core;

import static java.util.Collections.emptyList;

import java.lang.reflect.Array;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.internal.core.util.Util;

/**
 * @author lvoisin
 *
 */
public class InternalElementType<T extends IInternalElement> extends
		ContributedElementType<T> implements IInternalElementType<T>,
		IContributedItemType, Comparable<InternalElementType<?>> {

	// Name of the class implementing elements of this element type
	private final String className;

	// Class implementing elements of this element type
	// (cached value)
	protected Class<? extends T> classObject;

	/*
	 * The three following lists are quasi-immutable: they are all set together
	 * only once and never change afterwards.
	 */

	// List of all allowed parent types
	private List<InternalElementType<?>> parentTypes = null;

	// List of all allowed child types
	private List<InternalElementType<?>> childTypes = null;

	// List of all allowed attribute types
	private List<AttributeType<?>> attributeTypes = null;

	public InternalElementType(IConfigurationElement configurationElement,
			ElementTypeManager elementTypeManager) {
		super(configurationElement, elementTypeManager);
		this.className = configurationElement.getAttribute("class");
	}

	@SuppressWarnings("unchecked")
	protected void computeClass() {
		Bundle bundle = Platform.getBundle(getBundleName());
		try {
			Class<?> clazz = bundle.loadClass(getClassName());
			classObject = (Class<? extends T>) clazz.asSubclass(InternalElement.class);
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
			constructor = classObject.getConstructor(String.class, IRodinElement.class);
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
	 * @throws IllegalArgumentException
	 *             if violating parent-child relationship
	 */
	public T createInstance(String elementName, IRodinElement parent) {
		enforceElementRelationship(parent);
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

	private void enforceElementRelationship(IRodinElement parent) {
		if (parent instanceof RodinFile) {
			// Restriction enforced in RodinFile
			return;
		}
		final IInternalElement iParent = ((IInternalElement) parent);
		final IInternalElementType<?> parentType = iParent.getElementType();
		if (parentType.canParent(this)) {
			return;
		}
		throw new IllegalArgumentException("Forbidden child type " + this
				+ " for element " + parent);
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
		final ElementTypeManager typeManager = ElementTypeManager.getInstance();
		return typeManager.isUbiquitous((InternalElementType<?>) childType)
				|| childTypes.contains(childType);
	}

	@Override
	public InternalElementType<?>[] getChildTypes() {
		return childTypes
				.toArray(new InternalElementType<?>[childTypes.size()]);
	}

	@Override
	public boolean isUbiquitous() {
		final ElementTypeManager typeManager = ElementTypeManager.getInstance();
		return typeManager.isUbiquitous(this);
	}
	
	@Override
	public InternalElementType<?>[] getParentTypes() {
		return parentTypes.toArray(new InternalElementType<?>[parentTypes
				.size()]);
	}

	@Override
	public AttributeType<?>[] getAttributeTypes() {
		return attributeTypes.toArray(new AttributeType[attributeTypes.size()]);
	}

	@Override
	public boolean canCarry(IAttributeType attributeType) {
		final ElementTypeManager typeManager = ElementTypeManager.getInstance();
		return typeManager.isUbiquitous((AttributeType<?>) attributeType)
				|| attributeTypes.contains(attributeType);
	}

	public void setRelation(List<InternalElementType<?>> pTypes,
			List<InternalElementType<?>> cTypes, List<AttributeType<?>> aTypes) {
		assert parentTypes == null;
		parentTypes = pTypes;
		childTypes = cTypes;
		attributeTypes = aTypes;
	}

	public void finalizeRelations() {
		if (parentTypes == null) {
			parentTypes = emptyList();
			childTypes = emptyList();
			attributeTypes = emptyList();
		}
	}

	@Override
	public int compareTo(InternalElementType<?> other) {
		return this.id.compareTo(other.id);
	}

}