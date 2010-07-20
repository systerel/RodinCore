/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.extension;

import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Type;

/**
 * @author "Nicolas Beauger"
 * @since 2.0
 * 
 */
public interface IExpressionExtension extends IFormulaExtension {

	/**
	 * Returns the type of the given expression assuming that the preconditions
	 * are fulfilled.
	 * 
	 * @param expression
	 *            the AST node
	 * @return a type
	 */
	Type getType(ITypeMediator mediator, ExtendedExpression expression);

	// TODO these 2 methods can be mixed into 1 with 2 mediator implementations
	
	Type typeCheck(ITypeCheckMediator tcMediator, ExtendedExpression expression);
	// BoundIdentDecl[] quantifiedIdentifiers,
	// Expression[] childExpressions, Predicate[] childPredicates,

}
