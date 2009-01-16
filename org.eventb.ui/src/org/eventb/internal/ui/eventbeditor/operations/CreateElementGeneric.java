/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.operations;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.internal.ui.eventbeditor.editpage.AttributeRelUISpecRegistry;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;

class CreateElementGeneric<T extends IInternalElement> extends OperationLeaf {

	private IInternalElement parent;
	private final IInternalElementType<T> type;
	private final IInternalElement sibling;
	private IInternalElement element;
	private OperationTree operationDelete;
	private boolean first;

	public CreateElementGeneric(IInternalElement parent,
			final IInternalElementType<T> type, final IInternalElement sibling) {
		super("CreateElementGeneric");
		this.parent = parent;
		this.type = type;
		this.sibling = sibling;
		first = true;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {

		try {
			final IInternalElement root = parent.getRoot();
			element = AttributeRelUISpecRegistry.getDefault().createElement(
					root, parent, type, sibling);
			// page.recursiveExpand(element);
		} catch (CoreException e) {
			return e.getStatus();
		}
		final OperationBuilder builder = new OperationBuilder();
		operationDelete = builder.deleteElement(element, true);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return operationDelete.undo(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (first) {
			return operationDelete.execute(monitor, info);
		} else {
			return operationDelete.redo(monitor, info);
		}
	}

	public IInternalElement getElement() {
		return element;
	}

	public void setParent(IInternalElement element) {
		parent = element;
	}

	@Override
	public Collection<IInternalElement> getCreatedElements() {
		ArrayList<IInternalElement> result = new ArrayList<IInternalElement>();
		result.add(element);
		return result;
	}

	@Override
	public IInternalElement getCreatedElement() {
		return element;
	}
}
