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

import org.eventb.core.ITheorem;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Represents a Theorem in the Model
 *
 */
public class ModelTheorem extends ModelPOContainer {
	public ModelTheorem(ITheorem theorem, IModelElement parent){
		internalTheorem = theorem;
		this.parent =  parent;
	}

	private ITheorem internalTheorem;
	
	
	public ITheorem getInternalTheorem() {
		return internalTheorem;
	}

	@Override
	public String getLabel() {
		try {
			return "Theorem " +internalTheorem.getLabel();
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Theorem ??";
	}

	public IRodinElement getInternalElement() {
		return internalTheorem;
	}
	
}
