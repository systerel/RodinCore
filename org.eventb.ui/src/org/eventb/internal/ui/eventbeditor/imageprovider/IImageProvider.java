/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.imageprovider;

import org.eclipse.jface.resource.ImageDescriptor;
import org.rodinp.core.IRodinElement;

/**
 * An image provider.
 */
public interface IImageProvider {
	/**
	 * Returns the image descriptor for the given element.
	 * 
	 * @param element
	 *            the element for which an image descriptor is requested
	 * 
	 * @return the image descriptor used to decorate the element, or
	 *         <code>null</code> if there is no image associated to the given
	 *         object
	 */
	public ImageDescriptor getImageDescriptor(IRodinElement element);
}
