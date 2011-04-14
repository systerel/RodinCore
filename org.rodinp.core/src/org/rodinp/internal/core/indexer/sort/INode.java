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
package org.rodinp.internal.core.indexer.sort;

import java.util.List;

/**
 * Common protocol for graph nodes.
 * 
 * @author Nicolas Beauger
 * 
 * @param <T>
 */
public interface INode<T, N extends INode<T, N>> {

	/**
	 * Returns the label associated to this node.
	 * 
	 * @return a label
	 */
	T getLabel();

	/**
	 * Returns the degree of this node, i.e the number of its predecessors.
	 * 
	 * @return a positive or null integer
	 */
	int degree();

	/**
	 * Returns a list (possibly empty) of successor nodes.
	 * 
	 * @return a list of nodes
	 */
	List<N> getSuccessors();

}