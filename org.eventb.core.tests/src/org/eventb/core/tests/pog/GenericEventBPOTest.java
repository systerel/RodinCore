/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.rodinp.core.IRodinFile;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class GenericEventBPOTest<E extends IRodinFile> extends EventBPOTest {

	@Override
	protected void setUp() throws Exception {
		generic = newGeneric();
		super.setUp();
	}

	protected abstract IGenericPOTest<E> newGeneric();

	@Override
	protected void tearDown() throws Exception {
		generic = null;
		super.tearDown();
	}

	private IGenericPOTest<E> generic;
	
	public IGenericPOTest<E> getGeneric() {
		return generic;
	}

}
