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

import java.util.List;

import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IIdentifierElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.indexer.IDeclaration;

/**
 * @author Nicolas Beauger
 * 
 */
public class ContextIndexer extends EventBIndexer {

	private static final String ID = "fr.systerel.eventb.indexer.context";

	private static DependenceIndexer<IExtendsContext> extendsIndexer = new DependenceIndexer<IExtendsContext>() {

		@Override
		protected boolean hasName(IExtendsContext clause)
				throws RodinDBException {
			return clause.hasAbstractContextName();
		}

		@Override
		protected String getName(IExtendsContext clause)
				throws RodinDBException {
			return clause.getAbstractContextName();
		}

		@Override
		protected IEventBRoot getRoot(IExtendsContext clause)
				throws RodinDBException {
			return clause.getAbstractContextRoot();
		}
	};

	@Override
	protected void index(IInternalElement root) throws RodinDBException {
		if (!(root instanceof IContextRoot)) {
			throwIllArgException(root);
		}
		index((IContextRoot) root);
	}

	private void index(IContextRoot root) throws RodinDBException {
		checkCancel();
		
		indexAndExportRoot(root);
		checkCancel();
		
		processExtends(root.getExtendsClauses());
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

	private void processExtends(IExtendsContext[] extendsClauses)
			throws RodinDBException {
		extendsIndexer.process(currentBridge, extendsClauses);
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
			final String name = getIdentifierName(ident);
			if (name != null) {
				final IDeclaration declaration = indexDeclaration(ident, name);
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

		final IExtendsContext[] extendsClauses = context.getExtendsClauses();
		final List<IRodinFile> deps = extendsIndexer.getDeps(extendsClauses);

		return deps.toArray(new IRodinFile[deps.size()]);
	}

	@Override
	public String getId() {
		return ID;
	}

}
