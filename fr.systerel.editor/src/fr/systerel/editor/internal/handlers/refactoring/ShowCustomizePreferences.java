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
package fr.systerel.editor.internal.handlers.refactoring;


import static fr.systerel.editor.internal.preferences.PreferencesConstants.PREFIX_PREFERENCE_PAGE_ID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class ShowCustomizePreferences extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final String pageId = PREFIX_PREFERENCE_PAGE_ID;
		final String[] displayedIds = new String[] { PREFIX_PREFERENCE_PAGE_ID };
		final Dialog dialog = PreferencesUtil.createPreferenceDialogOn(null,
				pageId, displayedIds, null);
		dialog.open();
		return null;
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}

}
