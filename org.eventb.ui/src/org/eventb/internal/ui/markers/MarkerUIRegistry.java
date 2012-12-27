/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IElementType;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;

public class MarkerUIRegistry implements IMarkerUIRegistry {

	private static IMarkerUIRegistry instance;
	
	private MarkerUIRegistry() {
		// Singleton: Private constructor.
	}
	
	public static IMarkerUIRegistry getDefault() {
		if (instance == null) {
			instance = new MarkerUIRegistry();
		}
		return instance;
	}
	
	@Override
	public IMarker[] getMarkers(IRodinElement element) throws CoreException {
		return MarkerRegistry.getDefault().getMarkers(element);
	}

	@Override
	public int getMaxMarkerSeverity(IRodinElement element) throws CoreException {
		return MarkerRegistry.getDefault().getMaxMarkerSeverity(element);
	}

	@Override
	public int getMaxMarkerSeverity(IRodinElement element,
			IAttributeType attributeType) throws CoreException {
		return MarkerRegistry.getDefault().getMaxMarkerSeverity(element,
				attributeType);
	}

	@Override
	public IMarker[] getAttributeMarkers(IRodinElement element,
			IAttributeType attributeType) throws CoreException {
		return MarkerRegistry.getDefault().getAttributeMarkers(element,
				attributeType);
	}

	@Override
	public int getMaxMarkerSeverity(IParent parent, IElementType<?> childType)
			throws CoreException {
		IRodinElement[] elements = parent.getChildrenOfType(childType);
		int severity = -1;
		for (IRodinElement element : elements) {
			int newSeverity = MarkerRegistry.getDefault().getMaxMarkerSeverity(
					element);
			if (severity < newSeverity)
				severity = newSeverity;
		}
		return severity;
	}

}
