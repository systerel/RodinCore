/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.testscpog;

import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.ISCAxiomSet;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCTheoremSet;
import org.eventb.core.ISCVariable;
import org.eventb.core.ISees;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.basis.POIdentifier;
import org.eventb.core.basis.SCCarrierSet;
import org.eventb.core.basis.SCConstant;
import org.eventb.core.basis.SCVariable;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public class TestUtil {

	public static String[] makeList(String...strings) {
		return strings;
	}
	
	public static void addEvent(IRodinFile rodinFile, 
				String name,
				String[] vars,
				String[] guardNames,
				String[] guards,
				String[] actions
	) throws RodinDBException {
		IEvent event = (IEvent) rodinFile.createInternalElement(IEvent.ELEMENT_TYPE, name, null, null);
		for(int i=0; i<vars.length; i++) {
			event.createInternalElement(IVariable.ELEMENT_TYPE, vars[i], null, null);
		}
		for(int i=0; i<guards.length; i++) {
			IGuard guard = (IGuard) event.createInternalElement(IGuard.ELEMENT_TYPE, guardNames[i], null, null);
			guard.setContents(guards[i]);
		}
		for(int j=0; j<actions.length; j++) {
			IAction action = (IAction) event.createInternalElement(IAction.ELEMENT_TYPE, "ACTION", null, null);
			action.setContents(actions[j]);
		}
	}
	
	public static void addSCEvent(IRodinFile rodinFile, 
			String name,
			String[] vars,
			String[] guardNames,
			String[] guards,
			String[] actions,
			String[] types
) throws RodinDBException {
	ISCEvent event = (ISCEvent) rodinFile.createInternalElement(ISCEvent.ELEMENT_TYPE, name, null, null);
	for(int i=0; i<vars.length; i++) {
		ISCVariable variable = (ISCVariable) event.createInternalElement(ISCVariable.ELEMENT_TYPE, vars[i], null, null);
		variable.setContents(types[i]);
	}
	for(int i=0; i<vars.length; i++) {
		event.createInternalElement(IVariable.ELEMENT_TYPE, vars[i], null, null);
	}
	for(int i=0; i<guards.length; i++) {
		IGuard guard = (IGuard) event.createInternalElement(IGuard.ELEMENT_TYPE, guardNames[i], null, null);
		guard.setContents(guards[i]);
	}
	for(int j=0; j<actions.length; j++) {
		IAction action = (IAction) event.createInternalElement(IAction.ELEMENT_TYPE, "ACTION", null, null);
		action.setContents(actions[j]);
	}
}

//	public static void addEvent(IRodinFile rodinFile, 
//			String name,
//			String[] vars,
//			String[] guardNames,
//			String[] guards,
//			String[] actions,
//			
//) throws RodinDBException {
//	IEvent event = (IEvent) rodinFile.createInternalElement(IEvent.ELEMENT_TYPE, name, null, null);
//	for(int i=0; i<vars.length; i++) {
//		event.createInternalElement(IVariable.ELEMENT_TYPE, vars[i], null, null);
//	}
//	for(int i=0; i<guards.length; i++) {
//		IGuard guard = (IGuard) event.createInternalElement(IGuard.ELEMENT_TYPE, guardNames[i], null, null);
//		guard.setContents(guards[i]);
//	}
//	for(int j=0; j<actions.length; j++) {
//		IAction action = (IAction) event.createInternalElement(IAction.ELEMENT_TYPE, "ACTION", null, null);
//		action.setContents(actions[j]);
//	}
//}
//
	public static void addSees(IRodinFile rodinFile, String name) throws RodinDBException {
		ISees sees = (ISees) rodinFile.createInternalElement(ISees.ELEMENT_TYPE, null, null, null);
		sees.setContents(name);
	}
	
	public static void addVariables(IRodinFile rodinFile, String[] names) throws RodinDBException {
		for(String name : names)
			rodinFile.createInternalElement(IVariable.ELEMENT_TYPE, name, null, null);
	}
	
	public static void addCarrierSets(IRodinFile rodinFile, String[] names) throws RodinDBException {
		for(String name : names)
			rodinFile.createInternalElement(ICarrierSet.ELEMENT_TYPE, name, null, null);
	}
	
	public static void addConstants(IRodinFile rodinFile, String[] names) throws RodinDBException {
		for(String name : names)
			rodinFile.createInternalElement(IConstant.ELEMENT_TYPE, name, null, null);
	}
	
	public static void addInvariants(IRodinFile rodinFile, String[] names, String[] invariants) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			IInvariant invariant = (IInvariant) rodinFile.createInternalElement(IInvariant.ELEMENT_TYPE, names[i], null, null);
			invariant.setContents(invariants[i]);
		}
	}
	
	public static void addAxioms(IRodinFile rodinFile, String[] names, String[] axioms, String bag) throws RodinDBException {
		IInternalParent element = rodinFile;
		if(bag != null) {
			element = element.createInternalElement(ISCAxiomSet.ELEMENT_TYPE, bag, null, null);
		}
		for(int i=0; i<names.length; i++) {
			IAxiom axiom = (IAxiom) element.createInternalElement(IAxiom.ELEMENT_TYPE, names[i], null, null);
			axiom.setContents(axioms[i]);
		}
	}
	
	public static void addTheorems(IRodinFile rodinFile, String[] names, String[] theorems, String bag) throws RodinDBException {
		IInternalParent element = rodinFile;
		if(bag != null) {
			element = element.createInternalElement(ISCTheoremSet.ELEMENT_TYPE, bag, null, null);
		}
		for(int i=0; i<names.length; i++) {
			ITheorem theorem = (ITheorem) element.createInternalElement(ITheorem.ELEMENT_TYPE, names[i], null, null);
			theorem.setContents(theorems[i]);
		}
	}
	
	public static void addIdentifiers(IRodinFile rodinFile, String[] names, String[] types) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			POIdentifier identifier = (POIdentifier) rodinFile.createInternalElement(POIdentifier.ELEMENT_TYPE, names[i], null, null);
			identifier.setContents(types[i]);
		}
	}
	
	public static void addSCCarrierSets(IRodinFile rodinFile, String[] names, String[] types) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			SCCarrierSet identifier = (SCCarrierSet) rodinFile.createInternalElement(ISCCarrierSet.ELEMENT_TYPE, names[i], null, null);
			identifier.setContents(types[i]);
		}
	}
	
	public static void addSCVariables(IRodinFile rodinFile, String[] names, String[] types) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			SCVariable identifier = (SCVariable) rodinFile.createInternalElement(ISCVariable.ELEMENT_TYPE, names[i], null, null);
			identifier.setContents(types[i]);
		}
	}
	
	public static void addSCConstants(IRodinFile rodinFile, String[] names, String[] types) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			SCConstant identifier = (SCConstant) rodinFile.createInternalElement(ISCConstant.ELEMENT_TYPE, names[i], null, null);
			identifier.setContents(types[i]);
		}
	}
	
	public static ISCAxiomSet addOldAxioms(IRodinFile file, String name) throws RodinDBException {
		return (ISCAxiomSet) file.createInternalElement(ISCAxiomSet.ELEMENT_TYPE, name, null, null);
	}
	
	public static ISCTheoremSet addOldTheorems(IRodinFile file, String name) throws RodinDBException {
		return (ISCTheoremSet) file.createInternalElement(ISCTheoremSet.ELEMENT_TYPE, name, null, null);
	}
	
}
