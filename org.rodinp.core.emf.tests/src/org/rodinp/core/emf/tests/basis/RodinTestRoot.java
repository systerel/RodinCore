/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - copied from org.rodinp.core.tests
 *******************************************************************************/
package org.rodinp.core.emf.tests.basis;

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.basis.InternalElement;

public class RodinTestRoot extends InternalElement {

	public static final IInternalElementType<RodinTestRoot> ELEMENT_TYPE =
		RodinCore.getInternalElementType("org.rodinp.core.emf.tests.test");

	public RodinTestRoot(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<RodinTestRoot> getElementType() {
		return ELEMENT_TYPE;
	}

}
