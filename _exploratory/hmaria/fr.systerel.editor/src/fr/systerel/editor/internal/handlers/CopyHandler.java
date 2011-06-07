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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IWorkbench;
import org.eventb.internal.ui.RodinHandleTransfer;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.editors.RodinEditor;

/**
 * @author "Thomas Muller"
 */
public class CopyHandler extends AbstractEditorHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final RodinEditor editor = getActiveRodinEditor();
		if (editor == null) {
			return "The current editor is not the RodinEditor";
		}
		final ILElement[] selected = editor.getSelectionController()
				.getSelectedElements();
		final List<IRodinElement> ems = new ArrayList<IRodinElement>();
		for (ILElement el : selected) {
			final IInternalElement element = el.getElement();
			if (element != null)
				ems.add(element);
		}
		Collection<IRodinElement> elements = new ArrayList<IRodinElement>();
		// Collect the list of Rodin Elements to be copied.
		// If an ancestor of an element is already selected, the element will
		// not be added.
		for (IRodinElement element : ems) {
			elements = UIUtils.addToTreeSet(elements, element);
		}
		// Get the clipboard for the current workbench display.
		final IWorkbench workbench = EventBUIPlugin.getDefault().getWorkbench();
		final Clipboard clipboard = new Clipboard(workbench.getDisplay());

		// Copies internal element
		// Copies as Rodin Handle & Text transfer
		StringBuffer buf = new StringBuffer();
		int i = 0;
		for (IRodinElement element : elements) {
			if (i > 0)
				buf.append("\n");
			buf.append(element);
			i++;
		}
		clipboard.setContents(
				new Object[] {
						elements.toArray(new IRodinElement[elements.size()]),
						buf.toString() },
				new Transfer[] { RodinHandleTransfer.getInstance(),
						TextTransfer.getInstance() });

		return "Copy Rodin element successfully";
	}

	@Override
	protected boolean checkEnablement(RodinEditor editor, int caretOffset) {
		return editor.getSelectionController().getSelectedElements().length > 0;
	}

}