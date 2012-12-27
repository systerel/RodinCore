/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.core.IRegion
 *******************************************************************************/
package org.rodinp.core;

/**
 * A Rodin database region describes a hierarchical set of elements. Regions are
 * often used to describe a set of elements to be considered when performing
 * operations; for example, the set of elements to be considered during a
 * search. A region may include elements from different projects.
 * <p>
 * When an element is included in a region, all of its children are considered
 * to be included. Children of an included element <b>cannot</b> be selectively
 * excluded.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients. Instances can be
 * created via <code>RodinCore.newRegion</code>.
 * </p>
 * 
 * @see RodinCore#newRegion()
 * @since 1.0
 */
public interface IRegion {

	/**
	 * Adds the given element and all of its descendents to this region. If the
	 * specified element is already included, or one of its ancestors is already
	 * included, this has no effect. If the element being added is an ancestor
	 * of an element already contained in this region, the ancestor subsumes the
	 * descendent.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @param element
	 *            the given element
	 */
	void add(IRodinElement element);

	/**
	 * Returns whether the given element is contained in this region.
	 * <p>
	 * An element is contained in this region, either because it has been added
	 * directly, or because one of its antecedents has been added to this
	 * region.
	 * </p>
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @param element
	 *            the given element
	 * @return <code>true</code> if the given element is contained in this
	 *         region, <code>false</code> otherwise
	 */
	boolean contains(IRodinElement element);

	/**
	 * Returns the top level elements in this region. All descendents of these
	 * elements are also included in this region.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 *
	 * @return the top level elements in this region
	 */
	IRodinElement[] getElements();

	/**
	 * Removes the specified element from the region and returns
	 * <code>true</code> if successful, <code>false</code> if the remove
	 * fails. If an ancestor of the given element is included, the remove fails
	 * (in other words, it is not possible to selectively exclude descendants of
	 * included ancestors).
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @param element
	 *            the given element
	 * @return <code>true</code> if successful, <code>false</code> if the
	 *         remove fails
	 */
	boolean remove(IRodinElement element);
}
