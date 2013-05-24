/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.elementdesc;

import org.eventb.ui.itemdescription.IElementDesc;
import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinElement;

public interface IElementDescRegistry {

	enum Column {
		LABEL, CONTENT;

		public int getId() {
			return ordinal();
		}
		
		public static Column valueOf(int id) {
			return values()[id];
		}
	}

	/**
	 * Return an element description for given element type
	 * 
	 * @param type
	 *            an internal element type. This must not be <code>null</code>.
	 * @return an {@link ElementDesc} or <code>null</code>.
	 */
	public IElementDesc getElementDesc(IElementType<?> type);

	/**
	 * Return an element description for the type of the given element
	 * 
	 * @param element
	 *            an internal element. This must not be <code>null</code>.
	 * @return an {@link ElementDesc} or <code>null</code>.
	 */
	public IElementDesc getElementDesc(IRodinElement element);

	public String getValueAtColumn(IRodinElement element, Column column);

}
