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
package fr.systerel.editor.internal.editors;

import org.eclipse.ui.texteditor.IElementStateListener;

import fr.systerel.editor.internal.documentModel.RodinDocumentProvider;

/**
 * A listener for editor input content changes.
 */
public class EditorElementStateListener implements IElementStateListener {

	private final RodinDocumentProvider provider;
	private final RodinEditor editor;

	public EditorElementStateListener(RodinEditor editor,
			RodinDocumentProvider provider) {
		this.provider = provider;
		this.editor = editor;
	}

	public static IElementStateListener getNewListener(RodinEditor editor,
			RodinDocumentProvider provider) {
		return new EditorElementStateListener(editor, provider);
	}

	public void elementContentAboutToBeReplaced(Object element) {
		// do nothing
	}

	public void elementContentReplaced(Object element) {
		provider.setCanSaveDocument(provider.getEditorInput());
		editor.updateFoldingStructure();
	}

	public void elementDeleted(Object element) {
		// do nothing
	}

	public void elementDirtyStateChanged(Object element, boolean isDirty) {
		// do nothing
	}

	public void elementMoved(Object originalElement, Object movedElement) {
		// do nothing
	}

}