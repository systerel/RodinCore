/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.tool;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eventb.core.tool.IModule;
import org.eventb.internal.core.tool.BasicDesc.ModuleLoadingException;

/**
 * @author Stefan Hallerstede
 *
 */
public class SCConfigManager extends ConfigManager<ModuleDesc<? extends IModule>, ModuleConfig> {
	
	// Debug flag set from tracing options 
	public static boolean VERBOSE = false;
	
	private static final String SC_CONFIG_ID = "scModule";

	private static final SCConfigManager MANAGER = new SCConfigManager();
	
	public static SCConfigManager getInstance() {
		return MANAGER;
	}
	
	private static final SCModuleManager SC_MODULE_MANAGER = SCModuleManager.getInstance();
	
	private SCConfigManager() {
		super(VERBOSE);
	}

	@Override
	protected String getName() {
		return "SC Module Config";
	}

	@Override
	protected ModuleConfig makeConfig(IConfigurationElement element) throws ModuleLoadingException {
		return new ModuleConfig(SC_CONFIG_ID, element, SC_MODULE_MANAGER);
	}

	@Override
	protected void printConfig(ModuleConfig config) {
		System.out.println("  " + config.getId());
		System.out.println("    name: " + config.getName());
		for (ModuleDesc<? extends IModule> desc : config.getModuleDescs()) {
			System.out.println("    - " + desc.getId());
		}
	}

}
