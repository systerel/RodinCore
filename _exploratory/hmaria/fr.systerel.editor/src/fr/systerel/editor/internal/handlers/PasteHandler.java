/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.handlers;

import static org.eventb.internal.ui.utils.Messages.title_nothingToPaste;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IWorkbench;
import org.eventb.ui.ElementOperationFacade;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.actions.operations.RodinOperationUtils;
import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.editors.RodinEditorUtils;

/**
 * @author "Thomas Muller"
 */
public class PasteHandler extends AbstractEditionHandler {

	@Override
	public boolean isEnabled() {
		final RodinEditor rEditor = getActiveRodinEditor();
		return super.isEnabled()
				|| (rEditor != null && rEditor.isOverlayActive());
	}

	@Override
	protected String handleSelection(RodinEditor editor, int offset) {
		if (editor.isOverlayActive()) {
			final IAction action = editor.getOverlayEditorAction(ST.PASTE);
			if (action != null) {
				action.run();
				return "Text pasted";
			}
			return "Text paste failed";
		}
		final Interval inter = editor.getDocumentMapper()
				.findFirstElementIntervalAfter(offset);
		if (inter == null)
			return "No element found to perform paste.";
		final ILElement element = inter.getElement();
		if (element == null) {
			return "No element found to paste clipboard.";
		}
		final Clipboard clipboard = getClipBoard();
		// Try to handle by using a rodin handle transfer.
		final Transfer rodinHandleTransfer = ElementOperationFacade
				.getRodinHandleTransfer();
		final IRodinElement[] elements = (IRodinElement[]) clipboard
				.getContents(rodinHandleTransfer);

		// There is no data in the clipboard for rodin handle transfer then do
		// nothing.
		if (elements == null)
			return "Nothing to paste";
  
		// Check for the existing of the elements to be pasted.
		for (IRodinElement e : elements) {
			if (!e.exists()) {
				RodinEditorUtils.showError(title_nothingToPaste, "The element "
						+ element + "does not exist");
			}
		}
		final IRodinElement target = element.getElement();
		if (!(target instanceof IInternalElement) || !target.exists())
			return "Target does not exist";
		RodinOperationUtils.copyElements((IInternalElement) target, elements);
		return "Elements pasted";
	}

	@Override
	protected boolean checkEnablement(RodinEditor editor, int caretOffset) {
		if (editor.isOverlayActive())
			return true;
		// Create the clipboard associated with the workbench.
		final IWorkbench workbench = EventBUIPlugin.getDefault().getWorkbench();
		final Clipboard clipboard = new Clipboard(workbench.getDisplay());

		// Try to handle by using a rodin handle transfer.
		final Transfer rodinHandleTransfer = ElementOperationFacade
				.getRodinHandleTransfer();
		final IRodinElement[] elements = (IRodinElement[]) clipboard
				.getContents(rodinHandleTransfer);
		// enable only if there are elements to paste
		return elements != null && elements.length > 0;
	}

}
