package org.rodinp.core.index;

/**
 * Interface for occurrence kinds.
 * <p>
 * Clients should add their own kinds through the extension point mechanism.
 * <p>
 * This interface is NOT intended to be implemented by clients.
 * 
 * @author Nicolas Beauger
 */
public interface IOccurrenceKind {

	/**
	 * Returns the identifier of the kind.
	 * 
	 * @return the identifier of the kind.
	 */
	String getId();

	/**
	 * Returns the name of the kind.
	 * 
	 * @return the name of the kind.
	 */
	String getName();

}
