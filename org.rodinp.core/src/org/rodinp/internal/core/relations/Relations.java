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
package org.rodinp.internal.core.relations;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElementType;
import org.rodinp.internal.core.ElementTypeManager;
import org.rodinp.internal.core.InternalElementTypes;

/**
 * Generic class storing parent-children and child-parents relations where all
 * children bare the same type.
 * 
 * @author Thomas Muller
 */
public abstract class Relations<S, T> {
	
	final Map<S, Set<T>> childrenMap = new HashMap<S, Set<T>>();
	final Map<T, Set<S>> parentsMap = new HashMap<T, Set<S>>();

	/**
	 * Stores the mapping of parent and child items from the given ids.
	 * 
	 * @param parentId
	 *            the parent element id
	 * @param childIds
	 *            the children ids
	 */
	public void putAll(String parentId, List<String> childIds) {
		final S parentType = getParentInstanceFromId(parentId);
		Set<T> children = childrenMap.get(parentType);
		if (children == null) {
			children = new LinkedHashSet<T>();
			childrenMap.put(parentType, children);
		}
		for (String childId : childIds) {
			final T childType = getChildInstanceFromId(childId);
			// adding a reference to the child type
			children.add(childType);
			Set<S> parentTypes = parentsMap.get(childType);
			if (parentTypes == null) {
				parentTypes = new LinkedHashSet<S>();
				parentsMap.put(childType, parentTypes);
			}
			// adding a reference to the parent type
			parentTypes.add(parentType);
		}
	}

	/** Returns the parent item corresponding to the given id */
	protected abstract S getParentInstanceFromId(String itemId);

	/** Returns the child item corresponding to the given id */
	protected abstract T getChildInstanceFromId(String itemId);

	/** Returns all the parents of the given child element. */
	protected Set<S> getParentsOf(T child) {
		final Set<S> parents = parentsMap.get(child);
		return parents == null ? Collections.<S> emptySet() : parents;
	}

	/** Returns all the children of the given parent element. */
	protected Set<T> getChildrenOf(S parentType) {
		final Set<T> children = childrenMap.get(parentType);
		return children == null ? Collections.<T> emptySet() : children;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((childrenMap == null) ? 0 : childrenMap.hashCode());
		result = prime * result
				+ ((parentsMap == null) ? 0 : parentsMap.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Relations other = (Relations) obj;
		if (childrenMap == null) {
			if (other.childrenMap != null)
				return false;
		} else if (!childrenMap.equals(other.childrenMap))
			return false;
		if (parentsMap == null) {
			if (other.parentsMap != null)
				return false;
		} else if (!parentsMap.equals(other.parentsMap))
			return false;
		return true;
	}

	// public for testing purpose only
	public static class AttributeRelations extends
			Relations<IInternalElementType<?>, IAttributeType> {

		private final InternalElementTypes types;

		public AttributeRelations(InternalElementTypes types) {
			this.types = types;
		}

		@Override
		protected IInternalElementType<?> getParentInstanceFromId(
				String parentId) {
			return types.getElement(parentId);
		}

		@Override
		protected IAttributeType getChildInstanceFromId(String attributeId) {
			final ElementTypeManager mng = ElementTypeManager.getInstance();
			return mng.getAttributeType(attributeId);
		}

		/** Returns all the parents types of the given attribute. */
		public IAttributeType[] getAttributes(IInternalElementType<?> element) {
			final Set<IAttributeType> attributeTypes = getChildrenOf(element);
			return attributeTypes.toArray( //
					new IAttributeType[attributeTypes.size()]);
		}

	}

	// public for testing purpose only
	public static class ElementRelations extends
			Relations<IInternalElementType<?>, IInternalElementType<?>> {

		private final InternalElementTypes types;

		public ElementRelations(InternalElementTypes types) {
			this.types = types;
		}

		@Override
		protected IInternalElementType<?> getParentInstanceFromId(
				String parentId) {
			return types.getElement(parentId);
		}

		@Override
		protected IInternalElementType<?> getChildInstanceFromId(String childId) {
			return types.getElement(childId);
		}

		/** Returns all the parents types of the given child type. */
		public IInternalElementType<?>[] getParentTypes(
				IInternalElementType<?> child) {
			final Set<IInternalElementType<?>> parentsTypes = getParentsOf(child);
			return parentsTypes.toArray( //
					new IInternalElementType<?>[parentsTypes.size()]);
		}

		/** Returns all the child types of the given parent type. */
		public IInternalElementType<?>[] getChildTypes(
				IInternalElementType<?> parent) {
			final Set<IInternalElementType<?>> childTypes = getChildrenOf(parent);
			return childTypes.toArray( //
					new IInternalElementType<?>[childTypes.size()]);
		}

	}

}
