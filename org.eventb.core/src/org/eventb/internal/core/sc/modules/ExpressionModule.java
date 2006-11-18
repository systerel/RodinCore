/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IExpressionElement;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IResult;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ExpressionModule extends LabeledFormulaModule {

	@Override
	protected IAttributeType.String getFormulaAttributeType() {
		return EventBAttributes.EXPRESSION_ATTRIBUTE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.LabeledFormulaModule#parseFormula(int, org.rodinp.core.IInternalElement[], org.eventb.core.ast.Formula[], java.util.Collection, org.eventb.core.ast.FormulaFactory)
	 */
	@Override
	protected Formula parseFormula(
			IInternalElement formulaElement,
			Collection<FreeIdentifier> freeIdentifierContext,
			FormulaFactory factory) throws CoreException {
		
		IExpressionElement expressionElement = (IExpressionElement) formulaElement;

		String expressionString = expressionElement.getExpressionString();
		
		// parse the predicate
		
		IParseResult parseResult = factory.parseExpression(expressionString);
		
		if (!parseResult.isSuccess()) {
			issueASTProblemMarkers(expressionElement, getFormulaAttributeType(), parseResult);
			
			return null;
		}
		Expression expression = parseResult.getParsedExpression();
		
		// check legibility of the predicate
		// (this will only produce a warning on failure)
		
		IResult legibilityResult = expression.isLegible(freeIdentifierContext);
		
		if (!legibilityResult.isSuccess()) {
			issueASTProblemMarkers(expressionElement, getFormulaAttributeType(), legibilityResult);
		}
		
		return expression;
	}

}
