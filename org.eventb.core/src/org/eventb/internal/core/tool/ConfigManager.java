/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - allowed for multiple configurations in input files
 *     Systerel - added getUnknownConfigs()
 *******************************************************************************/
package org.eventb.internal.core.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.EventBPlugin;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.tool.BasicDesc.ModuleLoadingException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ConfigManager<T, C extends ConfigWithClosure<T>> extends SortingUtil {
	
	// Local id of the configuration extension point of this plugin
	private static final String CONFIGURATION_ID = "configurations";
	
	// Separator between configuration ids
	private static final String CONFIG_DELIM = ";";

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
			try {
				C config = makeConfig(element);
				register(config.getId(), config);
				configList.add(config);
			} catch (ModuleLoadingException e) {
				Util.log(e.getCause(), " while loading config "
						+ element.getName()
						+ " in "
						+ element.getNamespaceIdentifier());
				// ignore module
			}
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

	protected abstract C makeConfig(IConfigurationElement element) throws ModuleLoadingException;

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
	
	private static String[] splitConfigIds(String configIds) {
		return configIds.split(CONFIG_DELIM);
	}
	
	public List<T> getConfigClosure(String configIds) {
		final List<T> result = new ArrayList<T>();
		for (String configId : splitConfigIds(configIds)) {
			addConfigClosure(result, configId);
		}
		return result;
	}

	private void addConfigClosure(final List<T> moduleList, String configId) {
		C config = getConfig(configId);
		if (config != null)
			moduleList.addAll(config.computeClosure(configs));
	}

	public List<String> getUnknownConfigs(String configIds) {
		final List<String> result = new ArrayList<String>();
		for (String configId : splitConfigIds(configIds)) {
			final C config = getConfig(configId);
			if (config == null) {
				result.add(configId);
			}
		}
		return result;
	}
	
	protected abstract String getName();

	public ConfigManager(final boolean verbose) {
		super();
		this.verbose = verbose;
	}

}
