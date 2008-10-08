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

import static org.eventb.core.indexer.EventBIndexUtil.DECLARATION;

import org.eventb.core.IContextFile;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IPredicateElement;
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
public class ContextIndexer implements IIndexer {

	private static final String ID = "fr.systerel.eventb.indexer.context";

	private static final IRodinFile[] NO_DEPENDENCIES = new IRodinFile[0];

	private IIndexingToolkit index;
	// TODO consider passing as parameter
	
	public void index(IIndexingToolkit index) {
		this.index = index;
		final IRodinFile file = index.getRodinFile();

		if (file instanceof IContextFile) {
			try {
				index((IContextFile) file);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void index(IContextFile file) throws RodinDBException {
		final SymbolTable symbolTable = new SymbolTable();

		processImports(index.getImports(), symbolTable);
		processIdentifierElements(file.getCarrierSets(), symbolTable);
		processIdentifierElements(file.getConstants(), symbolTable);

		processPredicateElements(file.getAxioms(), symbolTable);
		processPredicateElements(file.getTheorems(), symbolTable);
	}

	private void processImports(IDeclaration[] imports, SymbolTable symbolTable) {
		// export each imported declaration
		// put the declarations into the SymbolTable
		for (IDeclaration declaration : imports) {
			index.export(declaration);
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

	private void processPredicateElements(IPredicateElement[] elems,
			SymbolTable symbolTable) throws RodinDBException {
		for (IPredicateElement elem : elems) {
			final PredicateElementIndexer predIndexer = new PredicateElementIndexer(
					elem, symbolTable, index);
			predIndexer.process();
		}
	}

	private IDeclaration indexIdent(IIdentifierElement ident)
			throws RodinDBException {
		final IDeclaration declaration = index.declare(ident, ident
				.getIdentifierString());
		final IRodinLocation loc = RodinIndexer.getRodinLocation(ident
				.getRodinFile());
		index.addOccurrence(declaration, DECLARATION, loc);
		return declaration;
	}

	public IRodinFile[] getDependencies(IRodinFile file) {
		return NO_DEPENDENCIES;
	}

	public String getId() {
		return ID;
	}

}
