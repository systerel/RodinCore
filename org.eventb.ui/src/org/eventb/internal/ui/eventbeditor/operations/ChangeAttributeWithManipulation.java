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
package org.eventb.internal.ui.eventbeditor.operations;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.ui.manipulation.IAttributeManipulation;
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
	public void doExecute(IProgressMonitor monitor, IAdaptable info)
			throws CoreException {
		if (manipulation.hasValue(element, monitor)) {
			valueUndo = manipulation.getValue(element, monitor);
		}
		setValue(valueDo, monitor);
	}

	@Override
	public void doRedo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		setValue(valueDo, monitor);
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
	public void doUndo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		setValue(valueUndo, monitor);
	}

	/**
	 * parent is the element to be modified.
	 * <p>
	 * The method is not available
	 */
	@Override
	public void setParent(IInternalElement element) {
		// The method is not available
	}
}
