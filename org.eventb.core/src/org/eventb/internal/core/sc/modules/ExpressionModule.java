/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - ensure that all AST problems are reported
 *     Systerel - mathematical language V2
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IExpressionElement;
import org.eventb.core.ISCExpressionElement;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IResult;
import org.eventb.core.sc.GraphProblem;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;

/**
 * @author Stefan Hallerstede
 * 
 */
public abstract class ExpressionModule<I extends IInternalElement> extends
		LabeledFormulaModule<Expression, I> {

	@Override
	protected IAttributeType.String getFormulaAttributeType() {
		return EventBAttributes.EXPRESSION_ATTRIBUTE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.internal.core.sc.modules.LabeledFormulaModule#parseFormula
	 * (int, org.rodinp.core.IInternalElement[], org.eventb.core.ast.Formula[],
	 * java.util.Collection, org.eventb.core.ast.FormulaFactory)
	 */
	@Override
	protected Expression parseFormula(I formulaElement,
			Collection<FreeIdentifier> freeIdentifierContext,
			FormulaFactory factory) throws CoreException {

		IExpressionElement expressionElement = (IExpressionElement) formulaElement;
		IAttributeType.String attributeType = getFormulaAttributeType();

		if (!expressionElement.hasExpressionString()) {
			createProblemMarker(expressionElement, attributeType,
					GraphProblem.ExpressionUndefError);
			return null;
		}
		String expressionString = expressionElement.getExpressionString();

		// parse the predicate

		IParseResult parseResult = factory.parseExpression(expressionString, expressionElement);
		if (issueASTProblemMarkers(expressionElement, attributeType,
				parseResult)) {
			return null;
		}
		Expression expression = parseResult.getParsedExpression();

		// check legibility of the predicate
		// (this will only produce a warning on failure)

		IResult legibilityResult = expression.isLegible(freeIdentifierContext);
		if (issueASTProblemMarkers(expressionElement, attributeType,
				legibilityResult)) {
			return null;
		}

		return expression;
	}

	@Override
	protected Expression[] allocateFormulas(int size) {
		return new Expression[size];
	}
	
	protected final void createSCExpressions(IInternalElement target,
			IProgressMonitor monitor) throws CoreException {
		for (int i = 0; i < formulaElements.length; i++) {
			if (formulas[i] == null)
				continue;
			ISCExpressionElement scExprElem = (ISCExpressionElement) symbolInfos[i]
					.createSCElement(target, null, monitor);
			scExprElem.setExpression(formulas[i], null);
		}
	}

}
