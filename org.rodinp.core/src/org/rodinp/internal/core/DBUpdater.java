/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.core.ModelUpdater.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.internal.core;

import java.util.HashSet;

import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * This class is used by <code>RodinDBManager</code> to update the RodinDB
 * based on some <code>IRodinElementDelta</code>s.
 */
public class DBUpdater {

	HashSet<RodinProject> projectsToUpdate = new HashSet<RodinProject>();

	/**
	 * Adds the given child handle to its parent's cache of children. 
	 */
	private static void addToParentInfo(Openable child) {

		Openable parent = child.getParent();
		if (parent != null && parent.isOpen()) {
			try {
				RodinElementInfo info = parent.getElementInfo();
				info.addChild(child);
			} catch (RodinDBException e) {
				// do nothing - we already checked if open
			}
		}
	}

	/**
	 * Closes the given element, which removes it from the cache of open elements.
	 */
	private static void close(Openable element) {

		try {
			element.close();
		} catch (RodinDBException e) {
			// do nothing
		}
	}

	/**
	 * Processing for an element that has been added:<ul>
	 * <li>If the element is a project, do nothing, and do not process
	 * children, as when a project is created it does not yet have any
	 * natures - specifically a java nature.
	 * <li>If the element is not a project, process it as added (see
	 * <code>basicElementAdded</code>.
	 * </ul>
	 */
	private void elementAdded(Openable element) {

		IElementType<?> elementType = element.getElementType();
		if (elementType == IRodinProject.ELEMENT_TYPE) {
			// project add is handled by RodinProject.configure() because
			// when a project is created, it does not yet have a Rodin nature
			addToParentInfo(element);
			this.projectsToUpdate.add((RodinProject) element);
		} else {
			addToParentInfo(element);

			// Force the element to be closed as it might have been opened 
			// before the resource modification came in and it might have a new child
			// For example, in an IWorkspaceRunnable:
			// 1. create a package fragment p using a java model operation
			// 2. open package p
			// 3. add file X.java in folder p
			// When the resource delta comes in, only the addition of p is notified, 
			// but the package p is already opened, thus its children are not recomputed
			// and it appears empty.
			close(element);
		}
	}

	/**
	 * Generic processing for elements with changed contents:<ul>
	 * <li>The element is closed such that any subsequent accesses will re-open
	 * the element reflecting its new structure.
	 * </ul>
	 */
	private static void elementChanged(Openable element) {

		close(element);
	}

	/**
	 * Generic processing for a removed element:<ul>
	 * <li>Close the element, removing its structure from the cache
	 * <li>Remove the element from its parent's cache of children
	 * <li>Add a REMOVED entry in the delta
	 * </ul>
	 */
	private void elementRemoved(Openable element) {

		if (element.isOpen()) {
			close(element);
		}
		removeFromParentInfo(element);
		IElementType<?> elementType = element.getElementType();

		if (elementType == IRodinDB.ELEMENT_TYPE) {
			// RodinDBManager.getRodinDBManager().getIndexManager().reset();
		} else if (elementType == IRodinProject.ELEMENT_TYPE) {
			RodinDBManager.getRodinDBManager().removePerProjectInfo(
					(RodinProject) element);
		} else {
			final RodinProject project = (RodinProject) element.getRodinProject();
			this.projectsToUpdate.add(project);
			project.resetCaches();
		}
	}

	/**
	 * Converts a <code>IResourceDelta</code> rooted in a
	 * <code>Workspace</code> into the corresponding set of
	 * <code>IRodinElementDelta</code>, rooted in the relevant
	 * <code>RodinDB</code>s.
	 */
	public void processRodinDelta(IRodinElementDelta delta) {

		if (DeltaProcessor.VERBOSE) {
			System.out.println("UPDATING Model with Delta: ["+Thread.currentThread()+":" + delta + "]:");//$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		}

		try {
			this.traverseDelta(delta, null); // traverse delta

			// update package fragment roots of projects that were affected
			for (RodinProject project: this.projectsToUpdate) {
				project.updateChildren();
			}
		} finally {
			this.projectsToUpdate.clear();
		}
	}

	/**
	 * Removes the given element from its parents cache of children. If the
	 * element does not have a parent, or the parent is not currently open,
	 * this has no effect. 
	 */
	private static void removeFromParentInfo(Openable child) {

		Openable parent = child.getParent();
		if (parent != null && parent.isOpen()) {
			try {
				RodinElementInfo info = parent.getElementInfo();
				info.removeChild(child);
			} catch (RodinDBException e) {
				// do nothing - we already checked if open
			}
		}
	}

	/**
	 * Converts an <code>IResourceDelta</code> and its children into
	 * the corresponding <code>IRodinElementDelta</code>s.
	 */
	private void traverseDelta(IRodinElementDelta delta, IRodinProject project) {

		boolean processChildren = true;

		Openable element = (Openable) delta.getElement();
		if (element.getElementType() == IRodinProject.ELEMENT_TYPE) {
			project = (IRodinProject) element;
		}
		if (element instanceof RodinFile) {
			processChildren = false;
		}

		switch (delta.getKind()) {
		case IRodinElementDelta.ADDED:
			elementAdded(element);
			break;
		case IRodinElementDelta.REMOVED:
			elementRemoved(element);
			break;
		case IRodinElementDelta.CHANGED:
			if ((delta.getFlags() & IRodinElementDelta.F_CONTENT) != 0) {
				elementChanged(element);
			}
			break;
		}
		if (processChildren) {
			for (IRodinElementDelta childDelta: delta.getAffectedChildren()) {
				this.traverseDelta(childDelta, project);
			}
		}
	}
}
