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
import org.eventb.core.IExpressionElement;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IResult;
import org.eventb.core.sc.IMarkerDisplay;
import org.rodinp.core.IInternalElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ExpressionModule extends LabeledFormulaModule {

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.LabeledFormulaModule#parseFormula(int, org.rodinp.core.IInternalElement[], org.eventb.core.ast.Formula[], java.util.Collection, org.eventb.core.ast.FormulaFactory)
	 */
	@Override
	protected boolean parseFormula(int index,
			IInternalElement[] formulaElements, Formula[] formulas,
			Collection<FreeIdentifier> freeIdentifierContext,
			FormulaFactory factory) throws CoreException {
		
		IExpressionElement expressionElement = (IExpressionElement) formulaElements[index];

		String expressionString = expressionElement.getExpressionString();
		
		// parse the predicate
		
		IParseResult parseResult = factory.parseExpression(expressionString);
		
		if (!parseResult.isSuccess()) {
			issueASTProblemMarkers(IMarkerDisplay.SEVERITY_ERROR, expressionElement, parseResult);
			
			return false;
		}
		Expression expression = parseResult.getParsedExpression();
		
		formulas[index] = expression;
		
		// check legibility of the predicate
		// (this will only produce a warning on failure)
		
		IResult legibilityResult = expression.isLegible(freeIdentifierContext);
		
		if (!legibilityResult.isSuccess()) {
			issueASTProblemMarkers(IMarkerDisplay.SEVERITY_WARNING, expressionElement, legibilityResult);
		}
		
		return true;
	}

}
