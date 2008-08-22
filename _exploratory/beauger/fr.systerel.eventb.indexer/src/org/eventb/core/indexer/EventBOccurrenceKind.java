package org.eventb.core.indexer;

import java.io.ObjectStreamException;

import org.rodinp.core.index.OccurrenceKind;

public class EventBOccurrenceKind extends OccurrenceKind {

	protected EventBOccurrenceKind(String name) {
		super(name);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4034811166410265505L;


	public static final EventBOccurrenceKind DECLARATION = new EventBOccurrenceKind(
	"declaration");

	public static final EventBOccurrenceKind REFERENCE = new EventBOccurrenceKind(
	"reference");

	private static final EventBOccurrenceKind[] PRIVATE_VALUES = { DECLARATION, REFERENCE };
	
	private Object readResolve() throws ObjectStreamException {
		return PRIVATE_VALUES[ordinal];
	}


}
