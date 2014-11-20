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
package fr.systerel.editor.internal.sourceprovider;

import static org.eclipse.ui.ISources.ACTIVE_EDITOR;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;

/**
 * Class providing a variable which denotes the current edition mode and can be
 * used within command enablement conditions.
 * 
 * @author Thomas Muller
 */
public class EditionModeProvider extends AbstractSourceProvider {

	public static final String EDITION_MODE = "fr.systerel.editor.sourceprovider.editionMode";

	// structural mode is the default value
	private EditionMode currentMode = EditionMode.STRUCTURAL;

	public EditionModeProvider() {
		// Nothing to do
	}

	@Override
	public void dispose() {
		// Nothing to do
	}

	public void setCurrentMode(EditionMode newMode) {
		if (newMode != currentMode) {
			currentMode = newMode;
			fireSourceChanged(ACTIVE_EDITOR, EDITION_MODE, currentMode.getId());
		}
	}

	@Override
	public Map<String, String> getCurrentState() {
		final Map<String, String> map = new HashMap<String, String>();
		map.put(EDITION_MODE, currentMode.getId());
		return map;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { EDITION_MODE };
	}

	public static enum EditionMode {

		STRUCTURAL("structural"), //
		OVERLAY("overlay"), //
		;

		private String id;

		private EditionMode(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

	}

}
