/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBPreferenceStore
 *     Systerel - removed direct access to 'consider hidden hyps' preference
 *******************************************************************************/
package org.eventb.internal.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eventb.internal.ui.utils.Messages;

/**
 * @author htson
 *         <p>
 *         The main preference page for the prover user interface. This is done by
 *         subclassing {@link FieldEditorPreferencePage}, we can use the field
 *         support built into JFace that allows us to create a page that is
 *         small and knows how to save, restore and apply itself.
 *         <p>
 *         This page is used to modify preferences only. They are stored in the
 *         preference store that belongs to the main plug-in class. That way,
 *         preferences can be accessed directly via the preference store.
 *         <p>
 *         At the moment, this page is empty.
 */
public class ProverUIPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private BooleanFieldEditor considerHiddenHypsEditor;

	/**
	 * Constructor.
	 */
	public ProverUIPreferencePage() {
		super(GRID);
		setPreferenceStore(EventBPreferenceStore.getPreferenceStore());
		setDescription(Messages.preferencepage_provingui_description);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	public void createFieldEditors() {
		considerHiddenHypsEditor = new BooleanFieldEditor(PreferenceConstants.P_CONSIDER_HIDDEN_HYPOTHESES,
				Messages.preferencepage_provingui_considerHiddenHypotheses,
				getFieldEditorParent());
		addField(considerHiddenHypsEditor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		// Do nothing.
	}
	
}