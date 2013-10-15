/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.editors;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

/**
 * Updates editor actions according to edition mode and selection.
 */
class EditorActionUpdater implements FocusListener, SelectionListener {
	
	protected static final String[] ACTIONS= new String[] {
		// navigation
		ITextEditorActionDefinitionIds.LINE_UP,
		ITextEditorActionDefinitionIds.LINE_DOWN,
		ITextEditorActionDefinitionIds.LINE_START,
		ITextEditorActionDefinitionIds.LINE_END,
		ITextEditorActionDefinitionIds.COLUMN_PREVIOUS,
		ITextEditorActionDefinitionIds.COLUMN_NEXT,
		ITextEditorActionDefinitionIds.PAGE_UP,
		ITextEditorActionDefinitionIds.PAGE_DOWN,
		ITextEditorActionDefinitionIds.WORD_PREVIOUS,
		ITextEditorActionDefinitionIds.WORD_NEXT,
		ITextEditorActionDefinitionIds.TEXT_START,
		ITextEditorActionDefinitionIds.TEXT_END,
		ITextEditorActionDefinitionIds.WINDOW_START,
		ITextEditorActionDefinitionIds.WINDOW_END,
		// selection
		ITextEditorActionDefinitionIds.SELECT_LINE_UP,
		ITextEditorActionDefinitionIds.SELECT_LINE_DOWN,
		ITextEditorActionDefinitionIds.SELECT_LINE_START,
		ITextEditorActionDefinitionIds.SELECT_LINE_END,
		ITextEditorActionDefinitionIds.SELECT_COLUMN_PREVIOUS,
		ITextEditorActionDefinitionIds.SELECT_COLUMN_NEXT,
		ITextEditorActionDefinitionIds.SELECT_PAGE_UP,
		ITextEditorActionDefinitionIds.SELECT_PAGE_DOWN,
		ITextEditorActionDefinitionIds.SELECT_WORD_PREVIOUS,
		ITextEditorActionDefinitionIds.SELECT_WORD_NEXT,
		ITextEditorActionDefinitionIds.SELECT_TEXT_START,
		ITextEditorActionDefinitionIds.SELECT_TEXT_END,
		ITextEditorActionDefinitionIds.SELECT_WINDOW_START,
		ITextEditorActionDefinitionIds.SELECT_WINDOW_END,
		ITextEditorActionDefinitionIds.DELETE_PREVIOUS,
		ITextEditorActionDefinitionIds.DELETE_NEXT,
		ITextEditorActionDefinitionIds.DELETE_PREVIOUS_WORD,
		ITextEditorActionDefinitionIds.DELETE_NEXT_WORD,
		// miscellaneous
		ITextEditorActionDefinitionIds.TOGGLE_OVERWRITE,
		// text editor related
		ITextEditorActionDefinitionIds.SCROLL_LINE_DOWN,
		ITextEditorActionDefinitionIds.SCROLL_LINE_UP,
	};

	/**
	 * The parent Rodin Editor
	 */
	private final RodinEditor editor;
	private final Map<String, IAction> editorActions;

	public EditorActionUpdater(RodinEditor parent) {
		this.editor = parent;
		this.editorActions = new HashMap<String, IAction>();
	}

	/**
	 * Deactivates actions registered on the source viewer (which is read-only).
	 * When the overlay becomes enabled, these actions are disabled because they
	 * would operate on the main source viewer which is unwanted.
	 */
	@Override
	public void focusGained(FocusEvent e) {
		for (String actionId : ACTIONS) {
			final IAction action = editor.getAction(actionId);
			if (action != null) {
				editorActions.put(actionId, action);
			}
			editor.setAction(actionId, null);
		}
		editor.updateSelectionDependentActions();
	}

	@Override
	public void focusLost(FocusEvent e) {
		for (Entry<String, IAction> actionEntry : editorActions.entrySet()) {
			editor.setAction(actionEntry.getKey(), actionEntry.getValue());
		}
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		widgetDefaultSelected(e);
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		editor.updateSelectionDependentActions();
	}
}