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

import java.util.LinkedList;
import java.util.List;

import org.eventb.core.IPOSequent;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IConfidence;
import org.rodinp.core.RodinDBException;

/**
 * Represents a Proof Obligation in the Model.
 *
 */
public class ModelProofObligation {
	public ModelProofObligation (IPOSequent sequent) {
		this.internal_sequent = sequent;
	}
	
	private IPOSequent internal_sequent;
	private IPSStatus internal_status;
	
	private List<ModelInvariant> invariants = new LinkedList<ModelInvariant>();
	private List<ModelEvent> events = new LinkedList<ModelEvent>();
	private List<ModelTheorem> theorems= new LinkedList<ModelTheorem>();
	private List<ModelAxiom> axioms = new LinkedList<ModelAxiom>();
	private ModelMachine machine; // A proof obligation can either belong to a context or a machine
	private ModelContext context;
	private boolean discharged = false;

	
	public void setMachine(ModelMachine machine) {
		this.machine = machine;		
	}
	
	public ModelMachine getMachine() {
		return machine;
	}
	
	public void setIPSStatus(IPSStatus status) {
		internal_status = status;
		try {
			discharged =  (status.getConfidence() > IConfidence.REVIEWED_MAX) && !status.isBroken() ;
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public IPSStatus getIPSStatus() {
		return internal_status;
	}
	
	public ModelInvariant[] getInvariants() {
		ModelInvariant[] inv = new ModelInvariant[invariants.size()];
		return invariants.toArray(inv);
	}
	
	public void addInvariant(ModelInvariant inv) {
		invariants.add(inv);
	}
	
	public void removeInvariants(ModelInvariant inv) {
		invariants.remove(inv);
	}

	public ModelEvent[] getEvents() {
		ModelEvent[] event = new ModelEvent[events.size()];
		return events.toArray(event);
	}
	
	public void addEvent(ModelEvent event) {
		events.add(event);
	}
	
	public void removeEvents(ModelEvent event) {
		events.remove(event);
	}

	public String getName() {
		return internal_sequent.getElementName();
	}

	public void addTheorem(ModelTheorem th) {
		theorems.add(th);
		
	}

	public void addAxiom(ModelAxiom ax) {
		axioms.add(ax);
		
	}

	public ModelContext getContext() {
		return context;
	}

	public void setContext(ModelContext ctx) {
		context = ctx;
	}
	
	public String getElementName(){
		return internal_sequent.getElementName();
	}
	
	public boolean isDischarged(){
		return discharged;
	}
}
