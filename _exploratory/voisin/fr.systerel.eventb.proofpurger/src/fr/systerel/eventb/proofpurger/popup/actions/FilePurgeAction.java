package fr.systerel.eventb.proofpurger.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.rodinp.core.IRodinElement;

public class FilePurgeAction implements IObjectActionDelegate {
	
	private static final Object[] NO_OBJECTS = new Object[0];
	
	private IWorkbenchPartSite site;

	private ISelection selection;
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		site = targetPart.getSite();
	}
	
	private void collectInformation() {
		// TODO implement
	}

	public void run(IAction action) {
		
		final Object[] sel = getSelectedObjects();
		final StringBuilder b = new StringBuilder();
		for (Object o: sel) {
			final IRodinElement e = ((IRodinElement) o);
			b.append('\t');
			b.append(e.getCorrespondingResource().getFullPath());
			b.append('\n');
		}
		
		BusyIndicator.showWhile(site.getShell().getDisplay(), new Runnable() {
			public void run() {
				collectInformation();
			}
		});

        
		// TODO properly implement this action
		final Shell shell = site.getShell();
		MessageDialog.openInformation(
			shell,
			"Proof Purger Plug-in",
			"Purge unused proofs was executed on\n" + b.toString());
	}

	private Object[] getSelectedObjects() {
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ss = (IStructuredSelection) selection;
			return ss.toArray();
		}
		return NO_OBJECTS;
	}

	public void selectionChanged(IAction action, ISelection s) {
		selection = s;
	}

}
