package org.rodinp.core.index;

/**
 * Interface for IOccurrenceKind, intended to be implemented by enum types that
 * will add their own kinds.
 * 
 * @author Nicolas Beauger
 * 
 */
public interface IOccurrenceKind {
	
	String getId();
	
	String getName();

}
