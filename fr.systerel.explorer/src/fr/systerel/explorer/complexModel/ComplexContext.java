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

import org.eventb.core.IContextFile;

/**
 * @author Administrator
 *
 */
public class ComplexContext {

	public ComplexContext(IContextFile file) {
		internalContext = file;
	}
	
	/**
	 * The Contexts that extend this Context (children)
	 */
	private List<ComplexContext> extendedByContexts = new LinkedList<ComplexContext>();
	/**
	 * The Contexts that are extended by this Context (ancestors)
	 */
	private List<ComplexContext> extendsContexts = new LinkedList<ComplexContext>();
	private List<ComplexMachine> seenByMachines = new LinkedList<ComplexMachine>();
	private IContextFile internalContext;

	public IContextFile getInternalContext(){
		return internalContext;
	}
	
	/**
	 * Assuming no cycles
	 * @return The longest branch among the extendedByContexts branches (including this Context)
	 */
	public List<ComplexContext> getLongestContextBranch() {
		List<ComplexContext> results = new LinkedList<ComplexContext>();
		results.add(this);
		List<ComplexContext> longest = new LinkedList<ComplexContext>();
		for (Iterator<ComplexContext> iterator = extendedByContexts.iterator(); iterator.hasNext();) {
			ComplexContext context = iterator.next();
			if (context.getLongestContextBranch().size() > longest.size()) {
				longest = context.getLongestContextBranch();
			}
		}
		results.addAll(longest);
		return results;
	}
	
	/**
	 * Assuming no cycles
	 * @return All Ancestors of this machine
	 */
	public List<ComplexContext> getAncestors(){
		List<ComplexContext> results = new LinkedList<ComplexContext>();
		for (Iterator<ComplexContext> iterator = extendsContexts.iterator(); iterator.hasNext();) {
			ComplexContext context = iterator.next();
			results.add(context);
			results.addAll(context.getAncestors());
		}
		return results;
		
	}
	
	/**
	 * 
	 * @return All the extenedByContexts, that are not returned by getLongestContextBranch
	 */
	public List<ComplexContext> getRestContexts(){
		List<ComplexContext> copy = new LinkedList<ComplexContext>(extendedByContexts);
		copy.removeAll(getLongestContextBranch());
		return copy;
	}

	
	public void addExtendedByContext(ComplexContext context){
		extendedByContexts.add(context);
	}

	public void addSeenByMachine(ComplexMachine machine){
		seenByMachines.add(machine);
	}
	
	/**
	 * 
	 * @return is this Context a root of a tree of Contexts?
	 */
	public boolean isRoot(){
		return (extendsContexts.size() ==0);
	}
	
	/**
	 * 
	 * @return True, if this context is seen by no machine, otherwise false.
	 */
	public boolean isNotSeen() {
		return (seenByMachines.size() == 0);
	}

	public void addExtends(ComplexContext context) {
		extendsContexts.add(context);
		
	}
	
}
