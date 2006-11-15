/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.internal.core;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.internal.core.util.Util;

/**
 * @author lvoisin
 *
 */
public class InternalElementType extends
		ContributedElementType<InternalElement> implements IInternalElementType {

	private final boolean named;
	
	public InternalElementType(IConfigurationElement configurationElement) {
		super(configurationElement);
		
		String value = configurationElement.getAttribute("named"); 
		this.named = value == null || value.equalsIgnoreCase(Boolean.TRUE.toString());
	}

	@Override
	protected void computeClass() {
		Bundle bundle = Platform.getBundle(getBundleName());
		try {
			Class<?> clazz = bundle.loadClass(getClassName());
			classObject = clazz.asSubclass(InternalElement.class);
		} catch (Exception e) {
			Util.log(e, "Can't find constructor for element type " + getId());
		}
	}

	@Override
	protected void computeConstructor() {
		if (classObject == null) {
			computeClass();
		}
		try {
			if (this.named)
				constructor = classObject.getConstructor(String.class, IRodinElement.class);
			else
				constructor = classObject.getConstructor(IRodinElement.class);
		} catch (Exception e) {
			Util.log(e, "Can't find constructor for element type " + getId());
		}
	}

	public boolean isNamed() {
		return named;
	}

	/**
	 * Creates a new internal element handle.
	 * 
	 * @param elementName
	 *            the name of the element to create. Ignored for an unnamed
	 *            element.
	 * @param parent
	 *            the new element's parent
	 * @return a handle on the internal element or <code>null</code> if the
	 *         element type is unknown
	 */
	public InternalElement createInstance(String elementName, IInternalParent parent) {
		if (constructor == null) {
			computeConstructor();
		}
		if (constructor == null) {
			return null;
		}
		try {
			if (isNamed()) {
				return constructor.newInstance(elementName, parent);
			} else {
				return constructor.newInstance(parent);
			}
		} catch (Exception e) {
			Util.log(e, "Error when creating handle of type " + getId());
			return null;
		}
	}
	
}