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
package org.eventb.ui.preferences.tests;

import static java.lang.Math.abs;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextRoot;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.preferences.autotactics.IAutoPostTacticManager;
import org.eventb.core.preferences.autotactics.TacticPreferenceConstants;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.autoTacticPreference.AutoTacticPreference;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.eventb.internal.ui.preferences.PreferenceUtils;
import org.eventb.internal.ui.preferences.tactics.TacticPreferenceUtils;
import org.eventb.internal.ui.preferences.tactics.TacticsProfilesCache;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.tests.utils.EventBUITest;

public class TacticsPreferencesTest extends EventBUITest {

	private static final String[] prjTacticIDs = {
			"org.eventb.core.seqprover.trueGoalTac",
			"org.eventb.core.seqprover.falseHypTac", };

	private static final String[] wsTacticIDs = {
			"org.eventb.core.seqprover.goalInHypTac",
			"org.eventb.core.seqprover.funGoalTac", };

	/**
	 * Test the read and write operations in preference store of a profile list.
	 * <p>
	 * Create and store a new list of profile in preference store. Then ensure
	 * the read profile are equals.
	 * </p>
	 */
	public void testProfilesList1() {
		final IPreferenceStore store = EventBUIPlugin.getDefault()
				.getPreferenceStore();
		// tactics1 is used to store the preference
		final TacticsProfilesCache tactics1 = new TacticsProfilesCache(store);
		final Collection<ITacticDescriptor> available = TacticPreferenceUtils
				.getAvailableTactics();
		final Map<String, List<ITacticDescriptor>> expected = new HashMap<String, List<ITacticDescriptor>>();
		expected.put("Profile 1", getRandomSubList(available));
		expected.put("Profile 2", getRandomSubList(available));
		expected.put("Profile 3", getRandomSubList(available));
		expected.put("Profile 4", getRandomSubList(available));
		for (Entry<String, List<ITacticDescriptor>> entry : expected.entrySet()) {
			tactics1.add(entry.getKey(), entry.getValue());
		}
		tactics1.store();

		// tactics2 is used to read and compare the preference
		final TacticsProfilesCache tactics2 = new TacticsProfilesCache(store);
		tactics2.load();
		final List<IPrefMapEntry<List<ITacticDescriptor>>> actual = tactics2
				.getEntries();
		assertEquals("The number of stored profiles is not correct",
				expected.size(), actual.size());
		for (IPrefMapEntry<List<ITacticDescriptor>> profile : actual) {
			assertList(expected.get(profile.getKey()), profile.getValue(),
					profile.getKey());
		}
	}

	/**
	 * Test the read and write operations in preference store of a profile list.
	 * <p>
	 * Modifies and stores an existing list of profile in preference store. Then
	 * ensures the read profiles are equal.
	 * </p>
	 * <p>
	 * The modification is adding profiles and changes the tactic list of
	 * existing profiles
	 * </p>
	 * */
	public void testProfilesList2() {
		final IPreferenceStore store = EventBUIPlugin.getDefault()
				.getPreferenceStore();
		// tactics1 is used to store the preference
		final TacticsProfilesCache tactics1 = new TacticsProfilesCache(store);
		final Collection<ITacticDescriptor> available = TacticPreferenceUtils
				.getAvailableTactics();
		final Map<String, List<ITacticDescriptor>> expected = new HashMap<String, List<ITacticDescriptor>>();
		expected.put("Profile 1", getRandomSubList(available));
		expected.put("Profile 2", getRandomSubList(available));
		expected.put("Profile 3", getRandomSubList(available));
		expected.put("Profile 4", getRandomSubList(available));
		for (Entry<String, List<ITacticDescriptor>> entry : expected.entrySet()) {
			tactics1.add(entry.getKey(), entry.getValue());
		}
		tactics1.store();

		// tactics2 is used to modify the existing preference
		final TacticsProfilesCache tactics2 = new TacticsProfilesCache(store);
		tactics2.load();
		// modify an existing profile
		final List<ITacticDescriptor> storedList = tactics2.getEntry(
				"Profile 2").getValue();
		storedList.remove(0);
		expected.put("Profile 2", storedList);
		tactics2.getEntry("Profile 2").setValue(expected.get("Profile 2"));
		// add Profiles
		expected.put("Profile 5", getRandomSubList(available));
		expected.put("Profile 6", getRandomSubList(available));
		expected.put("Profile 7", getRandomSubList(available));
		tactics2.add("Profile 5", expected.get("Profile 5"));
		tactics2.add("Profile 6", expected.get("Profile 6"));
		tactics2.add("Profile 7", expected.get("Profile 7"));
		tactics2.store();

		// ensure the tactics list of profile 2 is changed
		final List<ITacticDescriptor> list1 = tactics1.getEntry("Profile 2")
				.getValue();
		final List<ITacticDescriptor> list2 = tactics2.getEntry("Profile 2")
				.getValue();
		assertFalse("The tactics list should not be equals",
				list1.equals(list2));

		// tactics3 is used to read and compare the preference
		final TacticsProfilesCache tactics3 = new TacticsProfilesCache(store);
		tactics3.load();
		final List<IPrefMapEntry<List<ITacticDescriptor>>> actual = tactics3
				.getEntries();
		assertEquals("The number of stored profiles is not correct",
				expected.size(), actual.size());
		for (IPrefMapEntry<List<ITacticDescriptor>> profile : actual) {
			assertList(expected.get(profile.getKey()), profile.getValue(),
					profile.getKey());
		}
	}

	private void assertList(List<ITacticDescriptor> expected,
			List<ITacticDescriptor> actual, String name) {
		assertEquals("The number of tactics is not equals", expected.size(),
				actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals("The tactics list of " + name + " should be equals",
					actual.get(i), expected.get(i));
		}
	}

	/**
	 * Returns a sublist of a given list. The size of the list and the order of
	 * elements are chosen randomly.
	 * 
	 * @param available
	 *            a list of {@link ITacticDescriptor}
	 * @return a sublist of available tactic descriptors.
	 */
	private List<ITacticDescriptor> getRandomSubList(
			Collection<ITacticDescriptor> available) {
		final List<ITacticDescriptor> list = new ArrayList<ITacticDescriptor>();
		list.addAll(available);
		Collections.shuffle(list);
		// random integer in 0 .. length of the list
		final int index = abs(new Random(new Date().getTime()).nextInt()
				% list.size());
		return list.subList(0, index);
	}

	/**
	 * Sets a project specific profile, and verifies that this is the profile
	 * used to build the tactic. Then erases the property, modifies the
	 * workspace property and checks that the profile used is the workspace's
	 * one.
	 */
	public void testWorkspaceProjectSpecificDistinction() throws Exception {
		final IContextRoot c = createContext("c");
		final IProject p = c.getRodinProject().getProject();
		final IAutoPostTacticManager manager = EventBPlugin
				.getAutoPostTacticManager();
		final IAutoTacticPreference autoTac = manager.getAutoTacticPreference();
		final IAutoTacticPreference postTac = manager.getPostTacticPreference();
		
		// Creates the project scoped preference store
		final ProjectScope scope = new ProjectScope(p);
		final IEclipsePreferences node = scope
		.getNode(EventBUIPlugin.PLUGIN_ID);
		final ScopedPreferenceStore scStore = new ScopedPreferenceStore(scope,
				EventBUIPlugin.PLUGIN_ID);

		
		final String prjProfileName = "Profile 1";
		// Saving project profile
		final List<ITacticDescriptor> prjDescs = getTacticDescList(autoTac, prjTacticIDs);
		storeProfile(scStore, prjDescs, prjProfileName);
		scStore.setValue(TacticPreferenceConstants.P_AUTOTACTIC_CHOICE,	prjProfileName);
		scStore.setValue(TacticPreferenceConstants.P_POSTTACTIC_CHOICE,	prjProfileName);
		
		// We load the auto tactic
		manager.getSelectedAutoTactics(c);
		manager.getSelectedPostTactics(c);
		
		final List<ITacticDescriptor> projectAutoSelected = getSelectedDescs((AutoTacticPreference) autoTac);
		final List<ITacticDescriptor> projectPostSelected = getSelectedDescs((AutoTacticPreference) postTac);
		
		// We check that the selected profile is the project one
		assertList(prjDescs, projectAutoSelected, prjProfileName);
		assertList(prjDescs, projectPostSelected, prjProfileName);
		
		// WE CLEAR ALL PROJECT PROPERTIES!
		// Now the workspace preferences shall be used
		PreferenceUtils.clearAllProperties(node.name(), p);
		
		final IPreferenceStore wsStore = EventBUIPlugin.getDefault().getPreferenceStore();
		
		final String wsProfileName = "WSProfile 1";
		// Saving workspace profile
		final List<ITacticDescriptor> wsDescs = getTacticDescList(autoTac, wsTacticIDs);
		storeProfile(wsStore, wsDescs, wsProfileName);
		wsStore.setValue(TacticPreferenceConstants.P_AUTOTACTIC_CHOICE,	wsProfileName);
		scStore.setValue(TacticPreferenceConstants.P_POSTTACTIC_CHOICE,	wsProfileName);

		// We load the auto tactic
		manager.getSelectedAutoTactics(c);
		final List<ITacticDescriptor> wsSelected = getSelectedDescs((AutoTacticPreference) autoTac);
		// We check that the selected profile is the workspace one
		assertList(wsDescs, wsSelected, wsProfileName);
	}
	
	/**
	 * Stores a profile with the given name composed by the given
	 * tacticDescritors in the given store.
	 * 
	 * @param store
	 *            the store used by the tactic cache
	 * @param descs
	 *            the descriptors that shall compose the profile
	 * @param profileName
	 *            the profile name
	 */
	private void storeProfile(IPreferenceStore store,
			List<ITacticDescriptor> descs, String profileName) {
		final TacticsProfilesCache tactics = new TacticsProfilesCache(store);
		final Map<String, List<ITacticDescriptor>> wsProfiles = new HashMap<String, List<ITacticDescriptor>>();
		wsProfiles.put(profileName, descs);
		for (Entry<String, List<ITacticDescriptor>> entry : wsProfiles
				.entrySet()) {
			tactics.add(entry.getKey(), entry.getValue());
		}
		tactics.store();
	}
	
	/**
	 * Returns the list of tactic descriptors corresponding to the tactic ids
	 * given as parameters. They are retrieved from the set of declared tactic
	 * descriptors of the given tactic preference.
	 * 
	 * @param tac
	 *            the tactic preference used to search for declared tactic IDs
	 * @param ids
	 *            the ids of tactics that we want the descriptors for
	 * @return a list of tactic descriptors
	 */
	private List<ITacticDescriptor> getTacticDescList(
			IAutoTacticPreference tac, String[] ids) {
		final Collection<ITacticDescriptor> declaredDescs = tac
				.getDeclaredDescriptors();
		final List<ITacticDescriptor> selected = new ArrayList<ITacticDescriptor>();
		for (ITacticDescriptor desc : declaredDescs) {
			if (Arrays.asList(ids).contains(desc.getTacticID())) {
				selected.add(desc);
			}
		}
		return selected;
	}
	
	/**
	 * Get the list of selected descriptors of an {@link AutoTacticPreference}.
	 * 
	 * @param autopref
	 * 		the autoTacticPreference to retrieve the selected descriptors from
	 */
	@SuppressWarnings("unchecked")
	private List<ITacticDescriptor> getSelectedDescs(
			AutoTacticPreference autopref) throws Exception {
		final Class<AutoTacticPreference> clazz = AutoTacticPreference.class;
		final Field field = clazz.getDeclaredField("selectedDescriptors");
		field.setAccessible(true);
		return (List<ITacticDescriptor>) field.get(autopref);
	}
	
}
