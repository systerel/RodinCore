/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.indexers;

import static org.rodinp.core.RodinCore.*;

import org.eventb.core.ast.SourceLocation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.location.IAttributeSubstringLocation;
import org.rodinp.core.location.IInternalLocation;

public class EventBIndexUtil {

	/**
	 * When extracting a location from a SourceLocation, using that method is
	 * mandatory, as long as {@link SourceLocation} and
	 * {@link IAttributeSubstringLocation} do not share the same range
	 * convention.
	 * 
	 * @param element
	 * @param attributeType
	 * @param location
	 * @return the corresponding IInternalLocation
	 */
	public static IInternalLocation getRodinLocation(IInternalElement element,
			IAttributeType.String attributeType, SourceLocation location) {
		return getInternalLocation(element, attributeType, location.getStart(),
				location.getEnd() + 1);
	}

}
