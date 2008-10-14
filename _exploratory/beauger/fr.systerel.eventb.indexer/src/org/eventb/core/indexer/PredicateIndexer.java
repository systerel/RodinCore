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
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IIndexingToolkit;

/**
 * @author Nicolas Beauger
 * 
 */
public class PredicateIndexer extends ElementIndexer {
	
	final IPredicateElement element;

	public PredicateIndexer(IPredicateElement element,
			SymbolTable symbolTable) {

		super(symbolTable);
		this.element = element;
	}

	public void process(IIndexingToolkit index) throws RodinDBException {
		if (!isValid(element, PREDICATE_ATTRIBUTE)) {
			return;
		}
		final String predicateString = element.getPredicateString();
		IParseResult result = ff.parsePredicate(predicateString);
		if (!result.isSuccess()) {
			return;
		}
		final Predicate pred = result.getParsedPredicate();
		visitAndIndex(element, PREDICATE_ATTRIBUTE, pred, index);
	}



}
