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
 * A default class that implements <tt>IImageProvider</tt> and always provides
 * the same image descriptor.
 * */
public class DefaultImageProvider implements IImageProvider {

	private ImageDescriptor img;

	public DefaultImageProvider(ImageDescriptor img) {
		this.img = img;
	}

	@Override
	public ImageDescriptor getImageDescriptor(IRodinElement element) {
		return img;
	}

}
