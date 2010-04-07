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
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.rodinp.core.IInternalElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class GenericEventBPOTest<E extends IInternalElement> extends EventBPOTest {

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
