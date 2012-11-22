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

import static org.rodinp.internal.core.ElementTypeManager.debug;
import static org.rodinp.internal.core.ElementTypeManager.getSortedIds;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IInternalElement;

/**
 * Stores a map between ids and internal element types, as defined by the
 * extension point <code>internalElementTypes</code>.
 * <p>
 * Instances of this class are immutable and therefore thread-safe.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class InternalElementTypes extends
		ContributedItemTypes<InternalElementType<?>> {

	// Local id of the internalElementTypes extension point of this plug-in
	protected static final String INTERNAL_ELEMENT_TYPES_ID = "internalElementTypes";

	public InternalElementTypes(ElementTypeManager elementTypeManager) {
		super(INTERNAL_ELEMENT_TYPES_ID, elementTypeManager);
	}

	@Override
	protected InternalElementType<?> makeType(IConfigurationElement element) {
		return new InternalElementType<IInternalElement>(element,
				elementTypeManager);
	}

	@Override
	protected void showMap() {
		debug("---------------------------------------------------");
		debug("Internal element types known to the Rodin database:");
		for (final String id : getSortedIds(map)) {
			final InternalElementType<?> type = get(id);
			debug("  " + type.getId());
			debug("    name: " + type.getName());
			debug("    class: " + type.getClassName());
		}
		debug("---------------------------------------------------");
	}

}