/*******************************************************************************
 * Copyright (c) 2009, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - Added priority and implicitChildProvider
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.elementdesc;

import org.eventb.ui.IImplicitChildProvider;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElementType;

public class ElementDescRelationship implements IElementRelationship, Comparable<IElementRelationship> {

	private final IInternalElementType<?> childType;

	private final String id;

	private final IElementType<?> parentType;
	
	private final int priority;

	private final IImplicitChildProvider childProvider;

	public ElementDescRelationship(IElementType<?> parentType,
			IInternalElementType<?> childType, int priority, IImplicitChildProvider childProvider) {
		this.childType = childType;
		this.parentType = parentType;
		this.priority = priority;
		this.childProvider = childProvider;
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
	public boolean equals(Object obj) {
		if (!(obj instanceof IElementRelationship))
			return false;
		IElementRelationship rel = (IElementRelationship) obj;
		return parentType == rel.getParentType()
				&& childType == rel.getChildType();
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
	
}
