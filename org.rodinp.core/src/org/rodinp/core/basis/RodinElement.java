/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.core.JavaElement.java which is
 * 
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.basis;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.CreateProblemMarkerOperation;
import org.rodinp.internal.core.ElementTypeManager;
import org.rodinp.internal.core.MultiOperation;
import org.rodinp.internal.core.RodinDBStatus;
import org.rodinp.internal.core.RodinElementInfo;
import org.rodinp.internal.core.RodinProject;
import org.rodinp.internal.core.util.MementoTokenizer;
import org.rodinp.internal.core.util.Util;

/**
 * Root of Rodin element handle hierarchy.
 * 
 * @see IRodinElement
 */
public abstract class RodinElement extends PlatformObject implements
		IRodinElement {

	private static class NoResourceSchedulingRule implements ISchedulingRule {
		public IPath path;

		public NoResourceSchedulingRule(IPath path) {
			this.path = path;
		}

		public boolean contains(ISchedulingRule rule) {
			if (rule instanceof NoResourceSchedulingRule) {
				return this.path
						.isPrefixOf(((NoResourceSchedulingRule) rule).path);
			} else {
				return false;
			}
		}

		public boolean isConflicting(ISchedulingRule rule) {
			if (rule instanceof NoResourceSchedulingRule) {
				IPath otherPath = ((NoResourceSchedulingRule) rule).path;
				return this.path.isPrefixOf(otherPath)
						|| otherPath.isPrefixOf(this.path);
			} else {
				return false;
			}
		}
	}

	// Escape character in mementos
	public static final char REM_ESCAPE = '\\';

	// Start of an external element name
	public static final char REM_EXTERNAL = '/';

	// Start of an internal element name
	public static final char REM_INTERNAL = '|';

	// Start of an occurrence count 
	public static final char REM_COUNT = '!';

	// Character used to make a name from an element type
	public static final char REM_TYPE_SEP = '#';
	
	/**
	 * This element's parent, or <code>null</code> if this element does not
	 * have a parent.
	 */
	protected RodinElement parent;

	public static final RodinElement[] NO_ELEMENTS = new RodinElement[0];

	static final RodinElementInfo NO_INFO = new RodinElementInfo();

	/*
	 * Internal code shared by implementers of IInternalParent for computing the
	 * handle to a child from a memento.
	 */
	protected static final IRodinElement getInternalHandleFromMemento(MementoTokenizer memento, IInternalParent parent) {
		if (! memento.hasMoreTokens()) return parent;
		String childType = memento.nextToken();
		if (! memento.hasMoreTokens()) return parent;
		if (memento.nextToken().charAt(0) != REM_TYPE_SEP) return parent;
		ElementTypeManager manager = ElementTypeManager.getElementTypeManager();
		RodinElement child;
		if (manager.isNamedInternalElementType(childType)) {
			if (! memento.hasMoreTokens()) return parent;
			String childName = memento.nextToken();
			child = (RodinElement) parent.getInternalElement(childType, childName);
		} else {
			child = (RodinElement) parent.getInternalElement(childType, "");
		}
		if (child == null) {
			return null;
		}
		return child.getHandleFromMemento(memento);
	}
	
	/**
	 * Constructs a handle for a Rodin element with the given parent element.
	 * 
	 * @param parent
	 *            The parent of Rodin element
	 */
	protected RodinElement(IRodinElement parent) {
		this.parent = (RodinElement) parent;
	}

	/*
	 * Returns a new element info for this element.
	 */
	protected abstract RodinElementInfo createElementInfo();

	public void createProblemMarker(IRodinProblem problem, Object... args)
			throws RodinDBException {

		new CreateProblemMarkerOperation(this, problem, args, null, -1, -1)
				.runOperation(null);
	}

	/**
	 * Returns true if this handle represents the same Rodin element as the
	 * given handle. By default, two handles represent the same element if they
	 * are identical or if they represent the same type of element, have equal
	 * names, parents, and occurrence counts.
	 * 
	 * <p>
	 * If a subclass has other requirements for equality, this method must be
	 * overridden.
	 * 
	 * @see Object#equals
	 */
	@Override
	public boolean equals(Object o) {

		if (this == o)
			return true;

		// Rodin database parent is null
		if (this.parent == null)
			return super.equals(o);

		if (! (o instanceof RodinElement)) {
			return false;
		}
		
		RodinElement other = (RodinElement) o;
		return getElementName().equals(other.getElementName())
				&& getElementType().equals(other.getElementType())
				&& this.parent.equals(other.parent);
	}

	protected void escapeMementoName(StringBuilder buffer, String mementoName) {
		for (int i = 0, length = mementoName.length(); i < length; i++) {
			char character = mementoName.charAt(i);
			switch (character) {
				case REM_ESCAPE:
				case REM_EXTERNAL:
				case REM_INTERNAL:
				case REM_COUNT:
				case REM_TYPE_SEP:
					buffer.append(REM_ESCAPE);
			}
			buffer.append(character);
		}
	}

	public abstract boolean exists();

	/**
	 * @see IRodinElement
	 */
	public IRodinElement getAncestor(String ancestorType) {
		IRodinElement element = this;
		while (element != null) {
			if (element.getElementType() == ancestorType)
				return element;
			element = element.getParent();
		}
		return null;
	}

	/**
	 * @see IParent
	 */
	public RodinElement[] getChildren() throws RodinDBException {
		RodinElementInfo elementInfo = getElementInfo();
		if (elementInfo != null) {
			final RodinElement[] children = elementInfo.getChildren();
			// Must make a copy as we don't want to expose the internal array
			final int length = children.length;
			final RodinElement[] result = new RodinElement[length];
			System.arraycopy(children, 0, result, 0, length);
			return result;
		} else {
			return NO_ELEMENTS;
		}
	}

	/**
	 * Returns a collection of (immediate) children of this node of the
	 * specified type.
	 * 
	 * @param type
	 *            the given type (must be a canonical String)
	 * @see String#intern()
	 */
	public ArrayList<IRodinElement> getFilteredChildrenList(String type) throws RodinDBException {
		IRodinElement[] children = getChildren();
		int size = children.length;
		ArrayList<IRodinElement> list = new ArrayList<IRodinElement>(size);
		for (int i = 0; i < size; ++i) {
			RodinElement elt = (RodinElement) children[i];
			if (elt.getElementType() == type) {
				list.add(elt);
			}
		}
		return list;
	}

	/**
	 * @see IParent
	 */
	public IRodinElement[] getChildrenOfType(String type) throws RodinDBException {
		List<IRodinElement> list = getFilteredChildrenList(type);
		if (list.size() == 0)
			return NO_ELEMENTS;
		return list.toArray(new IRodinElement[list.size()]);
	}

	
	/**
	 * Returns the info for this handle. If this element is not already open, it
	 * and all of its parents are opened. Does not return null.
	 * 
	 * @exception RodinDBException
	 *                if the element is not present or not accessible
	 */
	public RodinElementInfo getElementInfo() throws RodinDBException {
		return getElementInfo(null);
	}

	/**
	 * Returns the info for this handle. If this element is not already open, it
	 * and all of its parents are opened. Does not return null.
	 * 
	 * @exception RodinDBException
	 *                if the element is not present or not accessible
	 */
	public abstract RodinElementInfo getElementInfo(IProgressMonitor monitor)
			throws RodinDBException;
	
	/**
	 * @see IRodinElement
	 */
	public abstract String getElementName();

	/**
	 * @see IRodinElement
	 */
	public abstract String getElementType();

	/*
	 * Creates a Rodin element handle from the given memento. The given token is
	 * the current delimiter indicating the type of the next token(s). The given
	 * working copy owner is used only for file element handles.
	 */
	protected abstract IRodinElement getHandleFromMemento(String token,
			MementoTokenizer memento);

	/*
	 * Creates a Rodin element handle from the given memento. The given working
	 * copy owner is used only for file element handles.
	 */
	public IRodinElement getHandleFromMemento(MementoTokenizer memento) {
		if (!memento.hasMoreTokens())
			return this;
		String token = memento.nextToken();
		return getHandleFromMemento(token, memento);
	}

	/*
	 * @see IRodinElement
	 */
	public String getHandleIdentifier() {
		return getHandleMemento();
	}

	/**
	 * @see RodinElement#getHandleMemento()
	 */
	protected String getHandleMemento() {
		StringBuilder buff = new StringBuilder();
		getHandleMemento(buff);
		return buff.toString();
	}

	protected void getHandleMemento(StringBuilder buff) {
		getParent().getHandleMemento(buff);
		buff.append(getHandleMementoDelimiter());
		escapeMementoName(buff, getElementName());
	}

	/**
	 * Returns the <code>char</code> that marks the start of this handles
	 * contribution to a memento.
	 */
	protected abstract char getHandleMementoDelimiter();

	/**
	 * @see IRodinElement
	 */
	public IRodinDB getRodinDB() {
		IRodinElement current = this;
		do {
			if (current instanceof IRodinDB)
				return (IRodinDB) current;
		} while ((current = current.getParent()) != null);
		return null;
	}

	/**
	 * @see IRodinElement
	 */
	public RodinProject getRodinProject() {
		IRodinElement current = this;
		do {
			if (current instanceof RodinProject)
				return (RodinProject) current;
		} while ((current = current.getParent()) != null);
		return null;
	}

	/**
	 * Return the first instance of IOpenable in the parent hierarchy of this
	 * element.
	 * 
	 * <p>
	 * Subclasses that are not IOpenable's must override this method.
	 */
	public Openable getOpenableParent() {
		return (Openable) this.parent;
	}

	/**
	 * @see IRodinElement
	 */
	public RodinElement getParent() {
		return this.parent;
	}

	/*
	 * @see IRodinElement#getPrimaryElement()
	 */
	public IRodinElement getPrimaryElement() {
		return getPrimaryElement(true);
	}

	/*
	 * Returns the primary element. If checkOwner, and the cu owner is primary,
	 * return this element.
	 */
	public IRodinElement getPrimaryElement(boolean checkOwner) {
		return this;
	}

	/*
	 * (non-Rodindoc)
	 * 
	 * @see org.rodinp.core.IRodinElement#getSchedulingRule()
	 */
	public ISchedulingRule getSchedulingRule() {
		IResource resource = getResource();
		if (resource == null) {
			return new NoResourceSchedulingRule(getPath());
		} else {
			return resource;
		}
	}

	/*
	 * @see IParent
	 */
	public abstract boolean hasChildren() throws RodinDBException;

	/**
	 * Returns the hash code for this Rodin element. By default, the hash code
	 * for an element is a combination of its name and parent's hash code.
	 * Elements with other requirements must override this method.
	 */
	@Override
	public int hashCode() {
		if (this.parent == null)
			return super.hashCode();
		return Util.combineHashCodes(getElementName().hashCode(), this.parent
				.hashCode());
	}

	/**
	 * Returns true if this element is an ancestor of the given element,
	 * otherwise false.
	 */
	public boolean isAncestorOf(IRodinElement e) {
		IRodinElement parentElement = e.getParent();
		while (parentElement != null && !parentElement.equals(this)) {
			parentElement = parentElement.getParent();
		}
		return parentElement != null;
	}

	/*
	 * @see IRodinElement
	 */
	public boolean isReadOnly() {
		return false;
	}

	/**
	 * Creates and returns a new not present exception for this element.
	 */
	public RodinDBException newNotPresentException() {
		return new RodinDBException(new RodinDBStatus(
				IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST, this));
	}

	/**
	 * Creates and returns a new Rodin database exception for this element with
	 * the given status.
	 */
	public RodinDBException newRodinDBException(IStatus status) {
		if (status instanceof IRodinDBStatus)
			return new RodinDBException((IRodinDBStatus) status);
		else
			return new RodinDBException(new RodinDBStatus(status.getSeverity(),
					status.getCode(), status.getMessage()));
	}

	/**
	 */
	public String readableName() {
		return this.getElementName();
	}

	/**
	 * Configures and runs the <code>MultiOperation</code>.
	 */
	protected void runOperation(MultiOperation op, IRodinElement sibling,
			String newName, IProgressMonitor monitor) throws RodinDBException {

		if (sibling != null) {
			op.setInsertBefore(this, sibling);
		}
		if (newName != null) {
			op.setRenamings(new String[] { newName });
		}
		op.runOperation(monitor);
	}

	protected String tabString(int tab) {
		StringBuilder buffer = new StringBuilder();
		for (int i = tab; i > 0; i--)
			buffer.append("  "); //$NON-NLS-1$
		return buffer.toString();
	}

	/**
	 * Debugging purposes
	 */
	public String toDebugString() {
		StringBuilder buffer = new StringBuilder();
		toString(0, buffer);
		return buffer.toString();
	}

	/**
	 * Debugging purposes
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		this.toStringInfo(0, buffer, NO_INFO);
		return buffer.toString();
	}

	/**
	 * Debugging purposes
	 */
	protected void toString(int tab, StringBuilder buffer) {
		RodinElementInfo info = this.toStringInfo(tab, buffer);
		if (tab == 0) {
			this.toStringAncestors(buffer);
		}
		this.toStringChildren(tab, buffer, info);
	}

	/**
	 * Debugging purposes
	 */
	public String toStringWithAncestors() {
		StringBuilder buffer = new StringBuilder();
		this.toStringInfo(0, buffer, NO_INFO);
		this.toStringAncestors(buffer);
		return buffer.toString();
	}

	/**
	 * Debugging purposes
	 */
	protected void toStringAncestors(StringBuilder buffer) {
		RodinElement parentElement = this.getParent();
		if (parentElement != null && parentElement.getParent() != null) {
			buffer.append(" [in "); //$NON-NLS-1$
			parentElement.toStringInfo(0, buffer, NO_INFO);
			parentElement.toStringAncestors(buffer);
			buffer.append("]"); //$NON-NLS-1$
		}
	}

	/**
	 * Debugging purposes
	 */
	protected void toStringChildren(int tab, StringBuilder buffer,
			RodinElementInfo info) {

		if (info == null)
			return;
		for (RodinElement child: info.getChildren()) {
			buffer.append("\n"); //$NON-NLS-1$
			child.toString(tab + 1, buffer);
		}
	}

	/**
	 * Debugging purposes
	 */
	public abstract RodinElementInfo toStringInfo(int tab, StringBuilder buffer);

	/**
	 * Debugging purposes
	 */
	protected void toStringInfo(int tab, StringBuilder buffer, RodinElementInfo info) {
		buffer.append(this.tabString(tab));
		toStringName(buffer);
		if (info == null) {
			buffer.append(" (not open)"); //$NON-NLS-1$
		}
	}

	/**
	 * Debugging purposes
	 */
	protected void toStringName(StringBuilder buffer) {
		buffer.append(getElementName());
	}
}
