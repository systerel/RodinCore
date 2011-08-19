/*******************************************************************************
 * Copyright (c) 2008 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.ui.eventbeditor.tests;

import java.util.List;

import junit.framework.TestCase;

import org.eventb.internal.ui.eventbeditor.DependenciesPage;
import org.eventb.internal.ui.eventbeditor.EditorPagesRegistry;
import org.eventb.internal.ui.eventbeditor.EventBContextEditor;
import org.eventb.internal.ui.eventbeditor.EventBMachineEditor;
import org.eventb.internal.ui.eventbeditor.IEditorPagesRegistry;
import org.eventb.internal.ui.eventbeditor.htmlpage.HTMLPage;
import org.eventb.ui.eventbeditor.EventBEditorPage;
import org.eventb.ui.tests.EventBUITestsPlugin;
import org.eventb.ui.tests.utils.Util;
import org.junit.Test;

/**
 * @author htson
 *         <p>
 *         This is the sets of JUnit tests for the editor pages registry
 *         {@link EditorPagesRegistry}
 */
public class TestEditorPagesRegistry extends TestCase {

	// The registry under test.
	private IEditorPagesRegistry registry;

	// The test registry.
	private static final String TEST_EXTENSION_POINT_ID = EventBUITestsPlugin.PLUGIN_ID
			+ ".editorPages";

	// Some pre-defined IDs and names.
	private String htmlPageID = "org.eventb.ui.htmlpage";

	private String htmlPageName = "%editorPages.HTMLPageName";

	private String noIsDefaultPageID = "org.eventb.ui.variablepage";

	private String syntheticViewPageID = "org.eventb.ui.syntheticviewpage";

	private String dependenciesPageID = "org.eventb.ui.dependencypage";

	private String dependenciesPageName = "%editorPages.DependenciesPageName";

	private String editPageID = "org.eventb.ui.edit";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		registry = EditorPagesRegistry.getDefault();
		((EditorPagesRegistry) registry)
				.setAlternateExtensionPointID(TEST_EXTENSION_POINT_ID);
	}

	@Override
	protected void tearDown() throws Exception {
		((EditorPagesRegistry) registry).setAlternateExtensionPointID(null);
		super.tearDown();
	}

	/**
	 * Tests for creating an individual editor pages.
	 */
	@Test
	public void testCreatePage() {
		EventBEditorPage page = registry.createPage(
				EventBMachineEditor.EDITOR_ID, htmlPageID);
		assertNotNull("HTML page should not be null", page);
		assertTrue("Incorrect type for HTML page", page instanceof HTMLPage);

		page = registry.createPage(EventBContextEditor.EDITOR_ID, htmlPageID);
		assertNull("There should be no HTML Page for context editor", page);

		page = registry.createPage(EventBMachineEditor.EDITOR_ID,
				dependenciesPageID);
		assertNotNull("Dependency page for machines should not be null", page);
		assertTrue("Incorrect type for Dependency Page for machines",
				page instanceof DependenciesPage);

		page = registry.createPage(EventBContextEditor.EDITOR_ID,
				dependenciesPageID);
		assertNotNull("Dependency page for contexts should not be null", page);
		assertTrue("Incorrect type for Dependency Page for contexts",
				page instanceof DependenciesPage);
	}

	/**
	 * Test for creating editor pages.
	 */
	@Test
	public void testCreateAllPages() {
		EventBEditorPage[] pages = registry
				.createAllPages(EventBMachineEditor.EDITOR_ID);
		List<String> pageIDs = registry
				.getAllPageIDs(EventBMachineEditor.EDITOR_ID);
		assertEquals("Incorrect number of pages", pageIDs.size(), pages.length);
		int i = 0;
		for (String pageID : pageIDs) {
			assertEquals("Incorrect Page ID for page number " + i
					+ " in machine editor", pageID, pages[i++].getId());
		}
		pages = registry.createAllPages(EventBContextEditor.EDITOR_ID);
		pageIDs = registry.getAllPageIDs(EventBContextEditor.EDITOR_ID);
		assertEquals("Incorrect number of pages", pageIDs.size(), pages.length);
		i = 0;
		for (String pageID : pageIDs) {
			assertEquals("Incorrect Page ID for page number " + i
					+ " in context editor", pageID, pages[i++].getId());
		}
	}

	/**
	 * Tests for getting default pages' IDs.
	 */
	@Test
	public void testGetDefaultPageIDs() {
		List<String> defaultMachinePages = registry
				.getDefaultPageIDs(EventBMachineEditor.EDITOR_ID);
		assertStrings("Incorrect default pages for machine editor", htmlPageID
				+ "\n" + noIsDefaultPageID + "\n" + dependenciesPageID,
				defaultMachinePages);

		List<String> defaultContextPages = registry
				.getDefaultPageIDs(EventBContextEditor.EDITOR_ID);
		assertStrings("Incorrect default pages for context editor",
				syntheticViewPageID + "\n" + dependenciesPageID,
				defaultContextPages);
	}

	/**
	 * Test for getting the list of all pages' IDs.
	 */
	@Test
	public void testGetAllPageIDs() {
		List<String> machinePageIDs = registry
				.getAllPageIDs(EventBMachineEditor.EDITOR_ID);
		assertStrings("Incorrect page IDs for machine editor", htmlPageID
				+ "\n" + noIsDefaultPageID + "\n" + editPageID + "\n"
				+ dependenciesPageID, machinePageIDs);

		List<String> contextPageIDs = registry
				.getAllPageIDs(EventBContextEditor.EDITOR_ID);
		assertStrings("Incorrect page IDs for context editor",
				syntheticViewPageID + "\n" + dependenciesPageID, contextPageIDs);
	}

	/**
	 * Test for getting the name of an individual editor page.
	 */
	@Test
	public void testGetPageName() {
		String pageName = registry.getPageName(EventBMachineEditor.EDITOR_ID,
				htmlPageID);
		assertNotNull("HTML Page should have a name", pageName);
		assertEquals("Incorrect name for HTML Page", htmlPageName, pageName);

		pageName = registry.getPageName(EventBContextEditor.EDITOR_ID,
				htmlPageID);
		assertNull("There should be no HTML Page for context editor", pageName);

		pageName = registry.getPageName(EventBMachineEditor.EDITOR_ID,
				dependenciesPageID);
		assertNotNull("Dependencies Page for machines should have a name",
				pageName);
		assertEquals("Incorrect name for Dependencies Page for machines",
				dependenciesPageName, pageName);

		pageName = registry.getPageName(EventBContextEditor.EDITOR_ID,
				dependenciesPageID);
		assertNotNull("Dependencies Page for contexts should have a name",
				pageName);
		assertEquals("Incorrect name for Dependencies Page for contexts",
				dependenciesPageName, pageName);
	}

	/**
	 * An utility method used for testing array of strings.
	 * <p>
	 * 
	 * @param message
	 *            a message
	 * @param expected
	 *            the expected string representation
	 * @param objects
	 *            a list of string object({@link String}
	 */
	private void assertStrings(String message, String expected,
			List<String> objects) {
		StringBuilder builder = new StringBuilder();
		boolean sep = false;
		for (Object object : objects) {
			if (sep)
				builder.append('\n');
			builder.append(object);
			sep = true;
		}
		String actual = builder.toString();
		if (!expected.equals(actual)) {
			System.out.println(Util.displayString(actual));
			fail(message + ":\n" + actual);
		}
	}

}
