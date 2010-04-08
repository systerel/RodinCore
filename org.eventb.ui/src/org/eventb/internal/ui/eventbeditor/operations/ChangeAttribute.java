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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
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
	public void doExecute(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		assert element != null;
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
	}

	@Override
	public void doRedo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		for (IAttributeValue value : newValues) {
			element.setAttributeValue(value, monitor);
		}
	}

	@Override
	public void doUndo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		for (IAttributeValue value : oldValues) {
			element.setAttributeValue(value, monitor);
		}
		for (IAttributeType type : newAttributes) {
			element.removeAttribute(type, monitor);
		}
	}

	public void setParent(IInternalElement element) {
		this.element = element;
	}
}
