/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests.version.db;

import static org.rodinp.core.tests.AbstractRodinDBTests.PLUGIN_ID;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.RodinCore;

/**
 * @author Stefan Hallerstede
 *
 */
public final class VersionAttributes {
	
	public final static IAttributeType.String StringAttr = 
		RodinCore.getStringAttrType(PLUGIN_ID + ".versionStrAttr");

	public final static IAttributeType.String StringAttrX = 
		RodinCore.getStringAttrType(PLUGIN_ID + ".versionStrAttrX");
}
