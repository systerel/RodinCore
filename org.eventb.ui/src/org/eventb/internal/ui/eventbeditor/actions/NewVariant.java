package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eventb.core.IVariant;
import org.eventb.internal.ui.eventbeditor.EventBEditor;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public class NewVariant implements IEditorActionDelegate {

	EventBEditor editor;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof EventBEditor)
			editor = (EventBEditor) targetEditor;
	}

	public void run(IAction action) {
		IRodinFile rodinFile = editor.getRodinInput();
		try {
			if (rodinFile.getChildrenOfType(IVariant.ELEMENT_TYPE).length != 0)
				MessageDialog.openError(editor.getEditorSite().getShell(),
						"Variant Exist",
						"Variant already exists in this machine");
			else
				EventBEditorUtils.newVariant(editor, rodinFile);
		} catch (RodinDBException e) {
			if (EventBEditorUtils.DEBUG) {
				EventBEditorUtils
						.debug("Error getting children of type variant of "
								+ rodinFile.getElementType());
				e.printStackTrace();
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		return; // Do nothing
	}

}
