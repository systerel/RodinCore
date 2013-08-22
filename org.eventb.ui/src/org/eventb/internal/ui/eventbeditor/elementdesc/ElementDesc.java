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

import org.eventb.internal.ui.eventbeditor.imageprovider.IImageProvider;
import org.eventb.ui.prettyprint.IElementPrettyPrinter;
import org.rodinp.core.IElementType;

/**
 * This interface is used for declaring how to display an element
 */
public class ElementDesc extends ItemDesc implements IElementDesc {

	private final String childrenSuffix;

	private final IImageProvider imgProvider;

	private final AttributeDesc[] attributeDesc;

	private final AttributeDesc[] atColumn;

	private final IElementRelationship[] childRelationships;

	private final int defaultColumn;

	private final String autoNamePrefix;

	private final AttributeDesc autoNameAttribute;

	private static final NullAttributeDesc noAttribute = new NullAttributeDesc();
	
	private final IElementPrettyPrinter prettyPrinter;

	public ElementDesc(String prefix, String childrenSuffix,
			IImageProvider imgProvider, AttributeDesc[] attributeDesc,
			AttributeDesc[] atColumn,
			IElementRelationship[] childRelationships, String autoNamePrefix,
			AttributeDesc autoNameAttribute, int defaultColumn,
			IElementPrettyPrinter prettyPrinter) {
		super(prefix);
		this.childrenSuffix = childrenSuffix;
		this.imgProvider = imgProvider;
		this.attributeDesc = attributeDesc;
		this.atColumn = atColumn;
		this.childRelationships = childRelationships;
		this.autoNamePrefix = autoNamePrefix;
		this.defaultColumn = defaultColumn;
		this.autoNameAttribute = autoNameAttribute;
		this.prettyPrinter = prettyPrinter;
	}

	/**
	 * The suffix for the last child of the element
	 */
	@Override
	public String getChildrenSuffix() {
		return childrenSuffix;
	}

	@Override
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
	@Override
	public AttributeDesc[] getAttributeDescription() {
		return attributeDesc.clone();
	}

	@Override
	public AttributeDesc atColumn(int column) {
		if (column < 0 || atColumn.length <= column)
			return null;
		return atColumn[column];
	}

	@Override
	public int getDefaultColumn() {
		return defaultColumn;
	}

	@Override
	public boolean isSelectable(int i) {
		if (!(0 <= i && i < atColumn.length))
			return false;
		return !atColumn[i].equals(noAttribute);
	}

	@Override
	public IElementType<?>[] getChildTypes() {
		final int childrenLength = childRelationships.length;
		IElementType<?>[] result = new IElementType<?>[childrenLength];
		for (int i = 0; i < childrenLength; i++) {
			result[i] = childRelationships[i].getChildType();
		}
		return result;
	}

	@Override
	public IElementRelationship[] getChildRelationships() {
		return childRelationships.clone();
	}

	@Override
	public String getAutoNamePrefix() {
		return autoNamePrefix;
	}

	@Override
	public AttributeDesc getAutoNameAttribute() {
		return autoNameAttribute;
	}
	
	/**
	 * Returns the pretty printer associated with this ElementDesc, 
	 * or <code>null</code> if none.
	 */
	@Override
	public IElementPrettyPrinter getPrettyPrinter() {
		return prettyPrinter;
	}

}
