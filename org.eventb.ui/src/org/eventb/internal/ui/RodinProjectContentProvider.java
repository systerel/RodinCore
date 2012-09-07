/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - remove unused code
 *******************************************************************************/
package org.eventb.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class RodinProjectContentProvider implements ITreeContentProvider {

    /**
     * Creates a new ContainerContentProvider.
     */
    public RodinProjectContentProvider() {
    	// Do nothing
    }

    /**
     * The visual part that is using this content provider is about
     * to be disposed. Deallocate all allocated SWT resources.
     */
    @Override
	public void dispose() {
    	// Do nothing
    }

    /*
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    @Override
	public Object[] getChildren(Object element) {
        if (element instanceof IRodinDB) {
			try {
				return ((IRodinDB) element).getRodinProjects();
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new Object[0];
			}
        } else if (element instanceof IContainer) {
            IContainer container = (IContainer) element;
            if (container.isAccessible()) {
                try {
					List<IResource> children = new ArrayList<IResource>();
					IResource[] members = container.members();
					for (int i = 0; i < members.length; i++) {
						if (members[i].getType() != IResource.FILE) {
							children.add(members[i]);
						}
					}
					return children.toArray();
				} catch (CoreException e) {
					// this should never happen because we call #isAccessible
					// before invoking #members
				}
            }
        }
        return new Object[0];
    }

    /*
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
    @Override
	public Object[] getElements(Object element) {
        return getChildren(element);
    }

    /*
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    @Override
	public Object getParent(Object element) {
        if (element instanceof IRodinProject) {
			element = ((IRodinProject) element).getResource();
		}
        if (element instanceof IResource) {
			return ((IResource) element).getParent();
		}
        return null;
    }

    /*
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    @Override
	public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

    /*
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged
     */
    @Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    	// Do nothing
    }

}
