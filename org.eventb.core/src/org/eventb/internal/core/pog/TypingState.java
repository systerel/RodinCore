/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.pog.state.ITypingState;
import org.eventb.internal.core.tool.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public class TypingState extends State implements ITypingState {
	
	private ITypeEnvironment typeEnvironment;

	public TypingState(ITypeEnvironment typeEnvironment) {
		
		this.typeEnvironment = typeEnvironment;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ITypingState#getTypeEnvironment()
	 */
	public ITypeEnvironment getTypeEnvironment() {
		return typeEnvironment;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	public String getStateType() {
		return STATE_TYPE;
	}

}
