/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core;

import static org.eventb.internal.core.Util.log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBProject;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.extension.IFormulaExtensionProvider;

/**
 * Singleton class implementing the formula extension provider registry.
 * 
 */
public class FormulaExtensionProviderRegistry {

	private static String PROVIDERS_ID = EventBPlugin.PLUGIN_ID
			+ ".formulaExtensionProviders";

	private static final FormulaExtensionProviderRegistry SINGLETON_INSTANCE = new FormulaExtensionProviderRegistry();

	private static final String[] NO_STRING = new String[0];

	/**
	 * Debug flag for <code>EXTENSIONPROVIDER_REGISTRY_TRACE</code>
	 */
	public static boolean DEBUG;

	private Map<String, IFormulaExtensionProvider> registry;

	/**
	 * Private default constructor enforces that only one instance of this class
	 * is present.
	 */
	private FormulaExtensionProviderRegistry() {
		// Singleton implementation
	}

	public static FormulaExtensionProviderRegistry getExtensionProviderRegistry() {
		return SINGLETON_INSTANCE;
	}

	public synchronized boolean isRegistered(String providerID) {
		if (registry == null) {
			loadRegistry();
		}
		return registry.containsKey(providerID);
	}

	public synchronized String[] getRegisteredIDs() {
		if (registry == null) {
			loadRegistry();
		}
		return registry.keySet().toArray(NO_STRING);
	}

	public synchronized Set<IFormulaExtension> getFormulaExtensions(
			IEventBProject project) {
		final Set<IFormulaExtension> extensions = new HashSet<IFormulaExtension>();
		for (Entry<String, IFormulaExtensionProvider> entry : registry
				.entrySet()) {
			final IFormulaExtensionProvider provider = entry.getValue();
			if (provider != null) {
				extensions.addAll(provider.getFormulaExtensions(project));
			}
		}
		return extensions;
	}

	public synchronized FormulaFactory getFormulaFactory(IEventBProject project) {
		if (registry == null) {
			loadRegistry();
		}
		final Set<IFormulaExtension> extensions = getFormulaExtensions(project);
		return FormulaFactory.getInstance(extensions);
	}

	/**
	 * Initializes the registry using extensions to the formula extension
	 * provider extension point.
	 */
	private synchronized void loadRegistry() {
		if (registry != null) {
			// Prevents loading by two thread in parallel
			return;
		}
		registry = new HashMap<String, IFormulaExtensionProvider>();
		final IExtensionRegistry xRegistry = Platform.getExtensionRegistry();
		final IExtensionPoint xPoint = xRegistry
				.getExtensionPoint(PROVIDERS_ID);
		for (IConfigurationElement element : xPoint.getConfigurationElements()) {
			try {
				final IFormulaExtensionProvider provider = (IFormulaExtensionProvider) element
						.createExecutableExtension("class");
				final String id = element.getAttribute("id");
				final IFormulaExtensionProvider old = registry.put(id, provider);  
				if (old != null) {
					registry.put(id, old);
					log(null, "Duplicate provider extension " + id
							+ " ignored");
				} else {
					if (DEBUG)
						System.out.println("Registered provider extension "
								+ id);
				}
			} catch (CoreException e) {
				log(e, "while loading extension providers registry");
			}
		}
	}

}
