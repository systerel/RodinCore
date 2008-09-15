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


import fr.systerel.explorer.complexModel.ComplexContext;
import fr.systerel.explorer.complexModel.ComplexMachine;
import fr.systerel.explorer.complexModel.ComplexModelFactory;
import fr.systerel.explorer.complexModel.ComplexProject;
import fr.systerel.explorer.poModel.PoModelFactory;

public class ComplexContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object element) {
        if (element instanceof IRodinProject) {
        	IRodinProject project = (IRodinProject) element;
        	ComplexModelFactory.processProject(project);
        	PoModelFactory.processProject(project);
        	ComplexProject prj = ComplexModelFactory.getProject(project.getHandleIdentifier());
        	IMachineFile[] machines = ComplexModelFactory.convertToIMachine(prj.getRootMachines());
        	IContextFile[] contexts = ComplexModelFactory.convertToIContext(prj.getDisconnectedContexts());
        	Object[] results = new Object[machines.length + contexts.length];
        	System.arraycopy(machines, 0, results, 0,machines.length);
        	System.arraycopy(contexts, 0, results, machines.length, contexts.length);
         	
        	return results;
        } 
        if (element instanceof IMachineFile) {
        	
        	ComplexMachine machine = ComplexModelFactory.getMachine(((IMachineFile) element).getHandleIdentifier());
        	List<ComplexMachine> rest = machine.getRestMachines();
        	List<ComplexMachine> machines = new LinkedList<ComplexMachine>();
        	List<Object> result = new LinkedList<Object>();
        	for (Iterator<ComplexMachine> iterator = rest.iterator(); iterator.hasNext();) {
				ComplexMachine mach = iterator.next();
				machines.addAll(mach.getLongestMachineBranch());
			}
        	result.addAll(ComplexModelFactory.convertToIMachine(machines));

        	List<ComplexContext> sees = machine.getSeesContexts();
        	List<ComplexContext> contexts = new LinkedList<ComplexContext>();
        	for (Iterator<ComplexContext> iterator = sees.iterator(); iterator.hasNext();) {
				ComplexContext context = iterator.next();
				contexts.addAll(context.getLongestContextBranch());
				
			}
        	result.addAll(ComplexModelFactory.convertToIContext(contexts));
        	return result.toArray();
        } 
        if (element instanceof IContextFile) {
        	ComplexContext context = ComplexModelFactory.getContext(((IContextFile) element).getHandleIdentifier());
        	List<ComplexContext> rest = context.getRestContexts();
        	List<ComplexContext> result = new LinkedList<ComplexContext>();
        	for (Iterator<ComplexContext> iterator = rest.iterator(); iterator.hasNext();) {
				ComplexContext ctx = iterator.next();
				result.addAll(ctx.getLongestContextBranch());
			}
        	return ComplexModelFactory.convertToIContext(result).toArray();
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
