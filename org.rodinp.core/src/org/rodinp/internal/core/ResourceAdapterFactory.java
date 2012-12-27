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
package org.rodinp.internal.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;

/**
 * Adapter from IResource to IRodinElement.
 * 
 * @author Laurent Voisin
 */
@SuppressWarnings("rawtypes")
public class ResourceAdapterFactory implements IAdapterFactory {

	private static final Class[] ADAPTERS = new Class[] {
		IRodinElement.class,
	};
	
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IRodinElement.class.equals(adapterType)) {
			return RodinCore.valueOf((IResource) adaptableObject);
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<IAdaptable>[] getAdapterList() {
		return ADAPTERS;
	}

}
