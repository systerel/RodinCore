/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.tests.basis;

import java.util.Collections;
import java.util.List;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.emf.lightcore.IImplicitChildProvider;

/**
 * Creates a buggy implicit child provider that creates a null pointer
 * exception which could do problems in implicit child retrieval.
 */
public class TestBuggyImplicitChildProvider implements IImplicitChildProvider {

	@SuppressWarnings("null")
	@Override
	public List<? extends IInternalElement> getImplicitChildren(
			IInternalElement parent) {
		// Oh noooo !
		Object o = null;
		o.hashCode();
		return Collections.emptyList();
	}
	
}
