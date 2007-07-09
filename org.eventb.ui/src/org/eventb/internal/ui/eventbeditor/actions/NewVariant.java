package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eventb.core.IMachineFile;
import org.eventb.core.IVariant;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public class NewVariant implements IEditorActionDelegate {

	IEventBEditor<IMachineFile> editor;

	@SuppressWarnings("unchecked")
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof IEventBEditor)
			editor = (IEventBEditor<IMachineFile>) targetEditor;
	}

	public void run(IAction action) {
		IRodinFile rodinFile = editor.getRodinInput();
		int length;
		try {
			length = rodinFile.getChildrenOfType(IVariant.ELEMENT_TYPE).length;
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleGetChildrenException(e);
			return;
		}
		if (length != 0)
			MessageDialog.openError(editor.getEditorSite().getShell(),
					"Variant Exist", "Variant already exists in this machine");
		else
			EventBEditorUtils.newVariant(editor, rodinFile);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		return; // Do nothing
	}

}
