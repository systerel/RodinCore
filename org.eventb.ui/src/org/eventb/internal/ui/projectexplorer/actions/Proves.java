package org.eventb.internal.ui.projectexplorer.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.WorkbenchException;
import org.eventb.core.EventBPlugin;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.perspectives.ProvingPerspective;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

public class Proves implements IObjectActionDelegate {

	private ISelection selection;

	/**
	 * Constructor for Action1.
	 */
	public Proves() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();
				if (!(obj instanceof IRodinFile))
					return;
				IRodinFile component = (IRodinFile) obj;
				IRodinProject prj = component.getRodinProject();
				String bareName = component.getBareName();
				IRodinFile prFile = prj.getRodinFile(EventBPlugin
						.getPRFileName(bareName));
				UIUtils.linkToProverUI(prFile);
				try {
					EventBUIPlugin.getActiveWorkbenchWindow().getWorkbench()
							.showPerspective(ProvingPerspective.PERSPECTIVE_ID,
									EventBUIPlugin.getActiveWorkbenchWindow());
				} catch (WorkbenchException e) {
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
