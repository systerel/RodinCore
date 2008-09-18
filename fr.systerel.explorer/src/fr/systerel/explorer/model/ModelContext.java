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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.IAxiom;
import org.eventb.core.IContextFile;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.ITheorem;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Maria Husmann
 *
 */
public class ModelContext extends ModelPOContainer implements IModelElement{
	public ModelContext(IContextFile file){
		internalContext = file;
		try {
			IAxiom[] axms = file.getChildrenOfType(IAxiom.ELEMENT_TYPE);
			for (int i = 0; i < axms.length; i++) {
				addAxiom(axms[i]);
			}
			ITheorem[] thms = file.getChildrenOfType(ITheorem.ELEMENT_TYPE);
			for (int i = 0; i < thms.length; i++) {
				addTheorem(thms[i]);
			}
			//TODO addInvariants etc.
			processPOFile();
			processPSFile();
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
	
	
	public void processPOFile() {
		try {
			IPOFile file = internalContext.getPOFile();
			IPOSequent[] sequents = file.getSequents();
			for (int i = 0; i < sequents.length; i++) {
				IPOSequent sequent =  sequents[i];
				ModelProofObligation po = new ModelProofObligation(sequent);
				po.setContext(this);
				proofObligations.put(sequent.getElementName(), po);
	
				IPOSource[] sources = sequents[i].getSources();
				for (int j = 0; j < sources.length; j++) {
					IRodinElement source = sources[j].getSource();
					if (source instanceof ITheorem) {
						if (theorems.containsKey(((ITheorem) source).getElementName())) {
							ModelTheorem thm = theorems.get(((ITheorem) source).getElementName());
							po.addTheorem(thm);
							thm.addProofObligation(po);
						}
					}
					if (source instanceof IAxiom) {
						if (axioms.containsKey(((IAxiom) source).getElementName())) {
							ModelAxiom axm = axioms.get(((IAxiom) source).getElementName());
							po.addAxiom(axm);
							axm.addProofObligation(po);
						}
					}
				}
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}

	public void processPSFile(){
		try {
			IPSFile file = internalContext.getPSFile();
			IPSStatus[] stats = file.getStatuses();
			for (int i = 0; i < stats.length; i++) {
				IPSStatus status = stats[i];
				IPOSequent sequent = status.getPOSequent();
				// check if there is a ProofObligation for this status (there should be one!)
				if (proofObligations.containsKey(sequent.getElementName())) {
					proofObligations.get(sequent.getElementName()).setIPSStatus(status);
				}
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}

	public void addAxiom(IAxiom axiom) {
		axioms.put(axiom.getElementName(), new ModelAxiom(axiom, this));
	}
	
	public void addTheorem(ITheorem theorem) {
		theorems.put(theorem.getElementName(), new ModelTheorem(theorem, this));
	}
	
	/**
	 * Assuming no cycles
	 * @return The longest branch among the extendedByContexts branches (including this Context)
	 */
	public List<ModelContext> getLongestContextBranch() {
		List<ModelContext> results = new LinkedList<ModelContext>();
		results.add(this);
		List<ModelContext> longest = new LinkedList<ModelContext>();
		for (Iterator<ModelContext> iterator = extendedByContexts.iterator(); iterator.hasNext();) {
			ModelContext context = iterator.next();
			if (context.getLongestContextBranch().size() > longest.size()) {
				longest = context.getLongestContextBranch();
			}
		}
		results.addAll(longest);
		return results;
	}
	
	/**
	 * Assuming no cycles
	 * @return All Ancestors of this context (extends context)
	 */
	public List<ModelContext> getAncestors(){
		List<ModelContext> results = new LinkedList<ModelContext>();
		for (Iterator<ModelContext> iterator = extendsContexts.iterator(); iterator.hasNext();) {
			ModelContext context = iterator.next();
			results.add(context);
			results.addAll(context.getAncestors());
		}
		return results;
		
	}
	
	/**
	 * 
	 * @return All the extenedByContexts, that are not returned by getLongestContextBranch
	 */
	public List<ModelContext> getRestContexts(){
		List<ModelContext> copy = new LinkedList<ModelContext>(extendedByContexts);
		copy.removeAll(getLongestContextBranch());
		return copy;
	}

	
	public void addExtendedByContext(ModelContext context){
		extendedByContexts.add(context);
	}

	public void addSeenByMachine(ModelMachine machine){
		seenByMachines.add(machine);
	}
	
	/**
	 * 
	 * @return is this Context a root of a tree of Contexts?
	 */
	public boolean isRoot(){
		return (extendsContexts.size() ==0);
	}
	
	/**
	 * 
	 * @return True, if this context is seen by no machine, otherwise false.
	 */
	public boolean isNotSeen() {
		return (seenByMachines.size() == 0);
	}

	public void addExtends(ModelContext context) {
		extendsContexts.add(context);
		
	}

	public IContextFile getInternalContext(){
		return internalContext;
	}

	public ModelTheorem getTheorem(ITheorem theorem){
		return theorems.get(theorem.getElementName());
	}

	public ModelAxiom getAxiom(IAxiom axiom){
		return axioms.get(axiom.getElementName());
	}

	public IModelElement getParent() {
		return ModelController.getProject(internalContext.getRodinProject());
	}
	
	
	/**
	 * The Contexts that extend this Context (children)
	 */
	private List<ModelContext> extendedByContexts = new LinkedList<ModelContext>();
	/**
	 * The Contexts that are extended by this Context (ancestors)
	 */
	private List<ModelContext> extendsContexts = new LinkedList<ModelContext>();
	private List<ModelMachine> seenByMachines = new LinkedList<ModelMachine>();
	private IContextFile internalContext;
	private HashMap<String, ModelAxiom> axioms = new HashMap<String, ModelAxiom>();
	private HashMap<String, ModelTheorem> theorems = new HashMap<String, ModelTheorem>();

}
