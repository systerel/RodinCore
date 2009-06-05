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
package org.eventb.internal.ui.eventbeditor.elementdesc;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eventb.internal.ui.eventbeditor.imageprovider.IImageProvider;
import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinElement;

class NullElementDesc implements IElementDesc {

	private static final IImageProvider nullImgProvider = new IImageProvider() {
		public ImageDescriptor getImageDescriptor(IRodinElement element) {
			return null;
		}
	};
	private final IAttributeDesc nullAttribute = new NullAttributeDesc();
	private final AttributeDesc[] nullAttributes = new AttributeDesc[0];
	private final IElementType<?>[] nullChildren = new IElementType<?>[0];

	public AttributeDesc atColumn(int i) {
		return (AttributeDesc) nullAttribute;
	}

	public IImageProvider getImageProvider() {
		return nullImgProvider;
	}

	public AttributeDesc[] getAttributeDescription() {
		return nullAttributes;
	}

	public String getChildrenSuffix() {
		return "";
	}

	public IElementType<?>[] getChildTypes() {
		return nullChildren;
	}

	public int getDefaultColumn() {
		return -1;
	}

	public boolean isSelectable(int i) {
		return false;
	}

	public String getPrefix() {
		return "";
	}

	public String getAutoNamePrefix() {
		return "";
	}

	public IAttributeDesc getAutoNameAttribute() {
		return nullAttribute;
	}
}
