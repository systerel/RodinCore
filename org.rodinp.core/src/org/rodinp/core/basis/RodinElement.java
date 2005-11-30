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
import java.util.HashMap;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.MultiOperation;
import org.rodinp.internal.core.RodinDBManager;
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

	// Escape character in mementos
	public static final char REM_ESCAPE = '\\';

	// Start of an external element name
	public static final char REM_EXTERNAL = '/';

	// Start of an internal element name
	public static final char REM_INTERNAL = '|';

	// Start of an occurrence count 
	public static final char REM_COUNT = '!';

	// Character used to make a name from an element type
	public static final char REM_TYPE_PREFIX = '#';
	
	/**
	 * This element's parent, or <code>null</code> if this element does not
	 * have a parent.
	 */
	protected RodinElement parent;

	public static final RodinElement[] NO_ELEMENTS = new RodinElement[0];

	static final RodinElementInfo NO_INFO = new RodinElementInfo();

	/**
	 * Constructs a handle for a Rodin element with the given parent element.
	 * 
	 * @param parent
	 *            The parent of Rodin element
	 */
	protected RodinElement(IRodinElement parent) {
		this.parent = (RodinElement) parent;
	}

	/**
	 * @see IOpenable
	 */
	public void close() throws RodinDBException {
		RodinDBManager.getRodinDBManager().removeInfoAndChildren(this);
	}

	/*
	 * Returns a new element info for this element.
	 */
	protected abstract RodinElementInfo createElementInfo();

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
				&& this.parent.equals(other.parent);
	}

	protected void escapeMementoName(StringBuffer buffer, String mementoName) {
		for (int i = 0, length = mementoName.length(); i < length; i++) {
			char character = mementoName.charAt(i);
			switch (character) {
				case REM_ESCAPE:
				case REM_EXTERNAL:
				case REM_INTERNAL:
				case REM_COUNT:
					buffer.append(REM_ESCAPE);
			}
			buffer.append(character);
		}
	}

	/**
	 * @see IRodinElement
	 */
	public boolean exists() {
		try {
			getElementInfo();
			return true;
		} catch (RodinDBException e) {
			// element doesn't exist: return false
		}
		return false;
	}

	/**
	 * Generates the element infos for this element, its ancestors (if they are not opened) and its children (if it is an Openable).
	 * Puts the newly created element info in the given map.
	 */
	protected abstract void generateInfos(RodinElementInfo info, 
			HashMap<IRodinElement, RodinElementInfo> newElements,
			IProgressMonitor monitor) throws RodinDBException;

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
		Object elementInfo = getElementInfo();
		if (elementInfo instanceof RodinElementInfo) {
			return ((RodinElementInfo) elementInfo).getChildren();
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
	public ArrayList<IRodinElement> getChildrenOfType(String type) throws RodinDBException {
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
	 * @see InternalElement
	 */
	public RodinFile getRodinFile() {
		return null;
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
	public RodinElementInfo getElementInfo(IProgressMonitor monitor)
			throws RodinDBException {

		RodinDBManager manager = RodinDBManager.getRodinDBManager();
		RodinElementInfo info = manager.getInfo(this);
		if (info != null)
			return info;
		throw newNotPresentException();
	}

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

	/**
	 * @see IRodinElement
	 */
	public String getHandleIdentifier() {
		return getHandleMemento();
	}

	/**
	 * @see RodinElement#getHandleMemento()
	 */
	protected String getHandleMemento() {
		StringBuffer buff = new StringBuffer();
		getHandleMemento(buff);
		return buff.toString();
	}

	protected void getHandleMemento(StringBuffer buff) {
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

	/*
	 * @see IRodinElement
	 */
	public Openable getOpenable() {
		return this.getOpenableParent();
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
			class NoResourceSchedulingRule implements ISchedulingRule {
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
			return new NoResourceSchedulingRule(getPath());
		} else {
			return resource;
		}
	}

	/**
	 * @see IParent
	 */
	public boolean hasChildren() throws RodinDBException {
		// if I am not open, return true to avoid opening (case of a Rodin
		// project, a compilation unit or a class file).
		// also see https://bugs.eclipse.org/bugs/show_bug.cgi?id=52474
		RodinElementInfo elementInfo = RodinDBManager.getRodinDBManager().getInfo(this);
		if (elementInfo != null) {
			return elementInfo.getChildren().length > 0;
		} else {
			return true;
		}
	}

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

	/**
	 * @see IRodinElement
	 */
	public boolean isReadOnly() {
		return false;
	}

	/*
	 * Opens an <code>Openable</code> that is known to be closed (no check for
	 * <code>isOpen()</code>). Returns the created element info.
	 */
	protected RodinElementInfo openWhenClosed(RodinElementInfo info, IProgressMonitor monitor)
			throws RodinDBException {
		RodinDBManager manager = RodinDBManager.getRodinDBManager();
		boolean hadTemporaryCache = manager.hasTemporaryCache();
		try {
			HashMap<IRodinElement, RodinElementInfo> newElements = manager.getTemporaryCache();
			generateInfos(info, newElements, monitor);
			if (info == null) {
				info = newElements.get(this);
			}
			if (info == null) {
				// TODO see if need to do some cleanup here.
				throw newNotPresentException();
			}
			if (!hadTemporaryCache) {
				manager.putInfos(this, newElements);
			}
		} finally {
			if (!hadTemporaryCache) {
				manager.resetTemporaryCache();
			}
		}
		return info;
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
		StringBuffer buffer = new StringBuffer();
		for (int i = tab; i > 0; i--)
			buffer.append("  "); //$NON-NLS-1$
		return buffer.toString();
	}

	/**
	 * Debugging purposes
	 */
	public String toDebugString() {
		StringBuffer buffer = new StringBuffer();
		this.toStringInfo(0, buffer, NO_INFO);
		return buffer.toString();
	}

	/**
	 * Debugging purposes
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		toString(0, buffer);
		return buffer.toString();
	}

	/**
	 * Debugging purposes
	 */
	protected void toString(int tab, StringBuffer buffer) {
		Object info = this.toStringInfo(tab, buffer);
		if (tab == 0) {
			this.toStringAncestors(buffer);
		}
		this.toStringChildren(tab, buffer, info);
	}

	/**
	 * Debugging purposes
	 */
	public String toStringWithAncestors() {
		StringBuffer buffer = new StringBuffer();
		this.toStringInfo(0, buffer, NO_INFO);
		this.toStringAncestors(buffer);
		return buffer.toString();
	}

	/**
	 * Debugging purposes
	 */
	protected void toStringAncestors(StringBuffer buffer) {
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
	protected void toStringChildren(int tab, StringBuffer buffer, Object info) {
		if (info == null || !(info instanceof RodinElementInfo))
			return;
		IRodinElement[] children = ((RodinElementInfo) info).getChildren();
		for (int i = 0; i < children.length; i++) {
			buffer.append("\n"); //$NON-NLS-1$
			((RodinElement) children[i]).toString(tab + 1, buffer);
		}
	}

	/**
	 * Debugging purposes
	 */
	public Object toStringInfo(int tab, StringBuffer buffer) {
		RodinElementInfo info = RodinDBManager.getRodinDBManager().peekAtInfo(this);
		this.toStringInfo(tab, buffer, info);
		return info;
	}

	/**
	 * Debugging purposes
	 * 
	 */
	protected void toStringInfo(int tab, StringBuffer buffer, RodinElementInfo info) {
		buffer.append(this.tabString(tab));
		toStringName(buffer);
		if (info == null) {
			buffer.append(" (not open)"); //$NON-NLS-1$
		}
	}

	/**
	 * Debugging purposes
	 */
	protected void toStringName(StringBuffer buffer) {
		buffer.append(getElementName());
	}
}
