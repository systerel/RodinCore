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

import org.eventb.core.EventBAttributes;
import org.eventb.core.IExpressionElement;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.indexer.SymbolTable.IdentTable;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IIndexingToolkit;

/**
 * @author Nicolas Beauger
 *
 */
public class ExpressionElementIndexer {

	// TODO consider factorizing material with PredicateElementIndexer
	
	private static final FormulaFactory ff = FormulaFactory.getDefault();

	private final IExpressionElement element;
	private final SymbolTable symbolTable;

	public ExpressionElementIndexer(IExpressionElement element,
			SymbolTable symbolTable) {
		this.element = element;
		this.symbolTable = symbolTable;
	}

	public void process(IIndexingToolkit index) throws RodinDBException {
		if (!isValid(element)) {
			return;
		}
		final String expressionString = element.getExpressionString();
		IParseResult result = ff.parseExpression(expressionString);
		if (!result.isSuccess()) {
			return;
		}
		final Expression expr = result.getParsedExpression();
		visitAndIndex(expr, index);
	}

	private void visitAndIndex(
			Formula<?> formula, IIndexingToolkit index) {
		final FreeIdentifier[] idents = formula.getFreeIdentifiers();

		// Idea: filter idents that are indeed declared. Ignore those that are
		// not and at the same time build a map from ident to declaration.
		// Then visit the expression and make an occurrence for each identifier
		// that belongs to the map.

		final IdentTable identTable = symbolTable.extractIdentTable(idents);

		if (!identTable.isEmpty()) {
			final FreeIdentIndexer freeIdentIndexer = new FreeIdentIndexer(
					element, identTable, index);

			formula.accept(freeIdentIndexer);
		}
	}

	private boolean isValid(IExpressionElement expr) throws RodinDBException {
		if (!expr.exists()) {
			return false;
		}
		if (!expr.hasAttribute(EventBAttributes.EXPRESSION_ATTRIBUTE)) {
			return false;
		}
		return true;
	}

	
}
