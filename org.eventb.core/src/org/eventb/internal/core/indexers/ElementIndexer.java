/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.indexers;

import static org.rodinp.core.RodinCore.getInternalLocation;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.LanguageVersion;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.indexer.IIndexingBridge;
import org.rodinp.core.location.IAttributeLocation;

public abstract class ElementIndexer extends Cancellable {
	
	protected static final LanguageVersion version = LanguageVersion.V2;

	private final IInternalElement element;
	private final SymbolTable symbolTable;
	private final IIndexingBridge bridge;
	private final IAttributeType.String attrType;

	public ElementIndexer(IInternalElement element,
			IAttributeType.String attrType, SymbolTable symbolTable,
			IIndexingBridge bridge) {
		this.element = element;
		this.attrType = attrType;
		this.symbolTable = symbolTable;
		this.bridge = bridge;
	}

	/**
	 * Actually performs the indexing.
	 * 
	 * @throws RodinDBException
	 */
	public void process() throws RodinDBException {
		if (!isValid(element, attrType)) {
			return;
		}
		final String formulaString = element.getAttributeValue(attrType);
		final IAttributeLocation loc = getInternalLocation(element, attrType);
		checkCancel();
		final IParseResult result = parseFormula(formulaString, loc);
		checkCancel();
		if (result.hasProblem()) {
			return;
		}
		final Formula<?> formula = getParsedFormula(result);
		visitAndIndex(formula);
	}

	protected abstract IParseResult parseFormula(String formulaString,
			IAttributeLocation location);

	protected abstract Formula<?> getParsedFormula(IParseResult result);

	private void visitAndIndex(Formula<?> formula) {

		// Idea: filter idents that are indeed declared. Ignore those that are
		// not and at the same time build a map from ident to declaration.
		// Then visit the formula and make an occurrence for each identifier
		// that belongs to the map.

		final IdentTable identTable = new IdentTable();
		identTable.addIdents(formula.getFreeIdentifiers(), symbolTable);
		if (identTable.isEmpty()) {
			// Nothing to index
			return;
		}

		final FormulaIndexer formulaIndexer = new FormulaIndexer(identTable,
				bridge);
		formula.accept(formulaIndexer);
	}

	private boolean isValid(IInternalElement elem,
			IAttributeType.String attribute) throws RodinDBException {
		return elem.exists() && elem.hasAttribute(attribute);
	}

	private void checkCancel() {
		checkCancel(bridge);
	}
}