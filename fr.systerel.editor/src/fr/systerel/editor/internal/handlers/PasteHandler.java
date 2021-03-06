/*******************************************************************************
 * Copyright (c) 2011, 2020 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     University of Southampton - Remove the usage of deprecated methods
 *******************************************************************************/
package fr.systerel.editor.internal.handlers;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.IEventBRoot;
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
		final ILElement ilTarget = getLocalParent(element);
		final IInternalElement target = ilTarget.getElement();
		if (!target.exists())
			return "Target does not exist";
		RodinOperationUtils.pasteElements(target, elements);
		return "Elements pasted";
	}
	
	/**
	 * Gets the nearest parent in the hierarchy which is not implicit. At least
	 * returns the component root which is necessarily local.
	 */
	private ILElement getLocalParent(ILElement element) {
		final ILElement parent = element.getParent();
		if (parent != null && element.isImplicit()) {
			return getLocalParent(parent);
		}
		return element;
	}

	@Override
	public boolean isEnabled() {
		return isPastePossible();
	}

	public static boolean isPastePossible() {
		final RodinEditor editor = getActiveRodinEditor();
		if (editor == null)
			return false;
		final IEventBRoot inputRoot = editor.getInputRoot();
		if (inputRoot == null || !inputRoot.exists())
			return false;
		final EditorElement target = editor.getDocumentMapper()
				.findEditorElementAt(editor.getCurrentOffset());
		if (target == null) {
			return false;
		}
		if (RodinOperationUtils.isReadOnly(target.getLightElement())) {
			return false;
		}
		// Create the clipboard associated with the workbench.
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Clipboard clipboard = new Clipboard(workbench.getDisplay());

		// Try to handle by using a rodin handle transfer.
		final Transfer rodinHandleTransfer = ElementManipulationFacade
				.getRodinHandleTransfer();
		try {
			final IRodinElement[] elements = (IRodinElement[]) clipboard
					.getContents(rodinHandleTransfer);
		// enable only if there are elements to paste
		return elements != null && elements.length > 0;
		} catch (Exception e) {
			// something wrong appened when retrieving the contents of the
			// clipboard
			return false;
		}
	}

}
