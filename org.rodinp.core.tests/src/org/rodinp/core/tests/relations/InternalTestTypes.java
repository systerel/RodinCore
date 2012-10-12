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

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.internal.core.InternalElementType;
import org.rodinp.internal.core.InternalElementTypes;
import org.rodinp.internal.core.relations.ItemRelation;
import org.rodinp.internal.core.relations.RelationsComputer;
import org.rodinp.internal.core.relations.tomerge.InternalElementType2;

/**
 * A class registering dynamic IInternalElementTypes for testing. This is done
 * by sub-classing the registry {@link InternalElementTypes} and feeding it with
 * a hard-coded list of element types.
 * 
 * @author Thomas Muller
 */
public class InternalTestTypes extends InternalElementTypes {

	private static final IConfigurationElement[] NONE = new IConfigurationElement[0];

	private static final String[] TYPE_IDS = new String[] { //
	"leaf", //
			"p1", "c1", //
			"p2", "c21", "c22", //
			"p21", "p22", "c2", //
			"cy1", //
			"cy21", "cy22", //
			"cy31", "cy32", "cy33", //
			"p", "child", //
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

	protected void computeRelations(List<ItemRelation> itemRelations,
			IInternalElementType<?>[] types) {
		final RelationsComputer c = new RelationsComputer();
		c.computeRelations(itemRelations);
		for (IInternalElementType<?> type : types) {
			c.setRelations(type);
		}
	}

	@Override
	protected InternalElementType<?> makeType(IConfigurationElement element) {
		return new InternalElementType2<IInternalElement>(element);
	}

}