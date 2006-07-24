package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eventb.core.IInvariant;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;

public class AutoInvNaming implements IEditorActionDelegate {

	EventBEditor editor;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof EventBEditor)
			editor = (EventBEditor) targetEditor;
	}

	public void run(IAction action) {
		try {
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					IRodinFile file = editor.getRodinInput();
					IRodinElement[] invariants = file
							.getChildrenOfType(IInvariant.ELEMENT_TYPE);

					// Dummy name first
					for (int counter = 1; counter <= invariants.length; counter++) {
						IRodinElement inv = invariants[counter-1];
						UIUtils.debugEventBEditor("Rename: "
								+ inv.getElementName() + " to ___"
								+ inv.getElementName());

						((IInternalElement) inv).rename("___"
								+ inv.getElementName(), false, null);
					}

					invariants = file
							.getChildrenOfType(IInvariant.ELEMENT_TYPE);

					// Rename to the real desired naming convention
					for (int counter = 1; counter <= invariants.length; counter++) {
						IRodinElement inv = invariants[counter-1];
						UIUtils.debugEventBEditor("Rename: "
								+ inv.getElementName() + " to inv"
								+ counter);
						((IInternalElement) inv).rename("inv" + counter, false,
								null);
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
