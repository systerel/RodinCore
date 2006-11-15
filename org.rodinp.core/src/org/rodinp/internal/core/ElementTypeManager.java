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
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IInternalElementType;
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
	 * 
	 * @deprecated Use {@link #getInstance()} instead.
	 */
	@Deprecated
	public final static ElementTypeManager getElementTypeManager() {
		return MANAGER;
	}

	/**
	 * Returns the singleton ElementTypeManager
	 */
	public final static ElementTypeManager getInstance() {
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
		FileElementType type = getFileElementTypeFromFileName(filename);
		if (type == null) {
			return null;		// Not a Rodin file.
		}
		Constructor<? extends RodinFile> constructor = type.getConstructor();
		IFile file = project.getProject().getFile(filename);
		try {
			return constructor.newInstance(file, project);
		} catch (Exception e) {
			// Some error occurred while reflecting.
			e.printStackTrace();
			return null;
		}
	}
	
	private FileElementType getFileElementType(IContentType contentType) {
		if (fileContentTypes == null) {
			computeFileElementTypes();
		}
		return fileContentTypes.get(contentType.getId());
	}
	
	private FileElementType getFileElementTypeFromFileName(String fileName) {
		IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
		IContentType contentType = contentTypeManager.findContentTypeFor(fileName);
		if (contentType == null) {
			return null;		// Not a Rodin file.
		}
		return getFileElementType(contentType);
	}

	// Local id of the fileElementTypes extension point of this plugin
	private static final String FILE_ELEMENT_TYPES_ID = "fileElementTypes";
	
	// Access to file element types using their content type name
	private HashMap<String, FileElementType> fileContentTypes;

	// Access to file element types using their unique id
	private HashMap<String, FileElementType> fileElementTypeIds;

	private void computeFileElementTypes() {
		fileElementTypeIds = new HashMap<String, FileElementType>();
		fileContentTypes = new HashMap<String, FileElementType>();
		fileContentTypes = new HashMap<String, FileElementType>();
		
		// Read the extension point extensions.
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = 
			registry.getConfigurationElementsFor(RodinCore.PLUGIN_ID, FILE_ELEMENT_TYPES_ID);
		for (IConfigurationElement element: elements) {
			FileElementType type = new FileElementType(element);
			fileElementTypeIds.put(type.getId(), type);
			fileContentTypes.put(type.getContentTypeId(), type);
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
	
	// Access to internal element types using their unique id
	private HashMap<String, InternalElementType> internalElementTypeIds;

	private void computeInternalElementTypes() {
		internalElementTypeIds = new HashMap<String, InternalElementType>();
		
		// Read the extension point extensions.
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = 
			registry.getConfigurationElementsFor(RodinCore.PLUGIN_ID, INTERNAL_ELEMENT_TYPES_ID);
		for (IConfigurationElement element: elements) {
			InternalElementType type = new InternalElementType(element);
			internalElementTypeIds.put(type.getId(), type);
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
	 * Tells whether the given Rodin element type is a named internal element type.
	 * 
	 * @param elementType
	 *   the element type to test
	 * @return <code>true</code> iff it is a named internal element type
	 */
	public boolean isNamedInternalElementType(String elementType) {
		InternalElementType type = getInternalElementType(elementType);
		return type != null && type.isNamed(); 
	}
	
	/**
	 * Returns the internal element type with the given id.
	 * 
	 * @param id
	 *            the id of the element type to retrieve
	 * @return the element type or <code>null</code> if this
	 *         element type id is unknown.
	 */
	public InternalElementType getInternalElementType(String id) {
		if (internalElementTypeIds == null) {
			computeInternalElementTypes();
		}
		return internalElementTypeIds.get(id);
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
	public IFileElementType getFileElementType(IFile file) {
		try {
			IContentDescription contentDescription = file.getContentDescription();
			if (contentDescription == null) return null; // Unknown kind of file.
			IContentType contentType = contentDescription.getContentType();
			if (contentType == null) return null; // Unknown kind of file.
			return getFileElementType(contentType);
		} catch (CoreException e) {
			// Ignore
		}
		// Maybe the file doesn't exist, try with its filename
		return getFileElementTypeFromFileName(file.getName());
	}

	public IFileElementType getFileElementType(String id) {
		if (fileElementTypeIds == null) {
			computeFileElementTypes();
		}
		return fileElementTypeIds.get(id);
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
		return getFileElementTypeFromFileName(fileName) != null;
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
	 * @deprecated Reimplement directly in the element type.
	 */
	@Deprecated
	public InternalElement createInternalElementHandle(
			IInternalElementType type, String name, IRodinElement parent) {

		assert name != null;
		if (type == null) {
			return null;		// Not a valid element type
		}
		InternalElementType lType = (InternalElementType) type;
		Constructor<? extends InternalElement> constructor = lType.getConstructor();
		try {
			if (lType.isNamed()) {
				return constructor.newInstance(name, parent);
			} else {
				return constructor.newInstance(parent);
			}
		} catch (Exception e) {
			Util.log(e, "Error when constructing instance of type " + type);
			return null;
		}
	}
	
	// Local id of the fileElementTypes extension point of this plugin
	private static final String ATTRIBUTE_TYPES_ID = "attributeTypes";
	
	// Access to attribute type descriptions using their unique id
	private HashMap<String, AttributeType> attributeTypeIds;

	private void computeAttributeTypes() {
		attributeTypeIds = new HashMap<String, AttributeType>();
		
		// Read the extension point extensions.
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = 
			registry.getConfigurationElementsFor(RodinCore.PLUGIN_ID, ATTRIBUTE_TYPES_ID);
		for (IConfigurationElement element: elements) {
			AttributeType description =
				AttributeType.valueOf(element);
			if (description != null) {
				attributeTypeIds.put(description.getId(), description);
			}
		}
	}

	/**
	 * Returns the attribute type description corresponding to the given name or
	 * <code>null</code> if it is not a valid attribute name.
	 * 
	 * @param name
	 *            the attribute name
	 * @return the attribute type description associated to the given name or
	 *         <code>null</code> if it is not a valid attribute name
	 */
	public AttributeType getAttributeType(String name) {
		if (attributeTypeIds == null) {
			computeAttributeTypes();
		}
		return attributeTypeIds.get(name);
	}

	/**
	 * Tells whether the given attribute name is valid (that is corresponds to a
	 * declared attribute type).
	 * 
	 * @param attributeName
	 *            the attribute name to test
	 * @return <code>true</code> iff the name is valid
	 */
	public boolean isValidAttributeName(String attributeName) {
		return getAttributeType(attributeName) != null;
	}

}
