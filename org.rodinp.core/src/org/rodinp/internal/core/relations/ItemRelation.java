/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.relations;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElementType;
import org.rodinp.internal.core.AttributeType;
import org.rodinp.internal.core.InternalElementType;

public class ItemRelation {

	private final InternalElementType<?> parentType;
	private final List<InternalElementType<?>> childTypes;
	private final List<AttributeType<?>> attributeTypes;

	public ItemRelation(InternalElementType<?> parentType) {
		this.parentType = parentType;
		this.childTypes = new ArrayList<InternalElementType<?>>();
		this.attributeTypes = new ArrayList<AttributeType<?>>();
	}

	public InternalElementType<?> getParentType() {
		return parentType;
	}

	public List<InternalElementType<?>> getChildTypes() {
		return unmodifiableList(childTypes);
	}

	public List<AttributeType<?>> getAttributeTypes() {
		return unmodifiableList(attributeTypes);
	}

	public void addChildType(InternalElementType<?> childType) {
		childTypes.add(childType);
	}

	public void addAttributeType(AttributeType<?> attributeTypeId) {
		attributeTypes.add(attributeTypeId);
	}
	
	public boolean isValid() {
		return parentType != null
				&& (!(childTypes.isEmpty()) || !(attributeTypes.isEmpty()));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 17;
		result = prime * result + parentType.hashCode();
		result = prime * result + childTypes.hashCode();
		result = prime * result + attributeTypes.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj.getClass() != ItemRelation.class)
			return false;
		final ItemRelation other = ((ItemRelation) obj);
		return this.parentType.equals(other.parentType)
				&& this.childTypes.equals(other.childTypes)
				&& this.attributeTypes.equals(other.attributeTypes);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Relation : ");
		sb.append(parentType);
		sb.append("\n");
		for (IInternalElementType<?> child : childTypes) {
			final String childId = child.getId();
			sb.append("|-- childType : ");
			sb.append(childId);
			sb.append("\n");
		}
		for (IAttributeType attr : attributeTypes) {
			final String attrId = attr.getId();
			sb.append("|-- attributeType : ");
			sb.append(attrId);
			sb.append("\n");
		}
		return sb.toString();
	}

}
