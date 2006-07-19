package org.eventb.internal.ui.projectexplorer.actions;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextFile;
import org.eventb.core.IExtendsContext;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

public class Extends implements IObjectActionDelegate {

	private ISelection selection;

	private IWorkbenchPart part;

	/**
	 * Constructor for Action1.
	 */
	public Extends() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		part = targetPart;
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();
				if (!(obj instanceof IContextFile))
					return;
				final IContextFile context = (IContextFile) obj;
				final IRodinProject prj = context.getRodinProject();

				InputDialog dialog = new InputDialog(part.getSite()
						.getShell(), "New EXTENDS Clause",
						"Please enter the name of the new context", "c0",
						new RodinFileInputValidator(prj));

				dialog.open();

				final String abstractContextName = EventBPlugin
						.getComponentName(context.getElementName());
				final String bareName = dialog.getValue();
				if (bareName == null)
					return;
				try {
					RodinCore.run(new IWorkspaceRunnable() {

						public void run(IProgressMonitor monitor)
								throws CoreException {
							IRodinFile newFile = prj.createRodinFile(
									EventBPlugin.getContextFileName(bareName),
									false, null);

							IInternalElement refined = newFile
									.createInternalElement(
											IExtendsContext.ELEMENT_TYPE,
											abstractContextName, null, null);
							refined.setContents(abstractContextName);
							newFile.save(null, true);
							UIUtils.linkToEventBEditor(newFile);

						}

					}, null);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
