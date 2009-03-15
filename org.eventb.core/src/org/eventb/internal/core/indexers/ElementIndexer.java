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

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.indexer.IIndexingBridge;

public abstract class ElementIndexer extends Cancellable {

	protected static final FormulaFactory ff = FormulaFactory.getDefault();

	protected final SymbolTable symbolTable;
	private final IIndexingBridge bridge;

	public ElementIndexer(SymbolTable symbolTable, IIndexingBridge bridge) {
		this.symbolTable = symbolTable;
		this.bridge = bridge;
	}

	/**
	 * Actually performs the indexing.
	 * 
	 * @throws RodinDBException
	 */
	public abstract void process() throws RodinDBException;

	protected final void process(IInternalElement element,
			IAttributeType.String attribute) throws RodinDBException {
		if (!isValid(element, attribute)) {
			return;
		}
		final String formulaString = getFormulaString();
		checkCancel();
		IParseResult result = parseFormula(formulaString);
		checkCancel();
		if (!result.isSuccess()) {
			return;
		}
		final Formula<?> formula = getParsedFormula(result);
		visitAndIndex(element, attribute, formula);
	}

	protected abstract String getFormulaString() throws RodinDBException;

	protected abstract IParseResult parseFormula(String formulaString);

	protected abstract Formula<?> getParsedFormula(IParseResult result);

	private void visitAndIndex(IInternalElement element,
			IAttributeType.String attribute, Formula<?> formula) {
		final FreeIdentifier[] idents = formula.getFreeIdentifiers();

		// Idea: filter idents that are indeed declared. Ignore those that are
		// not and at the same time build a map from ident to declaration.
		// Then visit the formula and make an occurrence for each identifier
		// that belongs to the map.

		final IdentTable identTable = new IdentTable();
		symbolTable.addToIdentTable(idents, identTable);

		if (!identTable.isEmpty()) {
			final FormulaIndexer formulaIndexer =
					new FormulaIndexer(element, attribute, identTable, bridge);

			formula.accept(formulaIndexer);
		}
	}

	private boolean isValid(IInternalElement elem,
			IAttributeType.String attribute) throws RodinDBException {
		if (!elem.exists()) {
			return false;
		}
		return elem.hasAttribute(attribute);
	}

	private void checkCancel() {
		checkCancel(bridge);
	}
}