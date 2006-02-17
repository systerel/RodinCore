/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.basis.RodinElement;
import org.rodinp.core.basis.RodinFile;
import org.rodinp.core.basis.UnnamedInternalElement;
import org.rodinp.internal.core.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class RodinFileElementInfo extends OpenableElementInfo {

	private static RodinDBException newMalformedError(IRodinElement rodinElement) {
		IRodinDBStatus status = new RodinDBStatus(
				IRodinDBStatusConstants.MALFORMED_FILE_ERROR, 
				rodinElement);
		return new RodinDBException(status);
	}
	
	private static class XMLErrorHandler implements ErrorHandler {

		public void warning(SAXParseException exception) throws SAXException {
			// ignore warnings
		}

		public void error(SAXParseException exception) throws SAXException {
			throw exception;
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			throw exception;
		}
		
	}
	
	// True iff the file has been changed since it was last parsed.
	private boolean changed = false;

	// Map of internal elements inside this file (at any depth)
	// All accesses to this field must be synchronized.
	private Map<InternalElement, InternalElementInfo> internalElements;

	public RodinFileElementInfo() {
		super();
		this.internalElements = new HashMap<InternalElement, InternalElementInfo>();
	}

	private void addToMap(InternalElement element, InternalElementInfo elementInfo) {
		// Make the occurrence count unique
		while (internalElements.containsKey(element)) {
			++ element.occurrenceCount;
		}
		internalElements.put(element, elementInfo);
	}

	public synchronized void changeDescendantContents(InternalElement descendant, String newContents) {
		InternalElementInfo info = getElementInfo(descendant);
		info.setContents(newContents);
		changed = true;
	}

	// dest must be an element of the Rodin file associated to this info.
	// TODO check for sourceInfo parameter removal
	public synchronized void copy(IInternalElement source,
			InternalElementInfo sourceInfo, InternalElement dest,
			InternalElement nextSibling) {
		
		getParentInfo(dest).addChildBefore(dest, nextSibling);
		copyToMap(source, sourceInfo, dest);
		changed = true;
	}

	// Clones deeply the source element.
	private void copyToMap(IInternalElement source,
			InternalElementInfo sourceInfo, InternalElement dest) {

		InternalElementInfo destInfo = dest.createElementInfo();
		addToMap(dest, destInfo);
		
		destInfo.setContents(sourceInfo.getContents());
		// TODO Copy attributes

		// Copy children
		RodinElement[] sourceChildren = sourceInfo.getChildren();
		int length = sourceChildren.length;
		RodinElement[] destChildren = new RodinElement[length];
		for (int i = 0; i < length; ++ i) {
			InternalElement sourceChild = (InternalElement) sourceChildren[i];
			InternalElement destChild = getChildSimilarTo(dest, sourceChild);
			destChildren[i] = destChild;

			try {
				InternalElementInfo sourceChildInfo = sourceChild.getElementInfo(null);
				copyToMap(sourceChild, sourceChildInfo, destChild);
			} catch (RodinDBException e) {
				Util.log(e, "when cloning an internal element sub-tree");
			}
		}
		destInfo.setChildren(destChildren);
	}

	// Returns a new handle for a child of <code>dest</code> with the same name
	// and type as <code>sourceChild</code>.
	private InternalElement getChildSimilarTo(InternalElement dest, InternalElement sourceChild) {
		return dest.getInternalElement(
				sourceChild.getElementType(),
				sourceChild.getElementName());
	}

	public synchronized void create(InternalElement newElement, InternalElement nextSibling) {
		addToMap(newElement, newElement.createElementInfo());
		getParentInfo(newElement).addChildBefore(newElement, nextSibling);
		changed = true;
	}
	
	public synchronized void delete(InternalElement element) {
		// First remove the element from its parent.
		getParentInfo(element).removeChild(element);

		// Then, remove the element and all its descendants from this file.
		removeFromMap(element);
		changed = true;
	}

	public synchronized InternalElementInfo getElementInfo(IInternalElement element) {
		return internalElements.get(element);
	}

	// Returns the element info associated to an element of this file.
	private RodinElementInfo getParentInfo(IRodinElement element) {
		IRodinElement parent = element.getParent();
		if (parent instanceof InternalElement) {
			return getElementInfo((InternalElement) parent);
		} else {
			return this;
		}
	}

	public synchronized boolean hasUnsavedChanges() {
		return changed;
	}

	// Returns true if parse was successful
	public synchronized boolean parseFile(IProgressMonitor pm, RodinFile rodinFile) throws RodinDBException {
		RodinDBManager manager = RodinDBManager.getRodinDBManager();
		DocumentBuilder builder = manager.getDocumentBuilder();
		builder.setErrorHandler(new XMLErrorHandler());
		try {
			Document document = builder.parse(rodinFile.getResource().getContents());
			Element root = document.getDocumentElement();
			parserAddChildren(root, rodinFile, this);
		} catch (SAXException e) {
			throw new RodinDBException(e, IRodinDBStatusConstants.XML_PARSE_ERROR);
		} catch (IOException e) {
			throw new RodinDBException(e, IRodinDBStatusConstants.IO_EXCEPTION);
		} catch (RodinDBException e) {
			throw e;
		} catch (CoreException e) {
			throw new RodinDBException(e);
		}
		this.changed = false;
		return true;
	}

	private void parserAddChildren(
			Element parent, 
			IInternalParent rodinParent,
			RodinElementInfo parentInfo) throws RodinDBException {
		
		NodeList list = parent.getChildNodes();
		int length = list.getLength();
		if (length == 0) return;

		RodinElement[] rodinChildren = new RodinElement[length];
		int rodinChildIndex = 0;
		boolean textNodeProcessed = false;

		for (int i = 0; i < length; ++i) {
			Node child = list.item(i);
			switch (child.getNodeType()) {
			case Node.ELEMENT_NODE:
				InternalElement rodinChild = parserCreate((Element) child, rodinParent);
				InternalElementInfo childInfo = new InternalElementInfo();
				addToMap(rodinChild, childInfo);
				parserAddChildren((Element) child, rodinChild, childInfo);
				rodinChildren[rodinChildIndex ++] = rodinChild;
				break;
			case Node.TEXT_NODE:
				String contents = child.getNodeValue();
				contents = Util.trimSpaceChars(contents);
				if (contents.length() != 0) {
					// True text node
					if (textNodeProcessed || ! (parentInfo instanceof InternalElementInfo)) {
						// Two text nodes for the same parent
						throw newMalformedError(rodinParent);
					}
					textNodeProcessed = true;
					((InternalElementInfo) parentInfo).setContents(contents);
				}
				break;
			default:
				// Ignore other children.
				break;
			}
		}
		
		// Adjust children array
		if (rodinChildIndex == 0) {
			rodinChildren = RodinElement.NO_ELEMENTS;
		} else if (rodinChildIndex != length) {
			RodinElement[] newArray = new RodinElement[rodinChildIndex];
			System.arraycopy(rodinChildren, 0, newArray, 0, rodinChildIndex);
			rodinChildren = newArray;
		}		
		parentInfo.setChildren(rodinChildren);
	}

	private InternalElement parserCreate(Element element, IInternalParent rodinParent) {
		String rodinType = element.getTagName();
		String name = element.getAttribute("name");
		return (InternalElement) rodinParent.getInternalElement(rodinType, name);
	}

	private void printerAppendElement(StringBuilder result, InternalElement element, String tabs) {
		String childTabs = tabs + "\t";
		String elementType = element.getElementType();
		result.append("<");
		result.append(elementType);
		if (! (element instanceof UnnamedInternalElement)) {
			result.append(childTabs);
			result.append("name=\"");
			printerAppendEscapedString(result, element.getElementName());
			result.append("\"");
		}

		// TODO save attributes here
		
		RodinElement[] children = RodinElement.NO_ELEMENTS;
		String contents = "";
		try {
			children = element.getChildren();
			contents = element.getContents();
		} catch (RodinDBException e) {
			Util.log(e, "Unexpected exception while saving a Rodin file.");
			assert false;
		}
		if (children.length == 0 && contents.length() == 0) {
			result.append(tabs);
			result.append("/>");
		} else {
			result.append(childTabs);
			result.append(">");
			for (RodinElement child: children) {
				printerAppendElement(result, (InternalElement) child, childTabs);
			}
			printerAppendEscapedString(result, contents);
			result.append("</");
			result.append(elementType);
			result.append(tabs);
			result.append(">");
		}
	}

	private void printerAppendEscapedString(StringBuilder result, String name) {
		for (char car: name.toCharArray()) {
			switch (car) {
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			case '&':
				result.append("&amp;");
				break;
			case '"':
				result.append("&quot;");
				break;
			default:
				result.append(car);
			}
		}
	}

	private String printerGetFileContents(RodinFile rodinFile) {
		StringBuilder result = new StringBuilder();
		String elementType = rodinFile.getElementType();
		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		result.append("<");
		result.append(elementType);
		RodinElement[] children = this.getChildren();
		if (children.length == 0) {
			result.append("/>");
		} else {
			result.append("\n>");
			for (RodinElement child: children) {
				printerAppendElement(result, (InternalElement) child, "\n");
			}
			result.append("</");
			result.append(elementType);
			result.append(">\n");
		}
		return result.toString();
	}

	// Removes an element and all its descendants from this file.
	private void removeFromMap(IInternalElement element) {
		InternalElementInfo info = internalElements.get(element);
		internalElements.remove(element);
		if (info != null) {
			for (RodinElement child: info.getChildren()) {
				removeFromMap((IInternalElement) child);
			}
		}
	}

	/**
	 * Renames an element within this file.
	 * 
	 * @param source
	 *            the source element
	 * @param dest
	 *            the destination element
	 */
	public synchronized void rename(InternalElement source, InternalElement dest) {
		assert source.getParent().equals(dest.getParent());
		assert source.getClass() == dest.getClass();
		assert ! (source instanceof UnnamedInternalElement);
		
		getParentInfo(source).replaceChild(source, dest);
			
		// Change handles for descendants.
		renameInMap(source, dest);
		changed = true;
	}

	private void renameInMap(InternalElement source, InternalElement dest) {
		InternalElementInfo info = getElementInfo(source);
		internalElements.remove(source);
		internalElements.put(dest, info);
		
		// Change children handles
		RodinElement[] children = info.getChildren();
		for (int i = 0; i < children.length; ++ i) {
			InternalElement sourceChild = (InternalElement) children[i];
			InternalElement destChild = getChildSimilarTo(dest, sourceChild);
			renameInMap(sourceChild, destChild);
			children[i] = destChild;
		}
	}

	// Returns true iff a change was made to the order of the parent children.
	public synchronized boolean reorder(InternalElement source, InternalElement nextSibling) {
		boolean didChange = getParentInfo(source).reorderBefore(source, nextSibling);
		changed |= didChange;
		return didChange;
	}

	public synchronized void saveToFile(RodinFile rodinFile, boolean force, IProgressMonitor pm) throws RodinDBException {
		IFile file = rodinFile.getResource();
		
		// use a platform operation to update the resource contents
		try {
			String stringContents = this.printerGetFileContents(rodinFile);
			if (stringContents == null) return;
			byte[] bytes = stringContents.getBytes("UTF-8");
			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);

			if (file.exists()) {
				file.setContents(
					stream, 
					force ? IResource.FORCE | IResource.KEEP_HISTORY : IResource.KEEP_HISTORY, 
					pm);
			} else {
				file.create(stream, force, pm);
			}	
		} catch (IOException e) {
			throw new RodinDBException(e, IRodinDBStatusConstants.IO_EXCEPTION);
		} catch (CoreException e) {
			throw new RodinDBException(e);
		}

		// the resource no longer has unsaved changes
		this.changed = false;
	}

}
