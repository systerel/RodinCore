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
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.rodinp.core.emf.lightcore.LightElement;

/**
 * The factory to get synchronisation with the Rodin Database from the listening
 * of deltas (i.e. changes).
 * 
 * @author Thomas Muller
 */
public class DeltaRootAdapterFactory extends AdapterFactoryImpl {

	@Override
	protected Adapter createAdapter(Notifier target) {
		if (target instanceof LightElement) {
			final LightElement le = (LightElement) target;
			if (le.isEIsRoot())
				return new DeltaRootAdapter(le);
		}
		return NullDeltaRootAdapter.INSTANCE;
	}

	@Override
	public boolean isFactoryForType(Object type) {
		return type == DeltaRootAdapter.class;
	}

	private static class NullDeltaRootAdapter extends AdapterImpl {

		protected final static NullDeltaRootAdapter INSTANCE //
		= new NullDeltaRootAdapter();

		private NullDeltaRootAdapter() {
			// private constructor, singleton pattern
		}

		@Override
		public boolean isAdapterForType(Object type) {
			return type == DeltaRootAdapter.class;
		}

	}

}
