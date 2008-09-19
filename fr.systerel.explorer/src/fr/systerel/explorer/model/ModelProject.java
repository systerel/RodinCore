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


package fr.systerel.explorer.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.IAxiom;
import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.ITheorem;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * Represents a RodinProject in the Model
 *
 */
public class ModelProject implements IModelElement {
	public ModelProject(IRodinProject project) {
		internalProject = project;
	}
	private IRodinProject internalProject;
	
	/**
	 * Process an IMachineFile. Creates an ModelMachine for this IMachineFile
	 * Creates dependencies between ModelMachines (refines) and ModelContexts (sees)
	 * May recursively call processMachine(), if a machine in dependency was not processed yet.
	 * Cycles in the dependencies are not taken into the Model!
	 * @param machine	The IMachineFile to process.
	 */
	public void processMachine(IMachineFile machine) {
		ModelMachine mach;
		if (!machines.containsKey(machine.getBareName())) {
			mach =  new ModelMachine(machine);
			machines.put(machine.getBareName(), mach);
		}
		mach = machines.get(machine.getBareName());
		try {
			// get all machines, that this machine refines (all abstract machines)
			IRefinesMachine[] refines;
			refines = machine.getRefinesClauses();
			for (int j = 0; j < refines.length; j++) {
				IRefinesMachine refine = refines[j];
				IMachineFile abst = refine.getAbstractMachine();
				if (!machines.containsKey(abst.getBareName())) {
					processMachine(abst);
				}
				ModelMachine cplxMach = machines.get(abst.getBareName());
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
				if (!contexts.containsKey(ctx.getBareName())) {
					processContext(ctx);
				}
				ModelContext cplxCtxt = contexts.get(ctx.getBareName());
				cplxCtxt.addSeenByMachine(mach);
				mach.addSeesContext(cplxCtxt);
				
			}
			
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		
	}

	/**
	 * Process an IContextFile. Creates an ModelContext for this IContextFile
	 * Creates dependencies betweenModelContexts (extends)
	 * May recursively call processContext(), if a context in dependency was not processed yet.
	 * Cycles in the dependencies are not taken into the Model!
	 * @param context	The IContextFile to process.
	 */
	public void processContext(IContextFile context) {
		ModelContext ctx;
		if (!contexts.containsKey(context.getBareName())) {
			ctx =  new ModelContext(context);
			contexts.put(context.getBareName(), ctx);
		}
		ctx = contexts.get(context.getBareName());
		// get all contexts, that this contexts extends 
		try {
			IExtendsContext[] exts;
			exts = context.getExtendsClauses();
			for (int j = 0; j < exts.length; j++) {
				IExtendsContext ext = exts[j];
				IContextFile extCtx = ext.getAbstractSCContext().getContextFile();
				if (!contexts.containsKey(extCtx.getBareName())) {
					processContext(extCtx);
				}
				ModelContext cplxCtx = contexts.get(extCtx.getBareName());
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
	public ModelMachine[] getRootMachines(){
		List<ModelMachine> results = new LinkedList<ModelMachine>();

		for (Iterator<ModelMachine> iterator = machines.values().iterator(); iterator.hasNext();) {
			ModelMachine machine = iterator.next();
			if (machine.isRoot()){
				results.addAll(machine.getLongestMachineBranch());
			}
			
		}
		return results.toArray(new ModelMachine[results.size()]);
	}

	
	/**
	 * 
	 * @return 	all contexts of this project that are not connected to a machine.
	 */
	public ModelContext[] getDisconnectedContexts(){
		List<ModelContext> results = new LinkedList<ModelContext>();

		for (Iterator<ModelContext> iterator = contexts.values().iterator(); iterator.hasNext();) {
			ModelContext context = iterator.next();
			if (context.isNotSeen() && context.isRoot()){
				results.addAll(context.getLongestContextBranch());
			}
			
		}
		return results.toArray(new ModelContext[results.size()]);
	}
	
	
	public ModelMachine getMachine(String identifier) {
		return machines.get(identifier);
	}

	public void putMachine(String identifier, ModelMachine machine) {
		machines.put(identifier, machine);
	}
	
	public boolean hasMachine(String identifier) {
		return machines.containsKey(identifier);
	}

	public ModelContext getContext(String identifier) {
		return contexts.get(identifier);
	}

	public void putContext(String identifier, ModelContext context) {
		contexts.put(identifier, context);
	}
	
	public boolean hasContext(String identifier) {
		return contexts.containsKey(identifier);
	}
	
	public ModelInvariant getInvariant(IInvariant invariant){
		IRodinFile file = invariant.getRodinFile();
		if (file instanceof IMachineFile) {
			ModelMachine machine = machines.get(file.getBareName());
			if (machine != null) {
				return machine.getInvariant(invariant);
			}
		}
		return null;
	}
	
	public ModelEvent getEvent(IEvent event){
		IRodinFile file = event.getRodinFile();
		if (file instanceof IMachineFile) {
			ModelMachine machine = machines.get(file.getBareName());
			if (machine != null) {
				return machine.getEvent(event);
			}
		}
		return null;
	}

	public ModelTheorem getTheorem(ITheorem theorem){
		IRodinFile file = theorem.getRodinFile();
		if (file instanceof IMachineFile) {
			ModelMachine machine = machines.get(file.getBareName());
			if (machine != null) {
				return machine.getTheorem(theorem);
			}
		}
		if (file instanceof IContextFile) {
			ModelContext context = contexts.get(file.getBareName());
			if (context != null) {
				return context.getTheorem(theorem);
			}
		}
		return null;
	}
	
	public ModelAxiom getAxiom(IAxiom axiom){
		IRodinFile file = axiom.getRodinFile();
		if (file instanceof IContextFile) {
			ModelContext context = contexts.get(file.getBareName());
			if (context != null) {
				return context.getAxiom(axiom);
			}
		}
		return null;
	}
	
	public IModelElement getParent() {
		//The Project doesn't have a ModelElement parent
		return null;
	}

	
	private HashMap<String, ModelMachine> machines = new HashMap<String, ModelMachine>();
	private HashMap<String, ModelContext> contexts = new HashMap<String, ModelContext>();

}
