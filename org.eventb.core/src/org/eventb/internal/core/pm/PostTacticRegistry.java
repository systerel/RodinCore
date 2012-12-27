/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.pm;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.EventBPlugin;
import org.eventb.core.pm.IPostTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.SequentProver;

@Deprecated
public class PostTacticRegistry implements IPostTacticRegistry {

	// The identifier of the extension point (value
	// <code>"org.eventb.core.postTactics"</code>).
	private final static String POSTTACTICS_ID = EventBPlugin.PLUGIN_ID
			+ ".postTactics";

	// The static instance of this singleton class
	private static IPostTacticRegistry instance;

	private List<String> tacticIDs = null;
	
	/**
	 * Constructor.
	 * <p>
	 * A private constructor to prevent creating an instance of this class
	 * directly
	 */
	private PostTacticRegistry() {
		// Singleton to hide the constructor
	}

	/**
	 * Getting the default instance of this class (create a new instance of it
	 * does not exist before)
	 * <p>
	 * 
	 * @return An instance of this class
	 */
	public static IPostTacticRegistry getDefault() {
		if (instance == null)
			instance = new PostTacticRegistry();
		return instance;
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
		IExtensionPoint extensionPoint = reg.getExtensionPoint(POSTTACTICS_ID);
		IConfigurationElement[] configurations = extensionPoint
				.getConfigurationElements();
		for (IConfigurationElement configuration : configurations) {
			String id = configuration.getAttribute("id"); //$NON-NLS-1$
			IAutoTacticRegistry tacticRegistry = SequentProver.getAutoTacticRegistry();
			// Check if the id is registered
			if (tacticIDs.contains(id)) {
				if (UserSupportUtils.DEBUG) {
					System.out
							.println("Tactic "
									+ id
									+ " is already registered, ignore this configuration.");
				}
			}
			else if (tacticRegistry.isRegistered(id)) {
				tacticIDs.add(id);
			} else {
				if (UserSupportUtils.DEBUG) {
					System.out.println("Tactic " + id
							+ " is not registered, ignore this configuration.");
				}
			}
		}
	}

	@Override
	public boolean isDeclared(String tacticID) {
		if (tacticIDs == null)
			loadRegistry();
		
		return tacticIDs.contains(tacticID);
	}

	@Override
	public String[] getTacticIDs() {
		if (tacticIDs == null)
			loadRegistry();

		return tacticIDs.toArray(new String[tacticIDs.size()]);
	}
	
}
