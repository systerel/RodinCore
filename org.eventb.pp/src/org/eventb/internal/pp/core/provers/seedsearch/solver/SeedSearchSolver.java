/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.core.provers.seedsearch.solver;

import java.util.ArrayList;
import java.util.List;

public class SeedSearchSolver {
	
	// adds this variable link to the signature + look for possible instantiations
	// precondition : the link does not exist yet for this signature
	public List<SolverResult> addVariableLink(VariableLink link) {
		LiteralSignature signature1 = link.getSignature1();
		LiteralSignature signature2 = link.getSignature2();

		List<SolverResult> result = new ArrayList<SolverResult>();
		if (!signature1.hasVariableLink(link)) { 
			signature1.addVariableLink(link);
			signature2.addVariableLink(link);
		
			linkVariables(signature1, signature2, link, result);
			linkVariables(signature2, signature1, link, result);
		}
		return result;
	}
	
	private void linkVariables(LiteralSignature signature1, LiteralSignature signature2,
			VariableLink link, List<SolverResult> result) {
		for (Instantiable value : signature1.getInstantiables()) {
			List<SolverResult> linkedResult = addInstantiableHelper(value, signature2, link);
			if (linkedResult != null) result.addAll(linkedResult);
		}
	}
	
	public void removeVariableLink(VariableLink link) {
		LiteralSignature signature1 = link.getSignature1();
		LiteralSignature signature2 = link.getSignature2();
		signature1.removeVariableLink(link);
		signature2.removeVariableLink(link);
		
		deleteInstantiablesOfLink(signature1, signature2, link);
		deleteInstantiablesOfLink(signature2, signature1, link);
	}
	
	private void deleteInstantiablesOfLink(LiteralSignature signature1, LiteralSignature signature2, 
			VariableLink link) {
		for (Instantiable instantiable : signature1.getInstantiables()) {
			deleteInstantiable(instantiable, signature2, link);
		}
	}
	
	// adds the constant/level pair to the signature + look for possible instantiations
	// precondition : signature has not been added to value before
	public List<SolverResult> addInstantiable(Instantiable instantiable) {
		return addInstantiableHelper(instantiable, instantiable.getSignature(), null);			
	}
	
	private List<SolverResult> addInstantiableHelper(Instantiable instantiable, LiteralSignature signature, VariableLink link) {
		List<SolverResult> result = new ArrayList<SolverResult>();
		LiteralSignature matchingSignature = signature.getMatchingLiteral();
		InstantiableContainer container = matchingSignature.getInstantiableContainer(instantiable);
		if (container != null) {
			// we stop here
			if (link != null) container.addTransmitorLink(link);
			else container.setNotTransmitted();
		}
		else {
			if (link != null) container = new InstantiableContainer(link);
			else container = new InstantiableContainer();
			// we add the container
			matchingSignature.addInstantiable(instantiable, container);
			// we look for instantiation
			for (InstantiationValue value : matchingSignature.getInstantiationValues()) {
				result.add(new SolverResult(instantiable,value));
			}
			// we continue on linked signatures
			for (VariableLink goingLink : matchingSignature.getVariableLinks()) {
				LiteralSignature linkedSignature = null;
				if (goingLink.getSignature1()==matchingSignature) linkedSignature = goingLink.getSignature2();
				else if (goingLink.getSignature2()==matchingSignature) linkedSignature = goingLink.getSignature1();
				else assert false;
				List<SolverResult> linkedResult = addInstantiableHelper(instantiable, linkedSignature, goingLink);
				result.addAll(linkedResult);
			}
		}
		return result;
	}
	
	public void removeInstantiable(Instantiable instantiable) {
		deleteInstantiable(instantiable, instantiable.getSignature(), null);
	}

	
	private void deleteInstantiable(Instantiable instantiable, LiteralSignature signature, VariableLink link) {
		LiteralSignature matchingSignature = signature.getMatchingLiteral();
		InstantiableContainer container = matchingSignature.getInstantiableContainer(instantiable);
		if (container != null) {
			if (link == null) assert !container.isTransmitted();
			if (link != null && !container.hasTransmitorLink(link)) return; //we are done
			if (link != null && container.hasTransmitorLink(link)) {
				// we remove the link
				container.removeTransmitorLink(link);
			}
			if (link == null || !container.isValid()) {
				// we remove the container and continue
				matchingSignature.removeInstantiable(instantiable);
				for (VariableLink goingLink : matchingSignature.getVariableLinks()) {
					LiteralSignature linkedSignature = null;
					if (goingLink.getSignature1()==matchingSignature) linkedSignature = goingLink.getSignature2();
					else if (goingLink.getSignature2()==matchingSignature) linkedSignature = goingLink.getSignature1();
					else assert false;
					deleteInstantiable(instantiable, linkedSignature, goingLink);
				}
			}
		}
	}

	public List<SolverResult> addInstantiationValue(InstantiationValue value) {
		List<SolverResult> result = new ArrayList<SolverResult>();
		LiteralSignature signature = value.getSignature();
		if (!signature.hasInstantiationValue(value)) {
			signature.addInstantiationValue(value);
			for (Instantiable instantiable : signature.getInstantiables()) {
				result.add(new SolverResult(instantiable, value));
			}
		}
		return result;
	}
	
	public void removeInstantiationValue(InstantiationValue value) {
		value.getSignature().removeInstantiationValue(value);
	}
	

}