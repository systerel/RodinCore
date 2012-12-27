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
package org.rodinp.core.tests.builder.basis;

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.tests.builder.IDependency;

public class Dependency extends InternalElement implements IDependency {
	
	public Dependency(String name, IRodinElement parent) {
		super(name, parent);
	}

	public IInternalElementType<IDependency> getElementType() {
		return ELEMENT_TYPE;
	}

}
