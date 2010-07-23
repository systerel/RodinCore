/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.core;

/**
 * Common protocol for Rodin element types. Every Rodin element has an
 * associated type that is described by instances of this interface. Most
 * notably, an element type has a unique identifier and a human-readable name.
 * <p>
 * Element types are grouped into four categories:
 * <ul>
 * <li>The database element type which is predefined and unique.</li>
 * <li>The project element type which is also predefined and unique.</li>
 * <li>The file element type which is also predefined and unique.</li>
 * <li>Internal element types that are contributed by clients (see
 * {@link IInternalElementType}).</li>
 * </ul>
 * </p>
 * <p>
 * Element type instances are guaranteed to be unique. Hence, two element types
 * can be compared directly using identity (<code>==</code>). Instances can
 * be obtained using the static factory method
 * <code>RodinCore.getElementType()</code>.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 *
 * @see IRodinDB#ELEMENT_TYPE
 * @see IRodinProject#ELEMENT_TYPE
 * @see IRodinFile#ELEMENT_TYPE
 * @see RodinCore#getElementType(String)
 * @see RodinCore#getInternalElementType(String)
 * @since 1.0
 */
public interface IElementType<T extends IRodinElement> {

	/**
	 * Returns the unique identifier of this element type.
	 * 
	 * @return the unique identifier of this element type
	 */
	String getId();

	/**
	 * Returns the human-readable name of this element type.
	 * 
	 * @return the name of this element type
	 */
	String getName();

	/**
	 * Returns a string representation of this element type, that is its unique
	 * identifier.
	 * 
	 * @return the unique identifier of this element type
	 */
	@Override
	String toString();

}
