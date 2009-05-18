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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.rodinp.core.IInternalElement;

/**
 * Operation Node to create an element. A root operation create the element and
 * the children operation set the attribute.
 */
public class OperationCreateElement extends AbstractOperation implements
		OperationTree {

	private final CreateElementGeneric<?> operationCreate;

	private final OperationNode operationChildren;

	public OperationCreateElement(CreateElementGeneric<?> operationCreate) {
		super("OperationCreatedElement");
		this.operationCreate = operationCreate;
		operationChildren = new OperationNode();
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		operationCreate.execute(monitor, info);
		final IInternalElement element = operationCreate.getCreatedElement();
		for (OperationTree op : operationChildren) {
			op.setParent(element);
		}
		return operationChildren.execute(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		operationCreate.redo(monitor, info);
		return operationChildren.redo(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return operationCreate.undo(monitor, info);
	}

	/**
	 * Set parent for creation of the element
	 */
	public void setParent(IInternalElement element) {
		operationCreate.setParent(element);
	}

	/**
	 * @return if many element are created, the first created element are the
	 *         first in the resulting collection. For example, in case of an
	 *         event with action, getCreatedElements().get(0) is the event.
	 * 
	 */
	public Collection<IInternalElement> getCreatedElements() {
		return operationCreate.getCreatedElements();
	}



	/**
	 * @return if many element are created, return the first created element. For example, in case of an
	 *         event with action, getCreatedElement() is the event.
	 * 
	 */
	public IInternalElement getCreatedElement() {
		return operationCreate.getCreatedElement();
	}


	
	public void addSubCommande(OperationTree cmd) {
		if (cmd != this) {
			operationChildren.addCommand(cmd);
		}
	}
}
