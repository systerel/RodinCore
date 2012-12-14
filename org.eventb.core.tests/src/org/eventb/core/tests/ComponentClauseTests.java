/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.rodinp.core.IRodinDBStatusConstants.ATTRIBUTE_DOES_NOT_EXIST;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IContextRoot;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.junit.Test;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Unit tests for method returning a component referred to by another component.
 * 
 * @author Laurent Voisin
 */
public class ComponentClauseTests extends BuilderTest {

	protected static void assertError(int expectedCode, IRodinElement element,
			IWorkspaceRunnable runnable) throws CoreException {
		try {
			runnable.run(null);
			fail("Should have raised an exception");
		} catch (RodinDBException e) {
			assertError(expectedCode, element, e);
		}
	}

	protected static void assertError(int expectedCode, IRodinElement element,
			RodinDBException exception) {

		final IRodinDBStatus status = exception.getRodinDBStatus();
		assertEquals("Status should be an error", IRodinDBStatus.ERROR, status
				.getSeverity());
		assertEquals("Unexpected status code", expectedCode, status.getCode());
		IRodinElement[] elements = status.getElements();
		if (element == null) {
			assertEquals("Status should have no related element", 0,
					elements.length);
		} else {
			assertEquals("Status should be related to the given element", 1,
					elements.length);
			assertEquals("Status should be related to the given element",
					element, elements[0]);
		}
	}

	@Test
	public void testExtendsContextAbsent() throws Exception {
		final IContextRoot ctx = createContext("foo");
		final IExtendsContext clause = ctx.createChild(
				IExtendsContext.ELEMENT_TYPE, null, null);
		assertFalse(clause.hasAbstractContextName());
		assertError(ATTRIBUTE_DOES_NOT_EXIST, clause, new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				clause.getAbstractContextRoot();
			}
		});
	}

	@Test
	public void testExtendsContext() throws Exception {
		final IContextRoot ctx = createContext("foo");
		final IExtendsContext clause = ctx.createChild(
				IExtendsContext.ELEMENT_TYPE, null, null);
		final IContextRoot target = eventBProject.getContextRoot("bar");
		clause.setAbstractContextName(target.getElementName(), null);
		assertTrue(clause.hasAbstractContextName());
		assertEquals(target, clause.getAbstractContextRoot());
		assertEquals(target.getSCContextRoot(), clause.getAbstractSCContext());
	}

	@Test
	public void testSeesContextAbsent() throws Exception {
		final IMachineRoot mch = createMachine("foo");
		final ISeesContext clause = mch.createChild(ISeesContext.ELEMENT_TYPE,
				null, null);
		assertFalse(clause.hasSeenContextName());
		assertError(ATTRIBUTE_DOES_NOT_EXIST, clause, new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				clause.getSeenContextRoot();
			}
		});
	}

	@Test
	public void testSeesContext() throws Exception {
		final IMachineRoot mch = createMachine("foo");
		final ISeesContext clause = mch.createChild(ISeesContext.ELEMENT_TYPE,
				null, null);
		final IContextRoot target = eventBProject.getContextRoot("bar");
		clause.setSeenContextName(target.getElementName(), null);
		assertTrue(clause.hasSeenContextName());
		assertEquals(target, clause.getSeenContextRoot());
		assertEquals(target.getSCContextRoot(), clause.getSeenSCContext()
				.getRoot());
	}

	@Test
	public void testRefinesMachineAbsent() throws Exception {
		final IMachineRoot mch = createMachine("foo");
		final IRefinesMachine clause = mch.createChild(
				IRefinesMachine.ELEMENT_TYPE, null, null);
		assertFalse(clause.hasAbstractMachineName());
		assertError(ATTRIBUTE_DOES_NOT_EXIST, clause, new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				clause.getAbstractMachineRoot();
			}
		});
	}

	@Test
	public void testRefinesMachine() throws Exception {
		final IMachineRoot mch = createMachine("foo");
		final IRefinesMachine clause = mch.createChild(
				IRefinesMachine.ELEMENT_TYPE, null, null);
		final IMachineRoot target = eventBProject.getMachineRoot("bar");
		clause.setAbstractMachineName(target.getElementName(), null);
		assertTrue(clause.hasAbstractMachineName());
		assertEquals(target, clause.getAbstractMachineRoot());
		assertEquals(target.getSCMachineRoot(), clause
				.getAbstractSCMachineRoot());
	}

}
