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
package fr.systerel.editor.internal.actions.operations;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IAction;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class CreateInitialisation extends OperationLeaf {

	private IMachineRoot root;
	private String actLabel;
	private String actSub;

	private IEvent event = null;
	private boolean newInit = true;

	private IAction action;

	// TODO a retravailler en utilisant les autres Operation
	public CreateInitialisation(IMachineRoot root, final String actLabel,
			final String actSub) {
		super("CreateInitialisation");
		this.root = root;
		this.actLabel = actLabel;
		this.actSub = actSub;
	}

	@Override
	public void doExecute(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		event = getInitialisationEvent(monitor);
		action = event.createChild(IAction.ELEMENT_TYPE, null, monitor);
		action.setLabel(actLabel, monitor);
		action.setAssignmentString(actSub, monitor);
	}

	@Override
	public void doRedo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		doExecute(monitor, info);
	}

	@Override
	public void doUndo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		action.delete(true, monitor);
		if (newInit) {
			event.delete(true, monitor);
		}
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

	@Override
	public void setParent(IInternalElement element) {
		// TODO Auto-generated method stub

	}

	@Override
	public IInternalElement getCreatedElement() {
		return action;
	}
}
