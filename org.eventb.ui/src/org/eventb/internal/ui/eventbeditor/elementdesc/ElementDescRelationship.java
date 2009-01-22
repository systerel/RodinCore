/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.elementdesc;

import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElementType;

public class ElementDescRelationship implements IElementRelationship {

	private final IInternalElementType<?> childType;

	private final String id;

	private final IElementType<?> parentType;

	public ElementDescRelationship(IElementType<?> parentType,
			IInternalElementType<?> childType) {
		this.childType = childType;
		this.parentType = parentType;
		id = parentType.getId() + childType.getId();
	}

	public IInternalElementType<?> getChildType() {
		return childType;
	}

	public String getID() {
		return id;
	}

	public IElementType<?> getParentType() {
		return parentType;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IElementRelationship))
			return false;
		IElementRelationship rel = (IElementRelationship) obj;
		return parentType == rel.getParentType()
				&& childType == rel.getChildType();
	}
}
