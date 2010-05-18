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
package org.rodinp.core.tests.builder;

import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.internal.core.builder.NullToolDescription;
import org.rodinp.internal.core.builder.ToolDescription;
import org.rodinp.internal.core.builder.ToolManager;

public class NullToolTest extends AbstractBuilderTest{

	public NullToolTest(String name) {
		super(name);
	}

	public void testLoadNullToolDescription() throws Exception {
		final String name = "NullTool.id";
		final ToolManager manager = ToolManager.getToolManager();
		final ToolDescription desc = manager.getToolDescription("NullTool.id");
		assertTrue("The description " + desc.getId()
				+ "is not a NullToolDescription",
				desc instanceof NullToolDescription);
		assertEquals(name, desc.getId());
		final IAutomaticTool tool = desc.getTool();
		assertNotNull(tool);	
	}
	
}
