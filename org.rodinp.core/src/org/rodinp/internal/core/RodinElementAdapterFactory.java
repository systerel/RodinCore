/*******************************************************************************
 * Copyright (c) 2006, 2017 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     INP Toulouse - use of generics for adapters
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.rodinp.core.IRodinElement;

/**
 * Adapter from IRodinElement to IResource.
 * 
 * @author Laurent Voisin
 */
public class RodinElementAdapterFactory implements IAdapterFactory {

	private static final Class<?>[] ADAPTERS = new Class[] {
		IResource.class,
	};
	
	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (!(adaptableObject instanceof IRodinElement)) {
			return null;
		}
		if (IResource.class.equals(adapterType)) {
			return adapterType.cast(
					((IRodinElement) adaptableObject).getCorrespondingResource());
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return ADAPTERS;
	}

}
