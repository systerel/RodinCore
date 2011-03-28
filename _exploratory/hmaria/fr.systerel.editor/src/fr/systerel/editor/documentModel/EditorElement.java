/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.documentModel;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;

public class EditorElement extends EditorItem {

	private final ILElement element;

	public EditorElement(ILElement element) {
		this.element = element;
	}
	
	/**
	 * Returns the light element associated to this item.  
	 * 
	 * @return the element associated with this EditorItem.
	 */
	public ILElement getLightElement() {
		return element;
	}
	
	public IRodinElement getRodinElement() {
		return (IRodinElement) element.getElement();
	}

}
