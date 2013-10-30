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
package fr.systerel.editor.internal.presentation.updaters;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.custom.StyledText;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.documentModel.EditorElement;
import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * Re-synchronizer which repositions the caret at the line where deletion
 * occurred if such line exists, and behave like its super class otherwise.
 *
 * @author Thomas Muller
 */
public class AfterDeletionResynchronizer extends BasicEditorResynchronizer {

	private final ILElement deleteElement;
	private int lineIndex = -1;

	public AfterDeletionResynchronizer(RodinEditor editor,
			IProgressMonitor monitor, ILElement deletedElement) {
		super(editor, monitor);
		this.deleteElement = deletedElement;
	}

	@Override
	protected void takeSnapshot() {
		super.takeSnapshot();
		final EditorElement deletedEditElem = editor.getDocumentMapper()
				.findEditorElement(deleteElement);
		if (deletedEditElem != null) {
			final int offset = deletedEditElem.getOffset();
			lineIndex = editor.getStyledText().getLineAtOffset(offset);
		}
	}

	@Override
	protected void repositionCaret(StyledText styledText) {
		if (lineIndex == -1) {
			super.repositionCaret(styledText);
		}
		final int newOffset = snapshot.getSafeLineOffset(lineIndex);
		if (newOffset != -1) {
			styledText.setCaretOffset(newOffset);
		}
	}

}
