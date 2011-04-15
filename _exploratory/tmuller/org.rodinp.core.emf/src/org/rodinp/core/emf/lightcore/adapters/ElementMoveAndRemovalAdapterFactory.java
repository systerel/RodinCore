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
package org.rodinp.core.emf.lightcore.adapters;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

/**
 * Factory for {@link ElementMoveAndRemovalAdapter}
 */
public class ElementMoveAndRemovalAdapterFactory extends AdapterFactoryImpl{

	protected static final ElementMoveAndRemovalAdapter ADAPTER = new ElementMoveAndRemovalAdapter();

	@Override
	protected Adapter createAdapter(Notifier target) {
		return ADAPTER;
	}

	@Override
	public boolean isFactoryForType(Object type) {
		return type == ElementMoveAndRemovalAdapter.class;
	}
	
}