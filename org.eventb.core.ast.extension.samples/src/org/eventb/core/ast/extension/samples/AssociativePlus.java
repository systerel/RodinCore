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
package org.eventb.core.ast.extension.samples;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.notation.INotation;
import org.eventb.core.ast.extension.notation.INotationSymbol;
import org.eventb.core.ast.extension.notation.NotationFactory;
import org.eventb.core.ast.extension.notation.IFormulaChild.Kind;
import org.eventb.internal.core.typecheck.TypeCheckResult;

/**
 * Attempt at implementing a variable size notation.
 * 
 * @author Nicolas Beauger
 * 
 */
public class AssociativePlus implements IExpressionExtension {

	private static final INotationSymbol PLUS = NotationFactory.getInstance()
			.makeSymbol("+");
	
	public Type getType(Expression[] childExpressions,
			Predicate[] childPredicates) {
		// TODO Auto-generated method stub
		return null;
	}

	public Type typeCheck(TypeCheckResult result,
			BoundIdentDecl[] quantifiedIdentifiers,
			Expression[] childExpressions, Predicate[] childPredicates,
			Expression origin) {
		// TODO Auto-generated method stub
		return null;
	}

	public void checkPreconditions(Expression[] expressions,
			Predicate[] predicates) {
		// TODO Auto-generated method stub

	}

	public INotation getNotation() {
		final NotationFactory factory = NotationFactory.getInstance();
		return factory.makeAssociativeInfixNotation(PLUS, Kind.EXPRESSION);
	}

	public String getTagOperator() {
		return PLUS.getSymbol();
	}

	public Predicate getWDPredicateRaw(FormulaFactory formulaFactory,
			Expression[] childExpressions, Predicate[] childPredicates) {
		// TODO Auto-generated method stub
		return null;
	}

}
