/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class GenericEventBSCTest<E extends IRodinElement, SCE extends IRodinElement> 
extends BasicSCTestWithFwdConfig {
	
	@Override
	protected void setUp() throws Exception {
		generic = newGeneric();
		super.setUp();
	}

	protected abstract IGenericSCTest<E, SCE> newGeneric();

	@Override
	protected void tearDown() throws Exception {
		generic = null;
		super.tearDown();
	}

	private IGenericSCTest<E, SCE> generic;
	
	public IGenericSCTest<E, SCE> getGeneric() {
		return generic;
	}

}
