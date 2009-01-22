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
import org.eventb.internal.ui.EventBImage;
import org.rodinp.core.IElementType;

/**
 * This interface is used for declaring how to display an element
 */
public class ElementDesc extends ItemDesc implements IElementDesc {

	private final String childrenSuffix;

	private final String image;

	private final IAttributeDesc[] attributeDesc;

	private final IAttributeDesc[] atColumn;

	private final IElementType<?>[] childrenType;

	private final int defaultColumn;

	private final String autoNamePrefix;

	private final IAttributeDesc autoNameAttribute;

	public ElementDesc(String prefix, String childrenSuffix, String image,
			IAttributeDesc[] attributeDesc, IAttributeDesc[] atColumn,
			IElementType<?>[] childrenType, String autoNamePrefix,
			IAttributeDesc autoNameAttribute, int defaultColumn) {
		super(prefix);
		this.childrenSuffix = childrenSuffix;
		this.image = image;
		this.attributeDesc = attributeDesc;
		this.atColumn = atColumn;
		this.childrenType = childrenType;
		this.autoNamePrefix = autoNamePrefix;
		this.defaultColumn = defaultColumn;
		this.autoNameAttribute = autoNameAttribute;
	}

	public ImageDescriptor createImageDescriptor() {
		return EventBImage.getImageDescriptor(image);
	}

	/**
	 * The suffix for the last child of the element
	 */
	public String getChildrenSuffix() {
		return childrenSuffix;
	}

	public String getImageName() {
		return image;
	}

	/**
	 * Return an array of attribute description.
	 * 
	 * @return an array of {@link AttributeDesc}. This must not be
	 *         <code>null</code>.
	 * 
	 */
	public IAttributeDesc[] getAttributeDescription() {
		return attributeDesc;
	}

	public IAttributeDesc atColumn(int column) {
		if (column < 0 || atColumn.length <= column)
			return null;
		return atColumn[column];
	}

	public int getDefaultColumn() {
		return defaultColumn;
	}

	public boolean isSelectable(int i) {
		return (0 <= i && i < atColumn.length);
	}

	public IElementType<?>[] getChildTypes() {
		return childrenType;
	}

	public String getAutoNamePrefix() {
		return autoNamePrefix;
	}

	public IAttributeDesc getAutoNameAttribute() {
		return autoNameAttribute;
	}

}
