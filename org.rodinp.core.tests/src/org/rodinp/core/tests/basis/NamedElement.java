/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests.basis;

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.basis.InternalElement;

public class NamedElement extends InternalElement {
	
	public static final IInternalElementType ELEMENT_TYPE = 
		RodinCore.getInternalElementType("org.rodinp.core.tests.namedElement");

	public NamedElement(String name, IRodinElement parent) {
		super(name, parent);
	}

	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}

}
