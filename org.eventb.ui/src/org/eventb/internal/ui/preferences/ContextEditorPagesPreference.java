/*******************************************************************************
 * Copyright (c) 2008, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBPreferenceStore
 *******************************************************************************/
package org.eventb.internal.ui.preferences;

import org.eventb.internal.ui.eventbeditor.EventBContextEditor;

/**
 * @author htson
 *         <p>
 *         An extension of {@link EditorPagesPreference} to store the current
 *         preference for context editor pages. This class is implemented as a
 *         singleton class.
 */
public class ContextEditorPagesPreference extends EditorPagesPreference {

	// Static singleton element.
	private static IEditorPagesPreference instance;

	/**
	 * Constructor.
	 * <p>
	 * Private constructor for singleton.
	 */
	private ContextEditorPagesPreference() {
		// Register for the changes in the preference store.
		EventBPreferenceStore.getPreferenceStore()
				.addPropertyChangeListener(this);
	}

	/**
	 * Returns the singleton instance (initialise the instance if necessary).
	 * 
	 * @return the singleton instance of this class.
	 */
	public static IEditorPagesPreference getDefault() {
		if (instance == null) // Initialise the first time.
			instance = new ContextEditorPagesPreference();
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.preferences.IEditorPagesPreference#getEditorID()
	 */
	@Override
	public String getEditorID() {
		return EventBContextEditor.EDITOR_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.preferences.EditorPagesPreference#getPreferenceName()
	 */
	@Override
	protected String getPreferenceName() {
		return PreferenceConstants.P_CONTEXT_EDITOR_PAGE;
	}

}
