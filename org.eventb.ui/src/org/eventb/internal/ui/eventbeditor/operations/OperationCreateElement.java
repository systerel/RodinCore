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

import java.util.Collection;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Operation Node to create an element. A root operation create the element and
 * the children operation set the attribute.
 */
public class OperationCreateElement extends AbstractEventBOperation implements
		OperationTree {

	private final CreateElementGeneric<?> operationCreate;

	private final OperationNode operationChildren;

	public OperationCreateElement(CreateElementGeneric<?> operationCreate) {
		super("OperationCreatedElement");
		this.operationCreate = operationCreate;
		operationChildren = new OperationNode();
	}

	@Override
	public void doExecute(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		operationCreate.doExecute(monitor, info);
		final IInternalElement element = operationCreate.getCreatedElement();
		operationChildren.setParent(element);
		operationChildren.doExecute(monitor, info);
	}

	@Override
	public void doRedo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		operationCreate.doRedo(monitor, info);
		operationChildren.doRedo(monitor, info);
	}

	@Override
	public void doUndo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		operationCreate.doUndo(monitor, info);
	}

	/**
	 * Set parent for creation of the element
	 */
	@Override
	public void setParent(IInternalElement element) {
		operationCreate.setParent(element);
	}

	/**
	 * @return if many element are created, the first created element are the
	 *         first in the resulting collection. For example, in case of an
	 *         event with action, getCreatedElements().get(0) is the event.
	 * 
	 */
	@Override
	public Collection<IInternalElement> getCreatedElements() {
		return operationCreate.getCreatedElements();
	}

	/**
	 * @return if many element are created, return the first created element. For example, in case of an
	 *         event with action, getCreatedElement() is the event.
	 * 
	 */
	@Override
	public IInternalElement getCreatedElement() {
		return operationCreate.getCreatedElement();
	}

	public void addSubCommande(OperationTree cmd) {
		if (cmd != this) {
			operationChildren.addCommand(cmd);
		}
	}
}
