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
 * Implementation of handlers for editable interval traversal.
 * 
 * @author "Thomas Muller"
 */
public class TraverseHandlers {

	public static class TraverseNextHandler extends AbstractEditionHandler {

		@Override
		protected String handleSelection(RodinEditor editor, int offset) {
			editor.getSelectionController().goToNextEditRegion();
			return null;
		}

	}

	public static class TraversePreviousHandler extends AbstractEditionHandler {

		@Override
		protected String handleSelection(RodinEditor editor, int offset) {
			editor.getSelectionController().goToPreviousEditRegion();
			return null;
		}

	}

}
