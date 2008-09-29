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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * 
 * Represents a Machine in the model
 *
 */
public class ModelMachine extends ModelPOContainer implements IModelElement {
	
	private List<ModelContext> seesContexts = new LinkedList<ModelContext>();
	/**
	 * Machines that refine this Machine.
	 */
	private List<ModelMachine> refinedByMachines = new LinkedList<ModelMachine>();
	/**
	 * Machines that this Machine refines.
	 */
	
	private List<ModelMachine> refinesMachines = new LinkedList<ModelMachine>();
	private IMachineFile internalMachine ;
	private HashMap<String, ModelInvariant> invariants = new HashMap<String, ModelInvariant>();
	private HashMap<String, ModelEvent> events = new HashMap<String, ModelEvent>();
	private HashMap<String, ModelTheorem> theorems = new HashMap<String, ModelTheorem>();
	/**
	 * All machines that are above this machine in the refinement tree. 
	 * (= machines that are refined by this machines)
	 */
	private ArrayList<ModelMachine> ancestors =  new ArrayList<ModelMachine>();
	/**
	 * The longest branch of machines that refine this machine.
	 * (including this machine)
	 * The value of this is calculated in the ModelProject by calling
	 * <code>calculateMachineBranches()</code>.
	 */
	private ArrayList<ModelMachine> longestRefineBranch =  new ArrayList<ModelMachine>();
	
	public ModelElementNode[] nodes;
	//TODO add variables etc?


	/**
	 * Creates a ModelMachine from a given IMachineFile
	 * @param file	The MachineFile that this ModelMachine is based on.
	 */
	public ModelMachine(IMachineFile file){
		internalMachine = file;
		nodes = new ModelElementNode[5];
		nodes[0] = new ModelElementNode(IVariable.ELEMENT_TYPE, this);
		nodes[1] = new ModelElementNode(IInvariant.ELEMENT_TYPE, this);
		nodes[2] = new ModelElementNode(ITheorem.ELEMENT_TYPE, this);
		nodes[3] = new ModelElementNode(IEvent.ELEMENT_TYPE, this);
	}
	
	/**
	 * @return The Project that contains this Machine.
	 */
	@Override
	public IModelElement getParent() {
		return ModelController.getProject(internalMachine.getRodinProject());
	}
	
	public void addAncestor(ModelMachine machine){
		if (!ancestors.contains(machine)) {
			ancestors.add(machine);
		}
	}
	
	public void addAncestors(ArrayList<ModelMachine> machines){
		ancestors.addAll(machines);
	}

	public void resetAncestors(){
		ancestors = new ArrayList<ModelMachine>();
	}

	public ArrayList<ModelMachine> getAncestors(){
		return ancestors;
	}

	public void addToLongestBranch(ModelMachine machine){
		if (!longestRefineBranch.contains(machine)) {
			longestRefineBranch.add(machine);
		}
	}
	
	public void addToLongestBranch(ArrayList<ModelMachine> machines){
		longestRefineBranch.addAll(machines);
	}

	public void removeFromLongestBranch(ModelMachine machine){
		longestRefineBranch.remove(machine);
	}

	/**
	 * <code>calculateMachineBranches()</code> has to be called before this, 
	 * to get a result that is up to date.
	 * 
	 * @return The longest branch of children (machines that refine this machine)
	 * 			including this machine.
	 */
	public ArrayList<ModelMachine> getLongestBranch(){
		System.out.println("LongestBranch machines of " +this +": " +longestRefineBranch);
		return longestRefineBranch;
	}
	
	public void setLongestBranch(ArrayList<ModelMachine> branch){
		longestRefineBranch = branch;
	}
	

	public void processChildren() {
		//clear existing children
		invariants.clear();
		events.clear();
		theorems.clear();
		try {
			IInvariant[] invs = internalMachine.getChildrenOfType(IInvariant.ELEMENT_TYPE);
			for (int i = 0; i < invs.length; i++) {
				addInvariant(invs[i]);
			}
			IEvent[] evts = internalMachine.getChildrenOfType(IEvent.ELEMENT_TYPE);
			for (int i = 0; i < evts.length; i++) {
				addEvent(evts[i]);
			}
			ITheorem[] thms = internalMachine.getChildrenOfType(ITheorem.ELEMENT_TYPE);
			for (int i = 0; i < thms.length; i++) {
				addTheorem(thms[i]);
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Processes the POFile that belongs to this machine.
	 * It creates a ModelProofObligation for each sequent
	 * and adds it to this machines as well as to the
	 * concerned Invariants, Theorems and Events.
	 */
	public void processPOFile() {
		try {
			IPOFile file = internalMachine.getPOFile();
			IPOSequent[] sequents = file.getSequents();
			for (int i = 0; i < sequents.length; i++) {
				IPOSequent sequent =  sequents[i];
				ModelProofObligation po = new ModelProofObligation(sequent);
				po.setMachine(this);
				proofObligations.put(sequent.getElementName(), po);
	
				IPOSource[] sources = sequents[i].getSources();
				for (int j = 0; j < sources.length; j++) {
					IRodinElement source = sources[j].getSource();
					if (source instanceof IInvariant) {
						if (invariants.containsKey(((IInvariant) source).getElementName())) {
							ModelInvariant inv = invariants.get(((IInvariant) source).getElementName());
							po.addInvariant(inv);
							inv.addProofObligation(po);
						}
					}
					if (source instanceof ITheorem) {
						if (theorems.containsKey(((ITheorem) source).getElementName())) {
							ModelTheorem thm = theorems.get(((ITheorem) source).getElementName());
							po.addTheorem(thm);
							thm.addProofObligation(po);
						}
					}
					if (source instanceof IEvent) {
						if (events.containsKey(((IEvent) source).getElementName())) {
							ModelEvent evt = events.get(((IEvent) source).getElementName());
							po.addEvent(evt);
							evt.addProofObligation(po);
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
	 * Processes the PSFile that belongs to this Machine.
	 * Each status is added to the corresponding Proof Obligation,
	 * if that ProofObligation is present.
	 */
	public void processPSFile() {
		try {
			IPSFile file = internalMachine.getPSFile();
			IPSStatus[] stats = file.getStatuses();
			for (int i = 0; i < stats.length; i++) {
				IPSStatus status = stats[i];
				IPOSequent sequent = status.getPOSequent();
				// check if there is a ProofObligation for this status (there should be one!)
				if (proofObligations.containsKey(sequent.getElementName())) {
					proofObligations.get(sequent.getElementName()).setIPSStatus(status);
				}
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a new ModelInvariant to this Machine.
	 * @param invariant The Invariant to add.
	 */
	public void addInvariant(IInvariant invariant) {
		invariants.put(invariant.getElementName(), new ModelInvariant(invariant, this));
	}

	/**
	 * Adds a new ModelEvent to this Machine.
	 * @param event The Event to add.
	 */
	public void addEvent(IEvent event) {
		events.put(event.getElementName(), new ModelEvent(event, this));
	}
	
	/**
	 * Adds a new ModelTheorem to this Machine.
	 * @param theorem The Theorem to add.
	 */
	public void addTheorem(ITheorem theorem) {
		theorems.put(theorem.getElementName(), new ModelTheorem(theorem, this));
	}

	/**
	 * 
	 * @return is this Machine a root of a tree of Machines?
	 * (= refines no other machine)
	 */
	public boolean isRoot(){
		return (refinesMachines.size() ==0);
	}
	
	/**
	 * 
	 * @return is this Machine a leaf of a tree of Machines?
	 * (= is refined by no other machine)
	 */
	public boolean isLeaf(){
		return (refinedByMachines.size() ==0);
	}
	
	
	/**
	 * 
	 * @return All the refinedByMachines, that are not returned by getLongestBranch
	 */
	public List<ModelMachine> getRestMachines(){
		System.out.println();
		List<ModelMachine> copy = new LinkedList<ModelMachine>(refinedByMachines);
		copy.removeAll(getLongestBranch());
		System.out.println("Rest machines of " +this +": " +copy);
		return copy;
	}
	
	public List<ModelMachine> getRefinesMachines() {
		return refinesMachines;
	}
	
	public void addRefinesMachine(ModelMachine machine) {
		//only add new Machines
		if (!refinesMachines.contains(machine)) {
			refinesMachines.add(machine);
		}
	}

	public void resetRefinesMachine() {
		refinesMachines  = new LinkedList<ModelMachine>();
	}
	
	public void removeRefinesMachine(ModelMachine machine) {
		refinesMachines.remove(machine);
	}
	
	public List<ModelMachine> getRefinedByMachines() {
		return refinedByMachines;
	}

	public void addRefinedByMachine(ModelMachine machine) {
		//only add new Machines
		if (!refinedByMachines.contains(machine)) {
			refinedByMachines.add(machine);
		}
	}

	public void removeRefinedByMachine(ModelMachine machine) {
		refinedByMachines.remove(machine);
	}
	
	public void addSeesContext(ModelContext context) {
		//only add new Contexts
		if (!seesContexts.contains(context)) {
			seesContexts.add(context);
		}
	}

	public void removeSeesContext(ModelContext context) {
		seesContexts.remove(context);
	}
	
	public void resetSeesContext() {
		seesContexts  = new LinkedList<ModelContext>();
	}

	public IMachineFile getInternalMachine() {
		return internalMachine;
	}

	public List<ModelContext> getSeesContexts() {
		return seesContexts;
	}

	public ModelInvariant getInvariant(IInvariant invariant){
		return invariants.get(invariant.getElementName());
	}
	
	public ModelEvent getEvent(IEvent event){
		return events.get(event.getElementName());
	}
	

	public ModelTheorem getTheorem(ITheorem theorem){
		return theorems.get(theorem.getElementName());
	}

	@Override
	public String toString(){
		return ("ModelMachine: "+internalMachine.getBareName());
	}

}
