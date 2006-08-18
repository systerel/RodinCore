/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.basis.InternalElement;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author Stefan Hallerstede
 *
 */
class CommonAttributesUtil {

	public static String getLabel(InternalElement element) {
		return element.getElementName();
	}
	
	public static void setLabel(InternalElement element, String label) {
// TODO implement labels as attributes
		throw new NotImplementedException();
	}

	public static IInternalElement getSource(InternalElement element) {
		return element;
	}
	
	public static void setSource(InternalElement element, IInternalElement source) {
// TODO implement labels as attributes
		throw new NotImplementedException();
	}

}
