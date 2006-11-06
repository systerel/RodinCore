package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;

public abstract class AutoElementNaming implements IEditorActionDelegate {

	IEventBEditor editor;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof IEventBEditor)
			editor = (IEventBEditor) targetEditor;
	}

	public void rename(final String type, final String prefix) {
		try {
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					IRodinFile file = editor.getRodinInput();
					IRodinElement[] elements = file.getChildrenOfType(type);

					// Rename to the real desired naming convention
					for (int counter = 1; counter <= elements.length; counter++) {
						IRodinElement element = elements[counter - 1];
						if (element instanceof IIdentifierElement) {
							if (EventBEditorUtils.DEBUG)
								EventBEditorUtils.debug("Rename: "
										+ ((IIdentifierElement) element)
												.getIdentifierString() + " to "
										+ prefix + +counter);
							((IIdentifierElement) element)
									.setIdentifierString(prefix + counter);
						} else if (element instanceof ILabeledElement) {
							if (EventBEditorUtils.DEBUG)
								EventBEditorUtils.debug("Rename: "
										+ ((ILabeledElement) element)
												.getLabel(monitor) + " to "
										+ prefix + +counter);
							((ILabeledElement) element).setLabel(prefix
									+ counter, monitor);

						}
						((IInternalElement) element).rename(prefix + counter,
								false, null);
					}
				}

			}, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		return; // Do nothing
	}

}
