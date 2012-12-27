/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added attribute modification
 *******************************************************************************/
package org.rodinp.internal.core.version;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class SimpleOperation extends Operation {
	
	private final SimpleConversionSheet sheet;

	public SimpleOperation(IConfigurationElement configElement, SimpleConversionSheet sheet) {
		super(configElement);
		this.sheet = sheet;
	}

	public SimpleConversionSheet getSheet() {
		return sheet;
	}

}
