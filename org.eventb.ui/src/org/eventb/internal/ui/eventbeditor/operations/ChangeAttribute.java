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
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

class ChangeAttribute extends OperationLeaf {

	private IInternalElement element;
	private final IAttributeValue[] values;
	
	private final List<IAttributeValue> newValues = new ArrayList<IAttributeValue>();
	private final List<IAttributeValue> oldValues = new ArrayList<IAttributeValue>();
	private final List<IAttributeType> newAttributes = new ArrayList<IAttributeType>();

	public ChangeAttribute(IAttributeValue[] values) {
		super("ChangeAttribute");
		this.values = values;
	}

	public ChangeAttribute(IInternalElement element, IAttributeValue[] values) {
		super("ChangeAttribute");
		this.element = element;
		this.values = values;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		assert element != null;
		try {
			for (IAttributeValue value : values) {
				final IAttributeType type = value.getType();
				if (element.hasAttribute(type)) {
					oldValues.add(element.getAttributeValue(type));
				} else {
					newAttributes.add(type);
				}
				element.setAttributeValue(value, monitor);
				newValues.add(value);
			}
		} catch (RodinDBException e) {
			UIUtils.log(e, "when executing operation " + this);
			return e.getStatus();
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			for (IAttributeValue value : newValues) {
				element.setAttributeValue(value, monitor);
			}
		} catch (RodinDBException e) {
			UIUtils.log(e, "when redoing operation " + this);
			return e.getStatus();
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			for (IAttributeValue value : oldValues) {
				element.setAttributeValue(value, monitor);
			}
			for (IAttributeType type : newAttributes) {
				element.removeAttribute(type, monitor);
			}
		} catch (RodinDBException e) {
			UIUtils.log(e, "when undoing operation " + this);
			return e.getStatus();
		}
		return Status.OK_STATUS;
	}

	public void setParent(IInternalElement element) {
		this.element = element;
	}
}
