/*******************************************************************************
 * Copyright (c) 2008 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.preferences;

import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author htson
 *         <p>
 *         An extension of {@link EventBEditorPreferencePage} to contribute a
 *         preference page for Context editor.
 */
public class ContextEditorPreferencePage extends EventBEditorPreferencePage
		implements IWorkbenchPreferencePage {

	/**
	 * Constructor.
	 * <p>
	 * Calling the super constructor with values for Machine Editors preference.
	 * </p>
	 */
	public ContextEditorPreferencePage() {
		super(
				ContextEditorPagesPreference.getDefault(),
				"Event-B Editor for contexts settings (changing this only effect newly opened editors)",
				PreferenceConstants.P_CONTEXT_EDITOR_PAGE,
				"Context Editor &Pages");
	}

}