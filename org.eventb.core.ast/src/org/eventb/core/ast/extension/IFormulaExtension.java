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
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.notation.INotation;

/**
 * @author "Nicolas Beauger"
 * @since 2.0
 *
 */
public interface IFormulaExtension {

	String getTagOperator();

	void checkPreconditions(Expression[] expressions, Predicate[] predicates);
	
	boolean isFlattenable();

	INotation getNotation();
	
	Predicate getWDPredicateRaw(FormulaFactory formulaFactory,
			Expression[] childExpressions, Predicate[] childPredicates);

}
