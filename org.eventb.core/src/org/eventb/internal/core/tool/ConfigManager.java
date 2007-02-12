/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.EventBPlugin;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ConfigManager<T, C extends ConfigWithClosure<T>> extends SortingUtil {
	
	// Local id of the configuration extension point of this plugin
	private static final String CONFIGURATION_ID = "configuration";
	
	// Access to configurations using their unique id
	private Map<String, C> configs;
	private final boolean verbose;

	private void register(String id, C config) {
		final C oldConfig = configs.put(id, config);
		if (oldConfig != null) {
			configs.put(id, oldConfig);
			throw new IllegalStateException(
					"[" + getName() + "] Attempt to create twice configuration " + id);
		}
	}
	
	public String[] getSortedConfigs() {
		return getSortedIds(configs);
	}

	private void computeConfigs() {
		configs = new HashMap<String, C>();
		
		List<C> configList = new LinkedList<C>();
		
		// Read the extension point extensions.
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = 
			registry.getConfigurationElementsFor(EventBPlugin.PLUGIN_ID, CONFIGURATION_ID);
		
		for (IConfigurationElement element: elements) {
			C config = makeConfig(element);
			register(config.getId(), config);
			configList.add(config);
		}

		if (verbose) {
			System.out.println("---------------------------------------------------");
			System.out.println("Configurations registered:");
			for (String id: getSortedIds(configs)) {
				C config = configs.get(id);
				printConfig(config);
			}
			System.out.println("---------------------------------------------------");
		}
		
		analyseConfigs(configList);
	}

	protected abstract void printConfig(C config);

	protected abstract C makeConfig(IConfigurationElement element);

	protected void analyseConfigs(List<C> configList) {
		// do nothing by default
	}

	/**
	 * Returns the configuration with the given id.
	 * 
	 * @param id
	 *            the id of the configuration to retrieve
	 * @return the configuration or <code>null</code> if this
	 *         configuration id is unknown.
	 */
	public C getConfig(String id) {

		if (configs == null) {
			computeConfigs();
		}
		return configs.get(id);
	}
	
	public List<T> getConfigClosure(String id) {
		C config = getConfig(id);
		if (config == null)
			return null;
		return config.computeClosure(configs);
	}
	
	protected abstract String getName();

	public ConfigManager(final boolean verbose) {
		super();
		this.verbose = verbose;
	}

}
