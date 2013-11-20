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
package org.eventb.core.tests.extension;

import static java.util.Collections.emptySet;
import static org.eventb.core.ast.FormulaFactory.getInstance;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.extension.IFormulaExtensionProvider;
import org.eventb.core.tests.BuilderTest;
import org.rodinp.core.IRodinFile;

/**
 * An extension provider that implements the formula extension provider
 * extension point. When asked about the extensions for an Event-B root, it
 * returns either an empty set or a singleton containing the Prime extension if
 * the root was previously registered.
 * 
 * @see Prime
 */
public class PrimeFormulaExtensionProvider implements IFormulaExtensionProvider {

	/**
	 * The factory with the Prime extension.
	 */
	public static final FormulaFactory EXT_FACTORY = getInstance(Prime
			.getPrime());

	/**
	 * The default formula factory.
	 */
	public static final FormulaFactory DEFAULT = getInstance();

	// Set of roots for which the prime extension shall be returned
	private static final Set<IEventBRoot> rootsWithPrime = new HashSet<IEventBRoot>();

	@Override
	public String getId() {
		return BuilderTest.PLUGIN_ID + ".PrimeFormulaExtensionProvider";
	}

	@Override
	public Set<IFormulaExtension> getFormulaExtensions(IEventBRoot root) {
		final FormulaFactory factory;
		if (rootsWithPrime.contains(root)) {
			factory = EXT_FACTORY;
		} else {
			factory = DEFAULT;
		}
		return factory.getExtensions();
	}

	@Override
	public Set<IRodinFile> getFactoryFiles(IEventBRoot root) {
		// Not tested here
		return emptySet();
	}

	/**
	 * Clears the set of roots for which prime shall be returned.
	 */
	public static void clear() {
		rootsWithPrime.clear();
	}

	/**
	 * Registers the given root as using the prime extension.
	 */
	public static void add(IEventBRoot root) {
		rootsWithPrime.add(root);
	}

}
