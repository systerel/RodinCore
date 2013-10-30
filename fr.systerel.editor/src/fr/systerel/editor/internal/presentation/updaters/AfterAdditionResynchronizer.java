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

import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.EditorElement;
import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * Re-synchronizer which repositions the caret at the first offset of the first
 * editable interval where addition occurred if such offset exists, and behave
 * like its super class otherwise.
 *
 * @author Thomas Muller
 */
public class AfterAdditionResynchronizer extends BasicEditorResynchronizer {

	private final ILElement elementAdded;

	public AfterAdditionResynchronizer(RodinEditor editor,
			IProgressMonitor monitor, ILElement elementAdded) {
		super(editor, monitor);
		this.elementAdded = elementAdded;
	}
	
	@Override
	protected void repositionCaret(StyledText styledText) {
		final DocumentMapper mapper = editor.getDocumentMapper();
		final EditorElement edElem = mapper.findEditorElement(elementAdded);
		if (edElem == null) {
			super.repositionCaret(styledText);
		}
		final int elemFirstOffset = edElem.getOffset();
		final Interval inter = mapper
				.findEditableIntervalAfter(elemFirstOffset);
		styledText.setCaretOffset(inter.getOffset());
	}

}
