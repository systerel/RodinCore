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

import static org.eventb.core.EventBPlugin.getContextFileName;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.IContextRoot;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IIdentifierElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.indexer.IDeclaration;

/**
 * @author Nicolas Beauger
 * 
 */
public class ContextIndexer extends EventBIndexer {

	private static final String ID = "fr.systerel.eventb.indexer.context";

	@Override
	protected void index(IInternalElement root) throws RodinDBException {
		if (!(root instanceof IContextRoot)) {
			throwIllArgException(root);
		}
		index((IContextRoot) root);
	}

	private void index(IContextRoot root) throws RodinDBException {
		checkCancel();

		final SymbolTable importST = new SymbolTable(null);
		processImports(currentBridge.getImports(), importST);
		checkCancel();

		final SymbolTable totalST = new SymbolTable(importST);
		processIdentifierElements(root.getCarrierSets(), totalST);
		checkCancel();
		processIdentifierElements(root.getConstants(), totalST);
		checkCancel();

		processPredicateElements(root.getAxioms(), totalST);
	}

	private void processImports(IDeclaration[] imports, SymbolTable importST) {
		for (IDeclaration declaration : imports) {
			export(declaration);
			importST.put(declaration);
		}
	}

	private void processIdentifierElements(IIdentifierElement[] elems,
			SymbolTable symbolTable) throws RodinDBException {
		for (IIdentifierElement ident : elems) {
			if (ident.hasIdentifierString()) {
				final IDeclaration declaration = indexDeclaration(ident);
				export(declaration);
				symbolTable.put(declaration);
			}
		}
	}

	@Override
	public IRodinFile[] getDeps(IInternalElement root) throws RodinDBException {
		if (!(root instanceof IContextRoot)) {
			throwIllArgException(root);
		}
		final IContextRoot context = (IContextRoot) root;

		final List<IRodinFile> extFiles = new ArrayList<IRodinFile>();

		final IExtendsContext[] extendsClauses = context.getExtendsClauses();

		addExtendedFiles(extendsClauses, extFiles);

		return extFiles.toArray(new IRodinFile[extFiles.size()]);
	}

	private void addExtendedFiles(IExtendsContext[] extendsClauses,
			List<IRodinFile> extendedFiles) throws RodinDBException {

		for (IExtendsContext extendsContext : extendsClauses) {
			final IRodinFile extended = getExtendedFile(extendsContext);
			if (extended != null) {
				extendedFiles.add(extended);
			}
		}
	}

	private IRodinFile getExtendedFile(IExtendsContext extendsContext)
			throws RodinDBException {
		if (!extendsContext.hasAbstractContextName()) {
			return null;
		}

		final String extBareName = extendsContext.getAbstractContextName();
		final String extFileName = getContextFileName(extBareName);

		final IRodinProject project = extendsContext.getRodinProject();

		return project.getRodinFile(extFileName);
	}

	public String getId() {
		return ID;
	}

}
