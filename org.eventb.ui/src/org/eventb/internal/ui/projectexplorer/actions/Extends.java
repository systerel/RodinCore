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
import org.eventb.internal.ui.eventbeditor.actions.PrefixExtendsContextName;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

public class Extends implements IObjectActionDelegate {

	private ISelection selection;

	private IWorkbenchPart part;

	private IRodinFile newFile;

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

				InputDialog dialog = new InputDialog(part.getSite().getShell(),
						"New EXTENDS Clause",
						"Please enter the name of the new context", "c0",
						new RodinFileInputValidator(prj));

				dialog.open();

				final String abstractContextName = context.getComponentName();
				final String bareName = dialog.getValue();
				if (bareName == null)
					return;
				try {
					RodinCore.run(new IWorkspaceRunnable() {

						public void run(IProgressMonitor monitor)
								throws CoreException {
							newFile = prj.createRodinFile(EventBPlugin
									.getContextFileName(bareName), false, null);

							IExtendsContext extended = (IExtendsContext) newFile
									.createInternalElement(
											IExtendsContext.ELEMENT_TYPE,
											"internal_"
													+ PrefixExtendsContextName.DEFAULT_PREFIX
													+ 1, null, monitor);
							extended
									.setAbstractContextName(abstractContextName, monitor);
							newFile.save(monitor, true);
						}

					}, null);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					newFile = null;
				}
				if (newFile != null)
					UIUtils.linkToEventBEditor(newFile);

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
