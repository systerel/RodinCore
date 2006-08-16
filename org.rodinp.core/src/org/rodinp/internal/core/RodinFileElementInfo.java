/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.basis.RodinElement;
import org.rodinp.core.basis.RodinFile;
import org.w3c.dom.Element;

// TODO implement getDOMElementExisting
// TODO implement getDOMElementNotExisting
// TODO implement children cache behavior for the file itself, then remove the
// special getChildren below.

public class RodinFileElementInfo extends OpenableElementInfo {

	private static RodinDBException newNameCollisionException(InternalElement element) {
		return element.newRodinDBException(
				new RodinDBStatus(IRodinDBStatusConstants.NAME_COLLISION, element)		
		);
	}
	
	// Buffer associated to this Rodin file
	private Buffer buffer;
	
	// Map of internal elements inside this file (at any depth)
	// All accesses to this field must be synchronized.
	private Map<InternalElement, Element> internalElements;
	
	// Local cache of internal element informations
	private Map<InternalElement, InternalElementInfo> internalCache;

	public RodinFileElementInfo() {
		super();
		this.internalElements = new HashMap<InternalElement, Element>();
		this.internalCache = new HashMap<InternalElement, InternalElementInfo>();
	}

	private void addToMap(InternalElement element, Element domElement) {
		internalElements.put(element, domElement);
		internalCache.remove(element.getParent());
	}

	public synchronized void changeDescendantContents(
			InternalElement element, String newContents) throws RodinDBException {

		Element domElement = getDOMElement(element);
		if (domElement == null) {
			throw element.newNotPresentException();
		}
		buffer.setElementContents(domElement, newContents);
	}

	// dest must be an element of the Rodin file associated to this info.
	// TODO check for sourceInfo parameter removal
	public synchronized void copy(InternalElement source,
			InternalElementInfo sourceInfo, InternalElement dest,
			InternalElement nextSibling) throws RodinDBException {
		
		assert source.getElementType() == dest.getElementType();
		
		// TODO fix big mess below.  Should synchronize properly
		// and distinguish betweem two cases.
		RodinFile rfSource = source.getOpenableParent();
		RodinFileElementInfo rfSourceInfo = 
			(RodinFileElementInfo) rfSource.getElementInfo(null);

		final Element domSource = rfSourceInfo.getDOMElement(source);
		final Element domDestParent = getDOMParent(dest);
		if (domDestParent == null) {
			throw dest.getParent().newNotPresentException();
		}
		Element domDest = getDOMElement(dest);
		if (domDest != null) {
			throw newNameCollisionException(dest);
		}
		final Element domNextSibling;
		if (nextSibling != null) {
			domNextSibling = getDOMElement(nextSibling);
			if (domNextSibling == null) {
				throw nextSibling.newNotPresentException();
			}
		} else {
			domNextSibling = null;
		}
		final String newName = dest.getElementName();
		final Element newDOMNode = 
			buffer.importNode(domSource, domDestParent, domNextSibling, newName);
		addToMap(dest, newDOMNode);
	}

	private void computeChildren(IInternalParent element,
			Element domElement, RodinElementInfo info) {
		
		LinkedHashMap<InternalElement, Element> childrenMap = 
			buffer.getChildren(element, domElement);
		internalElements.putAll(childrenMap);
		info.setChildren(childrenMap.keySet());
	}

	public synchronized void create(InternalElement newElement,
			InternalElement nextSibling) throws RodinDBException {
		
		Element domParent = getDOMParent(newElement);
		if (domParent == null) {
			throw newElement.getParent().newNotPresentException();
		}
		Element domElement = getDOMElement(newElement);
		if (domElement != null) {
			throw newNameCollisionException(newElement);
		}
		Element domNextSibling = null;
		if (nextSibling != null) {
			domNextSibling = getDOMElement(nextSibling);
			if (domNextSibling == null) {
				throw nextSibling.newNotPresentException();
			}
		}		
		final String type = newElement.getElementType();
		final String name = newElement.getElementName();
		Element domNewElement =
			buffer.createElement(type, name, domParent, domNextSibling);
		addToMap(newElement, domNewElement);
	}

	public synchronized void delete(InternalElement element)
			throws RodinDBException {
		
		final Element domElement = getDOMElement(element);
		if (domElement == null) {
			throw element.newNotPresentException();
		}
		removeFromMap(element);
		buffer.deleteElement(domElement);
	}

	/**
	 * Returns the DOM element corresponding to the given Rodin element.
	 * 
	 * @param element
	 *            a Rodin internal element
	 * @return the corresponding DOM element or <code>null</code> if
	 *         inexistent
	 */
	private Element getDOMElement(InternalElement element) {
		Element result = internalElements.get(element);
		if (result != null) {
			if (result.getParentNode() != null) {
				// The cached DOM element still exists
				return result;
			}
			// Clean up the cached DOM element
			removeFromMap(element);
		}
		
		// Not found, force a cache update
		IRodinElement parent = element.getParent();
		if (parent instanceof RodinFile) {
			getChildren((RodinFile) parent);
		} else {
			getElementInfo((InternalElement) element.getParent());
		}
		return internalElements.get(element);
	}
	
	/**
	 * Returns the children of this file.
	 * 
	 * @param rodinFile
	 *            handle to this file
	 * @return the children of this file element
	 */
	public synchronized RodinElement[] getChildren(RodinFile rodinFile) {

		Element domElement = buffer.getDocumentElement();
		computeChildren(rodinFile, domElement, this);
		return getChildren();
	}

	/**
	 * Returns the contents of an internal element in this file.
	 * 
	 * @param element
	 *            the internal element to look up
	 * @return the contents of this element (never <code>null</code>)
	 * @throws RodinDBException 
	 */
	public synchronized String getDescendantContents(InternalElement element)
			throws RodinDBException {

		Element domElement = getDOMElement(element);
		if (domElement == null) {
			throw element.newNotPresentException();
		}
		return buffer.getElementContents(domElement);
	}

	/**
	 * Returns the DOM element corresponding to the parent of the given Rodin
	 * element.
	 * 
	 * @param element
	 *            a Rodin internal element
	 * @return the DOM element corresponding to the parent or <code>null</code>
	 *         if inexistent
	 */
	private Element getDOMParent(InternalElement element) {
		RodinElement parent = element.getParent();
		if (parent instanceof RodinFile) {
			return buffer.getDocumentElement();
		}
		assert parent instanceof InternalElement;
		return getDOMElement((InternalElement) parent);
	}
		
	public synchronized InternalElementInfo getElementInfo(
			InternalElement element) {
		
		InternalElementInfo info = internalCache.get(element);
		if (info != null) {
			return info;
		}
		Element domElement = getDOMElement(element);
		if (domElement == null) {
			return null;
		}
		info = new InternalElementInfo();
		computeChildren(element, domElement, info);
		return info;
	}

	public synchronized boolean hasUnsavedChanges() {
		return buffer.hasUnsavedChanges();
	}

	// Returns true if parse was successful
	public synchronized boolean parseFile(IProgressMonitor pm,
			RodinFile rodinFile) throws RodinDBException {
		
		buffer = new Buffer(rodinFile);
		buffer.load(pm);
		return true;
	}

	// Removes an element and all its descendants from the cache map.
	private void removeFromMap(InternalElement element) {
		// Remove all descendants
		InternalElementInfo info = getElementInfo(element);
		for (IRodinElement child: info.getChildren()) {
			removeFromMap((InternalElement) child);
		}

		// Remove the given element
		internalElements.remove(element);
		internalCache.remove(element.getParent());
	}

	/**
	 * Renames an element within this file.
	 * 
	 * @param source
	 *            the source element
	 * @param dest
	 *            the destination element
	 * @throws RodinDBException 
	 */
	public synchronized void rename(InternalElement source,
			InternalElement dest) throws RodinDBException {

		assert source.getParent().equals(dest.getParent());
		assert source.getClass() == dest.getClass();
		
		Element domElement = getDOMElement(source);
		if (domElement == null) {
			throw source.newNotPresentException();
		}
		Element domDest = getDOMElement(dest);
		if (domDest != null) {
			throw newNameCollisionException(dest);
		}
		removeFromMap(source);
		buffer.renameElement(domElement, dest.getElementName());
		addToMap(dest, domElement);
	}

	// Returns true iff a change was made to the order of the parent children.
	public synchronized boolean reorder(InternalElement source,
			InternalElement nextSibling) throws RodinDBException {
		
		assert nextSibling == null
				|| source.getParent().equals(nextSibling.getParent());
		
		Element domSource = getDOMElement(source);
		if (domSource == null) {
			throw source.newNotPresentException();
		}

		Element domNextSibling = null;
		if (nextSibling != null) {
			domNextSibling = getDOMElement(nextSibling);
			if (domNextSibling == null) {
				throw nextSibling.newNotPresentException();
			}
			assert domNextSibling.getParentNode().isSameNode(
					domSource.getParentNode());
		}
		boolean changed = buffer.reorderElement(domSource, domNextSibling);
		if (changed) {
			internalCache.remove(source.getParent());
		}
		return changed;
	}
	
	public synchronized void saveToFile(RodinFile rodinFile, boolean force,
			IProgressMonitor pm) throws RodinDBException {
		
		buffer.save(force, pm);
	}

}
