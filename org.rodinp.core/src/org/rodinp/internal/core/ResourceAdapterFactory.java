/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;

/**
 * Adapter from IResource to IRodinElement.
 * 
 * @author Laurent Voisin
 */
public class ResourceAdapterFactory implements IAdapterFactory {

	private static final Class[] ADAPTERS = new Class[] {
		IRodinElement.class,
	};
	
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IRodinElement.class.equals(adapterType)) {
			return RodinCore.create((IResource) adaptableObject);
		}
		return null;
	}

	public Class[] getAdapterList() {
		return ADAPTERS;
	}

}
