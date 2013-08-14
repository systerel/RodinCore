/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.relations;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rodinp.internal.core.AttributeType;
import org.rodinp.internal.core.InternalElementType;

/**
 * Generic class storing parent-children and child-parents relations where all
 * children bare the same type.
 * 
 * @author Thomas Muller
 */
public abstract class Relations<S extends Comparable<S>, T extends Comparable<T>> {

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
			result = new HashSet<V>();
			map.put(key, result);
		}
		return result;
	}

	/** Returns all the parents of the given child element. */
	protected List<S> getParentsOf(T childElement) {
		return asSortedList(parentsMap.get(childElement));
	}

	/** Returns all the children of the given parent element. */
	protected List<T> getChildrenOf(S parentElement) {
		return asSortedList(childrenMap.get(parentElement));
	}

	// Finalize the list and makes it as small as possible
	protected static <U extends Comparable<U>> List<U> asSortedList(Set<U> set) {
		if (set == null) {
			return emptyList();
		}
		final ArrayList<U> result = new ArrayList<U>(set);
		Collections.sort(result);
		result.trimToSize();
		return result;
	}

	// public for testing purpose only
	public static class AttributeTypeRelations extends
			Relations<InternalElementType<?>, AttributeType<?>> {

		/** Returns all element types that can carry the given element type. */
		public List<InternalElementType<?>> getElementTypes(
				AttributeType<?> type) {
			return getParentsOf(type);
		}

		/** Returns all attribute types of the given element type. */
		public List<AttributeType<?>> getAttributeTypes(
				InternalElementType<?> type) {
			return getChildrenOf(type);
		}

	}

	// public for testing purpose only
	public static class ElementTypeRelations extends
			Relations<InternalElementType<?>, InternalElementType<?>> {

		/** Returns all parent element types of the given element type. */
		public List<InternalElementType<?>> getParentTypes(
				InternalElementType<?> type) {
			return getParentsOf(type);
		}

		/** Returns all child element types of the given element type. */
		public List<InternalElementType<?>> getChildTypes(
				InternalElementType<?> type) {
			return getChildrenOf(type);
		}

	}

}
