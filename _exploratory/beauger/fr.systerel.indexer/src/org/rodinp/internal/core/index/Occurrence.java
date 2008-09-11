package org.rodinp.internal.core.index;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.OccurrenceKind;

public class Occurrence {
	private final OccurrenceKind kind;
	private final IRodinLocation location;

	public Occurrence(OccurrenceKind kind, IRodinLocation location) {
		if (kind == null) {
			throw new NullPointerException("null kind");
		}
		if (location == null) {
			throw new NullPointerException("null location");
		}
		this.kind = kind;
		this.location = location;
	}

	public OccurrenceKind getKind() {
		return kind;
	}

	public IRodinLocation getLocation() {
		return location;
	}

	// DEBUG
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("occurrence\n");
		sb.append("kind: " + kind.toString() + "\n");
		final IRodinElement element = location.getElement();
		sb.append("location: " + element.getElementName() + " ("
				+ element.getElementType().getName() + ")\n");
		final IAttributeType attributeType = location.getAttributeType();
		sb.append("attribute id: "
				+ (attributeType == null ? "null" : attributeType.getName())
				+ "\n");
		sb.append("(" + location.getCharStart() + "; " + location.getCharEnd()
				+ ")\n");
		return sb.toString();
	}

}
