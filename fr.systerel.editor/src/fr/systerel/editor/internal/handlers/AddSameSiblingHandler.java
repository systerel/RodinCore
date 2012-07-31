/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.handlers.context.ChildCreationInfo;

/**
 * Handler to add siblings.
 */
public class AddSameSiblingHandler extends AbstractAddElementHandler {

	private static final String ERROR = "An error occured. No sibling was created.";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			final RodinEditor editor = getActiveRodinEditor(event);
			if (editor == null)
				return ERROR;
			final int offset = editor.getCurrentOffset();
			final Interval after = editor.getDocumentMapper()
					.findEditableKindOfIntervalAfter(offset);
			if (after == null)
				return ERROR;
			final ILElement elemAfter = after.getElement();
			if (elemAfter == null)
				return ERROR;
			final IInternalElementType<?> elementType = elemAfter
					.getElementType();
			createElementAndRefresh(editor,
					getCreationPossibility(editor, offset), elementType);
			return "Sibling created.";
		} catch (IllegalArgumentException e) {
			return ERROR;
		}
	}

	@Override
	protected ChildCreationInfo getCreationPossibility(RodinEditor editor,
			int caretOffset) {
		return editor.getDocumentMapper().getSiblingCreationPossibility(
				caretOffset);
	};

}
