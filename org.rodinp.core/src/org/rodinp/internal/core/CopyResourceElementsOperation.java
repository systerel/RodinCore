/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.internal.core.CopyResourceElementsOperation
 *******************************************************************************/
package org.rodinp.internal.core;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinElement;
import org.rodinp.internal.core.util.Messages;

/**
 * This operation copies/moves/renames a collection of resources from their current
 * container to a new container, optionally renaming the elements.
 * <p>Notes:<ul>
 *    <li>If there is already a resource with the same name in
 *    the new container, the operation either overwrites or aborts,
 *    depending on the collision policy setting. The default setting is
 *	  abort.</li>
 *
 *    <li>The collection of elements being copied must all share the
 *    same type of container.</li>
 *
 *    <li>This operation can be used to copy and rename elements within
 *    the same container.</li>
 *
 *    <li>This operation only copies Rodin files.</li>
 * </ul>
 *
 */
public class CopyResourceElementsOperation extends MultiOperation  {

	/**
	 * The list of new resources created during this operation.
	 */
	protected ArrayList<IRodinElement> createdElements;
	
	/**
	 * When executed, this operation will copy the given resources to the
	 * given container.
	 */
	public CopyResourceElementsOperation(IRodinElement[] resourcesToCopy,
			IRodinElement destContainer, boolean force) {

		this(resourcesToCopy, new IRodinElement[]{destContainer}, force);
	}

	/**
	 * When executed, this operation will copy the given resources to the
	 * given containers.  The resources and destination containers must be in
	 * the correct order. If there is > 1 destination, the number of destinations
	 * must be the same as the number of resources being copied/moved.
	 */
	public CopyResourceElementsOperation(IRodinElement[] resourcesToCopy,
			IRodinElement[] destContainers, boolean force) {

		super(resourcesToCopy, destContainers, force);
	}
	
	public CopyResourceElementsOperation(IRodinElement[] resourcesToCopy, boolean force) {
		super(resourcesToCopy, force);
	}
	
	public CopyResourceElementsOperation(IRodinElement resourceToCopy,
			IRodinElement destContainer, boolean force) {

		super(resourceToCopy, destContainer, force);
	}
	
	public CopyResourceElementsOperation(RodinFile resourceToCopy, boolean force) {
		super(resourceToCopy, force);
	}

	/*
	 * @see MultiOperation
	 */
	@Override
	protected String getMainTaskName() {
		return Messages.operation_copyResourceProgress; 
	}
	
	/**
	 * Sets the deltas to register the changes resulting from this operation
	 * for this source element and its destination.
	 * If the operation is a cross project operation<ul>
	 * <li>On a copy, the delta should be rooted in the dest project
	 * <li>On a move, two deltas are generated<ul>
	 * 			<li>one rooted in the source project
	 *			<li>one rooted in the destination project</ul></ul>
	 * If the operation is rooted in a single project, the delta is rooted in that project
	 * 	 
	 */
	protected void prepareDeltas(IRodinElement sourceElement, IRodinElement destinationElement, boolean isMove) {
		IRodinProject destProject = destinationElement.getRodinProject();
		if (isMove) {
			IRodinProject sourceProject = sourceElement.getRodinProject();
			getDeltaFor(sourceProject).movedFrom(sourceElement, destinationElement);
			getDeltaFor(destProject).movedTo(destinationElement, sourceElement);
		} else {
			getDeltaFor(destProject).added(destinationElement);
		}
	}
	
	/**
	 * Copies/moves a Rodin file with the name <code>newRFName</code>
	 * to the destination project.<br>
	 *
	 * @exception RodinDBException if the operation is unable to
	 * complete
	 */
	private void processRodinFileResource(RodinFile source, RodinProject dest) throws RodinDBException {
		String destName = getDestNameFor(source);
	
		// copy resource
		IFile sourceResource = source.getResource();
		IRodinFile destRF = dest.getRodinFile(destName);
		IFile destFile = destRF.getResource();
		if (! destRF.equals(source)) {
			try {
				if (destRF.exists()) {
					if (this.force) {
						// we can remove it
						deleteResource(destFile, IResource.KEEP_HISTORY);
						getDeltaFor(destRF.getRodinProject()).removed(destRF);
						destRF.revert(); // ensure the in-memory buffer for the dest RF is closed
					} else {
						// abort
						throw new RodinDBException(new RodinDBStatus(
							IRodinDBStatusConstants.NAME_COLLISION, 
							Messages.bind(Messages.status_nameCollision, destFile.getFullPath().toString()))); 
					}
				}
				int flags = this.force ? IResource.FORCE : IResource.NONE;
				if (this.isMove()) {
					flags |= IResource.KEEP_HISTORY;
					sourceResource.move(destFile.getFullPath(), flags, getSubProgressMonitor(1));
				} else {
					sourceResource.copy(destFile.getFullPath(), flags, getSubProgressMonitor(1));
				}
				this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
			} catch (RodinDBException e) {
				throw e;
			} catch (CoreException e) {
				throw new RodinDBException(e);
			}
	
			// register the correct change deltas
			prepareDeltas(source, destRF, isMove());
		} else {
			if (!this.force) {
				throw new RodinDBException(new RodinDBStatus(
					IRodinDBStatusConstants.NAME_COLLISION, 
					Messages.bind(Messages.status_nameCollision, destFile.getFullPath().toString()))); 
			}
			// Nothing to do.
		}
	}

	private String getDestNameFor(IRodinElement source) throws RodinDBException {
		String newName = getNewNameFor(source);
		return (newName != null) ? newName : source.getElementName();
	}
	
	/**
	 * Process all of the changed deltas generated by this operation.
	 */
	protected void processDeltas() {
		for (RodinElementDelta delta: this.deltasPerProject.values()) {
			addDelta(delta);
		}
	}
	
	/*
	 * @see MultiOperation
	 * This method delegates to <code>processRodinFileResource</code>.
	 */
	@Override
	protected void processElement(IRodinElement element) throws RodinDBException {
		IRodinElement dest = getDestinationParent(element);
		if (element instanceof RodinFile) {
				processRodinFileResource((RodinFile) element, (RodinProject) dest);
				createdElements.add(((RodinProject) dest).getRodinFile(getDestNameFor(element)));
		} else {
			throw new RodinDBException(new RodinDBStatus(
					IRodinDBStatusConstants.INVALID_ELEMENT_TYPES, element));
		}
	}
	
	/*
	 * @see MultiOperation
	 * Overridden to allow special processing of <code>RodinElementDelta</code>s
	 * and <code>fResultElements</code>.
	 */
	@Override
	protected void processElements() throws RodinDBException {
		createdElements = new ArrayList<IRodinElement>(elementsToProcess.length);
		try {
			super.processElements();
		} catch (RodinDBException jme) {
			throw jme;
		} finally {
			resultElements = new IRodinElement[createdElements.size()];
			createdElements.toArray(resultElements);
			processDeltas();
		}
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
	
		if (this.renamingsList != null && this.renamingsList.length != elementsToProcess.length) {
			return new RodinDBStatus(IRodinDBStatusConstants.INVALID_RENAMING);
		}
		return RodinDBStatus.VERIFIED_OK;
	}
	
	/*
	 * @see MultiOperation
	 */
	@Override
	protected void verify(IRodinElement element) throws RodinDBException {
		if (! (element instanceof RodinFile)) {
			error(IRodinDBStatusConstants.INVALID_ELEMENT_TYPES, element);
		}
		
		if (element == null || !element.exists())
			error(IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST, element);
			
		if (element.isReadOnly() && (isRename() || isMove()))
			error(IRodinDBStatusConstants.READ_ONLY, element);

		RodinElement dest = (RodinElement) getDestinationParent(element);
		verifyDestination(element, dest);
		if (this.renamings != null) {
			verifyRenaming(element);
		}
	}

	@Override
	public boolean modifiesResources() {
		return true;
	}
}
