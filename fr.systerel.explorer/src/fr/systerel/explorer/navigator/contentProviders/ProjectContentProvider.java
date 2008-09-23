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


package fr.systerel.explorer.navigator.contentProviders;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IWorkingSet;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * The Content Provider for Rodin Projects
 *
 */
public class ProjectContentProvider implements ITreeContentProvider {

 	/**
     * Creates a new ContainerContentProvider.
     */
    public ProjectContentProvider() {
    	// Do nothing
    }

    /**
     * The visual part that is using this content provider is about
     * to be disposed. Deallocate all allocated SWT resources.
     */
    public void dispose() {
    	// Do nothing
    }

    /*
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object element) {

    	if (element instanceof IWorkingSet) {
    		ArrayList<Object> results =  new ArrayList<Object>();
    		IWorkingSet working = (IWorkingSet) element;
    		IAdaptable[] children = working.getElements();
    		for (int i = 0; i < children.length; i++) {
				IAdaptable child = children[i];
				if (child instanceof IProject) {
					IProject project =  (IProject) child;
					if (project.isAccessible()) {
						try {
							//if it is a RodinProject return the IRodinProject from the DB.
							if (project.hasNature(RodinCore.NATURE_ID)) {
								results.add(RodinCore.getRodinDB().getRodinProject(project.getName()));
								
							} else results.add(child);
						} catch (CoreException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else results.add(child);
				} else results.add(child);
				
			}
    		return results.toArray();
    	}
    	
        if (element instanceof IRodinDB) {
        	// get open projects
        	// it is currently not possible to get closed projects from the RodinDB
            IRodinProject[] openProjects;
			try {
				openProjects = ((IRodinDB) element).getRodinProjects();
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new Object[0];
			}
			return openProjects;
			
        } else if (element instanceof IContainer) {
            IContainer container = (IContainer) element;
            if (container.isAccessible()) {
                try {
					List<Object> children = new ArrayList<Object>();
					IResource[] members = container.members();
					for (int i = 0; i < members.length; i++) {
						if (members[i].getType() != IResource.FILE) {
							if (members[i].getType() == IResource.PROJECT) {
								IProject project = (IProject) members[i];
								if (project.isAccessible()) {
									//if it is a RodinProject return the IRodinProject from the DB.
									if (project.hasNature(RodinCore.NATURE_ID)) {
										children.add(RodinCore.getRodinDB().getRodinProject(project.getName()));
									}else {
										children.add(members[i]);							
									}
								}else {
									children.add(members[i]);
								}
							}else {
								children.add(members[i]);
							}
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
    public Object[] getElements(Object element) {
        return getChildren(element);
    }

    /*
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object element) {
        if (element instanceof IRodinProject) {
			element = ((IRodinProject) element).getRodinDB();
		}
        if (element instanceof IResource) {
			return ((IResource) element).getParent();
		}
        return null;
    }

    /*
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

    /*
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    	// Do nothing
    }

    
}
