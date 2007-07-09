package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.editpage.EditSectionRegistry;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;

public abstract class PrefixElementName<T extends IInternalElement> implements IEditorActionDelegate {

	IEventBEditor<?> editor;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		editor = (IEventBEditor<?>) targetEditor;
	}

	public void setPrefix(IInternalElementType<T> type, String attributeID,
			String dialogTitle, String message) {
		QualifiedName qualifiedName = UIUtils.getQualifiedName(type);
		IRodinFile inputFile = editor.getRodinInput();
		String prefix = null;
		try {
			prefix = inputFile.getResource().getPersistentProperty(
					qualifiedName);
		} catch (CoreException e) {
			EventBUIExceptionHandler.handleGetPersistentPropertyException(e);
		}

		if (prefix == null)
			prefix = EditSectionRegistry.getDefault().getDefaultPrefix(type,
					attributeID);
		InputDialog dialog = new InputDialog(editor.getSite().getShell(),
				dialogTitle, message, prefix, null);
		dialog.open();
		prefix = dialog.getValue();

		try {
			if (prefix != null)
				inputFile.getResource().setPersistentProperty(qualifiedName,
						prefix);
		} catch (CoreException e) {
			EventBUIExceptionHandler.handleSetPersistentPropertyException(e);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// Do nothing
	}

}
