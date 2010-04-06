/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core;

import org.eclipse.core.runtime.IAdapterFactory;
import org.rodinp.core.IRodinProject;

/**
 * Adapter from IEventBProject to IRodinProject.
 * 
 * @author Laurent Voisin
 */
@SuppressWarnings("unchecked")
public class RodinProjectAdapterFactory implements IAdapterFactory {

	private static final Class[] ADAPTERS = new Class[] {
		IRodinProject.class,
	};
	
	public IRodinProject getAdapter(Object adaptableObject, Class adapterType) {
		if (IRodinProject.class.equals(adapterType)) {
			return ((EventBProject) adaptableObject).getRodinProject();
		}
		return null;
	}

	public Class[] getAdapterList() {
		return ADAPTERS;
	}

}
