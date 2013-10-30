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

import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * <em>This class provides the default behaviour.</em><br />
 * Basic re-synchronizer which repositions the Rodin editor caret at the
 * recorded offset before re-synchronization if such position is legal, and at
 * the end of the text otherwise.
 *
 * @author Thomas Muller
 */
public class BasicEditorResynchronizer extends EditorResynchronizer {

	public BasicEditorResynchronizer(RodinEditor editor,
			IProgressMonitor monitor) {
		super(editor, monitor);
	}

	@Override
	protected void repositionCaret(StyledText styledText) {
		styledText.setCaretOffset(snapshot.getCaretOffset());
	}

}
