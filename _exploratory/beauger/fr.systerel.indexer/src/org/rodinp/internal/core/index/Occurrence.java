package org.rodinp.internal.core.index;

import org.rodinp.core.index.IOccurrence;
import org.rodinp.core.index.IOccurrenceKind;
import org.rodinp.core.index.IRodinLocation;

public class Occurrence implements IOccurrence {
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
		sb.append("kind: " + kind.getName() + "\n");
		sb.append("location: " + location);
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
