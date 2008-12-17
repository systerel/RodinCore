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

import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;

import org.eventb.core.IPredicateElement;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IParseResult;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IIndexingBridge;

/**
 * @author Nicolas Beauger
 * 
 */
public class PredicateIndexer extends ElementIndexer {

	final IPredicateElement element;

	public PredicateIndexer(IPredicateElement element, SymbolTable symbolTable,
			IIndexingBridge bridge) {

		super(symbolTable, bridge);
		this.element = element;
	}

	public void process() throws RodinDBException {
		process(element, PREDICATE_ATTRIBUTE);
	}

	@Override
	protected String getFormulaString() throws RodinDBException {
		return element.getPredicateString();
	}

	@Override
	protected Formula<?> getParsedFormula(IParseResult result) {
		return result.getParsedPredicate();
	}

	@Override
	protected IParseResult parseFormula(String formulaString) {
		return ff.parsePredicate(formulaString);
	}

}
