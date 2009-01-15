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

import static org.eventb.core.indexer.EventBIndexUtil.*;
import static org.rodinp.core.indexer.RodinIndexer.*;

import java.util.concurrent.CancellationException;

import org.eventb.core.IPredicateElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexer;
import org.rodinp.core.indexer.IIndexingBridge;
import org.rodinp.core.location.IInternalLocation;

public abstract class EventBIndexer extends Cancellable implements IIndexer {

	public static boolean DEBUG;

	private static final IRodinFile[] NO_DEPENDENCIES = new IRodinFile[0];

	protected IIndexingBridge bridge;

	public boolean index(IIndexingBridge bridge) {
		this.bridge = bridge;
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

	protected IDeclaration indexDeclaration(IInternalElement element,
			String name) {

		final IDeclaration declaration = bridge.declare(element, name);
		final IInternalLocation loc =
				getInternalLocation(element.getRodinFile().getRoot());

		bridge.addOccurrence(declaration, DECLARATION, loc);

		return declaration;
	}

	protected void indexReference(IDeclaration declaration,
			IInternalLocation location) {
		bridge.addOccurrence(declaration, REFERENCE, location);
	}

	protected void export(IDeclaration declaration) {
		bridge.export(declaration);
	}

	protected void checkCancel() {
		checkCancel(bridge);
	}

	protected void processPredicateElements(IPredicateElement[] preds,
			SymbolTable symbolTable) throws RodinDBException {
		for (IPredicateElement elem : preds) {
			final PredicateIndexer predIndexer =
					new PredicateIndexer(elem, symbolTable, bridge);
			predIndexer.process();

			checkCancel();
		}
	}

	/**
	 * @param file
	 */
	protected void throwIllArgException(IInternalElement root) {
		throw new IllegalArgumentException("Cannot index "
				+ root
				+ ": bad element type");
	}

}