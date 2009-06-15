/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
import org.eventb.core.ISeesContext;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * Represents a RodinProject in the Model. Contains Machines and Context.
 * 
 */
public class ModelProject implements IModelElement {
	
	private HashMap<IMachineRoot, ModelMachine> machines = new HashMap<IMachineRoot, ModelMachine>();
	private HashMap<IContextRoot, ModelContext> contexts = new HashMap<IContextRoot, ModelContext>();
	private IRodinProject internalProject;
	//indicates whether to projects needs to be processed freshly (process Machines etc.)
	public boolean needsProcessing =  true;


	public ModelProject(IRodinProject project) {
		internalProject = project;
	}
	
	/**
	 * Process an IMachineRoot. Creates an ModelMachine for this IMachineRoot
	 * Creates dependencies between ModelMachines (refines) and ModelContexts
	 * (sees) May recursively call <code>processMachine()</code>, if a
	 * machine in dependency was not processed yet. Similarly it may call
	 * <code>processContext()</code> for contexts that are seen by this
	 * machine, but not processed yet. Cycles in the dependencies are not taken
	 * into the Model!
	 * 
	 * @param machine
	 *            The IMachineRoot to process.
	 */
	public void processMachine(IMachineRoot machine) {
		ModelMachine mach;
		if (!machines.containsKey(machine)) {
			mach =  new ModelMachine(machine);
			machines.put(machine, mach);
		} else {
			mach = machines.get(machine);
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
				processRefines(refine, mach);
				
			}
			// get all contexts, that this machine sees
			ISeesContext[] sees =  machine.getSeesClauses();
			for (ISeesContext see : sees) {
				processSees(see, mach);
			}
			
		} catch (RodinDBException e) {
			UIUtils.log(e, "when processing machine " +machine);
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
	 * Creates dependencies betweenModelContexts (extends) May recursively call
	 * processContext(), if a context in dependency was not processed yet.
	 * Cycles in the dependencies are not taken into the Model!
	 * 
	 * @param context
	 *            The IContextRoot to process.
	 */
	public void processContext(IContextRoot context) {
		ModelContext ctx;
		if (!contexts.containsKey(context)) {
			ctx =  new ModelContext(context);
			contexts.put(context, ctx);
		} else {
			ctx = contexts.get(context);
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
			for (IExtendsContext ext: exts) {
				processExtends(ext, ctx);
			}
		} catch (RodinDBException e) {
			UIUtils.log(e, "when processing context " +context);
		}
		
	}

	/**
	 * calculates the longestExtendsBranch for all context of this project.
	 * 
	 * In some cases the calculated branch may be not the very longest.
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
						// A context can extend more than one context.
						// Here we take the first to make it simpler and faster.
						// in some cases this might result in branch
						// that is not actually the longest one.
						// But that is not so grave, since it only influences
						// the presentation of the contexts.
						parent = parent.getExtendsContexts().get(0);
					} else parent = null;
				}
			}
		}
	}
	
	
	
	/**
	 * 
	 * @return all machines of this project marked as root plus for each their
	 *         longest branch.
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
	 * @return all contexts of this project marked as root plus for each their
	 *         longest branch.
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
	
	
	public ModelMachine getMachine(IMachineRoot machine) {
		return machines.get(machine);
	}

	public void putMachine(IMachineRoot root, ModelMachine machine) {
		machines.put(root, machine);
	}
	
	
	/**
	 * Removes a machine from this project. Also removes dependencies
	 * 
	 * @param machineRoot
	 *            An identifier for the machine to remove
	 */
	public void removeMachine(IMachineRoot machineRoot) {
		ModelMachine machine = machines.get(machineRoot);
		if (machine != null) {
			removeMachineDependencies(machine);
			//other machines also can't refine this machine anymore, because it won't exist any longer
			for(ModelMachine mach : machine.getRefinedByMachines()) {
				mach.removeRefinesMachine(machine);
			}
			machines.remove(machineRoot);
		}
	}
	
	/**
	 * Removes dependencies of this machine: Removes this machine from all
	 * contexts that it sees and all machine that it refines
	 */
	public void removeMachineDependencies(ModelMachine machine){
		for(ModelContext ctx : machine.getSeesContexts()) {
			ctx.removeSeenByMachine(machine);
		}
		for(ModelMachine mach : machine.getRefinesMachines()) {
			mach.removeRefinedByMachine(machine);
		}
	}

	public ModelContext getContext(IContextRoot context) {
		return contexts.get(context);
	}

	public void putContext(IContextRoot contextRoot, ModelContext context) {
		contexts.put(contextRoot, context);
	}
	
	
	/**
	 * Removes a context from this project. Also removes dependencies
	 * 
	 * @param contextRoot
	 *            The contextRoot corresponding to the context to be removed
	 */
	public void removeContext(IContextRoot contextRoot) {
		ModelContext context =  contexts.get(contextRoot);
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
			contexts.remove(contextRoot);
		}
	}
	
	/**
	 * Removes dependencies of this context: Removes this machine from all
	 * contexts that it extends
	 */
	public void removeContextDependencies(ModelContext context){
		for(ModelContext ctx : context.getExtendsContexts()) {
			ctx.removeExtendedByContext(context);
		}
	}
	
	
	public ModelInvariant getInvariant(IInvariant invariant){
		IEventBRoot root = (IEventBRoot) invariant.getRoot();
		if (root instanceof IMachineRoot) {
			ModelMachine machine = machines.get(root);
			if (machine != null) {
				return machine.getInvariant(invariant);
			}
		}
		return null;
	}
	
	public ModelEvent getEvent(IEvent event){
		IEventBRoot root = (IEventBRoot) event.getRoot();
		if (root instanceof IMachineRoot) {
			ModelMachine machine = machines.get(root);
			if (machine != null) {
				return machine.getEvent(event);
			}
		}
		return null;
	}

	public ModelAxiom getAxiom(IAxiom axiom){
		IEventBRoot root = (IEventBRoot) axiom.getRoot();
		if (root instanceof IContextRoot) {
			ModelContext context = contexts.get(root);
			if (context != null) {
				return context.getAxiom(axiom);
			}
		}
		return null;
	}
	
	public ModelProofObligation getProofObligation(IPSStatus status){
		IEventBRoot root = (IEventBRoot) status.getRoot();
		if (root instanceof IPSRoot) {
			
			ModelMachine machine = machines.get(root.getMachineRoot());
			if (machine != null) {
				return machine.getProofObligation(status);
			}
			
			ModelContext context = contexts.get(root.getContextRoot());
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
	
	/**
	 * Processes a refines clause belonging to a given <code>ModelMachine</code>.
	 * 
	 * @param refinesClause
	 *            The refines clause to process
	 * @param machine
	 *            The <code>ModelMachine</code> this clause comes from.
	 * @throws RodinDBException
	 */
	protected void processRefines(IRefinesMachine refinesClause, ModelMachine machine) throws RodinDBException {
		if (!refinesClause.hasAbstractMachineName())
			return;
		final IMachineRoot abst = refinesClause.getAbstractMachineRoot();
		// May not exist, if there are some errors in the project (e.g. was deleted)
		if (abst.exists()) {
			if (!machines.containsKey(abst)) {
				processMachine(abst);
			}
			ModelMachine abstMach = machines.get(abst);
			// don't allow cycles
			if (!abstMach.getAncestors().contains(machine)) {
				abstMach.addRefinedByMachine(machine);
				machine.addRefinesMachine(abstMach);
				machine.addAncestor(abstMach);
				machine.addAncestors(abstMach.getAncestors());
			}
		}
		
	}
	
	protected void processSees(ISeesContext seesClause, ModelMachine machine) throws RodinDBException {
		if (!seesClause.hasSeenContextName())
			return;
		final IContextRoot ctx = seesClause.getSeenContextRoot();
		// May not exist, if there are some errors in the project (e.g. was deleted)
		if (ctx.exists()) {
			if (!contexts.containsKey(ctx)) {
				processContext(ctx);
			}
			ModelContext cplxCtxt = contexts.get(ctx);
			cplxCtxt.addSeenByMachine(machine);
			machine.addSeesContext(cplxCtxt);
		}
		
	}
	
	protected void processExtends(IExtendsContext extendsClause, ModelContext context) throws RodinDBException {
		if (!extendsClause.hasAbstractContextName())
			return;
		final IContextRoot extCtx = extendsClause.getAbstractContextRoot();
		// May not exist, if there are some errors in the project (e.g. was deleted)
		if (extCtx.exists()) {
			if (!contexts.containsKey(extCtx)) {
				processContext(extCtx);
			}
			ModelContext exendsCtx = contexts.get(extCtx);
			// don't allow cycles!
			if (!exendsCtx.getAncestors().contains(context)) {
				exendsCtx.addExtendedByContext(context);
				context.addExtendsContext(exendsCtx);
				context.addAncestor(exendsCtx);
				context.addAncestors(exendsCtx.getAncestors());
			} else {
				if (ExplorerUtils.DEBUG) {
					System.out.println("Context is in cycle: " +context);
				}
			}
		}
	}

	public IModelElement getModelElement(IRodinElement element) {
		if (element instanceof IMachineRoot) {
			return machines.get(element);
		}
		if (element instanceof IContextRoot) {
			return contexts.get(element);
		}
		IEventBRoot parent= element.getAncestor(IMachineRoot.ELEMENT_TYPE);
		ModelMachine machine = machines.get(parent);
		if (machine != null) {
			return machine.getModelElement(element);
		}
		parent= element.getAncestor(IContextRoot.ELEMENT_TYPE);
		ModelContext context = contexts.get(parent);
		if (context != null) {
			return context.getModelElement(element);
		}
		
		return null;
		
	}

	/**
	 * Always returns <code>null</code> since projects don't have a parent in
	 * the model.
	 */
	public IModelElement getParent(boolean complex) {
		return null;
	}

	
	public Object[] getChildren(IInternalElementType<?> type, boolean complex) {
		if (!complex) {
			try {
				if (type == IMachineRoot.ELEMENT_TYPE) {
					return ExplorerUtils.getMachineRootChildren(internalProject);
				}
				if (type == IContextRoot.ELEMENT_TYPE) {
					return ExplorerUtils.getContextRootChildren(internalProject);
				}
			} catch (RodinDBException e) {
				UIUtils.log(e, "when getting machines or contexts of " +internalProject);
			}
		} else {
			if (type == IMachineRoot.ELEMENT_TYPE) {
				return ModelController.convertToIMachine(getRootMachines());
			}
			if (type == IContextRoot.ELEMENT_TYPE) {
				return ModelController.convertToIContext(getRootContexts());
			}
		}
		if (ExplorerUtils.DEBUG) {
			System.out.println("Did not find children of type: "+type +"for project " +internalProject);
		}
		return new Object[0];
	}
	
}
