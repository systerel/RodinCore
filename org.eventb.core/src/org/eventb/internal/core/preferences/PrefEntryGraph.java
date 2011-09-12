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

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.preferences.IReferenceMaker;
import org.eventb.internal.core.preferences.PreferenceUtils.UnresolvedPrefMapEntry;
import org.eventb.internal.core.tool.graph.Graph;
import org.eventb.internal.core.tool.graph.Node;

/**
 * @author Nicolas Beauger
 * 
 */
public class PrefEntryGraph<T> extends Graph<IPrefMapEntry<T>> {

	private static final String[] NO_PREDECS = new String[0];
	
	private final IReferenceMaker<T> refMaker;

	private final List<Node<IPrefMapEntry<T>>> nodes = new ArrayList<Node<IPrefMapEntry<T>>>();
	
	public PrefEntryGraph(String creator, IReferenceMaker<T> refMaker) {
		super(creator);
		this.refMaker = refMaker;
	}

	@Override
	protected Node<IPrefMapEntry<T>> createNode(IPrefMapEntry<T> entry) {
		final T value = entry.getValue();
		final String[] predecs;
		if (value == null) { // unresolved entry
			predecs = NO_PREDECS;
		} else {
			predecs = refMaker.getReferencedKeys(value);
		}
		final Node<IPrefMapEntry<T>> node = new Node<IPrefMapEntry<T>>(entry,
				entry.getKey(), predecs, this);
		nodes.add(node);
		return node;
	}

	/**
	 * Adds placeholders for unresolved references in this graph, except for the
	 * given key.
	 * 
	 * @param exceptKey
	 *            a key or <code>null</code>
	 * @return the set of ids of added placeholders
	 */
	public Set<String> addUnresolvedExcept(String exceptKey) {
		final Set<String> ids = new HashSet<String>();
		final Set<String> refs = new LinkedHashSet<String>();
		for (Node<IPrefMapEntry<T>> node : nodes) {
			ids.add(node.getId());
			refs.addAll(asList(node.getPredecs()));
		}
		refs.removeAll(ids);
		refs.remove(exceptKey);
		for (String ref : refs) {
			add(new UnresolvedPrefMapEntry<T>(ref));
		}
		return refs;
	}
}
