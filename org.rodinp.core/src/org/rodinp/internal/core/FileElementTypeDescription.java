/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import java.lang.reflect.Constructor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.osgi.framework.Bundle;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinFile;

public class FileElementTypeDescription {

	private String id;
	private String name;
	private String contentTypeId;
	private IContentType contentType;
	private String className;
	private Class<? extends RodinFile> classObject;
	private Constructor<? extends RodinFile> constructor;
	private IConfigurationElement configurationElement;
	
	public FileElementTypeDescription(IConfigurationElement configurationElement) {
		this.id = configurationElement.getAttributeAsIs("id");
		this.name = configurationElement.getAttribute("name");
		this.contentTypeId = configurationElement.getAttributeAsIs("content-type-id");
		this.className = configurationElement.getAttributeAsIs("class");
		this.configurationElement = configurationElement;
	}

	public Class<? extends RodinFile> getClassObject() {
		if (classObject == null) {
			computeConstructor();
		}
		return classObject;
	}

	public Constructor<? extends RodinFile> getConstructor() {
		if (constructor == null) {
			computeConstructor();
		}
		return constructor;
	}

	private void computeConstructor() {
		String bundleName = configurationElement.getNamespace();
		Bundle bundle = Platform.getBundle(bundleName);
		try {
			Class<?> clazz = bundle.loadClass(className);
			classObject = clazz.asSubclass(RodinFile.class);
			constructor = classObject.getConstructor(IFile.class, IRodinElement.class);
		} catch (Exception e) {
			e.printStackTrace();
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

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
