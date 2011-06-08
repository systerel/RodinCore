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
package fr.systerel.editor.internal.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.Position;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.EditorElement;
import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.editors.SelectionController;

/**
 * @author Nicolas Beauger
 */
public class SelectUpHandler extends AbstractEditorHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final RodinEditor rEditor = getActiveRodinEditor(event);
		if (rEditor == null) {
			return null;
		}
		final SelectionController selController = rEditor
				.getSelectionController();
		final int offset = rEditor.getCurrentOffset();
		final ILElement element = selController.getSelectionAt(offset);
		final Position reveal;
		if (element == null) {
			// not selected => expand selection
			reveal = selController.toggleSelection(offset);
		} else {
			final ILElement previous = getPrevious(rEditor, element);
			if (previous == null) {
				return null;
			}
			if (selController.isSelected(previous)) {
				// deselect current
				reveal = selController.toggleSelection(element);
			} else {
				// select previous
				reveal = selController.toggleSelection(previous);
			}
		}
		if (reveal != null) {
			rEditor.reveal(reveal.offset, reveal.length);
		}
		return null;
	}

	// TODO move to mapper
	// get previous sibling (of any type, with same parent, in editor order)
	// or parent itself
	private ILElement getPrevious(RodinEditor rEditor, ILElement element) {
		final DocumentMapper mapper = rEditor.getDocumentMapper();
		final EditorElement editorElement = mapper.findEditorElement(element);
		final int start = editorElement.getOffset();
		final Interval intervalBefore = mapper.findEditableIntervalBefore(start);
		if (intervalBefore == null) {
			return null;
		}
		ILElement previous = intervalBefore.getElement();
		if (previous.equals(element.getParent())) {
			return previous;
		}
		while(!sameParent(previous, element)) {
			previous = previous.getParent();
		}
		Assert.isNotNull(previous);
		return previous;
	}

	private static boolean sameParent(ILElement elem1, ILElement elem2) {
		
		final ILElement parent1 = elem1.getParent();
		final ILElement parent2 = elem2.getParent();
		if (parent1 == null || parent2 == null) {
			return elem1.equals(elem2);
		}
		return parent1.equals(parent2);
	}

}
