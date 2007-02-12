/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eventb.internal.core.tool.graph.ConfigGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class BaseConfigManager extends ConfigManager<String, BaseConfig> {
	
	// Debug flag set from tracing options 
	public static boolean VERBOSE = false;

	private static final BaseConfigManager MANAGER = new BaseConfigManager();
	
	@Override
	protected void analyseConfigs(List<BaseConfig> configList) {
		ConfigGraph graph = new ConfigGraph();
		graph.addAll(configList);
		graph.sort();
		if(!graph.isPartialOrder())
			throw new IllegalStateException(
					"Configuration graph is cyclic. Involved configurations: " + 
					graph.getCycle(), null);
	}

	public static BaseConfigManager getInstance() {
		return MANAGER;
	}
	
	private BaseConfigManager() {
		super(VERBOSE);
	}

	@Override
	protected String getName() {
		return "Base Config";
	}

	@Override
	protected BaseConfig makeConfig(IConfigurationElement element) {
		return new BaseConfig(element);
	}

	@Override
	protected void printConfig(BaseConfig config) {
		System.out.println("  " + config.getId());
		System.out.println("    name: " + config.getName());
		System.out.println("    included: " + config.getIncluded());		
	}

}
