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

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.lightcore.LightElement;

/**
 * Processes the delta, relatively to the given root which is interested to
 * update its implicit children.
 * 
 * @author Thomas Muller
 */
public class ImplicitDeltaRootAdapter extends AdapterImpl implements
		IElementChangedListener {

	private final ImplicitDeltaProcessor processor;

	public ImplicitDeltaRootAdapter(LightElement root) {
		this.processor = new ImplicitDeltaProcessor(this, root);
		RodinCore.addElementChangedListener(this);
	}

	@Override
	public void elementChanged(ElementChangedEvent event) {
		try {
			processor.processDelta(event.getDelta());
		} catch (RodinDBException e) {
			System.out.println("ImplicitDeltaRootAdapter:"
					+ " Could not process database delta: ");
			e.printStackTrace();
		}
	}

	@Override
	public boolean isAdapterForType(Object type) {
		return ImplicitDeltaRootAdapter.class == type;
	}

	public void finishListening() {
		RodinCore.removeElementChangedListener(this);
	}

}
