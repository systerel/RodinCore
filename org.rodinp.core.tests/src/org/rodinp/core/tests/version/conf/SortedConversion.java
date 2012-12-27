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
package org.rodinp.core.tests.version.conf;

import org.eclipse.core.runtime.InvalidRegistryObjectException;

/**
 * @author Stefan Hallerstede
 *
 */
public class SortedConversion extends SimpleConversion {

	public static final String ASCENDING = "ascending";
	public static final String DESCENDING = "descending";
	
	private final String order;

	public SortedConversion(String bundle, String type, String order, String version,
			Element... elements) {
		super(bundle, type, version, elements);
		assert order.equals(ASCENDING) || order.equals(DESCENDING);
		this.order = order;
	}
	
	@Override
	public String getAttribute(String name)
	throws InvalidRegistryObjectException {
		if (name.equals("order"))
			return order;
		else
			return super.getAttribute(name);
	}
	
	@Override
	public String getName() throws InvalidRegistryObjectException {
		return "sorted";
	}

}
