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

import static org.eclipse.core.runtime.Status.OK_STATUS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.rodinp.core.IInternalElement;

class OperationNode extends AbstractOperation implements OperationTree,
		Iterable<OperationTree> {

	protected ArrayList<OperationTree> childrens;

	public OperationNode() {
		super("CommandNode");
		childrens = new ArrayList<OperationTree>();
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		IStatus status = OK_STATUS;
		for (int i = 0; i < childrens.size(); i++) {
			if (status == OK_STATUS) {
				status = childrens.get(i).execute(monitor, info);
			}
		}
		return status;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		IStatus status = OK_STATUS;
		for (int i = 0; i < childrens.size(); i++) {
			if (status == OK_STATUS) {
				status = childrens.get(i).redo(monitor, info);
			}
		}
		return status;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		IStatus status = OK_STATUS;
		for (int i = (childrens.size() - 1); i >= 0; i--) {
			if (status == OK_STATUS) {
				status = childrens.get(i).undo(monitor, info);
			}
		}
		return status;
	}

	public void addCommand(OperationTree cmd) {
		if (cmd != this) {
			childrens.add(cmd);
		}
	}

	public void setParent(IInternalElement element) {
		for (OperationTree children : childrens) {
			children.setParent(element);
		}
	}

	public Collection<IInternalElement> getCreatedElements() {
		ArrayList<IInternalElement> result = new ArrayList<IInternalElement>();
		for (OperationTree op : childrens) {
			result.addAll(op.getCreatedElements());
		}
		return result;
	}

	public IInternalElement getCreatedElement() {
		if (childrens.size() > 0) {
			return childrens.get(0).getCreatedElement();
		} else {
			return null;
		}
	}

	public Iterator<OperationTree> iterator() {
		return childrens.iterator();
	}

}
