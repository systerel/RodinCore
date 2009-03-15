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
package org.eventb.internal.core.indexers;

import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;

import org.eventb.core.IPredicateElement;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IParseResult;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.indexer.IIndexingBridge;

/**
 * @author Nicolas Beauger
 * 
 */
public class PredicateIndexer extends ElementIndexer {

	public PredicateIndexer(IPredicateElement element, SymbolTable symbolTable,
			IIndexingBridge bridge) {

		super(element, symbolTable, bridge);
	}

	@Override
	public IAttributeType.String getAttributeType() {
		return PREDICATE_ATTRIBUTE;
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
