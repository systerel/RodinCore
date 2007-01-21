package org.eventb.internal.ui.prover;

import java.util.ArrayList;
import java.util.Collection;
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
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.EventBImage;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.prover.IProofCommand;
import org.eventb.ui.prover.ITacticProvider;

public class TacticUIRegistry {
	// The identifier of the extension point (value
	// <code>"org.eventb.ui.proofTactics"</code>).
	private final static String PROOFTACTICS_ID = EventBUIPlugin.PLUGIN_ID
			+ ".proofTactics";

	public static final String TARGET_GOAL = "goal";

	public static final String TARGET_HYPOTHESIS = "hypothesis";

	public static final String TARGET_GLOBAL = "global";

	// The static instance of this singleton class
	private static TacticUIRegistry instance;

	// The registry stored Element UI information
	private Map<String, TacticUIInfo> goalRegistry = null;

	private Map<String, TacticUIInfo> hypothesisRegistry = null;

	Map<String, TacticUIInfo> globalRegistry = null;

	private Map<String, ToolbarInfo> toolbarRegistry = null;

	Map<String, DropdownInfo> dropdownRegistry = null;

	private class TacticUIInfo {
		// Configuration information related to the element
		private IConfigurationElement configuration;

		private ITacticProvider provider = null;

		private IProofCommand command = null;

		private Image icon = null;

		private String tip;

		public TacticUIInfo(IConfigurationElement configuration) {
			this.configuration = configuration;
			tip = configuration.getAttribute("tooltip");
		}

		public List<IPosition> getApplicableToGoalPositions(IUserSupport us) {
			return getApplicablePositions(us, null, null);
		}

		public List<IPosition> getApplicablePositions(IUserSupport us, Predicate hyp,
				String input) {

			if (provider == null && command == null) {
				loadImplementation();
			}
			IProofTreeNode node = null;
			IProofState currentPO = us.getCurrentPO();
			if (currentPO != null) {
				node = currentPO.getCurrentNode();
			}

			// Try tactic provider first
			if (provider != null)
				return provider.getApplicablePositions(node, hyp, input);

			// Try proof command
			if (command != null && command.isApplicable(us, hyp, input))
				return new ArrayList<IPosition>();

			return null;
		}

		private void loadImplementation() {
			try {
				provider = (ITacticProvider) configuration
						.createExecutableExtension("tacticProvider");
				if (ProverUIUtils.DEBUG) {
					ProverUIUtils
							.debug("Instantiate tactic provider for tactic "
									+ configuration.getAttribute("id"));
				}
				return;
			} catch (CoreException e) {
				if (ProverUIUtils.DEBUG) {
					ProverUIUtils
							.debug("Cannot instantiate tactic provider for tactic "
									+ configuration.getAttribute("id"));
					e.printStackTrace();
				}
			}

			try {
				command = (IProofCommand) configuration
						.createExecutableExtension("proofCommand");
				if (ProverUIUtils.DEBUG) {
					ProverUIUtils.debug("Instantiate proof command for tactic "
							+ configuration.getAttribute("id"));
				}
			} catch (CoreException e) {
				if (ProverUIUtils.DEBUG) {
					ProverUIUtils
							.debug("Cannot instantiate proof command for tactic "
									+ configuration.getAttribute("id"));
					e.printStackTrace();
				}
			}

		}

		public Image getIcon() {
			if (icon == null) {
				IContributor contributor = configuration.getContributor();
				String iconName = configuration.getAttribute("icon"); //$NON-NLS-1$
				ImageDescriptor desc = EventBImage.getImageDescriptor(
						contributor.getName(), iconName);
				icon = desc.createImage();
				if (ProverUIUtils.DEBUG) {
					if (icon != null) {
						ProverUIUtils.debug("Create icon " + iconName
								+ " for tactic "
								+ configuration.getAttribute("id"));
					} else {
						ProverUIUtils.debug("Cannot create icon " + iconName
								+ " for tactic "
								+ configuration.getAttribute("id"));

					}
				}
			}
			return icon;
		}

		public ITacticProvider getTacticProvider() {
			if (provider == null & command == null) {
				loadImplementation();
			}

			return provider;
		}

		public String getTip() {
			return tip;
		}

		public List<IPosition> getApplicableToHypothesisPositions(IUserSupport us,
				Predicate hyp) {
			return getApplicablePositions(us, hyp, null);
		}

		public String getDropdown() {
			return configuration.getAttribute("dropdown");
		}

		public String getID() {
			return configuration.getAttribute("id");
		}

		public IProofCommand getProofCommand() {
			if (provider == null & command == null) {
				loadImplementation();
			}

			return command;
		}

		public boolean isInterruptable() {
			return configuration.getAttribute("interrupt").equalsIgnoreCase(
					"true");
		}

		public String getToolbar() {
			return configuration.getAttribute("toolbar");
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
				String id = configuration.getAttribute("id");

				for (String key : dropdownRegistry.keySet()) {
					DropdownInfo info = dropdownRegistry.get(key);
					if (id.equals(info.getToolbar())) {
						String dropdownID = info.getID();
						dropdowns.add(dropdownID);
						if (ProverUIUtils.DEBUG) {
							ProverUIUtils.debug("Attached dropdown "
									+ dropdownID + " to toolbar " + id);
						}
					}
				}
			}

			return dropdowns;
		}

		public Collection<String> getTactics() {
			assert globalRegistry != null;

			if (tactics == null) {
				tactics = new ArrayList<String>();
				String id = configuration.getAttribute("id");

				for (String key : globalRegistry.keySet()) {
					TacticUIInfo info = globalRegistry.get(key);
					if (id.equals(info.getToolbar())) {
						String tacticID = info.getID();
						tactics.add(tacticID);
						if (ProverUIUtils.DEBUG) {
							ProverUIUtils.debug("Attached tactic " + tacticID
									+ " to toolbar " + id);
						}
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
			toolbar = configuration.getAttribute("toolbar");
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
				String id = configuration.getAttribute("id");

				for (String tacticID : globalRegistry.keySet()) {
					TacticUIInfo info = globalRegistry.get(tacticID);
					if (id.equals(info.getDropdown())) {
						tactics.add(info.getID());
						if (ProverUIUtils.DEBUG) {
							ProverUIUtils.debug("Attached tactic " + tacticID
									+ " to dropdown " + id);
						}
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

	/**
	 * Initialises the registry using extensions to the element UI extension
	 * point
	 */
	private synchronized void loadRegistry() {
		if (goalRegistry != null) {
			// avoid to read the registry at the same time in different threads
			return;
		}

		goalRegistry = new LinkedHashMap<String, TacticUIInfo>();
		hypothesisRegistry = new LinkedHashMap<String, TacticUIInfo>();
		globalRegistry = new LinkedHashMap<String, TacticUIInfo>();
		toolbarRegistry = new LinkedHashMap<String, ToolbarInfo>();
		dropdownRegistry = new LinkedHashMap<String, DropdownInfo>();

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg.getExtensionPoint(PROOFTACTICS_ID);
		IConfigurationElement[] configurations = extensionPoint
				.getConfigurationElements();

		for (IConfigurationElement configuration : configurations) {
			String id = configuration.getAttribute("id"); //$NON-NLS-1$
			if (configuration.getName().equals("tactic")) {
				// Check for duplication first
				String target = configuration.getAttribute("target");
				TacticUIInfo info = goalRegistry.get(id);
				if (info != null) {
					if (ProverUIUtils.DEBUG) {
						System.out
								.println("Configuration is already exists for "
										+ target + " tactic " + id
										+ ", ignore this configuration.");
					}
					continue;
				}
				info = hypothesisRegistry.get(id);
				if (info != null) {
					if (ProverUIUtils.DEBUG) {
						System.out
								.println("Configuration is already exists for "
										+ target + " tactic " + id
										+ ", ignore this configuration.");
					}
					continue;
				}
				info = globalRegistry.get(id);
				if (info != null) {
					if (ProverUIUtils.DEBUG) {
						System.out
								.println("Configuration is already exists for "
										+ target + " tactic " + id
										+ ", ignore this configuration.");
					}
					continue;
				}
				Map<String, TacticUIInfo> registry;
				if (target.equals("goal"))
					registry = goalRegistry;
				else if (target.equals("hypothesis"))
					registry = hypothesisRegistry;
				else
					registry = globalRegistry;

				if (id != null) {
					registry.put(id, new TacticUIInfo(configuration));
					if (ProverUIUtils.DEBUG) {
						ProverUIUtils.debug("Registered " + target
								+ " tactic with id " + id);
					}
				}
			}

			else if (configuration.getName().equals("toolbar")) {
				if (id != null) {
					ToolbarInfo oldInfo = toolbarRegistry.put(id,
							new ToolbarInfo(configuration));

					if (oldInfo != null) {
						toolbarRegistry.put(id, oldInfo);
						if (ProverUIUtils.DEBUG)
							ProverUIUtils
									.debug("Configuration is already exists toolbar "
											+ id
											+ ", ignore this configuration.");
					} else {
						if (ProverUIUtils.DEBUG)
							ProverUIUtils.debug("Registered toolbar with id "
									+ id);
					}
				}
			}

			else if (configuration.getName().equals("dropdown")) {
				if (id != null) {
					DropdownInfo oldInfo = dropdownRegistry.put(id,
							new DropdownInfo(configuration));

					if (oldInfo != null) {
						dropdownRegistry.put(id, oldInfo);
						if (ProverUIUtils.DEBUG)
							ProverUIUtils
									.debug("Configuration is already exists dropdown "
											+ id
											+ ", ignore this configuration.");
					} else {
						if (ProverUIUtils.DEBUG)
							ProverUIUtils.debug("Registered dropdown with id "
									+ id);
					}
				}
			}

		}

	}

	public synchronized String[] getApplicableToGoal(IUserSupport us) {
		if (goalRegistry == null)
			loadRegistry();

		Collection<String> result = new ArrayList<String>();

		for (String key : goalRegistry.keySet()) {
			TacticUIInfo info = goalRegistry.get(key);
			if (info.getApplicableToGoalPositions(us) != null) {
				result.add(key);
			}
		}
		return result.toArray(new String[result.size()]);
	}

	public synchronized Image getIcon(String tacticID) {
		if (goalRegistry == null)
			loadRegistry();

		TacticUIInfo info = goalRegistry.get(tacticID);
		if (info != null)
			return info.getIcon();

		info = hypothesisRegistry.get(tacticID);
		if (info != null)
			return info.getIcon();

		info = globalRegistry.get(tacticID);
		if (info != null)
			return info.getIcon();

		return null;
	}

	public synchronized ITacticProvider getTacticProvider(String tacticID) {
		if (goalRegistry == null)
			loadRegistry();

		TacticUIInfo info = goalRegistry.get(tacticID);
		if (info != null)
			return info.getTacticProvider();

		info = hypothesisRegistry.get(tacticID);
		if (info != null)
			return info.getTacticProvider();

		info = globalRegistry.get(tacticID);
		if (info != null)
			return info.getTacticProvider();

		return null;
	}

	public synchronized String getTip(String tacticID) {
		if (goalRegistry == null)
			loadRegistry();

		TacticUIInfo info = goalRegistry.get(tacticID);
		if (info != null)
			return info.getTip();

		info = hypothesisRegistry.get(tacticID);
		if (info != null)
			return info.getTip();

		info = globalRegistry.get(tacticID);
		if (info != null)
			return info.getTip();

		return null;
	}

	public String[] getApplicableToHypothesis(IUserSupport us, Predicate hyp) {
		if (hypothesisRegistry == null)
			loadRegistry();

		Collection<String> result = new ArrayList<String>();

		for (String key : hypothesisRegistry.keySet()) {
			TacticUIInfo info = hypothesisRegistry.get(key);
			if (info.getApplicableToHypothesisPositions(us, hyp) != null) {
				result.add(key);
			}
		}
		return result.toArray(new String[result.size()]);
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

	public Collection<String> getDropdownTactics(String dropdownID) {
		if (dropdownRegistry == null)
			loadRegistry();

		DropdownInfo info = dropdownRegistry.get(dropdownID);
		if (info != null) {
			return info.getTactics();
		}

		return new ArrayList<String>(0);
	}

	public IProofCommand getProofCommand(String tacticID, String target) {
		if (goalRegistry == null)
			loadRegistry();

		Map<String, TacticUIInfo> registry;
		if (target.equals(TARGET_GOAL))
			registry = goalRegistry;
		else if (target.equals(TARGET_HYPOTHESIS))
			registry = hypothesisRegistry;
		else
			registry = globalRegistry;

		TacticUIInfo info = registry.get(tacticID);
		if (info != null)
			return info.getProofCommand();

		return null;
	}

	public boolean isInterruptable(String tacticID, String target) {
		if (goalRegistry == null)
			loadRegistry();

		Map<String, TacticUIInfo> registry;
		if (target.equals(TARGET_GOAL))
			registry = goalRegistry;
		else if (target.equals(TARGET_HYPOTHESIS))
			registry = hypothesisRegistry;
		else
			registry = globalRegistry;

		TacticUIInfo info = registry.get(tacticID);
		if (info != null)
			return info.isInterruptable();

		return false;

	}

	public Collection<String> getToolbarTactics(String toolbarID) {
		if (toolbarRegistry == null)
			loadRegistry();

		ToolbarInfo info = toolbarRegistry.get(toolbarID);
		if (info != null) {
			return info.getTactics();
		}

		return new ArrayList<String>(0);
	}

	public List<IPosition> getApplicableToHypothesisPositions(String tacticID, IUserSupport us, Predicate hyp) {
		if (hypothesisRegistry == null)
			loadRegistry();

		TacticUIInfo info = hypothesisRegistry.get(tacticID);
		if (info != null)
			return info.getApplicableToHypothesisPositions(us, hyp);

		return null;
	}

	public List<IPosition> getApplicableToGoalPositions(String tacticID, IUserSupport userSupport) {
		if (goalRegistry == null)
			loadRegistry();

		TacticUIInfo info = goalRegistry.get(tacticID);
		if (info != null)
			return info.getApplicableToGoalPositions(userSupport);

		return null;
	}

}
