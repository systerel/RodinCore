package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eventb.internal.ui.eventbeditor.EventBEditor;
import org.rodinp.core.IRodinFile;

public abstract class PrefixElementName implements IEditorActionDelegate {

	EventBEditor editor;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		editor = (EventBEditor) targetEditor;
	}

	public void setPrefix(QualifiedName qualifiedName, String defaultName, String dialogTitle, String message) {
		IRodinFile inputFile = editor.getRodinInput();
		String prefix = null;
		try {
			prefix = inputFile.getResource().getPersistentProperty(
					qualifiedName);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (prefix == null)
			prefix = defaultName;
		InputDialog dialog = new InputDialog(editor.getSite().getShell(),
				dialogTitle,
				message, prefix, null);
		dialog.open();
		prefix = dialog.getValue();

		try {
			if (prefix != null)
				inputFile.getResource().setPersistentProperty(qualifiedName,
						prefix);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// Do nothing
	}

}
