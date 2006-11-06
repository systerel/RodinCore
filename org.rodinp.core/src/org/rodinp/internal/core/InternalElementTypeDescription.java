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
import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.internal.core.util.Util;

/**
 * @author lvoisin
 *
 */
public class InternalElementTypeDescription extends ElementTypeDescription<InternalElement> {

	private final boolean named;
	
	public InternalElementTypeDescription(IConfigurationElement configurationElement) {
		super(configurationElement);
		
		String value = configurationElement.getAttribute("named"); 
		this.named = value == null || value.equalsIgnoreCase(Boolean.TRUE.toString());
	}

	@Override
	protected void computeConstructor() {
		Bundle bundle = Platform.getBundle(getBundleName());
		try {
			Class<?> clazz = bundle.loadClass(getClassName());
			classObject = clazz.asSubclass(InternalElement.class);
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

}
