/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/


package fr.systerel.internal.explorer.model;

import org.eventb.core.IEvent;
import org.eventb.core.IPSStatus;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * Represents an Event in the Model
 *
 */
public class ModelEvent extends ModelPOContainer {
	public ModelEvent(IEvent event, IModelElement parent){
		internalEvent = event;
		this.parent = parent;
	}

	private IEvent internalEvent;
	
	
	public IEvent getInternalEvent() {
		return internalEvent;
	}

	@Override
	public IRodinElement getInternalElement() {
		return internalEvent;
	}

	@Override
	public Object getParent(boolean complex) {
		if (parent instanceof ModelMachine ) {
			return ((ModelMachine) parent).event_node;
		}
		return parent;
	}


	@Override
	public Object[] getChildren(IInternalElementType<?> type, boolean complex) {
		if (type != IPSStatus.ELEMENT_TYPE) {
			if (ExplorerUtils.DEBUG) {
				System.out.println("Unsupported children type for event: " +type);
			}
			return new Object[0];
		}
		return getIPSStatuses();
	}
	
}
