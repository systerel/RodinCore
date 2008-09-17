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

/**
 * @author Administrator
 *
 */
public class ModelAxiom extends ModelPOContainer {
	public ModelAxiom(IAxiom axiom){
		internalAxiom = axiom;
	}

	private IAxiom internalAxiom;
	
	
	public IAxiom getInternalAxiom() {
		return internalAxiom;
	}

}
