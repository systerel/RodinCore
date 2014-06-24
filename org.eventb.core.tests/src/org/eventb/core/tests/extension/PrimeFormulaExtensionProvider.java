/*******************************************************************************
 * Copyright (c) 2010, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.extension;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.eventb.core.ast.FormulaFactory.getInstance;
import static org.junit.Assert.assertFalse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ILanguage;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.extension.IFormulaExtensionProvider;
import org.eventb.core.tests.BuilderTest;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * An extension provider that implements the formula extension provider
 * extension point. When asked about the extensions for an Event-B root, it
 * returns either an empty set or a singleton containing the Prime extension if
 * the root was previously registered.
 * <p>
 * It can also serialize and deserialize a formula factory in a language element,
 * to some extent (only the default and the prime extension factories are
 * supported).
 * </p>
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

	public static final IAttributeType.String FACTORY_ATTR = RodinCore
			.getStringAttrType(BuilderTest.PLUGIN_ID + ".factoryName");

	// Poor man's bidirectional map.
	private static final List<FormulaFactory> FACTORY_VALUES = asList(DEFAULT,
			EXT_FACTORY);
	private static final List<String> FACTORY_NAMES = asList("Default",
			"Ext Prime");

	public static boolean erroneousLoadFormulaFactory = false;
	public static boolean erroneousSaveFormulaFactory = false;

	public static void reset() {
		erroneousLoadFormulaFactory = false;
		erroneousSaveFormulaFactory = false;
	}

	static void maybeFail(boolean condition) {
		if (condition) {
			throw new RuntimeException(
					"Error in adversarial formula extension provider");
		}
	}

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

	@Override
	public void saveFormulaFactory(ILanguage element, FormulaFactory factory,
			IProgressMonitor monitor) throws RodinDBException {
		maybeFail(erroneousSaveFormulaFactory);
		if (element.hasChildren() || element.getAttributeTypes().length != 0) {
			final IStatus status = new Status(Status.ERROR,
					BuilderTest.PLUGIN_ID,
					"Non-empty element in saveFormulaFactory(): " + element);
			throw new RodinDBException(new CoreException(status));
		}
		final int idx = FACTORY_VALUES.indexOf(factory);
		assertFalse(idx < 0);
		element.setAttributeValue(FACTORY_ATTR, FACTORY_NAMES.get(idx), null);
	}

	@Override
	public FormulaFactory loadFormulaFactory(ILanguage element,
			IProgressMonitor monitor) throws RodinDBException {
		maybeFail(erroneousLoadFormulaFactory);
		final String name = element.getAttributeValue(FACTORY_ATTR);
		final int idx = FACTORY_NAMES.indexOf(name);
		assertFalse(idx < 0);
		return FACTORY_VALUES.get(idx);
	}

}
