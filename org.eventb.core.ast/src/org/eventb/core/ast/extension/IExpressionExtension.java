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
 * Common protocol for expression extensions.
 * 
 * @author "Nicolas Beauger"
 * @since 2.0
 */
public interface IExpressionExtension extends IFormulaExtension {

	/**
	 * Returns the type of the given expression.
	 * 
	 * @param expression
	 *            the AST node
	 * @return the type of the given expression or <code>null</code> if it is
	 *         ill-typed or if its type cannot be determined
	 */
	Type synthesizeType(ExtendedExpression expression, ITypeMediator mediator);

	// FIXME pass children: node is under construction; same for synthesize
	boolean verifyType(Type proposedType, ExtendedExpression expression);
	
	Type typeCheck(ExtendedExpression expression, ITypeCheckMediator tcMediator);

	/**
	 * <p>
	 * Most commonly, this method returns <code>false</code>. Returning
	 * <code>true</code> is reserved FOR ADVANCED PURPOSES ONLY.
	 * </p>
	 * <p>
	 * Can be <code>true</code> only if this extension is a type constructor,
	 * which implies, as a contract for maintaining inner coherence, that the
	 * {@link #synthesizeType(ExtendedExpression, ITypeMediator)} method returns
	 * a powerset of the generic type instantiated with this extension and the
	 * base type of its children if any.
	 * </p>
	 * 
	 * @return <code>true</code> iff this extension is a type constructor
	 */
	boolean isATypeConstructor();

}
