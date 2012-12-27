/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eventb.internal.ui.utils.Messages;

/**
 * @author Nicolas Beauger
 */
public class ColorsAndFontsPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/**
	 * Constructor
	 */
	public ColorsAndFontsPreferencePage() {
		super(GRID);
		setPreferenceStore(EventBPreferenceStore.getPreferenceStore());
		setDescription(Messages.preferencepage_colorsandfonts_description);
	}


	@Override
	public void init(IWorkbench workbench) {
		// Do nothing
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.P_BORDER_ENABLE,
				Messages.preferencepage_colorsandfonts_showborders,
				getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_TEXT_FOREGROUND,
				Messages.preferencepage_colorsandfonts_textForeground,
				getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COMMENT_FOREGROUND,
				Messages.preferencepage_colorsandfonts_commentForeground,
				getFieldEditorParent()));
//		addField(new ColorFieldEditor(PreferenceConstants.P_REQUIRED_FIELD_BACKGROUND,
//				Messages.preferencepage_colorsandfonts_requiredfieldbackground,
//				getFieldEditorParent()));
//		addField(new ColorFieldEditor(PreferenceConstants.P_DIRTY_STATE_COLOR,
//				Messages.preferencepage_colorsandfonts_dirtystatecolor,
//				getFieldEditorParent()));
//		addField(new ColorFieldEditor(PreferenceConstants.P_BOX_BORDER_COLOR,
//				Messages.preferencepage_colorsandfonts_boxbordercolor,
//				getFieldEditorParent()));		
	}

}
