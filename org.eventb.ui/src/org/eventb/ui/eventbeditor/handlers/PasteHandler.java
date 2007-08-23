package org.eventb.ui.eventbeditor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eventb.internal.ui.RodinHandleTransfer;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class PasteHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {

		ISelection selection = EventBUIPlugin.getActivePage().getSelection();
		if (selection == null)
			return "Must have a selection to paste";

		IWorkbench workbench = EventBUIPlugin.getDefault().getWorkbench();
		Clipboard clipboard = new Clipboard(workbench.getDisplay());

		// try a rodin handle transfer
		RodinHandleTransfer rodinHandleTransfer = RodinHandleTransfer
				.getInstance();
		final IRodinElement[] handleData = (IRodinElement[]) clipboard
				.getContents(rodinHandleTransfer);

		if (handleData == null)
			return "Nothing to paste";
		
		
		for (IRodinElement element : handleData) {
			if (!element.exists()) {
				Shell shell = workbench.getActiveWorkbenchWindow().getShell();
				MessageDialog.openError(shell, "Cannot Paste", "Element "
						+ element + " does not exist.");
			}
		}
		IStructuredSelection ssel = (IStructuredSelection) selection;
		final IRodinElement sibling = getTarget(ssel);
		if (sibling == null || !sibling.exists())
			return "Target does not exist";
		
		try {
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws RodinDBException {
					for (IRodinElement element : handleData) {
						IRodinElement parent = sibling.getParent();
						IInternalElement internalElement = (IInternalElement) element;
						(internalElement).copy(parent, sibling, "element"
								+ UIUtils.getFreeElementNameIndex(
										(IInternalParent) parent,
										internalElement.getElementType(),
										"element", 0), false, null);
					}
				}

			}, null);
		} catch (RodinDBException e) {
			return "Paste unsuccessful";
		}
	
		System.out.println("PASTE SUCCESSFULLY");
		return null;
	}
	
	/**
	 * Returns the actual target of the paste action. Returns null if no valid
	 * target is selected.
	 * 
	 * @return the actual target of the paste action
	 */
	private IRodinElement getTarget(IStructuredSelection selection) {
		if (selection.size() == 0)
			return null;

		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof IRodinElement)
			return (IRodinElement) firstElement;

		return null;
	}


}
