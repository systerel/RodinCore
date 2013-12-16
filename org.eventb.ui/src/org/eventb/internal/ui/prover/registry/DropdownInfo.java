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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eventb.internal.ui.prover.ProverUIUtils;

public class DropdownInfo extends AbstractInfo {

	private final String toolbarId;

	private final List<TacticUIInfo> tactics = new ArrayList<TacticUIInfo>();

	public DropdownInfo(Map<String, TacticUIInfo> globalRegistry, String id,
			String toolbarId) {
		super(id);
		this.toolbarId = toolbarId;
		initTactics(globalRegistry);
	}

	public String getToolbar() {
		return toolbarId;
	}

	private void initTactics(Map<String, TacticUIInfo> globalRegistry) {
		for (String tacticID : globalRegistry.keySet()) {
			final TacticUIInfo info = globalRegistry.get(tacticID);
			if (id.equals(info.getDropdown())) {
				tactics.add(info);
				if (ProverUIUtils.DEBUG)
					ProverUIUtils.debug("Attached tactic " + tacticID
							+ " to dropdown " + id);
			}
		}
	}
	
	public List<TacticUIInfo> getTactics() {
		return new ArrayList<TacticUIInfo>(tactics);
	}

}