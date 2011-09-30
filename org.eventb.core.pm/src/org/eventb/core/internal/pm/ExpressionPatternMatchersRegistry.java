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
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.pm.ExtendedExpressionMatcher;
import org.eventb.core.pm.IExpressionMatcher;
import org.eventb.core.pm.basis.engine.MatchingUtilities;
import org.eventb.core.pm.plugin.PMPlugin;

/**
 * 
 * @author maamria
 *
 */
public class ExpressionPatternMatchersRegistry {
	
	private final static String MATCHER_ID = PMPlugin.PLUGIN_ID + ".extendedExpressionMatcher";

	private static ExpressionPatternMatchersRegistry SINGLETON_INSTANCE;

	public static boolean DEBUG;

	private Map<Class<? extends IExpressionExtension>, ExtendedExpressionMatcher<? extends IExpressionExtension>> matchers;

	/**
	 * Private default constructor enforces that only one instance of this class
	 * is present.
	 */
	private ExpressionPatternMatchersRegistry() {
		// Singleton implementation
		if (matchers == null) {
			loadMatchers();
		}
	}

	public static ExpressionPatternMatchersRegistry getMatchersRegistry() {
		if(SINGLETON_INSTANCE == null){
			SINGLETON_INSTANCE = new ExpressionPatternMatchersRegistry();
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
		matchers = new LinkedHashMap<Class<? extends IExpressionExtension>, ExtendedExpressionMatcher<? extends IExpressionExtension>>();
		final IExtensionRegistry xRegistry = Platform.getExtensionRegistry();
		final IExtensionPoint xPoint = xRegistry.getExtensionPoint(MATCHER_ID);
		for (IConfigurationElement element : xPoint.getConfigurationElements()) {
			try {
				final ExtendedExpressionMatcher<? extends IExpressionExtension> matcher = 
					(ExtendedExpressionMatcher<?>) element.createExecutableExtension("class");
				if(matcher != null){
					matchers.put(matcher.getExtensionClass(), matcher);
				}
				
			} catch (CoreException e) {
				MatchingUtilities.log(e, "error while loading extended expression matcher");
			}
		}
	}

	public synchronized IExpressionMatcher getMatcher(Class<? extends IExpressionExtension> clazz){
		return matchers.get(clazz);
	}
	
}
