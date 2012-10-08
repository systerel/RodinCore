/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
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

public class ItemRelation {

	private final String parentTypeId;
	private final List<String> childTypeIds;
	private final List<String> attributeTypeIds;

	public ItemRelation(String parentTypeId) {
		this.parentTypeId = parentTypeId;
		this.childTypeIds = new ArrayList<String>();
		this.attributeTypeIds = new ArrayList<String>();
	}

	public String getParentTypeId() {
		return parentTypeId;
	}

	public List<String> getChildTypeIds() {
		return unmodifiableList(childTypeIds);
	}

	public List<String> getAttributeTypeIds() {
		return unmodifiableList(attributeTypeIds);
	}

	public void addChildTypeId(String childTypeId) {
		childTypeIds.add(childTypeId);
	}

	public void addAttributeTypeId(String attributeTypeId) {
		attributeTypeIds.add(attributeTypeId);
	}
	
	public boolean isValid() {
		return parentTypeId != null
				&& (!(childTypeIds.isEmpty()) || !(attributeTypeIds.isEmpty()));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 17;
		result = prime * result + parentTypeId.hashCode();
		result = prime * result + childTypeIds.hashCode();
		result = prime * result + attributeTypeIds.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ItemRelation))
			return false;
		final ItemRelation item = ((ItemRelation)obj);
		return childTypeIds.equals(item.getChildTypeIds()) 
				&& attributeTypeIds.equals(item.getAttributeTypeIds());
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Relation : ");
		sb.append(parentTypeId);
		sb.append("\n");
		for (String childId : childTypeIds) {
			sb.append("|-- childType : ");
			sb.append(childId);
			sb.append("\n");
		}
		for (String attrId : attributeTypeIds) {
			sb.append("|-- attributeType : ");
			sb.append(attrId);
			sb.append("\n");
		}
		return sb.toString();
	}

}
