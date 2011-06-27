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

import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * Unselects the selection if any, and abort the edition within the overlay
 * editor.
 * 
 * @author "Thomas Muller"
 */
public class AbortHandler extends AbstractEditionHandler {

	@Override
	protected String handleSelection(RodinEditor editor, int offset) {
		editor.getSelectionController().clearSelection();
		if (editor.isOverlayActive()) {
			editor.abordEdition();
		}
		return null;
	}

}
