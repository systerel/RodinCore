/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class BaseConfig extends ConfigWithClosure<String> {

	public BaseConfig(IConfigurationElement configElement) throws ModuleLoadingException {
		super(configElement);
	}

	@Override
	protected List<String> newClosure() {
		List<String> closure = super.newClosure();
		closure.add(getId());
		return closure;
	}
}
