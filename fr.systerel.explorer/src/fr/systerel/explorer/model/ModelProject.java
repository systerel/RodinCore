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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.IAxiom;
import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.ITheorem;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * Represents a RodinProject in the Model.
 * Contains Machines and Context.
 *
 */
public class ModelProject implements IModelElement {
	
	private HashMap<String, ModelMachine> machines = new HashMap<String, ModelMachine>();
	private HashMap<String, ModelContext> contexts = new HashMap<String, ModelContext>();
	private IRodinProject internalProject;


	public ModelProject(IRodinProject project) {
		internalProject = project;
	}
	
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
		} else {
			mach = machines.get(machine.getBareName());
			//remove existing dependencies. They will be calculated here freshly
			removeMachineDependencies(mach);
			mach.resetRefinesMachine();
			mach.resetSeesContext();
			mach.resetAncestors();
			
		}
		mach.processChildren();
		try {
			// get all machines, that this machine refines (all abstract machines, usually just 1)
			for (IRefinesMachine refine : machine.getRefinesClauses()){
				final IMachineFile abst = refine.getAbstractMachine();
				// May not exist, if there are some errors in the project (e.g. was deleted)
				if (abst.exists()) {
					if (!machines.containsKey(abst.getBareName())) {
						processMachine(abst);
					}
					ModelMachine abstMach = machines.get(abst.getBareName());
					// don't allow cycles
					if (!abstMach.getAncestors().contains(mach)) {
						abstMach.addRefinedByMachine(mach);
						mach.addRefinesMachine(abstMach);
						mach.addAncestor(abstMach);
						mach.addAncestors(abstMach.getAncestors());
					}
				}
				
			}
			// get all contexts, that this machine sees
			ISeesContext[] sees =  machine.getSeesClauses();
			for (int j = 0; j < sees.length; j++) {
				ISeesContext see = sees[j];
				IContextFile ctx = see.getSeenSCContext().getContextFile();
				// May not exist, if there are some errors in the project (e.g. was deleted)
				if (ctx.exists()) {
					if (!contexts.containsKey(ctx.getBareName())) {
						processContext(ctx);
					}
					ModelContext cplxCtxt = contexts.get(ctx.getBareName());
					cplxCtxt.addSeenByMachine(mach);
					mach.addSeesContext(cplxCtxt);
				}
			}
			
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		
	}
	
	/**
	 * calculates the longestRefineBranch for all machines of this project
	 */
	public void calculateMachineBranches() {
		//reset previous calculations
		for (ModelMachine machine : machines.values()) {
			machine.setLongestBranch(new ArrayList<ModelMachine>());
		}
		for (ModelMachine machine : machines.values()) {
			if (machine.isLeaf()) {
				ArrayList<ModelMachine> branch =  new ArrayList<ModelMachine>();
				ModelMachine parent =  machine;
				while (parent != null) {
					//add to front
					branch.add(0, parent);
					if (parent.getLongestBranch().size() < branch.size()) {
						//give a copy, because this list might be changed in process
						parent.setLongestBranch(new ArrayList<ModelMachine>(branch));
					}
					if (parent.getRefinesMachines().size() > 0) {
						//usually there should be just 1, if there are more those are ignored here
						//in that case, Rodin outputs error messages already.
						parent = parent.getRefinesMachines().get(0);
					} else parent = null;
				}
			}
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
		} else {
			ctx = contexts.get(context.getBareName());
			//remove existing dependencies. They will be calculated here freshly
			removeContextDependencies(ctx);
			ctx.resetExtendsContexts();
			ctx.resetAncestors();
		}
		ctx.processChildren();
		//clear existing proof obligations
		ctx.proofObligations.clear();
		// get all contexts, that this contexts extends 
		try {
			IExtendsContext[] exts;
			exts = context.getExtendsClauses();
			for (int j = 0; j < exts.length; j++) {
				IExtendsContext ext = exts[j];
				if (ext.getAbstractSCContext() != null) {
					IContextFile extCtx = ext.getAbstractSCContext().getContextFile();
					// May not exist, if there are some errors in the project (e.g. was deleted)
					if (extCtx.exists()) {
						if (!contexts.containsKey(extCtx.getBareName())) {
							processContext(extCtx);
						}
						ModelContext exendsCtx = contexts.get(extCtx.getBareName());
						// don't allow cycles!
						if (!exendsCtx.getAncestors().contains(ctx)) {
							exendsCtx.addExtendedByContext(ctx);
							ctx.addExtendsContext(exendsCtx);
							ctx.addAncestor(exendsCtx);
							ctx.addAncestors(exendsCtx.getAncestors());
						}
					}
				}
				
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		
	}

	/**
	 * calculates the longestExtendsBranch for all context of this project
	 */
	public void calculateContextBranches() {
		//reset previous calculations
		for (ModelContext context : contexts.values()) {
			context.setLongestBranch(new ArrayList<ModelContext>());
		}
		for (ModelContext context : contexts.values()) {
			if (context.isLeaf()) {
				ArrayList<ModelContext> branch =  new ArrayList<ModelContext>();
				ModelContext parent =  context;
				while (parent != null) {
					//add to front
					branch.add(0, parent);
					if (parent.getLongestBranch().size() < branch.size()) {
						//give a copy, because this list might be changed in process
						parent.setLongestBranch(new ArrayList<ModelContext>(branch));
					}
					if (parent.getExtendsContexts().size() > 0) {
						//usually there should be just 1, if there are more those are ignored here
						//in that case, Rodin outputs error messages already.
						//TODO: this is not true for contexts. adapt!
						parent = parent.getExtendsContexts().get(0);
					} else parent = null;
				}
			}
		}
	}
	
	
	
	/**
	 * 
	 * @return 	all machines of this project marked as root plus for each their longest branch.
	 */
	public ModelMachine[] getRootMachines(){
		List<ModelMachine> results = new LinkedList<ModelMachine>();

		for (ModelMachine machine : machines.values()) {
			if (machine.isRoot()){
				results.addAll(machine.getLongestBranch());
			}
			
		}
		return results.toArray(new ModelMachine[results.size()]);
	}

	/**
	 * 
	 * @return 	all contexts of this project marked as root plus for each their longest branch.
	 */
	public ModelContext[] getRootContexts(){
		List<ModelContext> results = new LinkedList<ModelContext>();

		for (ModelContext context : contexts.values()) {
			if (context.isRoot()){
				results.addAll(context.getLongestBranch());
			}
			
		}
		return results.toArray(new ModelContext[results.size()]);
	}
	
	
	/**
	 * 
	 * @return 	all contexts of this project that are not connected to a machine.
	 */
	public ModelContext[] getDisconnectedContexts(){
		List<ModelContext> results = new LinkedList<ModelContext>();

		for (ModelContext context : contexts.values()) {
			if (context.isNotSeen() && context.isRoot()){
				results.addAll(context.getLongestBranch());
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
	
	/**
	 * Removes a machine from this project.
	 * Also removes dependencies
	 * @param identifier	An identifier for the machine to remove
	 */
	public void removeMachine(String identifier) {
		ModelMachine machine = machines.get(identifier);
		if (machine != null) {
			removeMachineDependencies(machine);
			//other machines also can't refine this machine anymore, because it won't exist any longer
			for(ModelMachine mach : machine.getRefinedByMachines()) {
				mach.removeRefinesMachine(machine);
			}
			machines.remove(identifier);
		}
	}
	
	/**
	 * Removes dependencies of this machine:
	 * Removes this machine from all contexts that it sees and all machine that it refines
	 */
	public void removeMachineDependencies(ModelMachine machine){
		for(ModelContext ctx : machine.getSeesContexts()) {
			ctx.removeSeenByMachine(machine);
		}
		for(ModelMachine mach : machine.getRefinesMachines()) {
			mach.removeRefinedByMachine(machine);
		}
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
	
	/**
	 * Removes a context from this project.
	 * Also removes dependencies
	 * @param identifier	An identifier for the context to remove
	 */
	public void removeContext(String identifier) {
		ModelContext context =  contexts.get(identifier);
		if (context != null) {
			removeContextDependencies(context);
			//other contexts also can't extend this context anymore, because it won't exist any longer
			for(ModelContext ctx : context.getExtendedByContexts()) {
				ctx.removeExtendsContext(context);
			}
			//machines also can't see this context anymore, because it won't exist any longer
			for(ModelMachine mach : context.getSeenByMachines()) {
				mach.removeSeesContext(context);
			}
			contexts.remove(identifier);
		}
	}
	
	/**
	 * Removes dependencies of this context:
	 * Removes this machine from all contexts that it extends
	 */
	public void removeContextDependencies(ModelContext context){
		for(ModelContext ctx : context.getExtendsContexts()) {
			ctx.removeExtendedByContext(context);
		}
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
	
	public ModelProofObligation getProofObligation(IPSStatus status){
		IRodinFile file = status.getRodinFile();
		if (file instanceof IPSFile) {
			ModelMachine machine = machines.get(file.getBareName());
			if (machine != null) {
				return machine.getProofObligation(status);
			}
			
			ModelContext context = contexts.get(file.getBareName());
			if (context != null) {
				return context.getProofObligation(status);
			}
		}
		return null;
	}
	
	
	public IModelElement getParent() {
		//The Project doesn't have a ModelElement parent
		return null;
	}

}
