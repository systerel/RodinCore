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
package org.eventb.core.tests.sc;

import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class GenericEventBSCTest<E extends IRodinElement, SCE extends IRodinElement> 
extends BasicSCTestWithFwdConfig {
	
	private IGenericSCTest<E, SCE> generic = newGeneric();
	
	protected abstract IGenericSCTest<E, SCE> newGeneric();

	public IGenericSCTest<E, SCE> getGeneric() {
		return generic;
	}

}
