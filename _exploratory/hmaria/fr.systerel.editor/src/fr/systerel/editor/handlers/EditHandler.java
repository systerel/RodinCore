/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/

package fr.systerel.editor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Event;

/**
 * A handler that invokes an edit action on a styled text.
 *
 */
public abstract class EditHandler extends AbstractHandler {

	protected int action;
	

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		if (arg0.getTrigger() instanceof Event) {
			if (((Event) arg0.getTrigger()).widget instanceof StyledText) {
				StyledText text = (StyledText) ((Event) arg0.getTrigger()).widget;
				text.invokeAction(action);
			}
		}
		return null;
	}

}
