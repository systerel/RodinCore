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
package org.rodinp.internal.core.index;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.internal.core.index.IIndexDelta.Kind;

public class DeltaQueuer implements IElementChangedListener,
		IResourceChangeListener {

	public static boolean DEBUG = false;

	private final DeltaQueue queue;

	private final ThreadLocal<Boolean> interrupted = new ThreadLocal<Boolean>();

	public DeltaQueuer(DeltaQueue queue) {
		this.queue = queue;
	}

	public void elementChanged(ElementChangedEvent event) {
		final IRodinElementDelta delta = event.getDelta();
		interrupted.set(false);
		try {
			while (true) {
				try {
					processDelta(delta);
					return;
				} catch (InterruptedException e) {
					interrupted.set(true);
				}
			}
		} finally {
			if (interrupted.get()) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private void processDelta(IRodinElementDelta delta)
			throws InterruptedException {
		enqueueAffectedFiles(delta);
	}

	private void enqueueAffectedFiles(IRodinElementDelta delta)
			throws InterruptedException {
		final IRodinElement element = delta.getElement();
		if (!(element instanceof IOpenable)) {
			// No chance to find a file below
			return;
		}
		if (element instanceof IRodinFile) {
			final IRodinFile file = (IRodinFile) element;
			if (DEBUG) {
				System.out.println("Indexer: File "
						+ file.getPath()
						+ " has changed.");
			}
			final IIndexDelta indexDelta =
					new IndexDelta(file, Kind.FILE_CHANGED);
			queue.put(indexDelta, false);
			return;
		}
		for (IRodinElementDelta childDelta : delta.getAffectedChildren()) {
			enqueueAffectedFiles(childDelta);
		}
	}

	public void resourceChanged(IResourceChangeEvent event) {
		boolean process = false;
		boolean clean = false;
		switch (event.getType()) {
		case IResourceChangeEvent.POST_BUILD:
			if (event.getBuildKind() == IncrementalProjectBuilder.CLEAN_BUILD) {
				process = true;
				clean = true;
			}
			break;
		case IResourceChangeEvent.POST_CHANGE:
			process = true;
			clean = false;
			break;
		default:
			process = false;
			break;
		}
		if (process) {
			processDelta(event.getDelta(), clean);
		}
	}

	private void processDelta(final IResourceDelta delta, boolean clean) {
		interrupted.set(false);
		try {
			while (true) {
				try {
					enqueueRodinProjects(delta, clean);
					return;
				} catch (InterruptedException e) {
					interrupted.set(true);
				}
			}
		} finally {
			if (interrupted.get()) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private void enqueueRodinProjects(IResourceDelta delta, boolean clean)
			throws InterruptedException {
		final IProject project = delta.getResource().getProject();
		if (project == null) {
			for (IResourceDelta childDelta : delta.getAffectedChildren()) {
				enqueueRodinProjects(childDelta, clean);
			}
		} else {
			final IRodinProject rodinProject = RodinCore.valueOf(project);
			if (rodinProject == null) {
				return;
			}
			enqueueRodinProject(rodinProject, delta, clean);
		}
	}

	private void enqueueRodinProject(IRodinProject rodinProject,
			IResourceDelta delta, boolean clean) throws InterruptedException {
		if (clean) {
			queue.put(new IndexDelta(rodinProject, Kind.PROJECT_CLEANED), true);
			return;
		}
		switch (delta.getKind()) {

		case IResourceDelta.CHANGED:
			if (delta.getFlags() == IResourceDelta.OPEN) {
				final IIndexDelta indexDelta;
				if (rodinProject.getProject().isOpen()) {
					indexDelta =
							new IndexDelta(rodinProject, Kind.PROJECT_OPENED);
				} else {
					indexDelta =
							new IndexDelta(rodinProject, Kind.PROJECT_CLOSED);
				}
				queue.put(indexDelta, true);
			}
			break;
		case IResourceDelta.ADDED:
			queue.put(new IndexDelta(rodinProject, Kind.PROJECT_CREATED), true);
			break;
		case IResourceDelta.REMOVED:
			queue.put(new IndexDelta(rodinProject, Kind.PROJECT_DELETED), true);
			break;
		}
	}
}
