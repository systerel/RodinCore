/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added child indexes
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
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
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
	@Override
	int compareTo(IPosition other);

	/**
	 * Returns the position of the first child node of the node designated by
	 * this position. No attempt is made to check that this position indeed
	 * denotes a node in some formula.
	 * 
	 * @return the position of the first child
	 */
	IPosition getFirstChild();

	/**
	 * Returns the position of the next sibling node of the node designated by
	 * this position. No attempt is made to check that the returned position
	 * indeed denotes a node in some formula.
	 * <p>
	 * This position must not be a root position.
	 * </p>
	 * 
	 * @return the position of the next sibling
	 * @see #isRoot()
	 * @throws IllegalStateException
	 *             if this position is a root position.
	 */
	IPosition getNextSibling();

	/**
	 * Returns the position of the <code>n+1</code>th child node of the node
	 * designated by this position. No attempt is made to check that the
	 * returned position indeed denotes a node in some formula.
	 * <p>
	 * Calling this method is strictly equivalent to calling
	 * {@link #getFirstChild()} followed by <code>n</code> calls to
	 * {@link #getNextSibling()}. The first child is thus obtained by passing
	 * <code>0</code> as argument.
	 * </p>
	 * 
	 * @return the position of the <code>n+1</code>th child
	 * @throws IllegalStateException
	 *             if <code>n</code> is negative
	 * @since 2.1
	 */
	IPosition getChildAtIndex(int n);

	/**
	 * Returns the position of the parent node of the node designated by this
	 * position.
	 * <p>
	 * This position must not be a root position.
	 * </p>
	 * 
	 * @return the position of the parent
	 * @see #isRoot()
	 * @throws IllegalStateException
	 *             if this position is a root position.
	 */
	IPosition getParent();

	/**
	 * Returns the position of the previous sibling node of the node designated
	 * by this position.
	 * <p>
	 * This position must not be a root position, nor a first child position.
	 * </p>
	 * 
	 * @return the position of the previous sibling
	 * @see #isRoot()
	 * @throws IllegalStateException
	 *             if this position is a root position or a first child
	 *             position.
	 */
	IPosition getPreviousSibling();

	/**
	 * Tells whether this position denotes the first child of a formula node.
	 * 
	 * @return <code>true</code> iff this position denotes the first child of
	 *         a node of a formula
	 */
	boolean isFirstChild();

	/**
	 * Tells whether this position denotes the root of a formula.
	 * 
	 * @return <code>true</code> iff this position denotes the root of a
	 *         formula
	 */
	boolean isRoot();

	/**
	 * Returns the child index of this position, related to its parent position,
	 * counting from <code>0</code> for the first child.
	 * <p>
	 * This position must not be a root position.
	 * </p>
	 * 
	 * @return the child index of this position related to its parent
	 * @see #isRoot()
	 * @throws IllegalStateException
	 *             if this position is a root position
	 * @since 2.1
	 */
	int getChildIndex();

	/**
	 * Returns a string representation of this position. Such a string can be
	 * persisted and this position retrieved later using the appropriate factory
	 * method.
	 * 
	 * @see FormulaFactory#makePosition(String)
	 */
	@Override
	String toString();

}