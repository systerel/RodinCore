/*******************************************************************************
 * Copyright (c) 2009, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.elementdesc;

import org.eventb.ui.IImplicitChildProvider;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElementType;

public class ElementDescRelationship implements IElementRelationship {

	private final IInternalElementType<?> childType;

	private final String id;

	private final IElementType<?> parentType;
	
	private final int priority;

	private final IImplicitChildProvider childProvider;

	private final String prefix;

	public ElementDescRelationship(IElementType<?> parentType,
			IInternalElementType<?> childType, int priority, String prefix, IImplicitChildProvider childProvider) {
		this.childType = childType;
		this.parentType = parentType;
		this.priority = priority;
		this.childProvider = childProvider;
		this.prefix = prefix;
		id = parentType.getId() + childType.getId();
	}

	@Override
	public IInternalElementType<?> getChildType() {
		return childType;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public IElementType<?> getParentType() {
		return parentType;
	}
	
	@Override
	public int getPriority() {
		return priority;
	}

	@Override
	public IImplicitChildProvider getImplicitChildProvider() {
		return childProvider;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + childType.hashCode();
		result = prime * result + parentType.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final ElementDescRelationship other = (ElementDescRelationship) obj;
		return childType == other.childType && parentType == other.parentType;
	}

	@Override
	public int compareTo(IElementRelationship rs) {
		if (childType == rs.getChildType() && parentType == rs.getParentType())
			return 0;
		final int diff = priority - rs.getPriority();
		if (diff != 0) {
			return diff;
		}
		return childType.getName().compareTo(rs.getChildType().getName());
	}

	@Override
	public String getPrefix() {
		return prefix;
	}
	
}
