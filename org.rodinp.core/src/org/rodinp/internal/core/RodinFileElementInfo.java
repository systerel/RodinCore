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
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.InternalElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinElement;
import org.rodinp.core.RodinFile;
import org.rodinp.core.UnnamedInternalElement;
import org.rodinp.internal.core.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RodinFileElementInfo extends OpenableElementInfo {

	// True iff the file has been changed since it was last parsed.
	private boolean changed;
	
	// Map of internal elements inside this file (at any depth)
	// All accesses to this field must be synchronized.
	private Map<InternalElement, InternalElementInfo> internalElements;

	public RodinFileElementInfo() {
		super();
		this.changed = false;
		this.internalElements = new HashMap<InternalElement, InternalElementInfo>();
	}

	public boolean hasUnsavedChanges() {
		return changed;
	}

	// Returns true if parse was successful
	public synchronized boolean parseFile(IProgressMonitor pm, RodinFile rodinFile) throws RodinDBException {
		RodinDBManager manager = RodinDBManager.getRodinDBManager();
		DocumentBuilder builder = manager.getDocumentBuilder();
		try {
			Document document = builder.parse(rodinFile.getResource().getContents());
			Element root = document.getDocumentElement();
			addChildren(root, rodinFile, this);
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

	private void addChildren(
			Element parent, 
			RodinElement rodinParent,
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
				InternalElement rodinChild = create((Element) child, rodinParent);
				InternalElementInfo childInfo = new InternalElementInfo();
				addElement(rodinChild, childInfo);
				addChildren((Element) child, rodinChild, childInfo);
				rodinChildren[rodinChildIndex ++] = rodinChild;
				break;
			case Node.TEXT_NODE:
				String contents = child.getNodeValue();
				contents = Util.trimSpaceChars(contents);
				if (contents.length() != 0) {
					// True text node
					if (textNodeProcessed) {
						// Two text nodes for the same parent
						throw newMalformedError(rodinParent);
					}
					textNodeProcessed = true;
					// TODO store the text in the rodinParent.
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

	protected synchronized void addElement(InternalElement element, InternalElementInfo elementInfo) {
		// Make the occurrence count unique
		while (internalElements.containsKey(element)) {
			++ element.occurrenceCount;
		}
		internalElements.put(element, elementInfo);
		changed = true;
	}

	private InternalElement create(Element element, RodinElement rodinParent) {
		String rodinType = element.getTagName();
		String name = element.getAttribute("name");
		
		// TODO use rodinParent.create(rodinType, name)
		
		ElementTypeManager manager = ElementTypeManager.getElementTypeManager();
		return manager.createInternalElementHandle(rodinType, name, rodinParent);
	}

	private static RodinDBException newMalformedError(IRodinElement rodinElement) {
		IRodinDBStatus status = new RodinDBStatus(
				IRodinDBStatusConstants.MALFORMED_FILE_ERROR, 
				rodinElement);
		return new RodinDBException(status);
	}

	public synchronized void remove(InternalElement element) {
		internalElements.remove(element);
	}

	public synchronized RodinElementInfo getElementInfo(InternalElement element) {
		return internalElements.get(element);
	}

	public synchronized void saveToFile(RodinFile rodinFile, boolean force, IProgressMonitor pm) throws RodinDBException {
		IFile file = rodinFile.getResource();
		
		// use a platform operation to update the resource contents
		try {
			String stringContents = this.getFileContents(rodinFile);
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

	private String getFileContents(RodinFile rodinFile) {
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
				appendElement(result, child, "\n");
			}
			result.append("</");
			result.append(elementType);
			result.append(">\n");
		}
		return result.toString();
	}

	private void appendElement(StringBuilder result, RodinElement element, String tabs) {
		String childTabs = tabs + "\t";
		String elementType = element.getElementType();
		result.append("<");
		result.append(elementType);
		if (! (element instanceof UnnamedInternalElement)) {
			result.append(childTabs);
			result.append("name=\"");
			appendEscapedString(result, element.getElementName());
			result.append("\"");
		}

		// TODO save attributes here
		
		RodinElement[] children = RodinElement.NO_ELEMENTS;
		try {
			children = element.getChildren();
		} catch (RodinDBException e) {
			assert false;
		}
		if (children.length == 0) {
			result.append(tabs);
			result.append("/>");
		} else {
			result.append(childTabs);
			result.append(">");
			for (RodinElement child: children) {
				appendElement(result, child, childTabs);
			}
			result.append("</");
			result.append(elementType);
			result.append(tabs);
			result.append(">");
		}
	}

	private void appendEscapedString(StringBuilder result, String name) {
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

}
