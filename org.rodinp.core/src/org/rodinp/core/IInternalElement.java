package org.rodinp.core;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Common protocol for all internal elements.
 * <p>
 * Internal elements are elements of the database that are stored within Rodin
 * files (i.e., are descendants thereof).
 * </p>
 * 
 * @author Laurent Voisin
 */
//TODO document IInternalElement
public interface IInternalElement extends IRodinElement, IInternalParent,
		IElementManipulation, IAttributedElement {

	/**
	 * Returns the occurrence count of this internal element, which is always
	 * <code>1</code>.
	 * 
	 * @return <code>1</code>
	 * @deprecated Internal elements are now unique.
	 */
	@Deprecated
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
	String getContents() throws RodinDBException;

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
	String getContents(IProgressMonitor monitor) throws RodinDBException;

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
	void setContents(String contents) throws RodinDBException;

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
	void setContents(String contents, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Returns the Rodin file containing this internal element.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @return the Rodin file containing this element
	 */
	IRodinFile getRodinFile();
	
}