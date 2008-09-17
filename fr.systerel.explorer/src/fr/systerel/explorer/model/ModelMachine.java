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

import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.ITheorem;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class ModelMachine extends ModelPOContainer implements IModelElement {
	public ModelMachine(IMachineFile file){
		internalMachine = file;
		try {
			IInvariant[] invs = file.getChildrenOfType(IInvariant.ELEMENT_TYPE);
			for (int i = 0; i < invs.length; i++) {
				addInvariant(invs[i]);
			}
			IEvent[] evts = file.getChildrenOfType(IEvent.ELEMENT_TYPE);
			for (int i = 0; i < evts.length; i++) {
				addEvent(evts[i]);
			}
			ITheorem[] thms = file.getChildrenOfType(ITheorem.ELEMENT_TYPE);
			for (int i = 0; i < thms.length; i++) {
				addTheorem(thms[i]);
			}
			processPOFile();
			processPSFile();
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void processPOFile() throws RodinDBException {
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
	}

	public void processPSFile() throws RodinDBException {
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
	}
	
	public void addInvariant(IInvariant invariant) {
		invariants.put(invariant.getElementName(), new ModelInvariant(invariant, this));
	}

	public void addEvent(IEvent event) {
		events.put(event.getElementName(), new ModelEvent(event, this));
	}
	
	public void addTheorem(ITheorem theorem) {
		theorems.put(theorem.getElementName(), new ModelTheorem(theorem, this));
	}

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
	public List<ModelMachine> getLongestMachineBranch() {
		List<ModelMachine> results = new LinkedList<ModelMachine>();
		results.add(this);
		List<ModelMachine> longest = new LinkedList<ModelMachine>();
		for (Iterator<ModelMachine> iterator = refinedByMachines.iterator(); iterator.hasNext();) {
			ModelMachine machine = iterator.next();
			if (machine.getLongestMachineBranch().size() > longest.size()) {
				longest = machine.getLongestMachineBranch();
			}
		}
		results.addAll(longest);
		return results;
	}
	
	/**
	 * Assuming no cycles
	 * @return All Ancestors of this machine (refinement perspective)
	 */
	public List<ModelMachine> getAncestors(){
		List<ModelMachine> results = new LinkedList<ModelMachine>();
		for (Iterator<ModelMachine> iterator = refinesMachines.iterator(); iterator.hasNext();) {
			ModelMachine machine = iterator.next();
			results.add(machine);
			results.addAll(machine.getAncestors());
		}
		return results;
	}
	
	/**
	 * 
	 * @return All the refinedByMachines, that are not returned by getLongestBranch
	 */
	public List<ModelMachine> getRestMachines(){
		List<ModelMachine> copy = new LinkedList<ModelMachine>(refinedByMachines);
		copy.removeAll(getLongestMachineBranch());
		return copy;
	}
	
	public void addRefinesMachine(ModelMachine machine) {
		refinesMachines.add(machine);
	}
	public void addRefinedByMachine(ModelMachine machine) {
		refinedByMachines.add(machine);
	}
	public void addSeesContext(ModelContext context) {
		seesContexts.add(context);
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

	private List<ModelContext> seesContexts = new LinkedList<ModelContext>();
	/**
	 * Machines that refine this Machine.
	 */
	private List<ModelMachine> refinedByMachines = new LinkedList<ModelMachine>();
	/**
	 * Machines that this Machine refines.
	 */

	public IModelElement getParent() {
		return ModelController.getProject(internalMachine.getRodinProject());
	}

	private List<ModelMachine> refinesMachines = new LinkedList<ModelMachine>();
	private IMachineFile internalMachine ;
	private HashMap<String, ModelInvariant> invariants = new HashMap<String, ModelInvariant>();
	private HashMap<String, ModelEvent> events = new HashMap<String, ModelEvent>();
	private HashMap<String, ModelTheorem> theorems = new HashMap<String, ModelTheorem>();
	//TODO add variables etc?
}
