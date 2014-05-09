/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests;

import static org.rodinp.core.RodinMarkerUtil.RODIN_PROBLEM_MARKER;
import static org.rodinp.core.RodinMarkerUtil.getArguments;
import static org.rodinp.core.RodinMarkerUtil.getAttributeType;
import static org.rodinp.core.RodinMarkerUtil.getCharEnd;
import static org.rodinp.core.RodinMarkerUtil.getCharStart;
import static org.rodinp.core.RodinMarkerUtil.getElement;
import static org.rodinp.core.RodinMarkerUtil.getErrorCode;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.RodinElement;

/**
 * Encapsulate a marker to make it self describing in tests.
 * 
 * @author Laurent Voisin
 */
public class SelfDescribingMarker implements SelfDescribing {

	private final IMarker marker;

	public SelfDescribingMarker(IMarker marker) {
		this.marker = marker;
	}

	@Override
	public void describeTo(Description description) {
		if (isRodinMarker()) {
			final IRodinElement element = getElement(marker);
			final String path = ((RodinElement) element)
					.toStringWithAncestors();
			final IAttributeType attrType = getAttributeType(marker);
			final int charStart = getCharStart(marker);
			final int charEnd = getCharEnd(marker);
			final String errorCode = getErrorCode(marker);
			final String[] args = getArguments(marker);
			description
					.appendText("Marker {")
					//
					.appendText("\n  element = ").appendText(path)
					.appendText("\n  problem = ").appendValue(errorCode)
					.appendValueList("\n  args = {", ", ", "}", args)
					.appendText("\n  attribute = ").appendValue(attrType)
					.appendText("\n  charStart = ").appendValue(charStart)
					.appendText("\n  charEnd = ").appendValue(charEnd)
					.appendText("\n}");
		} else {
			description.appendText("Not a Rodin marker");
		}
	}

	private boolean isRodinMarker() {
		try {
			return marker.isSubtypeOf(RODIN_PROBLEM_MARKER);
		} catch (CoreException e) {
			return false;
		}
	}

}
