/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
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

import static org.eventb.core.basis.PRUtil.buildTypenv;
import static org.eventb.core.basis.PRUtil.setPRIdentifiers;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRStoredExpr;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
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

	@Override
	public Expression getExpression(FormulaFactory factory,
			ITypeEnvironment baseTypenv) throws RodinDBException {
		final ITypeEnvironment typenv = buildTypenv(this, factory, baseTypenv);
		return super.getExpression(factory, typenv);
	}

	@Override
	public void setExpression(Expression predicate,
			ITypeEnvironment baseTypenv, IProgressMonitor monitor)
			throws RodinDBException {
		setPRIdentifiers(this, predicate, baseTypenv, monitor);
		super.setExpression(predicate, monitor);
	}

}
