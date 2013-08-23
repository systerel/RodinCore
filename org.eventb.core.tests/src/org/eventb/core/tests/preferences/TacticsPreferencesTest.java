/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.preferences;

import static java.lang.Math.abs;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferenceNodeVisitor;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextRoot;
import org.eventb.core.preferences.CachedPreferenceMap;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.preferences.ListPreference;
import org.eventb.core.preferences.autotactics.IAutoPostTacticManager;
import org.eventb.core.preferences.autotactics.ITacticProfileCache;
import org.eventb.core.preferences.autotactics.TacticPreferenceConstants;
import org.eventb.core.preferences.autotactics.TacticPreferenceFactory;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;
import org.eventb.core.seqprover.IParamTacticDescriptor;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.autoTacticPreference.AutoTacticPreference;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.eventb.core.seqprover.eventbExtensions.TacticCombinators.LoopOnAllPending;
import org.eventb.core.tests.BuilderTest;
import org.eventb.internal.core.preferences.TacticPrefElement;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.prefs.BackingStoreException;

public class TacticsPreferencesTest extends BuilderTest {

	private static ITacticProfileCache makeTacticProfileCache() {
		return TacticPreferenceFactory
				.makeTacticProfileCache(InstanceScope.INSTANCE
						.getNode(store));
	}

	/**
	 * Clear all preference nodes related to the preference name given as
	 * parameter for a given project (i.e. clear the project properties).
	 * 
	 * @param cnode
	 *            the preference preference node we want to clear
	 * @param prj
	 *            the project on which the property is to clear
	 */
	public static void clearAllProperties(IEclipsePreferences cnode,
			IProject prj) {
		try {
			cnode.accept(new IPreferenceNodeVisitor() {

				@Override
				public boolean visit(IEclipsePreferences node)
						throws BackingStoreException {
					node.clear();
					node.flush();
					return true;
				}
			});
		} catch (BackingStoreException e) {
			// no need to bother the user with that
		}
	}

	private static final String[] prjTacticIDs = {
			"org.eventb.core.seqprover.trueGoalTac",
			"org.eventb.core.seqprover.falseHypTac", };

	private static final String[] wsTacticIDs = {
			"org.eventb.core.seqprover.goalInHypTac",
			"org.eventb.core.seqprover.funGoalTac", };

	private static final String store = "org.eventb.core.tests";
	private static final String uiStore = "org.eventb.ui";

	private static void resetPreference(String qualifier)
			throws BackingStoreException {
		final IEclipsePreferences node = InstanceScope.INSTANCE
				.getNode(qualifier);
		if (node != null) {
			node.removeNode();
		}
	}

	@Before
	public void resetPreferences() throws BackingStoreException {
		resetPreference(store);
		resetPreference(uiStore);
	}

	public static Collection<ITacticDescriptor> getAvailableTactics() {
		final IAutoTacticRegistry autoTacticRegistry = SequentProver.getAutoTacticRegistry();
		final String[] registeredIDs = autoTacticRegistry.getRegisteredIDs();
		final Collection<ITacticDescriptor> result = new ArrayList<ITacticDescriptor>();

		for (String autoTacticID : registeredIDs) {
			result.add(autoTacticRegistry.getTacticDescriptor(autoTacticID));
		}
		return result;
	}

	/**
	 * Test the read and write operations in preference store of a profile list.
	 * <p>
	 * Create and store a new list of profile in preference store. Then ensure
	 * the read profile are equals.
	 * </p>
	 */
	@Test
	public void testProfilesList1() {
		// tactics1 is used to store the preference
		final ITacticProfileCache tactics1 = makeTacticProfileCache();
		final Collection<ITacticDescriptor> available = getAvailableTactics();
		final Map<String, ITacticDescriptor> expected = new HashMap<String, ITacticDescriptor>();
		expected.put("Profile 1", getRandomSubList(available));
		expected.put("Profile 2", getRandomSubList(available));
		expected.put("Profile 3", getRandomSubList(available));
		expected.put("Profile 4", getRandomSubList(available));
		for (Entry<String, ITacticDescriptor> entry : expected.entrySet()) {
			tactics1.add(entry.getKey(), entry.getValue());
		}
		tactics1.store();

		// tactics2 is used to read and compare the preference
		final ITacticProfileCache tactics2 = makeTacticProfileCache();
		tactics2.load();
		final List<IPrefMapEntry<ITacticDescriptor>> actual = tactics2
				.getEntries();
		assertEquals("The number of stored profiles is not correct",
				expected.size(), actual.size());
		for (IPrefMapEntry<ITacticDescriptor> profile : actual) {
			assertTacDesc(expected.get(profile.getKey()), profile.getValue());
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
	@Test
	public void testProfilesList2() {
		// tactics1 is used to store the preference
		final ITacticProfileCache tactics1 = makeTacticProfileCache();
		final Collection<ITacticDescriptor> available = getAvailableTactics();
		final Map<String, ITacticDescriptor> expected = new HashMap<String, ITacticDescriptor>();
		expected.put("Profile 1", getRandomSubList(available));
		expected.put("Profile 2", getRandomSubList(available));
		expected.put("Profile 3", getRandomSubList(available));
		expected.put("Profile 4", getRandomSubList(available));
		for (Entry<String, ITacticDescriptor> entry : expected.entrySet()) {
			tactics1.add(entry.getKey(), entry.getValue());
		}
		tactics1.store();

		// tactics2 is used to modify the existing preference
		final ITacticProfileCache tactics2 = makeTacticProfileCache();
		tactics2.load();
		// modify an existing profile
		final ICombinedTacticDescriptor storedList = (ICombinedTacticDescriptor) tactics2
				.getEntry("Profile 2").getValue();
		final ITacticDescriptor modified = removeFirst(storedList);
		expected.put("Profile 2", modified);
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
		final List<ITacticDescriptor> list1 = ((ICombinedTacticDescriptor) tactics1
				.getEntry("Profile 2").getValue()).getCombinedTactics();
		final List<ITacticDescriptor> list2 = ((ICombinedTacticDescriptor) tactics2
				.getEntry("Profile 2").getValue()).getCombinedTactics();
		assertFalse("The tactics list should not be equals",
				list1.equals(list2));

		// tactics3 is used to read and compare the preference
		final ITacticProfileCache tactics3 = makeTacticProfileCache();
		tactics3.load();
		final List<IPrefMapEntry<ITacticDescriptor>> actual = tactics3
				.getEntries();
		assertEquals("The number of stored profiles is not correct",
				expected.size(), actual.size());
		for (IPrefMapEntry<ITacticDescriptor> profile : actual) {
			assertTacDesc(expected.get(profile.getKey()), profile.getValue());
		}
	}

	private static ICombinedTacticDescriptor removeFirst(
			ICombinedTacticDescriptor desc) {
		final String combinatorId = desc.getCombinatorId();
		final ICombinatorDescriptor combinator = SequentProver
				.getAutoTacticRegistry().getCombinatorDescriptor(combinatorId);
		final List<ITacticDescriptor> tactics = desc.getCombinedTactics();
		final List<ITacticDescriptor> modified = new ArrayList<ITacticDescriptor>(
				tactics);
		modified.remove(0);
		return combinator.combine(modified, "modified");
	}

	@Test
	public void testStoreLoadDeepCombined() throws Exception {
		final List<ITacticDescriptor> available = new ArrayList<ITacticDescriptor>(
				getAvailableTactics());
		final ITacticDescriptor desc = loopOnAllPending(asList(
				loopOnAllPending(asList(available.get(0), available.get(1))),
				available.get(2)));
		final Map<String, ITacticDescriptor> expected = new HashMap<String, ITacticDescriptor>();
		final String name = "Profile deep loop";
		expected.put(name, desc);

		assertStoreLoad(expected);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testRecoverOldStorage() throws Exception {

		final IAutoPostTacticManager manager = EventBPlugin
				.getAutoPostTacticManager();
		final IAutoTacticPreference autoTac = manager.getAutoTacticPreference();

		final String dftAutoProfileName = "Default Auto Tactic Profile";
		final ICombinedTacticDescriptor defaultDescriptor = (ICombinedTacticDescriptor) autoTac
				.getDefaultDescriptor();
		final List<ITacticDescriptor> defaultDescriptors = defaultDescriptor
				.getCombinedTactics();

		final CachedPreferenceMap<List<ITacticDescriptor>> oldMap = new CachedPreferenceMap<List<ITacticDescriptor>>(
				new ListPreference<ITacticDescriptor>(new TacticPrefElement()));

		oldMap.add(dftAutoProfileName, defaultDescriptors);

		final String oldPref = oldMap.extract();

		// store with old preference format
		InstanceScope.INSTANCE.getNode(uiStore).put(
				TacticPreferenceConstants.P_TACTICSPROFILES, oldPref);

		// load with a new format tactics cache, supposedly compatible
		final ITacticProfileCache newCache = makeTacticProfileCache();
		newCache.load();

		final IPrefMapEntry<ITacticDescriptor> newEntry = newCache
				.getEntry(dftAutoProfileName);
		assertNotNull(newEntry);
		final ITacticDescriptor newDesc = newEntry.getValue();

		// verify descriptor
		assertTacDesc(defaultDescriptor, newDesc);

		// verify no store/load issues afterwards
		final Map<String, ITacticDescriptor> expected = new HashMap<String, ITacticDescriptor>();
		expected.put(dftAutoProfileName, defaultDescriptor);
		assertStoreLoad(expected);
	}

	private static void assertStoreLoad(
			final Map<String, ITacticDescriptor> expected) {
		// tactics1 is used to store the preference
		final ITacticProfileCache tactics1 = makeTacticProfileCache();
		for (Entry<String, ITacticDescriptor> entry : expected.entrySet()) {
			tactics1.add(entry.getKey(), entry.getValue());
		}
		tactics1.store();

		// tactics2 is used to modify the existing preference
		final ITacticProfileCache tactics2 = makeTacticProfileCache();
		tactics2.load();
		final List<IPrefMapEntry<ITacticDescriptor>> actual = tactics2
				.getEntries();
		assertEquals("The number of stored profiles is not correct",
				expected.size(), actual.size());
		for (IPrefMapEntry<ITacticDescriptor> profile : actual) {
			assertTacDesc(expected.get(profile.getKey()), profile.getValue());
		}
	}

	private static void assertTacDesc(ITacticDescriptor expectedDesc,
			ITacticDescriptor actualDesc) {
		if (expectedDesc instanceof ICombinedTacticDescriptor) {
			// neglect tactic id to allow recovery
			assertTrue(actualDesc instanceof ICombinedTacticDescriptor);
			assertCombDesc((ICombinedTacticDescriptor) expectedDesc,
					(ICombinedTacticDescriptor) actualDesc);
		} else {
			assertEquals(expectedDesc.getTacticID(), actualDesc.getTacticID());
			if (expectedDesc instanceof IParamTacticDescriptor) {
				assertTrue(actualDesc instanceof IParamTacticDescriptor);
				assertParamDesc((IParamTacticDescriptor) expectedDesc,
						(IParamTacticDescriptor) actualDesc);
			}
		}
	}

	private static void assertCombDesc(ICombinedTacticDescriptor expectedDesc,
			ICombinedTacticDescriptor actualDesc) {
		final List<ITacticDescriptor> expected = expectedDesc
				.getCombinedTactics();
		final List<ITacticDescriptor> actual = actualDesc.getCombinedTactics();

		assertEquals("Wrong size of combined tactics", expected.size(),
				actual.size());
		final List<String> expIds = getIds(expected);
		final List<String> actIds = getIds(actual);
		assertEquals(expIds, actIds);
	}

	private static List<String> getIds(List<ITacticDescriptor> descs) {
		final List<String> result = new ArrayList<String>(descs.size());
		for (ITacticDescriptor desc : descs) {
			result.add(desc.getTacticID());
		}
		return result;
	}

	private static void assertParamDesc(IParamTacticDescriptor expectedDesc,
			IParamTacticDescriptor actualDesc) {
		assertEquals(expectedDesc.getParameterizerId(),
				actualDesc.getParameterizerId());
		assertEquals(expectedDesc.getValuation(),
				actualDesc.getParameterizerId());
	}

	/**
	 * Returns a sublist of a given list. The size of the list and the order of
	 * elements are chosen randomly.
	 * 
	 * @param available
	 *            a list of {@link ITacticDescriptor}
	 * @return a sublist of available tactic descriptors.
	 */
	private ITacticDescriptor getRandomSubList(
			Collection<ITacticDescriptor> available) {
		final List<ITacticDescriptor> list = new ArrayList<ITacticDescriptor>();
		list.addAll(available);
		Collections.shuffle(list);
		// random integer in 2 .. length of the list
		int index = abs(new Random(new Date().getTime()).nextInt()
				% list.size());
		index = Math.max(index, 2);
		final List<ITacticDescriptor> subList = list.subList(0, index);

		return loopOnAllPending(subList);
	}

	private static ITacticDescriptor loopOnAllPending(
			List<ITacticDescriptor> tactics) {
		final ICombinatorDescriptor combinator = SequentProver
				.getAutoTacticRegistry().getCombinatorDescriptor(
						LoopOnAllPending.COMBINATOR_ID);
		return combinator.combine(tactics, "testLoop");
	}

	/**
	 * Sets a project specific profile, and verifies that this is the profile
	 * used to build the tactic. Then erases the property, modifies the
	 * workspace property and checks that the profile used is the workspace's
	 * one.
	 */
	@Test
	public void testWorkspaceProjectSpecificDistinction() throws Exception {
		final IContextRoot c = createContext("c");
		final IProject p = c.getRodinProject().getProject();
		final IAutoPostTacticManager manager = EventBPlugin
				.getAutoPostTacticManager();
		final IAutoTacticPreference autoTac = manager.getAutoTacticPreference();
		final IAutoTacticPreference postTac = manager.getPostTacticPreference();
		final IEclipsePreferences wsNode = InstanceScope.INSTANCE
				.getNode(EventBPlugin.PLUGIN_ID);

		// Creates the project scoped preference store
		final ProjectScope scope = new ProjectScope(p);
		final IEclipsePreferences projectNode = scope.getNode(EventBPlugin.PLUGIN_ID);

		final String prjProfileName = "Profile 1";
		// Saving project profile
		final ITacticDescriptor prjDesc = getTacticDescList(autoTac,
				prjTacticIDs);
		storeProfile(projectNode, prjDesc, prjProfileName);
		projectNode.put(TacticPreferenceConstants.P_AUTOTACTIC_CHOICE,
				 prjProfileName);
		projectNode.put(TacticPreferenceConstants.P_POSTTACTIC_CHOICE,
				 prjProfileName);
		projectNode.sync();

		// We load the auto tactic
		manager.getSelectedAutoTactics(c);
		manager.getSelectedPostTactics(c);

		final ICombinedTacticDescriptor projectAutoSelected = (ICombinedTacticDescriptor) getSelectedDesc(autoTac);
		final ICombinedTacticDescriptor projectPostSelected = (ICombinedTacticDescriptor) getSelectedDesc(postTac);

		// We check that the selected profile is the project one
		assertTacDesc(prjDesc, projectAutoSelected);
		assertTacDesc(prjDesc, projectPostSelected);

		// WE CLEAR ALL PROJECT PROPERTIES!
		// Now the workspace preferences shall be used
		clearAllProperties(projectNode, p);

		final String wsProfileName = "WSProfile 1";
		// Saving workspace profile
		final ITacticDescriptor wsDescs = getTacticDescList(autoTac,
				wsTacticIDs);
		storeProfile(wsNode, wsDescs, wsProfileName);
		projectNode.put(TacticPreferenceConstants.P_AUTOTACTIC_CHOICE, wsProfileName);
		projectNode.put(TacticPreferenceConstants.P_POSTTACTIC_CHOICE, wsProfileName);

		// We load the auto tactic
		manager.getSelectedAutoTactics(c);
		final ICombinedTacticDescriptor wsSelected = (ICombinedTacticDescriptor) getSelectedDesc(autoTac);
		// We check that the selected profile is the workspace one
		assertTacDesc(wsDescs, wsSelected);
	}

	/**
	 * Stores a profile with the given name composed by the given
	 * tacticDescritors in the given store.
	 * 
	 * @param descs
	 *            the descriptors that shall compose the profile
	 * @param profileName
	 *            the profile name
	 */
	private void storeProfile(IEclipsePreferences node, ITacticDescriptor descs, String profileName) {
		final ITacticProfileCache tactics = TacticPreferenceFactory
				.makeTacticProfileCache(node);
		final Map<String, ITacticDescriptor> wsProfiles = new HashMap<String, ITacticDescriptor>();
		wsProfiles.put(profileName, descs);
		for (Entry<String, ITacticDescriptor> entry : wsProfiles.entrySet()) {
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
	private ITacticDescriptor getTacticDescList(IAutoTacticPreference tac,
			String[] ids) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final List<ITacticDescriptor> selected = new ArrayList<ITacticDescriptor>();
		for (String tacticID : ids) {
			final ITacticDescriptor desc = reg.getTacticDescriptor(tacticID);
			selected.add(desc);
		}
		return loopOnAllPending(selected);
	}

	/**
	 * Get the list of selected descriptors of an {@link IAutoTacticPreference}.
	 * 
	 * @param autopref
	 *            the autoTacticPreference to retrieve the selected descriptors
	 *            from
	 */
	private ITacticDescriptor getSelectedDesc(IAutoTacticPreference autopref)
			throws Exception {
		final Class<AutoTacticPreference> clazz = AutoTacticPreference.class;
		final Field field = clazz.getDeclaredField("selectedDescriptor");
		field.setAccessible(true);
		return (ITacticDescriptor) field.get(autopref);
	}

	/**
	 * Bug #3189256. THIS IS A NON REGRESSION TEST. Ensures that the default
	 * profile for the auto tactic is used in case of project specific settings
	 * with a non-default workspace profile. This ensures that the default
	 * preference which is serialized at a project scope, is taken into
	 * consideration. Indeed, for a preference store, the absence of value means
	 * default value. With eclipse's preferences mechanism, no value means no
	 * preference.
	 */
	@Test
	public void testBug3189256() throws Exception {
		final IContextRoot c = createContext("c");
		final IProject p = c.getRodinProject().getProject();
		final IAutoPostTacticManager manager = EventBPlugin
				.getAutoPostTacticManager();
		final IAutoTacticPreference autoTac = manager.getAutoTacticPreference();

		// Creates the project scoped preference store
		final ProjectScope scope = new ProjectScope(p);
		final IEclipsePreferences prjNode = scope.getNode(EventBPlugin.PLUGIN_ID);

		final String dftAutoProfileName = "Default Auto Tactic Profile";
		final ICombinedTacticDescriptor defaultDescriptor = (ICombinedTacticDescriptor) autoTac
				.getDefaultDescriptor();
		final List<ITacticDescriptor> defaultDescriptors = defaultDescriptor
				.getCombinedTactics();
		final String[] defaultAutoDescs = new String[defaultDescriptors.size()];
		int i = 0;
		for (ITacticDescriptor td : defaultDescriptors) {
			defaultAutoDescs[i] = td.getTacticID();
			i++;
		}
		// Saving project profile
		final ITacticDescriptor defaultAuto = autoTac.getDefaultDescriptor();
		storeProfile(prjNode, defaultAuto, dftAutoProfileName);
		// **************************************************************
		// IMPORTANT ! Forces serialization of the choice at project scope
		prjNode.put(TacticPreferenceConstants.P_AUTOTACTIC_CHOICE,
				dftAutoProfileName);
		prjNode.sync();
		// **************************************************************

		final IEclipsePreferences wsNode = InstanceScope.INSTANCE
				.getNode(EventBPlugin.PLUGIN_ID);
		final String wsProfileName = "WSProfile 1";
		// Saving workspace profile
		final ITacticDescriptor wsDesc = getTacticDescList(autoTac, wsTacticIDs);
		storeProfile(wsNode, wsDesc, wsProfileName);
		wsNode.put(TacticPreferenceConstants.P_AUTOTACTIC_CHOICE,
				wsProfileName);
		wsNode.sync();
		// Verifies that the choice is "Default Auto Tactic Profile"
		final IPreferencesService preferencesService = Platform
				.getPreferencesService();
		final IScopeContext[] sc = { scope };
		final String choice = preferencesService.getString(
				EventBPlugin.PLUGIN_ID,
				TacticPreferenceConstants.P_AUTOTACTIC_CHOICE, null, sc);
		assertEquals("Project scope choice is invalid", dftAutoProfileName,
				choice);

		// We load the auto tactic
		manager.getSelectedAutoTactics(c);

		final ITacticDescriptor projectAutoSelected = getSelectedDesc(autoTac);
		// We check that the selected profile is the project specific one
		assertTacDesc(defaultDescriptor, projectAutoSelected);
	}

}
