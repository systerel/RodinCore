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

import org.eclipse.core.runtime.CoreException;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.documentModel.Interval;
import fr.systerel.editor.documentModel.RodinDocumentProvider;
import fr.systerel.editor.editors.RodinEditor;

/**
 * Handler to add siblings.
 */
public class AddSiblingHandler extends AbstractEditorHandler {

	@Override
	protected void handleSelection(RodinEditor editor, int offset) {
		final Interval inter = editor.getDocumentMapper()
				.findEditableIntervalAfter(offset);
		if (inter == null)
			return;
		final ILElement element = inter.getElement();
		if (element == null) {
			return;
		}
		try {
			final IInternalElement localParent = element.getParent()
					.getElement();
			ElementDescRegistry.getInstance().createElement(
					localParent.getRoot(), localParent,
					element.getElementType(), element.getElement());
			((RodinDocumentProvider) editor.getDocumentProvider())
					.doSynchronize(element.getRoot(), null);
		} catch (RodinDBException dbe) {
			dbe.printStackTrace();
		} catch (CoreException ce) {
			ce.printStackTrace();
		}
	}

}
