package org.eventb.internal.ui.elementSpecs;

/**
 * @author htson
 *         <p>
 *         Proposed interface for element spec registry.
 *         </p>
 *         <p>
 *         This should be part of the {@link org.rodinp.core} package and
 *         subjected to changes.
 *         </p>
 */
public interface IElementSpecRegistry {

	/**
	 * Check if a element relationship id is valid.
	 * 
	 * @param id
	 *            a string represent the element relationship ID.
	 * @return <code>true</code> if the id indeed represent an element
	 *         relationship. Return <code>false</code> otherwise.
	 * @see IElementRelationship#getID()
	 */
	public boolean isValidElementRelationship(String id);

	/**
	 * Return all declared element relationships.
	 * 
	 * @return an array of element relationship IDs.
	 * @see IElementRelationship#getID()
	 */
	public String[] getElementRelationshipIDs();

	/**
	 * Get a particular element relationship given its unique id. The element
	 * relationship is used as a hash key, so it is important that it is return
	 * the same object for the same id.
	 * 
	 * @param id
	 *            the unique string id of the element relationship.
	 * @return The corresponding element relationship. This must not be
	 *         <code>null</null>.
	 * @throws IllegalArgumentException
	 *             if the id is invalid.
	 */
	public IElementRelationship getElementRelationship(String id)
			throws IllegalArgumentException;

	/**
	 * Check if a attribute relationship id is valid.
	 * 
	 * @param id
	 *            a string represent the attribute relationship ID.
	 * @return <code>true</code> if the id indeed represent an attribute
	 *         relationship. Return <code>false</code> otherwise.
	 * @see IAttributeRelationship#getID()
	 */
	public boolean isValidAttributeRelationship(String id);

	/**
	 * Return all declared attribute relationships.
	 * 
	 * @return an array of attribute relationship IDs.
	 * @see IAttributeRelationship#getID()
	 */
	public String[] getAttributeRelationshipIDs();

	/**
	 * Get a particular attribute relationship given its unique id. The
	 * attribute relationship is used as a hash key, so it is important that it
	 * is return the same object for the same id.
	 * 
	 * @param id
	 *            the unique string id of the attribute relationship.
	 * @return The corresponding attribute relationship. This must not be
	 *         <code>null</null>.
	 * @throws IllegalArgumentException
	 *             if the id is invalid.
	 */
	public IAttributeRelationship getAttributeRelationship(String id)
			throws IllegalArgumentException;

}
