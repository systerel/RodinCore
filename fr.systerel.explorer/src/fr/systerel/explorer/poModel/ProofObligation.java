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


package fr.systerel.explorer.poModel;

import java.util.LinkedList;
import java.util.List;

import org.eventb.core.IPOSequent;
import org.eventb.core.IPSStatus;

/**
 * @author Administrator
 *
 */
public class ProofObligation {

	public ProofObligation (IPOSequent sequent, String filename) {
		this.internal_sequent = sequent;
		events = new LinkedList<Event>();
		invariants = new LinkedList<Invariant>();
		theorems = new LinkedList<Theorem>();
		axioms = new LinkedList<Axiom>();
		this.fileIdentifier = filename;
	}
	private IPOSequent internal_sequent;
	private IPSStatus internal_status;
	
	private List<Invariant> invariants;
	private List<Event> events;
	private List<Theorem> theorems;
	private List<Axiom> axioms;
	private Machine machine;
	private Context context;
	private String fileIdentifier;

	/**
	 * 
	 * @return an Identifier for the File that this PO is stored in. 
	 */
	public String getIdentifier() {
		return fileIdentifier;
	}
	
	public void setMachine(Machine machine) {
		this.machine = machine;		
	}
	
	public Machine getMachine() {
		return machine;
	}
	
	public void setIPSStatus(IPSStatus status) {
		internal_status = status;
	}
	
	public IPSStatus getIPSStatus() {
		return internal_status;
	}
	
	public Invariant[] getInvariants() {
		Invariant[] inv = new Invariant[invariants.size()];
		return invariants.toArray(inv);
	}
	
	public void addInvariant(Invariant inv) {
		invariants.add(inv);
	}
	
	public void removeInvariants(Invariant inv) {
		invariants.remove(inv);
	}

	public Event[] getEvents() {
		Event[] event = new Event[events.size()];
		return events.toArray(event);
	}
	
	public void addEvent(Event event) {
		events.add(event);
	}
	
	public void removeEvents(Event event) {
		events.remove(event);
	}

	public String getName() {
		return internal_sequent.getElementName();
	}

	public void addTheorem(Theorem th) {
		theorems.add(th);
		
	}

	public void addAxiom(Axiom ax) {
		axioms.add(ax);
		
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context ctx) {
		context = ctx;
	}
	
}
