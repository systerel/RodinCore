/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
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
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

class DeleteElementLeaf extends OperationLeaf {

	private IInternalElement element;
	private OperationTree createTree;
	private IInternalElement nextSibling;
	private final boolean force;

	public DeleteElementLeaf(IInternalElement element, OperationTree createTree) {
		super("DeleteElement");
		this.element = element;
		this.createTree = createTree;
		nextSibling = null;
		force = true;
	}

	public DeleteElementLeaf(IInternalElement element,
			OperationTree createTree, boolean force) {
		super("DeleteElement");
		this.element = element;
		this.createTree = createTree;
		nextSibling = null;
		this.force = force;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			try {
				nextSibling = element.getNextSibling();
			} catch (RodinDBException e) {
				nextSibling = null;
			}
			element.delete(force, monitor);
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			EventBUIExceptionHandler.handleDeleteElementException(e);
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			element.create(nextSibling, monitor);
			createTree.execute(monitor, info);
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Status.OK_STATUS;
	}

	public void setParent(IInternalElement element) {
		// TODO Auto-generated method stub

	}
}
