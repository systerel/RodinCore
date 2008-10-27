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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.ExplorerUtils;
import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.model.ModelMachine;
import fr.systerel.explorer.model.ModelProject;

/**
 * The content provider for machines
 *
 */
public class ComplexMachineContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object element) {
		if (element instanceof IProject) {
			return getProjectChildren((IProject) element);
	    }
        if (element instanceof IMachineRoot) {
        	return getMachineChildren((IMachineRoot) element);
        } 
        return new Object[0];
	}

	public Object getParent(Object element) {
        if (element instanceof IMachineRoot) {
        	ModelMachine machine = ModelController.getMachine((IMachineRoot) element);
        	if (machine != null) {
        		//return the first machineRoot that is refined by this machine, if one exists.
        		if (machine.getRefinesMachines().size() >0) {
        			return machine.getRefinesMachines().get(0).getInternalMachine();
        		}
        	}
        	//this returns the project
			return ((IMachineRoot) element).getRodinFile().getParent();
		}
        return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IProject) {
        	IProject project = (IProject) element;
			//if it is a RodinProject return the IRodinProject from the DB.
			try {
				if (project.isAccessible() && project.hasNature(RodinCore.NATURE_ID)) {
					return ExplorerUtils.getMachineRootChildren(ExplorerUtils.getRodinProject(project)).length >0;
				}
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
        return getChildren(element).length >0;
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

	
	protected Object[] getProjectChildren(IProject project){
		if (project.isAccessible()) {
			try {
				//if it is a RodinProject get the IRodinProject from the DB.
				if (project.hasNature(RodinCore.NATURE_ID)) {
					IRodinProject proj = ExplorerUtils.getRodinProject(project);
					if (proj != null) {
		            	ModelController.processProject(proj);
			        	ModelProject prj= ModelController.getProject(proj);
			        	if (prj != null) {
				        	return ModelController.convertToIMachine(prj.getRootMachines());
			        	}
					}
				} 
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return new Object[0];
	}
	
	protected Object[] getMachineChildren(IMachineRoot root) {
    	ModelMachine machine = ModelController.getMachine(root);
    	if (machine != null) {
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
}
