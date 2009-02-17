/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation as
 *     		org.eclipse.jdt.internal.core.CopyElementsOperation
 *     ETH Zurich - adaptation from JDT to Rodin
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.internal.core;

import static org.rodinp.core.IRodinDBStatusConstants.ROOT_ELEMENT;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.internal.core.util.Messages;

/**
 * This operation copies/moves a collection of elements from their current
 * container to a new container, optionally renaming the elements.
 * <p>Notes:<ul>
 *    <li>If there is already an element with the same name in
 *    the new container, the operation either overwrites or aborts,
 *    depending on the collision policy setting. The default setting is
 *	  abort.</li>
 *
 *    <li>The elements are inserted in the new container in the order given.</li>
 *
 *    <li>The elements can be positioned in the new container.
 *    By default, the elements are appended at the end of the new container.</li>
 *
 *    <li>This operation can be used to copy and rename elements within
 *    the same container.</li>
 *
 *    <li>This operation only copies internal elements (that is elements
 *    contained within Rodin files).</li> 
 * </ul>
 *
 */
public class CopyElementsOperation extends MultiOperation {
	
	/**
	 * When executed, this operation will copy the given elements to the
	 * given containers.  The elements and destination containers must be in
	 * the correct order. If there is > 1 destination, the number of destinations
	 * must be the same as the number of elements being copied/moved/renamed.
	 */
	public CopyElementsOperation(IRodinElement[] elementsToCopy,
			IRodinElement[] destContainers, boolean force) {

		super(elementsToCopy, destContainers, force);
	}
	
	/**
	 * When executed, this operation will copy the given elements to the
	 * given container.
	 */
	public CopyElementsOperation(IRodinElement[] elementsToCopy,
			IRodinElement destContainer, boolean force) {

		super(elementsToCopy, new IRodinElement[] {destContainer}, force);
	}
	
	public CopyElementsOperation(IRodinElement[] elementsToCopy, boolean force) {
		super(elementsToCopy, force);
	}
	
	public CopyElementsOperation(IRodinElement elementToCopy,
			IRodinElement destContainer, boolean force) {

		super(elementToCopy, destContainer, force);
	}
	
	public CopyElementsOperation(IRodinElement elementToCopy, boolean force) {
		super(elementToCopy, force);
	}

	/*
	 * Returns the <code>String</code> to use as the main task name
	 * for progress monitoring.
	 */
	@Override
	protected String getMainTaskName() {
		return Messages.operation_copyElementProgress; 
	}
	
	/*
	 * Copy/move the element from the source to destination, renaming
	 * the elements as specified, honoring the collision policy.
	 *
	 * @exception RodinDBException if the operation is unable to
	 * be completed
	 */
	@Override
	protected void processElement(IRodinElement element) throws RodinDBException {
		
		RodinElementDelta delta = newRodinElementDelta();

		InternalElement source = (InternalElement) element;
		InternalElementInfo sourceInfo = source.getElementInfo(getSubProgressMonitor(1)); 
		InternalElement nextSibling = (InternalElement) this.insertBeforeElements.get(element);
		InternalElement dest = getDestElement(source);
		RodinFileElementInfo rfInfo = getRodinFileElementInfo(dest);

		if (source.equals(dest)) {
			if (rfInfo.reorder(source, nextSibling)) {
				delta.changed(source, IRodinElementDelta.F_REORDERED);
			}
		} else {
			if (dest.exists()) {
				if (this.force) {
					rfInfo.delete(dest);
					delta.removed(dest);
				} else {
					error(IRodinDBStatusConstants.NAME_COLLISION, element);
				}
			}
			if (isRename()) {
				rfInfo.rename(source, dest);
			} else {
				rfInfo.copy(source, sourceInfo, dest, nextSibling);
			}
			
			if (isMove()) {
				if (! isRename()) {
					// Remove source element
					RodinFile rfSource = source.getRodinFile();
					RodinFileElementInfo rfSourceInfo = 
						(RodinFileElementInfo) rfSource.getElementInfo(getSubProgressMonitor(1));
					rfSourceInfo.delete(source);
				}
				delta.movedFrom(source, dest);
				delta.movedTo(dest, source);
			} else {
				delta.added(dest);
			}
		}
		addDelta(delta);
	}

	/**
	 * Returns the destination element for the given element.
	 * 
	 * @param element
	 *            the internal element which is operated on
	 * @return the destination element for the given element
	 */
	private InternalElement getDestElement(InternalElement element) {
		final IInternalElement destParent = 
			(IInternalElement) getDestinationParent(element);
		String newName = getNewNameFor(element);
		if (newName == null) {
			newName = element.getElementName();
		}
		final IInternalElementType<? extends IInternalElement> newType =
			element.getElementType();
		return (InternalElement) destParent.getInternalElement(newType, newName);
	}

	/**
	 * Returns the element info associated to the Rodin file containing the
	 * given internal element.
	 * 
	 * @param element
	 *            an internal element
	 * @return the element info associated to the Rodin file containing the
	 *         given element
	 * @throws RodinDBException
	 *             if the Rodin file could not be opened.
	 */
	private RodinFileElementInfo getRodinFileElementInfo(InternalElement element) throws RodinDBException {
		RodinFile rf = element.getRodinFile();
		return (RodinFileElementInfo) rf.getElementInfo(getSubProgressMonitor(1));
	}

	/*
	 * Possible failures:
	 * <ul>
	 *  <li>NO_ELEMENTS_TO_PROCESS - no elements supplied to the operation
	 *	<li>INVALID_RENAMING - the number of renamings supplied to the operation
	 *		does not match the number of elements that were supplied.
	 * </ul>
	 */
	@Override
	protected IRodinDBStatus verify() {
		IRodinDBStatus status = super.verify();
		if (!status.isOK()) {
			return status;
		}
		if (this.renamingsList != null && this.renamingsList.length != this.elementsToProcess.length) {
			return new RodinDBStatus(IRodinDBStatusConstants.INVALID_RENAMING);
		}
		return RodinDBStatus.VERIFIED_OK;
	}
	
	/*
	 * @see MultiOperation
	 *
	 * Possible failure codes:
	 * <ul>
	 *	<li>ELEMENT_DOES_NOT_EXIST - <code>element</code> or its specified destination is
	 *		is <code>null</code> or does not exist. If a <code>null</code> element is
	 *		supplied, no element is provided in the status, otherwise, the non-existant element
	 *		is supplied in the status.
	 *	<li>INVALID_ELEMENT_TYPES - <code>element</code> is not contained within a compilation unit.
	 *		This operation only operates on elements contained within compilation units.
	 *  <li>READ_ONLY - <code>element</code> is read only.
	 *  <li>ROOT_ELEMENT - <code>element</code> is a root element and cannot be modified.
	 *	<li>INVALID_DESTINATION - The destination parent specified for <code>element</code>
	 *		is of an incompatible type. The destination for a package declaration or import declaration must
	 *		be a compilation unit; the destination for a type must be a type or compilation
	 *		unit; the destinaion for any type member (other than a type) must be a type. When
	 *		this error occurs, the element provided in the operation status is the <code>element</code>.
	 *	<li>INVALID_NAME - the new name for <code>element</code> does not have valid syntax.
	 *      In this case the element and name are provided in the status.
	 * </ul>
	 */
	@Override
	protected void verify(IRodinElement element) throws RodinDBException {
		if (! (element instanceof InternalElement))
			error(IRodinDBStatusConstants.INVALID_ELEMENT_TYPES, element);
		
		if (element == null || !element.exists())
			error(IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST, element);
		
		if (element.isReadOnly() && (isRename() || isMove()))
			error(IRodinDBStatusConstants.READ_ONLY, element);

		if (element.isRoot() && (isRename() || isMove()))
			error(ROOT_ELEMENT, element);

		IRodinElement dest = getDestinationParent(element);
		verifyDestination(element, dest);
		
		verifySibling(element, dest);
		
		if (this.renamingsList != null) {
			verifyRenaming(element);
		}
	}
}
