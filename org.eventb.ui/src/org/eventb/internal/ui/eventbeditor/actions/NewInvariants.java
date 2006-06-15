package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditor;
import org.rodinp.core.IRodinFile;

public class NewInvariants implements IEditorActionDelegate {

	EventBEditor editor;
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof EventBEditor)
			editor = (EventBEditor) targetEditor;
	}

	public void run(IAction action) {
		IRodinFile rodinFile = editor.getRodinInput();
		UIUtils.newInvariants(editor, rodinFile);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		return; // Do nothing
	}


}
