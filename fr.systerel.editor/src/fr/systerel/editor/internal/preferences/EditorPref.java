/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import fr.systerel.editor.EditorPlugin;

/**
 * Listens to Editor preference store changes and automatically updates
 * corresponding preferences.
 * <p>
 * The observed preference is P_BOOL_ATTR_EDITION.
 * </p>
 * 
 * @author Josselin Dolhen
 */
public class EditorPref implements IPropertyChangeListener {

	private static final EditorPref INSTANCE = new EditorPref();
	static {
		INSTANCE.init();
	}

	private boolean doubleClick;

	private EditorPref() {
		// singleton
	}

	public static EditorPref getInstance() {
		return INSTANCE;
	}

	private void init() {
		final IPreferenceStore preferenceStore = EditorPlugin.getDefault()
				.getPreferenceStore();
		final String editionMode = preferenceStore
				.getString(PreferencesConstants.P_BOOL_ATTR_EDITION);
		doubleClick = editionMode.equals(PreferencesConstants.DOUBLE_CLICK_VAL);
		preferenceStore.addPropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		final String property = event.getProperty();
		if (property.equals(PreferencesConstants.P_BOOL_ATTR_EDITION)) {
			doubleClick = event.getNewValue().equals(
					PreferencesConstants.DOUBLE_CLICK_VAL);
		}
	}

	/**
	 * Returns the current double click preference status.
	 * 
	 * @return <code>true</code> for double click edit, <code>false</code> for
	 *         simple click edit
	 */
	public boolean isDoubleClick() {
		return doubleClick;
	}
}
