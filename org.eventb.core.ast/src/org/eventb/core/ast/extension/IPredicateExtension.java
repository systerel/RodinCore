/*******************************************************************************
 * Copyright (c) 2010, 2022 Systerel and others.
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
 * 
 * Please use {@link IPredicateExtension2} instead which is more efficient.
 *
 * @author "Nicolas Beauger"
 * @since 2.0
 */
public interface IPredicateExtension extends IFormulaExtension {

	/**
	 * Define type check constraints for the given extended predicate. The given
	 * type check mediator is intended to be used for creating type variables when
	 * needed, and for adding type constraints.
	 * 
	 * @param predicate  the extended predicate to type check
	 * @param tcMediator a type check mediator
	 */
	void typeCheck(ExtendedPredicate predicate, ITypeCheckMediator tcMediator);

}
