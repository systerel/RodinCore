/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core.version;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class Operation extends ExtensionDesc {
	
	private final SimpleConversionSheet sheet;

	public Operation(IConfigurationElement configElement, SimpleConversionSheet sheet) {
		super(configElement);
		this.sheet = sheet;
	}

	public SimpleConversionSheet getSheet() {
		return sheet;
	}

	protected void checkId(String s) {
		int x = s.indexOf('@');
		int y = s.indexOf('/');
		if (x != -1 || y != -1)
			throw new IllegalStateException("bare id expected " + s);
	}

}
