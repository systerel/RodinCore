package org.rodinp.core;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Common protocol for all internal elements.
 * <p>
 * Internal elements are elements of the database that are stored within files.
 * </p>
 * 
 * @author Laurent Voisin
 */
//TODO document IInternalElement
public interface IInternalElement extends IRodinElement, IInternalParent, IElementManipulation{

	/**
	 * Returns the occurrence count of this internal element.
	 * <p>
	 * The occurrence count uniquely identifies this element in the case that a
	 * duplicate named element exists in the same parent. For example, if there
	 * are two variables in a machine with the same name, the occurrence
	 * count is used to distinguish them. The occurrence count starts at 1 (thus
	 * the first occurrence is occurrence 1, not occurrence 0) inside its
	 * parent.
	 * </p>
	 * 
	 * @return the occurrence count of this internal element
	 */
	int getOccurrenceCount();

	/**
	 * Returns the contents of this internal element. The file containing this
	 * internal element is opened if it was not already.
	 * 
	 * @exception RodinDBException
	 *                if the element could not be opened. Reasons include:
	 *                <ul>
	 *                <li> This Rodin element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li> A <code>CoreException</code> occurred while
	 *                accessing its underlying resource
	 *                </ul>
	 * @return the contents of this element.
	 */
	public String getContents() throws RodinDBException;

	/**
	 * Returns the contents of this internal element. The file containing this
	 * internal element is opened if it was not already.
	 * 
	 * @param monitor
	 *            the given progress monitor
	 * @exception RodinDBException
	 *                if the element could not be opened. Reasons include:
	 *                <ul>
	 *                <li> This Rodin element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li> A <code>CoreException</code> occurred while
	 *                accessing its underlying resource
	 *                </ul>
	 * @return the contents of this element.
	 */
	public String getContents(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Sets the contents of this internal element to the provided string. The
	 * file containing this internal element is opened if it was not already.
	 * 
	 * @param contents
	 *            the new contents to set
	 * @exception RodinDBException
	 *                if the element could not be opened. Reasons include:
	 *                <ul>
	 *                <li> This Rodin element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li> A <code>CoreException</code> occurred while
	 *                accessing its underlying resource
	 *                </ul>
	 */
	public void setContents(String contents) throws RodinDBException;

	/**
	 * Sets the contents of this internal element to the provided string. The
	 * file containing this internal element is opened if it was not already.
	 * 
	 * @param contents
	 *            the new contents to set
	 * @param monitor
	 *            the given progress monitor
	 * @exception RodinDBException
	 *                if the element could not be opened. Reasons include:
	 *                <ul>
	 *                <li> This Rodin element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li> A <code>CoreException</code> occurred while
	 *                accessing its underlying resource
	 *                </ul>
	 */
	public void setContents(String contents, IProgressMonitor monitor)
			throws RodinDBException;

}