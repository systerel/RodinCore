/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core.protopog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPODescription;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOHypothesis;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 */
public class ProofObligation {

	public final String name;
	public final ITypeEnvironment typeEnvironment;
	public final String globalHypothesis;
	public final ArrayList<Predicate> localHypothesis;
	public final Predicate goal;
	public final String desc;
	public final HashMap<String, String> hints;
	public final HashMap<String, String> sources;
	
	private void putPredicate(String predName, IInternalParent element, Predicate predicate, IProgressMonitor monitor) throws RodinDBException {
		IPOPredicate poPredicate = (IPOPredicate) element.createInternalElement(IPOPredicate.ELEMENT_TYPE, predName, null, monitor);
		poPredicate.setContents(predicate.toStringWithTypes(), monitor);
	}
	
	public void put(IPOFile file, IProgressMonitor monitor) throws RodinDBException {
		IPOSequent sequent = (IPOSequent) file.createInternalElement(
				IPOSequent.ELEMENT_TYPE, name, null, monitor);
		putTypeEnvironment(sequent, monitor);
		IPOHypothesis hypothesis = 
			(IPOHypothesis) sequent.createInternalElement(
					IPOHypothesis.ELEMENT_TYPE, "global-hyps", null, monitor);
		hypothesis.setContents(globalHypothesis, monitor);
		int idx = 1;
		for (Predicate predicate : localHypothesis) {
			putPredicate("l" + idx++, hypothesis, predicate, monitor);
		}
		putPredicate("goal", sequent, goal, monitor);
		IPODescription description = (IPODescription) sequent.createInternalElement(IPODescription.ELEMENT_TYPE, desc, null, monitor);
		for(Entry<String, String> entry : sources.entrySet()) {
			String role = entry.getKey();
			String handle = entry.getValue();
			IPOSource source = (IPOSource) description.createInternalElement(IPOSource.ELEMENT_TYPE, role, null, monitor);
			source.setContents(handle, monitor);
		}
		// TODO: output hints
	}
	
	private void putTypeEnvironment(IPOSequent sequent, IProgressMonitor monitor)
			throws RodinDBException {
		
		ITypeEnvironment.IIterator iter = typeEnvironment.getIterator();
		while (iter.hasNext()) {
			iter.advance();
			final String identName = iter.getName();
			final Type identType = iter.getType();
			final IPOIdentifier ident = (IPOIdentifier) sequent
					.createInternalElement(IPOIdentifier.ELEMENT_TYPE, identName,
							null, monitor);
			ident.setContents(identType.toString());
		}
	}

	@Override
	public String toString() {
		String result = name + "\n" + typeEnvironment.toString() + "\n" + globalHypothesis + "\n";
		for(Predicate predicate : localHypothesis) {
			result += predicate.toString() + "\n";
		}
		result += "=>\n" + goal.toString() + "\n";
		result += hints.toString();
		return result;
	}
	
	public ProofObligation(
			String name, 
			ITypeEnvironment typeEnvironment, 
			String globalHypothesis, 
			ArrayList<Predicate> localHypothesis, 
			Predicate goal, 
			String desc,
			HashMap<String, String> hints,
			HashMap<String, String> sources) {
		this.name = name;
		this.typeEnvironment = typeEnvironment;
		this.globalHypothesis = globalHypothesis;
		this.localHypothesis = localHypothesis;
		this.goal = goal;
		this.desc = desc;
		this.hints = hints;
		this.sources = sources;
	}
	
	public ProofObligation(
			String name, 
			String globalHypothesis, 
			Predicate goal,
			String desc) {
		this.name = name;
		this.typeEnvironment = FormulaFactory.getDefault().makeTypeEnvironment();
		this.globalHypothesis = globalHypothesis;
		this.localHypothesis = new ArrayList<Predicate>();
		this.goal = goal;
		this.desc = desc;
		this.hints = new HashMap<String, String>(5);
		this.sources = new HashMap<String, String>(11);
	}
	
}
