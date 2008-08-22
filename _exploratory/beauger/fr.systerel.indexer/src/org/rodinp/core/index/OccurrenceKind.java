package org.rodinp.core.index;

import java.io.ObjectStreamException;
import java.io.Serializable;

public class OccurrenceKind implements Serializable {
	// TODO: make the class abstract (or at least don't define constants here)
	// and extensible via an extension point
	// (the constructor may become protected)
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -933936871460817606L;

	private final String name;
	
	private static int nextOrdinal = 0;
	
	protected final int ordinal = nextOrdinal++;

	protected OccurrenceKind(String name) {
		this.name = name;
	}

	public static final OccurrenceKind NULL = new OccurrenceKind("null");
	
	@Override
	public String toString() {
		return name;
	}
	
	private static final OccurrenceKind[] PRIVATE_VALUES = { NULL };
	
	private Object readResolve() throws ObjectStreamException {
		return PRIVATE_VALUES[ordinal];
	}

}
