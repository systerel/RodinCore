package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBMachineEditor;

public class NewEvent implements IEditorActionDelegate {

	EventBMachineEditor editor;
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof EventBMachineEditor)
			editor = (EventBMachineEditor) targetEditor;
	}

	public void run(IAction action) {
		UIUtils.newEvent(editor, null);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		return; // Do nothing
	}

}
