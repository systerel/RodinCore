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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.handlers.context.ChildCreationInfo;

public class AddChildHandler extends AbstractAddChildHandler {

	private static final String TYPE_ID = "typeID";
	private static final String ERROR = "An error occured. No child was created.";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			final String parameter = event.getParameter(TYPE_ID);
			if (parameter == null || parameter.isEmpty()) {
				return "No possible Child Creation";
			}
			final IInternalElementType<?> elementType = (IInternalElementType<?>) RodinCore
					.getElementType(parameter);
			final RodinEditor editor = getActiveRodinEditor(event);
			if (editor == null)
				return ERROR;
			final ChildCreationInfo possibility = editor.getDocumentMapper()
					.getChildCreationPossibility(editor.getCurrentOffset());
			createChildAndRefresh(editor, possibility, elementType);
			return "Child created.";
		} catch (IllegalArgumentException e) {
			return ERROR;
		}
	};

	@Override
	protected boolean checkEnablement(RodinEditor editor, int caretOffset) {
		final ChildCreationInfo possibility = editor.getDocumentMapper()
				.getChildCreationPossibility(caretOffset);
		if (possibility == null) {
			return false;
		}
		if (possibility.getPossibleChildTypes().isEmpty()) {
			return false;
		}
		return true;
	}

}
