/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.preferences;

import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.preferences.IReferenceMaker;
import org.eventb.internal.core.tool.graph.Graph;
import org.eventb.internal.core.tool.graph.Node;

/**
 * @author Nicolas Beauger
 * 
 */
public class PrefEntryGraph<T> extends Graph<IPrefMapEntry<T>> {

	private final IReferenceMaker<T> refMaker;

	public PrefEntryGraph(String creator, IReferenceMaker<T> refMaker) {
		super(creator);
		this.refMaker = refMaker;
	}

	@Override
	protected Node<IPrefMapEntry<T>> createNode(IPrefMapEntry<T> entry) {
		final String[] predecs = refMaker.getReferencedKeys(entry.getValue());
		return new Node<IPrefMapEntry<T>>(entry, entry.getKey(), predecs, this);
	}

}
