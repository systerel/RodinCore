/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added history support
 *     Systerel - added check before pasting element
 *     Systerel - redirected dialog opening
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.handlers;

import static org.eventb.internal.ui.utils.Messages.dialogs_nothingToPaste;
import static org.eventb.internal.ui.utils.Messages.title_nothingToPaste;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eventb.internal.ui.RodinHandleTransfer;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 *         <p>
 *         An extension of {@link AbstractHandler} for handling Paste action.
 */
public class PasteHandler extends AbstractHandler {

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
		final IRodinElement[] elements = (IRodinElement[]) clipboard
				.getContents(rodinHandleTransfer);

		// There is no data in the clipboard for rodin handle transfer then do nothing.
		if (elements == null)
			return "Nothing to paste";
		
		// Check for the existing of the elements to be pasted.
		for (IRodinElement element : elements) {
			if (!element.exists()) {
				UIUtils.showError(title_nothingToPaste,
						dialogs_nothingToPaste(element));
			}
		}
		
		// Get the target from the current selection.
		IStructuredSelection ssel = (IStructuredSelection) selection;
		final IRodinElement target = getTarget(ssel);
		if (!(target instanceof IInternalElement) || !target.exists())
			return "Target does not exist";

		EventBEditorUtils.copyElements((IInternalElement) target, elements);
		return null;
	}
	
	/**
	 * Returns the actual target of the paste action. Returns null if no valid
	 * target is selected.
	 * 
	 * @return the actual target of the paste action
	 */
	private IRodinElement getTarget(IStructuredSelection selection) {
		if (selection.size() == 0) {
			return getRodinInput();
		}
		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof IRodinElement)
			return (IRodinElement) firstElement;

		return null;
	}

	/**
	 * Returns the Rodin input of the active editor or null if the editor is not
	 * an IEventBEditor.
	 * 
	 * @return the Rodin input of the active editor or null if there is not.
	 * */
	private IInternalElement getRodinInput() {
		IEditorPart editor = EventBUIPlugin.getActivePage().getActiveEditor();
		if (editor instanceof IEventBEditor<?>) {
			return ((IEventBEditor<?>) editor).getRodinInput();
		} else {
			return null;
		}
	}
}
