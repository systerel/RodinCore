package fr.systerel.editor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.EditorPlugin;
import fr.systerel.editor.documentModel.Interval;
import fr.systerel.editor.documentModel.RodinDocumentProvider;
import fr.systerel.editor.editors.RodinEditor;

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

/**
 * Handler to add siblings.
 */
public class AddSiblingHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IEditorPart activeEditor = EditorPlugin.getActivePage()
				.getActiveEditor();
		if (!(activeEditor instanceof RodinEditor)) {
			return null;
		}
		final RodinEditor rEditor = (RodinEditor) activeEditor;
		final ISelection currentSelection = HandlerUtil
				.getActiveMenuSelection(event);
		if (currentSelection instanceof TextSelection) {
			final int offset = ((TextSelection) currentSelection).getOffset();
			final Interval inter = rEditor.getDocumentMapper()
					.findEditableIntervalAfter(offset);
			if (inter == null)
				return null;
			final ILElement element = inter.getElement();
			if (element == null) {
				return null;
			}
			try {
				final IInternalElement localParent = element.getParent().getElement();
				ElementDescRegistry.getInstance().createElement(
						localParent.getRoot(), localParent, element.getElementType(),
						element.getElement());
				((RodinDocumentProvider) rEditor.getDocumentProvider())
						.doSynchronize(element.getRoot(), null);
			} catch (RodinDBException e1) {
				e1.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
