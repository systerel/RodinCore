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
package fr.systerel.editor.handlers;

import static fr.systerel.editor.operations.OperationUtils.copyElements;
import static fr.systerel.editor.operations.OperationUtils.showError;
import static org.eventb.internal.ui.utils.Messages.title_nothingToPaste;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.ui.IWorkbench;
import org.eventb.internal.ui.RodinHandleTransfer;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.editors.RodinEditor;
import fr.systerel.editor.internal.documentModel.Interval;

/**
 * @author "Thomas Muller"
 */
public class PasteHandler extends AbstractEditionHandler {

	@Override
	protected String handleSelection(RodinEditor editor, int offset) {

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
		final RodinHandleTransfer rodinHandleTransfer = RodinHandleTransfer
				.getInstance();
		final IRodinElement[] elements = (IRodinElement[]) clipboard
				.getContents(rodinHandleTransfer);

		// There is no data in the clipboard for rodin handle transfer then do
		// nothing.
		if (elements == null)
			return "Nothing to paste";

		// Check for the existing of the elements to be pasted.
		for (IRodinElement e : elements) {
			if (!e.exists()) {
				showError(title_nothingToPaste, "The element " + element
						+ "does not exist");
			}
		}
		final IRodinElement target = element.getElement();
		if (!(target instanceof IInternalElement) || !target.exists())
			return "Target does not exist";
		copyElements((IInternalElement) target, elements);
		editor.resync(null);
		return "Elements pasted";
	}

	@Override
	protected boolean checkEnablement(RodinEditor editor, int caretOffset) {
		// Create the clipboard associated with the workbench.
		final IWorkbench workbench = EventBUIPlugin.getDefault().getWorkbench();
		final Clipboard clipboard = new Clipboard(workbench.getDisplay());

		// Try to handle by using a rodin handle transfer.
		final RodinHandleTransfer rodinHandleTransfer = RodinHandleTransfer
				.getInstance();
		final IRodinElement[] elements = (IRodinElement[]) clipboard
				.getContents(rodinHandleTransfer);
		// enable only if there are elements to paste
		return elements != null && elements.length > 0;
	}

}
