/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core;

import static org.rodinp.internal.core.util.Util.log;

import java.util.HashMap;

import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.internal.core.ElementType.DatabaseElementType;
import org.rodinp.internal.core.ElementType.FileElementType;
import org.rodinp.internal.core.ElementType.ProjectElementType;

/**
 * A registry class to store element types.
 * 
 * @author Thomas Muller
 */
public class ElementTypeRegistry {

	// The owner
	private final ElementTypeManager manager;
	// The mapping between element type ids and element types
	private final HashMap<String, ElementType<?>> registry = new HashMap<String, ElementType<?>>();

	private DatabaseElementType dbType;
	private ProjectElementType projectType;
	private FileElementType fileType;

	public ElementTypeRegistry(ElementTypeManager manager) {
		this.manager = manager;
	}

	public void registerCoreTypes() {
		if (dbType == null && projectType == null && fileType == null) {
			this.dbType = new ElementType.DatabaseElementType(manager);
			this.projectType = new ElementType.ProjectElementType(manager);
			this.fileType = new ElementType.FileElementType(manager);
		}
	}

	public void register(String id, ElementType<?> type) {
		final ElementType<?> oldType = registry.put(id, type);
		if (oldType != null) {
			registry.put(id, oldType);
			final String msg = "Attempt to create twice element type " + id;
			log(null, msg);
			throw new IllegalStateException();
		}
	}

	public IElementType<?> getElementType(String id) {
		return registry.get(id);
	}

	public ElementType<IRodinDB> getRodinDBType() {
		return dbType;
	}

	public ElementType<IRodinProject> getRodinProjectType() {
		return projectType;
	}

	public ElementType<IRodinFile> getRodinFileType() {
		return fileType;
	}

}
