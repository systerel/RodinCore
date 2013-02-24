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
package org.eventb.ui.wizards;

import static org.eventb.internal.ui.wizards.EventBProjectValidator.CLOSED;
import static org.eventb.internal.ui.wizards.EventBProjectValidator.EMPTY_NAME;
import static org.eventb.internal.ui.wizards.EventBProjectValidator.INEXISTENT;
import static org.eventb.internal.ui.wizards.EventBProjectValidator.INVALID_NAME;
import static org.eventb.internal.ui.wizards.EventBProjectValidator.NOT_RODIN;
import static org.eventb.internal.ui.wizards.EventBProjectValidator.READ_ONLY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBProject;
import org.eventb.internal.ui.wizards.EventBProjectValidator;
import org.eventb.ui.tests.utils.EventBUITest;
import org.junit.Test;

/**
 * Unit tests for class {@link EventBProjectValidator}.
 * 
 * @author Laurent Voisin
 */
public class EventBProjectValidatorTest extends EventBUITest {

	private static final EventBProjectValidator validator = new EventBProjectValidator();

	private static void assertError(final String expected) {
		assertTrue(validator.hasError());
		assertEquals(expected, validator.getErrorMessage());
		assertNull(validator.getEventBProject());
	}

	private static void setReadOnly(final IProject project,
			final boolean readOnly) throws CoreException {
		final ResourceAttributes attr = project.getResourceAttributes();
		attr.setReadOnly(readOnly);
		project.setResourceAttributes(attr);
	}

	/**
	 * Ensures that an empty project name is rejected.
	 */
	@Test
	public void testEmpty() {
		validator.validate("");
		assertError(EMPTY_NAME);
	}

	/**
	 * Ensures that an invalid project name is rejected.
	 */
	@Test
	public void testInvalid() {
		validator.validate("a/b");
		assertError(INVALID_NAME);
	}

	/**
	 * Ensures that an inexistent project is rejected.
	 */
	@Test
	public void testInexistent() {
		validator.validate("inexistent");
		assertError(INEXISTENT);
	}

	/**
	 * Ensures that a closed project is rejected.
	 */
	@Test
	public void testClosed() throws Exception {
		rodinProject.getProject().close(null);
		validator.validate(rodinProject.getElementName());
		assertError(CLOSED);
	}

	/**
	 * Ensures that a non Rodin project is rejected.
	 */
	@Test
	public void testNonRodin() throws Exception {
		final IProject project = workspace.getRoot().getProject("new");
		project.create(null);
		project.open(null);
		validator.validate(project.getName());
		assertError(NOT_RODIN);
	}

	/**
	 * Ensures that a read-only project is rejected.
	 */
	@Test
	public void testReadOnly() throws Exception {
		final IProject project = rodinProject.getProject();
		try {
			setReadOnly(project, true);
			validator.validate(project.getName());
			assertError(READ_ONLY);
		} finally {
			setReadOnly(project, false);
		}
	}

	/**
	 * Ensures that a valid project is accepted and produces the expected
	 * event-B project.
	 */
	@Test
	public void testValid() throws Exception {
		validator.validate(rodinProject.getElementName());
		assertFalse(validator.hasError());
		assertNull(validator.getErrorMessage());
		final Object expected = rodinProject.getAdapter(IEventBProject.class);
		assertEquals(expected, validator.getEventBProject());
	}

}
