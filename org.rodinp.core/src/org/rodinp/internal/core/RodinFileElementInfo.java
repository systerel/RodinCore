/*******************************************************************************
 * Copyright (c) 2005-2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.basis.RodinElement;
import org.rodinp.core.basis.RodinFile;
import org.w3c.dom.Element;

public class RodinFileElementInfo extends OpenableElementInfo {

	// Buffer associated to this Rodin file
	private Buffer buffer;
	
	// Tells whether the list of children is up to date
	private boolean childrenUpToDate;
	
	// Local cache of internal element informations
	private Map<InternalElement, InternalElementInfo> internalCache;
	
	// Map of internal elements inside this file (at any depth)
	// All accesses to this field must be synchronized.
	private Map<InternalElement, Element> internalElements;

	public RodinFileElementInfo() {
		super();
		this.internalElements = new HashMap<InternalElement, Element>();
		this.internalCache = new HashMap<InternalElement, InternalElementInfo>();
	}

	private void addToMap(InternalElement element, Element domElement) {
		internalElements.put(element, domElement);
		invalidateParentCache(element);
	}

	public synchronized void changeDescendantContents(
			InternalElement element, String newContents) throws RodinDBException {

		Element domElement = getDOMElementCheckExists(element);
		buffer.setElementContents(domElement, newContents);
	}

	private void checkDOMElementForCollision(IInternalParent element)
			throws RodinDBException {
		
		Element domElement = getDOMElement(element);
		if (domElement != null) {
			throw ((RodinElement) element).newRodinDBException(
					new RodinDBStatus(
							IRodinDBStatusConstants.NAME_COLLISION, 
							element
					)		
			);
		}
	}
	
	private void computeChildren() {
		final RodinFile element = buffer.getOwner();
		final Element domElement = buffer.getDocumentElement();
		computeChildren(element, domElement, this);
	}
	
	private void computeChildren(IInternalParent element,
			Element domElement, RodinElementInfo info) {
		
		LinkedHashMap<InternalElement, Element> childrenMap = 
			buffer.getChildren(element, domElement);
		internalElements.putAll(childrenMap);
		info.setChildren(childrenMap.keySet());
	}

	public synchronized boolean containsDescendant(InternalElement element) {
		return getDOMElement(element) != null;
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
		final IInternalParent destParent = (IInternalParent) dest.getParent();
		final Element domDestParent = getDOMElementCheckExists(destParent);
		checkDOMElementForCollision(dest);
		final Element domNextSibling;
		if (nextSibling != null) {
			domNextSibling = getDOMElementCheckExists(nextSibling);
		} else {
			domNextSibling = null;
		}
		final String newName = dest.getElementName();
		final Element newDOMElement = 
			buffer.importNode(domSource, domDestParent, domNextSibling, newName);
		addToMap(dest, newDOMElement);
	}

	public synchronized void create(InternalElement newElement,
			InternalElement nextSibling) throws RodinDBException {
		
		IInternalParent parent = (IInternalParent) newElement.getParent();
		Element domParent = getDOMElementCheckExists(parent);
		checkDOMElementForCollision(newElement);
		Element domNextSibling = null;
		if (nextSibling != null) {
			domNextSibling = getDOMElementCheckExists(nextSibling);
		}		
		final IInternalElementType type = newElement.getElementType();
		final String name = newElement.getElementName();
		Element domNewElement =
			buffer.createElement(type, name, domParent, domNextSibling);
		addToMap(newElement, domNewElement);
	}

	public synchronized void delete(InternalElement element)
			throws RodinDBException {
		
		final Element domElement = getDOMElementCheckExists(element);
		removeFromMap(element);
		buffer.deleteElement(domElement);
	}

	public synchronized IAttributeType[] getAttributeTypes(
			IInternalParent element) throws RodinDBException {
		Element domElement = getDOMElementCheckExists(element);
		String[] rawAttrNames = buffer.getAttributeNames(domElement);
		ElementTypeManager manager = ElementTypeManager.getInstance();
		ArrayList<IAttributeType> result = new ArrayList<IAttributeType>(
				rawAttrNames.length);
		for (String attrName: rawAttrNames) {
			final AttributeType type = manager.getAttributeType(attrName);
			if (type != null) {
				result.add(type);
			}
		}
		return result.toArray(new IAttributeType[result.size()]);
	}

	public synchronized String getAttributeRawValue(IInternalParent element,
			String attrName) throws RodinDBException {
		Element domElement = getDOMElementCheckExists(element);
		String result = buffer.getAttributeRawValue(domElement, attrName);
		if (result == null) {
			throw new RodinDBException(
					new RodinDBStatus(
							IRodinDBStatusConstants.ATTRIBUTE_DOES_NOT_EXIST,
							element,
							attrName
					)
			);
		}
		return result;
	}

	@Override
	public synchronized RodinElement[] getChildren() {
		if (! childrenUpToDate) {
			computeChildren();
		}
		return super.getChildren();
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

		Element domElement = getDOMElementCheckExists(element);
		return buffer.getElementContents(domElement);
	}
	
	/**
	 * Returns the DOM element corresponding to the given Rodin element.
	 * 
	 * @param element
	 *            a Rodin file or internal element
	 * @return the corresponding DOM element or <code>null</code> if
	 *         inexistent
	 */
	private Element getDOMElement(IInternalParent element) {
		if (element instanceof RodinFile) {
			return buffer.getDocumentElement();
		}
		Element result = internalElements.get(element);
		if (result != null) {
			if (result.getParentNode() != null) {
				// The cached DOM element still exists
				return result;
			}
			// Clean up the cached DOM element
			removeFromMap((InternalElement) element);
		}
		
		// Not found, force a cache update
		IRodinElement parent = element.getParent();
		if (parent instanceof RodinFile) {
			computeChildren();
		} else {
			getElementInfo((InternalElement) parent);
		}
		return internalElements.get(element);
	}

	private Element getDOMElementCheckExists(IInternalParent element)
			throws RodinDBException {
		
		Element domElement = getDOMElement(element);
		if (domElement == null) {
			throw ((RodinElement) element).newNotPresentException();
		}
		return domElement;
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

	public synchronized boolean hasAttribute(IInternalParent element,
			IAttributeType type) throws RodinDBException {
		Element domElement = getDOMElementCheckExists(element);
		return buffer.hasAttribute(domElement, type.getId());
	}

	@Override
	public synchronized boolean hasUnsavedChanges() {
		return buffer.hasUnsavedChanges();
	}

	private void invalidateParentCache(InternalElement element) {
		final RodinElement parent = element.getParent();
		if (parent instanceof RodinFile) {
			childrenUpToDate = false;
		} else {
			internalCache.remove(parent);
		}
	}

	// Returns true if parse was successful
	public synchronized boolean parseFile(IProgressMonitor pm,
			RodinFile rodinFile) throws RodinDBException {
		
		final RodinDBManager rodinDBManager = RodinDBManager.getRodinDBManager();
		buffer = rodinDBManager.getBuffer(rodinFile);
		buffer.load(pm);
		return true;
	}
	
	public synchronized boolean removeAttribute(IInternalParent element,
			IAttributeType attrType) throws RodinDBException {
		Element domElement = getDOMElementCheckExists(element);
		return buffer.removeAttribute(domElement, attrType.getId());
	}

	// Removes an element and all its descendants from the cache map.
	private void removeFromMap(InternalElement element) {
		// Remove all descendants
		InternalElementInfo info = getElementInfo(element);
		if (info != null) {
			for (IRodinElement child: info.getChildren()) {
				removeFromMap((InternalElement) child);
			}
		}

		// Remove the given element
		internalElements.remove(element);
		invalidateParentCache(element);
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
		
		Element domElement = getDOMElementCheckExists(source);
		checkDOMElementForCollision(dest);
		removeFromMap(source);
		buffer.renameElement(domElement, dest.getElementName());
		addToMap(dest, domElement);
	}

	// Returns true iff a change was made to the order of the parent children.
	public synchronized boolean reorder(InternalElement source,
			InternalElement nextSibling) throws RodinDBException {
		
		assert nextSibling == null
				|| source.getParent().equals(nextSibling.getParent());
		
		Element domSource = getDOMElementCheckExists(source);

		Element domNextSibling = null;
		if (nextSibling != null) {
			domNextSibling = getDOMElementCheckExists(nextSibling);
			assert domNextSibling.getParentNode().isSameNode(
					domSource.getParentNode());
		}
		boolean changed = buffer.reorderElement(domSource, domNextSibling);
		if (changed) {
			invalidateParentCache(source);
		}
		return changed;
	}

	public synchronized void saveToFile(RodinFile rodinFile, boolean force,
			ISchedulingRule rule, IProgressMonitor pm) throws RodinDBException {
		
		buffer.save(force, rule, pm);
	}

	public synchronized void setAttributeRawValue(IInternalParent element,
			String attrName, String newRawValue) throws RodinDBException {
		Element domElement = getDOMElementCheckExists(element);
		buffer.setAttributeRawValue(domElement, attrName, newRawValue);
	}

}
