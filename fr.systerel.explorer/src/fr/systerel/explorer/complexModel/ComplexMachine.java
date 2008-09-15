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


package fr.systerel.explorer.complexModel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.IMachineFile;

/**
 * @author Administrator
 *
 */
public class ComplexMachine {
	
	public ComplexMachine(IMachineFile file){
		internalMachine = file;
	}
	
	private List<ComplexContext> seesContexts = new LinkedList<ComplexContext>();
	/**
	 * Machines that refine this Machine.
	 */
	private List<ComplexMachine> refinedByMachines = new LinkedList<ComplexMachine>();
	/**
	 * Machines that this Machine refines.
	 */
	private List<ComplexMachine> refinesMachines = new LinkedList<ComplexMachine>();
	private IMachineFile internalMachine ;
	
	/**
	 * 
	 * @return is this Machine a root of a tree of Machines?
	 */
	public boolean isRoot(){
		return (refinesMachines.size() ==0);
	}
	
	/**
	 * Assuming no cycles
	 * @return The longest branch among the refinedByMachines branches (including this Machine)
	 */
	public List<ComplexMachine> getLongestMachineBranch() {
		List<ComplexMachine> results = new LinkedList<ComplexMachine>();
		results.add(this);
		List<ComplexMachine> longest = new LinkedList<ComplexMachine>();
		for (Iterator<ComplexMachine> iterator = refinedByMachines.iterator(); iterator.hasNext();) {
			ComplexMachine machine = iterator.next();
			if (machine.getLongestMachineBranch().size() > longest.size()) {
				longest = machine.getLongestMachineBranch();
			}
		}
		results.addAll(longest);
		return results;
	}
	
	/**
	 * Assuming no cycles
	 * @return All Ancestors of this machine
	 */
	public List<ComplexMachine> getAncestors(){
		List<ComplexMachine> results = new LinkedList<ComplexMachine>();
		for (Iterator<ComplexMachine> iterator = refinesMachines.iterator(); iterator.hasNext();) {
			ComplexMachine machine = iterator.next();
			results.add(machine);
			results.addAll(machine.getAncestors());
		}
		return results;
	}
	
	/**
	 * 
	 * @return All the refinedByMachines, that are not returned by getLongestBranch
	 */
	public List<ComplexMachine> getRestMachines(){
		List<ComplexMachine> copy = new LinkedList<ComplexMachine>(refinedByMachines);
		copy.removeAll(getLongestMachineBranch());
		return copy;
	}
	
	public void addRefinesMachine(ComplexMachine machine) {
		refinesMachines.add(machine);
	}
	public void addRefinedByMachine(ComplexMachine machine) {
		refinedByMachines.add(machine);
	}
	public void addSeesContext(ComplexContext context) {
		seesContexts.add(context);
	}

	public IMachineFile getInternalMachine() {
		return internalMachine;
	}

	public List<ComplexContext> getSeesContexts() {
		return seesContexts;
	}
}
