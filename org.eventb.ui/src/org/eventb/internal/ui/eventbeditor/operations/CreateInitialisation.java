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
import org.eventb.core.IMachineFile;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.actions.PrefixEvtName;
import org.eventb.internal.ui.eventbeditor.editpage.AttributeRelUISpecRegistry;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

class CreateInitialisation extends OperationLeaf {

	private IEventBEditor<IMachineFile> editor;
	private String actLabel;
	private String actSub;

	private IEvent event = null;
	private boolean newInit = true;

	private IAction action;


	// TODO a retravailler en utilisant les autres Operation
	CreateInitialisation(final IEventBEditor<IMachineFile> editor,
			final String actLabel, final String actSub) {
		super("CreateInitialisation");
		this.editor = editor;
		this.actLabel = actLabel;
		this.actSub = actSub;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			// EventBEditorUtils.createNewInitialisationAction(editor, actLabel,
			// actSub, monitor);
			final String name;
			final String defaultPrefix;

			event = getInitialisationEvent(monitor);
			defaultPrefix = AttributeRelUISpecRegistry.getDefault()
					.getDefaultPrefix("org.eventb.core.actionLabel");
			name = UIUtils.getFreeElementName(editor, event,
					IAction.ELEMENT_TYPE, defaultPrefix);
			action = event.getInternalElement(IAction.ELEMENT_TYPE, name);
			assert !action.exists();
			action.create(null, monitor);
			action.setLabel(actLabel, monitor);
			action.setAssignmentString(actSub, monitor);
			editor.addNewElement(action);

		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

		return Status.OK_STATUS ;
	}

	private IEvent getInitialisationEvent(IProgressMonitor monitor)
			throws RodinDBException {
		final IMachineFile rodinFile = editor.getRodinInput();
		IEvent result = EventBEditorUtils.getInitialisation(rodinFile);
		if (result != null) {
			return result;
		}
		
		final String evtName = UIUtils.getFreeElementName(editor, rodinFile,
				IEvent.ELEMENT_TYPE, PrefixEvtName.DEFAULT_PREFIX);
		result = rodinFile.getEvent(evtName);
		assert !result.exists();
		result.create(null, monitor);
		result.setLabel(IEvent.INITIALISATION, monitor);
		result.setConvergence(IConvergenceElement.Convergence.ORDINARY,
						monitor);
		result.setExtended(false, monitor);
		editor.addNewElement(result);
		return result;
	}

	public void setParent(IInternalElement element) {
		// TODO Auto-generated method stub
		
	}
}
