/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
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

import java.util.ArrayList;
import java.util.Arrays;
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
import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.ui.prover.IPositionApplication;
import org.eventb.ui.prover.IPredicateApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider2;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.prover.IProofCommand;
import org.eventb.ui.prover.ITacticProvider;

public class TacticUIRegistry {
	// The identifier of the extension point (value
	// <code>"org.eventb.ui.proofTactics"</code>).
	private final static String PROOFTACTICS_ID = EventBUIPlugin.PLUGIN_ID
			+ ".proofTactics";
	
	public static final String TARGET_ANY = "any";

	public static final String TARGET_GOAL = "goal";

	public static final String TARGET_HYPOTHESIS = "hypothesis";

	public static final String TARGET_GLOBAL = "global";

	// The static instance of this singleton class
	private static TacticUIRegistry instance;

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

	private static abstract class TPApplicationWrapper implements ITacticApplication {

		protected final ITacticProvider provider;
		protected final String tacticID;
		protected final IProofTreeNode node;
		protected final Predicate hyp;
		protected final IPosition position;
		protected final String tip;
		private String latestGlobalInput = null;
		private String[] latestInputs = null;
		private ITactic latestTactic = null;

		public TPApplicationWrapper(ITacticProvider provider, String tacticID,
				IProofTreeNode node, Predicate hyp, IPosition position,
				String globalInput, String tip) {
			this.provider = provider;
			this.tacticID = tacticID;
			this.node = node;
			this.hyp = hyp;
			this.position = position;
			this.tip = tip;
		}

		public ITactic getTactic(String[] inputs, String globalInput) {
			if (!globalInput.equals(latestGlobalInput) || !Arrays.equals(inputs, latestInputs)) {
				latestGlobalInput = globalInput;
				latestInputs = inputs;
				latestTactic = provider.getTactic(node, hyp, position, inputs, globalInput);
			}
			return latestTactic;
		}
		
		public String getTacticID() {
			return tacticID;
		}
	}
	
	private static class TPPositionWrapper extends TPApplicationWrapper implements IPositionApplication {
		
		public TPPositionWrapper(ITacticProvider provider, String tacticID, IProofTreeNode node,
				Predicate hyp, IPosition position, String globalInput,
				String tip) {
			super(provider, tacticID, node, hyp, position, globalInput, tip);
		}

		public Point getHyperlinkBounds(String actualString, Predicate parsedPredicate) {
			return provider.getOperatorPosition(parsedPredicate, actualString, position);
		}
		
		public String getHyperlinkLabel() {
			return tip;
		}
	}
	
	private static class TPPredicateWrapper extends TPApplicationWrapper implements IPredicateApplication {

		private final ImageDescriptor iconDesc;
		private Image lazyIcon;
		
		public TPPredicateWrapper(ITacticProvider provider,
				String tacticID, IProofTreeNode node, Predicate hyp, IPosition position,
				String globalInput, String tip, ImageDescriptor iconDesc) {
			super(provider, tacticID, node, hyp, position, globalInput, tip);
			this.iconDesc = iconDesc;
		}

		public Image getIcon() {
			if (lazyIcon == null) {
				lazyIcon = EventBImage.getImage(iconDesc, 0);
			}
			return lazyIcon;
		}

		public String getTooltip() {
			return tip;
		}
		
	}
	
	// wraps ITacticProvider and ITacticProvider2 
	public static interface IApplicationProvider {

		List<ITacticApplication> getPossibleApplications(
				IProofTreeNode node, Predicate hyp, String globalInput);

		
	}

	private static class TPProvider implements IApplicationProvider {

		private final ITacticProvider provider;
		private final String tacticID;
		private final String tip;
		private final ImageDescriptor iconDesc;

		public TPProvider(ITacticProvider provider, String tacticID, String tip, ImageDescriptor iconDesc) {
			this.provider = provider;
			this.tacticID = tacticID;
			this.tip = tip;
			this.iconDesc = iconDesc;
		}

		public List<ITacticApplication> getPossibleApplications(
				IProofTreeNode node, Predicate hyp, String globalInput) {
			final List<IPosition> appPos = provider.getApplicablePositions(
					node, hyp, globalInput);
			if (appPos == null) {
				return Collections.emptyList();
			}
			final List<ITacticApplication> result = new ArrayList<ITacticApplication>();
			if (appPos.isEmpty()) { // application to predicate
				result.add(new TPPredicateWrapper(provider, tacticID, node, hyp, null,
						globalInput, tip, iconDesc));
			} else { // application to positions
				for (IPosition position : appPos) {
					result.add(new TPPositionWrapper(provider, tacticID, node, hyp,
							position, globalInput, tip));
				}
			}
			return result;
		}

	}
	
	
	private static class TP2Provider implements IApplicationProvider {

		private final ITacticProvider2 provider;

		public TP2Provider(ITacticProvider2 provider) {
			this.provider = provider;
		}

		public List<ITacticApplication> getPossibleApplications(
				IProofTreeNode node, Predicate hyp, String globalInput) {
			return provider.getPossibleApplications(node, hyp, globalInput);
		}
	}
	
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
					final IApplicationProvider appliProvider = getAppliProvider(candidate, id, tooltip, iconDesc);
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
			final String priorityStr = configuration.getAttribute("priorty");
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

		private static IApplicationProvider getAppliProvider(Object candidate,
				String id, String tip, ImageDescriptor iconDesc) {
			if (candidate instanceof ITacticProvider) {
				return new TPProvider((ITacticProvider) candidate, id, tip,
						iconDesc);
			} else if (candidate instanceof ITacticProvider2) {
				return new TP2Provider(((ITacticProvider2) candidate));
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
		private final IApplicationProvider appliProvider;

		public TacticProviderInfo(String id, ImageDescriptor iconDesc,
				boolean interrupt, String tooltip, int priority, String name,
				String dropdown, String toolbar, boolean skipPostTactic,
				IApplicationProvider appliProvider) {
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

			IProofTreeNode node = null;
			IProofState currentPO = us.getCurrentPO();
			if (currentPO != null) {
				node = currentPO.getCurrentNode();
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
		private Image lazyIcon;
		
		public CommandApplication(IProofCommand command, ImageDescriptor iconDesc,
				String tooltip) {
			this.iconDesc = iconDesc;
			this.tooltip = tooltip;
			this.command = command;
		}

		public Image getIcon() {
			if (lazyIcon == null) {
				lazyIcon = EventBImage.getImage(iconDesc, 0);
			}
			return lazyIcon;
		}

		public IProofCommand getProofCommand() {
			return command;
		}

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
						ProverUIUtils.debug("Attached dropdown " + dropdownID
								+ " to toolbar " + id);
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
						ProverUIUtils.debug("Attached tactic " + tacticID
								+ " to dropdown " + id);
					}
				}
			}

			return tactics;
		}

	}

	/**
	 * A private constructor to prevent creating an instance of this class
	 * directly
	 */
	private TacticUIRegistry() {
		// Singleton to hide the constructor
	}

	/**
	 * Getting the default instance of this class (create a new instance of it
	 * does not exist before)
	 * <p>
	 * 
	 * @return An instance of this class
	 */
	public static TacticUIRegistry getDefault() {
		if (instance == null)
			instance = new TacticUIRegistry();
		return instance;
	}

	private TacticUIInfo findInAnyTacticRegistry(String id) {
		if (goalTacticRegistry == null) {
			loadRegistry();
		}
		TacticUIInfo info = findInTacticRegistry(id, TARGET_GOAL);
		if (info != null) return info;
		info = findInTacticRegistry(id, TARGET_HYPOTHESIS);
		if (info != null) return info;
		info = findInTacticRegistry(id, TARGET_ANY);
		if (info != null) return info;
		return findInTacticRegistry(id, TARGET_GLOBAL);
	}
	
	private TacticUIInfo findInTacticRegistry(String id, String target) {
		if (goalTacticRegistry == null) {
			loadRegistry();
		}
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
			info = anyCommandRegistry.get(id);
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
		if (goalTacticRegistry != null) {
			// avoid to read the registry at the same time in different threads
			return;
		}

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
					if (id != null) {
						ToolbarInfo oldInfo = toolbarRegistry.put(id,
								new ToolbarInfo(configuration));

						if (oldInfo != null) {
							toolbarRegistry.put(id, oldInfo);
							printDebugConfExists(id, toolbarStr);
						} else {
							printDebugRegistration(id, toolbarStr);
						}
					}
				} else {
					final String dropdownStr = "dropdown";
					if (configuration.getName().equals(dropdownStr)) {
						if (id != null) {
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
	}

	private static void printDebugConfExists(String id, String kind) {
		ProverUIUtils.debug("Configuration already exists for " + kind + " "
				+ id + ", configuration ignored.");
	}
	
	private static void printDebugRegistration(String id, String kind) {
		ProverUIUtils.debug("Registered " + kind + " with id " + id);
	}

	public synchronized List<ITacticApplication> getTacticApplicationsToGoal(IUserSupport us) {
		if ((goalTacticRegistry == null) || (anyTacticRegistry == null)) {
			loadRegistry();
		}

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
		if ((goalTacticRegistry == null) || (anyTacticRegistry == null)) {
			loadRegistry();
		}

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
		if ((hypothesisTacticRegistry == null) || (anyTacticRegistry == null)) {
			loadRegistry();
		}
		
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
		if ((hypothesisTacticRegistry == null) || (anyTacticRegistry == null)) {
			loadRegistry();
		}

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
		if (toolbarRegistry == null)
			loadRegistry();
	
		return toolbarRegistry.keySet();
	}

	public synchronized Collection<String> getToolbarDropdowns(String toolbar) {
		if (toolbarRegistry == null)
			loadRegistry();

		ToolbarInfo info = toolbarRegistry.get(toolbar);
		if (info != null) {
			return info.getDropdowns();
		}

		return new ArrayList<String>(0);
	}
	
	public synchronized Collection<String> getDropdownTactics(String dropdownID) {
		if (dropdownRegistry == null)
			loadRegistry();

		DropdownInfo info = dropdownRegistry.get(dropdownID);
		if (info == null) {
			return Collections.emptyList();
		}
		 
		return info.getTactics();
	}


	public synchronized Collection<String> getToolbarTactics(String toolbarID) {
		if (toolbarRegistry == null)
			loadRegistry();

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
		if (globalRegistry == null) {
			loadRegistry();
		}

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
