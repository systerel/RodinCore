/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.rodinp.internal.core.version;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Nicolas Beauger
 *
 */
public abstract class PatternOperation extends Operation {

	public PatternOperation(IConfigurationElement configElement) {
		super(configElement);
	}

}
