package org.eventb.internal.core.pm;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.ITacticContainerRegistry;
import org.eventb.core.seqprover.ITacticRegistry;
import org.eventb.core.seqprover.SequentProver;

public abstract class TacticContainerRegistry implements ITacticContainerRegistry {

	// The identifier of the extension point.
	protected String registryID;

	private List<String> tacticIDs = null;
	
	protected TacticContainerRegistry(String registryID) {
		this.registryID = registryID;
	}
	
	/**
	 * Initialises the registry using extensions to the element UI extension
	 * point
	 */
	private synchronized void loadRegistry() {
		if (tacticIDs != null) {
			// avoid to read the registry at the same time in different threads
			return;
		}
		tacticIDs = new ArrayList<String>();
		
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg.getExtensionPoint(registryID);
		IConfigurationElement[] configurations = extensionPoint
				.getConfigurationElements();
		for (IConfigurationElement configuration : configurations) {
			String id = configuration.getAttribute("id"); //$NON-NLS-1$
			ITacticRegistry tacticRegistry = SequentProver.getTacticRegistry();
			// Check if the id is registered
			if (tacticIDs.contains(id)) {
				if (UserSupportUtils.DEBUG) {
					System.out
							.println("Tactic "
									+ id
									+ " is already declared, ignore this configuration.");
				}
			} else if (tacticRegistry.isRegistered(id)) {
				tacticIDs.add(id);
			} else {
				if (UserSupportUtils.DEBUG) {
					System.out.println("Tactic " + id
							+ " is not registered, ignore this configuration.");
				}
			}
		}
	}

	public boolean isDeclared(String tacticID) {
		if (tacticIDs == null)
			loadRegistry();
		
		return tacticIDs.contains(tacticID);
	}

	public String[] getTacticIDs() {
		if (tacticIDs == null)
			loadRegistry();

		return tacticIDs.toArray(new String[tacticIDs.size()]);
	}
	
}
