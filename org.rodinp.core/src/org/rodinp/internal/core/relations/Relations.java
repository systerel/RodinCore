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
	 * Stores the mapping of parent and child items
	 * 
	 * @param parentElement
	 *            the parent element
	 * @param childElements
	 *            the children elements
	 */
	public void putAll(S parentElement, List<T> childElements) {
		final Set<T> children = getSetValue(childrenMap, parentElement);
		for (T child : childElements) {
			children.add(child);
			getSetValue(parentsMap, child).add(parentElement);
		}
	}

	private static <U, V> Set<V> getSetValue(Map<U, Set<V>> map, U key) {
		Set<V> result = map.get(key);
		if (result == null) {
			result = new LinkedHashSet<V>();
			map.put(key, result);
		}
		return result;
	}

	/** Returns all the parents of the given child element. */
	protected Set<S> getParentsOf(T childElement) {
		final Set<S> parents = parentsMap.get(childElement);
		return parents == null ? Collections.<S> emptySet() : parents;
	}

	/** Returns all the children of the given parent element. */
	protected Set<T> getChildrenOf(S parentElement) {
		final Set<T> children = childrenMap.get(parentElement);
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
	public static class AttributeTypesRelations extends
			Relations<IInternalElementType<?>, IAttributeType> {

		/** Returns all attribute types of the given element type. */
		public IAttributeType[] getAttributes(IInternalElementType<?> type) {
			return getAttributeArray(getChildrenOf(type));
		}

		private IAttributeType[] getAttributeArray(Set<IAttributeType> set) {
			return set.toArray(new IAttributeType[set.size()]);
		}

	}

	// public for testing purpose only
	public static class ElementTypesRelations extends
			Relations<IInternalElementType<?>, IInternalElementType<?>> {

		/** Returns all parent element types of the given element type. */
		public IInternalElementType<?>[] getParentTypes(
				IInternalElementType<?> type) {
			return getElementArray(parentsMap.get(type));
		}

		/** Returns all child element types of the given element type. */
		public IInternalElementType<?>[] getChildTypes(
				IInternalElementType<?> type) {
			return getElementArray(childrenMap.get(type));
		}

		private IInternalElementType<?>[] getElementArray(
				Set<IInternalElementType<?>> set) {
			if (set == null) {
				return new IInternalElementType<?>[0];
			}
			return set.toArray(new IInternalElementType<?>[set.size()]);
		}

	}

}
