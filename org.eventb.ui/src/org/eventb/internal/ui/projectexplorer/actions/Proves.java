package org.eventb.internal.ui.projectexplorer.actions;

import static org.eventb.ui.EventBUIPlugin.PROVING_PERSPECTIVE_ID;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.WorkbenchException;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.EventBUIPlugin;

public class Proves implements IObjectActionDelegate {

	private ISelection selection;

	/**
	 * Constructor for Action1.
	 */
	public Proves() {
		super();
	}

	@Override
	public void run(IAction action) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();
				UIUtils.linkToProverUI(obj);
				try {
					EventBUIPlugin.getActiveWorkbenchWindow().getWorkbench()
							.showPerspective(PROVING_PERSPECTIVE_ID,
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
	@Override
	public void selectionChanged(IAction action, ISelection sel) {
		this.selection = sel;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// Do nothing
	}
}
