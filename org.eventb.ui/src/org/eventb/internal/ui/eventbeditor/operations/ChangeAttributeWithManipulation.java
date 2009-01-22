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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

class ChangeAttributeWithManipulation extends OperationLeaf {
	private final IAttributeManipulation manipulation;
	private final IInternalElement element;
	private String valueDo;
	private String valueUndo;

	public ChangeAttributeWithManipulation(IAttributeManipulation manipulation,
			IInternalElement element, String value) {
		super("ChangeAttributeWithManipulation");
		this.manipulation = manipulation;
		this.element = element;
		this.valueDo = value;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			// if getValue throws an exception, valueUndo is initialised with
			// null
			valueUndo = null;
			if (manipulation.hasValue(element, monitor)) {
				valueUndo = manipulation.getValue(element, monitor);
			}
			setValue(valueDo, monitor);
		} catch (RodinDBException e) {
			return e.getStatus();
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			setValue(valueDo, monitor);
		} catch (RodinDBException e) {
			return e.getStatus();
		}
		return Status.OK_STATUS;
	}

	private void setValue(String value, IProgressMonitor monitor)
			throws RodinDBException {
		if (value != null) {
			manipulation.setValue(element, value, monitor);
		} else {
			manipulation.removeAttribute(element, monitor);
		}
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			setValue(valueUndo, monitor);
		} catch (RodinDBException e) {
			return e.getStatus();
		}
		return Status.OK_STATUS;
	}

	/**
	 * parent is the element to be modified.
	 * <p>
	 * The method is not available
	 */
	public void setParent(IInternalElement element) {
		// The method is not available
	}
}
