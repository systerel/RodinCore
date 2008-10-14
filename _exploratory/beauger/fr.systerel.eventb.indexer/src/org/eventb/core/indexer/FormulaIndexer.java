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

import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.SourceLocation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IIndexingToolkit;
import org.rodinp.core.index.IRodinLocation;

/**
 * @author Nicolas Beauger
 * 
 */
public class FormulaIndexer extends DefaultVisitor {
	
	private final IInternalElement visited;
	private final IAttributeType.String attributeType;
	private final IdentTable visibleIdents;
	private final IIndexingToolkit index;



	public FormulaIndexer(IInternalElement visited, IAttributeType.String attributeType,
			IdentTable visibleIdents, IIndexingToolkit index) {
		this.visited = visited;
		this.attributeType = attributeType;
		this.visibleIdents = visibleIdents;
		this.index = index;
	}



	@Override
	public boolean visitFREE_IDENT(FreeIdentifier ident) {
		if (ident.isPrimed()) {
			ident = ident.withoutPrime(FormulaFactory.getDefault());
		}
		
		if (visibleIdents.contains(ident)) {
			final IDeclaration declaration = visibleIdents.get(ident);
			final SourceLocation srcLoc = ident.getSourceLocation();
			final IRodinLocation loc = EventBIndexUtil.getRodinLocation(
					visited, attributeType, srcLoc);

			index.addOccurrence(declaration, REFERENCE, loc);
		}
		return true;
	}


}
