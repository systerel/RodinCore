package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBContextEditor;

public class NewEnumeratedSet implements IEditorActionDelegate {

	EventBContextEditor editor;
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof EventBContextEditor)
			editor = (EventBContextEditor) targetEditor;
	}

	public void run(IAction action) {
		UIUtils.newEnumeratedSet(editor, null);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		return; // Do nothing
	}
	
}
