/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.internal.core;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.basis.RodinFile;
import org.rodinp.internal.core.util.Util;

/**
 * Manager for Rodin element types.
 * <p>
 * Most element types are contributed by plugins through two extension points,
 * one for file elements and one for internal elements.  This singleton class
 * manages the various contributions and provides a unique place for creating
 * element handles for these types, using reflection.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class ElementTypeManager {

	/**
	 * The singleton manager
	 */
	private static ElementTypeManager MANAGER = new ElementTypeManager();
	
	/**
	 * Returns the singleton ElementTypeManager
	 */
	public final static ElementTypeManager getElementTypeManager() {
		return MANAGER;
	}

	private ElementTypeManager() {
		// singleton: prevent others from creating a new instance
	}

	/**
	 * Creates a Rodin file element.
	 * 
	 * @param project
	 *            the element's Rodin project
	 * @param filename
	 *            the name of the file element to create
	 * @return a handle on the file element or <code>null</code> if unable to
	 *         determine the file type.
	 */
	public RodinFile createRodinFileHandle(RodinProject project, String filename) {
		FileElementTypeDescription description = getFileElementTypeDescription(filename);
		if (description == null) {
			return null;		// Not a Rodin file.
		}
		Constructor<? extends RodinFile> constructor = description.getConstructor();
		IFile file = project.getProject().getFile(filename);
		try {
			return constructor.newInstance(file, project);
		} catch (Exception e) {
			// Some error occurred while reflecting.
			e.printStackTrace();
			return null;
		}
	}
	
	private FileElementTypeDescription getFileElementTypeDescription(IFile file) {
		try {
			IContentDescription contentDescription = file.getContentDescription();
			if (contentDescription == null) return null; // Unknown kind of file.
			IContentType contentType = contentDescription.getContentType();
			if (contentType == null) return null; // Unknown kind of file.
			return getFileElementTypeDescription(contentType);
		} catch (CoreException e) {
			// Ignore
		}
		// Maybe the file doesn't exist, try with its filename
		return getFileElementTypeDescription(file.getName());
	}
	
	private FileElementTypeDescription getFileElementTypeDescription(String fileName) {
		IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
		IContentType contentType = contentTypeManager.findContentTypeFor(fileName);
		if (contentType == null) {
			return null;		// Not a Rodin file.
		}
		return getFileElementTypeDescription(contentType);
	}

	private FileElementTypeDescription getFileElementTypeDescription(IContentType contentType) {
		if (fileContentTypes == null) {
			computeFileElementTypes();
		}
		return fileContentTypes.get(contentType.getId());
	}

	// Local id of the fileElementTypes extension point of this plugin
	private static final String FILE_ELEMENT_TYPES_ID = "fileElementTypes";
	
	// Access to file element type descriptions using their content type name
	private HashMap<String, FileElementTypeDescription> fileContentTypes;

	// Access to file element type descriptions using their unique id
	private HashMap<String, FileElementTypeDescription> fileElementTypeIds;

	private void computeFileElementTypes() {
		fileElementTypeIds = new HashMap<String, FileElementTypeDescription>();
		fileContentTypes = new HashMap<String, FileElementTypeDescription>();
		
		// Read the extension point extensions.
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = 
			registry.getConfigurationElementsFor(RodinCore.PLUGIN_ID, FILE_ELEMENT_TYPES_ID);
		for (IConfigurationElement element: elements) {
			FileElementTypeDescription description = new FileElementTypeDescription(element);
			fileElementTypeIds.put(description.getId(), description);
			fileContentTypes.put(description.getContentTypeId(), description);
		}
	}

	/**
	 * Tells whether the given Rodin element type is a file element type.
	 * 
	 * @param elementType
	 *            the element type to test
	 * @return <code>true</code> iff it is a file element type
	 */
	public boolean isFileElementType(String elementType) {
		if (fileElementTypeIds== null) {
			computeFileElementTypes();
		}
		return fileElementTypeIds.containsKey(elementType);
	}

	// Local id of the fileElementTypes extension point of this plugin
	private static final String INTERNAL_ELEMENT_TYPES_ID = "internalElementTypes";
	
	// Access to internal element type descriptions using their unique id
	private HashMap<String, InternalElementTypeDescription> internalElementTypeIds;

	private void computeInternalElementTypes() {
		internalElementTypeIds = new HashMap<String, InternalElementTypeDescription>();
		
		// Read the extension point extensions.
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = 
			registry.getConfigurationElementsFor(RodinCore.PLUGIN_ID, INTERNAL_ELEMENT_TYPES_ID);
		for (IConfigurationElement element: elements) {
			InternalElementTypeDescription description = new InternalElementTypeDescription(element);
			internalElementTypeIds.put(description.getId(), description);
		}
	}

	/**
	 * Tells whether the given Rodin element type is an internal element type.
	 * 
	 * @param elementType
	 *   the element type to test
	 * @return <code>true</code> iff it is an internal element type
	 */
	public boolean isInternalElementType(String elementType) {
		if (internalElementTypeIds== null) {
			computeInternalElementTypes();
		}
		return internalElementTypeIds.containsKey(elementType);
	}
	
	/**
	 * Returns the full description of the given internal element type.
	 * 
	 * @param elementType
	 *            the element type to retrieve
	 * @return the element type description or <code>null</code> if this
	 *         element type is unknown.
	 */
	public InternalElementTypeDescription getInternalElementTypeDescription(String elementType) {
		if (internalElementTypeIds== null) {
			computeInternalElementTypes();
		}
		return internalElementTypeIds.get(elementType);
	}
	
	/**
	 * Returns the Rodin element type of the given file or <code>null</code>
	 * if it is not a Rodin File.
	 * 
	 * @param file
	 *            the file to test
	 * @return the file element type associated to the file or <code>null</code>
	 *         if it is not a Rodin file
	 */
	public String getFileElementType(IFile file) {
		FileElementTypeDescription description = getFileElementTypeDescription(file);
		if (description == null) {
			return null;		// Not a Rodin file.
		}
		return description.getId();
	}

	/**
	 * Tells whether the given file name is valid for a Rodin File (that is
	 * corresponds to a declared file element type).
	 * 
	 * @param fileName
	 *            the file name to test
	 * @return <code>true</code> iff the name is valid
	 */
	public boolean isValidFileName(String fileName) {
		return getFileElementTypeDescription(fileName) != null;
	}
	
	/**
	 * Creates a new internal element handle.
	 * 
	 * @param type
	 *            the type of the element to create
	 * @param name
	 *            the name of the element to create. Must be <code>null</code>
	 *            for an unnamed element.
	 * @param parent
	 *            the new element's parent
	 * @return a handle on the internal element or <code>null</code> if the
	 *         element type is unknown
	 */
	public InternalElement createInternalElementHandle(String type, String name, IRodinElement parent) {
		InternalElementTypeDescription description = getInternalElementTypeDescription(type);
		if (description == null) {
			// TODO create a default node for unknown types
			// When the type is unknown, this means that the plugin that contributed it is no
			// longer around.  However, we should not erase all information stored by this plugin.
			// We should rather use a default implementation.
			return null;		// Not a valid element type
		}
		Constructor<? extends InternalElement> constructor = description.getConstructor();
		try {
			if (description.isNamed()) {
				return constructor.newInstance(name, parent);
			} else {
				return constructor.newInstance(type, parent);
			}
		} catch (Exception e) {
			Util.log(e, "Error when constructing instance of type " + type);
			return null;
		}
	}
	
}
