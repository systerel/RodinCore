/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Color;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.ui.EventBUIPlugin;

/**
 * Provides Preferences retrieval facilities. It allows to hide calls to the
 * EventBUIPlugin methods by factorizing them inside this class.
 * 
 * @author Nicolas Beauger
 * 
 */
public class EventBPreferenceStore {

	private EventBPreferenceStore() {
		// Functional class: Private constructor
	}
	
	/**
	 * Gets the default PreferenceStore from EventBUIPlugin.
	 * 
	 * @return the PreferenceStore.
	 */
	public static IPreferenceStore getPreferenceStore() {
		return EventBUIPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * Gets the boolean preference associated to the given name.
	 * 
	 * @param name
	 *            the name of the desired preference.
	 * @return the boolean value of the desired preference.
	 */
	public static boolean getBooleanPreference(String name) {
		return getPreferenceStore().getBoolean(name);
	}
	
	/**
	 * Gets the Color preference associated to the given name.
	 * 
	 * @param name
	 *            the name of the desired preference.
	 * @return the Color value of the desired preference.
	 */
	public static Color getColorPreference(String name) {
		return EventBSharedColor.getColor(
				PreferenceConverter.getColor(getPreferenceStore(), name));
	}
}