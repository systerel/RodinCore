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
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContext;
import org.eventb.core.ITheorem;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 */
public class SCContextCache extends Cache<ISCContext> {

	/**
	 * @param file
	 */
	public SCContextCache(ISCContext file, IProgressMonitor monitor) throws RodinDBException {
		super(file);
		
		predicateSetMap = new HashMap<String, String>((getNewAxioms().length + getNewTheorems().length) * 4 / 3);
		
		if(axioms.length > 0)
			predicateSetMap.put(axioms[0].getElementName(), oldHypsetName);
		for(int i=1; i<axioms.length; i++) {
			predicateSetMap.put(axioms[i].getElementName(), hypsetPrefix + axioms[i-1].getElementName());
		}
		
		if(theorems.length > 0)
			if(axioms.length > 0)
				predicateSetMap.put(theorems[0].getElementName(), hypsetPrefix + axioms[axioms.length-1].getElementName());
			else
				predicateSetMap.put(theorems[0].getElementName(), oldHypsetName);
		for(int i=1; i<theorems.length; i++) {
			predicateSetMap.put(theorems[i].getElementName(), hypsetPrefix + theorems[i-1].getElementName());
		}
		
		typeEnvironment = getTypeEnvironment(file.getSCCarrierSets(), monitor);
		typeEnvironment.addAll(getTypeEnvironment(file.getSCConstants(), monitor));
	}
	
	@SuppressWarnings("unused")
	private IProgressMonitor monitor;
	
	private HashMap<String, String> predicateSetMap;
	
	// prefixes and names for predicate sets
	private final static String hypsetPrefix = "hypset-";
	private final static String oldHypsetName = "old-hypset";
	@SuppressWarnings("unused")
	private final static String newHypsetName = "new-hypset";
//	private final static String axiomPrefix = "axioms";
//	private final static String theoremPrefix = "theorems";
//	private final static String allAxioms = "allAxioms";
//	private final static String allTheorems = "allTheorems";
	
	// global type environment
	private ITypeEnvironment typeEnvironment;
	
	private IAxiom[] oldAxioms = null;
	public IAxiom[] getOldAxioms() throws RodinDBException {
		if(oldAxioms == null) {
			oldAxioms = file.getOldAxioms();
		}
		return oldAxioms;
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
	
	private IAxiom[] axioms = null;
	public IAxiom[] getNewAxioms() throws RodinDBException {
		if(axioms == null) {
			axioms = file.getAxioms();
		}
		return axioms;
	}
	
	private ITheorem[] theorems = null;
	public ITheorem[] getNewTheorems() throws RodinDBException {
		if(theorems == null) {
			theorems = file.getTheorems();
		}
		return theorems;
	}
	
	public String getOldHypSetName() {
		return oldHypsetName;
	}
	
	public String getHypSetName(String name) {
		return predicateSetMap.get(name);
	}
	
	public Predicate getPredicate(String predicate) {
		IParseResult presult = getFactory().parsePredicate(predicate);
		assert presult.getParsedPredicate() != null;
		ITypeCheckResult tcresult = presult.getParsedPredicate().typeCheck(typeEnvironment);
		assert tcresult.isSuccess();
		return presult.getParsedPredicate();
	}

}
