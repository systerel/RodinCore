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
import org.eventb.core.IAction;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

class CreateInitialisation extends OperationLeaf {

	private IMachineRoot root;
	private String actLabel;
	private String actSub;

	private IEvent event = null;
	private boolean newInit = true;

	private IAction action;

	// TODO a retravailler en utilisant les autres Operation
	CreateInitialisation(IMachineRoot root, final String actLabel,
			final String actSub) {
		super("CreateInitialisation");
		this.root = root;
		this.actLabel = actLabel;
		this.actSub = actSub;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			// EventBEditorUtils.createNewInitialisationAction(editor, actLabel,
			// actSub, monitor);

			event = getInitialisationEvent(monitor);
			action = event.createChild(IAction.ELEMENT_TYPE, null, monitor);
			action.setLabel(actLabel, monitor);
			action.setAssignmentString(actSub, monitor);
			// editor.addNewElement(action);

		} catch (RodinDBException e) {
			return e.getStatus();
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
			action.delete(true, monitor);
			if (newInit) {
				event.delete(true, monitor);
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Status.OK_STATUS;
	}

	private IEvent getInitialisationEvent(IProgressMonitor monitor)
			throws RodinDBException {
		IEvent result = EventBEditorUtils.getInitialisation(root);
		if (result != null) {
			newInit = false;
			return result;
		}

		newInit = true;
		result = root.createChild(IEvent.ELEMENT_TYPE, null, monitor);
		result.setLabel(IEvent.INITIALISATION, monitor);
		result
				.setConvergence(IConvergenceElement.Convergence.ORDINARY,
						monitor);
		result.setExtended(false, monitor);
		// editor.addNewElement(result);
		return result;
	}

	public void setParent(IInternalElement element) {
		// TODO Auto-generated method stub

	}

	@Override
	public IInternalElement getCreatedElement() {
		return action;
	}
}
