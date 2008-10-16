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

import org.eclipse.jface.action.Action;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;

public abstract class HistoryAction extends Action {

	protected final IInternalElement root;
	protected final History history;

	public HistoryAction(IEventBEditor<?> editor) {
		super();
		this.root = editor.getRodinInput();
		history = History.getInstance();
	}
	
	@Override
	public abstract void run();

}
