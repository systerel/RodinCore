/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.tests.basis;

import java.util.Collections;
import java.util.List;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.emf.api.itf.ICoreImplicitChildProvider;

/**
 * Creates a buggy implicit child provider that creates a null pointer
 * exception which could do problems in implicit child retrieval.
 */
public class TestBuggyImplicitChildProvider implements ICoreImplicitChildProvider {

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
