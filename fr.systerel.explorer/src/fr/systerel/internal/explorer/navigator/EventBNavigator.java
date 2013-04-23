/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

import fr.systerel.internal.explorer.model.ModelController;

public class EventBNavigator extends CommonNavigator {

	@Override
	protected CommonViewer createCommonViewerObject(Composite aParent) {
		return new CommonViewer(getViewSite().getId(), aParent, SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL) {
			@Override
			public void refresh(Object element, boolean updateLabels) {
				if (element instanceof IWorkspaceRoot) {
					final IProject[] projects = ((IWorkspaceRoot) element)
							.getProjects();
					for (IProject project : projects) {
						final IRodinProject rProject = RodinCore
								.valueOf(project);
						ModelController.getInstance().refreshModel(rProject);
					}
				}

				super.refresh(element, updateLabels);
			}
		};

	}

	@Override
	protected CommonViewer createCommonViewer(Composite aParent) {
		final CommonViewer viewer = super.createCommonViewer(aParent);
		NavigatorController.setUpNavigator(viewer);
		return viewer;
	}

}
