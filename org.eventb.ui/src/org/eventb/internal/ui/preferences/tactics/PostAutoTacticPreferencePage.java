/*******************************************************************************
 * Copyright (c) 2010, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences.tactics;

import static java.util.Arrays.asList;
import static org.eclipse.swt.layout.GridData.FILL_BOTH;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.DEFAULT_AUTO_TACTIC;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.DEFAULT_POST_TACTIC;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_AUTOTACTIC_CHOICE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_AUTOTACTIC_ENABLE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_POSTTACTIC_CHOICE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_POSTTACTIC_ENABLE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceFactory.makeTacticPreferenceMap;
import static org.eventb.core.preferences.autotactics.TacticPreferenceFactory.makeTacticProfileCache;
import static org.eventb.internal.ui.utils.Messages.preferencepage_pomtactic_enablementdescription;
import static org.eventb.internal.ui.utils.Messages.preferencepage_pomtactic_selectedtacticprofiledescription;
import static org.eventb.internal.ui.utils.Messages.preferencepage_postautotactic_description;
import static org.eventb.internal.ui.utils.Messages.preferencepage_postautotactic_duplicatebutton;
import static org.eventb.internal.ui.utils.Messages.preferencepage_postautotactic_editbutton;
import static org.eventb.internal.ui.utils.Messages.preferencepage_postautotactic_export_ws_profiles;
import static org.eventb.internal.ui.utils.Messages.preferencepage_postautotactic_import_ws_profiles;
import static org.eventb.internal.ui.utils.Messages.preferencepage_postautotactic_newbutton;
import static org.eventb.internal.ui.utils.Messages.preferencepage_postautotactic_removebutton;
import static org.eventb.internal.ui.utils.Messages.preferencepage_postautotactic_tab_autoposttactics;
import static org.eventb.internal.ui.utils.Messages.preferencepage_postautotactic_tab_profiles;
import static org.eventb.internal.ui.utils.Messages.preferencepage_postautotactic_tacticdetails_header;
import static org.eventb.internal.ui.utils.Messages.preferencepage_posttactic_enablementdescription;
import static org.eventb.internal.ui.utils.Messages.preferencepage_posttactic_selectedtacticprofiledescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eventb.core.EventBPlugin;
import org.eventb.core.preferences.CachedPreferenceMap;
import org.eventb.core.preferences.ICacheListener;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.preferences.autotactics.ITacticProfileCache;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.preferences.AbstractFieldPreferenceAndPropertyPage;
import org.eventb.internal.ui.preferences.EnabledComboEditor;
import org.eventb.internal.ui.preferences.IEventBFieldEditor;
import org.eventb.internal.ui.preferences.PreferenceConstants;
import org.eventb.internal.ui.utils.Messages;

public class PostAutoTacticPreferencePage extends
		AbstractFieldPreferenceAndPropertyPage {

	public static final String PAGE_ID = PreferenceConstants.AUTO_POST_TACTIC_PREFERENCE_PAGE_ID;

	// Cache containing the current list of profiles. Used in both tabs
	protected ITacticProfileCache cache;
	/*
	 * Additional cache containing the profiles list of workspace. Used in
	 * export profile tab > export
	 */
	protected ITacticProfileCache workspaceCache = null;
	// Auto/Post Tactics tab : auto-tactic group
	protected EnabledComboEditor autoTactic;
	// Auto/Post Tactics tab : post-tactic group
	protected EnabledComboEditor postTactic;
	// Tactics tab : list of Tactics
	protected DetailedList tacticList;
	// Tactics tab : button edit
	protected Button edit;
	// Tactics tab : button remove
	protected Button remove;
	// Tactics tab : button duplicate
	protected Button duplicate;
	
	
	public PostAutoTacticPreferencePage() {
		super(PAGE_ID, EventBPlugin.PLUGIN_ID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setDescription(preferencepage_postautotactic_description);
	}

	@Override
	protected void initializeDefaultProperties() {
		// nothing to do
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
		final Composite tabAutoPost = getTab(folder,
				preferencepage_postautotactic_tab_autoposttactics);
		final Composite tabProfiles = getTab(folder,
				preferencepage_postautotactic_tab_profiles);
		createAutoPostTab(tabAutoPost);
		createProfilesTab(tabProfiles);
	}

	private void initializeCaches() {
		// current cache
		cache = makeTacticProfileCache(node);

		if (getWorkspacePreferencePage() instanceof PostAutoTacticPreferencePage) {
			final PostAutoTacticPreferencePage page = (PostAutoTacticPreferencePage) getWorkspacePreferencePage();
			// this page is opened from an other preference page
			workspaceCache = page.cache;
		} else {
			workspaceCache = makeTacticProfileCache(node);
			workspaceCache.load();
		}
	}

	private void createAutoPostTab(final Composite tab) {
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

		cache.addListener(new ICacheListener<ITacticDescriptor>() {

			@Override
			public void cacheChanged(
					CachedPreferenceMap<ITacticDescriptor> map) {
				final String[] names = getSortedProfileNames();
				autoTactic.setItems(names);
				postTactic.setItems(names);
				tab.pack();
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

	protected void updateButtons() {
		updateProfilesButton();
	}

	private void createProfilesTab(Composite parent) {
		setLayout(parent, 2);
		setFillParent(parent);

		tacticList = new DetailedList(preferencepage_postautotactic_tacticdetails_header,
				parent);
		tacticList.setDetailsProvider(new TacticDetailsProvider(cache));
		cache.addListener(new ICacheListener<ITacticDescriptor>() {

			@Override
			public void cacheChanged(
					CachedPreferenceMap<ITacticDescriptor> map) {
				updateButtons();
				updateTacticsList();
			}

		});
		tacticList.addButton(preferencepage_postautotactic_newbutton,
				new Listener() {

					@Override
					public void handleEvent(Event event) {
						newProfile();
					}

				});
		edit = tacticList.addButton(preferencepage_postautotactic_editbutton,
				new Listener() {

					@Override
					public void handleEvent(Event event) {
						editProfile();
						updateTacticsList();
						updateButtons();
					}

				});
		remove = tacticList.addButton(
				preferencepage_postautotactic_removebutton, new Listener() {

					@Override
					public void handleEvent(Event event) {
						removeProfile();
					}

				});
		duplicate = tacticList.addButton(
				preferencepage_postautotactic_duplicatebutton, new Listener() {

					@Override
					public void handleEvent(Event event) {
						duplicateTactic();
					}

				});
		
		tacticList.addButton(
				"Export...", new Listener() {

					@Override
					public void handleEvent(Event event) {
						exportTactics();
					}

				});
		
		tacticList.addButton(
				"Import...", new Listener() {

					@Override
					public void handleEvent(Event event) {
						importTactics();
					}

				});
		
				
		edit.setEnabled(false);
		remove.setEnabled(false);
		duplicate.setEnabled(false);
		tacticList.addSelectionListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateButtons();
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
				final List<IPrefMapEntry<ITacticDescriptor>> profilesWS = workspaceCache
						.getEntries();
				cache.addAll(profilesWS);
			}
		};
		importAction.setText(preferencepage_postautotactic_import_ws_profiles);

		final IAction exportAction = new Action() {

			private List<IPrefMapEntry<ITacticDescriptor>> getSelectedProfiles() {
				final String[] selection = tacticList.getSelection();
				final List<IPrefMapEntry<ITacticDescriptor>> result = new ArrayList<IPrefMapEntry<ITacticDescriptor>>();
				for (String name : selection) {
					final IPrefMapEntry<ITacticDescriptor> profile = cache
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
				final List<IPrefMapEntry<ITacticDescriptor>> added = workspaceCache
						.addAll(getSelectedProfiles());

				// If there is a preference page, adds profiles into the
				// profiles list
				final AbstractFieldPreferenceAndPropertyPage parentPage = getWorkspacePreferencePage();
				if (parentPage instanceof PostAutoTacticPreferencePage) {
					final PostAutoTacticPreferencePage wsTacticPage = (PostAutoTacticPreferencePage) parentPage;
					for (IPrefMapEntry<ITacticDescriptor> profile : added) {
						wsTacticPage.tacticList.addElement(profile.getKey());
					}
				}
			}
		};

		tacticList.addSelectionListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				boolean enableExport = true;
				final int selectionCount = tacticList.getSelectionCount();
				if (selectionCount <= 2) {
					enableExport = !containsDefaultProfiles(Arrays.asList(tacticList.getSelection()));
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
		tacticList.setMenu(popupMenu);
	}

	protected void newProfile() {
		final EditProfilWizard wizard = new EditProfilWizard(cache);
		final WizardDialog dialog = new WizardDialog(getShell(), wizard);
		dialog.create();
		dialog.open();
	}

	protected void editProfile() {
		// save changes made through in-place editor
		tacticList.saveCurrentIfChanges(false);
		
		final String[] selection = tacticList.getSelection();
		if (selection.length == 1) {
			final String name = selection[0];
			final EditProfilWizard wizard = new EditProfilWizard(cache, name);
			final WizardDialog dialog = new WizardDialog(getShell(), wizard);
			dialog.create();
			dialog.open();
		}
	}

	protected void removeProfile() {
		cache.remove(tacticList.getSelection());
	}

	protected void duplicateTactic() {
		final String[] selection = tacticList.getSelection();
		if (selection.length == 1) {
			final String name = selection[0];
			final String copy = getFreeCopyName(name);
			final IPrefMapEntry<ITacticDescriptor> profile = cache
					.getEntry(name);
			if (profile != null) {
				cache.add(copy, profile.getValue());
			}
		}
	}

	private List<IPrefMapEntry<ITacticDescriptor>> getSelectedProfiles() {
		final String[] selection = tacticList.getSelection();
		final List<IPrefMapEntry<ITacticDescriptor>> profiles = new ArrayList<IPrefMapEntry<ITacticDescriptor>>(
				selection.length);
		for (String profile : selection) {
			profiles.add(cache.getEntry(profile));
		}
		return profiles;
	}

	protected void exportTactics() {
		final ListSelectionDialog select = ProfileImportExport
				.makeProfileSelectionDialog(getShell(), cache.getEntries(),
						"Export profiles", getSelectedProfiles());
		select.open();
		final Object[] result = select.getResult();
		if (result == null) {
			return;
		}
		final List<IPrefMapEntry<ITacticDescriptor>> exported = toMapEntries(result);
		final CachedPreferenceMap<ITacticDescriptor> exportCache = makeTacticPreferenceMap();
		try {
			exportCache.addAll(exported);
			ProfileImportExport.saveExported(getShell(), exportCache);
		} catch (IllegalArgumentException e) {
			UIUtils.log(e, "error while exporting profiles");
			MessageDialog.openError(getShell(), "Export error",
					"An error occurred while exporting selected profiles:\n"
							+ e.getMessage() + "\nSee error log for details.");
		}
	}

	@SuppressWarnings("unchecked")
	private static List<IPrefMapEntry<ITacticDescriptor>> toMapEntries(
			Object[] result) {
		final List<IPrefMapEntry<ITacticDescriptor>> entries = new ArrayList<IPrefMapEntry<ITacticDescriptor>>();
		for (Object object : result) {
			if (object instanceof IPrefMapEntry<?>) {
				entries.add((IPrefMapEntry<ITacticDescriptor>) object);
			}
		}
		return entries;
	}

	protected void importTactics() {
		final CachedPreferenceMap<ITacticDescriptor> loaded = ProfileImportExport
				.loadImported(getShell());
		if (loaded == null) {
			return;
		}
		final List<IPrefMapEntry<ITacticDescriptor>> imported = selectImport(
				loaded, loaded.getEntries());
		if (imported == null) {
			return;
		}
		try {
			cache.addAll(imported);
		} catch (IllegalArgumentException e) {
			UIUtils.log(e, "error while importing profiles");
			MessageDialog.openError(getShell(), "Import error",
					"An error occurred while importing selected profiles:\n"
							+ e.getMessage() + "\nSee error log for details.");
		}
	}

	private List<IPrefMapEntry<ITacticDescriptor>> selectImport(
			CachedPreferenceMap<ITacticDescriptor> available,
			List<IPrefMapEntry<ITacticDescriptor>> initSelected) {

		final ListSelectionDialog select = ProfileImportExport
				.makeProfileSelectionDialog(getShell(), available.getEntries(),
						"Import profiles", initSelected);

		select.open();
		final Object[] result = select.getResult();
		if (result == null) {
			return null;
		}
		final List<IPrefMapEntry<ITacticDescriptor>> selected = toMapEntries(result);
		final Set<String> existingKeys = getKeys(selected);
		existingKeys.retainAll(cache.getEntryNames());
		
		if (!existingKeys.isEmpty()) {
			final String message = "The following profiles already exist:\n"
					+ existingKeys + "\nThey will be overwritten.";
			final boolean confirm = MessageDialog.openConfirm(getShell(),
					"Overwrite profiles", message);
			if (confirm) {
				cache.remove(existingKeys.toArray(new String[existingKeys
						.size()]));
			} else {
				return selectImport(available, selected);
			}
		}
		return selected;
	}
	
	private static Set<String> getKeys(List<IPrefMapEntry<ITacticDescriptor>> entries) {
		final Set<String> keys = new LinkedHashSet<String>(entries.size());
		for (IPrefMapEntry<ITacticDescriptor> entry : entries) {
			keys.add(entry.getKey());
		}
		return keys;
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
	protected void updateTacticsList() {
		final String[] labels = getSortedProfileNames();
		final String[] selection = tacticList.getSelection();
		tacticList.clear();
		tacticList.setList(labels);
		tacticList.setSelection(selection);
		tacticList.updateDetails();
	}

	protected String[] getSortedProfileNames() {
		final List<String> profiles = new ArrayList<String>(cache.getEntryNames());
		final List<String> defaultProfiles = new ArrayList<String>();
		final Iterator<String> iter = profiles.iterator();
		while(iter.hasNext()) {
			final String profile = iter.next(); 
			if (cache.isDefaultEntry(profile)) {
				defaultProfiles.add(profile);
				iter.remove();
			}
		}
		Collections.sort(profiles);
		
		final boolean addAutoDefault = defaultProfiles.remove(DEFAULT_AUTO_TACTIC);
		final boolean addPostDefault = defaultProfiles.remove(DEFAULT_POST_TACTIC);
		Collections.sort(defaultProfiles);
		if (addPostDefault) {
			defaultProfiles.add(0, DEFAULT_POST_TACTIC);
		}
		if (addAutoDefault) {
			defaultProfiles.add(0, DEFAULT_AUTO_TACTIC);
		}

		final List<String> labels = new ArrayList<String>();
		labels.addAll(defaultProfiles);
		labels.addAll(profiles);
		return labels.toArray(new String[labels.size()]);
	}

	boolean containsDefaultProfiles(Collection<String> selection) {
		for (String name : selection) {
			if (cache.isDefaultEntry(name)) {
				return true;
			}
		}
		return false;
	}
	
	private void updateProfilesButton() {
		final Collection<String> selection = asList(tacticList.getSelection());
		final boolean enableNoDefault = !containsDefaultProfiles(selection);
		final boolean enableEq1 = selection.size() == 1;
		final boolean enableMin1 = selection.size() > 0;
		edit.setEnabled(enableNoDefault && enableEq1);
		remove.setEnabled(enableNoDefault && enableMin1);
		duplicate.setEnabled(enableEq1);
	}

	private static Composite getTab(TabFolder folder, String title) {
		final TabItem tab = new TabItem(folder, NONE);
		final Composite composite = new Composite(folder, NONE);
		tab.setControl(composite);
		tab.setText(title);
		return composite;
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
			tacticList.setEnabled(enabled);
			updateButtons();
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
		tacticList.saveCurrentIfChanges(false);
		if (getWorkspacePreferencePage() == null) {
			workspaceCache.store();
		}
		super.performApply();
	}

}
