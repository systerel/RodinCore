/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.model;

import org.eventb.core.IAxiom;
import org.eventb.core.IPSStatus;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

import fr.systerel.internal.explorer.navigator.ExplorerUtils;

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
	public IRodinElement getInternalElement() {
		return internalAxiom;
	}

	@Override
	public Object getParent(boolean complex) {
		if (parent instanceof ModelContext ) {
			return ((ModelContext) parent).axiom_node;
		}
		return parent;
	}


	@Override
	public Object[] getChildren(IInternalElementType<?> type, boolean complex) {
		if (type != IPSStatus.ELEMENT_TYPE) {
			if (ExplorerUtils.DEBUG) {
				System.out.println("Unsupported children type for axiom: " +type);
			}
			return new Object[0];
		}
		return getIPSStatuses();
	}

	
	

}
