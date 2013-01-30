/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.extension;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;


/**
 * Describes the signature of an operator contributed by an extension.
 * 
 * @since 2.0
 * @author Nicolas Beauger
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IExtensionKind {

	/**
	 * Returns the signature of an operator, including its relative position
	 * (prefix, infix, postfix), whether the operator is associative and the
	 * kinds of its children.
	 * 
	 * @return the signature of an operator
	 */
	IOperatorProperties getProperties();

	/**
	 * This method is not intended to be called by clients.
	 * 
	 * FIXME move this method outside of the API
	 */
	boolean checkPreconditions(Expression[] childExprs, Predicate[] childPreds);

}