/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.junit.After;
import org.junit.Before;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class GenericEventBSCTest<E extends IRodinElement, SCE extends IRodinElement> 
extends BasicSCTestWithFwdConfig {
	
	@Before
	public void setUpGEBSCT() throws Exception {
		generic = newGeneric();
	}

	protected abstract IGenericSCTest<E, SCE> newGeneric();

	@After
	public void tearDownGEBSCT() throws Exception {
		generic = null;
	}

	private IGenericSCTest<E, SCE> generic;
	
	public IGenericSCTest<E, SCE> getGeneric() {
		return generic;
	}

}
