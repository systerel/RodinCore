/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.handlers;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IWorkbench;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.manipulation.ElementManipulationFacade;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.actions.operations.RodinOperationUtils;
import fr.systerel.editor.internal.documentModel.EditorElement;
import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.editors.RodinEditorUtils;

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
		final Transfer rodinHandleTransfer = ElementManipulationFacade
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
				RodinEditorUtils.showError("Nothing to paste", "The element "
						+ element + "does not exist");
			}
		}
		final IRodinElement target = element.getElement();
		if (!(target instanceof IInternalElement) || !target.exists())
			return "Target does not exist";
		RodinOperationUtils.pasteElements((IInternalElement) target, elements);
		return "Elements pasted";
	}

	@Override
	protected boolean isEnabled(RodinEditor editor, int caretOffset) {
		return isPastePossible(editor, caretOffset);
	}

	public static boolean isPastePossible(RodinEditor editor, int caretOffset) {
		final EditorElement target = editor.getDocumentMapper()
				.findEditorElementAt(caretOffset);
		if (target == null) {
			return false;
		}
		if (RodinOperationUtils.isReadOnly(target.getLightElement())) {
			return false;
		}
		// Create the clipboard associated with the workbench.
		final IWorkbench workbench = EventBUIPlugin.getDefault().getWorkbench();
		final Clipboard clipboard = new Clipboard(workbench.getDisplay());

		// Try to handle by using a rodin handle transfer.
		final Transfer rodinHandleTransfer = ElementManipulationFacade
				.getRodinHandleTransfer();
		final IRodinElement[] elements = (IRodinElement[]) clipboard
				.getContents(rodinHandleTransfer);
		// enable only if there are elements to paste
		return elements != null && elements.length > 0;
	}

}
