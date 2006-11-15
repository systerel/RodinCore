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
	 * Creates a new Rodin Problem marker for the given attribute of this
	 * element.
	 * <p>
	 * The new marker is attached to the underlying resource of this element.
	 * Its marker type is {@link RodinMarkerUtil#RODIN_PROBLEM_MARKER}.
	 * </p>
	 * 
	 * @param attributeType
	 *            type of the attribute to mark, or <code>null</code> if no
	 *            attribute should be marked
	 * @param problem
	 *            problem to attach to the new marker
	 * @param args
	 *            arguments to the problem
	 * @exception RodinDBException
	 *                if this method fails. Reasons include:
	 *                <ul>
	 *                <li>This element does not exist.</li>
	 *                <li>The specified attribute is not set for this element.</li>
	 *                </ul>
	 * @see RodinMarkerUtil
	 */
	void createProblemMarker(IAttributeType attributeType,
			IRodinProblem problem, Object... args) throws RodinDBException;

	/**
	 * Creates a new Rodin Problem marker for the given String attribute of this
	 * element, and located at the given position in the attribute.
	 * <p>
	 * The new marker is attached to the underlying resource of this element.
	 * Its marker type is {@link RodinMarkerUtil#RODIN_PROBLEM_MARKER}.
	 * </p>
	 * 
	 * @param attributeType
	 *            type of the attribute to mark, or <code>null</code> if no
	 *            attribute should be marked
	 * @param charStart
	 *            start position (zero-relative and inclusive), or a negative
	 *            value to indicate its absence
	 * @param charEnd
	 *            end position (zero-relative and exclusive), or a negative
	 *            value to indicate its absence
	 * @param problem
	 *            problem to attach to the new marker
	 * @param args
	 *            arguments to the problem
	 * @exception RodinDBException
	 *                if this method fails. Reasons include:
	 *                <ul>
	 *                <li>This element does not exist.</li>
	 *                <li>The specified attribute is not set for this element.</li>
	 *                <li>The start and end positions are specified together
	 *                with a <code>null</code> attribute id.</li>
	 *                <li>The start and end positions are specified with an
	 *                attribute id whose kind is not <code>string</code>.</li>
	 *                <li>The start or end position is negative, but not both.</li>
	 *                <li>The end position is less than or equal to the start
	 *                position.</li>
	 *                </ul>
	 * @see RodinMarkerUtil
	 */
	void createProblemMarker(IAttributeType.String attributeType, int charStart,
			int charEnd, IRodinProblem problem, Object... args)
			throws RodinDBException;

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinElement#getElementType()
	 */
	IInternalElementType getElementType();
	
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
	 * @deprecated The contents pseudo-attribute is now deprecated in favor of
	 *             regular attributes (see {@link IAttributedElement}.
	 */
	@Deprecated
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
	 * @deprecated The contents pseudo-attribute is now deprecated in favor of
	 *             regular attributes (see {@link IAttributedElement}.
	 */
	@Deprecated
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
	 * @deprecated The contents pseudo-attribute is now deprecated in favor of
	 *             regular attributes (see {@link IAttributedElement}.
	 */
	@Deprecated
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
	 * @deprecated The contents pseudo-attribute is now deprecated in favor of
	 *             regular attributes (see {@link IAttributedElement}.
	 */
	@Deprecated
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