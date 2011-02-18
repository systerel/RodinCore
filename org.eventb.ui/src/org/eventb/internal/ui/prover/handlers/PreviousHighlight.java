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
package org.eventb.internal.ui.prover.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.ui.EventBUIPlugin;

/**
 * @author "Thomas Muller"
 *
 */
public class PreviousHighlight extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbenchPage page = EventBUIPlugin.getActivePage();
		if (page != null) {
			final IEditorPart activeEditor = page.getActiveEditor();
			if (activeEditor instanceof ProverUI) {
				final ProverUI pu = ((ProverUI) activeEditor);
				pu.traversePreviousHighlight();
			}
		}
		return null;
	}


}
