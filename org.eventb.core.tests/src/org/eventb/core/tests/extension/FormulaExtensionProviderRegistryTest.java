/*******************************************************************************
 * Copyright (c) 2010, 2023 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.extension;

import static org.eventb.core.tests.extension.PrimeFormulaExtensionProvider.DEFAULT;
import static org.eventb.core.tests.extension.PrimeFormulaExtensionProvider.EXT_FACTORY;
import static org.eventb.internal.core.FormulaExtensionProviderRegistry.getExtensionProviderRegistry;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContextRoot;
import org.eventb.core.ILanguage;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.tests.EventBTest;
import org.eventb.internal.core.FormulaExtensionProviderRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

/**
 * Unit tests for the formula extension provider registry.
 */
public class FormulaExtensionProviderRegistryTest extends EventBTest {

	/**
	 * Make sure that the extension provider remains in its default state.
	 */
	@Before
	@After
	public void resetExtensionProviderFlags() {
		PrimeFormulaExtensionProvider.reset();
	}

	/**
	 * Ensures that the factory associated to an Event-B root which is not
	 * registered is the regular one.
	 */
	@Test
	public void normalFactory() throws Exception {
		final IContextRoot root = createContext("ctx");
		assertFormulaFactory(root, FormulaFactory.getDefault());
	}

	/**
	 * Ensures that the factory associated to an Event-B root can contain some
	 * extensions.
	 */
	@Test
	public void specializedFactory() throws Exception {
		final IContextRoot root = createContext("ctx");
		PrimeFormulaExtensionProvider.add(root);
		assertFormulaFactory(root, EXT_FACTORY);
	}

	/**
	 * If a core exception is thrown by the provider, it is also thrown by the
	 * provider registry.
	 */
	@Test
	public void specializedFactoryCoreException() throws Exception {
		final IContextRoot root = createContext("ctx");
		PrimeFormulaExtensionProvider.erroneousGetExtensionsCoreExn = true;
		PrimeFormulaExtensionProvider.add(root);
		try {
			getExtensionProviderRegistry().getFormulaFactory(root);
			fail("Getting the factory should have thrown an exception");
		} catch (CoreException e) {
			// OK
		}
	}

	/**
	 * If a runtime exception is thrown by the provider, it is logged and a default
	 * factory is returned.
	 */
	@Test
	public void specializedFactoryRuntimeException() throws Exception {
		final IContextRoot root = createContext("ctx");
		PrimeFormulaExtensionProvider.erroneousGetExtensions = true;
		PrimeFormulaExtensionProvider.add(root);
		assertFormulaFactory(root, FormulaFactory.getDefault());
	}

	private void assertFormulaFactory(IContextRoot root, FormulaFactory expected) throws CoreException {
		final FormulaExtensionProviderRegistry registry = getExtensionProviderRegistry();
		final FormulaFactory actual = registry.getFormulaFactory(root);
		assertSame(expected, actual);
	}

	/**
	 * Ensures that both the default and a specialized factory can be serialized
	 * to and deserialized from a language element.
	 */
	@Test
	public void serializeFactory() throws Exception {
		final ILanguage lang = createLanguage();

		lang.setFormulaFactory(EXT_FACTORY, null);
		assertSame(EXT_FACTORY, lang.getFormulaFactory(null));

		lang.setFormulaFactory(DEFAULT, null);
		assertSame(DEFAULT, lang.getFormulaFactory(null));

	}

	/**
	 * Ensures that it is not possible to deserialize a factory from an empty
	 * element.
	 */
	@Test(expected = CoreException.class)
	public void deserializeFactoryError() throws Exception {
		final ILanguage lang = createLanguage();
		lang.getFormulaFactory(null);
	}

	private ILanguage createLanguage() throws RodinDBException {
		final IContextRoot root = createContext("ctx");
		return root.createChild(ILanguage.ELEMENT_TYPE, null, null);
	}

}
