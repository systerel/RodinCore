/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - optimized the equals() method
 *******************************************************************************/
package org.eventb.internal.ui.elementSpecs;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

public class AttributeRelationship implements IAttributeRelationship {

	private String id;

	private IInternalElementType<?> elementType;

	private IAttributeType attributeType;
	
	public AttributeRelationship(String id,
			IInternalElementType<?> elementType, String attributeName) {
		this.id = id;
		this.elementType = elementType;
		attributeType = RodinCore.getAttributeType(attributeName);
	}

	public String getID() {
		return id;
	}

	public IInternalElementType<?> getElementType() {
		return elementType;
	}

	@Override
	public String toString() {
		return id + " --> " + elementType;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AttributeRelationship))
			return false;
		
		AttributeRelationship rel = (AttributeRelationship) obj;
		return id.equals(rel.getID()) && elementType == rel.getElementType();
	}

	public IAttributeType getAttributeType() {
		return attributeType;
	}
	
}
