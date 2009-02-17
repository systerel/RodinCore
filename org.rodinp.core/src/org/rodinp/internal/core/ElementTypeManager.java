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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;

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
	
	// Debug flag set from tracing options 
	public static boolean VERBOSE = false;

	/**
	 * The singleton manager
	 */
	private static final ElementTypeManager MANAGER = new ElementTypeManager();
	
	/**
	 * Returns the singleton ElementTypeManager
	 */
	public final static ElementTypeManager getInstance() {
		return MANAGER;
	}

	private ElementTypeManager() {
		// singleton: prevent others from creating a new instance
	}

	private FileAssociation getFileAssociation(IContentType contentType) {
		if (fileContentTypes == null) {
			computeFileAssociations();
		}
		return fileContentTypes.get(contentType.getId());
	}
	
	public FileAssociation getFileAssociationFor(String fileName) {
		IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
		IContentType contentType = contentTypeManager.findContentTypeFor(fileName);
		if (contentType == null) {
			return null;		// Not a Rodin file.
		}
		return getFileAssociation(contentType);
	}

	// Local id of the fileAssociations extension point of this plugin
	private static final String FILE_ASSOCIATIONS_ID = "fileAssociations";
	
	// Access to file association using their content type name
	private HashMap<String, FileAssociation> fileContentTypes;

	// Access to file association using their root element id
	private HashMap<String, FileAssociation> fileRootElementTypeIds;

	private void computeFileAssociations() {
		fileRootElementTypeIds = new HashMap<String, FileAssociation>();
		fileContentTypes = new HashMap<String, FileAssociation>();
		
		// Read the extension point extensions.
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = 
			registry.getConfigurationElementsFor(RodinCore.PLUGIN_ID, FILE_ASSOCIATIONS_ID);
		for (IConfigurationElement element: elements) {
			FileAssociation type = new FileAssociation(element);
			fileRootElementTypeIds.put(type.getRootElementTypeId(), type);
			fileContentTypes.put(type.getContentTypeId(), type);
		}

		if (VERBOSE) {
			System.out.println("-----------------------------------------------");
			System.out.println("File association known to the Rodin database:");
			for (String id: getSortedIds(fileRootElementTypeIds)) {
				FileAssociation type = fileRootElementTypeIds.get(id);
				System.out.println("    root-element-type: " + id);
				System.out.println("    content-type: " + type.getContentTypeId());
			}
			System.out.println("-----------------------------------------------");
		}
	}

	// Local id of the internalElementTypes extension point of this plugin
	private static final String INTERNAL_ELEMENT_TYPES_ID = "internalElementTypes";
	
	// Access to internal element types using their unique id
	private HashMap<String, InternalElementType<? extends IInternalElement>>
		internalElementTypeIds;

	private void computeInternalElementTypes() {
		internalElementTypeIds =
			new HashMap<String, InternalElementType<? extends IInternalElement>>();
		
		// Read the extension point extensions.
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = 
			registry.getConfigurationElementsFor(RodinCore.PLUGIN_ID, INTERNAL_ELEMENT_TYPES_ID);
		for (IConfigurationElement element: elements) {
			InternalElementType<?> type =
				new InternalElementType<IInternalElement>(element);
			internalElementTypeIds.put(type.getId(), type);
		}

		if (VERBOSE) {
			System.out.println("---------------------------------------------------");
			System.out.println("Internal element types known to the Rodin database:");
			for (String id: getSortedIds(internalElementTypeIds)) {
				InternalElementType<?> type = internalElementTypeIds.get(id);
				System.out.println("  " + type.getId());
				System.out.println("    name: " + type.getName());
				System.out.println("    class: " + type.getClassName());
			}
			System.out.println("---------------------------------------------------");
		}
	}

	/**
	 * Returns the internal element type with the given id.
	 * 
	 * @param id
	 *            the id of the element type to retrieve
	 * @return the element type or <code>null</code> if this
	 *         element type id is unknown.
	 */
	public InternalElementType<? extends IInternalElement> getInternalElementType(
			String id) {

		if (internalElementTypeIds == null) {
			computeInternalElementTypes();
		}
		return internalElementTypeIds.get(id);
	}
	
	/**
	 * Returns the file association of the given file or <code>null</code> if it
	 * is not a Rodin File.
	 * 
	 * @param file
	 *            the file to test
	 * @return the file association or <code>null</code> if it is not a Rodin
	 *         file
	 */
	public FileAssociation getFileAssociation(IFile file) {
		try {
			IContentDescription contentDescription = file.getContentDescription();
			if (contentDescription == null) return null; // Unknown kind of file.
			IContentType contentType = contentDescription.getContentType();
			if (contentType == null) return null; // Unknown kind of file.
			return getFileAssociation(contentType);
		} catch (CoreException e) {
			// Ignore
		}
		// Maybe the file doesn't exist, try with its filename
		return getFileAssociationFor(file.getName());
	}

	public FileAssociation getFileAssociation(
			String id) {
		if (fileRootElementTypeIds == null) {
			computeFileAssociations();
		}
		return fileRootElementTypeIds.get(id);
	}
	
	/**
	 * Tells whether the given file name is valid for a Rodin File (that is
	 * corresponds to a declared file association).
	 * 
	 * @param fileName
	 *            the file name to test
	 * @return <code>true</code> iff the name is valid
	 */
	public boolean isValidFileName(String fileName) {
		return getFileAssociationFor(fileName) != null;
	}
	
	public IElementType<? extends IRodinElement> getElementType(String id) {
		if (internalElementTypeIds == null) {
			computeInternalElementTypes();
		}
		return ElementType.getElementType(id);
	}

	// Local id of the attributeTypes extension point of this plugin
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

		if (VERBOSE) {
			System.out.println("--------------------------------------------");
			System.out.println("Attribute types known to the Rodin database:");
			for (String id: getSortedIds(attributeTypeIds)) {
				AttributeType type = attributeTypeIds.get(id);
				System.out.println("  " + type.getId());
				System.out.println("    name:  " + type.getName());
				System.out.println("    kind:  " + type.getKind());
				System.out.println("    class: " + type.getClass());
			}
			System.out.println("--------------------------------------------");
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

	private <V> String[] getSortedIds(HashMap<String, V> map) {
		Set<String> idSet = map.keySet();
		String[] ids = idSet.toArray(new String[idSet.size()]);
		Arrays.sort(ids);
		return ids;
	}
	
}
