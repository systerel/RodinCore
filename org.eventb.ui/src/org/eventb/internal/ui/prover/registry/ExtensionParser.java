/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored to use ITacticProvider2 and ITacticApplication
 *******************************************************************************/
package org.eventb.internal.ui.prover.registry;

import static org.eclipse.core.runtime.Status.OK_STATUS;
import static org.eventb.internal.ui.UIUtils.log;
import static org.eventb.internal.ui.prover.registry.ErrorStatuses.duplicateId;
import static org.eventb.internal.ui.prover.registry.ErrorStatuses.invalidId;
import static org.eventb.internal.ui.prover.registry.ErrorStatuses.invalidInstance;
import static org.eventb.internal.ui.prover.registry.ErrorStatuses.loadingErrors;
import static org.eventb.internal.ui.prover.registry.ErrorStatuses.missingId;
import static org.eventb.internal.ui.prover.registry.ErrorStatuses.unknownElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.ui.prover.IUIDynTacticProvider;

/**
 * Utility class for parsing the extensions contributed to extension point
 * <code>org.eventb.ui.proofTactics</code>. This class is not thread-safe, but
 * this doesn't matter as it is only called during class initialization of
 * {@link TacticUIRegistry}.
 * 
 * @author Laurent Voisin
 * @see TacticUIRegistry
 */
@SuppressWarnings("synthetic-access")
public class ExtensionParser {

	/**
	 * Implements a set of configuration elements to parse.
	 */
	private abstract class ElementSet {

		// Set of elements: the use of the LinkedHashMap ensures that the
		// order of insertion is retained. Keys are element ids.
		private final Map<String, IConfigurationElement> set = new LinkedHashMap<String, IConfigurationElement>();

		ElementSet() {
			// Do nothing
		}

		/*
		 * Adds a new element, checking that its id is well-formed and unique
		 * among the set. Erroneous elements are ignored (and an exception is
		 * thrown). Returns the id in case of success.
		 */
		public void add(IConfigurationElement element) {
			String id = element.getAttribute("id"); //$NON-NLS-1$

			// Check that the id is present and well-formed
			if (id == null) {
				errors.add(missingId(element));
				return;
			}
			if (id.length() == 0) {
				errors.add(invalidId(element));
				return;
			}

			// Raw id becomes qualified with the name space of the element
			if (id.indexOf('.') == -1) { //$NON-NLS-1$
				id = element.getNamespaceIdentifier() + "." + id; //$NON-NLS-1$
			}

			// Register the element, checking for uniqueness of id
			final IConfigurationElement oldElement = set.put(id, element);
			if (oldElement != null) {
				// Repair and ignore duplicate id
				set.put(id, oldElement);
				errors.add(duplicateId(element));
			}
		}

		/*
		 * Parse the elements of this set and register them appropriately
		 */
		public void parse() {
			for (final Map.Entry<String, IConfigurationElement> entry : set
					.entrySet()) {
				parse(entry.getKey(), entry.getValue());
			}
		}

		protected abstract void parse(String id, IConfigurationElement element);

	}

	private class TacticParser extends ElementSet {

		TacticParser() {
			// Do nothing
		}

		@Override
		protected void parse(String id, IConfigurationElement element) {
			final TacticUILoader loader = new TacticUILoader(id, element);
			final TacticUIInfo info = loader.load();
			if (info != null) {
				putInRegistry(info);
				printDebugRegistration(id, TACTIC_TAG);
			}
		}

	}

	private class DynamicDropdownParser extends ElementSet {

		DynamicDropdownParser() {
			// Do nothing
		}

		@Override
		protected void parse(String id, IConfigurationElement element) {
			try {
				final String name = element.getAttribute(NAME_TAG);
				final String toolbar = element.getAttribute(TOOLBAR_TAG);
				final Object extn = element
						.createExecutableExtension(TACTIC_PROVIDER_TAG);
				if (!(extn instanceof IUIDynTacticProvider)) {
					errors.add(invalidInstance(element, TACTIC_PROVIDER_TAG));
				}
				final IUIDynTacticProvider provider = (IUIDynTacticProvider) extn;
				dynDropdownRegistry.put(id, new DynamicDropdownInfo(id, name,
						toolbar, provider));
				printDebugRegistration(id, DYNAMIC_DROPDOWN_tag);
			} catch (CoreException e) {
				log(e, "while loading dynamic dropdown " + id);
			}
		}

	}

	private class DropdownParser extends ElementSet {

		DropdownParser() {
			// Do nothing
		}

		@Override
		protected void parse(String id, IConfigurationElement element) {
			final String toolbarId = element.getAttribute("toolbar");
			if (toolbarId == null || toolbarId.isEmpty()) {
				if (ProverUIUtils.DEBUG)
					ProverUIUtils.debug("Did NOT register dropdown with id " + id + ": missing toolbar ID");
				return;
			}
			dropdownRegistry.put(id, new DropdownInfo(globalRegistry, id,
					toolbarId));
			printDebugRegistration(id, DROPDOWN_TAG);
		}

	}

	private class ToolbarParser extends ElementSet {

		ToolbarParser() {
			// Do nothing
		}

		@Override
		protected void parse(String id, IConfigurationElement element) {
			toolbars.add(new ToolbarInfo(globalRegistry, dropdownRegistry,
					dynDropdownRegistry, id));
			printDebugRegistration(id, TOOLBAR_TAG);
		}

	}

	// Possible tags of extensions
	private static final String TACTIC_TAG = "tactic";
	private static final String TOOLBAR_TAG = "toolbar";
	private static final String DROPDOWN_TAG = "dropdown";
	private static final String DYNAMIC_DROPDOWN_tag = "dynamic_dropdown";
	private static final String TACTIC_PROVIDER_TAG = "tacticProvider";
	private static final String NAME_TAG = "name";

	private final List<TacticProviderInfo> goalTactics = new ArrayList<TacticProviderInfo>();
	private final List<TacticProviderInfo> hypothesisTactics = new ArrayList<TacticProviderInfo>();
	private final List<TacticProviderInfo> anyTactics = new ArrayList<TacticProviderInfo>();
	private final Map<String, TacticUIInfo> globalRegistry = new LinkedHashMap<String, TacticUIInfo>();
	private final Map<String, TacticUIInfo> allTacticRegistry = new HashMap<String, TacticUIInfo>();
	private final List<ToolbarInfo> toolbars = new ArrayList<ToolbarInfo>();
	private final Map<String, DropdownInfo> dropdownRegistry = new LinkedHashMap<String, DropdownInfo>();
	private final Map<String, DynamicDropdownInfo> dynDropdownRegistry = new HashMap<String, DynamicDropdownInfo>();

	private final List<IStatus> errors = new ArrayList<IStatus>();

	/*
	 * Configuration elements are processed in two phases. In the first phase,
	 * they are sorted by type. In the second phase, the final objects are built
	 * and registered in the appropriate data structures.
	 */
	public void parse(IConfigurationElement[] elements) {
		final ElementSet tacticSet = new TacticParser();
		final ElementSet dropdownSet = new DropdownParser();
		final ElementSet toolbarSet = new ToolbarParser();
		final ElementSet dynDropdownSet = new DynamicDropdownParser();

		for (final IConfigurationElement element : elements) {
			final String tag = element.getName();
			if (tag.equals(TACTIC_TAG)) {
				tacticSet.add(element);
			} else if (tag.equals(DROPDOWN_TAG)) {
				dropdownSet.add(element);
			} else if (tag.equals(TOOLBAR_TAG)) {
				toolbarSet.add(element);
			} else if (tag.equals(DYNAMIC_DROPDOWN_tag)) {
				dynDropdownSet.add(element);
			} else {
				errors.add(unknownElement(element));
			}
		}

		tacticSet.parse();
		mergeListsOfTactics();
		dropdownSet.parse();
		toolbarSet.parse();
		dynDropdownSet.parse();
	}

	/**
	 * Merge lists of tactics and commands.
	 * 
	 * This could be done in a simpler way, by not creating the any lists to
	 * start with. However the simpler solution would change the order of
	 * tactics and commands and thus break backward compatibility.
	 */
	private void mergeListsOfTactics() {
		goalTactics.addAll(anyTactics);
		hypothesisTactics.addAll(anyTactics);
	}

	public IStatus getStatus() {
		final int length = errors.size();
		if (length == 0) {
			return OK_STATUS;
		} else {
			final IStatus[] array = errors.toArray(new IStatus[length]);
			return loadingErrors(array);
		}
	}

	private void putInRegistry(TacticUIInfo info) {
		final String id = info.getID();
		allTacticRegistry.put(id, info);

		switch (info.getTarget()) {
		case goal:
			goalTactics.add((TacticProviderInfo) info);
			break;
		case hypothesis:
			hypothesisTactics.add((TacticProviderInfo) info);
			break;
		case any:
			anyTactics.add((TacticProviderInfo) info);
			break;
		case global:
			globalRegistry.put(id, info);
			break;
		}
		printDebugRegistration(id, TACTIC_TAG);
	}

	private static void printDebugRegistration(String id, String kind) {
		if (ProverUIUtils.DEBUG)
			ProverUIUtils.debug("Registered " + kind + " with id " + id);
	}

	public List<TacticProviderInfo> getGoalTactics() {
		return goalTactics;
	}

	public List<TacticProviderInfo> getHypothesisTactics() {
		return hypothesisTactics;
	}

	public Map<String, TacticUIInfo> getAllTacticRegistry() {
		return allTacticRegistry;
	}

	public List<ToolbarInfo> getToolbars() {
		return toolbars;
	}

	public Map<String, DropdownInfo> getDropdownRegistry() {
		return dropdownRegistry;
	}

	public List<DynamicDropdownInfo> getDynTacticRegistry() {
		return new ArrayList<DynamicDropdownInfo>(dynDropdownRegistry.values());
	}

}
