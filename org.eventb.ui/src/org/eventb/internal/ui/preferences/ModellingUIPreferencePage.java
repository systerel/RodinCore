/*******************************************************************************
 * Copyright (c) 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBUIPlugin;

/**
 * @author htson
 *         <p>
 *         The main preference page for the modelling user interface. This is done by
 *         subclassing {@link FieldEditorPreferencePage}, we can use the field
 *         support built into JFace that allows us to create a page that is
 *         small and knows how to save, restore and apply itself.
 *         <p>
 *         This page is used to modify preferences only. They are stored in the
 *         preference store that belongs to the main plug-in class. That way,
 *         preferences can be accessed directly via the preference store.
 *         <p>
 *         At the moment, this page is empty. This acts as a place holder for
 *         sub-preferences such as preferences for machine and context editors.
 */
public class ModellingUIPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	/**
	 * Constructor.
	 */
	public ModellingUIPreferencePage() {
		super(GRID);
		setPreferenceStore(EventBUIPlugin.getDefault().getPreferenceStore());
		setDescription(Messages.preferencepage_modellingui_description);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	public void createFieldEditors() {
		// Do nothing at the moment
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		// Do nothing.
	}
	
}