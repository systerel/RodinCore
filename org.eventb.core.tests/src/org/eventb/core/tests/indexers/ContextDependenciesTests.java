/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.indexers;

import static junit.framework.Assert.fail;
import static org.eventb.core.tests.indexers.ListAssert.assertSameAsArray;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.tests.ResourceUtils;
import org.eventb.internal.core.indexers.ContextIndexer;
import org.junit.Test;
import org.rodinp.core.IRodinFile;

/**
 * @author Nicolas Beauger
 * 
 */
public class ContextDependenciesTests extends EventBIndexerTests {

	private static final List<IRodinFile> NO_FILES = Collections.emptyList();
	private static final String C1_NAME = "c1";
	private static final String C2_NAME = "c2";
	private static final String C3_NAME = "c3";

	private static void assertDependencies(List<IRodinFile> expected,
			IRodinFile[] actual) {
		assertSameAsArray(expected, actual, "dependencies");
	}

	private static final String CTX_1EXT =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
					+ "<org.eventb.core.extendsContext"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.target=\"c1\"/>"
					+ "</org.eventb.core.contextFile>";

	@Test
	public void testNoDependencies() throws Exception {

		final IContextRoot file =
			ResourceUtils.createContext(rodinProject, C1_NAME, EMPTY_CONTEXT);

		final ContextIndexer indexer = new ContextIndexer();

		final IRodinFile[] actual = indexer.getDependencies(file);

		assertDependencies(NO_FILES, actual);
	}

	@Test
	public void testOneDependence() throws Exception {

		final IContextRoot parent =
				ResourceUtils.createContext(rodinProject, C1_NAME, EMPTY_CONTEXT);

		final IContextRoot child = ResourceUtils.createContext(rodinProject, C2_NAME, CTX_1EXT);

		final ContextIndexer indexer = new ContextIndexer();

		final IRodinFile[] actual = indexer.getDependencies(child);

		final List<IRodinFile> expected = Arrays.asList(parent.getRodinFile());

		assertDependencies(expected, actual);
	}

	@Test
	public void testTwoDependencies() throws Exception {

		final IContextRoot parentC1 =
				ResourceUtils.createContext(rodinProject, C1_NAME, EMPTY_CONTEXT);

		final IContextRoot parentC2 =
				ResourceUtils.createContext(rodinProject, C2_NAME, EMPTY_CONTEXT);

		final String CTX_2EXT =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
						+ "		<org.eventb.core.extendsContext"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.target=\"c1\"/>"
						+ "<org.eventb.core.extendsContext"
						+ "		name=\"internal_element2\""
						+ "		org.eventb.core.target=\"c2\"/>"
						+ "</org.eventb.core.contextFile>";

		final IContextRoot child = ResourceUtils.createContext(rodinProject, C3_NAME, CTX_2EXT);

		final ContextIndexer indexer = new ContextIndexer();

		final IRodinFile[] actual = indexer.getDependencies(child);

		final List<IRodinFile> expected =
				Arrays.asList(parentC1.getRodinFile(), parentC2.getRodinFile());

		assertDependencies(expected, actual);
	}

	@Test
	public void testBadFileType() throws Exception {
		final IMachineRoot machine =
			ResourceUtils.createMachine(rodinProject, "file.bum", VAR_1DECL_1REF_INV);

		final ContextIndexer indexer = new ContextIndexer();

		try {
			indexer.getDependencies(machine);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testMalformedXML() throws Exception {
		// missing closing " after internal_element1 in extendsContext node
		final String MALFORMED_CONTEXT =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
						+ "<org.eventb.core.extendsContext"
						+ "		name=\"internal_element1"
						+ "		org.eventb.core.target=\"c1\"/>"
						+ "</org.eventb.core.contextFile>";

		final IContextRoot context =
				ResourceUtils.createContext(rodinProject, ResourceUtils.CTX_BARE_NAME,
						MALFORMED_CONTEXT);

		final ContextIndexer indexer = new ContextIndexer();

		// should not throw an exception
		indexer.getDependencies(context);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testMissingAttribute() throws Exception {
		final String CTX_1EXT_NO_TARGET_ATT =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
						+ "<org.eventb.core.extendsContext"
						+ "		name=\"internal_element1\"/>"
						+ "</org.eventb.core.contextFile>";

		final IContextRoot context =
				ResourceUtils.createContext(rodinProject, ResourceUtils.CTX_BARE_NAME,
						CTX_1EXT_NO_TARGET_ATT);

		final ContextIndexer indexer = new ContextIndexer();

		// should not throw an exception
		indexer.getDependencies(context);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testFileDoesNotExist() throws Exception {

		final IContextRoot child = ResourceUtils.createContext(rodinProject, C2_NAME, CTX_1EXT);

		final ContextIndexer indexer = new ContextIndexer();

		// should not throw an exception
		indexer.getDependencies(child);
	}

}
