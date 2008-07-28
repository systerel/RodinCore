/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import org.eventb.core.ast.Formula;
import org.eventb.core.sc.state.IParsedFormula;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public class ParsedFormula extends State implements IParsedFormula {

	@Override
	public String toString() {
		return formula.toString();
	}

	private Formula<?> formula;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IParsedFormula#setFormula(Formula)
	 */
	public void setFormula(Formula<?> f) {
		formula = f;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IParsedFormula#getFormula()
	 */
	public Formula<?> getFormula() {
		return formula;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

}
