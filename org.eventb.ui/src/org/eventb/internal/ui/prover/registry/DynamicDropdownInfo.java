/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.registry;

import org.eventb.ui.prover.IUIDynTacticProvider;

/**
 * @author beauger
 * 
 */
public class DynamicDropdownInfo extends AbstractInfo {

	private final String name;
	private final String toolbar;
	private final IUIDynTacticProvider provider;

	public DynamicDropdownInfo(String id, String name, String toolbar,
			IUIDynTacticProvider provider) {
		super(id);
		this.name = name;
		this.toolbar = toolbar;
		this.provider = provider;
	}

	public String getName() {
		return name;
	}

	public String getToolbar() {
		return toolbar;
	}
	
	public IUIDynTacticProvider getTacticProvider() {
		return provider;
	}

}
