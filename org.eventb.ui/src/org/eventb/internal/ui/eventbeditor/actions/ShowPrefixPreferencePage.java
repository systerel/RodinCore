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
package org.eventb.internal.ui.eventbeditor.actions;

import static org.eventb.internal.ui.preferences.PreferenceConstants.PREFIX_PREFERENCE_PAGE_ID;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eventb.internal.ui.preferences.PreferenceConstants;

/**
 * Action to show the prefix settings preference page
 */
public class ShowPrefixPreferencePage implements IEditorActionDelegate {

	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		// nothing to do
	}

	@Override
	public void run(IAction action) {
		final String pageId = PreferenceConstants.PREFIX_PREFERENCE_PAGE_ID;
		final String[] displayedIds = new String[] { PREFIX_PREFERENCE_PAGE_ID };
		final Dialog dialog = PreferencesUtil.createPreferenceDialogOn(null,
				pageId, displayedIds, null);
		dialog.open();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		return; // Do nothing
	}

}
