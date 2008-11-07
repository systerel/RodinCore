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

    public DeltaQueuer(DeltaQueue queue) {
	this.queue = queue;
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
		System.out.println("Indexer: File " + file.getPath()
			+ " has changed.");
	    }
	    final IIndexDelta indexDelta = new IndexDelta(file,
		    Kind.FILE_CHANGED);
	    queue.put(indexDelta, false);
	    return;
	}
	for (IRodinElementDelta childDelta : delta.getAffectedChildren()) {
	    enqueueAffectedFiles(childDelta);
	}
    }

    public void resourceChanged(IResourceChangeEvent event) {
	// FIXME general rodin editor problem: open editors of a closing project
	// should close as well; editors with errors should not generate
	// an exception (cannot get children of the ContextFile) up to the user.

	System.out.println(evtToString(event));

	if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
	    final IResourceDelta delta = event.getDelta();

	    interrupted.set(false);
	    try {
		while (true) {
		    try {
			enqueueRodinProjects(delta);
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

    private void enqueueRodinProjects(IResourceDelta delta)
	    throws InterruptedException {
	final IProject project = delta.getResource().getProject();
	if (project == null) {
	    for (IResourceDelta childDelta : delta.getAffectedChildren()) {
		enqueueRodinProjects(childDelta);
	    }
	} else {
	    final IRodinProject rodinProject = RodinCore.valueOf(project);
	    if (rodinProject == null) {
		return;
	    }
	    final IIndexDelta indexDelta;

	    switch (delta.getKind()) {

	    case IResourceDelta.CHANGED:
		if (delta.getFlags() == IResourceDelta.OPEN) {
		    if (project.isOpen()) {
			indexDelta = new IndexDelta(rodinProject,
				Kind.PROJECT_OPENED);
		    } else {
			indexDelta = new IndexDelta(rodinProject,
				Kind.PROJECT_CLOSED);
		    }
		    queue.put(indexDelta, true);
		}
		break;
	    case IResourceDelta.ADDED:
		indexDelta = new IndexDelta(rodinProject, Kind.PROJECT_CREATED);
		queue.put(indexDelta, true);
		break;
	    case IResourceDelta.REMOVED:
		indexDelta = new IndexDelta(rodinProject, Kind.PROJECT_DELETED);
		queue.put(indexDelta, true);
		break;
	    }
	}
    }

    private static String evtToString(IResourceChangeEvent event) {
	final StringBuilder sb = new StringBuilder("### IRCE type="
		+ event.getType() + "; resource=" + event.getResource());
	final IResourceDelta delta = event.getDelta();
	sb.append("; delta= " + delta);
	if (delta != null) {
	    appendDelta(sb, delta);
	}
	return sb.toString();
    }

    private static void appendDelta(final StringBuilder sb,
	    final IResourceDelta delta) {

	sb.append("CHILD delta kind= " + delta.getKind() + "; delta flags= "
		+ delta.getFlags() + "; path= " + delta.getFullPath()
		+ "; affected = {");

	final IResourceDelta[] affectedChildren = delta.getAffectedChildren();
	for (IResourceDelta resourceDelta : affectedChildren) {
	    appendDelta(sb, resourceDelta);
	}
	sb.append("}");
    }
}
