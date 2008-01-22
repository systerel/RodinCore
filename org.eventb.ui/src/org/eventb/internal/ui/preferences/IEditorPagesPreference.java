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

import org.eventb.ui.eventbeditor.EventBEditorPage;

/**
 * @author htson
 *         <p>
 *         The common interface for preferences for editor pages.
 */
public interface IEditorPagesPreference {
	
	/**
	 * Creates and returns all the editor pages.
	 * 
	 * @return an array of newly created editor pages. This must not be
	 *         <code>null</code>
	 */
	public EventBEditorPage[] createPages();

	/**
	 * Gets the editor ID corresponding to this preference.
	 * 
	 * @return the editor ID corresponding to the preference.
	 */
	public String getEditorID();

    /**
	 * Sets the default value for the preference.
	 */
	public void setDefault();

}
