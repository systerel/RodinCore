/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.internal.pm;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.pm.ExtendedPredicateMatcher;
import org.eventb.core.pm.IPredicateMatcher;
import org.eventb.core.pm.basis.engine.MatchingUtilities;
import org.eventb.core.pm.plugin.PMPlugin;

/**
 * 
 * @author maamria
 *
 */
public class PredicatePatternMatchersRegistry {

	private final static String MATCHER_ID = PMPlugin.PLUGIN_ID + ".extendedPredicateMatcher";

	private static PredicatePatternMatchersRegistry SINGLETON_INSTANCE;

	public static boolean DEBUG;

	private Map<Class<? extends IPredicateExtension>, ExtendedPredicateMatcher<? extends IPredicateExtension>> matchers;

	/**
	 * Private default constructor enforces that only one instance of this class
	 * is present.
	 */
	private PredicatePatternMatchersRegistry() {
		// Singleton implementation
		if (matchers == null) {
			loadMatchers();
		}
	}

	public static PredicatePatternMatchersRegistry getMatchersRegistry() {
		if(SINGLETON_INSTANCE == null){
			SINGLETON_INSTANCE = new PredicatePatternMatchersRegistry();
		}
		return SINGLETON_INSTANCE;
	}

	/**
	 * Initialises the provider using extensions to the formula extension
	 * provider extension point. It shall be only one extension provider.
	 */
	private synchronized void loadMatchers() {
		if (matchers != null) {
			// Prevents loading by two thread in parallel
			return;
		}
		matchers = new LinkedHashMap<Class<? extends IPredicateExtension>, ExtendedPredicateMatcher<? extends IPredicateExtension>>();
		final IExtensionRegistry xRegistry = Platform.getExtensionRegistry();
		final IExtensionPoint xPoint = xRegistry.getExtensionPoint(MATCHER_ID);
		for (IConfigurationElement element : xPoint.getConfigurationElements()) {
			try {
				final ExtendedPredicateMatcher<? extends IPredicateExtension> matcher = 
					(ExtendedPredicateMatcher<?>) element.createExecutableExtension("class");
				if(matcher != null){
					matchers.put(matcher.getExtensionClass(), matcher);
				}
				
			} catch (CoreException e) {
				MatchingUtilities.log(e, "error while loading extended predicate matcher");
			}
		}
	}

	public synchronized IPredicateMatcher getMatcher(Class<? extends IPredicateExtension> clazz){
		return matchers.get(clazz);
	}
	
}
