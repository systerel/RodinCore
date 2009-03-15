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

import static org.rodinp.core.RodinCore.getInternalLocation;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.SourceLocation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.location.IAttributeLocation;
import org.rodinp.core.location.IAttributeSubstringLocation;
import org.rodinp.core.location.IInternalLocation;

public class EventBIndexUtil {

	/**
	 * Returns the Rodin location for the given formula, assuming that the
	 * source location of the given formula contains an attribute location of
	 * type <code>String</code>.
	 * <p>
	 * Note that when extracting a location from a <code>SourceLocation</code>,
	 * using this method is mandatory, as long as {@link SourceLocation} and
	 * {@link IAttributeSubstringLocation} do not share the same range
	 * convention.
	 * </p>
	 * 
	 * @param formula
	 *            a formula
	 * @return the corresponding IInternalLocation
	 */
	public static IInternalLocation getRodinLocation(Formula<?> formula) {
		final SourceLocation srcLoc = formula.getSourceLocation();
		final IAttributeLocation elemLoc = (IAttributeLocation) srcLoc
				.getOrigin();
		final IInternalElement element = elemLoc.getElement();
		final IAttributeType.String attributeType = (IAttributeType.String) elemLoc
				.getAttributeType();
		return getInternalLocation(element, attributeType, srcLoc.getStart(),
				srcLoc.getEnd() + 1);
	}

}
