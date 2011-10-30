/*******************************************************************************
 * Copyright (c) 2005, 2011 ETH Zurich and others.
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

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.internal.ui.prover.TacticUIRegistry;

/**
 * Utility class for parsing the extensions contributed to extension point
 * <code>org.eventb.ui.proofTactics</code>. This class is not thread-safe, but
 * this doesn't matter as it is only called during class initialization of
 * {@link TacticUIRegistry}.
 * 
 * @author Laurent Voisin
 * @see TacticUIRegistry
 */
public class ExtensionParser {

	private static final String TARGET_ANY = "any"; //$NON-NLS-0$

	private static final String TARGET_GOAL = "goal"; //$NON-NLS-0$

	private static final String TARGET_HYPOTHESIS = "hypothesis"; //$NON-NLS-0$

	private static final String TARGET_GLOBAL = "global"; //$NON-NLS-0$

	private final Map<String, TacticProviderInfo> goalTacticRegistry = new LinkedHashMap<String, TacticProviderInfo>();
	private final Map<String, ProofCommandInfo> goalCommandRegistry = new LinkedHashMap<String, ProofCommandInfo>();
	private final Map<String, TacticProviderInfo> hypothesisTacticRegistry = new LinkedHashMap<String, TacticProviderInfo>();
	private final Map<String, ProofCommandInfo> hypothesisCommandRegistry = new LinkedHashMap<String, ProofCommandInfo>();
	private final Map<String, TacticProviderInfo> anyTacticRegistry = new LinkedHashMap<String, TacticProviderInfo>();
	private final Map<String, ProofCommandInfo> anyCommandRegistry = new LinkedHashMap<String, ProofCommandInfo>();
	private final Map<String, TacticUIInfo> globalRegistry = new LinkedHashMap<String, TacticUIInfo>();
	private final Map<String, ToolbarInfo> toolbarRegistry = new LinkedHashMap<String, ToolbarInfo>();
	private final Map<String, DropdownInfo> dropdownRegistry = new LinkedHashMap<String, DropdownInfo>();

	public void parse(IConfigurationElement[] configurations) {
		for (IConfigurationElement configuration : configurations) {
			String id = configuration.getAttribute("id"); //$NON-NLS-1$
			if (id == null)
				continue;
			final String tacticStr = "tactic";
			if (configuration.getName().equals(tacticStr)) {
				// Check for duplication first
				String target = configuration.getAttribute("target");
				if (findInAnyTacticRegistry(id) != null) {
					printDebugConfExists(id, target + " " + tacticStr);
					continue;
				}

				TacticUILoader loader = new TacticUILoader(configuration);
				final TacticUIInfo info = loader.load();
				if (info != null) {
					putInRegistry(info, target);
					printDebugRegistration(id, tacticStr);
				}
			} else {
				final String toolbarStr = "toolbar";
				if (configuration.getName().equals(toolbarStr)) {
					ToolbarInfo oldInfo = toolbarRegistry.put(id,
							new ToolbarInfo(globalRegistry, dropdownRegistry,
									configuration));

					if (oldInfo != null) {
						toolbarRegistry.put(id, oldInfo);
						printDebugConfExists(id, toolbarStr);
					} else {
						printDebugRegistration(id, toolbarStr);
					}
				} else {
					final String dropdownStr = "dropdown";
					if (configuration.getName().equals(dropdownStr)) {
						DropdownInfo oldInfo = dropdownRegistry
								.put(id, new DropdownInfo(globalRegistry,
										configuration));

						if (oldInfo != null) {
							dropdownRegistry.put(id, oldInfo);
							printDebugConfExists(id, dropdownStr);
						} else {
							printDebugRegistration(id, dropdownStr);
						}
					}
				}
			}
		}
	}

	private void putInRegistry(TacticUIInfo info, String target) {
		final String id = info.getID();
		boolean error = false;
		if (target.equals(TARGET_GOAL)) {
			if (info instanceof TacticProviderInfo) {
				goalTacticRegistry.put(id, (TacticProviderInfo) info);
			} else if (info instanceof ProofCommandInfo) {
				goalCommandRegistry.put(id, (ProofCommandInfo) info);
			} else
				error = true;
		} else if (target.equals(TARGET_HYPOTHESIS)) {
			if (info instanceof TacticProviderInfo) {
				hypothesisTacticRegistry.put(id, (TacticProviderInfo) info);
			} else if (info instanceof ProofCommandInfo) {
				hypothesisCommandRegistry.put(id, (ProofCommandInfo) info);
			} else
				error = true;
		} else if (target.equals(TARGET_ANY)) {
			if (info instanceof TacticProviderInfo) {
				anyTacticRegistry.put(id, (TacticProviderInfo) info);
			} else if (info instanceof ProofCommandInfo) {
				anyCommandRegistry.put(id, (ProofCommandInfo) info);
			} else
				error = true;
		} else {
			globalRegistry.put(id, info);
		}
		if (error) {
			if (ProverUIUtils.DEBUG)
				ProverUIUtils.debug("Error while trying to put info " + id
						+ " in a registry");
		} else {
			printDebugRegistration(id, "tactic");
		}
	}

	private TacticUIInfo findInAnyTacticRegistry(String id) {
		TacticUIInfo info = findInTacticRegistry(id, TARGET_GOAL);
		if (info != null)
			return info;
		info = findInTacticRegistry(id, TARGET_HYPOTHESIS);
		if (info != null)
			return info;
		info = findInTacticRegistry(id, TARGET_ANY);
		if (info != null)
			return info;
		return findInTacticRegistry(id, TARGET_GLOBAL);
	}

	private TacticUIInfo findInTacticRegistry(String id, String target) {
		TacticUIInfo info;
		if (target.equals(TARGET_GOAL)) {
			info = goalTacticRegistry.get(id);
			if (info != null)
				return info;
			return goalCommandRegistry.get(id);
		}

		if (target.equals(TARGET_HYPOTHESIS)) {
			info = hypothesisTacticRegistry.get(id);
			if (info != null)
				return info;
			return hypothesisCommandRegistry.get(id);
		}

		if (target.equals(TARGET_ANY)) {
			info = anyTacticRegistry.get(id);
			if (info != null)
				return info;
			return anyCommandRegistry.get(id);
		}

		return globalRegistry.get(id);
	}

	private static void printDebugConfExists(String id, String kind) {
		if (ProverUIUtils.DEBUG)
			ProverUIUtils.debug("Configuration already exists for " + kind
					+ " " + id + ", configuration ignored.");
	}

	private static void printDebugRegistration(String id, String kind) {
		if (ProverUIUtils.DEBUG)
			ProverUIUtils.debug("Registered " + kind + " with id " + id);
	}

	public Map<String, TacticProviderInfo> getGoalTacticRegistry() {
		return goalTacticRegistry;
	}

	public Map<String, ProofCommandInfo> getGoalCommandRegistry() {
		return goalCommandRegistry;
	}

	public Map<String, TacticProviderInfo> getHypothesisTacticRegistry() {
		return hypothesisTacticRegistry;
	}

	public Map<String, ProofCommandInfo> getHypothesisCommandRegistry() {
		return hypothesisCommandRegistry;
	}

	public Map<String, TacticProviderInfo> getAnyTacticRegistry() {
		return anyTacticRegistry;
	}

	public Map<String, ProofCommandInfo> getAnyCommandRegistry() {
		return anyCommandRegistry;
	}

	public Map<String, TacticUIInfo> getGlobalRegistry() {
		return globalRegistry;
	}

	public Map<String, ToolbarInfo> getToolbarRegistry() {
		return toolbarRegistry;
	}

	public Map<String, DropdownInfo> getDropdownRegistry() {
		return dropdownRegistry;
	}

}
