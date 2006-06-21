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
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ISCVariable;
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
public class SCMachineCache extends Cache<ISCMachineFile> {
	
	private final ISCInternalContext internalContext;
	
	/**
	 * @param file
	 */
	public SCMachineCache(ISCMachineFile file, IProgressMonitor monitor) throws RodinDBException {
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
		
		ISCInternalContext[] internalContexts = file.getSCInternalContexts();
		if (internalContexts.length == 0) {
			internalContext = null;
		} else {
			internalContext = internalContexts[0];
		}
		
		typeEnvironment = factory.makeTypeEnvironment();
		if (internalContext != null) {
			typeEnvironment.addAll(getTypeEnvironment(internalContext.getSCCarrierSets(), monitor));
			typeEnvironment.addAll(getTypeEnvironment(internalContext.getSCConstants(), monitor));
		}
		typeEnvironment.addAll(getTypeEnvironment(file.getSCVariables(), false, monitor));
	}
	
//	private IProgressMonitor monitor;
	
	private HashMap<String, String> predicateSetMap;
	
	// prefixes and names for predicate sets
	private final static String hypsetPrefix = "hypset-";
	private final static String oldHypsetName = "old-hypset";
	private String newHypsetName;
	private ITypeEnvironment typeEnvironment;
	
	private ISCAxiom[] oldAxioms = null;
	public ISCAxiom[] getOldAxioms() throws RodinDBException {
		if(oldAxioms == null) 
			if(internalContext == null) {
				oldAxioms = new ISCAxiom[0];
			} else {
				oldAxioms = internalContext.getSCAxioms();
			}
		return oldAxioms;
	}

//	private IInvariant[] oldInvariants = null;
//	public IInvariant[] getOldInvariants() throws RodinDBException {
//		if(oldInvariants == null) {
//			oldInvariants = file.getOldInvariants();
//		}
//		return oldInvariants;
//	}

	private ISCTheorem[] oldTheorems = null;
	public ISCTheorem[] getOldTheorems() throws RodinDBException {
		if(oldTheorems == null)
			if(internalContext == null) {
				oldTheorems = new ISCTheorem[0];
			} else {
				oldTheorems = internalContext.getSCTheorems();
			}
		return oldTheorems;
	}

	private ISCConstant[] constants = null;
	public ISCConstant[] getSCConstants() throws RodinDBException {
		if(constants == null)
			if(internalContext == null) {
				constants = new ISCConstant[0];
			} else {
				constants = internalContext.getSCConstants();
			}
		return constants;
	}
	
	private ISCCarrierSet[] carrierSets = null;
	public ISCCarrierSet[] getSCCarrierSets() throws RodinDBException {
		if(carrierSets == null)
			if(internalContext == null) {
				carrierSets = new ISCCarrierSet[0];
			} else {
				carrierSets = internalContext.getSCCarrierSets();
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
	
	private ISCInvariant[] invariants = null;
	public ISCInvariant[] getNewInvariants() throws RodinDBException {
		if(invariants == null) {
			invariants = file.getSCInvariants();
		}
		return invariants;
	}
	
	private ISCTheorem[] theorems = null;
	public ISCTheorem[] getNewTheorems() throws RodinDBException {
		if(theorems == null) {
			theorems = file.getSCTheorems();
		}
		return theorems;
	}
	
	private ISCEvent[] events = null;
	public ISCEvent[] getEvents() throws RodinDBException {
		if(events == null) {
			events = file.getSCEvents();
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
