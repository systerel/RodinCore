/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

public class FileElementType<T extends IRodinFile> extends
		ContributedElementType<T> implements IFileElementType<T> {

	// Content type associated to this file element type
	// (cached value)
	private IContentType contentType;

	// Unique identifier of the associated content type
	private String contentTypeId;
	
	public FileElementType(IConfigurationElement configurationElement) {
		super(configurationElement);
		this.contentTypeId = configurationElement.getAttribute("content-type-id");
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void computeClass() {
		Bundle bundle = Platform.getBundle(getBundleName());
		try {
			Class<?> clazz = bundle.loadClass(getClassName());
			classObject = (Class<? extends T>) clazz.asSubclass(RodinFile.class);
		} catch (Exception e) {
			throw new IllegalStateException(
					"Can't find class for element type " + getId(), e);
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
			throw new IllegalStateException(
					"Can't find constructor for element type " + getId(), e);
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
			throw new IllegalStateException(
					"Can't create an element of type " + getId(), e);
		}
	}

}
