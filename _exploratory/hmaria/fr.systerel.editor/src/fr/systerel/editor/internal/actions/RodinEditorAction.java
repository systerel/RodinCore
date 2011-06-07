/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/

package fr.systerel.editor.internal.actions;

import org.eclipse.jface.action.Action;

import fr.systerel.editor.internal.editors.RodinEditor;

/**
 *
 */
public class RodinEditorAction extends Action {

	protected RodinEditor editor;

	public RodinEditorAction(RodinEditor editor) {
		super();
		this.editor = editor;
	}
	
	
}
