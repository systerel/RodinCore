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

import static org.eventb.core.indexer.EventBIndexUtil.REFERENCE;

import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.indexer.SymbolTable.IdentTable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IIndexingToolkit;
import org.rodinp.core.index.IRodinLocation;

/**
 * @author Nicolas Beauger
 * 
 */
public class FreeIdentIndexer extends DefaultVisitor {
	
	private final IInternalElement visited;
	private final IdentTable visibleIdents;
	private final IIndexingToolkit index;



	public FreeIdentIndexer(IInternalElement visited,
			IdentTable visibleIdents, IIndexingToolkit index) {
		this.visited = visited;
		this.visibleIdents = visibleIdents;
		this.index = index;
	}



	@Override
	public boolean visitFREE_IDENT(FreeIdentifier ident) {
		final SourceLocation srcLoc = ident.getSourceLocation();

		if (visibleIdents.contains(ident)) {
			final IDeclaration declaration = visibleIdents.get(ident);
			final IRodinLocation loc = EventBIndexUtil.getRodinLocation(
					visited, EventBAttributes.PREDICATE_ATTRIBUTE, srcLoc);

			index.addOccurrence(declaration, REFERENCE, loc);
		}
		return true;
	}


}
