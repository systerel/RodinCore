/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.core;

import org.eclipse.core.runtime.content.IContentType;

/**
 * Common protocol for file element types, that is the types associated to Rodin
 * file elements. File element types are contributed to the Rodin database
 * through extension point <code>org.rodinp.core.fileElementTypes</code>.
 * <p>
 * Element type instances are guaranteed to be unique. Hence, two element types
 * can be compared directly using identity (<code>==</code>). Instances can
 * be obtained using the static factory method
 * <code>RodinCore.getFileElementType()</code>.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 * 
 * @see RodinCore#getFileElementType(String)
 */
public interface IFileElementType<T extends IRodinFile> extends IElementType<T> {

	/**
	 * Returns the content type associated to this file element type.
	 * All Rodin files of this type have the returned content type.
	 * 
	 * @return the content type of Rodin files of this type
	 */
	IContentType getContentType();
	
}
