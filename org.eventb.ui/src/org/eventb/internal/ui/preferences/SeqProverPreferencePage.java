/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBPreferenceStore
 ******************************************************************************/
package org.eventb.internal.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eventb.internal.ui.utils.Messages;

/**
 * @author htson
 *         <p>
 *         The main preference page for the sequent prover. This is done by
 *         subclassing {@link FieldEditorPreferencePage}, we can use the field
 *         support built into JFace that allows us to create a page that is
 *         small and knows how to save, restore and apply itself.
 *         <p>
 *         This page is used to modify preferences only. They are stored in the
 *         preference store that belongs to the main plug-in class. That way,
 *         preferences can be accessed directly via the preference store.
 *         <p>
 *         At the moment, this page is empty. This acts as a place holder for
 *         sub-preferences such as POM-Tactics preference and Post-Tactics
 *         preference.
 */
public class SeqProverPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	/**
	 * Constructor.
	 */
	public SeqProverPreferencePage() {
		super();
		setPreferenceStore(EventBPreferenceStore.getPreferenceStore());
		setDescription(Messages.preferencepage_seqprover_description);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		// Do nothing.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		// Do nothing at the moment.
	}

}