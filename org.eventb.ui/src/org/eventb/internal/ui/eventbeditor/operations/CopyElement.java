/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.internal.ui.EventBUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

public class CopyElement extends OperationLeaf {

	private final IInternalElement defaultParent;
	private IInternalElement source;
	private boolean first;
	// Operation to delete the copy. Use in undo
	private OperationTree operationDelete;

	public CopyElement(IInternalElement parent,
			IInternalElement source) {
		super("CopyElement");
		this.defaultParent = parent;
		this.source = source;
		first = true;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		IInternalElement copyParent;
		String nameCopy;
		final OperationBuilder builder;
		final IInternalElement element;
		final String copyId;

		try {
			copyParent = defaultParent.getRoot();
			if (source instanceof IEvent) {
				copyId = "evt";
			} else if (source instanceof IInvariant) {
				copyId = "inv";
			} else if (source instanceof IVariant) {
				copyId = "variant";
			} else if (source instanceof IAxiom) {
				copyId = "axm";
			} else if (source instanceof IConstant) {
				copyId = "cst";
			} else if (source instanceof ICarrierSet) {
				copyId = "set";

			} else if (source instanceof IVariable) {
				copyId = "set";
			} else {
				copyId = "element";
				copyParent = defaultParent;
			}

			final IInternalElementType<?> copyType = source.getElementType();

			nameCopy = copyId
					+ EventBUtils.getFreeChildNameIndex(copyParent, copyType,
							copyId);
			source.copy(copyParent, null, nameCopy, false, null);
			builder = new OperationBuilder();
			element = copyParent.getInternalElement(source.getElementType(),
					nameCopy);

			if (element != null) {
				operationDelete = builder.deleteElement(element, true);
				return Status.OK_STATUS;
			} else {
				operationDelete = null;
				return Status.OK_STATUS;
			}
		} catch (RodinDBException e) {
			operationDelete = null;
			return e.getStatus();
		}
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (operationDelete != null) {
			return operationDelete.undo(monitor, info);
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (operationDelete != null) {
			if (first) {
				first = false;
				return operationDelete.execute(monitor, info);
			} else {
				return operationDelete.redo(monitor, info);
			}
		}
		return Status.OK_STATUS;
	}

	public void setParent(IInternalElement element) {
		source = element;
	}

}
