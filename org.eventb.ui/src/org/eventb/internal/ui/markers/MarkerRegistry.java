/*******************************************************************************
 * Copyright (c) 2007, 2011 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich - initial API and implementation
 *     Systerel - only find markers in accessible resources
 ******************************************************************************/

package org.eventb.internal.ui.markers;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinMarkerUtil;

/**
 * @author htson
 *         <p>
 *         A "fake" implementation of IMarkerRegistry which query directly the
 *         resource for information about markers.
 *         </p>
 */
public class MarkerRegistry implements IMarkerRegistry {

	private static final IMarker[] NO_MARKER = new IMarker[0];
	
	private static IMarkerRegistry instance;
	
	private MarkerRegistry() {
		// Singleton: Private constructor.
	}
	
	public static IMarkerRegistry getDefault() {
		if (instance == null) {
			instance = new MarkerRegistry();
		}
		return instance;
	}
	
	private static IMarker[] findMarkers(IRodinElement element)
			throws CoreException {
		final IResource resource = element.getResource();
		if (!resource.isAccessible()) {
			return NO_MARKER;
		}
		return resource.findMarkers(RodinMarkerUtil.RODIN_PROBLEM_MARKER, true,
				IResource.DEPTH_INFINITE);
	}

	@Override
	public IMarker[] getMarkers(IRodinElement element) throws CoreException {
		assert element != null;
		ArrayList<IMarker> list = new ArrayList<IMarker>();
		IMarker[] markers = findMarkers(element);
		for (IMarker marker : markers) {
			IRodinElement rodinElement;
			try {
				rodinElement = RodinMarkerUtil.getElement(marker);
				if (element.equals(rodinElement)) {
					list.add(marker);
				}
			} catch (IllegalArgumentException e) {
				// Ignore non-Rodin marker
				continue;
			}
		}
		return list.toArray(new IMarker[list.size()]);
	}

	@Override
	public int getMaxMarkerSeverity(IRodinElement element) throws CoreException {
		assert element != null;
		int severity = -1;
		IMarker[] markers = findMarkers(element);
		for (IMarker marker : markers) {
			IRodinElement rodinElement;
			try {
				rodinElement = RodinMarkerUtil.getElement(marker);
				if (rodinElement == null) {
					// happens with tmp files
					continue;
				}
				if (element.equals(rodinElement)
						|| element.isAncestorOf(rodinElement)) {
					int severityAttribute = marker.getAttribute(
							IMarker.SEVERITY, -1);
					if (severity < severityAttribute) {
						severity = severityAttribute;
					}
				}
			} catch (IllegalArgumentException e) {
				// Ignore non-Rodin marker
				continue;
			}
		}
		return severity;
	}

	@Override
	public int getMaxMarkerSeverity(IRodinElement element,
			IAttributeType attributeType) throws CoreException {
		assert element != null;
		assert attributeType != null;
		int severity = -1;
		IMarker[] markers = findMarkers(element);
		for (IMarker marker : markers) {
			IRodinElement rodinElement;
			try {
				IAttributeType type = RodinMarkerUtil.getAttributeType(marker);
				if (!attributeType.equals(type))
					continue;
				rodinElement = RodinMarkerUtil.getElement(marker);
				if (element.equals(rodinElement)
						|| element.isAncestorOf(rodinElement)) {
					int severityAttribute = marker.getAttribute(
							IMarker.SEVERITY, -1);
					if (severity < severityAttribute) {
						severity = severityAttribute;
					}
				}
			} catch (IllegalArgumentException e) {
				// Ignore non-Rodin marker
				continue;
			}
		}
		return severity;
	}

	@Override
	public IMarker[] getAttributeMarkers(IRodinElement element,
			IAttributeType attributeType) throws CoreException {
		assert element != null;
		ArrayList<IMarker> list = new ArrayList<IMarker>();
		IMarker[] markers = findMarkers(element);
		for (IMarker marker : markers) {
			IRodinElement rodinElement;
			try {
				IAttributeType type = RodinMarkerUtil.getAttributeType(marker);
				if (!attributeType.equals(type))
					continue;
				rodinElement = RodinMarkerUtil.getElement(marker);
				if (element.equals(rodinElement)) {
					list.add(marker);
				}
			} catch (IllegalArgumentException e) {
				// Ignore non-Rodin marker
				continue;
			}
		}
		return list.toArray(new IMarker[list.size()]);
	}

}
