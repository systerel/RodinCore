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


package fr.systerel.explorer.navigator.contentProviders.complex;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.model.ModelMachine;
import fr.systerel.explorer.model.ModelProject;

/**
 * The content provider for machines
 *
 */
public class ComplexMachineContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object element) {
        if (element instanceof IRodinProject) {
        	IRodinProject project = (IRodinProject) element;
        	if (project.getProject().isOpen()) {
	        	ModelController.processProject(project);
	        	ModelProject prj= ModelController.getProject(project);	     	
	        	return ModelController.convertToIMachine(prj.getRootMachines());
        	}
        } 
        if (element instanceof IMachineFile) {
        	ModelMachine machine = ModelController.getMachine(((IMachineFile) element));
        	List<ModelMachine> rest = machine.getRestMachines();
        	List<ModelMachine> machines = new LinkedList<ModelMachine>();
        	List<Object> result = new LinkedList<Object>();

        	for (Iterator<ModelMachine> iterator = rest.iterator(); iterator.hasNext();) {
				ModelMachine mach = iterator.next();
				machines.addAll(mach.getLongestBranch());
			}
        	result.addAll(ModelController.convertToIMachine(machines));
        	
        	return result.toArray();
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
		if (element instanceof IRodinProject) {
        	IRodinProject project = (IRodinProject) element;
        	try {
				return project.hasChildren();
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	   	// Do nothing
		 
	}

}
