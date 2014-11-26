/*******************************************************************************
 * Copyright (c) 2009, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.handlers;

import static org.eclipse.ui.handlers.HandlerUtil.getVariableChecked;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Text;

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

		final Object activeFocusControl = getVariableChecked(event,
				"activeFocusControl");

		if (activeFocusControl instanceof StyledText) {
			doInsertion((StyledText) activeFocusControl, insertText);
		} else if (activeFocusControl instanceof Text) {
			doInsertion((Text) activeFocusControl, insertText);
		} else {
			throw new ExecutionException("No active place for insertion");
		}
		return null;
	}

	private static void doInsertion(StyledText textControl, String insertText) {
		textControl.insert(insertText);

		final int newCaretOffset = textControl.getCaretOffset()
				+ insertText.length();
		textControl.setCaretOffset(newCaretOffset);
	}

	private static void doInsertion(Text textControl, String insertText) {
		textControl.insert(insertText);
	}

}
