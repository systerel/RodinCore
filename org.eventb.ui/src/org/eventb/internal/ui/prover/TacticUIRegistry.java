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

import static org.eventb.ui.EventBUIPlugin.PLUGIN_ID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.prover.IProofCommand;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

public class TacticUIRegistry {

	// The identifier of the extension point (value
	// <code>"org.eventb.ui.proofTactics"</code>).
	private static final String PROOFTACTICS_ID = PLUGIN_ID + ".proofTactics";
	
	private static final String TARGET_ANY = "any";

	private static final String TARGET_GOAL = "goal";

	private static final String TARGET_HYPOTHESIS = "hypothesis";

	public static final String TARGET_GLOBAL = "global";  // FIXME why public

	// The static instance of this singleton class
	private static final TacticUIRegistry instance = new TacticUIRegistry();

	// The registry stored Element UI information
	private Map<String, TacticProviderInfo> goalTacticRegistry = null;
	private Map<String, ProofCommandInfo> goalCommandRegistry = null;

	private Map<String, TacticProviderInfo> hypothesisTacticRegistry = null;
	private Map<String, ProofCommandInfo> hypothesisCommandRegistry = null;

	private Map<String, TacticProviderInfo> anyTacticRegistry = null;
	private Map<String, ProofCommandInfo> anyCommandRegistry = null;
	
	Map<String, TacticUIInfo> globalRegistry = null;

	private Map<String, ToolbarInfo> toolbarRegistry = null;

	Map<String, DropdownInfo> dropdownRegistry = null;

	private static class TacticUILoader {

		// Configuration information related to the element
		private final IConfigurationElement configuration;


		public TacticUILoader(IConfigurationElement configuration) {
			this.configuration = configuration;
		}

		public TacticUIInfo load() {
			final String id = configuration.getAttribute("id"); //$NON-NLS-1$
			final ImageDescriptor iconDesc = getImageDesc();
			final boolean interrupt = configuration.getAttribute("interrupt")
					.equalsIgnoreCase("true");
			final String tooltip = configuration.getAttribute("tooltip"); //$NON-NLS-1$
			final int priority = getPriority(id);
			final String name = configuration.getAttribute("name");
			final String dropdown = getOptionalAttribute("dropdown");
			final String toolbar = getOptionalAttribute("toolbar");
			final String skipPostTacticStr = configuration
					.getAttribute("skipPostTactic");
			final boolean skipPostTactic = (skipPostTacticStr != null && skipPostTacticStr
					.equalsIgnoreCase("true"));

			final String tpAttribute = "tacticProvider";
			final String pcAttribute = "proofCommand";
			final String tacticProvider = getOptionalAttribute(tpAttribute);
			final String proofCommand = getOptionalAttribute(pcAttribute);

			if (!(tacticProvider != null ^ proofCommand != null)) {
				ProverUIUtils
						.debug("Either a tactic provider or a proof command should be set for extension: "
								+ id);
				return null;
			}

			final String instanceAttribute = tacticProvider == null ? pcAttribute
					: tpAttribute;
			final TacticUIInfo result;
			try {
				final Object candidate = configuration
				.createExecutableExtension(instanceAttribute);

				if (tacticProvider != null) {
					final ITacticProvider appliProvider = getAppliProvider(candidate, id, tooltip, iconDesc);
					if (appliProvider == null) {
						result = null;
					} else {
						result = new TacticProviderInfo(id, iconDesc,
								interrupt, tooltip, priority, name, dropdown,
								toolbar, skipPostTactic, appliProvider);
					}

				} else {
					if (!(candidate instanceof IProofCommand)) {
						result = null;
					} else {
						result = new ProofCommandInfo(id, iconDesc, interrupt,
								tooltip, priority, name, dropdown, toolbar,
								skipPostTactic, (IProofCommand) candidate);
					}
				}
				printDebugInfo(tacticProvider, result, id);
				return result;
			} catch (CoreException e) {
				if (ProverUIUtils.DEBUG) {
					ProverUIUtils.debug("Cannot instantiate class from "
							+ instanceAttribute + " for tactic " + id);
					e.printStackTrace();
				}
				return null;
			}

		}


		private String getOptionalAttribute(String attribute) {
			final String value = configuration.getAttribute(attribute);
			if (value == null || value.length() == 0) {
				return null;
			}
			return value;
		}

		private int getPriority(String id) {
			final String priorityStr = configuration.getAttribute("priority");
			try {
				return Integer.parseInt(priorityStr);
			} catch (NumberFormatException e) {
				ProverUIUtils.debug("Invalid integer :"+ priorityStr+" for extension "+ id);
				// lowest priority
				return Integer.MAX_VALUE;
			}
		}

		private ImageDescriptor getImageDesc() {
			IContributor contributor = configuration.getContributor();
			String iconName = configuration.getAttribute("icon"); //$NON-NLS-1$
			return EventBImage.getImageDescriptor(
					contributor.getName(), iconName);
		}

		private static ITacticProvider getAppliProvider(Object candidate,
				String id, String tip, ImageDescriptor iconDesc) {
			if (candidate instanceof ITacticProvider) {
				return (ITacticProvider) candidate;
			}
			return null;
		}

		private static void printDebugInfo(String tacticProvider,
				TacticUIInfo result, String id) {
			if (ProverUIUtils.DEBUG) {
				if (result == null) {
					ProverUIUtils.debug("Cannot instantiate class for tactic "
							+ id);
				} else if (tacticProvider != null) {
					ProverUIUtils
					.debug("Instantiated tactic provider for tactic "
							+ id);
				} else {
					ProverUIUtils
					.debug("Instantiated proof command for tactic "
							+ id);
				}
			}
		}

	}

	private static abstract class TacticUIInfo {

		protected final String id;
		protected final ImageDescriptor iconDesc;
		protected final boolean interrupt;
		protected final String tooltip;
		protected final int priority;
		protected final String name;
		protected final String dropdown;
		protected final String toolbar;
		protected final boolean skipPostTactic;

		private Image icon = null;


		public TacticUIInfo(String id, ImageDescriptor iconDesc, boolean interrupt,
				String tooltip, int priority, String name, String dropdown,
				String toolbar, boolean skipPostTactic) {
			this.id = id;
			this.iconDesc = iconDesc;
			this.interrupt = interrupt;
			this.tooltip = tooltip;
			this.priority = priority;
			this.name = name;
			this.dropdown = dropdown;
			this.toolbar = toolbar;
			this.skipPostTactic = skipPostTactic;
		}


		public Image getIcon() {
			if (icon == null) {
				icon = iconDesc.createImage();
				if (ProverUIUtils.DEBUG) {
					if (icon != null) {
						ProverUIUtils.debug("Created icon for tactic " + id);
					} else {
						ProverUIUtils.debug("Cannot create icon for tactic "
								+ id);

					}
				}
			}
			return icon;
		}

		public String getTooltip() {
			return tooltip;
		}

		public String getDropdown() {
			return dropdown;
		}

		public String getID() {
			return id;
		}

		public boolean isInterruptable() {
			return interrupt;
		}

		public String getToolbar() {
			return toolbar;
		}

		public boolean isSkipPostTactic() {
			return skipPostTactic;
		}
	}

	private static class TacticProviderInfo extends TacticUIInfo {
		private final ITacticProvider appliProvider;

		public TacticProviderInfo(String id, ImageDescriptor iconDesc,
				boolean interrupt, String tooltip, int priority, String name,
				String dropdown, String toolbar, boolean skipPostTactic,
				ITacticProvider appliProvider) {
			super(id, iconDesc, interrupt, tooltip, priority, name, dropdown,
					toolbar, skipPostTactic);
			this.appliProvider = appliProvider;
		}

		public List<ITacticApplication> getApplicationsToGoal(
				IUserSupport us) {
			return getApplications(us, null, null);
		}

		public List<ITacticApplication> getApplicationsToHypothesis(
				IUserSupport us, Predicate hyp) {
			return getApplications(us, hyp, null);
		}

		public List<ITacticApplication> getApplications(IUserSupport us,
				Predicate hyp, String globalInput) {

			final IProofState currentPO = us.getCurrentPO();
			if(currentPO == null) {
				return Collections.emptyList();
			}
			final IProofTreeNode node = currentPO.getCurrentNode();
			if(node == null) {
				return Collections.emptyList();
			}

			return appliProvider
					.getPossibleApplications(node, hyp, globalInput);

		}

	}
	
	private static class ProofCommandInfo extends TacticUIInfo {
		private final CommandApplication commandApplication;

		public ProofCommandInfo(String id, ImageDescriptor iconDesc,
				boolean interrupt, String tooltip, int priority, String name,
				String dropdown, String toolbar, boolean skipPostTactic, IProofCommand command) {
			super(id, iconDesc, interrupt, tooltip, priority, name, dropdown,
					toolbar, skipPostTactic);
			this.commandApplication = new CommandApplication(command, iconDesc,
					tooltip);
		}

		public boolean isApplicable(IUserSupport us, Predicate hyp,
				String globalInput) {
			return commandApplication.getProofCommand().isApplicable(us, hyp,
					globalInput);
		}

		public ICommandApplication getCommandApplication() {
			return commandApplication;
		}
	}
	
	private static class CommandApplication implements ICommandApplication {

		private final ImageDescriptor iconDesc;
		private final String tooltip;
		private final IProofCommand command;
		private volatile Image lazyIcon;
		
		public CommandApplication(IProofCommand command, ImageDescriptor iconDesc,
				String tooltip) {
			this.iconDesc = iconDesc;
			this.tooltip = tooltip;
			this.command = command;
		}

		@Override
		public Image getIcon() {
			/*
			 * This is thread-safe because lazyIcon is volatile and
			 * EventBImage.getImage() always returns the same value.
			 */
			if (lazyIcon == null) {
				lazyIcon = EventBImage.getImage(iconDesc);
			}
			return lazyIcon;
		}

		@Override
		public IProofCommand getProofCommand() {
			return command;
		}

		@Override
		public String getTooltip() {
			return tooltip;
		}
		
	}
	
	private class ToolbarInfo {
		IConfigurationElement configuration;

		Collection<String> dropdowns;

		Collection<String> tactics;

		public ToolbarInfo(IConfigurationElement configuration) {
			this.configuration = configuration;
		}

		public Collection<String> getDropdowns() {
			assert dropdownRegistry != null;

			if (dropdowns == null) {
				dropdowns = new ArrayList<String>();
				final String id = configuration.getAttribute("id");

				for (String key : dropdownRegistry.keySet()) {
					DropdownInfo info = dropdownRegistry.get(key);
					if (id.equals(info.getToolbar())) {
						String dropdownID = info.getID();
						dropdowns.add(dropdownID);
						if (ProverUIUtils.DEBUG)
							ProverUIUtils.debug("Attached dropdown "
									+ dropdownID + " to toolbar " + id);
					}
				}
			}

			return dropdowns;
		}

		public Collection<String> getTactics() {
			assert globalRegistry != null;

			if (tactics == null) {
				tactics = new ArrayList<String>();
				final String id = configuration.getAttribute("id");

				for (String key : globalRegistry.keySet()) {
					final TacticUIInfo info = globalRegistry.get(key);
					if (id.equals(info.getToolbar())) {
						String tacticID = info.getID();
						tactics.add(tacticID);
						if (ProverUIUtils.DEBUG)
							ProverUIUtils.debug("Attached tactic " + tacticID
									+ " to toolbar " + id);
					}
				}
			}

			return tactics;
		}
	}

	private class DropdownInfo {
		IConfigurationElement configuration;

		String toolbar;

		Collection<String> tactics;

		public DropdownInfo(IConfigurationElement configuration) {
			this.configuration = configuration;
			this.toolbar = configuration.getAttribute("toolbar");
		}

		public String getID() {
			return configuration.getAttribute("id");
		}

		public String getToolbar() {
			return toolbar;
		}

		public Collection<String> getTactics() {
			assert globalRegistry != null;

			if (tactics == null) {
				tactics = new ArrayList<String>();
				final String id = configuration.getAttribute("id");

				for (String tacticID : globalRegistry.keySet()) {
					final TacticUIInfo info = globalRegistry.get(tacticID);
					if (id.equals(info.getDropdown())) {
						tactics.add(info.getID());
						if (ProverUIUtils.DEBUG)
							ProverUIUtils.debug("Attached tactic " + tacticID
									+ " to dropdown " + id);
					}
				}
			}

			return tactics;
		}

	}

	/**
	 * The unique instance of this class is created when initializing the static
	 * final field "instance", thus in a thread-safe manner.
	 */
	private TacticUIRegistry() {
		loadRegistry();
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

	private TacticUIInfo findInAnyTacticRegistry(String id) {
		TacticUIInfo info = findInTacticRegistry(id, TARGET_GOAL);
		if (info != null) return info;
		info = findInTacticRegistry(id, TARGET_HYPOTHESIS);
		if (info != null) return info;
		info = findInTacticRegistry(id, TARGET_ANY);
		if (info != null) return info;
		return findInTacticRegistry(id, TARGET_GLOBAL);
	}
	
	private TacticUIInfo findInTacticRegistry(String id, String target) {
		TacticUIInfo info;
		if (target.equals(TARGET_GOAL)) {
			info = goalTacticRegistry.get(id);
			if (info != null) return info;
			return goalCommandRegistry.get(id);
		}
		
		if (target.equals(TARGET_HYPOTHESIS)) {
			info = hypothesisTacticRegistry.get(id);
			if (info != null) return info;
			return hypothesisCommandRegistry.get(id);
		}
		
		if(target.equals(TARGET_ANY)) {
			info = anyTacticRegistry.get(id);
			if (info != null) return info;
			return anyCommandRegistry.get(id);
		}
		
		return globalRegistry.get(id);
	}
	
	// must be called from a synchronized method
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

	/**
	 * Initialises the registry using extensions to the element UI extension
	 * point
	 */
	private synchronized void loadRegistry() {
		goalTacticRegistry = new LinkedHashMap<String, TacticProviderInfo>();
		goalCommandRegistry = new LinkedHashMap<String, ProofCommandInfo>();
		hypothesisTacticRegistry = new LinkedHashMap<String, TacticProviderInfo>();
		hypothesisCommandRegistry = new LinkedHashMap<String, ProofCommandInfo>();
		anyTacticRegistry = new LinkedHashMap<String, TacticProviderInfo>();
		anyCommandRegistry = new LinkedHashMap<String, ProofCommandInfo>();
		globalRegistry = new LinkedHashMap<String, TacticUIInfo>();
		toolbarRegistry = new LinkedHashMap<String, ToolbarInfo>();
		dropdownRegistry = new LinkedHashMap<String, DropdownInfo>();

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg.getExtensionPoint(PROOFTACTICS_ID);
		IConfigurationElement[] configurations = extensionPoint
				.getConfigurationElements();

		for (IConfigurationElement configuration : configurations) {
			String id = configuration.getAttribute("id"); //$NON-NLS-1$
			if (id == null) continue;
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
							new ToolbarInfo(configuration));

					if (oldInfo != null) {
						toolbarRegistry.put(id, oldInfo);
						printDebugConfExists(id, toolbarStr);
					} else {
						printDebugRegistration(id, toolbarStr);
					}
				} else {
					final String dropdownStr = "dropdown";
					if (configuration.getName().equals(dropdownStr)) {
						DropdownInfo oldInfo = dropdownRegistry.put(id,
								new DropdownInfo(configuration));

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

	private static void printDebugConfExists(String id, String kind) {
		if (ProverUIUtils.DEBUG)
			ProverUIUtils.debug("Configuration already exists for " + kind + " "
					+ id + ", configuration ignored.");
	}
	
	private static void printDebugRegistration(String id, String kind) {
		if (ProverUIUtils.DEBUG)
			ProverUIUtils.debug("Registered " + kind + " with id " + id);
	}

	public synchronized List<ITacticApplication> getTacticApplicationsToGoal(IUserSupport us) {
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

	public synchronized List<ICommandApplication> getCommandApplicationsToGoal(IUserSupport us) {
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

	public synchronized List<ITacticApplication> getTacticApplicationsToHypothesis(IUserSupport us, Predicate hyp) {
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
	
	public synchronized List<ICommandApplication> getCommandApplicationsToHypothesis(
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

	
	public synchronized Image getIcon(String tacticID) {
		final TacticUIInfo info = findInAnyTacticRegistry(tacticID);
		if (info != null)
			return info.getIcon();

		return null;
	}

	public synchronized String getTip(String tacticID) {
		final TacticUIInfo info = findInAnyTacticRegistry(tacticID);
		
		if (info != null)
			return info.getTooltip();

		return null;
	}

	public synchronized boolean isSkipPostTactic(String tacticID) {
		final TacticUIInfo info = findInAnyTacticRegistry(tacticID);
		if (info != null)
			return info.isSkipPostTactic();
	
		return false;
	}

	public synchronized boolean isInterruptable(String tacticID, String target) {
		TacticUIInfo info = findInTacticRegistry(tacticID, target);
		if (info != null)
			return info.isInterruptable();

		return false;
	}
	
	public synchronized Collection<String> getToolbars() {
		return toolbarRegistry.keySet();
	}

	public synchronized Collection<String> getToolbarDropdowns(String toolbar) {
		ToolbarInfo info = toolbarRegistry.get(toolbar);
		if (info != null) {
			return info.getDropdowns();
		}

		return new ArrayList<String>(0);
	}
	
	public synchronized Collection<String> getDropdownTactics(String dropdownID) {
		DropdownInfo info = dropdownRegistry.get(dropdownID);
		if (info == null) {
			return Collections.emptyList();
		}
		 
		return info.getTactics();
	}


	public synchronized Collection<String> getToolbarTactics(String toolbarID) {
		ToolbarInfo info = toolbarRegistry.get(toolbarID);
		if (info == null) {
			return Collections.emptyList();
		}

		return info.getTactics();
	}

	// returns a ITacticApplication or ICommandApplication if applicable
	// returns null if not applicable
	public synchronized Object getGlobalApplication(String tacticID,
			IUserSupport us, String globalInput) {
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
