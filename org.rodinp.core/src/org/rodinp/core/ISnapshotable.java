/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.core;

/**
 * Common protocol for elements that have two versions: a snapshot and a mutable
 * copy.
 * <p>
 * Such elements are Rodin files and internal elements themselves (which are
 * descendants of a Rodin file).
 * </p>
 * <p>
 * For each Rodin file, the database provides two versions:
 * <ul>
 * <li>a stable snapshot that corresponds to the contents of the Rodin file on
 * disk, and which is read-only.</li>
 * <li>a buffered copy of the Rodin file in memory which is read-write.</li>
 * </ul>
 * As a consequence, there are two kinds of handles for these elements, stable
 * snapshot handles and mutable handles.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 */
public interface ISnapshotable extends IRodinElement {

	/**
	 * Returns a handle to this element in the snapshot of its Rodin file.
	 * 
	 * <p>
	 * This is a handle-only method. The element may or may not be present.
	 * </p>
	 * 
	 * @return this element in the snapshot of its Rodin file
	 */
	ISnapshotable getSnapshot();

	/**
	 * Returns a handle to this element in the mutable copy of its Rodin file.
	 * 
	 * <p>
	 * This is a handle-only method. The element may or may not be present.
	 * </p>
	 * 
	 * @return this element in the mutable copy of its Rodin file
	 */
	ISnapshotable getMutableCopy();

	/**
	 * Returns whether this is a handle in a file snapshot.
	 * <p>
	 * This is a handle-only method. The element may or may not be present.
	 * </p>
	 * 
	 * @return <code>true</code> iff the corresponding element is or belongs
	 *         to the stable snapshot of a Rodin file
	 */
	boolean isSnapshot();

}
