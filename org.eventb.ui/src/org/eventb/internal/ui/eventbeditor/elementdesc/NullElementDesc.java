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
import org.rodinp.core.IElementType;

class NullElementDesc implements IElementDesc {

	private final IAttributeDesc nullAttribute = new NullAttributeDesc();
	private final AttributeDesc[] nullAttributes = new AttributeDesc[0];
	private final IElementType<?>[] nullChildren = new IElementType<?>[0];

	public AttributeDesc atColumn(int i) {
		return (AttributeDesc) nullAttribute;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
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
