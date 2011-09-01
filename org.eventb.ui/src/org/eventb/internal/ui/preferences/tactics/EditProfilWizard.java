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

import static org.eventb.internal.ui.utils.Messages.wizard_editprofil_description;
import static org.eventb.internal.ui.utils.Messages.wizard_editprofil_nameheader;
import static org.eventb.internal.ui.utils.Messages.wizard_editprofil_profileexists;
import static org.eventb.internal.ui.utils.Messages.wizard_editprofil_profilemustbespecified;
import static org.eventb.internal.ui.utils.Messages.wizard_editprofil_title;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.IParamTacticDescriptor;
import org.eventb.core.seqprover.IParameterizerDescriptor;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.ui.preferences.AbstractEventBPreferencePage;

public class EditProfilWizard extends Wizard {
	// wizard map:
	// if new (selected == null && created == false)
	//   ChoiceParamCombined
	//   if param
	//     ChoiceParameterizer
	//   EditProfile
	// if edit (selected != null && created == true)
	//   EditProfile

	private final ChoiceParamCombined choiceParamCombined = new ChoiceParamCombined();
	final ChoiceParameterizer choiceParameterizer = new ChoiceParameterizer();
	final EditProfilWizardPage editProfile = new EditProfilWizardPage();

	final TacticsProfilesCache cache;
	// indicates if the wizard has to create the profile
	final boolean created;
	// the profile name to edit or empty. Not null
	String profileName;

	private IPrefMapEntry<ITacticDescriptor> profile = null;

	ITacticDescriptor selected;

	TacticKind kind = null;

	public EditProfilWizard(TacticsProfilesCache cache) {
		this.cache = cache;
		this.created = true;
		this.profileName = "";
		this.selected = null;
	}

	public EditProfilWizard(TacticsProfilesCache cache, String profileName) {
		this.cache = cache;
		created = false;
		this.profileName = profileName;
		final IPrefMapEntry<ITacticDescriptor> selectedProfile = cache
				.getEntry(profileName);
		if (selectedProfile != null) {
			this.selected = selectedProfile.getValue();
		} else {
			this.selected = null;
		}
	}

	@Override
	public void addPages() {
		if (selected == null) {
			addPage(choiceParamCombined);
			addPage(choiceParameterizer);
			// edit profile page must be added when selected is set
		} else {
			addPage(editProfile);
		}
	}

	/**
	 * If new profile, create the profile with selected tactics. Else change the
	 * tactics list of existing profile. The modification is only in the
	 * {@link TacticsProfilesCache}.
	 */
	@Override
	public boolean performFinish() {
		// renames the profile if it already exist
		final String editProfileName = editProfile.getProfileName();
		profile = cache.getEntry(profileName);
		if (!created && !profileName.equals(editProfileName)) {
			Assert.isNotNull(profile, "profile should already exist: "
					+ profileName);
			profile.setKey(editProfileName);
		}
		final ITacticDescriptor resultDesc = editProfile.getResultDescriptor();
		if (profile == null) {
			cache.add(editProfileName, resultDesc);
			profile = cache.getEntry(editProfileName);
		} else {
			profile.setValue(resultDesc);
		}
		return true;
	}

	@Override
	public boolean canFinish() {
		return editProfile.isPageComplete();
	}

	private static enum TacticKind {
		COMBINED("Combined tactic"), PARAMETERIZED("Parameterized tactic");

		private final String text;

		private TacticKind(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}
	}

	static Composite createParentComposite(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		final GridLayout compLayout = new GridLayout();
		compLayout.numColumns = 1;
		composite.setLayout(compLayout);
		AbstractEventBPreferencePage.setFillParent(composite);
		return composite;
	}

	private class ChoiceParamCombined extends WizardPage {

		private final Button[] radioButtons = new Button[TacticKind.values().length];

		TacticKind choice = TacticKind.COMBINED;

		public ChoiceParamCombined() {
			super("Profile kind choice");
		}

		@Override
		public void createControl(Composite parent) {
			// create parent control
			final Composite composite = createParentComposite(parent);
			setControl(composite);

			final Font font = parent.getFont();

			final Group group = new Group(composite, SWT.NONE);
			group.setFont(font);
			group.setText("Choose which kind of profile to create");

			final GridLayout layout = new GridLayout();
			layout.numColumns = 1;
			group.setLayout(layout);

			final TacticKind[] choices = TacticKind.values();
			for (int i = 0; i < choices.length; i++) {
				final TacticKind ch = choices[i];
				final Button radio = new Button(group, SWT.RADIO | SWT.LEFT);
				radioButtons[i] = radio;
				radio.setText(ch.getText());
				radio.setData(ch);
				radio.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent event) {
						kind = (TacticKind) event.widget.getData();
					}
				});
			}
			radioButtons[TacticKind.COMBINED.ordinal()].setSelection(true);
			kind = TacticKind.COMBINED;
		}

		@Override
		public IWizardPage getNextPage() {
			if (kind == null) {
				return null;
			}
			switch (kind) {
			case COMBINED:
				return editProfile;
			case PARAMETERIZED:
				return choiceParameterizer;
			default:
				throw new IllegalStateException("illegal choice: " + choice);
			}
		}
	}

	static IParameterizerDescriptor[] getChoices() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getParameterizerDescriptors();
	}

	private class ChoiceParameterizer extends WizardPage {

		final IParameterizerDescriptor[] choices;
		IParameterizerDescriptor choice = null;

		protected ChoiceParameterizer() {
			super("Choice of Parameterized Tactic");
			this.choices = getChoices();
		}

		@Override
		public void createControl(Composite parent) {
			// create parent control
			final Composite composite = createParentComposite(parent);
			setControl(composite);

			setTitle("Choice of a tactic");
			if (choices.length == 0) {
				setDescription("Found no tactic to parameterize");
				return;
			}
			setDescription("Choose a tactic to parameterize");
			final String[] names = new String[choices.length];
			for (int i = 0; i < choices.length; i++) {
				names[i] = choices[i].getTacticDescriptor().getTacticName();
			}

			final List list = new List(composite, NONE);
			list.setItems(names);
			list.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					widgetDefaultSelected(e);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					final int selectionIndex = list.getSelectionIndex();
					if (selectionIndex < 0) {
						return;
					}
					choice = choices[selectionIndex];
					ChoiceParameterizer.this.setPageComplete(true);
				}
			});
		}

		@Override
		public IWizardPage getNextPage() {
			if (choice == null)
				return null;
			selected = choice.instantiate(choice.makeParameterSetting(), choice
					.getTacticDescriptor().getTacticID() + ".custom");
			return editProfile;
		}
	}

	private class EditProfilWizardPage extends WizardPage {
		// input text for profile name
		private Text profileText;
		private ParamTacticViewer paramViewer = null;
		private CombinedTacticEditor combEditor = null;
		private SimpleTacticViewer simpleViewer = null;
		
		public EditProfilWizardPage() {
			super(wizard_editprofil_title);
			setDescription(wizard_editprofil_description);
			setWizard(EditProfilWizard.this);
			setPageComplete(false);
		}

		@Override
		public void createControl(Composite parent) {

			// create parent control
			final Composite composite = createParentComposite(parent);
			setControl(composite);

			setTitle(wizard_editprofil_title);

			// create input text for profile name
			final Label labelProfil = new Label(composite, SWT.FILL);
			labelProfil.setText(wizard_editprofil_nameheader);
			profileText = new Text(composite, SWT.BORDER);
			profileText.setText(profileName);
			profileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			profileText.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					updateStatus();
				}
			});

			if (selected instanceof IParamTacticDescriptor) {
				paramViewer = new ParamTacticViewer();
				paramViewer.createContents(composite);
				paramViewer.setInput((IParamTacticDescriptor) selected);
			} else {
				// selected is a combined or ref or simple: treat equally with a
				// combined tactic editor
				combEditor = new CombinedTacticEditor();
				combEditor.createContents(composite);
				combEditor.setInput(selected);
				combEditor.show();
				composite.pack();
				parent.pack();
			}
			updateStatus();
		}

		public String getProfileName() {
			if (profileText == null || profileText.isDisposed()) {
				return "";
			}
			return profileText.getText();
		}

		/**
		 * Gets the list of selected objects.
		 * 
		 * @return the list of selected objects.
		 */
		public ITacticDescriptor getResultDescriptor() {
			if (paramViewer != null) {
				return paramViewer.getEditResult();
			}
			if (combEditor != null) {
				return combEditor.getEditResult();
			}
			if(simpleViewer != null) {
				return simpleViewer.getEditResult();
			}
			return null;
		}

		/**
		 * Update the status of this dialog.
		 */
		void updateStatus() {
			String message = null;
			boolean complete = true;
			if (profileText == null)
				return;
			final String name = profileText.getText();
			if (created || !profileText.getText().equals(profileName)) {
				if (cache.exists(name)) {
					message = wizard_editprofil_profileexists;
					complete = false;
				} else if (name.isEmpty()) {
					message = wizard_editprofil_profilemustbespecified;
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
	public IPrefMapEntry<ITacticDescriptor> getProfile() {
		return profile;
	}

}
