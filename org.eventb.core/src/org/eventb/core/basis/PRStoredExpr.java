/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - streamlined interface
 ******************************************************************************/
package org.eventb.core.basis;

import static org.eventb.internal.core.basis.PRUtil.buildTypenv;
import static org.eventb.internal.core.basis.PRUtil.setPRIdentifiers;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRStoredExpr;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Farhad Mehta
 * @since 1.0
 * 
 */
public class PRStoredExpr extends SCExpressionElement implements IPRStoredExpr {

	public PRStoredExpr(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<IPRStoredExpr> getElementType() {
		return ELEMENT_TYPE;
	}

	/**
	 * @since 3.0: use immutable type environment
	 */
	@Override
	public Expression getExpression(ISealedTypeEnvironment baseTypenv)
			throws RodinDBException {
		final ISealedTypeEnvironment typenv = buildTypenv(this, baseTypenv);
		return super.getExpression(typenv);
	}

	/**
	 * @since 3.0: use immutable type environment
	 */
	@Override
	public void setExpression(Expression expression,
			ISealedTypeEnvironment baseTypenv, IProgressMonitor monitor)
			throws RodinDBException {
		setPRIdentifiers(this, expression, baseTypenv, monitor);
		super.setExpression(expression, monitor);
	}

}
