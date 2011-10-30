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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eventb.internal.ui.prover.ProverUIUtils;

public class ToolbarInfo {

	// FIXME remove both variables (should be computed earlier)
	private final Map<String, TacticUIInfo> globalRegistry;
	private final Map<String, DropdownInfo> dropdownRegistry;

	IConfigurationElement configuration;

	Collection<String> dropdowns;

	Collection<String> tactics;

	public ToolbarInfo(Map<String, TacticUIInfo> globalRegistry,
			Map<String, DropdownInfo> dropdownRegistry,
			IConfigurationElement configuration) {
		this.globalRegistry = globalRegistry;
		this.dropdownRegistry = dropdownRegistry;
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
					if (ProverUIUtils.DEBUG)
						ProverUIUtils.debug("Attached tactic " + tacticID
								+ " to toolbar " + id);
				}
			}
		}

		return tactics;
	}
}