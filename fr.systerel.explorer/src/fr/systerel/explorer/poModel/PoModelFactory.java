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

import java.util.HashMap;
import java.util.Iterator;

import org.eventb.core.IAxiom;
import org.eventb.core.IContextFile;
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
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class PoModelFactory {

	public static void processPOFile(IPOFile file) {
		try {
			IPOSequent[] sequents = file.getSequents();
			for (int i = 0; i < sequents.length; i++) {
				String filename = file.getAncestor(IRodinProject.ELEMENT_TYPE).getElementName()+file.getBareName();
				IPOSequent sequent = sequents[i];
				String name = sequent.getHandleIdentifier();
				if (!proofObligations.containsKey(name)) {
					ProofObligation po = new ProofObligation(sequent, filename);
					proofObligations.put(name, po);
					// check if a status for this sequent has been added previously
					if (statuses.containsKey(name)) {
						po.setIPSStatus(statuses.get(name));
					}
					
					if (machines.containsKey(filename)) {
						machines.get(filename).addProofObligation(po);
					}
					if (contexts.containsKey(filename)) {
						contexts.get(filename).addProofObligation(po);
					}
					
					IPOSource[] sources = sequents[i].getSources();
					for (int j = 0; j < sources.length; j++) {
						IRodinElement source = sources[j].getSource();
						if (source instanceof IInvariant) {
							Invariant inv = getInvariant((IInvariant) source);
							po.addInvariant(inv);
							inv.addProofObligation(po);
							
						}
						if (source instanceof IEvent) {
							Event ev = getEvent((IEvent) source);
							po.addEvent(ev);
							ev.addProofObligation(po);
						}
						if (source instanceof ITheorem) {
							Theorem th = getTheorem((ITheorem) source);
							po.addTheorem(th);
							th.addProofObligation(po);
						}
						if (source instanceof IAxiom) {
							Axiom ax = getAxiom((IAxiom) source);
							po.addAxiom(ax);
							ax.addProofObligation(po);
						}
					}
				}
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void processPSFile(IPSFile file) {
		try {
			IPSStatus[] stats = file.getStatuses();
			for (int i = 0; i < stats.length; i++) {
				IPSStatus status = stats[i];
				IPOSequent sequent = status.getPOSequent();
				// check if there is already a ProofObligation for this sequent
				if (proofObligations.containsKey(sequent.getHandleIdentifier())) {
					proofObligations.get(sequent.getHandleIdentifier()).setIPSStatus(status);
				}
				statuses.put(sequent.getHandleIdentifier(), status);
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Invariant getInvariant(IInvariant invariant){
		// look in the hashmap, if this Invariant exists
		if (invariants.containsKey(invariant.getHandleIdentifier())) {
			return invariants.get(invariant.getHandleIdentifier());
		}
		// otherwise create a new Invariant.
		Invariant inv = new Invariant(invariant);
		invariants.put(invariant.getHandleIdentifier(), inv);
		return inv;
	}

	/**
	 * Processes all POFiles and PSFiles of a given project
	 * @param project The Project to process
	 */
	public static void processProject(IRodinProject project){
		
		try {
			IPOFile[] pofiles = project.getChildrenOfType(IPOFile.ELEMENT_TYPE);
			for (int i = 0; i < pofiles.length; i++) {
				processPOFile(pofiles[i]);
			}
			IPSFile[] psfiles =  project.getChildrenOfType(IPSFile.ELEMENT_TYPE);
			for (int i = 0; i < psfiles.length; i++) {
				processPSFile(psfiles[i]);
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static Event getEvent(IEvent event){
		// look in the hashmap, if this Event exists
		if (events.containsKey(event.getHandleIdentifier())) {
			return events.get(event.getHandleIdentifier());
		}
		// otherwise create a new Event.
		Event ev = new Event(event);
		events.put(event.getHandleIdentifier(), ev);
		return ev;
	}

	public static Theorem getTheorem(ITheorem theorem){
		// look in the hashmap, if this Theorem exists
		if (theorems.containsKey(theorem.getHandleIdentifier())) {
			return theorems.get(theorem.getHandleIdentifier());
		}
		// otherwise create a new Theorem.
		Theorem th = new Theorem(theorem);
		theorems.put(theorem.getHandleIdentifier(), th);
		return th;
	}

	public static Axiom getAxiom(IAxiom axiom){
		// look in the hashmap, if this Axiom exists
		if (axioms.containsKey(axiom.getHandleIdentifier())) {
			return axioms.get(axiom.getHandleIdentifier());
		}
		// otherwise create a new Axiom.
		Axiom ax = new Axiom(axiom);
		axioms.put(axiom.getHandleIdentifier(), ax);
		return ax;
	}

	public static Machine getMachine(IMachineFile machine){
		// look in the hashmap, if this Machine exists
		String filename = machine.getAncestor(IRodinProject.ELEMENT_TYPE).getElementName()+machine.getBareName();
		if (machines.containsKey(filename)) {
			return machines.get(filename);
		}
		// otherwise create a new Machine.
		Machine mach = new Machine(machine);
		machines.put(filename, mach);
		// check if there are any Proof Obligations already there for this machine
		for (Iterator<ProofObligation> iterator = proofObligations.values().iterator(); iterator.hasNext();) {
			ProofObligation po = iterator.next();
			if (po.getIdentifier().equals(filename)) {
				mach.addProofObligation(po);
				po.setMachine(mach);
			}
		}		
		return mach;
	}

	public static Context getContext(IContextFile context){
		// look in the hashmap, if this Machine exists
		String filename = context.getAncestor(IRodinProject.ELEMENT_TYPE).getElementName()+context.getBareName();
		if (contexts.containsKey(filename)) {
			return contexts.get(filename);
		}
		// otherwise create a new Machine.
		Context ctx = new Context(context);
		contexts.put(filename, ctx);
		// check if there are any Proof Obligations already there for this machine
		for (Iterator<ProofObligation> iterator = proofObligations.values().iterator(); iterator.hasNext();) {
			ProofObligation po = iterator.next();
			if (po.getIdentifier().equals(filename)) {
				ctx.addProofObligation(po);
				po.setContext(ctx);
			}
		}		
		return ctx;
	}
	
	
	public static void clearAll() {
		proofObligations.clear();
		statuses.clear();
		invariants.clear();
		events.clear();
		theorems.clear();
		axioms.clear();
		machines.clear();
	}
	
	private static HashMap<String, ProofObligation> proofObligations = new HashMap<String, ProofObligation>();
	private static HashMap<String, IPSStatus> statuses = new HashMap<String, IPSStatus>();
	private static HashMap<String, Invariant> invariants = new HashMap<String,Invariant>();
	private static HashMap<String, Event> events = new HashMap<String, Event>();
	private static HashMap<String, Theorem> theorems = new HashMap<String, Theorem>();
	private static HashMap<String, Axiom> axioms = new HashMap<String, Axiom>();
	private static HashMap<String, Machine> machines = new HashMap<String, Machine>();
	private static HashMap<String, Context> contexts = new HashMap<String, Context>();
}
