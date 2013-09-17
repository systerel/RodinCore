/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - updated Javadoc
 *     Systerel - streamlined interface
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.pm.IProofManager;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for proof expressions.
 * <p>
 * Clients should use the Proof Manager API rather than direct access to this
 * Rodin database API.
 * </p>
 *
 * @see IProofManager
 * 
 * @author Farhad Mehta
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IPRStoredExpr extends IInternalElement {

	IInternalElementType<IPRStoredExpr> ELEMENT_TYPE = RodinCore
			.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prExpr"); //$NON-NLS-1$

	/**
	 * Returns the expression stored in this element. The result is always
	 * type-checked.
	 * 
	 * @param baseTypenv
	 *            common type environment of the proof tree
	 * @return the expression stored in this element
	 * @throws RodinDBException
	 *             in case of an error accessing the Rodin database
	 * @since 3.0
	 */
	Expression getExpression(ISealedTypeEnvironment baseTypenv)
			throws RodinDBException;

	/**
	 * Stores the given expression in this element. The given expression must be
	 * type-checked.
	 * 
	 * @param expression
	 *            the expression to store
	 * @param baseTypenv
	 *            common type environment of the proof tree
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @throws RodinDBException
	 *             in case of an error accessing the Rodin database
	 * @since 3.0
	 */
	void setExpression(Expression expression,
			ISealedTypeEnvironment baseTypenv, IProgressMonitor monitor)
			throws RodinDBException;

}
