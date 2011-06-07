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
package fr.systerel.editor.editors;

import static org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants.EDITOR_UNDO_HISTORY_SIZE;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.IUndoManagerExtension;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.editors.text.EditorsUI;

import fr.systerel.editor.internal.operations.History;
import fr.systerel.editor.internal.operations.RodinFileUndoContext;

/**
 *
 */
public class RodinUndoManager implements IUndoManager, IUndoManagerExtension {

	private static final History HISTORY = History.getInstance();
	
	private IUndoContext context;
	private ITextViewer fTextViewer;

	public RodinUndoManager(RodinFileUndoContext context) {
		super();
		this.context = context;
		EditorsUI.getPreferenceStore().addPropertyChangeListener(
				new IPropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent event) {
						if (EDITOR_UNDO_HISTORY_SIZE.equals(event.getProperty())) {
							setMaximalUndoLevel(getUndoLevel());
						}
					}
				});
	}

	public static int getUndoLevel() {
		return EditorsUI.getPreferenceStore().getInt(EDITOR_UNDO_HISTORY_SIZE);
	}

	@Override
	public IUndoContext getUndoContext() {
		return context;
	}

	@Override
	public void connect(ITextViewer viewer) {
		fTextViewer = viewer;
	}

	@Override
	public void disconnect() {
		fTextViewer = null;
		reset();
	}

	@Override
	public void beginCompoundChange() {
		// Do nothing

	}

	@Override
	public void endCompoundChange() {
		// Do nothing
	}

	@Override
	public void reset() {
		if (context != null)
			HISTORY.dispose(context);
	}

	@Override
	public void setMaximalUndoLevel(int undoLevel) {
		if (isConnected()) {
			HISTORY.setLimit(Math.max(0, undoLevel));
		}
	}

	private boolean isConnected() {
		return fTextViewer != null;
	}

	@Override
	public boolean undoable() {
		return HISTORY.isUndo(getUndoContext());
	}

	@Override
	public boolean redoable() {
		return HISTORY.isRedo(getUndoContext());
	}

	@Override
	public void undo() {
		HISTORY.undo(getUndoContext());
	}

	@Override
	public void redo() {
		HISTORY.redo(getUndoContext());

	}

}
