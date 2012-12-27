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
package org.rodinp.core.emf.lightcore.adapters;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

/**
 * @author Thomas Muller
 */
public class AttributeUpdateAdapterFactory extends AdapterFactoryImpl {

	protected static final AttributeUpdateAdapter ADAPTER = new AttributeUpdateAdapter();

	@Override
	protected Adapter createAdapter(Notifier target) {
		return ADAPTER;
	}

	@Override
	public boolean isFactoryForType(Object type) {
		return type == AttributeUpdateAdapter.class;
	}

}
