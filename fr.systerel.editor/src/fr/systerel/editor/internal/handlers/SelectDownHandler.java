/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.handlers;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.ST;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.editors.EditPos;
import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * @author Nicolas Beauger
 *
 */
public class SelectDownHandler extends AbstractSelectHandler {

	@Override
	protected ILElement getSibling(RodinEditor rEditor, ILElement element) {
		final DocumentMapper mapper = rEditor.getDocumentMapper();
		final EditPos pos = mapper.getItemPosition(element);
		final int end = pos.getEnd();
		final Interval intervalAfter = mapper.findEditableIntervalAfter(end);
		if (intervalAfter == null) {
			return null;
		}
		ILElement next = intervalAfter.getElement();
		Assert.isNotNull(next);
		return next;
	}

	@Override
	protected void handleOverlayAction(RodinEditor editor) {
		final IAction action = editor.getOverlayEditorAction(ST.SELECT_LINE_DOWN);
		if (action != null) {
			action.run();
		}
		
	}
	
}
