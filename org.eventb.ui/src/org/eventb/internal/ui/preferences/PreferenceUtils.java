/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences;

import static org.eventb.internal.ui.preferences.PreferenceConstants.*;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementDesc;
import org.eventb.ui.EventBUIPlugin;
import org.osgi.service.prefs.BackingStoreException;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;

/**
 * This class contains some utility static methods that are used in this Event-B
 * User Interface plug-in by the preference mechanism.
 */
public class PreferenceUtils {

	/**
	 * Returns the default name prefix for a given element type provided by its
	 * contribution descriptor.
	 * 
	 * @param type
	 *            type of element for which a name prefix is wanted
	 */
	public static String getAutoNamePrefixFromDesc(IElementType<?> type) {
		final ElementDescRegistry registry = ElementDescRegistry.getInstance();
		final IElementDesc desc = registry.getElementDesc(type);
		return desc.getAutoNamePrefix();
	}

	/**
	 * Retrieves recursively a set of contributed internal elements and all
	 * their children, starting from a top level element given as parameter.
	 * 
	 * @param type
	 *            the element from which we start
	 * @return the set of contributed element types
	 */
	private static Set<IInternalElementType<?>> getContributedElementTypes(
			IElementType<?> type) {
		final ElementDescRegistry registry = ElementDescRegistry.getInstance();
		final IElementType<?>[] children = registry.getChildTypes(type);
		final Set<IInternalElementType<?>> result = new HashSet<IInternalElementType<?>>();
		
		for (IElementType<?> child : children) {
			final IElementDesc desc = registry.getElementDesc(child);
			if (child instanceof IInternalElementType<?>
					&& desc.getAutoNamePrefix().length() != 0)
				result.add((IInternalElementType<?>) child);
			// recursion
			result.addAll(getContributedElementTypes(child));
		}
		return result;
	}

	/**
	 * Retrieves a set of contributed internal element types, children of the
	 * context type.
	 * 
	 * @return a set of elements types children of a context element type
	 */
	public static Set<IInternalElementType<?>> getCtxElementsPrefixes() {
		return getContributedElementTypes(IContextRoot.ELEMENT_TYPE);
	}

	/**
     * Retrieves a set of contributed internal element types, children of the
	 * machine type.
	 * 
	 * @return a set of elements types children of a context element type
	 */
	public static Set<IInternalElementType<?>> getMchElementsPrefixes() {
		return getContributedElementTypes(IMachineRoot.ELEMENT_TYPE);
	}

	/**
	 * Returns the string describing a prefix preference key for a given element
	 * type
	 * 
	 * @param type
	 *            the element from which we want the preference
	 * @return the prefix preference key for the given type
	 */
	public static String getPrefixPreferenceKey(IInternalElementType<?> type) {
		return P_PREFIX + type.getId();
	}

	/**
	 * Returns the name prefix for an element described by its parent and
	 * element type.
	 * 
	 * @param parent
	 *            parent of element for which a name prefix is wanted
	 * @param type
	 *            type of element for which a name prefix is wanted
	 */
	public static String getAutoNamePrefix(IInternalElement parent,
			IInternalElementType<?> type) {
	
		final IProject prj = parent.getRodinFile().getRodinProject()
				.getProject();
		final String prefName = getPrefixPreferenceKey(type);
		final IEclipsePreferences pref = getProjectPreference(prj);
		if (pref != null) {
			final String projectSpecificPrefix = pref.get(prefName, "");
			if (projectSpecificPrefix.length() != 0) {
				return projectSpecificPrefix;
			}
		}
		// we try with the preference store
		final IPreferenceStore store = EventBUIPlugin.getDefault()
				.getPreferenceStore();
		final String prefPrefix = store.getString(prefName);
		if (prefPrefix.length() != 0)
			return prefPrefix;
		// we finally get the default prefix from the contribution,
		// this is a security but should never be called
		return getAutoNamePrefixFromDesc(type);
	}

	/**
	 * Update prefix for an element type in the EventBUI preference store.
	 * 
	 * @param type
	 *            type of element
	 * @param newPrefix
	 *            new name prefix or <code>null</code> to delete this property
	 */
	public static void setAutoNamePrefix(IInternalElementType<?> type,
			String newPrefix) {
		final IPreferenceStore store = EventBUIPlugin.getDefault()
				.getPreferenceStore();
		store.setValue(getPrefixPreferenceKey(type), newPrefix);
	}

	/**
	 * Clear all preference nodes related to the preference name given as
	 * parameter for a given project (i.e. clear the project properties).
	 * 
	 * @param prefName
	 *            the name of the preference we want to clear
	 * @param prj
	 *            the project on which the property is to clear
	 */
	public static void clearAllProperties(String prefName, IProject prj) {
		final IScopeContext sc = new ProjectScope(prj);
		final IEclipsePreferences cnode = sc.getNode(prefName);
		try {
			for (final String childName : cnode.childrenNames()) {
				clearAllProperties(childName, prj);
			}
			cnode.clear();
		} catch (BackingStoreException e) {
			// no need to bother the user with that
		}
	}

	/**
	 * Changes locally to a given project, the name prefix for an element type.
	 * 
	 * @param project
	 *            the project which will hold the property
	 * @param type
	 *            type of element
	 * @param newPrefix
	 *            new name prefix or <code>null</code> to delete this property
	 */
	public static void setAutoNamePrefix(IProject project,
			IInternalElementType<?> type, String newPrefix) {
		final IEclipsePreferences pref = getProjectPreference(project);
		if (pref != null) {
			pref.put(getPrefixPreferenceKey(type), newPrefix);
		}
	}

	private static IEclipsePreferences getProjectPreference(IProject project) {
		final IScopeContext sc = new ProjectScope(project);
		return sc.getNode(PREFIX_PREFERENCE_PAGE_ID);
	}

	/**
	 * Sets the default values in the given preference store.
	 * 
	 * @param store
	 *            a preference store
	 */
	public static void setDefaultPreferences(IPreferenceStore store) {
		setDefaultPreferences(store, getCtxElementsPrefixes());
		setDefaultPreferences(store, getMchElementsPrefixes());
	}

	private static void setDefaultPreferences(IPreferenceStore store,
			Set<IInternalElementType<?>> types) {
		for (final IInternalElementType<?> type : types) {
			final String key = getPrefixPreferenceKey(type);
			final String value = getAutoNamePrefixFromDesc(type);
			store.setDefault(key, value);
		}
	}

}
