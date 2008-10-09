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

import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPredicateElement;
import org.eventb.core.IVariant;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IIndexingToolkit;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.RodinIndexer;

/**
 * @author Nicolas Beauger
 * 
 */
public class MachineIndexer implements IIndexer {

	private static final boolean DEBUG = true;
	// FIXME manage exceptions and remove

	private static final String ID = "fr.systerel.eventb.indexer.machine";

	private static final IRodinFile[] NO_DEPENDENCIES = new IRodinFile[0];

	private IIndexingToolkit index;

	// TODO consider passing as parameter

	public IRodinFile[] getDependencies(IRodinFile file) {
		return NO_DEPENDENCIES;
	}

	public String getId() {
		return ID;
	}

	public void index(IIndexingToolkit index) {
		this.index = index;
		final IRodinFile file = index.getRodinFile();

		if (file instanceof IMachineFile) {
			try {
				index((IMachineFile) file);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				if (DEBUG) {
					e.printStackTrace();
				}
			}
		}
	}

	private void index(IMachineFile file) throws RodinDBException {
		final SymbolTable symbolTable = new SymbolTable();

		processImports(index.getImports(), symbolTable);
		processIdentifierElements(file.getVariables(), symbolTable);
		processLabeledElements(file.getEvents(), symbolTable);

		processPredicateElements(file.getInvariants(), symbolTable);
		processPredicateElements(file.getTheorems(), symbolTable);
		processExpressionElements(file.getVariants(), symbolTable);
	}

	// TODO consider factorizing methods with ContextIndexer

	private void processImports(IDeclaration[] imports, SymbolTable symbolTable) {
		// put the declarations into the SymbolTable
		for (IDeclaration declaration : imports) {
			// index.export(declaration);
			symbolTable.put(declaration);
		}
	}

	private void processIdentifierElements(IIdentifierElement[] elems,
			SymbolTable symbolTable) throws RodinDBException {
		// index declaration for each identifier element and export them
		// put the declarations into the SymbolTable
		for (IIdentifierElement ident : elems) {
			final IDeclaration declaration = indexIdent(ident);
			index.export(declaration);
			symbolTable.put(declaration);
		}
	}

	private void processLabeledElements(ILabeledElement[] elems,
			SymbolTable symbolTable) throws RodinDBException {
		// index declaration for each identifier element and export them
		// put the declarations into the SymbolTable
		for (ILabeledElement label : elems) {
			final IDeclaration declaration = indexLabel(label);
			index.export(declaration);
			symbolTable.put(declaration);
		}
	}

	private void processPredicateElements(IPredicateElement[] elems,
			SymbolTable symbolTable) throws RodinDBException {
		for (IPredicateElement elem : elems) {
			final PredicateElementIndexer predIndexer = new PredicateElementIndexer(
					elem, symbolTable);
			predIndexer.process(index);
		}
	}

	private void processExpressionElements(IVariant[] variants,
			SymbolTable symbolTable) throws RodinDBException {
		for (IVariant variant : variants) {
			final ExpressionElementIndexer exprIndexer = new ExpressionElementIndexer(
					variant, symbolTable);
			exprIndexer.process(index);
		}
	}

	private IDeclaration indexElement(IInternalElement element, String name) {
		final IDeclaration declaration = index.declare(element, name);
		final IRodinLocation loc = RodinIndexer.getRodinLocation(element
				.getRodinFile());
		index.addOccurrence(declaration, EventBIndexUtil.DECLARATION, loc);
		return declaration;
	}

	private IDeclaration indexIdent(IIdentifierElement ident)
			throws RodinDBException {
		return indexElement(ident, ident.getIdentifierString());
	}

	private IDeclaration indexLabel(ILabeledElement label)
			throws RodinDBException {
		return indexElement(label, label.getLabel());
	}

}
