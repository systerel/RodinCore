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

import org.rodinp.core.IInternalElement;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.documentModel.DocumentMapper;
import fr.systerel.editor.documentModel.EditorElement;
import fr.systerel.editor.documentModel.Interval;
import fr.systerel.editor.editors.RodinEditor;
import fr.systerel.editor.operations.AtomicOperation;
import fr.systerel.editor.operations.History;
import fr.systerel.editor.operations.OperationFactory;

/**
 * Handler to add siblings.
 */
public class AddSiblingHandler extends AbstractEditorHandler {

	private boolean foundBefore = false;
	
	@Override
	protected void handleSelection(RodinEditor editor, int offset) {
		foundBefore = false;
		final DocumentMapper mapper = editor.getDocumentMapper();
		EditorElement item = mapper
				.findItemContaining(offset);
		final ILElement element;
		if (item == null) {
			final Interval inter = mapper.findEditableIntervalBefore(offset);
			element = inter.getElement();
			foundBefore = true;
		} else {
			element = item.getLightElement();
		}
		if (element == null) {
			return;
		}
		final ILElement parent = element.getParent();
		if (parent == null)
			return;
		final IInternalElement localParent = parent.getElement();
		final IInternalElement sibling;
		if (element.isImplicit() || foundBefore) {				
			sibling = null;
		} else {
			sibling = element.getElement();
		}
		final AtomicOperation op = OperationFactory.createElementGeneric(
				localParent, element.getElementType(), sibling);
		History.getInstance().addOperation(op);
		editor.resync(null);
	}

}
