package org.rodinp.core;

import org.eclipse.core.resources.IFile;

/**
 * Represents an entire Rodin file. File elements need to be opened before they
 * can be navigated or manipulated.
 * 
 * TODO write doc for IRodinFile.
 *
 * @author Laurent Voisin
 */
public interface IRodinFile extends IOpenable, IInternalParent, IElementManipulation {

	/**
	 * Finds the elements in this file that correspond to the given element. An
	 * element A corresponds to an element B if:
	 * <ul>
	 * <li>A has the same element name as B.
	 * <li>The parent of A corresponds to the parent of B recursively up to
	 * their respective files.
	 * <li>A exists.
	 * </ul>
	 * Returns <code>null</code> if no such Rodin elements can be found or if
	 * the given element is not included in a file.
	 * 
	 * @param element
	 *            the given element
	 * @return the found elements in this file that correspond to the given
	 *         element
	 */
	public IRodinElement[] findElements(IRodinElement element);

	/** 
	 * @see org.rodinp.core.IRodinElement#getResource()
	 */
	public IFile getResource();

}