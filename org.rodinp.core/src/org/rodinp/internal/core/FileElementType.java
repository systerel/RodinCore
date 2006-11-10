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
import org.rodinp.core.basis.RodinFile;
import org.rodinp.internal.core.util.Util;

public class FileElementType extends
		ContributedElementType<RodinFile> implements IFileElementType {

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
	protected void computeConstructor() {
		Bundle bundle = Platform.getBundle(getBundleName());
		try {
			Class<?> clazz = bundle.loadClass(getClassName());
			classObject = clazz.asSubclass(RodinFile.class);
			constructor = classObject.getConstructor(IFile.class, IRodinElement.class);
		} catch (Exception e) {
			Util.log(e, "Can't find constructor for element type " + getId());
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

}
