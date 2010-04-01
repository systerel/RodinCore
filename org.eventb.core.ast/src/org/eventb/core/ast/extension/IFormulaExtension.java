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

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;

/**
 * @author "Nicolas Beauger"
 * @since 2.0 TODO encapsulate children in mediators
 */
public interface IFormulaExtension {

	void toString(IToStringMediator mediator, IExtendedFormula formula);

	void checkPreconditions(Expression[] expressions, Predicate[] predicates);

	/**
	 * 
	 * @param wdMediator
	 *            for further purposes
	 * @param formula
	 *            the AST node
	 * @return a predicate, or <code>null</code>
	 */
	Predicate getWDPredicate(IWDMediator wdMediator, IExtendedFormula formula);

	String getSyntaxSymbol();

	boolean isFlattenable();

}
