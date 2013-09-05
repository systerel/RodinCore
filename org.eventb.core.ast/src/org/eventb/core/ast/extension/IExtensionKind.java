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
import org.eventb.core.ast.Type;

/**
 * Common protocol for describing the signature of an operator, that is the
 * number of its children and their breakdown between expressions and
 * predicates.
 * 
 * @author Nicolas Beauger
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface IExtensionKind {

	/**
	 * Returns the properties of this operator kind.
	 * 
	 * @return the properties of this operator kind
	 */
	IOperatorProperties getProperties();

	/**
	 * Tells whether the given expressions and predicates are compatible with
	 * this operator kind.
	 * 
	 * @return <code>true</code> iff the number of expressions and predicates
	 *         are valid
	 */
	boolean checkPreconditions(Expression[] childExprs, Predicate[] childPreds);

	/**
	 * Tells whether the given type parameters are compatible with this operator
	 * kind.
	 * 
	 * @return <code>true</code> iff the number of types are valid
	 * @since 3.0
	 */
	boolean checkTypePreconditions(Type[] typeParameters);

}