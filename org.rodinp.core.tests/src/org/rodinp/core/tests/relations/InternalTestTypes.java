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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.internal.core.InternalElementTypes;
import org.rodinp.internal.core.relations.InternalElementType2;
import org.rodinp.internal.core.relations.ItemRelation;
import org.rodinp.internal.core.relations.RelationsComputer;
import org.rodinp.internal.core.relations.api.IInternalElementType2;

/**
 * A class registering dynamic IInternalElementTypes for testing. This is done
 * by sub-classing the registry {@link InternalElementTypes} and feeding it with
 * a hard-coded list of element types.
 * 
 * @author Thomas Muller
 */
public class InternalTestTypes extends InternalElementTypes {

	private static final IConfigurationElement[] NONE = new IConfigurationElement[0];

	private static final Map<String, InternalElementType2<?>> map = new HashMap<String, InternalElementType2<?>>();

	private static final String[] TYPE_IDS = new String[] { //
	"leaf", //
			"p1", "c1", //
			"p2", "c21", "c22", //
			"p21", "p22", "c2", //
			"cy1", //
			"cy21", "cy22", //
			"cy31", "cy32", "cy33", //
			"p", "child", "attr" //
	};

	@Override
	protected IConfigurationElement[] readExtensions() {
		final int length = TYPE_IDS.length;
		final IConfigurationElement[] result = new IConfigurationElement[length];
		for (int i = 0; i < length; i++) {
			final String id = TYPE_IDS[i];
			final String[] attributes = new String[] { "id='" + id + "'",
					"name='" + id + " Element'", };
			result[i] = new FakeConfigurationElement(INTERNAL_ELEMENT_TYPES_ID,
					attributes, NONE);
		}
		return result;
	}

	@Override
	protected void computeInternalElementTypes() {
		final IConfigurationElement[] elements = readExtensions();
		for (final IConfigurationElement element : elements) {
			final InternalElementType2<?> type = new InternalElementType2<IInternalElement>(
					element);
			map.put(type.getId(), type);
		}

	}

	public InternalElementType2<?> getElement(String id) {
		return map.get(id);
	}

	protected void computeRelations(List<ItemRelation> itemRelations,
			IInternalElementType2<?>[] types) {
		final RelationsComputer c = new RelationsComputer();
		c.computeRelations(itemRelations);
		for (IInternalElementType2<?> type : types) {
			c.setRelations(type);
		}
	}

}