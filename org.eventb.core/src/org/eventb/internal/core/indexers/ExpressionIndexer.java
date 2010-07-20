/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.indexers;

import static org.eventb.core.EventBAttributes.EXPRESSION_ATTRIBUTE;

import org.eventb.core.IExpressionElement;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IParseResult;
import org.rodinp.core.indexer.IIndexingBridge;
import org.rodinp.core.location.IAttributeLocation;

/**
 * @author Nicolas Beauger
 * 
 */
public class ExpressionIndexer extends ElementIndexer {

	public ExpressionIndexer(IExpressionElement element,
			SymbolTable symbolTable, IIndexingBridge bridge) {
		super(element, EXPRESSION_ATTRIBUTE, symbolTable, bridge);
	}

	@Override
	protected Formula<?> getParsedFormula(IParseResult result) {
		return result.getParsedExpression();
	}

	@Override
	protected IParseResult parseFormula(String formulaString,
			IAttributeLocation location) {
		return ff.parseExpression(formulaString, version, location);
	}
}
