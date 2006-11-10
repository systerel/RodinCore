/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.internal.core;

import java.util.HashMap;

import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.internal.core.util.Messages;

/**
 * Base class for all element types (predefined or contributed).
 * 
 * @author Laurent Voisin
 */
public abstract class ElementType implements IElementType {

	public static final class DatabaseElementType extends ElementType {
		protected DatabaseElementType() {
			super(RodinCore.PLUGIN_ID + ".database", Messages.type_database);
		}

		@Override
		public RodinDB[] getArray(int length) {
			return new RodinDB[length];
		}
	}

	public static final class ProjectElementType extends ElementType {
		protected ProjectElementType() {
			super(RodinCore.PLUGIN_ID + ".project", Messages.type_project);
		}

		@Override
		public RodinProject[] getArray(int length) {
			return new RodinProject[length];
		}
	}

	private static final HashMap<String, ElementType> registry = 
		new HashMap<String, ElementType>();
	
	public static final DatabaseElementType DATABASE_ELEMENT_TYPE = 
		new DatabaseElementType();
	
	public static final ProjectElementType PROJECT_ELEMENT_TYPE = 
		new ProjectElementType();

	private static void register(String id, ElementType type) {
		final ElementType oldType = registry.put(id, type);
		if (oldType != null) {
			registry.put(id, oldType);
			throw new IllegalStateException(
					"Attempt to create twice element type " + id);
		}
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

	public final String getId() {
		return id;
	}

	public final String getName() {
		return name;
	}

	@Override
	public final String toString() {
		return id;
	}

	public abstract IRodinElement[] getArray(int length);
	
}
