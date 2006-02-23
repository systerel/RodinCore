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
import org.eventb.core.IPOModifiedPredicate;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.ast.BecomesEqualTo;
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

	public abstract static class Form { 
		// abstract base class
		
		public abstract void put(IInternalParent element, IProgressMonitor monitor) throws RodinDBException;
	}

	
	public static class PForm extends Form {
		public final Predicate predicate;
		public PForm(Predicate predicate) {
			this.predicate = predicate;
		}
		@Override
		public void put(IInternalParent element, IProgressMonitor monitor) throws RodinDBException {
			IPOPredicate poPredicate = (IPOPredicate) element.createInternalElement(IPOPredicate.ELEMENT_TYPE, null, null, monitor);
			poPredicate.setContents(this.predicate.toString(), monitor);
		}
		
		@Override
		public String toString() {
			return predicate.toString();
		}
	}
	
	public static class SForm extends Form {
		public final BecomesEqualTo substitution;
		public final Form form;
		public SForm(BecomesEqualTo substitution, Form form) {
			this.substitution = substitution;
			this.form = form;
		}
		@Override
		public void put(IInternalParent element, IProgressMonitor monitor) throws RodinDBException {
			IPOModifiedPredicate predicateForm = (IPOModifiedPredicate) element.createInternalElement(IPOModifiedPredicate.ELEMENT_TYPE, null, null, monitor);
			predicateForm.setContents(this.substitution.toString());
			form.put(predicateForm, monitor);
		}
		
		@Override
		public String toString() {
			return "[" + substitution.toString() + "] " + form.toString();
		}
	}

	public final String name;
	public final ITypeEnvironment typeEnvironment;
	public final String globalHypothesis;
	public final ArrayList<Form> localHypothesis;
	public final Form goal;
	public final String desc;
	public final HashMap<String, String> hints;
	public final HashMap<String, String> sources;
	
	public void put(IPOFile file, IProgressMonitor monitor) throws RodinDBException {
		IPOSequent sequent = (IPOSequent) file.createInternalElement(
				IPOSequent.ELEMENT_TYPE, name, null, monitor);
		putTypeEnvironment(sequent, monitor);
		IPOHypothesis hypothesis = 
			(IPOHypothesis) sequent.createInternalElement(
					IPOHypothesis.ELEMENT_TYPE, null, null, monitor);
		hypothesis.setContents(globalHypothesis, monitor);
		for (Form form : localHypothesis) {
			form.put(hypothesis, monitor);
		}
		goal.put(sequent, monitor);
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
		for(Form form : localHypothesis) {
			result += form.toString() + "\n";
		}
		result += "=>\n" + goal.toString() + "\n";
		result += hints.toString();
		return result;
	}
	
	public ProofObligation(
			String name, 
			ITypeEnvironment typeEnvironment, 
			String globalHypothesis, 
			ArrayList<Form> localHypothesis, 
			Form goal, 
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
			Form goal,
			String desc) {
		this.name = name;
		this.typeEnvironment = FormulaFactory.getDefault().makeTypeEnvironment();
		this.globalHypothesis = globalHypothesis;
		this.localHypothesis = new ArrayList<Form>();
		this.goal = goal;
		this.desc = desc;
		this.hints = new HashMap<String, String>(5);
		this.sources = new HashMap<String, String>(11);
	}
	
}
