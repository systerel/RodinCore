package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.editpage.EditSectionRegistry;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public abstract class AutoElementNaming<T extends IInternalElement>
		implements IEditorActionDelegate {

	IEventBEditor<?> editor;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof IEventBEditor)
			editor = (IEventBEditor<?>) targetEditor;
	}

	public void rename(final IInternalElementType<T> type, final String prefix,
			final String attributeID) {
		try {
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws RodinDBException {
					IRodinFile file = editor.getRodinInput();
					T[] elements = file.getChildrenOfType(type);

					// Rename to the real desired naming convention
					for (int counter = 1; counter <= elements.length; counter++) {
						T element = elements[counter - 1];
						if (element instanceof IIdentifierElement
								&& attributeID
										.equals(EditSectionRegistry.IDENTIFIER_ATTRIBUTE_ID)) {
							if (EventBEditorUtils.DEBUG)
								EventBEditorUtils.debug("Rename: "
										+ ((IIdentifierElement) element)
												.getIdentifierString() + " to "
										+ prefix + +counter);
							((IIdentifierElement) element)
									.setIdentifierString(prefix + counter,
											new NullProgressMonitor());
						} else if (element instanceof ILabeledElement
								&& attributeID
										.equals(EditSectionRegistry.LABEL_ATTRIBUTE_ID)) {
							if (EventBEditorUtils.DEBUG)
								EventBEditorUtils.debug("Rename: "
										+ ((ILabeledElement) element)
												.getLabel() + " to " + prefix
										+ +counter);
							((ILabeledElement) element).setLabel(prefix
									+ counter, monitor);
						}
					}
				}

			}, null);
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleSetAttributeException(e);
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		return; // Do nothing
	}

	public void run(IInternalElementType<T> type, String attributeID) {
		IRodinFile inputFile = editor.getRodinInput();
		String prefix = null;
		try {
			prefix = inputFile.getResource().getPersistentProperty(
					UIUtils.getQualifiedName(type));
		} catch (CoreException e) {
			EventBUIExceptionHandler.handleGetPersistentPropertyException(e);
		}

		if (prefix == null)
			prefix = EditSectionRegistry.getDefault().getDefaultPrefix(type,
					attributeID);

		rename(type, prefix, attributeID);
	}
}
