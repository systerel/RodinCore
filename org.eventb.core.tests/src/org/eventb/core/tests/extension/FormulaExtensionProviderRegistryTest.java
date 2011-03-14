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
package org.eventb.core.tests.extension;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContextRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.tests.EventBTest;
import org.eventb.internal.core.FormulaExtensionProviderRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the formula extension provider registry.
 */
public class FormulaExtensionProviderRegistryTest extends EventBTest {

	protected IWorkspace workspace = ResourcesPlugin.getWorkspace();

	private IContextRoot contextRoot;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		contextRoot = createContext("ctx");
	}

	@After
	public void cleanUp() throws CoreException {
		rodinProject.getResource().delete(true, null);
	}

	/**
	 * Tests that a 'Prime' extension is added to the existing default
	 * extensions of the factory through the formula extension provider
	 * mechanism. This test aims to show that the mechanism of extension
	 * providers works. The extension 'Prime' is static so it is possible to
	 * compare instances.
	 */
	@Test
	public void testFormulaFactoriesEquals() {
		final Set<IFormulaExtension> expected = Collections.singleton(Prime
				.getPrime());

		final FormulaFactory factory1 = FormulaExtensionProviderRegistry
				.getExtensionProviderRegistry().getFormulaFactory(contextRoot);
		final Set<IFormulaExtension> actual = factory1.getExtensions();
		
		assertEquals("wrong extensions", expected, actual);
	}

}
