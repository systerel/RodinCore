/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.rodinp.core.version;

/**
 * Common protocol for classes that modify an attribute in a version upgrade.
 * Attribute modifiers are contributed to the Rodin versioning system through
 * extension point <code>org.rodinp.core.conversions</code>.
 * <p>
 * Instances of this class are called during an xsl transformation process to
 * provide new attribute values.
 * </p>
 * 
 * @author Nicolas Beauger
 */
public interface IAttributeModifier {

	/**
	 * Returns the new value to set for an attribute having the given value.
	 * 
	 * @param attributeValue
	 *            the current value of an attribute in a rodin file
	 * @return the new value to set the attribute with
	 */
	String getNewValue(String attributeValue);

}
