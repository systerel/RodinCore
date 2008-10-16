/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.osgi.framework.Bundle;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.basis.RodinFile;
import org.rodinp.internal.core.util.Util;

public class FileElementType
		extends ContributedElementType<IRodinFile> implements IFileElementType {

	// Content type associated to this file element type
	// (cached value)
	private IContentType contentType;

	// Unique identifier of the associated content type
	private String contentTypeId;
	
	// Type of the root element of the file
	private InternalElementType<?> rootType;
	
	// Unique identifier of the associated root type
	private String rootTypeId;
	
	public FileElementType(IConfigurationElement ce) {
		super(ce);
		this.contentTypeId = getAttribute(ce, "content-type-id");
		this.rootTypeId = getAttribute(ce, "root-element-type");
	}

	private String getAttribute(IConfigurationElement ce, String attrName) {
		final String result = ce.getAttribute(attrName);
		if (result == null) {
			throw new NullPointerException("Missing " + attrName
					+ " for element type " + id);
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void computeClass() {
		Bundle bundle = Platform.getBundle(getBundleName());
		try {
			Class<?> clazz = bundle.loadClass(getClassName());
			classObject = (Class<? extends IRodinFile>) clazz.asSubclass(RodinFile.class);
		} catch (Exception e) {
			String message = "Can't find class for element type " + getId();
			Util.log(null, message);
			throw new IllegalStateException(message, e);
		}
	}

	@Override
	protected void computeConstructor() {
		if (classObject == null) {
			computeClass();
		}
		try {
			constructor = classObject.getConstructor(IFile.class, IRodinElement.class);
		} catch (Exception e) {
			String message = "Can't find constructor for element type "
					+ getId();
			Util.log(null, message);
			throw new IllegalStateException(message, e);
		}
	}

	public IContentType getContentType() {
		if (contentType == null) {
			IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
			contentType = contentTypeManager.getContentType(contentTypeId);
		}
		return contentType;
	}

	public String getContentTypeId() {
		return contentTypeId;
	}

	public InternalElementType<?> getRootElementType() {
		if (rootType == null) {
			computeRootType();
		}
		return rootType;
	}

	private void computeRootType() {
		final ElementTypeManager etm = ElementTypeManager.getInstance();
		final InternalElementType<?> type = etm
				.getInternalElementType(rootTypeId);
		if (type == null) {
			final String msg = "Root element type for " + this + " doesn't exist";
			Util.log(null, msg);
			throw new IllegalStateException(msg);
		}
		rootType = type;
	}

	public String getRootElementTypeId() {
		return rootTypeId;
	}

	public IRodinFile createInstance(IFile file, RodinProject project) {
		if (constructor == null) {
			computeConstructor();
		}
		if (constructor == null) {
			return null;
		}
		try {
			return constructor.newInstance(file, project);
		} catch (Exception e) {
			String message = "Can't create an element of type " + getId();
			Util.log(null, message);
			throw new IllegalStateException(message, e);
		}
	}

}
