/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - generic attribute manipulation
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.internal.core.util.Messages;

public class ChangeElementAttributeOperation extends RodinDBOperation{

	private final IInternalElement element;
	private final AttributeValue<?,?> attrValue;
	
	public ChangeElementAttributeOperation(IInternalElement element,
			AttributeValue<?,?> attrValue) {
		super(new IRodinElement[] { element });
		this.element = element;
		this.attrValue = attrValue;
	}

	@Override
	protected void executeOperation() throws RodinDBException {
		try {
			beginTask(Messages.operation_changeElementAttributeProgress, 2);
			final RodinFile file = ((InternalElement) element).getRodinFile();
			final RodinFileElementInfo fileInfo = (RodinFileElementInfo)
					file.getElementInfo(getSubProgressMonitor(1));
			fileInfo.setAttributeValue(element, attrValue);
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
		final IInternalElementType<?> elemType = element.getElementType();
		final AttributeType<?> attrType = attrValue.getType();
		if (!attrType.isAttributeOf(elemType)) {
			return new RodinDBStatus(
					IRodinDBStatusConstants.INVALID_ATTRIBUTE_TYPE, element,
					attrType.getId());
		}
		return RodinDBStatus.VERIFIED_OK;
	}
}
