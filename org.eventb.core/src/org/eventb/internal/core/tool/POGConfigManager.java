/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eventb.internal.core.tool.BasicDesc.ModuleLoadingException;
import org.eventb.internal.core.tool.types.IModule;

/**
 * @author Stefan Hallerstede
 *
 */
public class POGConfigManager extends ConfigManager<ModuleDesc<? extends IModule>, ModuleConfig> {
	
	// Debug flag set from tracing options 
	public static boolean VERBOSE = false;
	
	private static final String POG_CONFIG_ID = "pogModule";

	private static final POGConfigManager MANAGER = new POGConfigManager();
	
	public static POGConfigManager getInstance() {
		return MANAGER;
	}
	
	private static final POGModuleManager POG_MODULE_MANAGER = POGModuleManager.getInstance();
	
	private POGConfigManager() {
		super(VERBOSE);
	}

	@Override
	protected String getName() {
		return "POG Module Config";
	}

	@Override
	protected ModuleConfig makeConfig(IConfigurationElement element) throws ModuleLoadingException {
		return new ModuleConfig(POG_CONFIG_ID, element, POG_MODULE_MANAGER);
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
