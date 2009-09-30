/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation as
 *     		org.eclipse.jdt.core.IJavaModel
 *     ETH Zurich - adaptation from JDT to Rodin
 *******************************************************************************/
package org.rodinp.core;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.rodinp.internal.core.ElementTypeManager;
import org.rodinp.internal.core.util.Util;


/**
 * Utility class for manipulating markers used by the Rodin database.
 * <p>
 * TODO add description of Rodin markers: their attributes and their meaning
 * </p>
 * <p>
 * This class is not intended to be extended by clients.
 * </p>
 * 
 * @author Laurent Voisin
 * @author Thai Son Hoang
 * @since 1.0
 */
public final class RodinMarkerUtil {

	/**
	 * Rodin database problem marker type (value
	 * <code>"org.rodinp.core.problem"</code>). This can be used to recognize
	 * those markers in the workspace that flag problems detected by Rodin
	 * tools.
	 */
	public static final String RODIN_PROBLEM_MARKER = 
		RodinCore.PLUGIN_ID + ".problem"; //$NON-NLS-1$

	/**
	 * Error code marker attribute (value <code>"code"</code>). This
	 * attribute contains the internal error code of the problem, so that
	 * clients do not have to parse the localized error message associated to a
	 * problem. The error code must be unique (i.e., prefixed with the
	 * contributing plug-in identifier).
	 */
	public static final String ERROR_CODE = "code"; //$NON-NLS-1$

	/**
	 * Arguments marker attribute (value <code>"arguments"</code>). This
	 * attribute contains the arguments to the problem.
	 * <p>
	 * Arguments are concatenated into one String, each being prefixed with its
	 * length (in decimal form followed with a colon). For example:
	 * <ul>
	 * <li><code>{ "foo", "cafe" }</code> is encoded as
	 * <code>"3:foo4:cafe"</code>,</li>
	 * <li><code>{  }</code> is encoded as <code>""</code>.</li>
	 * </ul>
	 * </p>
	 */
	public static final String ARGUMENTS = "arguments"; //$NON-NLS-1$

	/**
	 * Element marker attribute (value <code>"element"</code>). This
	 * attribute contains the handle identifier of the Rodin internal element
	 * where the problem is located.
	 * <p>
	 * This attribute is optional, however it must be set if
	 * {@link #ATTRIBUTE_ID} is specified.
	 * </p>
	 */
	public static final String ELEMENT = "element"; //$NON-NLS-1$

	/**
	 * Attribute id marker attribute (value <code>"attributeId"</code>). This
	 * attribute contains the identifier of the Rodin attribute where the
	 * problem is located.
	 * <p>
	 * This marker attribute is optional, however it must be set if either
	 * {@link #CHAR_START} or {@link #CHAR_END} is specified.
	 * </p>
	 */
	public static final String ATTRIBUTE_ID = "attributeId"; //$NON-NLS-1$

	/**
	 * Character start marker attribute (value <code>"charStart"</code>).
	 * <p>
	 * An integer value indicating where a text marker starts. This attribute is
	 * zero-relative and inclusive. The position stored in this attribute is
	 * relative to the String representation of the Rodin attribute where the
	 * error is located (see {@link #ATTRIBUTE_ID}). That latter attribute must
	 * thus be of kind <code>String</code>.
	 * </p>
	 * <p>
	 * This attribute is optional, however it must be set if {@link #CHAR_END}
	 * is set. Moreover, its value must be strictly less than that of
	 * <code>CHAR_END</code>.
	 * </p>
	 */
	public static final String CHAR_START = "charStart"; //$NON-NLS-1$

	/**
	 * Character end marker attribute (value <code>"charEnd"</code>).
	 * <p>
	 * An integer value indicating where a text marker ends. This attribute is
	 * zero-relative and exclusive. The position stored in this attribute is
	 * relative to the String representation of the Rodin attribute where the
	 * error is located (see {@link #ATTRIBUTE_ID}). That latter attribute must
	 * thus be of kind <code>String</code>.
	 * </p>
	 * <p>
	 * This attribute is optional, however it must be set if {@link #CHAR_START}
	 * is set. Moreover, its value must be strictly greater than that of
	 * <code>CHAR_START</code>.
	 * </p>
	 */
	public static final String CHAR_END = "charEnd"; //$NON-NLS-1$

	/**
	 * Build problem marker type (value
	 * <code>"org.rodinp.core.buildProblem"</code>). This can be used to
	 * recognize those markers in the workspace that flag problems detected by
	 * the Rodin builder while computing the dependency graph.
	 */
	public static final String BUILDPATH_PROBLEM_MARKER = 
		RodinCore.PLUGIN_ID + ".buildProblem"; //$NON-NLS-1$

	/**
	 * Cycle detected marker attribute (value <code>"cycleDetected"</code>).
	 * Used only on build problem markers. The value of this attribute is either
	 * <code>true</code> or <code>false</code>.
	 */
	public static final String CYCLE_DETECTED = "cycleDetected"; //$NON-NLS-1$

	private static final String[] NO_STRING = new String[0]; 
	
	private static void checkRodinProblemMarker(IMarker marker) {
		try {
			if (!marker.isSubtypeOf(RODIN_PROBLEM_MARKER)) {
				throw new IllegalArgumentException("Not a Rodin problem marker");
			}
		} catch (CoreException ce) {
			// Inexistent marker
		}
	}
	
	/**
	 * Parse an argument from the arguments attribute.
	 * <p>
	 * This method is coded in an offensive way, so that all exceptions are
	 * passed to the caller, without any special handling.
	 * </p>
	 * 
	 * @param argsString
	 *            string containing the concatenated arguments
	 * @param idx
	 *            start index for the argument to parse
	 * @param args
	 *            array where to store the parsed argument
	 * @return the index just after the parsed argument
	 * @throws IndexOutOfBoundsException
	 * @throws NumberFormatException
	 */
	private static int parseArg(String argsString, int idx,
			ArrayList<String> args) {

		final int colIdx = argsString.indexOf(':', idx);
		final String argLengthString = argsString.substring(idx, colIdx);
		final int argLength = Integer.parseInt(argLengthString);
		final int argStart = colIdx + 1;
		final int argEnd = argStart + argLength;
		final String arg = argsString.substring(argStart, argEnd);
		args.add(arg);
		return argEnd;
	}

	/**
	 * Returns the error code of the given marker. Returns <code>null</code>
	 * if the marker does not exist or does not carry an error code.
	 * 
	 * @param marker
	 *            marker to read
	 * @return the error code of the given marker or <code>null</code> in case
	 *         of error
	 * @throws IllegalArgumentException
	 *             if the given marker is not a Rodin problem marker
	 * @see #ERROR_CODE
	 */
	public static String getErrorCode(IMarker marker) {
		checkRodinProblemMarker(marker);
		return marker.getAttribute(ERROR_CODE, null);
	}

	/**
	 * Returns the arguments of the given marker. Returns <code>null</code> if
	 * the marker does not exist, does not carry an arguments attribute or the
	 * attribute value is ill-formed. If the marker has no argument, an empty
	 * array is returned.
	 * 
	 * @param marker
	 *            marker to read
	 * @return the arguments of the given marker or <code>null</code> in case
	 *         of error
	 * @throws IllegalArgumentException
	 *             if the given marker is not a Rodin problem marker
	 * @see #ARGUMENTS
	 */
	public static String[] getArguments(IMarker marker) {
		checkRodinProblemMarker(marker);
		final String argsString = marker.getAttribute(ARGUMENTS, null);
		if (argsString == null) {
			return null;
		}
		final int length = argsString.length();
		ArrayList<String> args = new ArrayList<String>();
		try {
			int idx = 0;
			while (idx < length) {
				idx = parseArg(argsString, idx, args);
			}
		} catch (IndexOutOfBoundsException e1) {
			// A substring was out of bounds => ill-formed
			Util.log(e1, "when parsing marker arguments '" + argsString + "'");
			return null;
		} catch (NumberFormatException e2) {
			// A length was unreadable => ill-formed
			Util.log(e2, "when parsing marker arguments '" + argsString + "'");
			return null;
		}
		return args.toArray(NO_STRING);
	}

	/**
	 * Returns the smallest element marked by the given marker. Returns
	 * <code>null</code> if the marker does not exist or does not relate to a
	 * Rodin element.
	 * <p>
	 * If the given marker carries an internal element, this internal element is
	 * returned. Otherwise, if the resource to which this marked is attached
	 * corresponds to a Rodin element, this element is returned. Otherwise,
	 * <code>null</code> is returned.
	 * </p>
	 * 
	 * @param marker
	 *            marker to read
	 * @return the smallest element corresponding to the given marker or
	 *         <code>null</code> if there is no corresponding Rodin element
	 * @throws IllegalArgumentException
	 *             if the given marker is not a Rodin problem marker
	 * @see #ELEMENT
	 */
	public static IRodinElement getElement(IMarker marker) {
		final IRodinElement ie = getInternalElement(marker);
		if (ie != null) {
			return ie;
		}
		return RodinCore.valueOf(marker.getResource());
	}
	
	/**
	 * Returns the internal element of the given marker. Returns
	 * <code>null</code> if the marker does not exist or does not carry an
	 * internal element.
	 * 
	 * @param marker
	 *            marker to read
	 * @return the internal element of the given marker or <code>null</code>
	 *         in case of error
	 * @throws IllegalArgumentException
	 *             if the given marker is not a Rodin problem marker
	 * @see #ELEMENT
	 */
	public static IInternalElement getInternalElement(IMarker marker) {
		checkRodinProblemMarker(marker);
		final String handleId = marker.getAttribute(ELEMENT, null);
		if (handleId == null) {
			return null;
		}
		final IRodinElement elem = RodinCore.valueOf(handleId);
		if (!(elem instanceof IInternalElement)) {
			return null;
		}
		final IInternalElement ie = (IInternalElement) elem;
		final IResource resource = marker.getResource();
		if (resource.equals(ie.getUnderlyingResource())) {
			return ie;
		}
		// Transpose element to correct file (marker has moved)
		final IRodinElement dest = RodinCore.valueOf(resource);
		if (dest instanceof IRodinFile) {
			return ie.getSimilarElement((IRodinFile) dest);
		}
		return null;
	}
	
	/**
	 * Returns the attribute type of the given marker. Returns
	 * <code>null</code> if the marker does not exist or does not carry an
	 * attribute type.
	 * 
	 * @param marker
	 *            marker to read
	 * @return the attribute type of the given marker or <code>null</code> in case
	 *         of error
	 * @throws IllegalArgumentException
	 *             if the given marker is not a Rodin problem marker
	 * @see #ATTRIBUTE_ID
	 */
	public static IAttributeType getAttributeType(IMarker marker) {
		checkRodinProblemMarker(marker);
		final String attrId = marker.getAttribute(ATTRIBUTE_ID, null);
		final ElementTypeManager manager = ElementTypeManager.getInstance();
		return manager.getAttributeType(attrId);
	}

	/**
	 * Returns the start position of the given marker. Returns <code>-1</code>
	 * if the marker does not exist or does not carry a start position.
	 * 
	 * @param marker
	 *            marker to read
	 * @return the start position of the given marker or <code>-1</code> in
	 *         case of error
	 * @throws IllegalArgumentException
	 *             if the given marker is not a Rodin problem marker
	 * @see #CHAR_START
	 */
	public static int getCharStart(IMarker marker) {
		checkRodinProblemMarker(marker);
		return marker.getAttribute(CHAR_START, -1);
	}

	/**
	 * Returns the end position of the given marker. Returns <code>-1</code>
	 * if the marker does not exist or does not carry an end position.
	 * 
	 * @param marker
	 *            marker to read
	 * @return the end position of the given marker or <code>-1</code> in case
	 *         of error
	 * @throws IllegalArgumentException
	 *             if the given marker is not a Rodin problem marker
	 * @see #CHAR_START
	 */
	public static int getCharEnd(IMarker marker) {
		checkRodinProblemMarker(marker);
		return marker.getAttribute(CHAR_END, -1);
	}

	/**
	 * Returns the smallest element marked by the given marker delta. Returns
	 * <code>null</code> if the marker delta does not relate to a Rodin
	 * element.
	 * <p>
	 * If the given marker delta carries an internal element, this internal
	 * element is returned. Otherwise, if the resource to which this marker
	 * delta is attached corresponds to a Rodin element, this element is
	 * returned. Otherwise, <code>null</code> is returned.
	 * </p>
	 * 
	 * @param delta
	 *            marker delta to read
	 * @return the smallest element corresponding to the given marker delta or
	 *         <code>null</code> if there is no corresponding Rodin element
	 * @throws IllegalArgumentException
	 *             if the given marker is not a Rodin problem marker delta
	 * @see #ELEMENT
	 * @see #getElement(IMarker)
	 */
	public static IRodinElement getElement(IMarkerDelta delta) {
		final IRodinElement ie = getInternalElement(delta);
		if (ie != null) {
			return ie;
		}
		return RodinCore.valueOf(delta.getResource());
	}

	/**
	 * Returns the internal element of the given marker. Returns
	 * <code>null</code> if the marker does not exist or does not carry an
	 * internal element.
	 * 
	 * @param delta
	 *            marker to read
	 * @return the internal element of the given marker or <code>null</code>
	 *         in case of error
	 * @throws IllegalArgumentException
	 *             if the given marker is not a Rodin problem marker
	 * @see #ELEMENT
	 * @see #getInternalElement(IMarker)
	 */
	public static IInternalElement getInternalElement(IMarkerDelta delta) {
		checkRodinProblemMarker(delta.getMarker());
		final String handleId = delta.getAttribute(ELEMENT, null);
		final IResource resource = delta.getResource();
		return getInternalElement(resource, handleId);
	}

	/**
	 * Extracted method in order to share between
	 * {@link #getInternalElement(IMarker)} and
	 * {@link #getInternalElement(IMarkerDelta)}
	 * 
	 * @param resource
	 *            the resource
	 * @param handleId
	 *            the handle ID of the internal element.
	 * @return the internal element corresponding to the resource.
	 */
	private static IInternalElement getInternalElement(IResource resource,
			String handleId) {
		if (handleId == null) {
			return null;
		}
		final IRodinElement elem = RodinCore.valueOf(handleId);
		if (!(elem instanceof IInternalElement)) {
			return null;
		}
		final IInternalElement ie = (IInternalElement) elem;
		if (resource.equals(ie.getUnderlyingResource())) {
			return ie;
		}
		// Transpose element to correct file (marker has moved)
		final IRodinElement dest = RodinCore.valueOf(resource);
		if (dest instanceof IRodinFile) {
			return ie.getSimilarElement((IRodinFile) dest);
		}
		return null;
	}

	/**
	 * Returns the attribute type of the given marker delta. Returns
	 * <code>null</code> if the marker delta does not carry an attribute type.
	 * 
	 * @param delta
	 *            marker delta to read
	 * @return the attribute type of the given marker delta or <code>null</code>
	 *         in case of error
	 * @throws IllegalArgumentException
	 *             if the given marker delta is not a Rodin problem marker delta
	 * @see #ATTRIBUTE_ID
	 * @see #getAttributeType(IMarker)
	 */
	public static IAttributeType getAttributeType(IMarkerDelta delta) {
		checkRodinProblemMarker(delta.getMarker());
		final String attrId = delta.getAttribute(ATTRIBUTE_ID, null);
		final ElementTypeManager manager = ElementTypeManager.getInstance();
		return manager.getAttributeType(attrId);
	}

}
