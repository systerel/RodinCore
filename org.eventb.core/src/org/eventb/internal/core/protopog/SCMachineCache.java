/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core.protopog;

import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IAxiom;
import org.eventb.core.IInvariant;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCMachine;
import org.eventb.core.ISCVariable;
import org.eventb.core.ITheorem;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 */
public class SCMachineCache extends Cache<ISCMachine> {
	/**
	 * @param file
	 */
	public SCMachineCache(ISCMachine file, IProgressMonitor monitor) throws RodinDBException {
		super(file);
		
		predicateSetMap = new HashMap<String, String>((getNewInvariants().length + getNewTheorems().length) * 4 / 3);
		
		newHypsetName = oldHypsetName;
		
		if(invariants.length > 0) {
			predicateSetMap.put(invariants[0].getElementName(), oldHypsetName);
			newHypsetName = hypsetPrefix + invariants[invariants.length-1].getElementName();
		}
		for(int i=1; i<invariants.length; i++) {
			predicateSetMap.put(invariants[i].getElementName(), hypsetPrefix + invariants[i-1].getElementName());
		}
		
		if(theorems.length > 0) {
			if(invariants.length > 0)
				predicateSetMap.put(theorems[0].getElementName(), hypsetPrefix + invariants[invariants.length-1].getElementName());
			else
				predicateSetMap.put(theorems[0].getElementName(), oldHypsetName);
			newHypsetName = hypsetPrefix + theorems[theorems.length-1].getElementName();
		}
		for(int i=1; i<theorems.length; i++) {
			predicateSetMap.put(theorems[i].getElementName(), hypsetPrefix + theorems[i-1].getElementName());
		}
		
		typeEnvironment = getTypeEnvironment(file.getSCCarrierSets(), monitor);
		typeEnvironment.addAll(getTypeEnvironment(file.getSCConstants(), monitor));
		typeEnvironment.addAll(getTypeEnvironment(file.getSCVariables(), monitor));
	}
	
//	private IProgressMonitor monitor;
	
	private HashMap<String, String> predicateSetMap;
	
	// prefixes and names for predicate sets
	private final static String hypsetPrefix = "hypset-";
	private final static String oldHypsetName = "old-hypset";
	private String newHypsetName;
	private ITypeEnvironment typeEnvironment;
	
	private IAxiom[] oldAxioms = null;
	public IAxiom[] getOldAxioms() throws RodinDBException {
		if(oldAxioms == null) {
			oldAxioms = file.getOldAxioms();
		}
		return oldAxioms;
	}

	private IInvariant[] oldInvariants = null;
	public IInvariant[] getOldInvariants() throws RodinDBException {
		if(oldInvariants == null) {
			oldInvariants = file.getOldInvariants();
		}
		return oldInvariants;
	}

	private ITheorem[] oldTheorems = null;
	public ITheorem[] getOldTheorems() throws RodinDBException {
		if(oldTheorems == null) {
			oldTheorems = file.getOldTheorems();
		}
		return oldTheorems;
	}

	private ISCConstant[] constants = null;
	public ISCConstant[] getSCConstants() throws RodinDBException {
		if(constants == null) {
			constants = file.getSCConstants();
		}
		return constants;
	}
	
	private ISCCarrierSet[] carrierSets = null;
	public ISCCarrierSet[] getSCCarrierSets() throws RodinDBException {
		if(carrierSets == null) {
			carrierSets = file.getSCCarrierSets();
		}
		return carrierSets;
	}
	
	private ISCVariable[] variables = null;
	public ISCVariable[] getSCVariables() throws RodinDBException {
		if(variables == null) {
			variables = file.getSCVariables();
		}
		return variables;
	}
	
	private IInvariant[] invariants = null;
	public IInvariant[] getNewInvariants() throws RodinDBException {
		if(invariants == null) {
			invariants = file.getInvariants();
		}
		return invariants;
	}
	
	private ITheorem[] theorems = null;
	public ITheorem[] getNewTheorems() throws RodinDBException {
		if(theorems == null) {
			theorems = file.getTheorems();
		}
		return theorems;
	}
	
	private ISCEvent[] events = null;
	public ISCEvent[] getEvents() throws RodinDBException {
		if(events == null) {
			events = (ISCEvent[]) file.getEvents();
		}
		return events;
	}
	
	public String getOldHypSetName() {
		return oldHypsetName;
	}
	
	public String getHypSetName(String name) {
		return predicateSetMap.get(name);
	}
	
	public Predicate getPredicate(String predicate) {
		return getPredicate(predicate, typeEnvironment);
	}

	public Predicate getPredicate(String predicate, @SuppressWarnings("hiding") ITypeEnvironment typeEnvironment) {
		IParseResult presult = getFactory().parsePredicate(predicate);
		assert presult.getParsedPredicate() != null;
		ITypeCheckResult tcresult = presult.getParsedPredicate().typeCheck(typeEnvironment);
		assert tcresult.isSuccess();
		return presult.getParsedPredicate();
	}

	public Assignment getAssignment(String assignment) {
		return getAssignment(assignment, typeEnvironment);
	}

	public Assignment getAssignment(String assignment, @SuppressWarnings("hiding") ITypeEnvironment typeEnvironment) {
		IParseResult presult = getFactory().parseAssignment(assignment);
		assert presult.getParsedAssignment() != null;
		ITypeCheckResult tcresult = presult.getParsedAssignment().typeCheck(typeEnvironment);
		assert tcresult.isSuccess();
		return presult.getParsedAssignment();
	}

	/**
	 * @return Returns the global typeEnvironment.
	 */
	public ITypeEnvironment getGlobalTypeEnvironment() {
		return typeEnvironment;
	}

	/**
	 * @return Returns the newHypsetName.
	 */
	public String getNewHypsetName() {
		return newHypsetName;
	}

}
