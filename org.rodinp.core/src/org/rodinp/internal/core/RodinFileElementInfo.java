/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added clearChildren() method
 *     Systerel - removed deprecated methods (contents)
 *     Systerel - added auto-upgrade of file with past version
 *     Systerel - separation of file and root element
 *     Systerel - added creation of new internal element child
 *     Systerel - generic attribute manipulation
 *******************************************************************************/
package org.rodinp.internal.core;

import static org.rodinp.core.IRodinDBStatusConstants.PAST_VERSION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.basis.RodinElement;
import org.rodinp.internal.core.version.ConversionEntry;
import org.rodinp.internal.core.version.VersionManager;
import org.w3c.dom.Element;

public class RodinFileElementInfo extends OpenableElementInfo {
	
	public static boolean DEBUG = false;

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
	}
	
	public synchronized RodinElement[] clearChildren(IInternalElement element)
			throws RodinDBException {

		if (DEBUG) {
			System.out.println("--- CLEAR ---");
			System.out.println("Destination file "
					+ buffer.getOwner().getResource());
			if (element instanceof InternalElement) {
				printInternalElement("element: ", (InternalElement) element);
			} else {
				System.out.println("root element");
			}
			printCaches();
		}

		final Element domElement = getDOMElementCheckExists(element);
		final RodinElementInfo info;
		info = getElementInfo((InternalElement) element);
		final RodinElement[] children = info.getChildren();
		for (final RodinElement child : children) {
			removeFromMap((InternalElement) child);
		}
		buffer.deleteElementChildren(domElement);
		info.setChildren(RodinElement.NO_ELEMENTS);

		if (DEBUG) {
			printCaches();
			System.out.println("--- END OF CLEAR ---");
		}
		return children;
	}

	private void checkDOMElementForCollision(IInternalElement element)
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
		final RodinFile file = buffer.getOwner();
		final InternalElement root = (InternalElement) file.getRoot();
		final Element domRoot = buffer.getDocumentElement();
		internalElements.put(root, domRoot);
		setChildren(new RodinElement[] {root});
		childrenUpToDate = true;
	}
	
	private void computeChildren(IInternalElement element,
			Element domElement, RodinElementInfo info) {
		
		LinkedHashMap<InternalElement, Element> childrenMap = 
			buffer.getChildren(element, domElement);
		internalElements.putAll(childrenMap);
		info.setChildren(childrenMap.keySet());
	}
	
	public synchronized long getVersion() throws RodinDBException {
		return buffer.getVersion();
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

		if (DEBUG) {
			System.out.println("--- COPY ---");
			System.out.println("Destination file " + buffer.getOwner().getResource());
			printInternalElement("source: ", source);
			printInternalElement("dest: ", dest);
			printInternalElement("nextSibling: ", nextSibling);
			printCaches();
		}
		
		// TODO fix big mess below.  Should synchronize properly
		// and distinguish between two cases.
		RodinFile rfSource = source.getRodinFile();
		RodinFileElementInfo rfSourceInfo = 
			(RodinFileElementInfo) rfSource.getElementInfo(null);

		final Element domSource = rfSourceInfo.getDOMElement(source);
		final IInternalElement destParent = (IInternalElement) dest.getParent();
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
		addToParentInfo(dest, nextSibling);

		if (DEBUG) {
			printCaches();
			System.out.println("--- END OF COPY ---");
		}
	}

	public synchronized void create(InternalElement newElement,
			InternalElement nextSibling) throws RodinDBException {
		
		if (DEBUG) {
			System.out.println("--- CREATE ---");
			System.out.println("Destination file " + buffer.getOwner().getResource());
			printInternalElement("newElement: ", newElement);
			printInternalElement("nextSibling: ", nextSibling);
			printCaches();
		}

		final RodinElement newParent = newElement.getParent();
		if (newParent instanceof IRodinFile) {
			throw new IllegalArgumentException("Can't create root element: "
					+ newElement);
		}
		final IInternalElement parent = (IInternalElement) newParent;
		final Element domParent = getDOMElementCheckExists(parent);
		checkDOMElementForCollision(newElement);
		final Element domNextSibling;
		if (nextSibling != null) {
			domNextSibling = getDOMElementCheckExists(nextSibling);
		} else {
			domNextSibling = null;
		}
		final IInternalElementType<?> type = newElement.getElementType();
		final String name = newElement.getElementName();
		final Element domNewElement =
			buffer.createElement(type, name, domParent, domNextSibling);
		
		addToMap(newElement, domNewElement);
		addToParentInfo(newElement, nextSibling);

		if (DEBUG) {
			printCaches();
			System.out.println("--- END OF CREATE ---");
		}
	}

	public synchronized <T extends IInternalElement> T create(InternalElement parent,
			InternalElementType<T> childType,
			InternalElement nextSibling) throws RodinDBException {
		final InternalElementInfo parentInfo = getElementInfo(parent);
		final String childName = parentInfo.getFreshName();
		T child = parent.getInternalElement(childType, childName);
		create((InternalElement) child, nextSibling);
		return child;
	}

	public synchronized void delete(InternalElement element)
			throws RodinDBException {
		
		if (DEBUG) {
			System.out.println("--- DELETE ---");
			System.out.println("Destination file " + buffer.getOwner().getResource());
			printInternalElement("element: ", element);
			printCaches();
		}

		final Element domElement = getDOMElementCheckExists(element);
		removeFromMap(element);
		buffer.deleteElement(domElement);
		removeFromParentInfo(element);

		if (DEBUG) {
			printCaches();
			System.out.println("--- END OF DELETE ---");
		}
	}

	private synchronized Map<String, String> getAttributes(
			IInternalElement element) throws RodinDBException {
		final Element domElement = getDOMElementCheckExists(element);
		return buffer.getAttributes(domElement);
	}

	public IAttributeType[] getAttributeTypes(IInternalElement element)
			throws RodinDBException {
		final Set<String> attrNames = getAttributes(element).keySet();
		final ElementTypeManager etm = ElementTypeManager.getInstance();
		final List<IAttributeType> result = new ArrayList<IAttributeType>(
				attrNames.size());
		for (String attrName : attrNames) {
			final AttributeType<?> type = etm.getAttributeType(attrName);
			if (type != null) {
				result.add(type);
			}
		}
		return result.toArray(new IAttributeType[result.size()]);
	}

	public IAttributeValue[] getAttributeValues(IInternalElement element)
			throws RodinDBException {
		final Map<String, String> attributes = getAttributes(element);
		final ElementTypeManager etm = ElementTypeManager.getInstance();
		final List<IAttributeValue> result = new ArrayList<IAttributeValue>(
				attributes.size());
		for (Map.Entry<String, String> entry : attributes.entrySet()) {
			final AttributeType<?> type = etm.getAttributeType(entry.getKey());
			if (type != null) {
				result.add(type.makeValueFromRaw(entry.getValue()));
			}
		}
		return result.toArray(new IAttributeValue[result.size()]);
	}

	public synchronized String getAttributeRawValue(IInternalElement element,
			AttributeType<?> attrType) throws RodinDBException {
		final String attrId = attrType.getId();
		final Element domElement = getDOMElementCheckExists(element);
		final String result = buffer.getAttributeRawValue(domElement, attrId);
		if (result == null) {
			throw new RodinDBException(
					new RodinDBStatus(
							IRodinDBStatusConstants.ATTRIBUTE_DOES_NOT_EXIST,
							element,
							attrId
					)
			);
		}
		return result;
	}

	public IAttributeValue getAttributeValue(IInternalElement element,
			AttributeType<?> attrType) throws RodinDBException {
		final String rawValue = getAttributeRawValue(element, attrType);
		return attrType.makeValueFromRaw(rawValue);
	}

	public boolean getAttributeValue(IInternalElement element,
			AttributeType.Boolean attrType) throws RodinDBException {
		final String rawValue = getAttributeRawValue(element, attrType);
		return attrType.parseValue(rawValue);
	}

	public IRodinElement getAttributeValue(IInternalElement element,
			AttributeType.Handle attrType) throws RodinDBException {
		final String rawValue = getAttributeRawValue(element, attrType);
		return attrType.parseValue(rawValue);
	}

	public int getAttributeValue(IInternalElement element,
			AttributeType.Integer attrType) throws RodinDBException {
		final String rawValue = getAttributeRawValue(element, attrType);
		return attrType.parseValue(rawValue);
	}

	public long getAttributeValue(IInternalElement element,
			AttributeType.Long attrType) throws RodinDBException {
		final String rawValue = getAttributeRawValue(element, attrType);
		return attrType.parseValue(rawValue);
	}

	public String getAttributeValue(IInternalElement element,
			AttributeType.String attrType) throws RodinDBException {
		final String rawValue = getAttributeRawValue(element, attrType);
		return attrType.parseValue(rawValue);
	}

	@Override
	public synchronized RodinElement[] getChildren() {
		if (! childrenUpToDate) {
			computeChildren();
		}
		return super.getChildren();
	}

	/**
	 * Returns the DOM element corresponding to the given Rodin element.
	 * 
	 * @param element
	 *            an internal element
	 * @return the corresponding DOM element or <code>null</code> if
	 *         inexistent
	 */
	private Element getDOMElement(IInternalElement element) {
		Element result = internalElements.get(element);
		if (result != null) {
			assert result.getParentNode() != null;
			return result;
		}
		
		// Not found, force a cache update
		IRodinElement parent = element.getParent();
		if (parent instanceof InternalElement) {
			getElementInfo((InternalElement) parent);
		} else if (! childrenUpToDate) {
			computeChildren();
		}
		return internalElements.get(element);
	}

	private Element getDOMElementCheckExists(IInternalElement element)
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

	public synchronized boolean hasAttribute(IInternalElement element,
			IAttributeType attrType) throws RodinDBException {
		Element domElement = getDOMElementCheckExists(element);
		return buffer.hasAttribute(domElement, attrType.getId());
	}

	@Override
	public synchronized boolean hasUnsavedChanges() {
		return buffer.hasUnsavedChanges();
	}

	// Returns true if parse was successful
	public synchronized boolean parseFile(IProgressMonitor pm,
			RodinFile rodinFile) throws RodinDBException {
		
		final RodinDBManager rodinDBManager = RodinDBManager.getRodinDBManager();
		buffer = rodinDBManager.getBuffer(rodinFile);
		try {
			buffer.load(pm);
		} catch (RodinDBException e) {
			final int code = e.getStatus().getCode();
			if (code != PAST_VERSION) {
				throw e;
			}
			if (!attemptUpgrade(rodinFile)) {
				throw e;
			}				
			buffer.load(pm);
		}
		return true;
	}

	private boolean attemptUpgrade(RodinFile rodinFile) throws RodinDBException {
		final ConversionEntry cv = new ConversionEntry(rodinFile);
		final VersionManager vManager = VersionManager.getInstance();
		cv.upgrade(vManager, false, null);
		final boolean success = cv.success();
		if (success) {
			cv.accept(false, true, null);
		}
		return success;
	}
	
	private void printCaches() {
		System.out.println("Keys of internalCache:");
		printSet(internalCache.keySet());

		System.out.println("Keys of internalElements:");
		printSet(internalElements.keySet());
	}

	private void printSet(Set<InternalElement> entries) {
		for (InternalElement entry: entries) {
			printInternalElement("  ", entry);
		}
	}
	
	private void printInternalElement(String prefix, InternalElement elem) {
		System.out.println(prefix
				+ (elem == null ? "<null>" : elem.toStringWithAncestors()));
	}

	public synchronized boolean removeAttribute(IInternalElement element,
			IAttributeType attrType) throws RodinDBException {
		final Element domElement = getDOMElementCheckExists(element);
		return buffer.removeAttribute(domElement, attrType.getId());
	}

	// Removes an element and all its descendants from the cache map.
	private void removeFromMap(InternalElement element) {
		final Element domElement = internalElements.remove(element);
		if (domElement == null) {
			// This element is not cached, nor its children
			return;
		}

		final InternalElementInfo info = internalCache.remove(element);
		if (info != null) {
			for (IRodinElement child: info.getChildren()) {
				removeFromMap((InternalElement) child);
			}
			return;
		}
		
		final LinkedHashMap<InternalElement, Element> childrenMap = buffer
				.getChildren(element, domElement);
		for (IRodinElement child: childrenMap.keySet()) {
			removeFromMap((InternalElement) child);
		}
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
		
		if (DEBUG) {
			System.out.println("--- RENAME ---");
			System.out.println("Destination file " + buffer.getOwner().getResource());
			printInternalElement("source: ", source);
			printInternalElement("dest: ", dest);
			printCaches();
		}

		Element domElement = getDOMElementCheckExists(source);
		checkDOMElementForCollision(dest);
		removeFromMap(source);
		buffer.renameElement(domElement, dest.getElementName());
		addToMap(dest, domElement);
		changeInParentInfo(source, dest);

		if (DEBUG) {
			printCaches();
			System.out.println("--- END OF RENAME ---");
		}
	}

	// Returns true iff a change was made to the order of the parent children.
	public synchronized boolean reorder(InternalElement source,
			InternalElement nextSibling) throws RodinDBException {
		
		assert nextSibling == null
				|| source.getParent().equals(nextSibling.getParent());
		
		if (DEBUG) {
			System.out.println("--- REORDER ---");
			System.out.println("Destination file " + buffer.getOwner().getResource());
			printInternalElement("source: ", source);
			printInternalElement("nextSibling: ", nextSibling);
			printCaches();
		}

		Element domSource = getDOMElementCheckExists(source);

		Element domNextSibling = null;
		if (nextSibling != null) {
			domNextSibling = getDOMElementCheckExists(nextSibling);
			assert domNextSibling.getParentNode().isSameNode(
					domSource.getParentNode());
		}
		boolean changed = buffer.reorderElement(domSource, domNextSibling);
		if (changed) {
			moveInParentInfo(source, nextSibling);
		}

		if (DEBUG) {
			printCaches();
			System.out.println("--- END OF REORDER ---");
		}

		return changed;
	}

	public synchronized void saveToFile(RodinFile rodinFile, boolean force,
			boolean keepHistory, ISchedulingRule rule, IProgressMonitor pm) throws RodinDBException {
		
		buffer.save(force, keepHistory, rule, pm);
	}

	public synchronized void setAttributeValue(IInternalElement element,
			AttributeValue<?,?> attrValue) throws RodinDBException {
		final Element domElement = getDOMElementCheckExists(element);
		final String name = attrValue.getId();
		final String value = attrValue.getRawValue();
		buffer.setAttributeRawValue(domElement, name, value);
	}

	// Parent info management methods.
	//
	// These methods update the parent information if it exists.
	
	/*
	 * Returns the element info associated to the parent of the given element
	 * is it has already been computed, otherwise null. 
	 */
	private RodinElementInfo peekParentInfo(InternalElement element) {
		final RodinElement parent = element.getParent();
		if (parent instanceof InternalElement) {
			return internalCache.get(parent);
		}
		if (childrenUpToDate) {
			return this;
		}
		return null;
	}

	private void addToParentInfo(InternalElement child, InternalElement next) {
		final RodinElementInfo parentInfo = peekParentInfo(child);
		if (parentInfo != null) {
			parentInfo.addChildBefore(child, next);
		}		
	}

	private void changeInParentInfo(InternalElement source, InternalElement dest) {
		internalCache.remove(source);
		final RodinElementInfo parentInfo = peekParentInfo(source);
		if (parentInfo != null) {
			parentInfo.changeChild(source, dest);
		}
	}

	private void moveInParentInfo(InternalElement child, InternalElement next) {
		final RodinElementInfo parentInfo = peekParentInfo(child);
		if (parentInfo != null) {
			parentInfo.moveChildBefore(child, next);
		}
	}

	private void removeFromParentInfo(InternalElement child) {
		internalCache.remove(child);
		final RodinElementInfo parentInfo = peekParentInfo(child);
		if (parentInfo != null) {
			parentInfo.removeChild(child);
		}
	}

}
