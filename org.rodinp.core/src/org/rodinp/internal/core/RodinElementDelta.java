/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.internal.core.JavaElementDelta
 *     Systerel - removed unused method contentChanged()
 *     Systerel - fixed flag for attribute change
 *******************************************************************************/
package org.rodinp.internal.core;

import java.util.ArrayList;

import org.eclipse.core.resources.IResourceDelta;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.basis.RodinElement;

/**
 * @see IRodinElementDelta
 */
public class RodinElementDelta extends SimpleDelta implements
		IRodinElementDelta {
	/**
	 * @see #getAffectedChildren()
	 */
	protected RodinElementDelta[] fAffectedChildren = fgEmptyDelta;

	/*
	 * The element that this delta describes the change to.
	 */
	protected IRodinElement changedElement;

	/**
	 * Collection of resource deltas that correspond to non java resources
	 * deltas.
	 */
	protected IResourceDelta[] resourceDeltas = null;

	/**
	 * Counter of resource deltas
	 */
	protected int resourceDeltasCounter;

	/**
	 * @see #getMovedFromElement()
	 */
	protected IRodinElement fMovedFromHandle = null;

	/**
	 * @see #getMovedToElement()
	 */
	protected IRodinElement fMovedToHandle = null;

	/**
	 * Empty array of IRodinElementDelta
	 */
	protected static RodinElementDelta[] fgEmptyDelta = new RodinElementDelta[] {};

	/**
	 * Creates the root delta. To create the nested delta hierarchies use the
	 * following convenience methods. The root delta can be created at any level
	 * (for example: project, package root, package fragment...).
	 * <ul>
	 * <li><code>added(IRodinElement)</code>
	 * <li><code>changed(IRodinElement)</code>
	 * <li><code>moved(IRodinElement, IRodinElement)</code>
	 * <li><code>removed(IRodinElement)</code>
	 * <li><code>renamed(IRodinElement, IRodinElement)</code>
	 * </ul>
	 */
	public RodinElementDelta(IRodinElement element) {
		this.changedElement = element;
	}

	/**
	 * Adds the child delta to the collection of affected children. If the child
	 * is already in the collection, walk down the hierarchy.
	 */
	protected void addAffectedChild(RodinElementDelta child) {
		switch (this.kind) {
		case ADDED:
		case REMOVED:
			// no need to add a child if this parent is added or removed
			return;
		case CHANGED:
			this.changeFlags |= F_CHILDREN;
			break;
		default:
			this.kind = CHANGED;
			this.changeFlags |= F_CHILDREN;
		}

		if (fAffectedChildren.length == 0) {
			fAffectedChildren = new RodinElementDelta[] { child };
			return;
		}
		RodinElementDelta existingChild = null;
		int existingChildIndex = -1;
		for (int i = 0; i < fAffectedChildren.length; i++) {
			if (this.equalsAndSameParent(fAffectedChildren[i].getElement(),
					child.getElement())) { // handle case of two jars that
											// can be equals but not in the
											// same project
				existingChild = fAffectedChildren[i];
				existingChildIndex = i;
				break;
			}
		}
		if (existingChild == null) { // new affected child
			fAffectedChildren = growAndAddToArray(fAffectedChildren, child);
		} else {
			switch (existingChild.getKind()) {
			case ADDED:
				switch (child.getKind()) {
				case ADDED: // child was added then added -> it is added
				case CHANGED: // child was added then changed -> it is added
					return;
				case REMOVED: // child was added then removed -> noop
					fAffectedChildren = this.removeAndShrinkArray(
							fAffectedChildren, existingChildIndex);
					return;
				}
				break;
			case REMOVED:
				switch (child.getKind()) {
				case ADDED: // child was removed then added -> it is changed
					child.kind = CHANGED;
					child.changeFlags |= F_ATTRIBUTE | F_CONTENT | F_CHILDREN
							| F_REORDERED | F_REPLACED;
					fAffectedChildren[existingChildIndex] = child;
					return;
				case CHANGED: // child was removed then changed -> it is
								// removed
				case REMOVED: // child was removed then removed -> it is
								// removed
					return;
				}
				break;
			case CHANGED:
				switch (child.getKind()) {
				case ADDED: // child was changed then added -> it is added
				case REMOVED: // child was changed then removed -> it is removed
					fAffectedChildren[existingChildIndex] = child;
					return;
				case CHANGED: // child was changed then changed -> it is changed
					IRodinElementDelta[] children = child.getAffectedChildren();
					for (int i = 0; i < children.length; i++) {
						RodinElementDelta childsChild = (RodinElementDelta) children[i];
						existingChild.addAffectedChild(childsChild);
					}

					// update flags
					existingChild.changeFlags |= child.changeFlags;

					// add the non-Rodin resource deltas if needed
					// note that the child delta always takes precedence over
					// this existing child delta
					// as non-Rodin resource deltas are always created last (by
					// the DeltaProcessor)
					IResourceDelta[] resDeltas = child.getResourceDeltas();
					if (resDeltas != null) {
						existingChild.resourceDeltas = resDeltas;
						existingChild.resourceDeltasCounter = child.resourceDeltasCounter;
					}
					return;
				}
				break;
			default:
				// unknown -> existing child becomes the child with the existing
				// child's flags
				int flags = existingChild.getFlags();
				fAffectedChildren[existingChildIndex] = child;
				child.changeFlags |= flags;
			}
		}
	}

	/**
	 * Creates the nested deltas resulting from an add operation. Convenience
	 * method for creating add deltas. The constructor should be used to create
	 * the root delta and then an add operation should call this method.
	 */
	public void added(IRodinElement element) {
		added(element, 0);
	}

	public void added(IRodinElement element, int flags) {
		RodinElementDelta addedDelta = new RodinElementDelta(element);
		addedDelta.added();
		addedDelta.changeFlags |= flags;
		insertDeltaTree(element, addedDelta);
	}

	/**
	 * Adds the child delta to the collection of affected children. If the child
	 * is already in the collection, walk down the hierarchy.
	 */
	protected void addResourceDelta(IResourceDelta child) {
		switch (this.kind) {
		case ADDED:
		case REMOVED:
			// no need to add a child if this parent is added or removed
			return;
		case CHANGED:
			this.changeFlags |= F_CONTENT;
			break;
		default:
			this.kind = CHANGED;
			this.changeFlags |= F_CONTENT;
		}
		if (resourceDeltas == null) {
			resourceDeltas = new IResourceDelta[5];
			resourceDeltas[resourceDeltasCounter++] = child;
			return;
		}
		if (resourceDeltas.length == resourceDeltasCounter) {
			// need a resize
			System
					.arraycopy(
							resourceDeltas,
							0,
							(resourceDeltas = new IResourceDelta[resourceDeltasCounter * 2]),
							0, resourceDeltasCounter);
		}
		resourceDeltas[resourceDeltasCounter++] = child;
	}

	/**
	 * Creates the nested deltas resulting from a change operation. Convenience
	 * method for creating change deltas. The constructor should be used to
	 * create the root delta and then a change operation should call this
	 * method.
	 */
	public RodinElementDelta changed(IRodinElement element, int changeFlag) {
		RodinElementDelta changedDelta = new RodinElementDelta(element);
		changedDelta.changed(changeFlag);
		insertDeltaTree(element, changedDelta);
		return changedDelta;
	}

	/**
	 * Creates the nested deltas for a closed element.
	 */
	public void closed(IRodinElement element) {
		RodinElementDelta delta = new RodinElementDelta(element);
		delta.changed(F_CLOSED);
		insertDeltaTree(element, delta);
	}

	/**
	 * Creates the nested delta deltas based on the affected element its delta,
	 * and the root of this delta tree. Returns the root of the created delta
	 * tree.
	 */
	protected RodinElementDelta createDeltaTree(IRodinElement element,
			RodinElementDelta delta) {
		RodinElementDelta childDelta = delta;
		ArrayList<IRodinElement> ancestors = getAncestors(element);
		if (ancestors == null) {
			if (this.equalsAndSameParent(delta.getElement(), getElement())) { // handle
																				// case
																				// of
																				// two
																				// jars
																				// that
																				// can
																				// be
																				// equals
																				// but
																				// not
																				// in
																				// the
																				// same
																				// project
				// the element being changed is the root element
				this.kind = delta.kind;
				this.changeFlags = delta.changeFlags;
				fMovedToHandle = delta.fMovedToHandle;
				fMovedFromHandle = delta.fMovedFromHandle;
			}
		} else {
			for (int i = 0, size = ancestors.size(); i < size; i++) {
				IRodinElement ancestor = ancestors.get(i);
				RodinElementDelta ancestorDelta = new RodinElementDelta(
						ancestor);
				ancestorDelta.addAffectedChild(childDelta);
				childDelta = ancestorDelta;
			}
		}
		return childDelta;
	}

	/**
	 * Returns whether the two java elements are equals and have the same
	 * parent.
	 */
	protected boolean equalsAndSameParent(IRodinElement e1, IRodinElement e2) {
		IRodinElement parent1;
		return e1.equals(e2) && ((parent1 = e1.getParent()) != null)
				&& parent1.equals(e2.getParent());
	}

	/**
	 * Returns the <code>RodinElementDelta</code> for the given element in the
	 * delta tree, or null, if no delta for the given element is found.
	 */
	protected RodinElementDelta find(IRodinElement e) {
		if (this.equalsAndSameParent(this.changedElement, e)) { // handle case
																// of two jars
																// that can be
																// equals but
																// not in the
																// same project
			return this;
		} else {
			for (int i = 0; i < fAffectedChildren.length; i++) {
				RodinElementDelta delta = fAffectedChildren[i].find(e);
				if (delta != null) {
					return delta;
				}
			}
		}
		return null;
	}

	/**
	 * @see IRodinElementDelta
	 */
	@Override
	public IRodinElementDelta[] getAddedChildren() {
		return getChildrenOfType(ADDED);
	}

	/**
	 * @see IRodinElementDelta
	 */
	@Override
	public IRodinElementDelta[] getAffectedChildren() {
		return fAffectedChildren;
	}

	/**
	 * Returns a collection of all the parents of this element up to (but not
	 * including) the root of this tree in bottom-up order. If the given element
	 * is not a descendant of the root of this tree, <code>null</code> is
	 * returned.
	 */
	private ArrayList<IRodinElement> getAncestors(IRodinElement element) {
		IRodinElement parent = element.getParent();
		if (parent == null) {
			return null;
		}
		ArrayList<IRodinElement> parents = new ArrayList<IRodinElement>();
		while (!parent.equals(this.changedElement)) {
			parents.add(parent);
			parent = parent.getParent();
			if (parent == null) {
				return null;
			}
		}
		parents.trimToSize();
		return parents;
	}

	/**
	 * @see IRodinElementDelta
	 */
	@Override
	public IRodinElementDelta[] getChangedChildren() {
		return getChildrenOfType(CHANGED);
	}

	/**
	 * @see IRodinElementDelta
	 */
	protected IRodinElementDelta[] getChildrenOfType(int type) {
		int length = fAffectedChildren.length;
		if (length == 0) {
			return new IRodinElementDelta[] {};
		}
		ArrayList<IRodinElementDelta> children = new ArrayList<IRodinElementDelta>(
				length);
		for (int i = 0; i < length; i++) {
			if (fAffectedChildren[i].getKind() == type) {
				children.add(fAffectedChildren[i]);
			}
		}

		IRodinElementDelta[] childrenOfType = new IRodinElementDelta[children
				.size()];
		children.toArray(childrenOfType);

		return childrenOfType;
	}

	/**
	 * Returns the delta for a given element. Only looks below this delta.
	 */
	protected RodinElementDelta getDeltaFor(IRodinElement element) {
		if (this.equalsAndSameParent(getElement(), element)) {
			// handle case of two jars that can be equals but not in the same project
			return this;
		}
		if (fAffectedChildren.length == 0)
			return null;
		int childrenCount = fAffectedChildren.length;
		for (int i = 0; i < childrenCount; i++) {
			RodinElementDelta delta = fAffectedChildren[i];
			if (this.equalsAndSameParent(delta.getElement(), element)) { 
				// handle case of two jars that can be equals but not in the same project
				return delta;
			} else {
				delta = delta.getDeltaFor(element);
				if (delta != null)
					return delta;
			}
		}
		return null;
	}

	/**
	 * @see IRodinElementDelta
	 */
	@Override
	public IRodinElement getElement() {
		return this.changedElement;
	}

	/**
	 * @see IRodinElementDelta
	 */
	@Override
	public IRodinElement getMovedFromElement() {
		return fMovedFromHandle;
	}

	/**
	 * @see IRodinElementDelta
	 */
	@Override
	public IRodinElement getMovedToElement() {
		return fMovedToHandle;
	}

	/**
	 * @see IRodinElementDelta
	 */
	@Override
	public IRodinElementDelta[] getRemovedChildren() {
		return getChildrenOfType(REMOVED);
	}

	/**
	 * Return the collection of resource deltas. Return null if none.
	 */
	@Override
	public IResourceDelta[] getResourceDeltas() {
		if (resourceDeltas == null)
			return null;
		if (resourceDeltas.length != resourceDeltasCounter) {
			System.arraycopy(resourceDeltas, 0,
					resourceDeltas = new IResourceDelta[resourceDeltasCounter],
					0, resourceDeltasCounter);
		}
		return resourceDeltas;
	}

	/**
	 * Adds the new element to a new array that contains all of the elements of
	 * the old array. Returns the new array.
	 */
	protected RodinElementDelta[] growAndAddToArray(
			RodinElementDelta[] array, RodinElementDelta addition) {
		RodinElementDelta[] old = array;
		array = new RodinElementDelta[old.length + 1];
		System.arraycopy(old, 0, array, 0, old.length);
		array[old.length] = addition;
		return array;
	}

	/**
	 * Creates the delta tree for the given element and delta, and then inserts
	 * the tree as an affected child of this node.
	 */
	protected void insertDeltaTree(IRodinElement element,
			RodinElementDelta delta) {
		RodinElementDelta childDelta = createDeltaTree(element, delta);
		if (!this.equalsAndSameParent(element, getElement())) {
			// handle case of two elements that can be equals but not in the same project
			addAffectedChild(childDelta);
		}
	}

	/**
	 * Creates the nested deltas resulting from an move operation. Convenience
	 * method for creating the "move from" delta. The constructor should be used
	 * to create the root delta and then the move operation should call this
	 * method.
	 */
	public void movedFrom(IRodinElement movedFromElement,
			IRodinElement movedToElement) {
		RodinElementDelta removedDelta = new RodinElementDelta(movedFromElement);
		removedDelta.kind = REMOVED;
		removedDelta.changeFlags |= F_MOVED_TO;
		removedDelta.fMovedToHandle = movedToElement;
		insertDeltaTree(movedFromElement, removedDelta);
	}

	/**
	 * Creates the nested deltas resulting from an move operation. Convenience
	 * method for creating the "move to" delta. The constructor should be used
	 * to create the root delta and then the move operation should call this
	 * method.
	 */
	public void movedTo(IRodinElement movedToElement,
			IRodinElement movedFromElement) {
		RodinElementDelta addedDelta = new RodinElementDelta(movedToElement);
		addedDelta.kind = ADDED;
		addedDelta.changeFlags |= F_MOVED_FROM;
		addedDelta.fMovedFromHandle = movedFromElement;
		insertDeltaTree(movedToElement, addedDelta);
	}

	/**
	 * Creates the nested deltas for an opened element.
	 */
	public void opened(IRodinElement element) {
		RodinElementDelta delta = new RodinElementDelta(element);
		delta.changed(F_OPENED);
		insertDeltaTree(element, delta);
	}

	/**
	 * Removes the child delta from the collection of affected children.
	 */
	protected void removeAffectedChild(RodinElementDelta child) {
		int index = -1;
		if (fAffectedChildren != null) {
			for (int i = 0; i < fAffectedChildren.length; i++) {
				if (this.equalsAndSameParent(fAffectedChildren[i].getElement(),
						child.getElement())) { // handle case of two jars that
												// can be equals but not in the
												// same project
					index = i;
					break;
				}
			}
		}
		if (index >= 0) {
			fAffectedChildren = removeAndShrinkArray(fAffectedChildren, index);
		}
	}

	/**
	 * Removes the element from the array. Returns the a new array which has
	 * shrunk.
	 */
	protected RodinElementDelta[] removeAndShrinkArray(
			RodinElementDelta[] old, int index) {
		RodinElementDelta[] array = new RodinElementDelta[old.length - 1];
		if (index > 0)
			System.arraycopy(old, 0, array, 0, index);
		int rest = old.length - index - 1;
		if (rest > 0)
			System.arraycopy(old, index + 1, array, index, rest);
		return array;
	}

	/**
	 * Creates the nested deltas resulting from an delete operation. Convenience
	 * method for creating removed deltas. The constructor should be used to
	 * create the root delta and then the delete operation should call this
	 * method.
	 */
	public void removed(IRodinElement element) {
		removed(element, 0);
	}

	public void removed(IRodinElement element, int flags) {
		RodinElementDelta removedDelta = new RodinElementDelta(element);
		insertDeltaTree(element, removedDelta);
		RodinElementDelta actualDelta = getDeltaFor(element);
		if (actualDelta != null) {
			actualDelta.removed();
			actualDelta.changeFlags |= flags;
			actualDelta.fAffectedChildren = fgEmptyDelta;
		}
	}

	/**
	 * Returns a string representation of this delta's structure suitable for
	 * debug purposes.
	 * 
	 * @see #toString()
	 */
	public String toDebugString(int depth) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < depth; i++) {
			buffer.append('\t');
		}
		buffer.append(((RodinElement) getElement()).toString());
		toDebugString(buffer);
		IRodinElementDelta[] children = getAffectedChildren();
		if (children != null) {
			for (int i = 0; i < children.length; ++i) {
				buffer.append("\n"); //$NON-NLS-1$
				buffer.append(((RodinElementDelta) children[i])
						.toDebugString(depth + 1));
			}
		}
		for (int i = 0; i < resourceDeltasCounter; i++) {
			buffer.append("\n");//$NON-NLS-1$
			for (int j = 0; j < depth + 1; j++) {
				buffer.append('\t');
			}
			IResourceDelta resourceDelta = resourceDeltas[i];
			buffer.append(resourceDelta.toString());
			buffer.append("["); //$NON-NLS-1$
			switch (resourceDelta.getKind()) {
			case IResourceDelta.ADDED:
				buffer.append('+');
				break;
			case IResourceDelta.REMOVED:
				buffer.append('-');
				break;
			case IResourceDelta.CHANGED:
				buffer.append('*');
				break;
			default:
				buffer.append('?');
				break;
			}
			buffer.append("]"); //$NON-NLS-1$
		}
		return buffer.toString();
	}

	@Override
	protected boolean toDebugString(StringBuffer buffer, int flags) {
		boolean prev = super.toDebugString(buffer, flags);

		if ((flags & IRodinElementDelta.F_CHILDREN) != 0) {
			if (prev)
				buffer.append(" | "); //$NON-NLS-1$
			buffer.append("CHILDREN"); //$NON-NLS-1$
			prev = true;
		}
		if ((flags & IRodinElementDelta.F_CONTENT) != 0) {
			if (prev)
				buffer.append(" | "); //$NON-NLS-1$
			buffer.append("CONTENT"); //$NON-NLS-1$
			prev = true;
		}
		if ((flags & IRodinElementDelta.F_ATTRIBUTE) != 0) {
			if (prev)
				buffer.append(" | "); //$NON-NLS-1$
			buffer.append("ATTRIBUTE"); //$NON-NLS-1$
			prev = true;
		}
		if ((flags & IRodinElementDelta.F_MOVED_FROM) != 0) {
			if (prev)
				buffer.append(" | "); //$NON-NLS-1$
			buffer
					.append("MOVED_FROM(" + ((RodinElement) getMovedFromElement()).toStringWithAncestors() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
			prev = true;
		}
		if ((flags & IRodinElementDelta.F_MOVED_TO) != 0) {
			if (prev)
				buffer.append(" | "); //$NON-NLS-1$
			buffer
					.append("MOVED_TO(" + ((RodinElement) getMovedToElement()).toStringWithAncestors() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
			prev = true;
		}
		if ((flags & IRodinElementDelta.F_REORDERED) != 0) {
			if (prev)
				buffer.append(" | "); //$NON-NLS-1$
			buffer.append("REORDERED"); //$NON-NLS-1$
			prev = true;
		}
		if ((flags & IRodinElementDelta.F_REPLACED) != 0) {
			if (prev)
				buffer.append(" | "); //$NON-NLS-1$
			buffer.append("REPLACED"); //$NON-NLS-1$
			prev = true;
		}
		if ((flags & IRodinElementDelta.F_OPENED) != 0) {
			if (prev)
				buffer.append(" | "); //$NON-NLS-1$
			buffer.append("OPENED"); //$NON-NLS-1$
			prev = true;
		}
		if ((flags & IRodinElementDelta.F_CLOSED) != 0) {
			if (prev)
				buffer.append(" | "); //$NON-NLS-1$
			buffer.append("CLOSED"); //$NON-NLS-1$
			prev = true;
		}
		return prev;
	}

	/**
	 * Returns a string representation of this delta's structure suitable for
	 * debug purposes.
	 */
	@Override
	public String toString() {
		return toDebugString(0);
	}
}
