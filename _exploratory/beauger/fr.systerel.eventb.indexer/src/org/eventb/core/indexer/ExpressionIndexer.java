/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.indexer;

import static org.eventb.core.EventBAttributes.EXPRESSION_ATTRIBUTE;

import org.eventb.core.IExpressionElement;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IParseResult;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IIndexingToolkit;

/**
 * @author Nicolas Beauger
 * 
 */
public class ExpressionIndexer extends ElementIndexer {

	private final IExpressionElement element;

	public ExpressionIndexer(IExpressionElement element,
			SymbolTable symbolTable, IIndexingToolkit index) {
		super(symbolTable, index);
		this.element = element;
	}

	public void process() throws RodinDBException {
		if (!isValid(element, EXPRESSION_ATTRIBUTE)) {
			return;
		}
		final String expressionString = element.getExpressionString();
		// TODO checkCancel()
		IParseResult result = ff.parseExpression(expressionString);
		// TODO checkCancel()
		if (!result.isSuccess()) {
			return;
		}
		final Expression expr = result.getParsedExpression();
		visitAndIndex(element, EXPRESSION_ATTRIBUTE, expr);
	}
}
