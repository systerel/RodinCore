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

import org.eventb.core.ast.ExtendedPredicate;

/**
 * Common protocol for predicate extensions.
 * @author "Nicolas Beauger"
 * @since 2.0
 */
public interface IPredicateExtension extends IFormulaExtension {

	/**
	 * Registers type-check constraints for the given extended predicate. The
	 * given type-check mediator can be used for creating types, type variables
	 * as needed, and for registering type constraints.
	 * <p>
	 * When called, it is guaranteed that every child expression bears a type
	 * (possibly containing type variables).
	 * </p>
	 * 
	 * @param predicate
	 *            the predicate expression to type check
	 * @param tcMediator
	 *            a type check mediator
	 */
	void typeCheck(ExtendedPredicate predicate, ITypeCheckMediator tcMediator);

	// FIXME shall have a verifyTypes() method

}
