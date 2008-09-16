package org.rodinp.internal.core.index;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.IOccurrenceKind;

public class Occurrence {
	private final IOccurrenceKind kind;
	private final IRodinLocation location;

	public Occurrence(IOccurrenceKind kind, IRodinLocation location) {
		if (kind == null) {
			throw new NullPointerException("null kind");
		}
		if (location == null) {
			throw new NullPointerException("null location");
		}
		this.kind = kind;
		this.location = location;
	}

	public IOccurrenceKind getKind() {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + kind.hashCode();
		result = prime * result + location.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (! (obj instanceof Occurrence))
			return false;
		final Occurrence other = (Occurrence) obj;
		if (!kind.equals(other.kind))
			return false;
		if (!location.equals(other.location))
			return false;
		return true;
	}

}
