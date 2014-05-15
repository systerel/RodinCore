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
package org.rodinp.core.tests.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.internal.core.builder.NullToolDescription;
import org.rodinp.internal.core.builder.ToolDescription;
import org.rodinp.internal.core.builder.ToolManager;

public class NullToolTest extends AbstractBuilderTest{

	@Test
	public void testLoadNullToolDescription() throws Exception {
		final String name = "NullTool.id";
		final ToolManager manager = ToolManager.getToolManager();
		final ToolDescription desc = manager.getToolDescription(name);
		assertTrue("The description " + desc.getId()
				+ "is not a NullToolDescription",
				desc instanceof NullToolDescription);
		assertEquals(name, desc.getId());
		final IAutomaticTool tool = desc.getTool();
		assertNotNull(tool);	
	}
	
}
