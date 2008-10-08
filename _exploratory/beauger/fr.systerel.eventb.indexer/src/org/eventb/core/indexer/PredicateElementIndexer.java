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
import org.eventb.core.IPredicateElement;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.indexer.SymbolTable.IdentTable;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IIndexingToolkit;

/**
 * @author Nicolas Beauger
 * 
 */
public class PredicateElementIndexer {
	private static final FormulaFactory ff = FormulaFactory.getDefault();

	private final IPredicateElement element;
	private final SymbolTable symbolTable;
	private final IIndexingToolkit index;

	public PredicateElementIndexer(IPredicateElement element,
			SymbolTable symbolTable, IIndexingToolkit index) {
		this.element = element;
		this.symbolTable = symbolTable;
		this.index = index;
	}

	public void process() throws RodinDBException {
		if (!isValid(element)) {
			return;
		}
		final String predicateString = element.getPredicateString();
		IParseResult result = ff.parsePredicate(predicateString);
		if (!result.isSuccess()) {
			return;
		}
		final Predicate pred = result.getParsedPredicate();
		extractAndIndexRefs(element, pred);
	}

	private void extractAndIndexRefs(IPredicateElement pred, Formula<?> formula) {
		final FreeIdentifier[] idents = formula.getFreeIdentifiers();

		// Idea: filter idents that are indeed declared. Ignore those that are
		// not and at the same time build a map from ident to declaration.
		// Then visit the predicate and make an occurrence for each identifier
		// that belongs to the map.

		final IdentTable identTable = symbolTable.extractIdentTable(idents);

		if (!identTable.isEmpty()) {
			final FreeIdentIndexer freeIdentIndexer = new FreeIdentIndexer(
					pred, identTable, index);

			formula.accept(freeIdentIndexer);
		}
	}

	private boolean isValid(IPredicateElement pred) throws RodinDBException {
		if (!pred.exists()) {
			return false;
		}
		if (!pred.hasAttribute(EventBAttributes.PREDICATE_ATTRIBUTE)) {
			return false;
		}
		return true;
	}

}
