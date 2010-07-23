/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator.contentProviders;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.navigator.CommonViewer;
import org.eventb.core.IEventBRoot;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.navigator.NavigatorController;

/**
 * @author Nicolas Beauger
 * 
 */
public abstract class AbstractRootContentProvider implements
		ITreeContentProvider {

	private static final Object[] NO_OBJECT = new Object[0];

	protected final IInternalElementType<? extends IEventBRoot> rootType;

	public AbstractRootContentProvider(
			IInternalElementType<? extends IEventBRoot> rootType) {
		this.rootType = rootType;
	}

	protected abstract IEventBRoot[] getRootChildren(IRodinProject project)
			throws RodinDBException;

	@Override
	public Object[] getChildren(Object element) {
		if (element instanceof IProject) {
			IRodinProject proj = RodinCore.valueOf((IProject) element);
			if (proj.exists()) {
				ModelController.processProject(proj);
				try {
					return getRootChildren(proj);
				} catch (RodinDBException e) {
					UIUtils.log(e, "when accessing " + rootType.getName()
							+ " roots of " + proj);
				}
			}
		}
		return NO_OBJECT;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IProject) {
			IRodinProject proj = RodinCore.valueOf((IProject) element);
			if (proj.exists()) {
				try {
					return getRootChildren(proj).length > 0;
				} catch (RodinDBException e) {
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
		// ignore
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (viewer instanceof CommonViewer) {
			NavigatorController.setUpNavigator((CommonViewer) viewer);
		}
	}

}
