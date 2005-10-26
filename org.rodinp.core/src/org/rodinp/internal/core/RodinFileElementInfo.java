/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinElement;
import org.rodinp.core.RodinFile;
import org.rodinp.internal.core.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RodinFileElementInfo extends OpenableElementInfo {

	// True iff the file has been changed since it was last parsed.
	private boolean changed;

	public RodinFileElementInfo() {
		// TODO implement constructor
		this.changed = false;
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
			IRodinElement rodinParent,
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
				RodinElement rodinChild = RodinDBManager.create(child, rodinParent);
				RodinElementInfo childInfo = new InternalElementInfo();
				// TODO bind child with its info
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
		
		if (rodinChildIndex == 0) {
			// No children
			rodinChildren = RodinElement.NO_ELEMENTS;
		} else if (rodinChildIndex != length) {
			// Adjust array size
			RodinElement[] newArray = new RodinElement[rodinChildIndex];
			System.arraycopy(rodinChildren, 0, newArray, 0, rodinChildIndex);
			rodinChildren = newArray;
		}		
		parentInfo.setChildren(rodinChildren);
	}

	private static RodinDBException newMalformedError(IRodinElement rodinElement) {
		IRodinDBStatus status = new RodinDBStatus(
				IRodinDBStatusConstants.MALFORMED_FILE_ERROR, 
				rodinElement);
		return new RodinDBException(status);
	}
	
	
	
}
