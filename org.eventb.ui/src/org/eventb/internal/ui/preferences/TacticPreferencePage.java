/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBPreferenceStore
 *     Systerel - removed direct access to tactic preferences
 ******************************************************************************/
package org.eventb.internal.ui.preferences;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.eventb.internal.ui.UIUtils;

/**
 * @author htson
 *         <p>
 *         An abstract class for implementing preference page for auto-tactics,
 *         e.g. Post-tactics or POM-tactics. Through this preference, the users
 *         can customize the following fields:
 *         <ul>
 *         <li>The enablement field: Enable/Disable the specific auto-tactics.
 *         <li>List of tactics to be selected to be run as auto-tactics.
 *         <ul>
 */
public abstract class TacticPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	// The two-list editor for choosing tactics from the set of available tactics.
	TwoListSelectionEditor tacticsEditor;
	
	// The enablement field.
	BooleanFieldEditor enablementEditor;

	// Preference name (key) of the enablement field.
	String enableFieldName;
	
	// Description for the enablement field.
	String enableFieldDescription;

	// Preference name (key) of the (selected) tactics field. 
	String tacticsFieldName;
	
	// Description for the (selected) tactics field.
	String tacticsFieldDescription;

	// The tactic preference associated with this preference page. 
	IAutoTacticPreference tacticPreference = null;
	
	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param description
	 *            Description of this tactic preference
	 * @param enableFieldName
	 *            the preference name (key) of the enablement field.
	 * @param enableFieldDescription
	 *            the description of the enablement field.
	 * @param tacticsFieldName
	 *            the preference name (key) of the (selected) tactics field.
	 * @param tacticsFieldDescription
	 *            the description of the (selected) tactics field.
	 */
	public TacticPreferencePage(String description, String enableFieldName,
			String enableFieldDescription, String tacticsFieldName,
			String tacticsFieldDescription) {
		super();
		this.enableFieldName = enableFieldName;
		this.enableFieldDescription = enableFieldDescription;
		this.tacticsFieldName = tacticsFieldName;
		this.tacticsFieldDescription = tacticsFieldDescription;
		setTacticPreference();
		setPreferenceStore(EventBPreferenceStore.getPreferenceStore());
		setDescription(description);
	}

	/**
	 * Set the tactic preference to be use with this preference page. This is
	 * the method to be called by the constructor.
	 */
	protected abstract void setTacticPreference();

	/**
	 * Creates the field editors for enablement field using
	 * {@link BooleanFieldEditor} and selected tactics field using
	 * {@link TwoListSelectionEditor}. Each field editor knows how to save and
	 * restore itself.
	 */
	@Override
	public void createFieldEditors() {
		// Create the enablement field.
		enablementEditor = new BooleanFieldEditor(
			enableFieldName,
			enableFieldDescription,
			getFieldEditorParent());
		addField(
				enablementEditor);

		// Create the (selected) tactics field.
		tacticsEditor = new TwoListSelectionEditor(
				tacticsFieldName,
				tacticsFieldDescription,
				getFieldEditorParent()) {

			@Override
			protected String createList(ArrayList<Object> objects) {
				ArrayList<Object> tacticIDs = new ArrayList<Object>();
				for (Object obj : objects) {
					assert obj instanceof ITacticDescriptor;
					tacticIDs.add(((ITacticDescriptor) obj).getTacticID());
				}
				return UIUtils.toCommaSeparatedList(tacticIDs);
			}

			@Override
			protected ArrayList<Object> parseString(String stringList) {
				String[] tacticIDs = UIUtils.parseString(stringList);
				ArrayList<Object> result = new ArrayList<Object>();
				for (String tacticID : tacticIDs) {
					IAutoTacticRegistry tacticRegistry = SequentProver.getAutoTacticRegistry();
					if (!tacticRegistry.isRegistered(tacticID)) {
						if (UIUtils.DEBUG) {
							System.out.println("Tactic " + tacticID //$NON-NLS-1$
									+ " is not registered."); //$NON-NLS-1$
						}
						continue;
					}
					
					ITacticDescriptor tacticDescriptor = tacticRegistry
							.getTacticDescriptor(tacticID);
					if (!tacticPreference.isDeclared(tacticDescriptor)) {
						if (UIUtils.DEBUG) {
							System.out
									.println("Tactic " //$NON-NLS-1$
											+ tacticID
											+ " is not declared for using within this tactic preference."); //$NON-NLS-1$
						}
					}
					else {
						result.add(tacticDescriptor);
					}
				}
				return result;
			}

			@Override
			protected String getLabel(Object object) {
				return ((ITacticDescriptor) object).getTacticName();
			}

			@Override
			protected Collection<Object> getDeclaredObjects() {
				Collection<ITacticDescriptor> declaredDescriptors = tacticPreference
						.getDeclaredDescriptors();
				Collection<Object> result = new ArrayList<Object>(
						declaredDescriptors.size());
				result.addAll(declaredDescriptors);
				return result;
			}

		};
		addField(tacticsEditor);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		// Do nothing
	}

}