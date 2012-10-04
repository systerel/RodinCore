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

	@Override
	public String toString() {
		// FIXME implement for debugging purposes
		return super.toString();
	}

}
