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

import org.eventb.internal.ui.eventbeditor.imageprovider.IImageProvider;
import org.rodinp.core.IElementType;

/**
 * This interface is used for declaring how to display an element
 */
public class ElementDesc extends ItemDesc implements IElementDesc {

	private final String childrenSuffix;

	private final IImageProvider imgProvider;

	private final IAttributeDesc[] attributeDesc;

	private final IAttributeDesc[] atColumn;

	private final IElementType<?>[] childrenType;

	private final int defaultColumn;

	private final String autoNamePrefix;

	private final IAttributeDesc autoNameAttribute;

	private static final NullAttributeDesc noAttribute = new NullAttributeDesc();

	public ElementDesc(String prefix, String childrenSuffix,
			IImageProvider imgProvider, IAttributeDesc[] attributeDesc,
			IAttributeDesc[] atColumn, IElementType<?>[] childrenType,
			String autoNamePrefix, IAttributeDesc autoNameAttribute,
			int defaultColumn) {
		super(prefix);
		this.childrenSuffix = childrenSuffix;
		this.imgProvider = imgProvider;
		this.attributeDesc = attributeDesc;
		this.atColumn = atColumn;
		this.childrenType = childrenType;
		this.autoNamePrefix = autoNamePrefix;
		this.defaultColumn = defaultColumn;
		this.autoNameAttribute = autoNameAttribute;
	}

	/**
	 * The suffix for the last child of the element
	 */
	public String getChildrenSuffix() {
		return childrenSuffix;
	}

	public IImageProvider getImageProvider() {
		return imgProvider;
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
		if (!(0 <= i && i < atColumn.length))
			return false;
		return !atColumn[i].equals(noAttribute);
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
