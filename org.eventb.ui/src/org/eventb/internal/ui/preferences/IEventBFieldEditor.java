/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences;

import org.eventb.internal.ui.preferences.tactics.IEventBPreference;

public interface IEventBFieldEditor extends IEventBPreference {

	/**
	 * Set whether or not the controls in the field editor are enabled.
	 */
	public void setEnabled(boolean enabled);

	/**
	 * Initializes this field editor with the preference value from the
	 * preference store.
	 */
	public void load();

	/**
	 * Initializes this field editor with the default preference value from the
	 * preference store.
	 */
	public void loadDefault();

}
