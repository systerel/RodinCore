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

import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * A simple handler to refresh the active Rodin editor.
 * 
 * @author "Thomas Muller"
 */
public class RefreshHandler extends AbstractEditionHandler {

	@Override
	protected String handleSelection(RodinEditor editor, int offset) {
		editor.abordEdition();
		editor.resync(null, true);
		return null;
	}

}
