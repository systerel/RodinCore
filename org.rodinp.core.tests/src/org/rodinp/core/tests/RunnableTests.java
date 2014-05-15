/*******************************************************************************
 * Copyright (c) 2009, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Tests for runnable run through the Rodin platform.
 * 
 * @author Laurent Voisin
 */
public class RunnableTests extends AbstractRodinDBTests {

	private static class CheckingRunnable implements IWorkspaceRunnable {
		boolean done;

		public void run(IProgressMonitor monitor) {
			done = true;
		}
	}

	private static class RodinDBExceptionThrower implements IWorkspaceRunnable {
		RodinDBException exc;

		public void run(IProgressMonitor monitor) throws CoreException {
			try {
				getRodinProject("inexistent").open(null);
				fail("Should have raised a not present exception");
			} catch (RodinDBException e) {
				exc = e;
				throw exc;
			}
		}
	}

	private static class CoreExceptionThrower implements IWorkspaceRunnable {
		CoreException exc;

		public void run(IProgressMonitor monitor) throws CoreException {
			try {
				workspace.getRoot().getProject("inexistent").open(null);
				fail("Should have raised a not present exception");
			} catch (CoreException e) {
				exc = e;
				throw exc;
			}
		}
	}

	private static class RuntimeExceptionThrower implements IWorkspaceRunnable {
		RuntimeException exc;

		public void run(IProgressMonitor monitor) throws CoreException {
			exc = new NullPointerException("null");
			throw exc;
		}
	}

	private static final IWorkspace workspace = ResourcesPlugin.getWorkspace();

	/**
	 * Ensures that a Rodin runnable can be run while processing a resource
	 * change event.
	 */
	@Test
	public void testRunnableInResourceDelta() throws Exception {
		final CheckingRunnable inner = new CheckingRunnable();
		final IResourceChangeListener listener = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				try {
					RodinCore.run(inner, null);
				} catch (RodinDBException e) {
					fail("Unexpected exception");
				}
			}
		};
		final IProject project = workspace.getRoot().getProject("P");
		try {
			workspace.addResourceChangeListener(listener);
			project.create(null);
		} finally {
			workspace.removeResourceChangeListener(listener);
			project.delete(true, null);
		}
		assertTrue(inner.done);
	}

	/**
	 * Ensures that a RodinDBException thrown by a Rodin runnable is properly
	 * propagated.
	 */
	@Test
	public void testThrowRodinDBException() {
		final RodinDBExceptionThrower action = new RodinDBExceptionThrower();
		try {
			RodinCore.run(action, null);
			fail("Should have raised an exception");
		} catch (RodinDBException e) {
			assertSame(action.exc, e);
		}
	}

	/**
	 * Ensures that a CoreException thrown by a Rodin runnable is properly
	 * encapsulated in a Rodin DB exception.
	 */
	@Test
	public void testThrowCoreException() {
		final CoreExceptionThrower action = new CoreExceptionThrower();
		try {
			RodinCore.run(action, null);
			fail("Should have raised an exception");
		} catch (RodinDBException e) {
			assertSame(action.exc.getStatus(), e.getStatus());
			assertSame(action.exc, e.getException());
		}
	}

	/**
	 * Ensures that a RuntimeException thrown by a Rodin runnable is properly
	 * encapsulated in a Rodin DB exception.
	 */
	@Test
	public void testThrowRuntimeException() throws RodinDBException {
		final RuntimeExceptionThrower action = new RuntimeExceptionThrower();
		try {
			RodinCore.run(action, null);
			fail("Should have raised an exception");
		} catch (RuntimeException e) {
			assertSame(action.exc, e);
		}
	}

}
