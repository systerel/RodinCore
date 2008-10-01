/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.index.tables;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.rodinp.core.IInternalElement;
import org.rodinp.internal.core.index.Descriptor;

public final class RodinIndex {

	private Map<IInternalElement, Descriptor> map;

	public RodinIndex() {
		map = new HashMap<IInternalElement, Descriptor>();
	}

	/**
	 * Gets the Descriptor corresponding to the given element. Returns
	 * <code>null</code> if such a Descriptor does not exist.
	 * 
	 * @param element
	 * @return the Descriptor of the given element, or <code>null</code> if it
	 *         does not exist.
	 * @see #makeDescriptor(IInternalElement, String)
	 */
	public Descriptor getDescriptor(IInternalElement element) {
		return map.get(element);
	}

	public Descriptor[] getDescriptors() {
		final Collection<Descriptor> descriptors = map.values();
		return descriptors.toArray(new Descriptor[descriptors.size()]);
	}

	/**
	 * Creates a Descriptor for the given element, with the given name. Throws
	 * {@link IllegalArgumentException} if a Descriptor already exists for the
	 * element.
	 * 
	 * @param element
	 *            the element for which to make a Descriptor.
	 * @param name
	 *            the public name (user-known) of the element.
	 * @return the newly created Descriptor.
	 * @throws IllegalArgumentException
	 *             if the Descriptor already exists.
	 * @see #getDescriptor(IInternalElement)
	 */
	public Descriptor makeDescriptor(IInternalElement element, String name) {
		if (map.containsKey(element)) {
			throw new IllegalArgumentException(
					"Descriptor for already exists for element: "
							+ element.getElementName());
		}
		final Descriptor descriptor = new Descriptor(element, name);
		map.put(element, descriptor);

		return descriptor;
	}

	public void removeDescriptor(IInternalElement element) {
		map.remove(element);
	}

	public void clear() {
		map.clear();
	}

	// DEBUG
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("index\n");
		for (Object o : map.keySet()) {
			sb.append(map.get(o).toString() + "\n");
		}
		return sb.toString();
	}

}
