/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast;

import org.eventb.internal.core.ast.Position;

/**
 * Common protocol for a position in a formula. A position denotes a node in a
 * formula tree. By extension, this node is also sometimes considered as a
 * sub-tree, that sub-tree which is rooted at this node.
 * <p>
 * Positions are ordered using pre-order traversal of a formula tree. A position
 * is considered less than another position iff the node designated by the first
 * position occurs before the node designated by the second position in a
 * pre-order (top-down) traversal of a formula tree.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 */
public interface IPosition extends Comparable<IPosition> {

	/**
	 * The position of the root node of a formula.
	 */
	IPosition ROOT = Position.getRoot();

	/**
	 * Compares this position with the given position, using the order induces
	 * by pre-order (top-down) traversal of a formula tree.
	 * 
	 * @return a negative integer, zero, or a positive integer as this position
	 *         is less than, equal to, or greater than the specified position.
	 */
	int compareTo(IPosition other);

	/**
	 * Returns the position of the parent node of the node designated by this
	 * position.
	 * <p>
	 * This position must not be a root position.
	 * </p>
	 * 
	 * @return the position of the parent
	 * @see #isRoot()
	 */
	IPosition getParent();

	/**
	 * Tells whether this position denotes the root of a formula.
	 * 
	 * @return <code>true</code> iff this position denotes the root of a
	 *         formula
	 */
	boolean isRoot();

}