/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.UnnamedInternalElement;

public class UnnamedElement2 extends UnnamedInternalElement {
	
	public static final String ELEMENT_TYPE = 
		"org.rodinp.core.tests.unnamedElement2";

	public UnnamedElement2(IRodinElement parent) {
		super(ELEMENT_TYPE, parent);
	}

}
