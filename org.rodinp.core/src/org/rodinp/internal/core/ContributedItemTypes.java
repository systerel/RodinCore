/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - code extracted from class ElementTypeManager
 *******************************************************************************/
package org.rodinp.internal.core;

import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.rodinp.core.RodinCore;

/**
 * Stores a map between ids and some Rodin item types contributed through an
 * extension point.
 * <p>
 * Instances of this class are immutable and therefore thread-safe.
 * </p>
 * 
 * @author Laurent Voisin
 */
public abstract class ContributedItemTypes<T extends IContributedItemType> {

	private final String extensionPointName;
	protected final HashMap<String, T> map = new HashMap<String, T>();

	public ContributedItemTypes(String extensionPointName) {
		this.extensionPointName = extensionPointName;
		computeTypes();
	}

	public T get(String id) {
		return map.get(id);
	}

	protected void computeTypes() {
		final IConfigurationElement[] elements = readExtensions();
		for (final IConfigurationElement element : elements) {
			final T type = makeType(element);
			map.put(type.getId(), type);
		}
		if (ElementTypeManager.VERBOSE) {
			showMap();
		}
	}

	protected IConfigurationElement[] readExtensions() {
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		return registry.getConfigurationElementsFor(RodinCore.PLUGIN_ID,
				extensionPointName);
	}

	protected abstract T makeType(IConfigurationElement element);

	protected abstract void showMap();

}