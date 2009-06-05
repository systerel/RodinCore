/*******************************************************************************
* Copyright (c) 2008 Systerel and others.
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

public interface IElementDesc extends IItemDesc {

	/**
	 * The suffix for the last child of the element
	 */
	public String getChildrenSuffix();

	public IImageProvider getImageProvider();

	/**
	 * Return an array of attribute description.
	 * 
	 * @return an array of {@link AttributeDesc}. This must not be
	 *         <code>null</code>.
	 * 
	 */
	public IAttributeDesc[] getAttributeDescription();

	public IAttributeDesc atColumn(int i);

	public int getDefaultColumn();

	public boolean isSelectable(int i);

	public IElementType<?>[] getChildTypes();

	public String getAutoNamePrefix();

	public IAttributeDesc getAutoNameAttribute();
}
