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

import static fr.systerel.editor.internal.preferences.PreferencesConstants.DOUBLE_CLICK_DESC;
import static fr.systerel.editor.internal.preferences.PreferencesConstants.DOUBLE_CLICK_VAL;
import static fr.systerel.editor.internal.preferences.PreferencesConstants.SIMPLE_CLICK_DESC;
import static fr.systerel.editor.internal.preferences.PreferencesConstants.SIMPLE_CLICK_VAL;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import fr.systerel.editor.EditorPlugin;
import fr.systerel.editor.internal.utils.Messages;

/**
 * A preference page for the editor
 * <p>
 * The user can choose if edition of boolean attributes shall be possible by
 * simple clicks or double clicks.
 * </p>
 * @author Josselin Dolhen
 */
public class RodinEditorPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public RodinEditorPreferencePage() {
		super(GRID);
		setPreferenceStore(EditorPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		addField(new RadioGroupFieldEditor(
				PreferencesConstants.P_BOOL_ATTR_EDITION,
				Messages.preferencepage_editionMode,
				2,
				new String[][] {
						{ SIMPLE_CLICK_DESC, SIMPLE_CLICK_VAL },
						{ DOUBLE_CLICK_DESC, DOUBLE_CLICK_VAL } }, //
				getFieldEditorParent(), true));
	}

	@Override
	public void init(IWorkbench workbench) {
		// Do nothing.
	}

}
