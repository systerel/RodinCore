/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package fr.systerel.internal.explorer.navigator.contentProviders.complex;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.navigator.CommonViewer;
import org.eventb.core.IContextRoot;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.model.ModelProject;
import fr.systerel.internal.explorer.navigator.ExplorerUtils;
import fr.systerel.internal.explorer.navigator.NavigatorController;

/**
 * The content provider for Contexts. 
 *
 */
public class ComplexContextContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object element) {
		if (element instanceof IProject) {
			return getProjectChildren((IProject) element);
		}
		IModelElement model = ModelController.getModelElement(element);
		if (model != null) {
			return model.getChildren(IContextRoot.ELEMENT_TYPE, false);
		}
		return new Object[0];
	}

	public Object getParent(Object element) {
		IModelElement model = ModelController.getModelElement(element);
		if (model != null) {
			return model.getParent(true);
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IProject) {
			IRodinProject rodinProject = RodinCore.valueOf((IProject) element);
			if (rodinProject.exists()) {
				try {
					return ExplorerUtils.getContextRootChildren(rodinProject).length > 0;
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return getChildren(element).length > 0;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
		// Do nothing

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (viewer instanceof CommonViewer) {
			NavigatorController.setUpNavigator((CommonViewer) viewer);
		}

	}

	protected Object[] getProjectChildren(IProject project) {
		//if it is a RodinProject return the IRodinProject from the DB.
		IRodinProject proj = RodinCore.valueOf(project);
		if (proj.exists()) {
			ModelController.processProject(proj);
			ModelProject prj = ModelController.getProject(proj);
			if (prj != null) {
				return ModelController.convertToIContext(prj.getRootContexts());
			}
		}

		return new Object[0];
	}
}
