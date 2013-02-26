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

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
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
	 * <li>NO_ELEMENTS_TO_PROCESS - the newElement supplied to the operation is
	 * <code>null</code>.</li>
	 * <li>INVALID_CHILD_TYPE - the type of the element to create is invalid
	 * with the type of the parent element</li>
	 * <li>READ_ONLY - the parent of the newElement supplied is readonly.</li>
	 * <li>NAME_COLLISION - the newElement supplied already exists and creating
	 * it anew would create a duplicate element.</li>
	 * <li>INVALID_SIBLING - the sibling supplied to the operation has a
	 * different parent.</li>
	 * <li>ELEMENT_DOES_NOT_EXIST - the sibling supplied doesn't exist.</li>
	 * </ul>
	 */
	@Override
	public IRodinDBStatus verify() {
		final InternalElementType<?> pType = (InternalElementType<?>) iParent
				.getElementType();
		if (!pType.canParent(childType)) {
			return new RodinDBStatus(
					IRodinDBStatusConstants.INVALID_CHILD_TYPE, iParent,
					childType.getId());
		}
		return super.verify();
	}

}
