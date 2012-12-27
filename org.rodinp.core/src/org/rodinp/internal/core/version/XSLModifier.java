/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.version;

import java.util.ArrayList;
import java.util.List;

import org.rodinp.core.version.IAttributeModifier;
import org.rodinp.internal.core.util.Util;

/**
 * @author Nicolas Beauger
 * 
 */
public class XSLModifier {

	private static final List<IAttributeModifier> modifiers = new ArrayList<IAttributeModifier>();

	/**
	 * Add the given modifier to the repository. The added modifier can later be
	 * referenced to through the returned int key.
	 * 
	 * @param modifier
	 *            the modifier to add
	 * @return a key that uniquely references the modifier
	 * @see #addModifier(IAttributeModifier)
	 */
	public static synchronized int addModifier(IAttributeModifier modifier) {
		final int pos = modifiers.size();
		modifiers.add(pos, modifier);
		return pos;
	}

	/**
	 * Method called from inside an xsl sheet to get the new contents of an
	 * attribute.
	 * 
	 * @param currentValue
	 *            the current value of the attribute
	 * @param modifierKey
	 *            the key of the desired modifier, previously returned by a call
	 *            to {@link #addModifier(IAttributeModifier)}
	 * @return the new value to set the attribute with
	 */
	public static synchronized String modify(String currentValue, int modifierKey) {
		final IAttributeModifier modifier = modifiers.get(modifierKey);

		if (modifier == null) {
			Util.log(null, "Upgrade [XSLModifier]: unknown modifier key: "
					+ modifierKey + "\nexpected a natural lower than "
					+ modifiers.size());
			return currentValue;
		}
		return modifier.getNewValue(currentValue);
	}
}
