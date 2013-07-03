/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.handlers.rename;

import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;
import static org.eventb.internal.ui.preferences.PreferenceConstants.PREFIX_PREFERENCE_PAGE_ID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;

/**
 * Handler class for "org.eventb.ui.editor.rename.customizePrefixes" command.
 */
public class CustomizePrefixesHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final String pageId = PREFIX_PREFERENCE_PAGE_ID;
		final String[] displayedIds = new String[] { PREFIX_PREFERENCE_PAGE_ID };
		final Dialog dialog = createPreferenceDialogOn(null, pageId,
				displayedIds, null);
		dialog.open();
		return null;
	}

}
