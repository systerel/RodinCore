/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/
package org.eventb.core.seqprover;

/**
 * @author htson
 *         <p>
 *         This is the interface for filters that can be used to traverse the
 *         proof tree.
 * @since 1.0
 */
public interface IProofTreeNodeFilter {

	/**
	 * Returns whether the given proof tree node makes it through this filter.
	 * <p>
	 * 
	 * @param node
	 *            the node
	 * @return <code>true</code> if node is included in the filtered set, and
	 *         <code>false</code> if excluded
	 * @see IProofTreeNode.#getNextNode(boolean, IProofTreeNodeFilter)
	 */
	public abstract boolean select(IProofTreeNode node);

}
