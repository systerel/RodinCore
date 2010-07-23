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
package fr.systerel.internal.explorer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.IAction;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * 
 * Represents a Machine in the model
 *
 */
public class ModelMachine extends ModelPOContainer implements IModelElement {
	
	/**
	 * The nodes are used by the ContentProviders to present a node in the tree
	 * above elements such as Invariants or Variables
	 */
	public final ModelElementNode variable_node;
	public final ModelElementNode invariant_node;
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

	private IMachineRoot machineRoot ;

	private HashMap<IInvariant, ModelInvariant> invariants = new HashMap<IInvariant, ModelInvariant>();
	private HashMap<IEvent, ModelEvent> events = new HashMap<IEvent, ModelEvent>();
	//Variables are not taken into the model, because they don't have any proof obligations
	
	/**
	 * All machines that are above this machine in the refinement tree. (=
	 * machines that are refined by this machines). This is computed by the
	 * ModelProject.
	 */
	private ArrayList<ModelMachine> ancestors =  new ArrayList<ModelMachine>();
	/**
	 * The longest branch of machines that refine this machine. (including this
	 * machine) The value of this is calculated in the ModelProject by calling
	 * <code>calculateMachineBranches()</code>.
	 */
	private ArrayList<ModelMachine> longestRefineBranch =  new ArrayList<ModelMachine>();
	
	public boolean psNeedsProcessing = true;
	public boolean poNeedsProcessing = true;
	

	/**
	 * Creates a ModelMachine from a given IMachineRoot
	 * 
	 * @param root
	 *            The MachineRoot that this ModelMachine is based on.
	 */
	public ModelMachine(IMachineRoot root){
		machineRoot = root;
		variable_node = new ModelElementNode(IVariable.ELEMENT_TYPE, this);
		invariant_node = new ModelElementNode(IInvariant.ELEMENT_TYPE, this);
		event_node = new ModelElementNode(IEvent.ELEMENT_TYPE, this);
		po_node = new ModelElementNode(IPSStatus.ELEMENT_TYPE, this);
	}
	
	/**
	 * @return The Project that contains this Machine.
	 */
	@Override
	public IModelElement getModelParent() {
		return ModelController.getProject(machineRoot.getRodinProject());
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
		try {
			IInvariant[] invs = machineRoot.getChildrenOfType(IInvariant.ELEMENT_TYPE);
			for (int i = 0; i < invs.length; i++) {
				addInvariant(invs[i]);
			}
			IEvent[] evts = machineRoot.getChildrenOfType(IEvent.ELEMENT_TYPE);
			for (IEvent evt :  evts) {
				addEvent(evt);
			}
		} catch (RodinDBException e) {
			UIUtils.log(e, "when accessing events, invariants and theorems of "+machineRoot);
		}
		
	}
	
	
	/**
	 * Processes the PORoot that belongs to this machine. It creates a
	 * ModelProofObligation for each sequent and adds it to this machines as
	 * well as to the concerned Invariants, Theorems and Events.
	 */
	public void processPORoot() {
		if (poNeedsProcessing) {
			try {
				//clear old POs
				proofObligations.clear();
				IPORoot root = machineRoot.getPORoot();
				if (root.exists()) {
					IPOSequent[] sequents = root.getSequents();
					int pos = 1;
					for (IPOSequent sequent : sequents) {
						ModelProofObligation po = new ModelProofObligation(sequent, pos);
						pos++;
						po.setMachine(this);
						proofObligations.put(sequent, po);
						IPOSource[] sources = sequent.getSources();
						for (int j = 0; j < sources.length; j++) {
							IRodinElement source = sources[j].getSource();
							//only process sources that belong to this machine.
							if (machineRoot.isAncestorOf(source) ){
								processSource(source, po);
							}
						}
					}
				}
			} catch (RodinDBException e) {
				UIUtils.log(e, "when processing proof obligations of " +machineRoot);
			}
			poNeedsProcessing = false;
		}
	}


	/**
	 * Processes the PSRoot that belongs to this Machine. Each status is added
	 * to the corresponding Proof Obligation, if that ProofObligation is
	 * present.
	 */
	public void processPSRoot() {
		if (psNeedsProcessing) {
			try {
				IPSRoot root = machineRoot.getPSRoot();
				if (root.exists()) {
					IPSStatus[] stats = root.getStatuses();
					for (int i = 0; i < stats.length; i++) {
						IPSStatus status = stats[i];
						IPOSequent sequent = status.getPOSequent();
						// check if there is a ProofObligation for this status (there should be one!)
						if (proofObligations.containsKey(sequent)) {
							proofObligations.get(sequent).setIPSStatus(status);
						}
					}
				}
			} catch (RodinDBException e) {
				UIUtils.log(e, "when processing proof statuses of " +machineRoot);
			}
			psNeedsProcessing = false;
		}
	}
	
	/**
	 * Adds a new ModelInvariant to this Machine.
	 * 
	 * @param invariant
	 *            The Invariant to add.
	 */
	public void addInvariant(IInvariant invariant) {
		invariants.put(invariant, new ModelInvariant(invariant, this));
	}

	/**
	 * Adds a new ModelEvent to this Machine.
	 * 
	 * @param event
	 *            The Event to add.
	 */
	public void addEvent(IEvent event) {
		events.put(event, new ModelEvent(event, this));
	}
	
	/**
	 * 
	 * @return is this Machine a root of a tree of Machines? (= refines no other
	 *         machine)
	 */
	public boolean isRoot(){
		return (refinesMachines.size() ==0);
	}
	
	/**
	 * 
	 * @return is this Machine a leaf of a tree of Machines? (= is refined by no
	 *         other machine)
	 */
	public boolean isLeaf(){
		return (refinedByMachines.size() ==0);
	}
	
	
	/**
	 * 
	 * @return All the refinedByMachines, that are not returned by
	 *         getLongestBranch. They will be added as children to this machine
	 *         by the <code>ComplexMachineContentProvider</code> in the
	 *         navigator tree.
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
		return machineRoot;
	}

	public List<ModelContext> getSeesContexts() {
		return seesContexts;
	}

	public ModelInvariant getInvariant(IInvariant invariant){
		return invariants.get(invariant);
	}
	
	public ModelEvent getEvent(IEvent event){
		return events.get(event);
	}
	
	@Override
	public String toString(){
		return ("ModelMachine: "+machineRoot.getComponentName());
	}
	
	/**
	 * process the proof obligations if needed
	 * 
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
	 * 
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
	public IRodinElement getInternalElement() {
		return machineRoot;
	}
	
	protected void processSource(IRodinElement source, ModelProofObligation po) {
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
			if (invariants.containsKey(source)) {
				ModelInvariant inv = invariants.get(source);
				po.addInvariant(inv);
				inv.addProofObligation(po);
			}
		}
		if (source instanceof IEvent) {
			if (events.containsKey(source) ){
				ModelEvent evt = events.get(source);
				po.addEvent(evt);
				evt.addProofObligation(po);
			}
		}
	}

	public IModelElement getModelElement(IRodinElement element) {
		if (element instanceof IInvariant ) {
			return invariants.get(element);
		}
		if (element instanceof IEvent ) {
			return events.get(element);
		}
		return null;
	}

	/**
	 * In the complex version this gets the first abstract machine of this
	 * machine. If none exist or in the non-complex version this returns the
	 * containing project.
	 */
	@Override
	public Object getParent(boolean complex) {
		if (complex) {
			if (refinesMachines.size() > 0) {
				return (refinesMachines.get(0).getInternalMachine());
			}
		}
		return machineRoot.getRodinProject();
	}

	@Override
	public Object[] getChildren(IInternalElementType<?> type, boolean complex) {
		if (type == IContextRoot.ELEMENT_TYPE) {
    		return ModelController.convertToIContext(getSeesContexts()).toArray();

		}
		if (type == IMachineRoot.ELEMENT_TYPE) {
        	List<ModelMachine> rest = getRestMachines();
        	List<ModelMachine> machines = new LinkedList<ModelMachine>();

        	for (ModelMachine mach : rest) {
				machines.addAll(mach.getLongestBranch());
			}
        	return ModelController.convertToIMachine(machines).toArray();
		}
		
		if (poNeedsProcessing || psNeedsProcessing) {
			processPORoot();
			processPSRoot();
		}
		if (type == IInvariant.ELEMENT_TYPE) {
			return new Object[]{invariant_node};
		}
		if (type == IVariable.ELEMENT_TYPE) {
			return new Object[]{variable_node};
		}
		if (type == IEvent.ELEMENT_TYPE) {
			return new Object[]{event_node};
		}
		if (type == IPSStatus.ELEMENT_TYPE) {
			return new Object[]{po_node};
		}
		if (ExplorerUtils.DEBUG) {
			System.out.println("Unsupported children type for machine: " +type);
		}
		return new Object[0];
	}
	
	
}
