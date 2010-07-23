/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
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

import java.util.HashMap;
import java.util.List;

import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.internal.core.util.Messages;
import org.rodinp.internal.core.util.Util;

/**
 * Base class for all element types (predefined or contributed).
 * 
 * @author Laurent Voisin
 */
public abstract class ElementType<T extends IRodinElement> implements IElementType<T> {

	public static final class DatabaseElementType extends ElementType<IRodinDB> {
		protected DatabaseElementType() {
			super(RodinCore.PLUGIN_ID + ".database", Messages.type_database);
		}

		@Override
		public IRodinDB[] getArray(int length) {
			return new IRodinDB[length];
		}
	}

	public static final class ProjectElementType extends ElementType<IRodinProject> {
		protected ProjectElementType() {
			super(RodinCore.PLUGIN_ID + ".project", Messages.type_project);
		}

		@Override
		public RodinProject[] getArray(int length) {
			return new RodinProject[length];
		}
	}

	public static final class FileElementType extends ElementType<IRodinFile> {
		protected FileElementType() {
			super(RodinCore.PLUGIN_ID + ".file", Messages.type_file);
		}

		@Override
		public RodinFile[] getArray(int length) {
			return new RodinFile[length];
		}
	}

	private static final HashMap<String, ElementType<?>> registry =
		new HashMap<String, ElementType<?>>();
	
	public static final DatabaseElementType DATABASE_ELEMENT_TYPE = 
		new DatabaseElementType();
	
	public static final ProjectElementType PROJECT_ELEMENT_TYPE = 
		new ProjectElementType();

	public static final FileElementType FILE_ELEMENT_TYPE = 
		new FileElementType();

	private static void register(String id, ElementType<?> type) {
		final ElementType<?> oldType = registry.put(id, type);
		if (oldType != null) {
			registry.put(id, oldType);
			String msg = "Attempt to create twice element type " + id;
			Util.log(null, msg);
			throw new IllegalStateException();
		}
	}
	
	public static IElementType<?> getElementType(String id) {
		return registry.get(id);
	}
	
	// Unique identifier of this element type
	protected final String id;
	
	// Human-readable name of this element type
	protected final String name;

	public ElementType(final String id, final String name) {
		this.id = id;
		this.name = name;
		register(id, this);
	}

	@Override
	public final String getId() {
		return id;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final String toString() {
		return id;
	}

	public abstract T[] getArray(int length);

	public final T[] toArray(List<T> list) {
		return list.toArray(getArray(list.size()));
	}

}
