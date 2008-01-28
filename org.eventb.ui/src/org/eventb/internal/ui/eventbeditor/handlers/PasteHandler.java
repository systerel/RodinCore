/*******************************************************************************
 * Copyright (c) 2006-2008 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Rodin @ ETH Zurich
******************************************************************************/

package org.eventb.internal.ui.eventbeditor.handlers;

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
import org.eclipse.ui.IWorkbenchPage;
import org.eventb.internal.ui.EventBUtils;
import org.eventb.internal.ui.RodinHandleTransfer;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         An extension of {@link AbstractHandler} for handling Paste action.
 */
public class PasteHandler extends AbstractHandler implements IHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {

		// Get the current selection from the active page.
		IWorkbenchPage activePage = EventBUIPlugin.getActivePage();
		ISelection selection = activePage.getSelection();
		
		// Do nothing if there is no selection.
		if (selection == null) {
			if (EventBEditorUtils.DEBUG) {
				EventBEditorUtils.debug("Paste action: Current active page is "
						+ activePage);
			}
			return "Must have a selection to paste";
		}

		// Create the clipboard associated with the workbench.
		IWorkbench workbench = EventBUIPlugin.getDefault().getWorkbench();
		Clipboard clipboard = new Clipboard(workbench.getDisplay());

		// Try to handle by using a rodin handle transfer.
		RodinHandleTransfer rodinHandleTransfer = RodinHandleTransfer
				.getInstance();
		final IRodinElement[] handleData = (IRodinElement[]) clipboard
				.getContents(rodinHandleTransfer);

		// There is no data in the clipboard for rodin handle transfer then do nothing.
		if (handleData == null)
			return "Nothing to paste";
		
		// Check for the existing of the elements to be pasted.
		for (IRodinElement element : handleData) {
			if (!element.exists()) {
				Shell shell = workbench.getActiveWorkbenchWindow().getShell();
				MessageDialog.openError(shell, "Cannot Paste", "Element "
						+ element + " does not exist.");
			}
		}
		
		// Get the target from the current selection.
		IStructuredSelection ssel = (IStructuredSelection) selection;
		final IRodinElement sibling = getTarget(ssel);
		if (sibling == null || !sibling.exists())
			return "Target does not exist";
		
		// Copy the elements from clipboard to the target.
		try {
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws RodinDBException {
					for (IRodinElement element : handleData) {
						IRodinElement parent = sibling.getParent();
						IInternalElement internalElement = (IInternalElement) element;
						(internalElement).copy(parent, sibling, "element"
								+ EventBUtils.getFreeChildNameIndex(
										(IInternalParent) parent,
										internalElement.getElementType(),
										"element", 0), false, null);
					}
				}

			}, null);
		} catch (RodinDBException e) {
			return "Paste unsuccessful";
		}
	
		if (EventBEditorUtils.DEBUG)
			EventBEditorUtils.debug("PASTE SUCCESSFULLY");
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
