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
package org.rodinp.internal.core.indexer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.internal.core.indexer.IIndexDelta.Kind;
import org.rodinp.internal.core.util.Util;

public class DeltaQueuer implements IElementChangedListener,
		IResourceChangeListener {

	public static boolean DEBUG = false;

	private final DeltaQueue queue;

	private final ThreadLocal<Boolean> interrupted = new ThreadLocal<Boolean>();

	public DeltaQueuer(DeltaQueue queue) {
		this.queue = queue;
	}

	static IRodinProject getRodinProject(IResource resource) {
		final IProject project = resource.getProject();
		if (project == null) {
			return null;
		}
		return RodinCore.valueOf(project);
	}

	@Override
	public void elementChanged(ElementChangedEvent event) {
		enqueueAffectedElements(event.getDelta());
	}

	private void enqueueAffectedElements(IRodinElementDelta delta) {
		final IRodinElement element = delta.getElement();
		if (!(element instanceof IOpenable)) {
			// No chance to find a project or a file below
			return;
		}
		if (element instanceof IRodinFile) {
			enqueueChangedFile((IRodinFile) element);
		} else {
			final IRodinElementDelta[] affectedChildren = delta
					.getAffectedChildren();
			if (affectedChildren.length != 0) {
				for (IRodinElementDelta childDelta : affectedChildren) {
					enqueueAffectedElements(childDelta);
				}
			} else {
				if (element instanceof IRodinProject) {
					enqueueAffectedProject((IRodinProject) element, delta);
				} else { // special cases like deleting a closed project
					final IResourceDelta[] resourceDeltas = delta
							.getResourceDeltas();
					if (resourceDeltas != null) {
						enqueueResourceDeltas(resourceDeltas);
					}
				}
			}
		}
	}

	private void enqueueAffectedProject(IRodinProject rodinProject,
			IRodinElementDelta delta) {
		switch (delta.getKind()) {

		case IRodinElementDelta.CHANGED:
			if ((delta.getFlags() & IRodinElementDelta.F_OPENED) != 0) {
				enqueueProject(rodinProject, Kind.PROJECT_OPENED);
			} else if ((delta.getFlags() & IRodinElementDelta.F_CLOSED) != 0) {
				enqueueProject(rodinProject, Kind.PROJECT_CLOSED);
			}
			break;
		case IRodinElementDelta.ADDED:
			enqueueProject(rodinProject, Kind.PROJECT_CREATED);
			break;
		case IRodinElementDelta.REMOVED:
			enqueueProject(rodinProject, Kind.PROJECT_DELETED);
			break;
		}
	}

	private void enqueueChangedFile(IRodinFile file) {
		final IIndexDelta indexDelta = new IndexDelta(file, Kind.FILE_CHANGED);
		enqueueDelta(indexDelta, false);
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		switch(event.getType()) {
		case IResourceChangeEvent.POST_BUILD:
			if (event.getBuildKind() == IncrementalProjectBuilder.CLEAN_BUILD) {
				enqueueCleanedProjects(event.getDelta());
			}
			break;
		case IResourceChangeEvent.PRE_CLOSE:
		case IResourceChangeEvent.PRE_DELETE:
			// prevent from further indexing of the vanishing project
			final IRodinProject project = getRodinProject(event.getResource());
			if (project != null) {
				IndexManager.getDefault().projectVanishing(project);
			}
			break;
		}
	}

	private void enqueueCleanedProjects(IResourceDelta delta) {
		final IRodinProject project = getRodinProject(delta.getResource());
		if (project == null) {
			for (IResourceDelta childDelta : delta.getAffectedChildren()) {
				enqueueCleanedProjects(childDelta);
			}
		} else {
			enqueueProject(project, Kind.PROJECT_CLEANED);
		}
	}

	private void enqueueProject(IRodinProject project, Kind kind) {
		final IndexDelta delta = new IndexDelta(project, kind);
		enqueueDelta(delta, true);
	}

	public void enqueueDelta(IIndexDelta delta, boolean allowDuplicate) {
		interrupted.set(false);
		try {
			while (true) {
				try {
					queue.put(delta, allowDuplicate);
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

	private void enqueueResourceDeltas(final IResourceDelta[] resourceDeltas) {
		final ResourceDeltaQueuer rdQueuer = new ResourceDeltaQueuer();
		for (IResourceDelta resourceDelta : resourceDeltas) {
			try {
				resourceDelta.accept(rdQueuer);
			} catch (CoreException e) {
				Util.log(e, "While visiting resource delta: " + resourceDelta);
				// forget the delta
			}
		}
		for (IIndexDelta indexDelta : rdQueuer.getDeltas()) {
			enqueueDelta(indexDelta, true);
		}
	}

	private static class ResourceDeltaQueuer implements IResourceDeltaVisitor {
	
		private final List<IIndexDelta> deltas;
	
		public ResourceDeltaQueuer() {
			this.deltas = new ArrayList<IIndexDelta>();
		}
	
		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			final IRodinProject project = getRodinProject(resource);
			if (project == null) {
				return false;
			}
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				deltas.add(new IndexDelta(project, Kind.PROJECT_CREATED));
				return false;
			case IResourceDelta.REMOVED:
				deltas.add(new IndexDelta(project, Kind.PROJECT_DELETED));
				return false;
			}
			return true;
		}
	
		public List<IIndexDelta> getDeltas() {
			return deltas;
		}
	}

	public void restore(Collection<? extends IIndexDelta> deltas) {
		interrupted.set(false);
		try {
			while (true) {
				try {
					queue.putAll(deltas);
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
}
