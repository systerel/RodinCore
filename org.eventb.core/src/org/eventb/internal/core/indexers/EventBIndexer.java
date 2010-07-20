/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.indexers;

import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBPlugin.DECLARATION;
import static org.eventb.core.EventBPlugin.REDECLARATION;
import static org.rodinp.core.RodinCore.getInternalLocation;

import java.util.concurrent.CancellationException;

import org.eventb.core.IEventBRoot;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IPredicateElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexer;
import org.rodinp.core.indexer.IIndexingBridge;
import org.rodinp.core.location.IInternalLocation;

public abstract class EventBIndexer extends Cancellable implements IIndexer {

	public static boolean DEBUG;

	private static final IRodinFile[] NO_DEPENDENCIES = new IRodinFile[0];

	protected String getIdentifierName(IIdentifierElement ident)
			throws RodinDBException {
		if (ident.hasIdentifierString()) {
			final String name = ident.getIdentifierString();
			if (isValidIdentifierName(name)) {
				return name;
			}
		}
		return null;
	}

	protected boolean isValidIdentifierName(String name) {
		final IEventBRoot root = (IEventBRoot) currentBridge.getRootToIndex();
		return root.getFormulaFactory().isValidIdentifierName(name);
	}
	
	protected IIndexingBridge currentBridge;

	public final boolean index(IIndexingBridge bridge) {
		this.currentBridge = bridge;
		final IInternalElement root = bridge.getRootToIndex();

		try {
			index(root);
			return true;
		} catch (RodinDBException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			return false;
		} catch (CancellationException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			return false;
		}
	}

	protected abstract void index(IInternalElement root)
			throws RodinDBException;

	public IRodinFile[] getDependencies(IInternalElement root) {
		try {
			return getDeps(root);
		} catch (RodinDBException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			return NO_DEPENDENCIES;
		}
	}

	protected abstract IRodinFile[] getDeps(IInternalElement root)
			throws RodinDBException;

	protected IDeclaration indexAndExportRoot(IEventBRoot root) {
		final IDeclaration rootDecl = currentBridge.declare(root, root
				.getElementName());
		currentBridge.addOccurrence(rootDecl, DECLARATION, RodinCore
				.getInternalLocation(root));
		currentBridge.export(rootDecl);

		return rootDecl;
	}
	
	// the identifier must be checked non empty before calling this method
	protected IDeclaration indexDeclaration(IIdentifierElement ident,
			String identifier) {

		final IDeclaration declaration = currentBridge.declare(ident, identifier);
		final IInternalLocation loc = getInternalLocation(ident,
				IDENTIFIER_ATTRIBUTE);

		currentBridge.addOccurrence(declaration, DECLARATION, loc);

		return declaration;
	}

	protected void indexRedeclaration(IDeclaration declaration,
			IInternalLocation location) {
		currentBridge.addOccurrence(declaration, REDECLARATION, location);
	}

	protected void export(IDeclaration declaration) {
		currentBridge.export(declaration);
	}

	protected void checkCancel() {
		checkCancel(currentBridge);
	}

	protected void processPredicateElements(IPredicateElement[] preds,
			SymbolTable symbolTable) throws RodinDBException {
		for (IPredicateElement elem : preds) {
			final PredicateIndexer predIndexer =
					new PredicateIndexer(elem, symbolTable, currentBridge);
			predIndexer.process();

			checkCancel();
		}
	}

	protected void throwIllArgException(IInternalElement root) {
		throw new IllegalArgumentException("Cannot index "
				+ root
				+ ": bad element type");
	}

}