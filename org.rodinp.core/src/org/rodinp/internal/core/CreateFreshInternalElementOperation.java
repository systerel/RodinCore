/*******************************************************************************
 * Copyright (c) 2009, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core;

import static org.rodinp.core.IRodinDBStatusConstants.INVALID_CHILD_TYPE;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

public class CreateFreshInternalElementOperation<T extends IInternalElement>
		extends CreateInternalElementOperation {

	private T newElement;
	private InternalElement iParent;
	private final InternalElementType<T> childType;

	public CreateFreshInternalElementOperation(InternalElement parent,
			IInternalElementType<T> type, IInternalElement nextSibling) {
		super(parent, nextSibling, false);
		this.iParent = parent;
		this.childType = (InternalElementType<T>) type;
	}

	@Override
	protected IInternalElement doCreate() throws RodinDBException {
		RodinFile file = iParent.getRodinFile();
		RodinFileElementInfo fileInfo = (RodinFileElementInfo) file
				.getElementInfo(getSubProgressMonitor(1));
		newElement = fileInfo.create(iParent, childType, nextSibling);
		return newElement;
	}

	public T getResultElement() {
		return newElement;
	}

	/**
	 * Possible failures:
	 * <ul>
	 * <li>Those of the super-class.</li>
	 * <li>INVALID_CHILD_TYPE - the type supplied to the operation is not
	 * allowed for a child.</li>
	 * </ul>
	 */
	@Override
	public IRodinDBStatus verify() {
		final IRodinDBStatus status = super.verify();
		if (!status.isOK())
			return status;
		if (!iParent.getElementType().canParent(childType)) {
			return new RodinDBStatus(INVALID_CHILD_TYPE, iParent,
					childType.toString());
		}
		return RodinDBStatus.VERIFIED_OK;
	}

}
