/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool;

import java.util.List;

import org.eventb.core.sc.ISCFilterModule;
import org.eventb.core.sc.ISCProcessorModule;
import org.eventb.core.tool.IModule;
import org.eventb.internal.core.tool.graph.ModuleGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class SCModuleManager extends ModuleManager<ISCFilterModule, ISCProcessorModule> {

	private static final String SC_MODULES_ID = "scModules";
	private static final SCModuleManager MANAGER = new SCModuleManager();
	
	private static final SCConfigManager SC_CONFIG_MANAGER = SCConfigManager.getInstance();
	
	public static SCModuleManager getInstance() {
		return MANAGER;
	}
	
	private SCModuleManager() {
		super(SC_MODULES_ID);
	}

	@Override
	protected List<ModuleDesc<? extends IModule>> getModuleListForConfig(String configId) {
		return SC_CONFIG_MANAGER.getConfigClosure(configId);
	}

	@Override
	protected ModuleFactory<ISCFilterModule, ISCProcessorModule> computeModuleFactory(ModuleGraph moduleGraph) {
		return new SCModuleFactory(moduleGraph, this);
	}

}
