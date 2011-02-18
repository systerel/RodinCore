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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.EventBInputDialog;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.internal.ui.prover.SearchHighlighter;
import org.eventb.ui.EventBUIPlugin;

/**
 * Handler used to open an input dialog to search a string pattern in
 * hypotheses and goal.
 * 
 * @author "Thomas Muller"
 */
public class SearchHighlight extends AbstractHandler {
	private static final String title = "Higlight a pattern";
	private static final String message = "Enter the pattern to higlight";
	private static final String initialValue = "";

	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbenchPage page = EventBUIPlugin.getActivePage();
		if (page != null) {
			final IEditorPart activeEditor = page.getActiveEditor();
			if (activeEditor instanceof ProverUI) {
				final ProverUI pu = ((ProverUI) activeEditor);
				highlight(pu);
			}
		}
		return null;
	}


	private void highlight(final ProverUI ui) {
		final Shell shell = ui.getSite().getShell();
		final IUserSupport userSupport = ui.getUserSupport();
		final SearchHighlighter highlighter = ui.getHighlighter();
		final EventBInputDialog dialog = new EventBInputDialog(shell,
				title, message, initialValue, null, userSupport);
		dialog.open();
		if (dialog.getReturnCode() == Dialog.OK) {
			highlighter.highlightPattern(dialog.getValue());
		}
	}
	
}
