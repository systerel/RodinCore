/*******************************************************************************
 * Copyright (c) 2005, 2024 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored to use ITacticProvider2 and ITacticApplication
 *     UPEC - refactored as immutable object with data computed in parser
 *******************************************************************************/
package org.eventb.internal.ui.prover.registry;

import static java.util.Collections.unmodifiableList;

import java.util.List;

public class ToolbarInfo extends AbstractInfo {

	private final List<DropdownInfo> dropdowns;

	private final List<DynamicDropdownInfo> dynDropdowns;

	private final List<TacticUIInfo> tactics;

	public ToolbarInfo(List<TacticUIInfo> tactics, List<DropdownInfo> dropdowns, List<DynamicDropdownInfo> dynDropdowns,
			String id) {
		super(id);
		this.tactics = unmodifiableList(tactics);
		this.dropdowns = unmodifiableList(dropdowns);
		this.dynDropdowns = unmodifiableList(dynDropdowns);
	}

	public List<DropdownInfo> getDropdowns() {
		return dropdowns;
	}

	public List<TacticUIInfo> getTactics() {
		return tactics;
	}

	public List<DynamicDropdownInfo> getDynamicDropdowns() {
		return dynDropdowns;
	}
}