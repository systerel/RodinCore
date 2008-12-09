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
package org.rodinp.internal.core.index.persistence;

import java.util.List;

import org.rodinp.internal.core.index.sort.Node;

/**
 * @author Nicolas Beauger
 * 
 */
public class PersistentSortedNodes<T> {

	final List<Node<T>> nodes;
	final List<T> iterated;

	public PersistentSortedNodes(List<Node<T>> nodes, List<T> iterated) {
		this.nodes = nodes;
		this.iterated = iterated;
	}

	public List<Node<T>> getNodes() {
		return nodes;
	}

	public List<T> getIterated() {
		return iterated;
	}

}
