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

import static org.eventb.core.tests.extension.PrimeFormulaExtensionProvider.EXT_FACTORY;
import static org.eventb.internal.core.FormulaExtensionProviderRegistry.getExtensionProviderRegistry;
import static org.junit.Assert.assertSame;

import org.eventb.core.IContextRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.tests.EventBTest;
import org.eventb.internal.core.FormulaExtensionProviderRegistry;
import org.junit.Test;

/**
 * Unit tests for the formula extension provider registry.
 */
public class FormulaExtensionProviderRegistryTest extends EventBTest {

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

	private void assertFormulaFactory(IContextRoot root, FormulaFactory expected) {
		final FormulaExtensionProviderRegistry registry = getExtensionProviderRegistry();
		final FormulaFactory actual = registry.getFormulaFactory(root);
		assertSame(expected, actual);
	}

}
