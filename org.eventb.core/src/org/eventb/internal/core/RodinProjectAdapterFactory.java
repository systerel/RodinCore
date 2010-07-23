/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core;

import org.eclipse.core.runtime.IAdapterFactory;
import org.rodinp.core.IRodinProject;

/**
 * Adapter from IEventBProject to IRodinProject.
 * 
 * @author Laurent Voisin
 */
@SuppressWarnings("rawtypes")
public class RodinProjectAdapterFactory implements IAdapterFactory {

	private static final Class[] ADAPTERS = new Class[] {
		IRodinProject.class,
	};
	
	@Override
	public IRodinProject getAdapter(Object adaptableObject, Class adapterType) {
		if (IRodinProject.class.equals(adapterType)) {
			return ((EventBProject) adaptableObject).getRodinProject();
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return ADAPTERS;
	}

}
