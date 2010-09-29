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

import org.eclipse.core.commands.IParameter;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.core.IWitness;
import org.eventb.internal.ui.EventBUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

public class CopyElement extends OperationLeaf {

	private final IInternalElement defaultParent;
	private IInternalElement source;
	private final IInternalElement sibling;
	private boolean first;

	// Operation to delete the copy. Used in undo
	private OperationTree operationDelete;

	public CopyElement(IInternalElement parent, IInternalElement source, IInternalElement sibling) {
		super("CopyElement");
		this.defaultParent = parent;
		this.source = source;
		this.sibling = sibling;
		first = true;
	}

	@Override
	public void doExecute(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		String nameCopy;
		final OperationBuilder builder;
		final IInternalElement element;
		final String copyId;

		if (source instanceof IAction) {
			copyId = "act";
		} else if (source instanceof IAxiom) {
			copyId = "axm";
		} else if (source instanceof ICarrierSet) {
			copyId = "set";
		} else if (source instanceof IConstant) {
			copyId = "cst";
		} else if (source instanceof IEvent) {
			copyId = "evt";
		} else if (source instanceof IGuard) {
			copyId = "grd";
		} else if (source instanceof IInvariant) {
			copyId = "inv";
		} else if (source instanceof IParameter) {
			copyId = "prm";
		} else if (source instanceof IVariant) {
			copyId = "variant";
		} else if (source instanceof IVariable) {
			copyId = "var";
		} else if (source instanceof IWitness) {
			copyId = "wit";
		} else {
			copyId = "element";
		}

		final IInternalElementType<?> copyType = source.getElementType();

		nameCopy = copyId
				+ EventBUtils.getFreeChildNameIndex(defaultParent, copyType,
						copyId);
		source.copy(defaultParent, sibling, nameCopy, false, null);
		builder = new OperationBuilder();
		element = defaultParent.getInternalElement(source.getElementType(),
				nameCopy);

		if (element != null) {
			operationDelete = builder.deleteElement(element, true);
		}
	}

	@Override
	public void doRedo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		if (operationDelete != null) {
			operationDelete.doUndo(monitor, info);
		}
	}

	@Override
	public void doUndo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		if (operationDelete != null) {
			if (first) {
				first = false;
				operationDelete.doExecute(monitor, info);
			} else {
				operationDelete.doRedo(monitor, info);
			}
		}
	}

	@Override
	public void setParent(IInternalElement element) {
		source = element;
	}

}
