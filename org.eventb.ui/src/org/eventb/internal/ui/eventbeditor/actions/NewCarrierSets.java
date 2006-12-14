package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eventb.internal.ui.eventbeditor.EventBContextEditor;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;

public class NewCarrierSets implements IEditorActionDelegate {
	
	EventBContextEditor editor;
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof EventBContextEditor)
			editor = (EventBContextEditor) targetEditor;
	}

	public void run(IAction action) {
		EventBEditorUtils.newCarrierSets(editor, null);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		return; // Do nothing
	}

}
