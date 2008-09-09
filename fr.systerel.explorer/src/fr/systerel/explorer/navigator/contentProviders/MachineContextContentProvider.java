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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * The content provider for Machine and Context elements.
 * Provides content for a project (shows all machines and contexts).
 * @author Maria Husmann
 *
 */
public class MachineContextContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object element) {
        if (element instanceof IRodinProject) {
        	IRodinProject project = (IRodinProject) element;
        	try {
				return filterChildren(project.getChildren());
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new Object[0];
			}
        } 
        return new Object[0];
	}

	public Object getParent(Object element) {
        if (element instanceof IMachineFile) {
			return ((IMachineFile) element).getParent();
		}
        if (element instanceof IContextFile) {
			return ((IContextFile) element).getParent();
		}
        return null;
	}

	public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
    	// Do nothing

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	   	// Do nothing
		 
	}
	
	/**
	 * This filter lets pass only elements that are of type <code>IMachineFile</code> or <code>IContextFile</code>.
	 * @param children	The children to filter
	 * @return			The filtered objects
	 */
	private Object[] filterChildren(IRodinElement[] children) {
		if (children != null) {
			List<IRodinElement> list = new LinkedList<IRodinElement>();
			for (int i = 0; i < children.length; i++) {
				if (children[i] instanceof IMachineFile) {
					list.add(children[i]);
				}
				else if (children[i] instanceof IContextFile) {
					list.add(children[i]);
				}
			}
			return list.toArray();
		} else return new Object[0];
	}

}
