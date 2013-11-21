/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ILanguage;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.extension.IFormulaExtensionProvider;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Singleton class implementing the formula extension provider registry.
 * 
 */
public class FormulaExtensionProviderRegistry {

	private static String PROVIDERS_ID = EventBPlugin.PLUGIN_ID
			+ ".formulaExtensionProviders";

	private static final FormulaExtensionProviderRegistry SINGLETON_INSTANCE = new FormulaExtensionProviderRegistry();

	/**
	 * Debug flag for <code>EXTENSIONPROVIDER_REGISTRY_TRACE</code>
	 */
	public static boolean DEBUG;

	private IFormulaExtensionProvider provider;

	/**
	 * Private default constructor enforces that only one instance of this class
	 * is present.
	 */
	private FormulaExtensionProviderRegistry() {
		// Singleton implementation
		if (provider == null) {
			loadProvider();
		}
	}

	public static FormulaExtensionProviderRegistry getExtensionProviderRegistry() {
		return SINGLETON_INSTANCE;
	}

	public synchronized boolean isCurrentRegisteredProvider(String providerID) {
		if (provider == null)
			return false;
		return provider.getId().equals(providerID);
	}

	public synchronized String getRegisteredProviderID() {
		if (provider == null)
			return "no_id";
		return provider.getId();
	}

	public synchronized Set<IFormulaExtension> getFormulaExtensions(
			IEventBRoot root) {
		if (provider == null) {
			return Collections.emptySet();
		}
		return provider.getFormulaExtensions(root);
	}

	public synchronized FormulaFactory getFormulaFactory(IEventBRoot root) {
		return FormulaFactory.getInstance(getFormulaExtensions(root));
	}

	public synchronized FormulaFactory getFormulaFactory(ILanguage language,
			IProgressMonitor pm) throws RodinDBException {
		if (provider != null) {
			return provider.loadFormulaFactory(language, pm);
		}
		// No formula extensions available
		return FormulaFactory.getDefault();
	}

	public synchronized void setFormulaFactory(ILanguage language,
			FormulaFactory ff, IProgressMonitor pm) throws RodinDBException {
		if (provider != null) {
			provider.saveFormulaFactory(language, ff, pm);
		}
	}

	/**
	 * Initializes the provider using extensions to the formula extension
	 * provider extension point. It shall be only one extension provider.
	 */
	private synchronized void loadProvider() {
		if (provider != null) {
			// Prevents loading by two thread in parallel
			return;
		}
		final IExtensionRegistry xRegistry = Platform.getExtensionRegistry();
		final IExtensionPoint xPoint = xRegistry
				.getExtensionPoint(PROVIDERS_ID);
		for (IConfigurationElement element : xPoint.getConfigurationElements()) {
			try {
				final String id = element.getAttribute("id");
				if (provider != null) {
					log(null, "Only one extension provider allowed. Provider"
							+ id + " ignored");
					break;
				} else {
					provider = (IFormulaExtensionProvider) element
							.createExecutableExtension("class");
				}
				if (DEBUG)
					System.out.println("Registered provider extension " + id);
			} catch (CoreException e) {
				log(e, "while loading extension provider");
			}
		}
	}

	public synchronized Set<IRodinFile> getAllExtensionFiles(
			IEventBRoot root) {
		if (provider == null)
			return Collections.emptySet();

		final Set<IRodinFile> extFiles = new HashSet<IRodinFile>();
		extFiles.addAll(provider.getFactoryFiles(root));

		return extFiles;
	}
}
