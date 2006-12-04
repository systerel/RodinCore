/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eventb.core.ISCVariant;
import org.eventb.core.ast.Expression;
import org.eventb.core.pog.state.IMachineVariantInfo;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineVariantInfo implements IMachineVariantInfo {

	private final Expression varExpression;
	
	private final ISCVariant variant;
	
	public Expression getExpression() {
		return varExpression;
	}
	
	public ISCVariant getVariant() {
		return variant;
	}

	public String getStateType() {
		return STATE_TYPE;
	}

	public MachineVariantInfo(final Expression expression, final ISCVariant variant) {
		this.varExpression = expression;
		this.variant = variant;
	}

	public boolean machineHasVariant() {
		return varExpression != null;
	}

}
