/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.operations;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
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
			return null;
		}
	}

	private void move(IInternalElement lNewParent,
			IInternalElement lNewSibling, IProgressMonitor monitor)
			throws RodinDBException {
		movedElement.move(lNewParent, lNewSibling, movedElement
				.getElementName(), false, monitor);
	}

	@Override
	public void doExecute(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		move(newParent, newSibling, monitor);
	}

	@Override
	public void doUndo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		move(oldParent, oldSibling, monitor);
	}

	@Override
	public void doRedo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		move(newParent, newSibling, monitor);
	}

	@Override
	public void setParent(IInternalElement element) {
		// do nothing
	}

}