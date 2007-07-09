package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eventb.core.IMachineFile;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IRodinFile;

public class NewVariable implements IEditorActionDelegate {

	IEventBEditor<IMachineFile> editor;
	
	@SuppressWarnings("unchecked")
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof IEventBEditor)
			editor = (IEventBEditor<IMachineFile>) targetEditor;
	}

	public void run(IAction action) {
		IRodinFile rodinFile = editor.getRodinInput();
		EventBEditorUtils.intelligentNewVariable(editor, rodinFile);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		return; // Do nothing
	}

}
