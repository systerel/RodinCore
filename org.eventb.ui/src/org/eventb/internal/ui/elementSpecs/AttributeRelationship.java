package org.eventb.internal.ui.elementSpecs;

import org.rodinp.core.IInternalElementType;

public class AttributeRelationship implements IAttributeRelationship {

	private String id;

	private IInternalElementType<?> elementType;

	public AttributeRelationship(String id,
			IInternalElementType<?> elementType) {
		this.id = id;
		this.elementType = elementType;
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
		return id.equals(rel.getID())
				&& elementType.equals(rel.getElementType());
	}
	
}
