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
		return id.equals(rel.getID())
				&& elementType.equals(rel.getElementType());
	}

	public IAttributeType getAttributeType() {
		return attributeType;
	}
	
}
