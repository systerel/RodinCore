/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.swt.custom.StyledText;

import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * Handles insertion command (id="org.eventb.ui.edit.insert").
 * 
 * @author "Nicolas Beauger"
 * 
 */
public class InsertHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final String insertText = event
				.getParameter("org.eventb.ui.edit.insert.text");

		if (insertText == null) {
			return "Unable to retrieve the text to insert";
		}

		Object appContext = event.getApplicationContext();

		if (!(appContext instanceof EvaluationContext)) {
			return "Unexpected context for insertion: operation aborted";
		}

		final EvaluationContext context = (EvaluationContext) appContext;
		final Object activeEditor = context.getVariable("activeEditor");
		if (!(activeEditor instanceof RodinEditor)) {
			return "Can only handle Rodin Editor";
		}
		if (!((RodinEditor) activeEditor).isOverlayActive()) {
			return "Can only insert into overlay editor";
		}
		final Object activeFocusControl = context.getVariable("activeFocusControl");

		if (!(activeFocusControl instanceof StyledText)) {
			return "No active place for insertion";
		}
		doInsertion((StyledText) activeFocusControl, insertText);
		return null;
	}

	private static void doInsertion(StyledText textControl, String insertText) {
		textControl.insert(insertText);

		final int newCaretOffset = textControl.getCaretOffset()
				+ insertText.length();
		textControl.setCaretOffset(newCaretOffset);
	}

}
