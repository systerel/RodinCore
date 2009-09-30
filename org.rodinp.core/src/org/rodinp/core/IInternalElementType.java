/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.core;


/**
 * Common protocol for internal element types, that is the types associated to Rodin
 * internal elements. Internal element types are contributed to the Rodin database
 * through extension point <code>org.rodinp.core.internalElementTypes</code>.
 * <p>
 * Element type instances are guaranteed to be unique. Hence, two element types
 * can be compared directly using identity (<code>==</code>). Instances can
 * be obtained using the static factory method
 * <code>RodinCore.getInternalElementType()</code>.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 * 
 * @see RodinCore#getInternalElementType(String)
 * @since 1.0
 */
public interface IInternalElementType<T extends IInternalElement> extends
		IElementType<T> {

	// No additional method
	
}
