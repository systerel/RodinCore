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

import org.eventb.core.IInvariant;
import org.eventb.core.IPSStatus;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * Represents an Invariant in the model
 *
 */
public class ModelInvariant extends ModelPOContainer{
	public ModelInvariant(IInvariant invariant, IModelElement parent){
		internalInvariant = invariant;
		this.parent = parent; 
	}

	private IInvariant internalInvariant;
	
	public IInvariant getInternalInvariant() {
		return internalInvariant;
	}


	@Override
	public IRodinElement getInternalElement() {
		return internalInvariant;
	}


	@Override
	public Object getParent(boolean complex) {
		if (parent instanceof ModelMachine ) {
			return ((ModelMachine) parent).invariant_node;
		}
		return parent;
	}


	@Override
	public Object[] getChildren(IInternalElementType<?> type, boolean complex) {
		if (type != IPSStatus.ELEMENT_TYPE) {
			if (ExplorerUtils.DEBUG) {
				System.out.println("Unsupported children type for invariant: " +type);
			}
			return new Object[0];
		}
		return getIPSStatuses();
	}
	
}
