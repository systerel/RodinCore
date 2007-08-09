package org.eventb.internal.ui.elementSpecs;

import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElementType;

public class ElementRelationship implements IElementRelationship {

	private String id;

	private IElementType<?> parentType;

	private IInternalElementType<?> childType;

	public ElementRelationship(String id,
			IElementType<?> parentType,
			IInternalElementType<?> childType) {
		this.id = id;
		this.parentType = parentType;
		this.childType = childType;
	}

	public String getID() {
		return id;
	}

	public IElementType<?> getParentType() {
		return parentType;
	}

	public IInternalElementType<?> getChildType() {
		return childType;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IElementRelationship))
			return false;
		IElementRelationship rel = (IElementRelationship) obj;
		return (id.equals(rel.getID()));
	}

	@Override
	public String toString() {
		return id + " : " + parentType + " --> " + childType;
	}

	
	
}
