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
package org.rodinp.core.tests.relations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.internal.core.InternalElementTypes;
import org.rodinp.internal.core.relations.InternalElementTypeExt;
import org.rodinp.internal.core.relations.ItemRelation;
import org.rodinp.internal.core.relations.RelationsComputer;
import org.rodinp.internal.core.relations.api.IInternalElementType2;

/**
 * A class registering dynamic IInternalElementTypes for testing.
 * 
 * @author Thomas Muller
 */
public class InternalTestTypes extends InternalElementTypes {

	private static final IConfigurationElement[] NONE = new IConfigurationElement[0];

	private static final Map<String, InternalElementTypeExt<?>> map = new HashMap<String, InternalElementTypeExt<?>>();

	private static final String[] TYPE_IDS = new String[] { //
	"leaf", //
			"p1", "c1", //
			"p2", "c21", "c22", //
			"p21", "p22", "c2", //
			"cy1", //
			"cy21", "cy22", //
			"cy31", "cy32", "cy33" //
	};

	@Override
	protected IConfigurationElement[] readExtensions() {
		final List<IConfigurationElement> elements = new ArrayList<IConfigurationElement>();
		for (final String id : TYPE_IDS) {
			final String[] attributes = new String[2];
			attributes[0] = "id='" + id + "'";
			attributes[1] = "name='" + id + "_Element'";
			final FakeConfigurationElement e = new FakeConfigurationElement(
					INTERNAL_ELEMENT_TYPES_ID, attributes, NONE);
			elements.add(e);
		}
		return elements.toArray(new IConfigurationElement[elements.size()]);
	}

	@Override
	protected void computeInternalElementTypes() {
		final IConfigurationElement[] elements = readExtensions();
		for (final IConfigurationElement element : elements) {
			final InternalElementTypeExt<?> type = new InternalElementTypeExt<IInternalElement>(
					element);
			map.put(type.getId(), type);
		}

	}

	public InternalElementTypeExt<?> getElement(String id) {
		return map.get(id);
	}

	protected void computeRelations(List<ItemRelation> itemRelations,
			IInternalElementType2<?>[] types) {
		final RelationsComputer c = new RelationsComputer(this);
		c.computeRelations(itemRelations);
		for (IInternalElementType2<?> type : types) {
			c.setRelations(type);
		}
	}

}