/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
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
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IElementPrettyPrinter;
import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinElement;

class NullElementDesc implements IElementDesc {

	private static final IImageProvider nullImgProvider = new IImageProvider() {
		@Override
		public ImageDescriptor getImageDescriptor(IRodinElement element) {
			return null;
		}
	};
	
	private static final IElementPrettyPrinter nullPrettyPrinter = new DefaultPrettyPrinter();
	
	private final IAttributeDesc nullAttribute = new NullAttributeDesc();
	private final AttributeDesc[] nullAttributes = new AttributeDesc[0];
	private final IElementRelationship[] nullRelationships = new IElementRelationship[0];
	private final IElementType<?>[] nullChildren = new IElementType<?>[0];

	@Override
	public AttributeDesc atColumn(int i) {
		return (AttributeDesc) nullAttribute;
	}

	@Override
	public IImageProvider getImageProvider() {
		return nullImgProvider;
	}

	@Override
	public AttributeDesc[] getAttributeDescription() {
		return nullAttributes;
	}

	@Override
	public String getChildrenSuffix() {
		return "";
	}

	@Override
	public IElementRelationship[] getChildRelationships() {
		return nullRelationships;
	}

	@Override
	public int getDefaultColumn() {
		return -1;
	}

	@Override
	public boolean isSelectable(int i) {
		return false;
	}

	@Override
	public String getPrefix() {
		return "";
	}

	@Override
	public String getAutoNamePrefix() {
		return "";
	}

	@Override
	public IAttributeDesc getAutoNameAttribute() {
		return nullAttribute;
	}

	@Override
	public IElementPrettyPrinter getPrettyPrinter() {
		return nullPrettyPrinter;
	}

	@Override
	public IElementType<?>[] getChildTypes() {
		return nullChildren;
	}
}
