/*******************************************************************************
 * Copyright (c) 2008, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences;

import org.eventb.internal.ui.utils.Messages;

/**
 * @author htson
 *         <p>
 *         An extension of {@link EventBEditorPreferencePage} to contribute a
 *         preference page for Context editor.
 */
public class ContextEditorPreferencePage extends EventBEditorPreferencePage {

	/**
	 * Constructor.
	 * <p>
	 * Calling the super constructor with values for Machine Editors preference.
	 * </p>
	 */
	public ContextEditorPreferencePage() {
		super(
				ContextEditorPagesPreference.getDefault(),
				Messages.preferencepage_contexteditor_description,
				PreferenceConstants.P_CONTEXT_EDITOR_PAGE,
				Messages.preferencepage_contexteditor_editorpagedescription);
	}

}