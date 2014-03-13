/*******************************************************************************
 * Copyright (c) 2013, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.registry;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eventb.ui.prover.IUIDynTacticProvider;

/**
 * Stores information about contribution elements of type "dynamic_dropdown" of
 * extension point "org.eventb.ui.proofTactics".
 * 
 * @author beauger
 * 
 */
public class DynamicDropdownInfo extends AbstractUIInfo {

	private final String toolbar;
	private final IUIDynTacticProvider provider;

	public DynamicDropdownInfo(String id, String name,
			ImageDescriptor iconDesc, String toolbar,
			IUIDynTacticProvider provider) {
		super(id, name, iconDesc);
		this.toolbar = toolbar;
		this.provider = provider;
	}

	public String getToolbar() {
		return toolbar;
	}

	public IUIDynTacticProvider getTacticProvider() {
		return provider;
	}

}
