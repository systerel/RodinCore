package org.eventb.internal.ui.elementSpecs;

import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElementType;

/**
 * @author htson
 *         <p>
 *         Proposed interface for element relationship. This is represented by a
 *         unique string id and denotes the the relationship from a parent
 *         element type to a child element type.
 *         </p>
 *         <p>
 *         This should be part of the org.rodinp.core package and subjected to
 *         changes.
 *         </p>
 */
public interface IElementRelationship {

	/**
	 * Return the unique id which can be used to identify the relationship
	 * <p>
	 * 
	 * @return The unique string id of the relationship
	 */
	public abstract String getID();

	/**
	 * Return the parent element type of the relationship.
	 * <p>
	 * 
	 * @return an element type
	 */
	public abstract IElementType<?> getParentType();

	/**
	 * Return the child element type of the relationship.
	 * <p>
	 * 
	 * @return an element type
	 */
	public abstract IInternalElementType<?> getChildType();

}
