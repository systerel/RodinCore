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

import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * 
 * Represents a Machine in the model
 *
 */
public class ModelMachine extends ModelPOContainer implements IModelElement {
	
	/**
	 * The nodes are used by the ContentProviders to present a node in the tree
	 * above elements such as Invariants or Theorems.
	 */
	public final ModelElementNode variable_node;
	public final ModelElementNode invariant_node;
	public final ModelElementNode theorem_node;
	public final ModelElementNode event_node;
	public final ModelElementNode po_node;
	
	private List<ModelContext> seesContexts = new LinkedList<ModelContext>();
	/**
	 * Machines that refine this Machine.
	 */
	private List<ModelMachine> refinedByMachines = new LinkedList<ModelMachine>();
	/**
	 * Machines that this Machine refines.
	 */
	private List<ModelMachine> refinesMachines = new LinkedList<ModelMachine>();
	private IMachineRoot internalMachine ;
	private HashMap<String, ModelInvariant> invariants = new HashMap<String, ModelInvariant>();
	private HashMap<String, ModelEvent> events = new HashMap<String, ModelEvent>();
	private HashMap<String, ModelTheorem> theorems = new HashMap<String, ModelTheorem>();
	//Variables are not taken into the model, because they don't have any proof obligations
	
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
	
	public boolean psNeedsProcessing = true;
	public boolean poNeedsProcessing = true;
	

	/**
	 * Creates a ModelMachine from a given IMachineRoot
	 * @param root	The MachineRoot that this ModelMachine is based on.
	 */
	public ModelMachine(IMachineRoot root){
		internalMachine = root;
		variable_node = new ModelElementNode(IVariable.ELEMENT_TYPE, this);
		invariant_node = new ModelElementNode(IInvariant.ELEMENT_TYPE, this);
		theorem_node = new ModelElementNode(ITheorem.ELEMENT_TYPE, this);
		event_node = new ModelElementNode(IEvent.ELEMENT_TYPE, this);
		po_node = new ModelElementNode(IPSStatus.ELEMENT_TYPE, this);
	}
	
	/**
	 * @return The Project that contains this Machine.
	 */
	@Override
	public IModelElement getModelParent() {
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
			for (IEvent evt :  evts) {
				addEvent(evt);
			}
			ITheorem[] thms = internalMachine.getChildrenOfType(ITheorem.ELEMENT_TYPE);
			for (ITheorem thm : thms) {
				addTheorem(thm);
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Processes the PORoot that belongs to this machine.
	 * It creates a ModelProofObligation for each sequent
	 * and adds it to this machines as well as to the
	 * concerned Invariants, Theorems and Events.
	 */
	public void processPORoot() {
		if (poNeedsProcessing) {
			try {
				//clear old POs
				proofObligations.clear();
				IPORoot root = internalMachine.getPORoot();
				if (root.exists()) {
					IPOSequent[] sequents = root.getSequents();
					for (IPOSequent sequent : sequents) {
						ModelProofObligation po = new ModelProofObligation(sequent);
						po.setMachine(this);
						proofObligations.put(sequent.getElementName(), po);
						IPOSource[] sources = sequent.getSources();
						for (int j = 0; j < sources.length; j++) {
							IRodinElement source = sources[j].getSource();
							//only process sources that belong to this machine.
							if (internalMachine.getRodinFile().isAncestorOf(source) ){
								if (source instanceof IAction) {
									source =source.getParent();
								}
								if (source instanceof IGuard ) {
									source = source.getParent();
								}
								if (source instanceof IWitness ) {
									source = source.getParent();
								}
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
					}
				}
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
	//			e.printStackTrace();
			}
			poNeedsProcessing = false;
		}
	}

	/**
	 * Processes the PSRoot that belongs to this Machine.
	 * Each status is added to the corresponding Proof Obligation,
	 * if that ProofObligation is present.
	 */
	public void processPSRoot() {
		if (psNeedsProcessing) {
			try {
				IPSRoot root = internalMachine.getPSRoot();
				if (root.exists()) {
					IPSStatus[] stats = root.getStatuses();
					for (int i = 0; i < stats.length; i++) {
						IPSStatus status = stats[i];
						IPOSequent sequent = status.getPOSequent();
						// check if there is a ProofObligation for this status (there should be one!)
						if (proofObligations.containsKey(sequent.getElementName())) {
							proofObligations.get(sequent.getElementName()).setIPSStatus(status);
						}
					}
				}
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			psNeedsProcessing = false;
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
		List<ModelMachine> copy = new LinkedList<ModelMachine>(refinedByMachines);
		copy.removeAll(getLongestBranch());
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

	public IMachineRoot getInternalMachine() {
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
		return ("ModelMachine: "+internalMachine.getComponentName());
	}
	
	/**
	 * process the proof obligations if needed
	 * @return the total number of Proof Obligations
	 */
	@Override
	public int getPOcount(){
		if (poNeedsProcessing || psNeedsProcessing) {
			processPORoot();
			processPSRoot();
		}
		return proofObligations.size();
	}
	
	/**
	 * process the proof obligations if needed
	 * @return The number of undischarged Proof Obligations
	 */
	@Override
	public int getUndischargedPOcount() {
		if (poNeedsProcessing || psNeedsProcessing) {
			processPORoot();
			processPSRoot();
		}
		int result = 0;
		for (ModelProofObligation po : proofObligations.values()) {
			if (!po.isDischarged()) {
				result++;
			}
		}
		return result;
		
	}

	@Override
	public String getLabel() {
		return "Machine " +internalMachine.getComponentName();
	}

	public IRodinElement getInternalElement() {
		return internalMachine;
	}
	
	
}
