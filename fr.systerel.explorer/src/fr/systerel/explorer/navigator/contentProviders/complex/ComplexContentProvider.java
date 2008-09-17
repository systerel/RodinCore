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

import fr.systerel.explorer.model.ModelContext;
import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.model.ModelMachine;
import fr.systerel.explorer.model.ModelProject;

public class ComplexContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object element) {
        if (element instanceof IRodinProject) {
        	IRodinProject project = (IRodinProject) element;
        	ModelController.processProject(project);
        	ModelProject prj= ModelController.getProject(project);
        	IMachineFile[] machines = ModelController.convertToIMachine(prj.getRootMachines());
        	IContextFile[] contexts = ModelController.convertToIContext(prj.getDisconnectedContexts());
        	Object[] results = new Object[machines.length + contexts.length];
        	System.arraycopy(machines, 0, results, 0,machines.length);
        	System.arraycopy(contexts, 0, results, machines.length, contexts.length);
     	
        	return results;
        } 
        if (element instanceof IMachineFile) {
        	ModelMachine machine = ModelController.getMachine(((IMachineFile) element));
        	List<ModelMachine> rest = machine.getRestMachines();
        	List<ModelMachine> machines = new LinkedList<ModelMachine>();
        	List<Object> result = new LinkedList<Object>();
        	for (Iterator<ModelMachine> iterator = rest.iterator(); iterator.hasNext();) {
				ModelMachine mach = iterator.next();
				machines.addAll(mach.getLongestMachineBranch());
			}
        	result.addAll(ModelController.convertToIMachine(machines));
        	List<ModelContext> sees = machine.getSeesContexts();
        	List<ModelContext> contexts = new LinkedList<ModelContext>();
        	for (Iterator<ModelContext> iterator = sees.iterator(); iterator.hasNext();) {
				ModelContext context = iterator.next();
				contexts.addAll(context.getLongestContextBranch());
				
			}
        	result.addAll(ModelController.convertToIContext(contexts));
        	
        	return result.toArray();
        } 
        if (element instanceof IContextFile) {
        	ModelContext context = ModelController.getContext(((IContextFile) element));
        	List<ModelContext> rest = context.getRestContexts();
        	List<ModelContext> result = new LinkedList<ModelContext>();
        	for (Iterator<ModelContext> iterator = rest.iterator(); iterator.hasNext();) {
				ModelContext ctx = iterator.next();
				result.addAll(ctx.getLongestContextBranch());
			}
        	return ModelController.convertToIContext(result).toArray();
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

}
