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


import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.lightcore.LightElement;

/**
 * 
 * This is the adapter for light root elements which have to listen for Rodin
 * Database modifications, thus updating their light model accordingly.
 * 
 * @author Thomas Muller
 * 
 */
public class DeltaRootAdapter extends AdapterImpl implements
		IElementChangedListener {

	private final DeltaProcessor processor;

	public DeltaRootAdapter(LightElement root) {
		this.processor = new DeltaProcessor(this, root);
		RodinCore.addElementChangedListener(this);
	}

	@Override
	public void elementChanged(ElementChangedEvent event) {
		try {
			processor.processDelta(event.getDelta());
		} catch (RodinDBException e) {
			System.out.println("Could not process database delta: ");
			e.printStackTrace();
		}
	}

	@Override
	public boolean isAdapterForType(Object type) {
		return DeltaRootAdapter.class == type;
	}

	public void finishListening() {
		RodinCore.removeElementChangedListener(this);
	}

}
