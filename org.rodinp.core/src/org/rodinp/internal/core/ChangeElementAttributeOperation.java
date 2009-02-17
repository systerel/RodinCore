/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.internal.core.util.Messages;

public class ChangeElementAttributeOperation extends RodinDBOperation{

	private IInternalElement element;
	private String attrName;
	private String newValue;
	
	public ChangeElementAttributeOperation(IInternalElement element,
			String attrName, String newValue) {
		super(new IRodinElement[] { element });
		this.element = element;
		this.attrName = attrName;
		this.newValue = newValue;
	}

	@Override
	protected void executeOperation() throws RodinDBException {
		try {
			beginTask(Messages.operation_changeElementAttributeProgress, 2);
			final RodinFile file = ((InternalElement) element).getRodinFile();
			final RodinFileElementInfo fileInfo = (RodinFileElementInfo)
					file.getElementInfo(getSubProgressMonitor(1));
			fileInfo.setAttributeRawValue(element, attrName, newValue);
			final RodinElementDelta delta = newRodinElementDelta();
			delta.changed(element, IRodinElementDelta.F_ATTRIBUTE);
			addDelta(delta);
			worked(1);
		} finally {
			done();
		}
	}

	@Override
	protected ISchedulingRule getSchedulingRule() {
		assert false;
		return null;
	}

	/**
	 * Possible failures:
	 * <ul>
	 * <li>NO_ELEMENTS_TO_PROCESS - the element supplied to the operation is
	 * <code>null</code>.
	 * <li>ELEMENT_DOES_NOT_EXIST - the element supplied to the operation
	 * doesn't exist yet.
	 * <li>READ_ONLY - the element supplied to the operation is read only.
	 * <li>NULL_STRING - the new value supplied to the operation is
	 * <code>null</code>.
	 * </ul>
	 */
	@Override
	public IRodinDBStatus verify() {
		super.verify();
		if (! element.exists()) {
			return new RodinDBStatus(
					IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST,
					element
			);
		}
		if (element.isReadOnly()) {
			return new RodinDBStatus(
					IRodinDBStatusConstants.READ_ONLY,
					element
			);
		}
		// Existence of the attribute type has been checked by caller.
		if (newValue == null) {
			return new RodinDBStatus(IRodinDBStatusConstants.NULL_STRING);
		}
		return RodinDBStatus.VERIFIED_OK;
	}
}
