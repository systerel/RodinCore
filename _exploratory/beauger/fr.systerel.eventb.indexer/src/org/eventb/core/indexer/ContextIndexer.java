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

import static org.eventb.core.EventBPlugin.getContextFileName;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.IContextFile;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IPredicateElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IIndexingToolkit;

/**
 * @author Nicolas Beauger
 * 
 */
public class ContextIndexer extends EventBIndexer {

	private static final String ID = "fr.systerel.eventb.indexer.context";

	IIndexingToolkit index;

	// TODO manage exceptions

	public void index(IIndexingToolkit index) {
		this.index = index;
		final IRodinFile file = index.getRodinFile();

		if (file instanceof IContextFile) {
			try {
				index((IContextFile) file);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				if (DEBUG) {
					e.printStackTrace();
				}
			}
		}
	}

	private void index(IContextFile file) throws RodinDBException {

		final SymbolTable imports = new SymbolTable(null);
		processImports(index.getImports(), imports);
		
		final SymbolTable totalST = new SymbolTable(imports);
		processIdentifierElements(file.getCarrierSets(), totalST);
		processIdentifierElements(file.getConstants(), totalST);

		processPredicateElements(file.getAxioms(), totalST);
		processPredicateElements(file.getTheorems(), totalST);
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
			final IDeclaration declaration = indexDeclaration(ident, ident
					.getIdentifierString(), index);
			index.export(declaration);
			// FIXME possible conflict between sets and constants
			symbolTable.put(declaration);
		}
	}

	private void processPredicateElements(IPredicateElement[] elems,
			SymbolTable symbolTable) throws RodinDBException {
		for (IPredicateElement elem : elems) {
			final PredicateIndexer predIndexer = new PredicateIndexer(elem,
					symbolTable);
			predIndexer.process(index);
		}
	}

	public IRodinFile[] getDependencies(IRodinFile file) {
		if (!(file instanceof IContextFile)) {
			return NO_DEPENDENCIES;
		}
		final IContextFile context = (IContextFile) file;

		final List<IRodinFile> extendedFiles = new ArrayList<IRodinFile>();
		try {
			final IExtendsContext[] extendsClauses = context
					.getExtendsClauses();

			addExtendedFiles(extendsClauses, extendedFiles);

		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return extendedFiles.toArray(new IRodinFile[extendedFiles.size()]);
	}

	private void addExtendedFiles(final IExtendsContext[] extendsClauses,
			final List<IRodinFile> extendedFiles) throws RodinDBException {

		for (IExtendsContext extendsContext : extendsClauses) {
			final IRodinFile extended = getExtendedFile(extendsContext);
			if (extended != null) {
				extendedFiles.add(extended);
			}
		}
	}

	private IRodinFile getExtendedFile(IExtendsContext extendsContext)
			throws RodinDBException {
		final String extBareName = extendsContext.getAbstractContextName();
		final String extFileName = getContextFileName(extBareName);

		final IRodinProject project = extendsContext.getRodinProject();
		final IRodinFile extended = project.getRodinFile(extFileName);

		return extended;
	}

	public String getId() {
		return ID;
	}

}
