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
import org.w3c.dom.Node;

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
	 * Returns the DOM element corresponding to the given Rodin internal element,
	 * searching for a child of the given DOM element.
	 * 
	 * @param element
	 *            a Rodin internal element
	 * @param domParent
	 *            the DOM element corresponding to the parent of the given
	 *            element
	 * @return the DOM element corresponding to the given element or
	 *         <code>null</code> if not found.
	 */
	private Element findDOMChildElement(InternalElement element, Element domParent) {
		final String elementType = element.getElementType();
		final String elementName = element.getElementName();
		for (Node domChild = domParent.getFirstChild(); domChild != null;
				domChild = domChild.getNextSibling()) {
			if (domChild.getNodeType() == Node.ELEMENT_NODE
					&& elementType.equals(domChild.getNodeName())) {
				Element domElement = (Element) domChild;
				String domElementName = domElement.getAttributeNS(null, "name");
				if (elementName.equals(domElementName)) {
					return domElement;
				}
			}
		}
		return null;
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
		
		Element domParent = getDOMParent(element);
		if (domParent == null) {
			return null;
		}
		result = findDOMChildElement(element, domParent);
		if (result == null) {
			return null;
		}
		addToMap(element, result);
		return result;
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

//	// Returns the element info associated to an element of this file.
//	private RodinElementInfo getParentInfo(IRodinElement element) {
//		IRodinElement parent = element.getParent();
//		if (parent instanceof InternalElement) {
//			return getElementInfo((InternalElement) parent);
//		} else {
//			return this;
//		}
//	}

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

//	private void parserAddChildren(
//			Element parent, 
//			IInternalParent rodinParent,
//			RodinElementInfo parentInfo) throws RodinDBException {
//		
//		NodeList list = parent.getChildNodes();
//		int length = list.getLength();
//		if (length == 0) return;
//
//		RodinElement[] rodinChildren = new RodinElement[length];
//		int rodinChildIndex = 0;
//		boolean textNodeProcessed = false;
//
//		for (int i = 0; i < length; ++i) {
//			Node child = list.item(i);
//			switch (child.getNodeType()) {
//			case Node.ELEMENT_NODE:
//				InternalElement rodinChild = parserCreate((Element) child, rodinParent);
//				InternalElementInfo childInfo = new InternalElementInfo();
//				addToMap(rodinChild, childInfo);
//				parserAddChildren((Element) child, rodinChild, childInfo);
//				rodinChildren[rodinChildIndex ++] = rodinChild;
//				break;
//			case Node.TEXT_NODE:
//				String contents = child.getNodeValue();
//				contents = Util.trimSpaceChars(contents);
//				if (contents.length() != 0) {
//					// True text node
//					if (textNodeProcessed || ! (parentInfo instanceof InternalElementInfo)) {
//						// Two text nodes for the same parent
//						throw newMalformedError(rodinParent);
//					}
//					textNodeProcessed = true;
//					((InternalElementInfo) parentInfo).setContents(contents);
//				}
//				break;
//			default:
//				// Ignore other children.
//				break;
//			}
//		}
//		
//		// Adjust children array
//		if (rodinChildIndex == 0) {
//			rodinChildren = RodinElement.NO_ELEMENTS;
//		} else if (rodinChildIndex != length) {
//			RodinElement[] newArray = new RodinElement[rodinChildIndex];
//			System.arraycopy(rodinChildren, 0, newArray, 0, rodinChildIndex);
//			rodinChildren = newArray;
//		}		
//		parentInfo.setChildren(rodinChildren);
//	}

//	private InternalElement parserCreate(Element element, IInternalParent rodinParent) {
//		String rodinType = element.getTagName();
//		String name = element.getAttribute("name");
//		return (InternalElement) rodinParent.getInternalElement(rodinType, name);
//	}

//	private void printerAppendElement(StringBuilder result, InternalElement element, String tabs) {
//		String childTabs = tabs + "\t";
//		String elementType = element.getElementType();
//		result.append("<");
//		result.append(elementType);
//		if (! (element instanceof UnnamedInternalElement)) {
//			result.append(childTabs);
//			result.append("name=\"");
//			printerAppendEscapedString(result, element.getElementName());
//			result.append("\"");
//		}
//
//		// TODO save attributes here
//		
//		RodinElement[] children = RodinElement.NO_ELEMENTS;
//		String contents = "";
//		try {
//			children = element.getChildren();
//			contents = element.getContents();
//		} catch (RodinDBException e) {
//			Util.log(e, "Unexpected exception while saving a Rodin file.");
//			assert false;
//		}
//		if (children.length == 0 && contents.length() == 0) {
//			result.append(tabs);
//			result.append("/>");
//		} else {
//			result.append(childTabs);
//			result.append(">");
//			for (RodinElement child: children) {
//				printerAppendElement(result, (InternalElement) child, childTabs);
//			}
//			printerAppendEscapedString(result, contents);
//			result.append("</");
//			result.append(elementType);
//			result.append(tabs);
//			result.append(">");
//		}
//	}

//	private void printerAppendEscapedString(StringBuilder result, String name) {
//		for (char car: name.toCharArray()) {
//			switch (car) {
//			case '<':
//				result.append("&lt;");
//				break;
//			case '>':
//				result.append("&gt;");
//				break;
//			case '&':
//				result.append("&amp;");
//				break;
//			case '"':
//				result.append("&quot;");
//				break;
//			default:
//				result.append(car);
//			}
//		}
//	}

//	private String printerGetFileContents(RodinFile rodinFile) {
//		StringBuilder result = new StringBuilder();
//		String elementType = rodinFile.getElementType();
//		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
//		result.append("<");
//		result.append(elementType);
//		RodinElement[] children = this.getChildren();
//		if (children.length == 0) {
//			result.append("/>");
//		} else {
//			result.append("\n>");
//			for (RodinElement child: children) {
//				printerAppendElement(result, (InternalElement) child, "\n");
//			}
//			result.append("</");
//			result.append(elementType);
//			result.append(">\n");
//		}
//		return result.toString();
//	}

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
