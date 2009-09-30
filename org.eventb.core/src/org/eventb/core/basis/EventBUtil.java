/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.internal.core.Util;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * @since 1.0
 *
 */
@Deprecated
public final class EventBUtil {

	private EventBUtil() {
		// no instances of this class
	}
	
	public static <T extends IRodinElement> T getSingletonChild(
			IInternalElement parent,
			IElementType<T> elementType,
			String message) throws RodinDBException {

		T[] elements = parent.getChildrenOfType(elementType);
		if (elements.length == 1)
			return elements[0];
		else if (elements.length == 0)
			return null;
		else
			throw Util.newRodinDBException(message, parent);
	}

}
