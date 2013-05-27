/*******************************************************************************
 * Copyright (c) 2009, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.elementdesc;

import static org.rodinp.core.RodinCore.getAttributeType;

import org.eventb.internal.ui.eventbeditor.imageprovider.IImageProvider;
import org.eventb.ui.itemdescription.IAttributeDesc;
import org.eventb.ui.itemdescription.IElementDesc;
import org.eventb.ui.prettyprint.IElementPrettyPrinter;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElementType;

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

	/**
	 * Return the image provider which will be used to display an icon for
	 * elements concerned by this description.
	 * 
	 * @return an image provider for the elements concerned by this description
	 */
	public IImageProvider getImageProvider() {
		return imgProvider;
	}

	@Override
	public AttributeDesc[] getAttributeDescriptions() {
		return attributeDesc.clone();
	}

	/**
	 * Return the description of the attribute at column <code>i</code>, or
	 * <code>null</code> if the given index is below 0 or exceeds the number of
	 * columns for the given element description.
	 * 
	 * @param i
	 *            the index of the column to get the attribute description for
	 * @return the description of the attribute at column <code>i</code>, or
	 *         <code>null</code> if the given index is out of range
	 * 
	 */
	public AttributeDesc atColumn(int i) {
		if (i < 0 || atColumn.length <= i)
			return null;
		return atColumn[i];
	}

	/**
	 * Returns the column which shall be edited by default for the given element
	 * description.
	 * 
	 * @return the default column to edit
	 */
	public int getDefaultColumn() {
		return defaultColumn;
	}

	/**
	 * Tells if the attribute at column index <code>i</code> can be selected.
	 * 
	 * @param i
	 *            the index of the attribute to be checked
	 * @return <code>true</code> if the attribute at column <code>i</code> can
	 *         be selected, <code>false</code> otherwise
	 */
	public boolean isSelectable(int i) {
		if (!(0 <= i && i < atColumn.length))
			return false;
		return !atColumn[i].equals(noAttribute);
	}

	@Override
	public IInternalElementType<?>[] getChildTypes() {
		final int childrenLength = childRelationships.length;
		IInternalElementType<?>[] result = new IInternalElementType<?>[childrenLength];
		for (int i = 0; i < childrenLength; i++) {
			result[i] = childRelationships[i].getChildType();
		}
		return result;
	}

	/**
	 * Returns the ordered list of child relationship for which the element type
	 * concerned by this description is the parent.
	 * 
	 * @return the ordered list of element relationship whose parent is the
	 *         element type of the current element description
	 */
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
	 * Returns the pretty printer for the display of the element in the pretty
	 * print view.
	 * 
	 * @return the pretty printer for the element of this description.
	 */
	public IElementPrettyPrinter getPrettyPrinter() {
		return prettyPrinter;
	}

	@Override
	public IAttributeDesc getAttributeDescription(String attributeTypeId) {
		if (canCarry(attributeTypeId)) {
			for (IAttributeDesc desc : attributeDesc) {
				if (attributeTypeId.equals(desc.getAttributeType().getId())) {
					return desc;
				}
			}
		}
		return null;
	}

	private boolean canCarry(String attributeTypeId) {
		final IInternalElementType<?> elementType = ElementDescRegistry
				.getInstance().getElementType(this);
		final IAttributeType attrType = getAttributeType(attributeTypeId);
		if (attrType == null) {
			return false;
		}
		return elementType.canCarry(attrType);
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
