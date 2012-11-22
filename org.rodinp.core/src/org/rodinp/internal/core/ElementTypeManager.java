/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - generic attribute manipulation
 *******************************************************************************/
package org.rodinp.internal.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

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
	private static ElementTypeManager INSTANCE;
	
	//The registry of element types
	private final ElementTypeRegistry typeRegistry;

	//Associative map of file associations
	private FileAssociations fileAssociations;
	
	//Associative map of internal element types
	private InternalElementTypes internalElementTypes;
	
	//Associative map of internal attribute types
	private AttributeTypes attributeTypes;

	/**
	 * Returns the singleton ElementTypeManager
	 */
	public final static ElementTypeManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ElementTypeManager();
			INSTANCE.initialize();		
		}
		return INSTANCE;
	}

	/**
	 * Returns the singleton ElementTypeManager
	 */
	public final static ElementTypeManager getInstanceForTests() {
		if (INSTANCE == null) {
			INSTANCE = new ElementTypeManager();
		}
		return INSTANCE;
	}

	private ElementTypeManager() {
		this.typeRegistry = new ElementTypeRegistry(this);
		typeRegistry.registerCoreTypes();
	}
	
	private void initialize() {
		if (fileAssociations == null && internalElementTypes == null
				&& attributeTypes == null) {
			this.fileAssociations = new FileAssociations();
			this.internalElementTypes = new InternalElementTypes(this);
			this.attributeTypes = new AttributeTypes(this);
		}
	}

	private FileAssociation getFileAssociation(IContentType contentType) {
		return fileAssociations.getFromContentId(contentType.getId());
	}
	
	public FileAssociation getFileAssociationFor(String fileName) {
		IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
		IContentType contentType = contentTypeManager.findContentTypeFor(fileName);
		if (contentType == null) {
			return null;		// Not a Rodin file.
		}
		return getFileAssociation(contentType);
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
		return internalElementTypes.get(id);
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
		return typeRegistry.getElementType(id);
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
	public AttributeType<?> getAttributeType(String name) {
		return attributeTypes.get(name);
	}

	/* package */static void debug(String str) {
		System.out.println(str);
	}

	/* package */static <V> String[] getSortedIds(HashMap<String, V> map) {
		Set<String> idSet = map.keySet();
		String[] ids = idSet.toArray(new String[idSet.size()]);
		Arrays.sort(ids);
		return ids;
	}

	public void register(String id, ElementType<?> type) {
		typeRegistry.register(id, type);
	}

	public IElementType<IRodinDB> getDatabaseElementType() {
		return typeRegistry.getRodinDBType();
	}

	public IElementType<IRodinProject> getProjectElementType() {
		return typeRegistry.getRodinProjectType();
	}
		
	public IElementType<IRodinFile> getFileElementType() {
		return typeRegistry.getRodinFileType();
	}
	
}