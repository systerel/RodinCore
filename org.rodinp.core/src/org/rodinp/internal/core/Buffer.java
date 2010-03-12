/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added deleteElementChildren() method
 *     Systerel - removed deprecated methods (contents)
 *     Systerel - separation of file and root element
 *     Systerel - generic attribute manipulation
 *     Systerel - made NAME_ATTRIBUTE and VERSION_ATTRIBUTE public
 *     Systerel - refactored automatic file conversion
 *******************************************************************************/
package org.rodinp.internal.core;

import static org.eclipse.core.runtime.IStatus.OK;
import static org.rodinp.core.IRodinDBStatusConstants.FUTURE_VERSION;
import static org.rodinp.core.IRodinDBStatusConstants.INVALID_VERSION_NUMBER;
import static org.rodinp.core.IRodinDBStatusConstants.IO_EXCEPTION;
import static org.rodinp.core.IRodinDBStatusConstants.PAST_VERSION;
import static org.rodinp.core.IRodinDBStatusConstants.XML_PARSE_ERROR;
import static org.rodinp.core.IRodinDBStatusConstants.XML_SAVE_ERROR;
import static org.rodinp.internal.core.version.VersionManager.UNKNOWN_VERSION;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.internal.core.util.Util;
import org.rodinp.internal.core.version.ConversionEntry;
import org.rodinp.internal.core.version.VersionManager;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Implements a Rodin file loaded into memory. 
 * <p>
 * The file is stored as a DOM document.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class Buffer {
	
	protected static class XMLErrorHandler implements ErrorHandler {
		
		public void error(SAXParseException exception) throws SAXException {
			throw exception;
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			throw exception;
		}

		public void warning(SAXParseException exception) throws SAXException {
			// ignore warnings
		}
		
	}

	protected static class XMLErrorListener implements ErrorListener {
		
		public void error(TransformerException e) throws TransformerException {
			throw e;
		}

		public void fatalError(TransformerException e) throws TransformerException {
			throw e;
		}

		public void warning(TransformerException e) throws TransformerException {
			// ignore warnings
		}
		
	}

	private static final String CONTENTS_ATTRIBUTE = "contents";
	
	public static final String NAME_ATTRIBUTE = "name";

	private static final ErrorListener errorListener = new XMLErrorListener();
	
	
	public static final String VERSION_ATTRIBUTE = "version";
	private static final ErrorHandler errorHandler = new XMLErrorHandler();
	private Document domDocument;
	/**
	 * The file for which this buffer was created.
	 */
	private final RodinFile owner;

	@SuppressWarnings("unused")
	private long stamp;
	private long version;
	
	// Whether this buffer has been fully loaded.
	private AtomicBoolean loaded = new AtomicBoolean();

	private volatile boolean changed;

	public Buffer(RodinFile owner) {
		this.owner = owner;
		this.stamp = IResource.NULL_STAMP;
		this.version = UNKNOWN_VERSION;
	}
	
	public boolean hasBeenLoaded() {
		return loaded.get();
	}
	
	/*
	 * Loads this buffer from the corresponding resource.
	 */
	public synchronized void load(IProgressMonitor pm) throws RodinDBException {
		if (hasBeenLoaded()) {
			// Buffer has already been loaded (maybe concurrently by another
			// thread).
			return;
		}
		final SubMonitor sm = SubMonitor.convert(pm, 2);
		attemptLoad(sm.newChild(1));
		int status = checkVersion();
		if (status == PAST_VERSION && attemptUpgrade()) {
			attemptLoad(sm.newChild(1));
			status = checkVersion();
		}
		if (status != OK) {
			throw versionProblem(status, Long.toString(version));
		}
		
		normalize(domDocument.getDocumentElement());
		changed = false;
		loaded.set(true);
	}
	
	public void attemptLoad(IProgressMonitor pm) throws RodinDBException {
		final RodinDBManager manager = RodinDBManager.getRodinDBManager();
		final DocumentBuilder builder = manager.getDocumentBuilder();
		builder.setErrorHandler(errorHandler);

		try {
			final IFile file = owner.getResource();
			stamp = file.getModificationStamp();
			domDocument = builder.parse(file.getContents());
		} catch (SAXException e) {
			throw new RodinDBException(e, XML_PARSE_ERROR);
		} catch (IOException e) {
			throw new RodinDBException(e, IO_EXCEPTION);
		} catch (RodinDBException e) {
			throw e;
		} catch (CoreException e) {
			throw new RodinDBException(e);
		}

		// the version is always fetched from the file;
		// if it cannot be verified, then the document is not fetched (although it was parsed successfully)
		version = fetchVersion(domDocument);
	}

	private int checkVersion() throws RodinDBException {
		Element root = domDocument.getDocumentElement();
		IInternalElementType<?> rootType = getElementType(root);
		long reqVersion = VersionManager.getInstance().getVersion(rootType);

		if (version > reqVersion) {
			return FUTURE_VERSION;
		} else if (version < reqVersion) {
			return PAST_VERSION;
		} else {
			return OK;
		}
	}

	private boolean attemptUpgrade() throws RodinDBException {
		final ConversionEntry cv = new ConversionEntry(owner, version);
		final VersionManager vManager = VersionManager.getInstance();
		cv.upgrade(vManager, false, null);
		final boolean success = cv.success();
		if (success) {
			cv.accept(false, true, null);
		}
		return success;
	}

	private RodinDBException versionProblem(int code, String versionStr) {
		final RodinDBStatus status = new RodinDBStatus(code, owner, versionStr);
		return new RodinDBException(status);
	}

	public Element getDocumentElement() {
		return domDocument.getDocumentElement();
	}

	/**
	 * Returns the Rodin file element of this buffer.
	 * 
	 * @return the handle of the file element for which this buffer was created.
	 */
	public final RodinFile getOwner() {
		return owner;
	}

	/**
	 * Returns the version number of the Rodin file element of this buffer
	 * 
	 * @return the version number of the Rodin file element of this buffer
	 * @throws RodinDBException if the version number is invalid
	 */
	public long getVersion() throws RodinDBException {
		return version;
	}

	private long fetchVersion(Document document) throws RodinDBException {
		long v;
		Element documentElement = document.getDocumentElement();
		if (documentElement.hasAttributeNS(null, VERSION_ATTRIBUTE)) {
			String rawVersion = documentElement.getAttributeNS(null, VERSION_ATTRIBUTE);
			try {
				v = Long.parseLong(rawVersion);
			} catch (NumberFormatException e) {
				throw versionProblem(INVALID_VERSION_NUMBER, rawVersion);
			}
			if (v < 0) {
				throw versionProblem(INVALID_VERSION_NUMBER, rawVersion);
			}
		} else {
			v = 0;
		}
		return v;
	}

	public Element createElement(IInternalElementType<?> type, String name, Element domParent,
			Element domNextSibling) {
		
		Element domNewElement = domDocument.createElementNS(null, type.getId());
		domNewElement.setAttributeNS(null, NAME_ATTRIBUTE, name);
		domParent.insertBefore(domNewElement, domNextSibling);
		changed = true;
		return domNewElement;
	}

	public void deleteElement(Element domElement) {
		Node domParent = domElement.getParentNode();
		domParent.removeChild(domElement);
		changed = true;
	}

	public void deleteElementChildren(Element domElement) {
		Node domFirstChild = domElement.getFirstChild();
		while (domFirstChild != null) {
			domElement.removeChild(domFirstChild);
			changed = true;
			domFirstChild = domElement.getFirstChild();
		}
	}

	/**
	 * Returns the DOM text element corresponding to the contents of the given
	 * Rodin internal element.
	 * <p>
	 * Actually, it's the last child text element which doesn't contain only
	 * whitespace that is returned. No check is performed that there is a unique
	 * such text node.
	 * </p>
	 * 
	 * @param domElement
	 *            the DOM element corresponding to the Rodin element
	 * @return the DOM element corresponding to the contents of the
	 *         corresponding Rodin element or <code>null</code> if not found.
	 */
	private Text findDOMTextElement(Element domElement) {
		for (Node domChild = domElement.getLastChild(); domChild != null;
				domChild = domChild.getPreviousSibling()) {
			if (domChild.getNodeType() == Node.TEXT_NODE) {
				String contents = domChild.getNodeValue();
				contents = Util.trimSpaceChars(contents);
				if (contents.length() != 0) {
					return (Text) domChild;
				}
			}
		}
		return null;
	}

	public Map<String, String> getAttributes(Element domElement) {
		final NamedNodeMap attributeMap = domElement.getAttributes();
		final int length = attributeMap.getLength();
		final Map<String, String> result = new HashMap<String, String>(
				length * 2);
		for (int i = 0; i < length; i++) {
			final Node item = attributeMap.item(i);
			result.put(item.getLocalName(), item.getNodeValue());
		}
		return result;
	}

	/**
	 * Returns the value of the attribute with the given name carried by the
	 * given element.
	 * 
	 * @param domElement
	 *            the element
	 * @param attrName
	 *            the name of the attribute
	 * @return the value of the specified attribute or <code>null</code> if
	 *         there is no such attribute.
	 */
	public String getAttributeRawValue(Element domElement, String attrName) {
		Attr attribute = domElement.getAttributeNodeNS(null, attrName);
		if (attribute == null) {
			return null;
		}
		return attribute.getValue();
	}
	
	public LinkedHashMap<InternalElement, Element> getChildren(
			IInternalElement element, Element domElement) {

		assert domElement.getParentNode() != null;
		LinkedHashMap<InternalElement, Element> result = 
			new LinkedHashMap<InternalElement, Element>();
		for (Node domChildNode = domElement.getFirstChild();
				domChildNode != null;
				domChildNode = domChildNode.getNextSibling()) {
		
			if (domChildNode.getNodeType() == Node.ELEMENT_NODE) {
				final Element domChild = (Element) domChildNode;
				final InternalElement child = 
					getElement(element, domChild);
				if (child != null) {
					final Element domOtherChild = result.put(child, domChild);
					if (domOtherChild != null) {
						Util.log(null, "Duplicate child "
								+ child.toStringWithAncestors());
						// Put back the first one.
						result.put(child, domOtherChild);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Returns the Rodin element corresponding to <code>domChild</code> in the
	 * given parent. Returns <code>null</code> in case of error building the
	 * Rodin element.
	 * 
	 * @param parent
	 * @param domChild
	 * @return the Rodin element corresponding to the given DOM child, or
	 *         <code>null</code> in case of error
	 */
	private InternalElement getElement(IInternalElement parent,
			Element domChild) {
		
		try {
			final IInternalElementType<? extends IInternalElement> childType =
				getElementType(domChild);
			final String childName = getElementName(domChild);
			if (childName == null) {
				Util.log(null, "internal element with no name in file " + owner);
				return null;
			}
			return (InternalElement) parent.getInternalElement(childType,
					childName);
		} catch (Exception e) {
			Util.log(e, "when creating an internal element from file " + owner);
			return null;
		}
	}

	public String getElementName(Element domElement) {
		return domElement.getAttributeNS(null, NAME_ATTRIBUTE);
	}

	public IInternalElementType<? extends IInternalElement> getElementType(
			Element domElement) {

		final String id = domElement.getNodeName();
		return RodinCore.getInternalElementType(id);
	}
	
	/**
	 * Returns whether the give element carries an attribute with the given name
	 * in this buffer.
	 * 
	 * @return <code>true</code> iff the given element carries the given
	 *         attribute in this buffer
	 */
	public boolean hasAttribute(Element domElement, String attrName) {
		return domElement.hasAttributeNS(null, attrName);
	}

	/**
	 * Returns whether this buffer has been modified since it was last loaded.
	 * 
	 * @return <code>true</code> iff this buffer has been modified
	 */
	public boolean hasUnsavedChanges() {
		return changed;
	}

	public Element importNode(Element domSource, Element domDestParent,
			Element domNextSibling, String newName) {

		Element domNewNode = (Element) domDocument.importNode(domSource, true);
		domNewNode.setAttributeNS(null, NAME_ATTRIBUTE, newName);
		domDestParent.insertBefore(domNewNode, domNextSibling);
		changed = true;
		return domNewNode;
	}

	private boolean isReorderNoop(Element domSource, Element domNextSibling) {
		// Let's get the successor of the source element to reorder
		Node domSucc = domSource.getNextSibling();
		while (domSucc != null && domSucc.getNodeType() != Node.ELEMENT_NODE) {
			domSucc = domSucc.getNextSibling();
		}
		
		if (domNextSibling == null) {
			// To be reordered as last child
			return domSucc == null;
		}
		
		return domNextSibling.isSameNode(domSource)
				|| domNextSibling.isSameNode(domSucc);
	}

	/*
	 * Normalizes the given element and all its descendants.
	 * 
	 * In previous versions of the database, the "contents" pseudo-attribute was
	 * stored in a Text node. It's not the case anymore. It's now stored in a
	 * proper attribute called "contents".
	 * 
	 * This method normalizes this element by removing all Text nodes, and
	 * adding a new contents attribute which will contain the value of the last
	 * non-empty text node, where a Text node is considered empty if it contains
	 * only white space.
	 * 
	 * If the node is already in the new format, this method just deleted all
	 * text nodes.
	 */
	private void normalize(Element domElement) {
		if (domElement.getAttributeNodeNS(null, CONTENTS_ATTRIBUTE) == null) {
			// Old format
			Text domText = findDOMTextElement(domElement);
			if (domText != null) {
				domElement.setAttributeNS(null, CONTENTS_ATTRIBUTE, 
						domText.getNodeValue());
			}
		}

		// Delete all text nodes and process children recursively
		Node domChild = domElement.getFirstChild();
		while (domChild != null) {
			Node nextChild = domChild.getNextSibling();
			switch (domChild.getNodeType()) {
			case Node.ELEMENT_NODE:
				normalize((Element) domChild);
				break;
			case Node.TEXT_NODE:
				domElement.removeChild(domChild);
				break;
			}
			domChild = nextChild;
		}
	}

	public boolean removeAttribute(Element domElement, String attrName) {
		if (domElement.hasAttributeNS(null, attrName)) {
			domElement.removeAttributeNS(null, attrName);
			changed = true;
			return true;
		}
		return false;
	}
	
	public void renameElement(Element domElement, String newName) {
		domElement.setAttributeNS(null, NAME_ATTRIBUTE, newName);
		changed = true;
	}

	public boolean reorderElement(Element domSource, Element domNextSibling) {
		if (isReorderNoop(domSource, domNextSibling)) {
			// No change
			return false;
		}
		
		Element domParent = (Element) domSource.getParentNode();
		domParent.insertBefore(domSource, domNextSibling);
		changed = true;
		return true;
	}

	/*
	 * Save this buffer to the corresponding resource.
	 */
	public synchronized void save(final boolean force,
			final boolean keepHistory, ISchedulingRule rule, IProgressMonitor pm)
			throws RodinDBException {
		
		final RodinDBManager manager = RodinDBManager.getRodinDBManager();
		final Transformer transformer = manager.getDOMTransformer();

		// TODO use the progress monitor while transforming
		try {
			final ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			final StreamResult domResult = new StreamResult(oStream);

			transformer.setErrorListener(errorListener);
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	        transformer.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml");
	        transformer.transform(new DOMSource(domDocument), domResult);
			
			byte[] bytes = oStream.toByteArray();
			final ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			final IFile file = owner.getResource();
			final IWorkspaceRunnable action = new IWorkspaceRunnable() {
				@SuppressWarnings("synthetic-access")
				public void run(IProgressMonitor monitor) throws CoreException {
					file.setContents(stream, force, keepHistory, monitor);
					stamp = file.getModificationStamp();
				}
			};
			file.getWorkspace().run(action, rule, 0, pm);
		} catch (TransformerException e) {
			throw new RodinDBException(e, XML_SAVE_ERROR);
		} catch (RodinDBException e) {
			throw e;
		} catch (CoreException e) {
			throw new RodinDBException(e);
		}

		// the resource no longer has unsaved changes
		this.changed = false;
	}
	
	public void setAttributeRawValue(Element domElement, String attrName,
			String newRawValue) {
		domElement.setAttributeNS(null, attrName, newRawValue);
		changed = true;
	}

}
