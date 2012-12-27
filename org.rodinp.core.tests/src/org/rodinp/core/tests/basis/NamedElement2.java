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
package org.rodinp.core.tests.basis;

import static org.rodinp.core.tests.AbstractRodinDBTests.PLUGIN_ID;

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.basis.InternalElement;

public class NamedElement2 extends InternalElement {
	
	public static final IInternalElementType<NamedElement2> ELEMENT_TYPE = 
		RodinCore.getInternalElementType(PLUGIN_ID + ".namedElement2");

	public NamedElement2(String name, IRodinElement parent) {
		super(name, parent);
	}

	public IInternalElementType<NamedElement2> getElementType() {
		return ELEMENT_TYPE;
	}

}
