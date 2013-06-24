/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Refactored and fixed NPE
 *******************************************************************************/
package org.eventb.internal.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.rodinp.core.RodinMarkerUtil;

/**
 * This class provides labels for different elements appeared in the UI.
 * 
 * @author htson
 */
public class RodinElementTreeLabelProvider extends
		RodinElementStructuredLabelProvider {

	public RodinElementTreeLabelProvider(TreeViewer viewer) {
		super(viewer);
	}

	@Override
	protected Set<Object> getRefreshElements(IResourceChangeEvent event) {
		final IMarkerDelta[] rodinProblemMakerDeltas = event.findMarkerDeltas(
				RodinMarkerUtil.RODIN_PROBLEM_MARKER, true);
		final Set<Object> elements = new HashSet<Object>();

		final IContentProvider contentProvider = viewer.getContentProvider();
		if (contentProvider instanceof ITreeContentProvider) {
			final ITreeContentProvider cp = (ITreeContentProvider) contentProvider;

			for (IMarkerDelta delta : rodinProblemMakerDeltas) {
				Object element = RodinMarkerUtil.getElement(delta);
				if (element != null && !elements.contains(element)) {
					do {
						elements.add(element);
						element = cp.getParent(element);
					} while (element != null);
				}
			}
		}

		return elements;
	}

}