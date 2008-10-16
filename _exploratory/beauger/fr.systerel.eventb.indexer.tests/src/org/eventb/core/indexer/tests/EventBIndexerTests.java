/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.indexer.tests;

import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.AbstractRodinDBTests;

/**
 * @author Nicolas Beauger
 *
 */
public abstract class EventBIndexerTests extends AbstractRodinDBTests {

	protected static final String EVT1 = "evt1";
	protected static final String IMPORTER = "importer";
	protected static final String EXPORTER = "exporter";
	protected static final String VAR1 = "var1";
	protected static final String PRM1 = "prm1";
	protected static final String CST1 = "cst1";
	protected static IRodinProject project;

	/**
	 * @param name
	 */
	@SuppressWarnings("restriction")
	public EventBIndexerTests(String name) {
		super(name);
		RodinIndexer.disableIndexing();
	}

	protected void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
	}

	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

}
