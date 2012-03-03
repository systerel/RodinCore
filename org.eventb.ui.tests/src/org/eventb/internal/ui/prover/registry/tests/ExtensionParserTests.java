/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.registry.tests;

import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.eventb.internal.ui.prover.registry.ErrorStatuses.*;
import static org.eventb.internal.ui.prover.registry.ErrorStatuses.MISSING_ID;
import static org.eventb.internal.ui.prover.registry.ErrorStatuses.UNKNOWN_ELEMENT;
import static org.eventb.ui.EventBUIPlugin.PLUGIN_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eventb.internal.ui.prover.registry.ExtensionParser;
import org.junit.Test;

/**
 * Unit tests for class {@link ExtensionParser}.
 * 
 * @author Laurent Voisin
 */
public class ExtensionParserTests {

	private ExtensionParser parser = new ExtensionParser();

	@Test
	public void unknownElement() {
		runParser(ce("invalid", null));
		assertError(UNKNOWN_ELEMENT,
				"Unknown element 'invalid' contributed by some.stub.plugin");
	}

	@Test
	public void missingId() {
		runParser(ce("tactic", null));
		assertError(MISSING_ID,
				"Missing id in tactic extension contributed by some.stub.plugin");
	}

	@Test
	public void invalidId() {
		runParser(ce("tactic", ""));
		assertError(INVALID_ID,
				"Invalid id in tactic extension '' contributed by some.stub.plugin");
	}

	@Test
	public void duplicateId() {
		runParser(ce("toolbar", "foo"), ce("toolbar", "foo"));
		assertError(DUPLICATE_ID,
				"Duplicate id for toolbar extension 'foo' contributed by some.stub.plugin");
	}

	@Test
	public void duplicateIdQualified() {
		runParser(ce("toolbar", "foo"), ce("toolbar", "some.stub.plugin.foo"));
		assertError(DUPLICATE_ID,
				"Duplicate id for toolbar extension 'some.stub.plugin.foo' contributed by some.stub.plugin");
	}

	@Test
	public void nonDuplicateId() {
		runParser(ce("toolbar", "foo"), ce("dropdown", "foo"));
		assertNoError();
	}

	// Helper methods

	private static IConfigurationElement ce(String elementName, String id,
			String... attrs) {
		return new ConfigurationElementStub(elementName, id, attrs);
	}

	private void runParser(IConfigurationElement... ces) {
		parser.parse(ces);
	}

	private void assertError(int code, String message) {
		final IStatus status = parser.getStatus();
		assertTrue(status.isMultiStatus());
		assertEquals(ERROR, status.getSeverity());
		boolean found = false;
		for (final IStatus child : status.getChildren()) {
			if (child.getCode() == code) {
				assertStatus(child, message);
				found = true;
			}
		}
		if (!found) {
			fail("Can't find error with code " + code);
		}
	}

	private static void assertStatus(IStatus status, String message) {
		assertEquals(ERROR, status.getSeverity());
		assertEquals(PLUGIN_ID, status.getPlugin());
		assertEquals(message, status.getMessage());
	}

	private void assertNoError() {
		final IStatus status = parser.getStatus();
		assertTrue(status.isOK());
	}

}
