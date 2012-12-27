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
package org.rodinp.core.tests.version.db;

import org.rodinp.core.version.IAttributeModifier;

/**
 * @author Nicolas Beauger
 *
 */
public class Modifier implements IAttributeModifier {

	private static final String MODIFIED = "[modified]";

	public String getNewValue(String attributeValue) {
		return attributeValue + MODIFIED;
	}

}
