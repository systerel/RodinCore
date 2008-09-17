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

/**
 * @author Administrator
 *
 */
public class ModelTheorem extends ModelPOContainer {
	public ModelTheorem(ITheorem theorem){
		internalTheorem = theorem;
	}

	private ITheorem internalTheorem;
	
	
	public ITheorem getInternalTheorem() {
		return internalTheorem;
	}

}
