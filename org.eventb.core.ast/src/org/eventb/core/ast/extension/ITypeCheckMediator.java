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

import org.eventb.core.ast.Type;

/**
 * This interface describes the type-check mediator which is used by extensions
 * for registering type constraints and create types and type variables.
 * 
 * @author Nicolas Beauger
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITypeCheckMediator extends ITypeMediator {

	/**
	 * Returns a fresh type variable that can be used as a place-holder in type
	 * check constraints. Type variables are implemented by an internal class of
	 * the AST library and are just simple object references for client code.
	 * Clients can build new types containing type variables, and pass type
	 * variables to any method of this interface that takes a type as argument.
	 * The references to type variables must not be held by clients, nor cached.
	 * <p>
	 * Type variables have a short life-span: they are created during a run of
	 * the formula type-checker and do not live after the type-checker has
	 * finished its job.
	 * </p>
	 * 
	 * @return a new type variable
	 */
	Type newTypeVariable();

	/**
	 * Adds the type-check constraint that the given types must be unifiable.
	 * This means that there should exist an instantiation of the type variables
	 * contained in both types such that the two types become equal to each
	 * other.
	 * 
	 * @param left
	 *            the type on the left-hand side of the unification
	 * @param right
	 *            the type on the right-hand side of the unification
	 */
	void sameType(Type left, Type right);

}
