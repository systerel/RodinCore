/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored to use ITacticProvider2 and ITacticApplication
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import static java.util.Collections.unmodifiableSet;
import static org.eventb.ui.EventBUIPlugin.PLUGIN_ID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.prover.registry.DropdownInfo;
import org.eventb.internal.ui.prover.registry.ExtensionParser;
import org.eventb.internal.ui.prover.registry.ProofCommandInfo;
import org.eventb.internal.ui.prover.registry.TacticProviderInfo;
import org.eventb.internal.ui.prover.registry.TacticUIInfo;
import org.eventb.internal.ui.prover.registry.ToolbarInfo;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Registry of all tactic and proof command contributions to the prover UI.
 * <p>
 * This registry is implemented as a singleton immutable class, which ensures
 * thread-safety. The extension point is analyzed when this class gets loaded by
 * the JVM, which happens the first time that {{@link #getDefault()} is called.
 * </p>
 * 
 * @author Thai Son Hoang
 */
public class TacticUIRegistry {

	// The identifier of the extension point (value
	// <code>"org.eventb.ui.proofTactics"</code>).
	public static final String PROOFTACTICS_ID = PLUGIN_ID + ".proofTactics"; //$NON-NLS-1$

	private static final String TARGET_ANY = "any";

	private static final String TARGET_GOAL = "goal";

	private static final String TARGET_HYPOTHESIS = "hypothesis";

	public static final String TARGET_GLOBAL = "global"; // FIXME why public

	// The static instance of this singleton class
	private static final TacticUIRegistry instance = new TacticUIRegistry();

	// The registry stored Element UI information
	private final Map<String, TacticProviderInfo> goalTacticRegistry;
	private final Map<String, ProofCommandInfo> goalCommandRegistry;

	private final Map<String, TacticProviderInfo> hypothesisTacticRegistry;
	private final Map<String, ProofCommandInfo> hypothesisCommandRegistry;

	private final Map<String, TacticProviderInfo> anyTacticRegistry;
	private final Map<String, ProofCommandInfo> anyCommandRegistry;

	private final Map<String, TacticUIInfo> globalRegistry;

	private final Map<String, ToolbarInfo> toolbarRegistry;

	private final Map<String, DropdownInfo> dropdownRegistry;

	/**
	 * The unique instance of this class is created when initializing the static
	 * final field "instance", thus in a thread-safe manner.
	 */
	private TacticUIRegistry() {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg.getExtensionPoint(PROOFTACTICS_ID);
		IConfigurationElement[] configurations = extensionPoint
				.getConfigurationElements();

		final ExtensionParser parser = new ExtensionParser();
		parser.parse(configurations);
		final IStatus status = parser.getStatus();
		if (!status.isOK()) {
			UIUtils.log(status);
		}

		goalTacticRegistry = parser.getGoalTacticRegistry();
		goalCommandRegistry = parser.getGoalCommandRegistry();
		hypothesisTacticRegistry = parser.getHypothesisTacticRegistry();
		hypothesisCommandRegistry = parser.getHypothesisCommandRegistry();
		anyTacticRegistry = parser.getAnyTacticRegistry();
		anyCommandRegistry = parser.getAnyCommandRegistry();
		globalRegistry = parser.getGlobalRegistry();
		toolbarRegistry = parser.getToolbarRegistry();
		dropdownRegistry = parser.getDropdownRegistry();

		if (ProverUIUtils.DEBUG) {
			show(goalTacticRegistry, "goalTacticRegistry");
			show(goalCommandRegistry, "goalCommandRegistry");
			show(hypothesisTacticRegistry, "hypothesisTacticRegistry");
			show(hypothesisCommandRegistry, "hypothesisCommandRegistry");
			show(anyTacticRegistry, "anyTacticRegistry");
			show(anyCommandRegistry, "anyCommandRegistry");
			show(globalRegistry, "globalRegistry");
			show(toolbarRegistry, "toolbarRegistry");
			show(dropdownRegistry, "dropdownRegistry");
		}
	}

	private void show(Map<String, ?> registry, String name) {
		System.out.println("Contents of registry : " + name + ":");
		for (final String id : registry.keySet()) {
			System.out.println("\t" + id);
		}
	}

	/**
	 * Returns the unique instance of this registry. The instance of this class
	 * is lazily constructed at class loading time.
	 * 
	 * @return the unique instance of this registry
	 */
	public static TacticUIRegistry getDefault() {
		return instance;
	}

	// FIXME duplicated code with ExtensionParser
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

	// FIXME duplicated code with ExtensionParser
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

	public List<ITacticApplication> getTacticApplicationsToGoal(IUserSupport us) {
		final List<ITacticApplication> result = new ArrayList<ITacticApplication>();

		for (TacticProviderInfo info : goalTacticRegistry.values()) {
			final List<ITacticApplication> applications = info
					.getApplicationsToGoal(us);
			result.addAll(applications);
		}
		for (TacticProviderInfo info : anyTacticRegistry.values()) {
			final List<ITacticApplication> applications = info
					.getApplicationsToGoal(us);
			result.addAll(applications);
		}
		return result;
	}

	public List<ICommandApplication> getCommandApplicationsToGoal(
			IUserSupport us) {
		final List<ICommandApplication> result = new ArrayList<ICommandApplication>();

		for (ProofCommandInfo info : goalCommandRegistry.values()) {
			if (info.isApplicable(us, null, null)) {
				result.add(info.getCommandApplication());
			}
		}
		for (ProofCommandInfo info : anyCommandRegistry.values()) {
			if (info.isApplicable(us, null, null)) {
				result.add(info.getCommandApplication());
			}
		}
		return result;
	}

	public List<ITacticApplication> getTacticApplicationsToHypothesis(
			IUserSupport us, Predicate hyp) {
		final List<ITacticApplication> result = new ArrayList<ITacticApplication>();

		for (TacticProviderInfo info : hypothesisTacticRegistry.values()) {
			final List<ITacticApplication> applications = info
					.getApplicationsToHypothesis(us, hyp);
			result.addAll(applications);
		}
		for (TacticProviderInfo info : anyTacticRegistry.values()) {
			final List<ITacticApplication> applications = info
					.getApplicationsToHypothesis(us, hyp);
			result.addAll(applications);
		}
		return result;

	}

	public List<ICommandApplication> getCommandApplicationsToHypothesis(
			IUserSupport us, Predicate hyp) {
		final List<ICommandApplication> result = new ArrayList<ICommandApplication>();

		for (ProofCommandInfo info : hypothesisCommandRegistry.values()) {
			if (info.isApplicable(us, null, null)) {
				result.add(info.getCommandApplication());

			}
		}
		for (ProofCommandInfo info : anyCommandRegistry.values()) {
			if (info.isApplicable(us, null, null)) {
				result.add(info.getCommandApplication());

			}
		}
		return result;
	}

	public Image getIcon(String tacticID) {
		final TacticUIInfo info = findInAnyTacticRegistry(tacticID);
		if (info != null)
			return info.getIcon();

		return null;
	}

	public String getTip(String tacticID) {
		final TacticUIInfo info = findInAnyTacticRegistry(tacticID);

		if (info != null)
			return info.getTooltip();

		return null;
	}

	public boolean isSkipPostTactic(String tacticID) {
		final TacticUIInfo info = findInAnyTacticRegistry(tacticID);
		if (info != null)
			return info.isSkipPostTactic();

		return false;
	}

	public boolean isInterruptable(String tacticID, String target) {
		TacticUIInfo info = findInTacticRegistry(tacticID, target);
		if (info != null)
			return info.isInterruptable();

		return false;
	}

	public Collection<String> getToolbars() {
		return unmodifiableSet(toolbarRegistry.keySet());
	}

	public Collection<String> getToolbarDropdowns(String toolbar) {
		ToolbarInfo info = toolbarRegistry.get(toolbar);
		if (info != null) {
			return info.getDropdowns();
		}

		return new ArrayList<String>(0);
	}

	public Collection<String> getDropdownTactics(String dropdownID) {
		DropdownInfo info = dropdownRegistry.get(dropdownID);
		if (info == null) {
			return Collections.emptyList();
		}

		return info.getTactics();
	}

	public Collection<String> getToolbarTactics(String toolbarID) {
		ToolbarInfo info = toolbarRegistry.get(toolbarID);
		if (info == null) {
			return Collections.emptyList();
		}

		return info.getTactics();
	}

	// returns a ITacticApplication or ICommandApplication if applicable
	// returns null if not applicable
	public Object getGlobalApplication(String tacticID, IUserSupport us,
			String globalInput) {
		final TacticUIInfo info = globalRegistry.get(tacticID);
		if (info instanceof TacticProviderInfo) {
			final List<ITacticApplication> applications = ((TacticProviderInfo) info)
					.getApplications(us, null, globalInput);
			// TODO document protocol in extension point
			switch (applications.size()) {
			case 0:
				// not applicable
				return null;
			case 1:
				// sole application
				return applications.get(0);
			default:
				// more than 1 application is ambiguous and forbidden by
				// protocol
				final String message = "could not provide global tactic application for tactic "
						+ tacticID
						+ "\nReason: unexpected number of applications: "
						+ applications.size();
				UIUtils.log(null, message);
				ProverUIUtils.debug(message);
				return null;
			}
		} else if (info instanceof ProofCommandInfo) {
			final ProofCommandInfo pcInfo = (ProofCommandInfo) info;
			if (pcInfo.isApplicable(us, null, globalInput)) {
				return pcInfo.getCommandApplication();
			}
		}
		return null;
	}

}
