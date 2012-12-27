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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * Implementation of handlers for editable interval traversal.
 * 
 * @author "Thomas Muller"
 */
public class TraverseHandlers {

	public static class TraverseNextHandler extends AbstractEditorHandler {

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			final RodinEditor editor = getActiveRodinEditor(event);
			if (editor != null) {
				editor.getOverlayEditor().saveAndExit(false);
				editor.getSelectionController().goToNextEditRegion();
			}
			return null;
		}

	}

	public static class TraversePreviousHandler extends AbstractEditorHandler {

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			final RodinEditor editor = getActiveRodinEditor(event);
			if (editor != null) {
				editor.getOverlayEditor().saveAndExit(false);
				editor.getSelectionController().goToPreviousEditRegion();
			}
			return null;
		}

	}

}
