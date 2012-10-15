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

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IAttributeType;
import org.rodinp.internal.core.AttributeTypes;
import org.rodinp.internal.core.relations.RelationsComputer;

/**
 * A class registering dynamic IAttributeTypes for testing. This is done
 * by sub-classing the registry {@link AttributeTypes} and feeding it with
 * a hard-coded list of attribute types.
 * 
 * @author Thomas Muller
 */
public class AttributeTestTypes extends AttributeTypes {

	private static final IConfigurationElement[] NONE = new IConfigurationElement[0];

	private static final String[] TYPE_IDS = new String[] { //
	"attr", "a1", "a2" //
	};

	@Override
	protected IConfigurationElement[] readExtensions() {
		final int length = TYPE_IDS.length;
		final IConfigurationElement[] result = new IConfigurationElement[length];
		for (int i = 0; i < length; i++) {
			final String id = TYPE_IDS[i];
			final String[] attributes = new String[] { "id='" + id + "'",
					"name='" + id + " Attribute'", "kind='handle'" };
			result[i] = new FakeConfigurationElement("attributeType",
					attributes, NONE);
		}
		return result;
	}
	
	public void setRelations(RelationsComputer c,
			IAttributeType[] attrTypes) {
		for (IAttributeType type : attrTypes) {
			c.setAttributeRelations(type);
		}
	}
	
}
