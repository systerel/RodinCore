/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.tool.state;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.tool.IState;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.BasicDescWithClass;
import org.osgi.framework.Bundle;

/**
 * @author Stefan Hallerstede
 *
 */
public class StateType<T extends IState> extends BasicDescWithClass implements IStateType<T> {

	// Class implementing elements of this element type
	// (cached value)
	protected Class<? extends T> classObject;

	public StateType(IConfigurationElement configElement) throws ModuleLoadingException {
		super(configElement);
		computeClass();
	}

	@SuppressWarnings("unchecked")
	protected void computeClass() throws ModuleLoadingException {
		Bundle bundle = Platform.getBundle(getBundleName());
		try {
			Class<?> clazz = bundle.loadClass(getClassName());
			classObject = (Class<? extends T>) clazz.asSubclass(IState.class);
		} catch (Throwable e) {
			throw new ModuleLoadingException(e);
		}
	}

}
