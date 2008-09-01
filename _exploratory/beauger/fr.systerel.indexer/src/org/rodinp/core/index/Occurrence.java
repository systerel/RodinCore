package org.rodinp.core.index;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinElement;

// TODO: consider declaring final
public class Occurrence {
	private final OccurrenceKind kind;
	private final IRodinLocation location;
	private final IIndexer indexer;

	public Occurrence(OccurrenceKind kind, IRodinLocation location,
			IIndexer indexer) {
		if (kind == null) {
			throw new NullPointerException("null kind");
		}
		if (location == null) {
			throw new NullPointerException("null location");
		}
		if (indexer == null) {
			throw new NullPointerException("null indexer");
		}
		this.kind = kind;
		this.location = location;
		this.indexer = indexer;
	}

	public OccurrenceKind getKind() {
		return kind;
	}

	public IRodinLocation getLocation() {
		return location;
	}

	public IIndexer getIndexer() {
		return indexer;
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
