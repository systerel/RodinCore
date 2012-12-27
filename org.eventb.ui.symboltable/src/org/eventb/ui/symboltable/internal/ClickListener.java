/*******************************************************************************
 * Copyright (c) 2009, 2012 Universitaet Duesseldorf and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Universitaet Duesseldorf - initial API and implementation
 *     Systerel - used Eclipse Command Framework
 *******************************************************************************/
package org.eventb.ui.symboltable.internal;

import java.util.HashMap;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eventb.ui.symboltable.ISymbolTableConstants;

public class ClickListener implements MouseListener {

	
	private IEditorPart editor;
	private final SymbolViewPart symbolViewPart;

	public ClickListener(final SymbolViewPart symbolViewPart) {
		this.symbolViewPart = symbolViewPart;
	}

	public void setEditor(final IEditorPart editor) {
		if (editor != null)
			this.editor = editor;
	}

	public void mouseUp(final MouseEvent event) {
		boolean success = false;
		
		if (isEditorOk()) {
			final Object source = event.getSource();

			if (source instanceof Label) {
				final Object data = ((Label) source).getData();

				if (data instanceof Symbol) {
					
					final String insertText = ((Symbol) data).text;
					final ParameterizedCommand command = makeInsertCommand(insertText);
					
					if (command != null) {
						final IHandlerService handlerService = (IHandlerService) editor
								.getSite().getService(IHandlerService.class);
						try {
							handlerService.executeCommand(
									command, null);
							// set focus back to editor
							editor.getSite().getPage().activate(editor);
							success = true;
						} catch (NotHandledException e) {
							// user must select a place for insertion:
							// error reported below
						} catch (Exception e) {
							log(e, "While executing command "+ command.getId()); //$NON-NLS-1$
						}
					}
				}
			}
		}

		if (!success) {
			final String CANNOT_INSERT_SYMBOL = Messages.errorReport_cannotInsertSymbol;
			symbolViewPart.reportError(CANNOT_INSERT_SYMBOL);
		}
	}

	private ParameterizedCommand makeInsertCommand(final String insertText) {
		final ICommandService commandService = (ICommandService) editor
				.getSite().getService(ICommandService.class);

		final Command command = commandService
				.getCommand("org.eventb.ui.edit.insert"); //$NON-NLS-1$
		final HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("org.eventb.ui.edit.insert.text", insertText); //$NON-NLS-1$
		
		return ParameterizedCommand.generateCommand(command, parameters);
	}

	private boolean isEditorOk() {
		if (editor == null) {
			return false;
		}

		final IEditorPart activeEditor = symbolViewPart.getSite().getPage()
				.getActiveEditor();
		return editor == activeEditor;
	}

	public void mouseDoubleClick(final MouseEvent e) {
		// IGNORE
	}

	public void mouseDown(final MouseEvent e) {
		// IGNORE
	}

	// Logs the given exception with the given context message.
	private static void log(Exception e, String message) {
		final IStatus status = new Status(IStatus.ERROR,
				ISymbolTableConstants.PLUGIN_ID, IStatus.ERROR, message, e);
		SymbolTablePlugin.getDefault().getLog().log(status);
	}
}
