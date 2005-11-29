/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.basis.RodinFile;
import org.rodinp.internal.core.util.Messages;

public class CreateInternalElementOperation extends RodinDBOperation{

	private InternalElement newElement;
	private InternalElement nextSibling;
	
	public CreateInternalElementOperation(InternalElement newElement, IInternalElement nextSibling) {
		super(new IRodinElement[] { newElement });
		this.newElement = newElement;
		this.nextSibling = (InternalElement) nextSibling;
	}

	@Override
	protected void executeOperation() throws RodinDBException {
		RodinElementDelta delta = newRodinElementDelta();

		try {
			beginTask(Messages.operation_createInternalElementProgress, 2);
			RodinFile file = newElement.getOpenableParent();
			RodinFileElementInfo fileInfo = (RodinFileElementInfo) file.getElementInfo(getSubProgressMonitor(1));
			fileInfo.create(newElement, nextSibling);
			delta.added(newElement);
			addDelta(delta);
			worked(1);
		} finally {
			done();
		}
	}

	@Override
	protected ISchedulingRule getSchedulingRule() {
		IResource resource = newElement.getOpenableParent().getResource();
		IWorkspace workspace = resource.getWorkspace();
		if (resource.exists()) {
			return workspace.getRuleFactory().modifyRule(resource);
		} else {
			return workspace.getRuleFactory().createRule(resource);
		}
	}

	/**
	 * Possible failures:
	 * <ul>
	 * <li>NO_ELEMENTS_TO_PROCESS - the newElement supplied to the operation is
	 * <code>null</code>.
	 * <li>INVALID_SIBLING - the sibling supplied to the operation has a different parent.
	 * </ul>
	 */
	@Override
	public IRodinDBStatus verify() {
		super.verify();
		if (nextSibling != null && nextSibling.getParent() != newElement.getParent()) {
			return new RodinDBStatus(IRodinDBStatusConstants.INVALID_SIBLING,
					nextSibling);
		}
		return RodinDBStatus.VERIFIED_OK;
	}
}
