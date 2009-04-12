/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added creation of new internal element child
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.basis.RodinElement;
import org.rodinp.internal.core.util.Messages;

public class CreateInternalElementOperation extends RodinDBOperation{

	private final InternalElement newElement;
	private final RodinElement parent;
	protected final InternalElement nextSibling;
	
	public CreateInternalElementOperation(InternalElement newElement, IInternalElement nextSibling) {
		super(new IRodinElement[] { newElement });
		this.newElement = newElement;
		this.parent = newElement.getParent();
		this.nextSibling = (InternalElement) nextSibling;
	}

	public CreateInternalElementOperation(InternalElement parent,
			IInternalElement nextSibling, boolean unused) {
		super(new IRodinElement[] { parent });
		this.newElement = null;
		this.parent = parent;
		this.nextSibling = (InternalElement) nextSibling;
	}

	@Override
	protected void executeOperation() throws RodinDBException {
		try {
			beginTask(Messages.operation_createInternalElementProgress, 2);
			final IInternalElement result = doCreate();
			final RodinElementDelta delta = newRodinElementDelta();
			delta.added(result);
			addDelta(delta);
			worked(1);
		} finally {
			done();
		}
	}

	protected IInternalElement doCreate() throws RodinDBException {
		final RodinFile file = newElement.getRodinFile();
		final RodinFileElementInfo fileInfo = (RodinFileElementInfo) file
				.getElementInfo(getSubProgressMonitor(1));
		fileInfo.create(newElement, nextSibling);
		return newElement;
	}

	@Override
	protected ISchedulingRule getSchedulingRule() {
		assert false;
		return null;
	}

	/**
	 * Possible failures:
	 * <ul>
	 * <li>NO_ELEMENTS_TO_PROCESS - the newElement supplied to the operation is
	 * <code>null</code>.</li>
	 * <li>READ_ONLY - the parent of the newElement supplied is readonly.</li>
	 * <li>NAME_COLLISION - the newElement supplied already exists and creating it
	 * anew would create a duplicate element.</li>
	 * <li>INVALID_SIBLING - the sibling supplied to the operation has a different parent.</li>
	 * <li>ELEMENT_DOES_NOT_EXIST - the sibling supplied doesn't exist.</li>
	 * </ul>
	 */
	@Override
	public IRodinDBStatus verify() {
		final IRodinDBStatus status = super.verify();
		if (!status.isOK())
			return status;

		if (! parent.exists()) {
			return new RodinDBStatus(
					IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST,
					parent
			);
		}
		if (parent.isReadOnly()) {
			return new RodinDBStatus(
					IRodinDBStatusConstants.READ_ONLY,
					parent
			);
		}
		if (newElement != null && newElement.exists()) {
			return new RodinDBStatus(
					IRodinDBStatusConstants.NAME_COLLISION,
					newElement
			);
		}
		if (nextSibling != null) {
			if (! parent.equals(nextSibling.getParent())) {
				return new RodinDBStatus(
						IRodinDBStatusConstants.INVALID_SIBLING,
						nextSibling
				);
			}
			if (! nextSibling.exists()) {
				return new RodinDBStatus(
						IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST,
						nextSibling
				);
			}
		}
		return RodinDBStatus.VERIFIED_OK;
	}
}
