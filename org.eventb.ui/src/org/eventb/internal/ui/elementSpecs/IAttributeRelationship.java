package org.eventb.internal.ui.elementSpecs;

import org.rodinp.core.IInternalElementType;

/**
 * @author htson
 *         <p>
 *         Proposed interface for attribute relationship. This is represented by
 *         a unique string id and denotes the relationship to an element type.
 *         </p>
 *         <p>
 *         This should be part of the org.rodinp.core package and subjected to
 *         changes.
 *         </p>
 */
public interface IAttributeRelationship {

	/**
	 * Return the unique id which can be used to identify the relationship
	 * <p>
	 * 
	 * @return The unique string id of the relationship
	 */
	public abstract String getID();

	/**
	 * Return the element type associated with this relationship. The return
	 * value must not be <code>null</code>.
	 * <p>
	 * 
	 * @return an element type
	 */
	public abstract IInternalElementType<?> getElementType();

}
