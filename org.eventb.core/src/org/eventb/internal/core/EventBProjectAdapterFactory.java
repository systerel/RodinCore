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
package org.eventb.internal.core;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eventb.core.IEventBProject;
import org.rodinp.core.IRodinProject;

/**
 * Adapter from IRodinProject to IEventBProject.
 * 
 * @author Laurent Voisin
 */
public class EventBProjectAdapterFactory implements IAdapterFactory {

	private static final Class<?>[] ADAPTERS = new Class[] {
		IEventBProject.class,
	};
	
	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (!(adaptableObject instanceof IRodinProject)) {
			return null;
		}
		final IRodinProject rodinProject = (IRodinProject) adaptableObject;
		if (IEventBProject.class.equals(adapterType)) {
			return adapterType.cast(new EventBProject(rodinProject));
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return ADAPTERS;
	}

}
