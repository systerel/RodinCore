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

public abstract class Operation extends ExtensionDesc {

	public Operation(IConfigurationElement configElement) {
		super(configElement);
	}

	protected void checkId(String s) {
		int x = s.indexOf('@');
		int y = s.indexOf('/');
		if (x != -1 || y != -1)
			throw new IllegalStateException("bare id expected " + s);
	}

}