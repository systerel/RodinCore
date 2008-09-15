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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.IContextFile;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;


public class ComplexProject {
	
	public ComplexProject(IRodinProject project) {
		internalProject = project;
	}
	private IRodinProject internalProject;
	
	public void processMachine(IMachineFile machine) {
		ComplexMachine mach;
		if (!machines.containsKey(machine.getHandleIdentifier())) {
			mach =  new ComplexMachine(machine);
			machines.put(machine.getHandleIdentifier(), mach);
		}
		mach = machines.get(machine.getHandleIdentifier());
		try {
			// get all machines, that this machine refines (all abstract machines)
			IRefinesMachine[] refines;
			refines = machine.getRefinesClauses();
			for (int j = 0; j < refines.length; j++) {
				IRefinesMachine refine = refines[j];
				IMachineFile abst = refine.getAbstractMachine();
				if (!machines.containsKey(abst.getHandleIdentifier())) {
					processMachine(abst);
				}
				ComplexMachine cplxMach = machines.get(abst.getHandleIdentifier());
				// don't allow cycles
				if (!cplxMach.getAncestors().contains(mach)) {
					cplxMach.addRefinedByMachine(mach);
					mach.addRefinesMachine(cplxMach);
				}
				
			}
			// get all contexts, that this machine sees
			ISeesContext[] sees =  machine.getSeesClauses();
			for (int j = 0; j < sees.length; j++) {
				ISeesContext see = sees[j];
				IContextFile ctx = see.getSeenSCContext().getContextFile();
				if (!contexts.containsKey(ctx.getHandleIdentifier())) {
					processContext(ctx);
				}
				ComplexContext cplxCtxt = contexts.get(ctx.getHandleIdentifier());
				cplxCtxt.addSeenByMachine(mach);
				mach.addSeesContext(cplxCtxt);
				
			}
			
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void processContext(IContextFile context) {
		ComplexContext ctx;
		if (!contexts.containsKey(context.getHandleIdentifier())) {
			ctx =  new ComplexContext(context);
			contexts.put(context.getHandleIdentifier(), ctx);
		}
		ctx = contexts.get(context.getHandleIdentifier());
		// get all contexts, that this contexts extends (all abstract machines)
		try {
			IExtendsContext[] exts;
			exts = context.getExtendsClauses();
			for (int j = 0; j < exts.length; j++) {
				IExtendsContext ext = exts[j];
				IContextFile extCtx = ext.getAbstractSCContext().getContextFile();
				if (!contexts.containsKey(extCtx.getHandleIdentifier())) {
					processContext(extCtx);
				}
				ComplexContext cplxCtx = contexts.get(extCtx.getHandleIdentifier());
				// don't allow cycles!
				if (!cplxCtx.getAncestors().contains(ctx)) {
					cplxCtx.addExtendedByContext(ctx);
					ctx.addExtends(cplxCtx);
				}
				
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 
	 * @return 	all machines of this project marked as root plus for each their longest branch.
	 */
	public ComplexMachine[] getRootMachines(){
		List<ComplexMachine> results = new LinkedList<ComplexMachine>();

		for (Iterator<ComplexMachine> iterator = machines.values().iterator(); iterator.hasNext();) {
			ComplexMachine machine = iterator.next();
			if (machine.isRoot()){
				results.addAll(machine.getLongestMachineBranch());
			}
			
		}
		return results.toArray(new ComplexMachine[results.size()]);
	}

	public LinkedList<ComplexMachine> findUnrootedMachines(){
		LinkedList<ComplexMachine> results =  new LinkedList<ComplexMachine>();
		return results;
	}
	
	/**
	 * 
	 * @return 	all contexts of this project that are not connected to a machine.
	 */
	public ComplexContext[] getDisconnectedContexts(){
		List<ComplexContext> results = new LinkedList<ComplexContext>();

		for (Iterator<ComplexContext> iterator = contexts.values().iterator(); iterator.hasNext();) {
			ComplexContext context = iterator.next();
			if (context.isNotSeen() && context.isRoot()){
				results.addAll(context.getLongestContextBranch());
			}
			
		}
		return results.toArray(new ComplexContext[results.size()]);
	}
	
	
	public ComplexMachine getMachine(String identifier) {
		return machines.get(identifier);
	}

	public void putMachine(String identifier, ComplexMachine machine) {
		machines.put(identifier, machine);
	}
	
	public boolean hasMachine(String identifier) {
		return machines.containsKey(identifier);
	}

	public ComplexContext getContext(String identifier) {
		return contexts.get(identifier);
	}

	public void putContext(String identifier, ComplexContext context) {
		contexts.put(identifier, context);
	}
	
	public boolean hasContext(String identifier) {
		return contexts.containsKey(identifier);
	}

	
	private HashMap<String, ComplexMachine> machines = new HashMap<String, ComplexMachine>();
	private HashMap<String, ComplexContext> contexts = new HashMap<String, ComplexContext>();

}
