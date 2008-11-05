/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.ui.IWorkbenchWindow;
import org.eventb.internal.ui.eventbeditor.operations.History;

public class RedoAction extends HistoryAction {

	public RedoAction(IWorkbenchWindow workbenchWindow) {
		super(workbenchWindow);
	}

	@Override
	public void doRun(IUndoContext context) {
		History.getInstance().redo(context);
	}

}
