package org.eventb.core.indexer;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IIndexingToolkit;

public abstract class ElementIndexer {

	protected static final FormulaFactory ff = FormulaFactory.getDefault();

	protected final SymbolTable symbolTable;

	public ElementIndexer(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	public abstract void process(IIndexingToolkit index) throws RodinDBException;

	protected void visitAndIndex(IInternalElement element, IAttributeType.String attribute, Formula<?> formula,
			IIndexingToolkit index) {
		final FreeIdentifier[] idents = formula.getFreeIdentifiers();

		// Idea: filter idents that are indeed declared. Ignore those that are
		// not and at the same time build a map from ident to declaration.
		// Then visit the predicate and make an occurrence for each identifier
		// that belongs to the map.

		final IdentTable identTable = new IdentTable();
		symbolTable.addToIdentTable(idents, identTable);

		if (!identTable.isEmpty()) {
			final FormulaIndexer formulaIndexer = new FormulaIndexer(
					element, attribute, identTable, index);

			formula.accept(formulaIndexer);
		}
	}
	
	protected boolean isValid(IAttributedElement elem, IAttributeType.String attribute) throws RodinDBException {
		if (!elem.exists()) {
			return false;
		}
		return elem.hasAttribute(attribute);
	}

}