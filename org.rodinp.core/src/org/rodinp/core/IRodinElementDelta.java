/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.core.IJavaElementDelta
 *******************************************************************************/
package org.rodinp.core;

import org.eclipse.core.resources.IResourceDelta;

/**
 * A Rodin element delta describes changes in Rodin element between two discrete
 * points in time. Given a delta, clients can access the element that has
 * changed, and any children that have changed.
 * <p>
 * Deltas have a different status depending on the kind of change they
 * represent. The list below summarizes each status (as returned by
 * <code>getKind</code>) and its meaning (see individual constants for a more
 * detailled description):
 * <ul>
 * <li><code>ADDED</code> - The element described by the delta has been
 * added.</li>
 * <li><code>REMOVED</code> - The element described by the delta has been
 * removed.</li>
 * <li><code>CHANGED</code> - The element described by the delta has been
 * changed in some way. Specification of the type of change is provided by
 * <code>getFlags</code> which returns the following values:
 * <ul>
 * <li><code>F_ATTRIBUTE</code> - The value of some attribute of the element
 * has been altered. This flag is only valid for internal elements.</li>
 * <li><code>F_CHILDREN</code> - A child of the element has changed in some
 * way. This flag is only valid if the element is an <code>IParent</code>.</li>
 * <li><code>F_CLOSED</code> - The underlying <code>IProject</code> has
 * been closed. This flag is only valid if the element is an
 * <code>IRodinProject</code>.</li>
 * <li><code>F_CONTENT</code> - The contents of the element have been
 * altered. This flag is only valid for elements which correspond to files or
 * internal elements.</li>
 * <li><code>F_OPENED</code> - The underlying <code>IProject</code> has
 * been opened. This flag is only valid if the element is an
 * <code>IRodinProject</code>.</li>
 * <li><code>F_REORDERED</code> - The element has changed position in its
 * parent. This flag is only valid if the element is an
 * <code>IInternalElement</code>.</li>
 * <li><code>F_REPLACED</code> - The element (and its children) has been
 * completely replaced by another element (and its children). The deltas for the
 * children of this element are not given in the delta tree. This flag is only
 * valid if the element is an <code>IInternalElement</code>.</li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 * <p>
 * Move operations are indicated by other change flags, layered on top of the
 * change flags described above. If element A is moved to become B, the delta
 * for the change in A will have status <code>REMOVED</code>, with change
 * flag <code>F_MOVED_TO</code>. In this case, <code>getMovedToElement</code>
 * on delta A will return the handle for B. The delta for B will have status
 * <code>ADDED</code>, with change flag <code>F_MOVED_FROM</code>, and
 * <code>getMovedFromElement</code> on delta B will return the handle for A.
 * (Note, the handle to A in this case represents an element that no longer
 * exists).
 * </p>
 * <p>
 * Note that the move change flags only describe the changes to a single
 * element, they do not imply anything about the parent or children of the
 * element.
 * </p>
 * <p>
 * The <code>F_REORDERED</code> flag is triggered by changes to the order of a
 * child element with respect to its siblings. It does not mean that the child
 * element was added, removed or changed. When a child is moved from one place
 * to another inside the same parent, an element delta is generated for that
 * child element with the <code>F_REORDERED</code> flag.
 * </p>
 * <p>
 * The <code>F_REPLACED</code> flag is triggered by a deletion followed by an
 * addition of the same element. It thus specifies only a coarse grain change.
 * In particular, no comparison is done between the children before deletion and
 * the children after addition. When encountering this flag, clients should
 * therefore recompute from scratch the set of children of the replaced element.
 * </p>
 * <p>
 * No assumptions should be made on whether the Rodin element delta tree is
 * rooted at the <code>IRodinDB</code> level or not.
 * </p>
 * <p>
 * <code>IRodinElementDelta</code> object are not valid outside the dynamic
 * scope of the notification.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @since 1.0
 */
public interface IRodinElementDelta {

	/**
	 * Status constant indicating that the element has been added.
	 * Note that an added Rodin element delta has no children, as they are all implicitely added.
	 */
	public int ADDED = 1;

	/**
	 * Status constant indicating that the element has been removed.
	 * Note that a removed Rodin element delta has no children, as they are all implicitely removed.
	 */
	public int REMOVED = 2;

	/**
	 * Status constant indicating that the element has been changed,
	 * as described by the change flags.
	 * 
	 * @see #getFlags()
	 */
	public int CHANGED = 4;

	/**
	 * Change flag indicating that the content of the element has changed in an
	 * unspecified manner. This flag is only valid for file elements.
	 */
	public int F_CONTENT = 0x00001;

	/**
	 * Change flag indicating that there are changes to the children of the element.
	 * This flag is only valid if the element is an <code>IParent</code>. 
	 */
	public int F_CHILDREN = 0x00002;

	/**
	 * Change flag indicating that the element has changed position relatively to its siblings.
	 * This flag is only valid if the element is an <code>IInternalElement</code>. 
	 */
	public int F_REORDERED = 0x00004;

	/**
	 * Change flag indicating that the element (and its children) have been
	 * replaced by a new element (with a new set of children). This flag is only
	 * valid if the element is an <code>IInternalElement</code>.
	 */
	public int F_REPLACED = 0x00008;

	/**
	 * Change flag indicating that the element was moved from another location.
	 * The location of the old element can be retrieved using <code>getMovedFromElement</code>.
	 */
	public int F_MOVED_FROM = 0x00010;

	/**
	 * Change flag indicating that the element was moved to another location.
	 * The location of the new element can be retrieved using <code>getMovedToElement</code>.
	 */
	public int F_MOVED_TO = 0x00020;

	/**
	 * Change flag indicating that the underlying <code>IProject</code> has been
	 * opened. This flag is only valid if the element is an <code>IRodinProject</code>. 
	 */
	public int F_OPENED = 0x00100;

	/**
	 * Change flag indicating that the underlying <code>IProject</code> has been
	 * closed. This flag is only valid if the element is an <code>IRodinProject</code>. 
	 */
	public int F_CLOSED = 0x00200;

	/**
	 * Change flag indicating that the value of some attribute of the element
	 * has changed. This flag is only valid for internal elements.
	 */
	public int F_ATTRIBUTE = 0x00400;

	/**
	 * Returns deltas for the children that have been added.
	 * @return deltas for the children that have been added
	 */
	public IRodinElementDelta[] getAddedChildren();

	/**
	 * Returns deltas for the affected (added, removed, or changed) children.
	 * @return deltas for the affected (added, removed, or changed) children
	 */
	public IRodinElementDelta[] getAffectedChildren();

	/**
	 * Returns deltas for the children which have changed.
	 * @return deltas for the children which have changed
	 */
	public IRodinElementDelta[] getChangedChildren();

	/**
	 * Returns the element that this delta describes a change to.
	 * @return the element that this delta describes a change to
	 */
	public IRodinElement getElement();

	/**
	 * Returns flags that describe how an element has changed. 
	 * Such flags should be tested using the <code>&</code> operand. For example:
	 * <pre>
	 * if ((delta.getFlags() & IRodinElementDelta.F_CONTENT) != 0) {
	 * 	// the delta indicates a content change
	 * }
	 * </pre>
	 *
	 * @return flags that describe how an element has changed
	 */
	public int getFlags();

	/**
	 * Returns the kind of this delta - one of <code>ADDED</code>, <code>REMOVED</code>,
	 * or <code>CHANGED</code>.
	 * 
	 * @return the kind of this delta
	 */
	public int getKind();

	/**
	 * Returns an element describing this element before it was moved
	 * to its current location, or <code>null</code> if the
	 * <code>F_MOVED_FROM</code> change flag is not set. 
	 * 
	 * @return an element describing this element before it was moved
	 * to its current location, or <code>null</code> if the
	 * <code>F_MOVED_FROM</code> change flag is not set
	 */
	public IRodinElement getMovedFromElement();

	/**
	 * Returns an element describing this element in its new location,
	 * or <code>null</code> if the <code>F_MOVED_TO</code> change
	 * flag is not set.
	 * 
	 * @return an element describing this element in its new location,
	 * or <code>null</code> if the <code>F_MOVED_TO</code> change
	 * flag is not set
	 */
	public IRodinElement getMovedToElement();

	/**
	 * Returns deltas for the children which have been removed.
	 * 
	 * @return deltas for the children which have been removed
	 */
	public IRodinElementDelta[] getRemovedChildren();

	/**
	 * Returns the collection of resource deltas.
	 * <p>
	 * Note that resource deltas, like Rodin element deltas, are generally only valid
	 * for the dynamic scope of an event notification. Clients must not hang on to
	 * these objects.
	 * </p>
	 *
	 * @return the underlying resource deltas, or <code>null</code> if none
	 */
	public IResourceDelta[] getResourceDeltas();
}
