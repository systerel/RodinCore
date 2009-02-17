/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.core.DeleteResourceElementsOperation.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.util.Messages;

/**
 * This operation deletes a collection of resources and all of their children.
 * It does not delete resources which do not belong to the Rodin Model
 * (eg GIF files).
 */
public class DeleteResourceElementsOperation extends MultiOperation {

	/**
	 * When executed, this operation will delete the given elements. The elements
	 * to delete cannot be <code>null</code> or empty, and must have a corresponding
	 * resource.
	 */
	protected DeleteResourceElementsOperation(IRodinElement[] elementsToProcess, boolean force) {
		super(elementsToProcess, force);
	}
	
	/**
	 * When executed, this operation will delete the given element. The element
	 * to delete cannot be <code>null</code>, and must have a corresponding
	 * resource.
	 */
	public DeleteResourceElementsOperation(IRodinElement elementToProcess, boolean force) {
		super(elementToProcess, force);
	}
	
	@Override
	protected String getMainTaskName() {
		return Messages.operation_deleteResourceProgress; 
	}

	@Override
	protected ISchedulingRule getSchedulingRule() {
		if (this.elementsToProcess != null && this.elementsToProcess.length == 1) {
			IResource resource = this.elementsToProcess[0].getResource();
			if (resource != null) {
				final IWorkspace ws = resource.getWorkspace();
				return ws.getRuleFactory().deleteRule(resource);
			}
		}
		return super.getSchedulingRule();
	}
	
	@Override
	public boolean modifiesResources() {
		return true;
	}

	@Override
	protected void processElement(IRodinElement element) throws RodinDBException {
		if (element instanceof RodinFile) {
			deleteResource(element.getResource(), force ? IResource.FORCE | IResource.KEEP_HISTORY : IResource.KEEP_HISTORY);
		} else {
			throw new RodinDBException(new RodinDBStatus(IRodinDBStatusConstants.INVALID_ELEMENT_TYPES, element));
		}
		// ensure the element is closed and all buffers removed
		final RodinFile rodinFile = (RodinFile) element;
		rodinFile.revert();
		final RodinDBManager rodinDBManager = RodinDBManager.getRodinDBManager();
		rodinDBManager.removeBuffer(rodinFile.getMutableCopy(), true);
		// Also remove the element from its parent, if open
		Openable parent = rodinFile.getParent();
		OpenableElementInfo parentInfo = rodinDBManager.getInfo(parent);
		if (parentInfo != null) {
			parentInfo.removeChild(rodinFile);
		}
	}

	@Override
	protected void verify(IRodinElement element) throws RodinDBException {
		if (! (element instanceof RodinFile)) {
			error(IRodinDBStatusConstants.INVALID_ELEMENT_TYPES, element);
		}

		if (element == null || !element.exists())
			error(IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST, element);
	}

}
