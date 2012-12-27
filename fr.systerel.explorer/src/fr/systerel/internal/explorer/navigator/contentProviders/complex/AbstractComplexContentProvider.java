/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator.contentProviders.complex;

import org.eclipse.core.resources.IProject;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.model.ModelProject;
import fr.systerel.internal.explorer.navigator.contentProviders.AbstractRootContentProvider;

/**
 * @author Nicolas Beauger
 * 
 */
public abstract class AbstractComplexContentProvider extends
		AbstractRootContentProvider {

	public AbstractComplexContentProvider(
			IInternalElementType<? extends IEventBRoot> rootType) {
		super(rootType);
	}

	protected static final Object[] NO_OBJECT = new Object[0];

	protected Object[] getProjectChildren(IProject project) {
		// if it is a RodinProject return the IRodinProject from the DB.
		IRodinProject proj = RodinCore.valueOf(project);
		if (proj.exists()) {
			ModelController.processProject(proj);
			ModelProject prj = ModelController.getProject(proj);
			if (prj != null) {
				return convertToElementType(prj);
			}
		}
		return NO_OBJECT;
	}

	@Override
	public Object[] getChildren(Object element) {
		if (element instanceof IProject) {
			return getProjectChildren((IProject) element);
		}
		IModelElement model = ModelController.getModelElement(element);
		if (model != null) {
			return model.getChildren(rootType, false);
		}
		return NO_OBJECT;
	}

	protected abstract IEventBRoot[] convertToElementType(ModelProject project);

	@Override
	public Object getParent(Object element) {
		IModelElement model = ModelController.getModelElement(element);
		if (model != null) {
			return model.getParent(true);
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		// TODO could factorize with super.hasChildren
		if (element instanceof IProject) {
			IRodinProject rodinProject = RodinCore.valueOf((IProject) element);
			if (rodinProject.exists()) {
				try {
					return getRootChildren(rodinProject).length > 0;
				} catch (RodinDBException e) {
					return false;
				}
			}
		}
		return getChildren(element).length > 0;
	}

}
