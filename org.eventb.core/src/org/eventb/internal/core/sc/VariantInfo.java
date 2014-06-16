/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.sc;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Expression;
import org.eventb.core.sc.state.IVariantInfo;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public class VariantInfo extends State implements IVariantInfo {

	@Override
	public String toString() {
		return expression == null ? "null" : expression.toString();
	}

	Expression expression;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IVariantInfo#setExpression(org.eventb.core.ast.Expression)
	 */
	public void setExpression(Expression expression) throws CoreException {
		assertMutable();
		this.expression = expression;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IVariantInfo#getExpression()
	 */
	@Override
	public Expression getExpression() throws CoreException {
		assertImmutable();
		return expression;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

}
