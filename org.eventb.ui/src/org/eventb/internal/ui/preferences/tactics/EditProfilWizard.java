/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences.tactics;

import static java.util.Collections.emptyList;
import static org.eventb.internal.ui.utils.Messages.wizard_editprofil_description;
import static org.eventb.internal.ui.utils.Messages.wizard_editprofil_detailsdescription;
import static org.eventb.internal.ui.utils.Messages.wizard_editprofil_nameheader;
import static org.eventb.internal.ui.utils.Messages.wizard_editprofil_profileexists;
import static org.eventb.internal.ui.utils.Messages.wizard_editprofil_profilemustbespecified;
import static org.eventb.internal.ui.utils.Messages.wizard_editprofil_profilemusthaveatactic;
import static org.eventb.internal.ui.utils.Messages.wizard_editprofil_title;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.internal.ui.preferences.AbstractEventBPreferencePage;
import org.eventb.internal.ui.preferences.TwoListSelection;

public class EditProfilWizard extends Wizard {

	// The wizard page.
	private EditProfilWizardPage page;

	private final TacticsProfilesCache cache;
	// indicates if the wizard has to create the profile
	private final boolean created;
	// the profile name to edit or empty. Not null
	private String profileName;

	private IPrefMapEntry<List<ITacticDescriptor>> profile = null;

	private final List<ITacticDescriptor> selected;

	public EditProfilWizard(TacticsProfilesCache cache) {
		this.cache = cache;
		this.created = true;
		this.profileName = "";
		this.selected = emptyList();
	}

	public EditProfilWizard(TacticsProfilesCache cache, String profileName) {
		this.cache = cache;
		created = false;
		this.profileName = profileName;
		final IPrefMapEntry<List<ITacticDescriptor>> selectedProfile = cache
				.getEntry(profileName);
		if (selectedProfile != null) {
			this.selected = selectedProfile.getValue();
		} else {
			this.selected = emptyList();
		}
	}

	@Override
	public void addPages() {
		page = new EditProfilWizardPage(cache, wizard_editprofil_title,
				created, profileName, selected);
		page.setTitle(wizard_editprofil_title);
		addPage(page);
	}

	/**
	 * If new profile, create the profile with selected tactics. Else change the
	 * tactics list of existing profile. The modification is only in the
	 * {@link TacticsProfilesCache}.
	 */
	@Override
	public boolean performFinish() {
		// renames the profile if it already exist
		if (!created && !profileName.equals(page.getProfileName())) {
			cache.remove(profileName);
		}
		profile = cache.getEntry(page.getProfileName());
		if (profile == null) {
			cache.add(page.getProfileName(), page.getSelectedTactics());
			profile = cache.getEntry(page.getProfileName());
		} else {
			profile.setValue(page.getSelectedTactics());
		}
		return true;
	}

	static class EditProfilWizardPage extends WizardPage {
		// input text for profile name
		private Text profile;
		// widget to associate tactics to profile
		private TwoListSelection<ITacticDescriptor> selection;
		// indicates if the wizard have to create the profile
		private final boolean created;
		// list of selected tactics
		private final List<ITacticDescriptor> selected;

		private final TacticsProfilesCache cache;

		private final String editedProfile;

		protected EditProfilWizardPage(TacticsProfilesCache cache,
				String pageName, boolean created, String profilName,
				List<ITacticDescriptor> selected) {
			super(wizard_editprofil_title);
			this.cache = cache;
			setDescription(wizard_editprofil_description);
			this.selected = selected;
			editedProfile = profilName;
			this.created = created;
		}

		@Override
		public void createControl(Composite parent) {
			// create parent control
			final Composite composite = new Composite(parent, SWT.NONE);
			final GridLayout layout = new GridLayout();
			layout.numColumns = 1;
			composite.setLayout(layout);
			AbstractEventBPreferencePage.setFillParent(composite);
			setControl(composite);

			// create input text for profile name
			final Label labelProfil = new Label(composite, SWT.FILL);
			labelProfil.setText(wizard_editprofil_nameheader);
			profile = new Text(composite, SWT.BORDER);
			profile.setText(editedProfile);
			profile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			profile.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					updateStatus();
				}
			});
			// create label
			final Label labelList = new Label(composite, NONE);
			labelList.setText(wizard_editprofil_detailsdescription);
			// create 2 list for available tactics and associated tactics
			selection = new TwoListSelection<ITacticDescriptor>(composite,
					new TwoListSelection.ILabelProvider() {
						@Override
						public String getLabel(Object object) {
							return ((ITacticDescriptor) object).getTacticName();
						}
					});
			selection.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateStatus();
				}
			});
			final Collection<ITacticDescriptor> available = TacticPreferenceUtils
					.getAvailableTactics();
			available.removeAll(selected);
			setLists(available, selected);

			updateStatus();
		}

		public void setLists(Collection<ITacticDescriptor> newAvailable,
				Collection<ITacticDescriptor> newSelected) {
			selection.setLists(newAvailable, newSelected);
		}

		public String getProfileName() {
			return profile.getText();
		}

		/**
		 * Gets the list of selected objects.
		 * 
		 * @return the list of selected objects.
		 */
		public ArrayList<ITacticDescriptor> getSelectedTactics() {
			return selection.getSelectedObjects();
		}

		/**
		 * Update the status of this dialog.
		 */
		void updateStatus() {
			String message = null;
			boolean complete = true;
			final String name = profile.getText();
			if (created || !profile.getText().equals(editedProfile)) {
				if (cache.exists(name)) {
					message = wizard_editprofil_profileexists;
					complete = false;
				} else if (name.isEmpty()) {
					message = wizard_editprofil_profilemustbespecified;
					complete = false;
				} else if (selection.getSelectedObjects().size() == 0) {
					message = wizard_editprofil_profilemusthaveatactic;
					complete = false;
				}
			}
			setErrorMessage(message);
			setPageComplete(complete);
		}
	}

	/**
	 * Returns the edited profile.
	 */
	public IPrefMapEntry<List<ITacticDescriptor>> getProfile() {
		return profile;
	}
}
