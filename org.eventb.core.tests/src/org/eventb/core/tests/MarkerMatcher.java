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
import static org.rodinp.core.RodinMarkerUtil.getInternalElement;

import java.util.Arrays;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.basis.RodinElement;

/**
 * Describes an expected Rodin marker in the database.
 * 
 * @author Laurent Voisin
 */
public class MarkerMatcher extends BaseMatcher<IMarker> {

	/**
	 * Returns a matcher for a marker on the given element with the given
	 * problem and arguments. The matched marker must not have an attribute set.
	 */
	public static MarkerMatcher marker(IRodinElement element,
			IRodinProblem problem, String... args) {
		return new MarkerMatcher(element, null, -1, -1, problem, args);
	}

	/**
	 * Returns a matcher for a marker on the given attribute with the given
	 * problem and arguments. The matched marker must not have any substring
	 * position set.
	 */
	public static MarkerMatcher marker(IInternalElement element,
			IAttributeType attrType, IRodinProblem problem, String... args) {
		return new MarkerMatcher(element, attrType, -1, -1, problem, args);
	}

	/**
	 * Returns a matcher for a marker on the given attribute substring with the
	 * given problem and arguments.
	 */
	public static MarkerMatcher marker(IInternalElement element,
			IAttributeType attrType, int charStart, int charEnd,
			IRodinProblem problem, String... args) {
		return new MarkerMatcher(element, attrType, charStart, charEnd,
				problem, args);
	}

	private final IRodinElement element;
	private final IAttributeType attrType;
	private final int charStart;
	private final int charEnd;
	private final String errorCode;
	private final String[] args;

	private MarkerMatcher(IRodinElement element, IAttributeType attrType,
			int charStart, int charEnd, IRodinProblem problem, String[] args) {
		this.element = element;
		this.attrType = attrType;
		this.charStart = charStart;
		this.charEnd = charEnd;
		this.errorCode = problem.getErrorCode();
		this.args = args;
	}

	@Override
	public boolean matches(Object item) {
		if (!(item instanceof IMarker)) {
			return false;
		}
		final IMarker marker = (IMarker) item;
		try {
			return matches(marker);
		} catch (CoreException e) {
			return false;
		}
	}

	private boolean matches(IMarker marker) throws CoreException {
		if (!marker.isSubtypeOf(RODIN_PROBLEM_MARKER)) {
			return false;
		}
		return matchesElement(marker) && matchesProblem(marker);
	}

	private boolean matchesElement(IMarker marker) throws CoreException {
		if (attrType == null) {
			final IRodinElement markedElement = getElement(marker);
			final IAttributeType markedAttr = getAttributeType(marker);
			return element.equals(markedElement) && markedAttr == null;
		}
		final IInternalElement markedElement = getInternalElement(marker);
		final IAttributeType markedAttr = getAttributeType(marker);
		final int markedCharStart = getCharStart(marker);
		final int markedCharEnd = getCharEnd(marker);
		return element.equals(markedElement) && attrType == markedAttr
				&& charStart == markedCharStart && charEnd == markedCharEnd;
	}

	private boolean matchesProblem(IMarker marker) {
		final String markedErrorCode = getErrorCode(marker);
		final String[] markerArgs = getArguments(marker);
		return errorCode.equals(markedErrorCode)
				&& Arrays.equals(args, markerArgs);
	}

	@Override
	public void describeTo(Description description) {
		final String path = ((RodinElement) element).toStringWithAncestors();
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
	}
}
