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
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class Move extends OperationLeaf {

	final IInternalElement movedElement;
	final IInternalElement oldParent;
	final IInternalElement oldSibling;
	final IInternalElement newParent;
	final IInternalElement newSibling;

	public Move(IInternalElement movedElement, IInternalElement newParent,
			IInternalElement newSibling) {
		super("Move");
		this.movedElement = movedElement;
		this.newParent = newParent;
		this.newSibling = newSibling;
		oldParent = (IInternalElement) movedElement.getParent();
		oldSibling = getNextSibling(movedElement);

	}

	private IInternalElement getNextSibling(IInternalElement element) {
		try {
			return element.getNextSibling();
		} catch (RodinDBException e) {
			e.printStackTrace();
			return null;
		}
	}

	private IStatus move(IInternalElement lNewParent,
			IInternalElement lNewSibling, IProgressMonitor monitor) {
		try {
			movedElement.move(lNewParent, lNewSibling, movedElement
					.getElementName(), false, monitor);
		} catch (RodinDBException e) {
			e.printStackTrace();
			return e.getStatus();
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return move(newParent, newSibling, monitor);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return move(oldParent, oldSibling, monitor);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return move(newParent, newSibling, monitor);
	}

	public void setParent(IInternalElement element) {
		// TODO Auto-generated method stub

	}

}