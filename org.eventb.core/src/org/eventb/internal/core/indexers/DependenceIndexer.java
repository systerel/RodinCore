/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.indexers;

import static org.eventb.core.EventBAttributes.TARGET_ATTRIBUTE;
import static org.eventb.core.EventBPlugin.REFERENCE;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.IEventBRoot;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexingBridge;
import org.rodinp.core.location.IAttributeLocation;

public abstract class DependenceIndexer<T extends IInternalElement> {

	public List<IRodinFile> getDeps(T[] clauses) throws RodinDBException {
		final List<IRodinFile> result = new ArrayList<IRodinFile>();
		for (T clause : clauses) {
			if (!hasName(clause)) {
				continue;
			}
			final IEventBRoot root = getRoot(clause);
			result.add(root.getRodinFile());
		}
		return result;
	}

	public void process(IIndexingBridge bridge, T[] clauses)
			throws RodinDBException {
		for (T clause : clauses) {
			if (!hasName(clause)) {
				continue;
			}
			final String name = getName(clause);
			final IEventBRoot root = getRoot(clause);
			indexTargetReference(bridge, root, name, clause);
		}
	}

	protected abstract boolean hasName(T clause) throws RodinDBException;

	protected abstract String getName(T clause) throws RodinDBException;

	protected abstract IEventBRoot getRoot(T clause) throws RodinDBException;

	private void indexTargetReference(IIndexingBridge bridge, IEventBRoot root,
			String name, IInternalElement element) {
		IDeclaration rootDecl = findDeclaration(root, bridge.getImports());
		if (rootDecl == null) {
			return;
		}
		final IAttributeLocation location = RodinCore.getInternalLocation(
				element, TARGET_ATTRIBUTE);
		bridge.addOccurrence(rootDecl, REFERENCE, location);
	}

	private IDeclaration findDeclaration(IInternalElement element,
			IDeclaration[] declarations) {
		IDeclaration result = null;
		for (IDeclaration decl : declarations) {
			if (element.equals(decl.getElement())) {
				result = decl;
				break;
			}
		}
		return result;
	}

}