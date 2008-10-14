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

import org.eventb.core.IAxiom;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Represents an Axiom in the model.
 *
 */
public class ModelAxiom extends ModelPOContainer {
	public ModelAxiom(IAxiom axiom, IModelElement parent){
		internalAxiom = axiom;
		this.parent = parent;
	}

	private IAxiom internalAxiom;
	
	
	public IAxiom getInternalAxiom() {
		return internalAxiom;
	}


	@Override
	public String getLabel() {
		try {
			return "Axiom " +internalAxiom.getLabel();
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Axiom ??";
	}


	public IRodinElement getInternalElement() {
		// TODO Auto-generated method stub
		return internalAxiom;
	}

}
