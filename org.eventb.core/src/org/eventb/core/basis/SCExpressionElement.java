/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.basis;

import static org.eventb.internal.core.Util.newCoreException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ISCExpressionElement;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.internal.core.Messages;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Common implementation of Event-B SC elements that contain an expression, as
 * an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it in a
 * database extension. In particular, clients should not use it, but rather use
 * its associated interface <code>ISCExpressionElement</code>.
 * </p>
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 */
public abstract class SCExpressionElement extends EventBElement
		implements ISCExpressionElement {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public SCExpressionElement(String name, IRodinElement parent) {
		super(name, parent);
	}

	/**
	 * @since 3.0
	 */
	@Override
	public Expression getExpression(ITypeEnvironment typenv)
			throws CoreException {
		final String contents = getExpressionString();
		final FormulaFactory factory = typenv.getFormulaFactory();
		final IRodinElement source = getSourceIfExists();
		final IParseResult pResult = factory.parseExpression(contents, source);
		if (pResult.hasProblem()) {
			throw newCoreException(Messages.database_SCExpressionParseFailure,
					this);
		}
		final Expression result = pResult.getParsedExpression();
		final ITypeCheckResult tcResult = result.typeCheck(typenv);
		if (!tcResult.isSuccess()) {
			throw newCoreException(Messages.database_SCExpressionTCFailure,
					this);
		}
		assert result.isTypeChecked();
		return result;
	}

	@Override
	public void setExpression(Expression expression, IProgressMonitor monitor) 
	throws RodinDBException {
		setExpressionString(expression.toStringWithTypes(), monitor);	
	}

}
