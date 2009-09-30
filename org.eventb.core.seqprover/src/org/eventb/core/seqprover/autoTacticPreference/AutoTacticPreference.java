package org.eventb.core.seqprover.autoTacticPreference;

import static org.eventb.core.seqprover.tactics.BasicTactics.loopOnAllPending;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.internal.core.seqprover.tacticPreference.TacticPreferenceUtils;

/**
 * @since 1.0
 */
public abstract class AutoTacticPreference implements IAutoTacticPreference {

	private boolean enabled = false;

	private List<ITacticDescriptor> declaredDescriptors = null;

	private ITactic selectedComposedTactic;

	private ITactic defaultComposedTactic = null;
	
	private List<ITacticDescriptor> selectedDescriptors;
	
	// The identifier of the extension point.
	private String registryID;

	public AutoTacticPreference(String registryID) {
		this.registryID = registryID;
		setSelectedDescriptors(getDeclaredDescriptors());
	}

	/**
	 * Initialises the registry using extensions to the element UI extension
	 * point
	 */
	private synchronized void loadRegistry() {
		if (declaredDescriptors != null) {
			// avoid to read the registry at the same time in different threads
			return;
		}
		declaredDescriptors = new ArrayList<ITacticDescriptor>();

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg.getExtensionPoint(registryID);
		if (extensionPoint == null) // Invalid registry ID
			return;

		IAutoTacticRegistry tacticRegistry = SequentProver.getAutoTacticRegistry();
		IConfigurationElement[] configurations = extensionPoint
				.getConfigurationElements();
		for (IConfigurationElement configuration : configurations) {
			String tacticID = configuration.getAttribute("id"); //$NON-NLS-1$
			
			// Check if the id is registered as a tactic
			if (!tacticRegistry.isRegistered(tacticID)) {
				if (TacticPreferenceUtils.DEBUG) {
					System.out.println("Tactic " + tacticID
							+ " is not registered, ignore this configuration.");
				}
				continue;
			}
			
			ITacticDescriptor tacticDescriptor = tacticRegistry
					.getTacticDescriptor(tacticID);
			// Check if the id is registered
			if (declaredDescriptors.contains(tacticDescriptor)) {
				if (TacticPreferenceUtils.DEBUG) {
					System.out
							.println("Tactic "
									+ tacticID
									+ " is already declared, ignore this configuration.");
				}
			} else {
				declaredDescriptors.add(tacticDescriptor);
			} 
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.sequenprover.tacticPreference.ITacticPreference#isDeclared(org.eventb.core.seqprover.ITacticRegistry.ITacticDescriptor)
	 */
	public boolean isDeclared(ITacticDescriptor tacticDesc) {
		if (declaredDescriptors == null)
			loadRegistry();

		return declaredDescriptors.contains(tacticDesc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.sequenprover.tacticPreference.ITacticPreference#getDeclaredDescriptors()
	 */
	public List<ITacticDescriptor> getDeclaredDescriptors() {
		if (declaredDescriptors == null)
			loadRegistry();

		return declaredDescriptors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.sequenprover.tacticPreference.ITacticPreference#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.sequenprover.tacticPreference.ITacticPreference#isEnabled()
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.sequenprover.tacticPreference.ITacticPreference#getSelectedComposedTactic()
	 */
	public ITactic getSelectedComposedTactic() {
		if (selectedComposedTactic == null) {
			selectedComposedTactic = composeTactics(selectedDescriptors);
		}
		return selectedComposedTactic;
	}

	protected ITactic composeTactics(List<ITacticDescriptor> tacticDescs) {
		ITactic [] tactics = new ITactic[tacticDescs.size()];
		int i = 0;
		for (ITacticDescriptor tacticDesc : tacticDescs) {
			tactics[i] = tacticDesc.getTacticInstance();
			++i;
		}
		return loopOnAllPending(tactics);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sequenprover.tacticPreference.ITacticPreference#setSelectedDescriptors(org.eventb.core.seqprover.ITacticRegistry.ITacticDescriptor[])
	 */
	public void setSelectedDescriptors(List<ITacticDescriptor> tacticDescs) {
		selectedDescriptors = tacticDescs;
		selectedComposedTactic = null;
	}

	public ITactic getDefaultComposedTactic() {
		if (defaultComposedTactic == null) {
			defaultComposedTactic = composeTactics(getDefaultDescriptors());
		}
		return defaultComposedTactic;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sequenprover.tacticPreference.ITacticPreference#getDefaultDescriptors()
	 */
	public List<ITacticDescriptor>  getDefaultDescriptors() {
		return stringsToTacticDescriptors(getDefaultIDs());
	}

	protected abstract String [] getDefaultIDs();

	private List<ITacticDescriptor> stringsToTacticDescriptors(
			String[] tacticIDs) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final List<ITacticDescriptor> result = new ArrayList<ITacticDescriptor>();
		for (String id : tacticIDs) {
			if (reg.isRegistered(id)) {
				final ITacticDescriptor desc = reg.getTacticDescriptor(id);
				if (isDeclared(desc)) {
					result.add(desc);
				}
			}
		}
		return result;
	}

}
