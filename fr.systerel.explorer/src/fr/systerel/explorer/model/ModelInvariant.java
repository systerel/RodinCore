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


package fr.systerel.explorer.model;

import org.eventb.core.IInvariant;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

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


	public IRodinElement getInternalElement() {
		return internalInvariant;
	}

}
