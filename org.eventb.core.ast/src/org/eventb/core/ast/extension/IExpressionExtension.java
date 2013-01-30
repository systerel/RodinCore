/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
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
 * <p>
 * In this interface, clients contribute code related to type-checking of the
 * extended expressions built with this extension. Three methods must be
 * implemented:
 * <ul>
 * <li><code>synthesizeType</code> computes, if possible, the type of the
 * extended expression based on the types of its children.</li>
 * <li><code>typeCheck</code> creates the constraints that the extended
 * expression and its children must satisfy for the expression to be
 * type-checked. It is used by the formula type-checker to infer types of
 * formulas.</li>
 * <li><code>verifyType</code> is the most important of the three. This method
 * states whether an expression is indeed type-checked, given the type and the
 * children of the expression.</li>
 * </ul>
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 2.0
 */
public interface IExpressionExtension extends IFormulaExtension {

	/**
	 * Returns the type of an extended expression from its children or
	 * <code>null</code> if unknown. If possible, the implementation shall
	 * attempt to compute a type. But it is OK to always return
	 * <code>null</code>.
	 * <p>
	 * The returned value must be <code>null</code> if children types are
	 * incoherent. The returned value must satisfy the constraint computed by
	 * the {@link #typeCheck(ExtendedExpression, ITypeCheckMediator)} method and
	 * must also be accepted by
	 * {@link #verifyType(Type, Expression[], Predicate[])}.
	 * </p>
	 * <p>
	 * When called, it is guaranteed that all children are already type-checked.
	 * </p>
	 * 
	 * @param childExprs
	 *            the child expressions
	 * @param childPreds
	 *            the child predicates
	 * @return the type of the extended expression or <code>null</code> if it
	 *         would be ill-typed or if its type cannot be determined
	 */
	Type synthesizeType(Expression[] childExprs, Predicate[] childPreds,
			ITypeMediator mediator);

	/**
	 * Verifies that the given proposed type is coherent with the given
	 * children.
	 * <p>
	 * If the given type is incoherent with the given children or the extension
	 * typing rules, <code>false</code> must be returned.
	 * </p>
	 * <p>
	 * When called, it is guaranteed that all children are already type-checked.
	 * </p>
	 * 
	 * @param proposedType
	 *            a type proposed for the extended expression
	 * @param childExprs
	 *            the child expressions
	 * @param childPreds
	 *            the child predicates
	 * @return <code>true</code> iff the proposed type is coherent with the
	 *         children and the typing rules of this extension
	 */
	boolean verifyType(Type proposedType, Expression[] childExprs,
			Predicate[] childPreds);

	/**
	 * Registers type-check constraints for the given extended expression. The
	 * given type-check mediator can be used for creating types, type variables
	 * as needed, and for registering type constraints.
	 * <p>
	 * If the extension introduces new given types that are not coming from
	 * children, then the returned type must contain these given types.
	 * </p>
	 * <p>
	 * When called, it is guaranteed that every child expression bears a type
	 * (possibly containing type variables).
	 * </p>
	 * 
	 * @param expression
	 *            the extended expression to type check
	 * @param tcMediator
	 *            a type-check mediator
	 * @return the type of the given extended expression (in terms of type
	 *         variables if needed)
	 */
	Type typeCheck(ExtendedExpression expression, ITypeCheckMediator tcMediator);

	/**
	 * Tells whether this extension is also a type constructor.
	 * <p>
	 * Most commonly, this method returns <code>false</code>. Returning
	 * <code>true</code> is reserved FOR ADVANCED PURPOSES ONLY.
	 * </p>
	 * <p>
	 * Can be <code>true</code> only if this extension is a type constructor,
	 * which implies, as a contract for maintaining inner coherence, that the
	 * {@link #synthesizeType(Expression[], Predicate[], ITypeMediator)} method
	 * returns a powerset of the parametric type instantiated with this
	 * extension and a combination of the types of its children if any.
	 * </p>
	 * 
	 * @return <code>true</code> iff this extension is a type constructor
	 */
	boolean isATypeConstructor();

}
