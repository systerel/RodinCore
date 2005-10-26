/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core;

import java.util.HashMap;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.internal.core.InternalElementInfo;
import org.rodinp.internal.core.RodinDBManager;
import org.rodinp.internal.core.RodinElementInfo;
import org.rodinp.internal.core.util.MementoTokenizer;

/**
 * Common implementationfor all internal elements.
 * <p>
 * Internal elements are elements of the database that are stored within files.
 * </p>
 * 
 * TODO describe internal element
 * TODO extract an interface for it
 * 
 * @author Laurent Voisin
 */
public abstract class InternalElement extends RodinElement {
	
	/* Name of this internal element */
	private String name;

	/*
	 * A count to uniquely identify this element in the case
	 * that a duplicate named element exists. For example, if
	 * there are two fields in a compilation unit with the
	 * same name, the occurrence count is used to distinguish
	 * them.  The occurrence count starts at 1 (thus the first 
	 * occurrence is occurrence 1, not occurrence 0).
	 */
	public int occurrenceCount = 1;

	public InternalElement(String name, RodinElement parent) {
		super(parent);
		this.name = name;
		// Name must not be empty
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException();
	}

	@Override
	protected RodinElementInfo createElementInfo() {
		return new InternalElementInfo();
	}
	
	@Override
	public boolean equals(Object o) {
		if (! (o instanceof InternalElement))
			return false;
		return this.occurrenceCount == ((InternalElement) o).occurrenceCount
				&& super.equals(o);
	}

	@Override
	protected IRodinElement getHandleFromMemento(String token,
			MementoTokenizer memento) {
		switch (token.charAt(0)) {
		case REM_COUNT:
			return getHandleUpdatingCountFromMemento(memento);
		case REM_INTERNAL:
			if (!memento.hasMoreTokens())
				return this;
			String childName = memento.nextToken();
			RodinElement child = getChild(childName);
			return child.getHandleFromMemento(memento);
		}
		return this;
	}

	/*
	 * Update the occurence count of the receiver and creates a Java element handle from the given memento.
	 */
	public IRodinElement getHandleUpdatingCountFromMemento(MementoTokenizer memento) {
		if (!memento.hasMoreTokens()) return this;
		this.occurrenceCount = Integer.parseInt(memento.nextToken());
		if (!memento.hasMoreTokens()) return this;
		String token = memento.nextToken();
		return getHandleFromMemento(token, memento);
	}

	@Override
	protected void getHandleMemento(StringBuffer buff) {
		super.getHandleMemento(buff);
		if (this.occurrenceCount > 1) {
			buff.append(REM_COUNT);
			buff.append(this.occurrenceCount);
		}
	}

	@Override
	protected char getHandleMementoDelimiter() {
		return REM_INTERNAL;
	}

	public IResource getCorrespondingResource() {
		return null;
	}

	public IPath getPath() {
		return getOpenableParent().getPath();
	}

	public IResource getResource() {
		try {
			return getUnderlyingResource();
		} catch (RodinDBException e) {
			return null;
		}
	}

	public IResource getUnderlyingResource() throws RodinDBException {
		return getOpenableParent().getCorrespondingResource();
	}

	/**
	 * Returns the closest openable ancestor of this element (that is its
	 * enclosing file element). Should never return <code>null</code>.
	 * 
	 * @return the closest openable ancestor of this element
	 */
	@Override
	public Openable getOpenableParent() {
		RodinElement ancestor = parent;
		while (ancestor != null) {
			if (ancestor instanceof Openable) {
				return (Openable) ancestor;
			}
			ancestor = ancestor.getParent();
		}
		assert false;
		return null;
	}

	@Override
	protected void generateInfos(RodinElementInfo info,
			HashMap<IRodinElement, RodinElementInfo> newElements,
			IProgressMonitor monitor) throws RodinDBException {
		
		Openable openableParent = getOpenableParent();
		if (openableParent == null) return;

		RodinElementInfo openableParentInfo = RodinDBManager.getRodinDBManager().getInfo(openableParent);
		if (openableParentInfo == null) {
			openableParent.generateInfos(openableParent.createElementInfo(), newElements, monitor);
		}
	}
	
	/**
	 * @see IParent 
	 */
	@Override
	public boolean hasChildren() throws RodinDBException {
		return getChildren().length > 0;
	}

	
	/**
	 * Returns the child with the given name or <code>null</code> is there is no such child.
	 * @param childName
	 *   the name of the child
	 * @return the child with the given name
	 */
	protected RodinElement getChild(String childName) {
		// TODO implement getChild
		return null;
	}

	@Override
	public String getElementName() {
		return name;
	}
	
}
