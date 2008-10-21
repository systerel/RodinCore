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

import org.eventb.core.EventBPlugin;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISeesContext;
import org.eventb.core.ITheorem;
import org.rodinp.core.IRodinElement;
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
	//indicates whether to projects needs to be processed freshly (process Machines etc.)
	public boolean needsProcessing =  true;


	public ModelProject(IRodinProject project) {
		internalProject = project;
	}
	
	/**
	 * Process an IMachineRoot. Creates an ModelMachine for this IMachineRoot
	 * Creates dependencies between ModelMachines (refines) and ModelContexts (sees)
	 * May recursively call processMachine(), if a machine in dependency was not processed yet.
	 * Cycles in the dependencies are not taken into the Model!
	 * @param machine	The IMachineRoot to process.
	 */
	public void processMachine(IMachineRoot machine) {
		ModelMachine mach;
		if (!machines.containsKey(machine.getElementName())) {
			mach =  new ModelMachine(machine);
			machines.put(machine.getElementName(), mach);
		} else {
			mach = machines.get(machine.getElementName());
			//remove existing dependencies. They will be calculated here freshly
			removeMachineDependencies(mach);
			mach.resetRefinesMachine();
			mach.resetSeesContext();
			mach.resetAncestors();
			
		}
		mach.processChildren();
		mach.poNeedsProcessing = true;
		mach.psNeedsProcessing = true;
		try {
			// get all machines, that this machine refines (all abstract machines, usually just 1)
			for (IRefinesMachine refine : machine.getRefinesClauses()){
				final IMachineRoot abst = (IMachineRoot) refine.getAbstractMachine().getRoot();
				// May not exist, if there are some errors in the project (e.g. was deleted)
				if (abst.exists()) {
					if (!machines.containsKey(abst.getElementName())) {
						processMachine(abst);
					}
					ModelMachine abstMach = machines.get(abst.getElementName());
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
				IContextRoot ctx = ((ISCContextRoot) see.getSeenSCContext().getRoot()).getContextRoot();
				// May not exist, if there are some errors in the project (e.g. was deleted)
				if (ctx.exists()) {
					if (!contexts.containsKey(ctx.getElementName())) {
						processContext(ctx);
					}
					ModelContext cplxCtxt = contexts.get(ctx.getElementName());
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
	 * Process an IContextRoot. Creates an ModelContext for this IContextRoot
	 * Creates dependencies betweenModelContexts (extends)
	 * May recursively call processContext(), if a context in dependency was not processed yet.
	 * Cycles in the dependencies are not taken into the Model!
	 * @param context	The IContextRoot to process.
	 */
	public void processContext(IContextRoot context) {
		ModelContext ctx;
		if (!contexts.containsKey(context.getElementName())) {
			ctx =  new ModelContext(context);
			contexts.put(context.getElementName(), ctx);
		} else {
			ctx = contexts.get(context.getElementName());
			//remove existing dependencies. They will be calculated here freshly
			removeContextDependencies(ctx);
			ctx.resetExtendsContexts();
			ctx.resetAncestors();
		}
		ctx.processChildren();
		ctx.poNeedsProcessing = true;
		ctx.psNeedsProcessing = true;
		// get all contexts, that this contexts extends 
		try {
			IExtendsContext[] exts;
			exts = context.getExtendsClauses();
			for (int j = 0; j < exts.length; j++) {
				IExtendsContext ext = exts[j];
				if (ext.getAbstractSCContext() != null) {
					IRodinFile file = internalProject.getRodinFile(EventBPlugin.getContextFileName(ext.getAbstractContextName()));
					IContextRoot extCtx =  (IContextRoot) file.getRoot();
//					IContextRoot extCtx = ext.getAbstractSCContext().getContextRoot();
					// May not exist, if there are some errors in the project (e.g. was deleted)
					if (extCtx.exists()) {
						if (!contexts.containsKey(extCtx.getContextRoot())) {
							processContext(extCtx);
						}
						ModelContext exendsCtx = contexts.get(extCtx.getElementName());
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
		IEventBRoot root = (IEventBRoot) invariant.getRodinFile().getRoot();
		if (root instanceof IMachineRoot) {
			ModelMachine machine = machines.get(root.getElementName());
			if (machine != null) {
				return machine.getInvariant(invariant);
			}
		}
		return null;
	}
	
	public ModelEvent getEvent(IEvent event){
		IEventBRoot root = (IEventBRoot) event.getRodinFile().getRoot();
		if (root instanceof IMachineRoot) {
			ModelMachine machine = machines.get(root.getElementName());
			if (machine != null) {
				return machine.getEvent(event);
			}
		}
		return null;
	}

	public ModelTheorem getTheorem(ITheorem theorem){
		IEventBRoot root = (IEventBRoot) theorem.getRodinFile().getRoot();
		if (root instanceof IMachineRoot) {
			ModelMachine machine = machines.get(root.getElementName());
			if (machine != null) {
				return machine.getTheorem(theorem);
			}
		}
		if (root instanceof IContextRoot) {
			ModelContext context = contexts.get(root.getElementName());
			if (context != null) {
				return context.getTheorem(theorem);
			}
		}
		return null;
	}
	
	public ModelAxiom getAxiom(IAxiom axiom){
		IEventBRoot root = (IEventBRoot) axiom.getRodinFile().getRoot();
		if (root instanceof IContextRoot) {
			ModelContext context = contexts.get(root.getElementName());
			if (context != null) {
				return context.getAxiom(axiom);
			}
		}
		return null;
	}
	
	public ModelProofObligation getProofObligation(IPSStatus status){
		IEventBRoot root = (IEventBRoot) status.getRodinFile().getRoot();
		if (root instanceof IPSRoot) {
			ModelMachine machine = machines.get(root.getElementName());
			if (machine != null) {
				return machine.getProofObligation(status);
			}
			
			ModelContext context = contexts.get(root.getElementName());
			if (context != null) {
				return context.getProofObligation(status);
			}
		}
		return null;
	}
	
	
	public IModelElement getModelParent() {
		//The Project doesn't have a ModelElement parent
		return null;
	}

	/**
	 * @return the total number of Proof Obligations of this project
	 */
	public int getPOcount(){
		int result = 0;
		for (ModelMachine machine : machines.values()) {
			result += machine.getPOcount();
		}
		for (ModelContext context : contexts.values()) {
			result += context.getPOcount();
		}
		return result;
	}
	
	/**
	 * @return The number of undischarged Proof Obligations of this project
	 */
	public int getUndischargedPOcount() {
		int result = 0;
		for (ModelMachine machine : machines.values()) {
			result += machine.getUndischargedPOcount();
		}
		for (ModelContext context : contexts.values()) {
			result += context.getUndischargedPOcount();
		}
		return result;
		
	}

	/**
	 * @return The number of manually discharged Proof Obligations of this project
	 */
	public int getManuallyDischargedPOcount() {
		int result = 0;
		for (ModelMachine machine : machines.values()) {
			result += machine.getManuallyDischargedPOcount();
		}
		for (ModelContext context : contexts.values()) {
			result += context.getManuallyDischargedPOcount();
		}
		return result;
	}

	/**
	 * @return The number of manually discharged Proof Obligations of this project
	 */
	public int getReviewedPOcount() {
		int result = 0;
		for (ModelMachine machine : machines.values()) {
			result += machine.getReviewedPOcount();
		}
		for (ModelContext context : contexts.values()) {
			result += context.getReviewedPOcount();
		}
		return result;
		
	}

	public IRodinElement getInternalElement() {
		return internalProject;
	}
	
	
}
