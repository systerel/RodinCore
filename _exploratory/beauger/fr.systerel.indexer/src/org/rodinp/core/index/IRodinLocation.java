package org.rodinp.core.index;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * Type of locations within a IRodinFile.
 * <p>
 * A IRodinLocation points to a place within a IRodinElement. The element may be
 * the IRodinFile itself, or a IInternalElement within the file.
 * <p>
 * If the mere element is not accurate enough, it is possible to precise a
 * IAttributeType
 * <p>
 * This interface is NOT intended to be implemented by clients.
 * 
 * @author Nicolas Beauger
 */
public interface IRodinLocation {

	/**
	 * Constant indicating a null char position.
	 */
	int NULL_CHAR_POS = -1;

	/**
	 * Returns the file containing the location.
	 * 
	 * @return the file containing the location.
	 */
	IRodinFile getRodinFile();

	/**
	 * Returns the element containing the location.
	 * <p>
	 * It may be the file itself, or any inner element.
	 * 
	 * @return the element containing the location.
	 */
	IRodinElement getElement();

	/**
	 * Returns the attribute type containing the location.
	 * <p>
	 * It may be <code>null</code>. In this case, the location reduces to the
	 * mere element returned by {@link #getElement()}. The meaning is that any
	 * direct child of the element of the location can be pointed to by this
	 * location.
	 * <p>
	 * In case it is not <code>null</code>, the location points to a place
	 * inside the attribute type (defined by {@link #getCharStart()} and
	 * {@link #getCharEnd()}. It can be assumed that the element of the
	 * location has the returned attribute.
	 * 
	 * @return the attribute type of the location, or <code>null</code> if it
	 *         is not relevant.
	 */
	IAttributeType getAttributeType();

	/**
	 * Returns the start position of the location within the attribute type.
	 * <p>
	 * The start position is INclusive.
	 * 
	 * @return the start position of the location.
	 */
	int getCharStart();

	/**
	 * Returns the end position of the location within the attribute type.
	 * <p>
	 * The end position is EXclusive.
	 * 
	 * @return the end position of the location.
	 */
	int getCharEnd();

}
