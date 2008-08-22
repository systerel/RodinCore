package org.rodinp.core.index;

import org.rodinp.core.IRodinElement;
import org.rodinp.internal.core.index.RodinLocation;

// TODO: consider providing only an interface when the Constructor problem is solved.
// TODO: consider declaring final
public class Occurrence {
	private OccurrenceKind kind;
	private IRodinLocation location;
	private IIndexer indexer;

	public Occurrence(OccurrenceKind kind, IRodinLocation location, IIndexer indexer) {
		this.kind = kind;
		this.location = location;
		this.indexer = indexer;
	}

	public Occurrence(OccurrenceKind kind, IIndexer indexer) {
		this(kind, null, indexer);
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

	public void setKind(OccurrenceKind kind) {
		this.kind = kind;
	}

	public void setLocation(IRodinLocation location) {
		this.location = location;
	}

	public void setLocation(IRodinElement element, String attributeId,
			int start, int end) {
		setLocation(new RodinLocation(element, attributeId, start, end));
	}

	public void setDefaultLocation(IRodinElement element) {
		setLocation(element, null, IRodinLocation.NULL_CHAR_POS,
				IRodinLocation.NULL_CHAR_POS);
	}

	// DEBUG
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("occurrence\n");
		sb.append("kind: " + kind.toString() + "\n");
		final IRodinElement element = location.getElement();
		sb.append("location: " + element.getElementName() + "("
				+ element.getElementType().getName() + ")\n");
		sb.append("attribute id: " + location.getAttributeId() + "\n");
		sb.append("(" + location.getCharStart() + "; " + location.getCharEnd()
				+ ")\n");
		return sb.toString();
	}

}
