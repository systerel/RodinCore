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
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

/**
 * Common protocol for expression extensions.
 * 
 * @author "Nicolas Beauger"
 * @since 2.0
 */
public interface IExpressionExtension extends IFormulaExtension {

	/**
	 * Computes the type of the extended expression from its given children.
	 * 
	 * @param childExprs
	 *            the child expressions
	 * @param childPreds
	 *            the child predicates
	 * @return the type of the extended expression or <code>null</code> if it is
	 *         ill-typed or if its type cannot be determined
	 */
	Type synthesizeType(Expression[] childExprs, Predicate[] childPreds,
			ITypeMediator mediator);

	/**
	 * Verifies that the given proposed type is coherent with the given
	 * children.
	 * 
	 * @param proposedType
	 *            a type proposed for the extended expression
	 * @param childExprs
	 *            the child expressions
	 * @param childPreds
	 *            the child predicates
	 * @return <code>true</code> iff the proposed type is coherent with the
	 *         children, <code>false</code> otherwise
	 */
	boolean verifyType(Type proposedType, Expression[] childExprs,
			Predicate[] childPreds);

	/**
	 * Define type check constraints for the given extended expression. The
	 * given type check mediator is intended to be used for creating type
	 * variables when needed, and for adding type constraints.
	 * 
	 * @param expression
	 *            the extended expression to type check
	 * @param tcMediator
	 *            a type check mediator
	 * @return the type of the given extended expression (in terms of type
	 *         variables if needed)
	 */
	Type typeCheck(ExtendedExpression expression, ITypeCheckMediator tcMediator);

	/**
	 * <p>
	 * Most commonly, this method returns <code>false</code>. Returning
	 * <code>true</code> is reserved FOR ADVANCED PURPOSES ONLY.
	 * </p>
	 * <p>
	 * Can be <code>true</code> only if this extension is a type constructor,
	 * which implies, as a contract for maintaining inner coherence, that the
	 * {@link #synthesizeType(Expression[], Predicate[], ITypeMediator)} method
	 * returns a powerset of the parametric type instantiated with this
	 * extension and the base type of its children if any.
	 * </p>
	 * 
	 * @return <code>true</code> iff this extension is a type constructor
	 */
	boolean isATypeConstructor();

}
