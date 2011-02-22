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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.eclipse.swt.layout.GridData.FILL_BOTH;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_AUTOTACTIC_CHOICE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_AUTOTACTIC_ENABLE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_POSTTACTIC_CHOICE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_POSTTACTIC_ENABLE;
import static org.eventb.internal.ui.utils.Messages.preferencepage_pomtactic_enablementdescription;
import static org.eventb.internal.ui.utils.Messages.preferencepage_pomtactic_selectedtacticprofiledescription;
import static org.eventb.internal.ui.utils.Messages.preferencepage_postautotactic_description;
import static org.eventb.internal.ui.utils.Messages.preferencepage_postautotactic_duplicatebutton;
import static org.eventb.internal.ui.utils.Messages.preferencepage_postautotactic_editbutton;
import static org.eventb.internal.ui.utils.Messages.preferencepage_postautotactic_export_ws_profiles;
import static org.eventb.internal.ui.utils.Messages.preferencepage_postautotactic_import_ws_profiles;
import static org.eventb.internal.ui.utils.Messages.preferencepage_postautotactic_newbutton;
import static org.eventb.internal.ui.utils.Messages.preferencepage_postautotactic_profiledetails_header;
import static org.eventb.internal.ui.utils.Messages.preferencepage_postautotactic_removebutton;
import static org.eventb.internal.ui.utils.Messages.preferencepage_postautotactic_tab_autoposttactics;
import static org.eventb.internal.ui.utils.Messages.preferencepage_postautotactic_tab_profiles;
import static org.eventb.internal.ui.utils.Messages.preferencepage_posttactic_enablementdescription;
import static org.eventb.internal.ui.utils.Messages.preferencepage_posttactic_selectedtacticprofiledescription;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eventb.core.preferences.CachedPreferenceMap;
import org.eventb.core.preferences.ICacheListener;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.internal.ui.preferences.AbstractFieldPreferenceAndPropertyPage;
import org.eventb.internal.ui.preferences.EnabledComboEditor;
import org.eventb.internal.ui.preferences.IEventBFieldEditor;
import org.eventb.internal.ui.preferences.PreferenceConstants;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBUIPlugin;

public class PostAutoTacticPreferencePage extends
		AbstractFieldPreferenceAndPropertyPage {

	static final String DEFAULT_AUTO_TACTICS = TacticPreferenceUtils
			.getDefaultAutoTactics();
	static final String DEFAULT_POST_TACTICS = TacticPreferenceUtils
			.getDefaultPostTactics();

	public static final String PAGE_ID = PreferenceConstants.AUTO_POST_TACTIC_PREFERENCE_PAGE_ID;

	// Cache containing the current list of profiles. Used in both tabs
	protected TacticsProfilesCache cache;
	/*
	 * Additional cache containing the profiles list of workspace. Used in
	 * export profile tab > export
	 */
	protected TacticsProfilesCache workspaceCache = null;
	// Tactics tab : auto-tactic group
	protected EnabledComboEditor autoTactic;
	// Tactics tab : post-tactic group
	protected EnabledComboEditor postTactic;
	// Profiles tab : list of profiles
	protected DetailedList profilesList;
	// Profiles tab : button edit
	protected Button edit;
	// Profiles tab : button remove
	protected Button remove;
	// Profiles tab : button duplicate
	protected Button duplicate;

	public PostAutoTacticPreferencePage() {
		super(PAGE_ID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setDescription(preferencepage_postautotactic_description);
	}

	@Override
	protected void initializeDefaultProperties() {
		TacticPreferenceUtils.initializeDefault(getPreferenceStore());
	}

	@Override
	protected void createFieldEditors(Composite parent) {
		super.createFieldEditors(parent);
		// initialize the cache after setting preference store
		initializeCaches();
		/*
		 * Create two tabs
		 */
		final TabFolder folder = new TabFolder(parent, NONE);
		folder.setLayoutData(new GridData(FILL_BOTH));
		final Composite tabTactic = getTab(folder,
				preferencepage_postautotactic_tab_autoposttactics);
		final Composite tabProfiles = getTab(folder,
				preferencepage_postautotactic_tab_profiles);
		createTacticsTab(tabTactic);
		createProfilsTab(tabProfiles);
	}

	private void initializeCaches() {
		// current cache
		cache = new TacticsProfilesCache(getPreferenceStore());

		if (getWorkspacePreferencePage() instanceof PostAutoTacticPreferencePage) {
			final PostAutoTacticPreferencePage page = (PostAutoTacticPreferencePage) getWorkspacePreferencePage();
			// this page is opened from an other preference page
			workspaceCache = page.cache;
		} else {
			final IPreferenceStore wsStore = EventBUIPlugin.getDefault()
					.getPreferenceStore();
			workspaceCache = new TacticsProfilesCache(wsStore);
			workspaceCache.load();
		}
	}

	private void createTacticsTab(Composite tab) {
		setLayout(tab, 1);
		autoTactic = new EnabledComboEditor(getPreferenceStore(),
				Messages.preferencepage_pomtactic_title, P_AUTOTACTIC_ENABLE,
				preferencepage_pomtactic_enablementdescription,
				P_AUTOTACTIC_CHOICE,
				preferencepage_pomtactic_selectedtacticprofiledescription, tab,
				isPropertyPage());
		postTactic = new EnabledComboEditor(getPreferenceStore(),
				Messages.preferencepage_posttactic_title, P_POSTTACTIC_ENABLE,
				preferencepage_posttactic_enablementdescription,
				P_POSTTACTIC_CHOICE,
				preferencepage_posttactic_selectedtacticprofiledescription,
				tab, isPropertyPage());

		cache.addListener(new ICacheListener<List<ITacticDescriptor>>() {

			@Override
			public void cacheChanged(
					CachedPreferenceMap<List<ITacticDescriptor>> map) {
				final String[] names = getSortedProfileNames();
				autoTactic.setItems(names);
				postTactic.setItems(names);

			}
		});

	}

	private void setLayout(Composite composite, int numColumns) {
		final GridLayout layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		layout.horizontalSpacing = 5;
		layout.verticalSpacing = 5;
		layout.numColumns = numColumns;
		composite.setLayout(layout);
	}

	protected void updateButtonAndDetails() {
		profilesList.updateDetails();
		updateProfilesButton();
	}

	private void createProfilsTab(Composite parent) {
		setLayout(parent, 2);
		setFillParent(parent);

		profilesList = new DetailedList("",
				preferencepage_postautotactic_profiledetails_header, parent);
		profilesList.setDetailsProvider(new TacticsProvider());
		cache.addListener(new ICacheListener<List<ITacticDescriptor>>() {

			@Override
			public void cacheChanged(
					CachedPreferenceMap<List<ITacticDescriptor>> map) {
				updateButtonAndDetails();
				updateProfilesList();
			}

		});
		profilesList.addButton(preferencepage_postautotactic_newbutton,
				new Listener() {

					@Override
					public void handleEvent(Event event) {
						newProfile();
					}

				});
		edit = profilesList.addButton(preferencepage_postautotactic_editbutton,
				new Listener() {

					@Override
					public void handleEvent(Event event) {
						editProfile();
						updateProfilesList();
					}

				});
		remove = profilesList.addButton(
				preferencepage_postautotactic_removebutton, new Listener() {

					@Override
					public void handleEvent(Event event) {
						removeProfile();
					}

				});
		duplicate = profilesList.addButton(
				preferencepage_postautotactic_duplicatebutton, new Listener() {

					@Override
					public void handleEvent(Event event) {
						duplicateTactic();
					}

				});
		edit.setEnabled(false);
		remove.setEnabled(false);
		duplicate.setEnabled(false);
		profilesList.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateButtonAndDetails();
			}

		});
		setProfileContextMenu();
		addField(new TacticsEditor());
	}

	private void setProfileContextMenu() {
		// Set context menu only for project specific profile
		if (!isPropertyPage())
			return;

		final MenuManager popupMenu = new MenuManager();
		final IAction importAction = new Action() {

			@Override
			public void run() {
				final List<IPrefMapEntry<List<ITacticDescriptor>>> profilesWS = workspaceCache
						.getEntries();
				cache.addAll(profilesWS);
			}
		};
		importAction.setText(preferencepage_postautotactic_import_ws_profiles);

		final IAction exportAction = new Action() {

			private List<IPrefMapEntry<List<ITacticDescriptor>>> getSelectedProfiles() {
				final String[] selection = profilesList.getSelection();
				final List<IPrefMapEntry<List<ITacticDescriptor>>> result = new ArrayList<IPrefMapEntry<List<ITacticDescriptor>>>();
				for (String name : selection) {
					final IPrefMapEntry<List<ITacticDescriptor>> profile = cache
							.getEntry(name);
					if (profile != null) {
						result.add(profile);
					}
				}
				return result;
			}

			@Override
			@SuppressWarnings("synthetic-access")
			public void run() {
				// Adds profiles into the cache and gets profiles which wasn't
				// already in the cache
				final List<IPrefMapEntry<List<ITacticDescriptor>>> added = workspaceCache
						.addAll(getSelectedProfiles());

				// If there is a preference page, adds profiles into the
				// profiles list
				final AbstractFieldPreferenceAndPropertyPage parentPage = getWorkspacePreferencePage();
				if (parentPage instanceof PostAutoTacticPreferencePage) {
					final PostAutoTacticPreferencePage wsTacticPage = (PostAutoTacticPreferencePage) parentPage;
					for (IPrefMapEntry<List<ITacticDescriptor>> profile : added) {
						wsTacticPage.profilesList.addElement(profile.getKey());
					}
				}
			}
		};

		profilesList.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean enableExport = true;
				final int selectionCount = profilesList.getSelectionCount();
				if (selectionCount <= 2) {
					for (String str : profilesList.getSelection()) {
						enableExport = !(str.equals(DEFAULT_AUTO_TACTICS)
								|| str.equals(DEFAULT_POST_TACTICS));
					}
				} else {
					enableExport = selectionCount > 0;
				}
				exportAction.setEnabled(enableExport);
			}
		});
		exportAction.setText(preferencepage_postautotactic_export_ws_profiles);
		exportAction.setEnabled(false);
		popupMenu.add(importAction);
		popupMenu.add(exportAction);
		profilesList.setMenu(popupMenu);
	}

	protected void newProfile() {
		final EditProfilWizard wizard = new EditProfilWizard(cache);
		final WizardDialog dialog = new WizardDialog(getShell(), wizard);
		dialog.create();
		dialog.open();
	}

	protected void editProfile() {
		final String[] selection = profilesList.getSelection();
		if (selection.length == 1) {
			final String name = selection[0];
			final EditProfilWizard wizard = new EditProfilWizard(cache, name);
			final WizardDialog dialog = new WizardDialog(getShell(), wizard);
			dialog.create();
			dialog.open();
		}
	}

	protected void removeProfile() {
		cache.remove(profilesList.getSelection());
	}

	protected void duplicateTactic() {
		final String[] selection = profilesList.getSelection();
		if (selection.length == 1) {
			final String name = selection[0];
			final String copy = getFreeCopyName(name);
			final IPrefMapEntry<List<ITacticDescriptor>> profile = cache
					.getEntry(name);
			if (profile != null) {
				cache.add(copy, profile.getValue());
			}
		}
	}

	private String getFreeCopyName(String name) {
		// number of copy
		int i = 1;
		// search the base name
		final String copyBase;
		final Pattern p = Pattern.compile("(.+) \\(copy[ ]?(\\d+)?\\)"); //$NON-NLS-1$
		final Matcher m = p.matcher(name);
		if (m.matches()) {
			copyBase = m.group(1) + " (copy"; //$NON-NLS-1$
			if (m.group(2) != null) {
				i = Integer.parseInt(m.group(2)) + 1;
			}
		} else {
			copyBase = name + " (copy"; //$NON-NLS-1$
		}

		// search the first free name
		String copy = copyBase + ")"; //$NON-NLS-1$
		while (cache.exists(copy)) {
			final StringBuilder builder = new StringBuilder(copyBase);
			builder.append(' ');
			builder.append(i);
			builder.append(')');
			copy = builder.toString();
			i++;
		}
		return copy;
	}

	/**
	 * Set the list of profiles with profile name in the cache.
	 */
	protected void updateProfilesList() {
		final String[] labels = getSortedProfileNames();
		final String[] selection = profilesList.getSelection();
		profilesList.clear();
		profilesList.setList(labels);
		profilesList.setSelection(selection);
		updateButtonAndDetails();
	}

	protected String[] getSortedProfileNames() {
		final Set<String> profiles = cache.getEntryNames();
		final boolean addAutoDefault = profiles.remove(DEFAULT_AUTO_TACTICS);
		final boolean addPostDefault = profiles.remove(DEFAULT_POST_TACTICS);
		final List<String> labels = new ArrayList<String>();
		labels.addAll(profiles);
		Collections.sort(labels);
		if (addAutoDefault) {
			labels.add(0, DEFAULT_AUTO_TACTICS);
		}
		if (addPostDefault) {
			labels.add(1, DEFAULT_POST_TACTICS);
		}
		return labels.toArray(new String[labels.size()]);
	}

	private void updateProfilesButton() {
		final Collection<String> selection = asList(profilesList.getSelection());
		final boolean enableNoDefault = !selection
				.contains(DEFAULT_AUTO_TACTICS)
				&& !selection.contains(DEFAULT_POST_TACTICS);
		final boolean enableEq1 = selection.size() == 1;
		final boolean enableMin1 = selection.size() > 0;
		edit.setEnabled(enableNoDefault && enableEq1);
		remove.setEnabled(enableNoDefault && enableMin1);
		duplicate.setEnabled(enableEq1);
	}

	private Composite getTab(TabFolder folder, String title) {
		final TabItem tab = new TabItem(folder, NONE);
		final Composite composite = new Composite(folder, NONE);
		tab.setControl(composite);
		tab.setText(title);
		return composite;
	}

	class TacticsProvider implements IDetailsProvider {
		@Override
		public String[] getDetails(String element) {
			final IPrefMapEntry<List<ITacticDescriptor>> profile = cache
					.getEntry(element);
			final List<ITacticDescriptor> tactics;
			if (profile == null) {
				tactics = emptyList();
			} else {
				tactics = profile.getValue();
			}
			final String[] result = new String[tactics.size()];
			for (int i = 0; i < result.length; i++) {
				result[i] = tactics.get(i).getTacticName();
			}
			return result;
		}
	}

	/**
	 * The preference page use a unique field editor to ensure that the cache is
	 * loaded first
	 */
	class TacticsEditor implements IEventBFieldEditor {

		@Override
		public void store() {
			cache.store();
			autoTactic.store();
			postTactic.store();
		}

		@Override
		public void setEnabled(boolean enabled) {
			profilesList.setEnabled(enabled);
			updateButtonAndDetails();
			autoTactic.setEnabled(enabled);
			postTactic.setEnabled(enabled);
		}

		@Override
		public void load() {
			cache.load();
			autoTactic.load();
			postTactic.load();
		}

		@Override
		public void loadDefault() {
			cache.loadDefault();
			autoTactic.loadDefault();
			postTactic.loadDefault();
		}

	}

	@Override
	public void performApply() {
		if (getWorkspacePreferencePage() == null) {
			workspaceCache.store();
		}
		super.performApply();
	}

}
