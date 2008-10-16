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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.ITheorem;
import org.eventb.core.IWitness;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * This class represents a Context in the model
 *
 */
public class ModelContext extends ModelPOContainer implements IModelElement{
	
	//The indexes for the IElement nodes in array
	public static final int CARRIERSET_NODE = 0;
	public static final int CONSTANT_NODE = 1;
	public static final int AXIOM_NODE = 2;
	public static final int THEOREM_NODE = 3;
	public static final int PO_NODE = 4;

	/**
	 * The Contexts that extend this Context (children)
	 */
	private List<ModelContext> extendedByContexts = new LinkedList<ModelContext>();
	/**
	 * The Contexts that are extended by this Context (ancestors)
	 */
	private List<ModelContext> extendsContexts = new LinkedList<ModelContext>();
	/**
	 * The Machines that see this context.
	 */
	private List<ModelMachine> seenByMachines = new LinkedList<ModelMachine>();
	
	private IContextFile internalContext;
	private HashMap<String, ModelAxiom> axioms = new HashMap<String, ModelAxiom>();
	private HashMap<String, ModelTheorem> theorems = new HashMap<String, ModelTheorem>();
	
	/**
	 * All contexts that are above this context in the extends tree. 
	 * (= context that are extended by this context or his ancestors)
	 */
	private ArrayList<ModelContext> ancestors =  new ArrayList<ModelContext>();
	
	/**
	 * The longest branch of contexts that extend this context.
	 * (including this context)
	 * The value of this is calculated in the ModelProject by calling
	 * <code>calculateContextBranches()</code>.
	 */
	private ArrayList<ModelContext> longestExtendsBranch =  new ArrayList<ModelContext>();

	//indicate whether the poFile or the psFile should be processed freshly
	public boolean psNeedsProcessing = true;
	public boolean poNeedsProcessing = true;

	/**
	 * The nodes are used by the ContextProviders to present a node in the tree
	 * above elements such as Axioms or Theorems.
	 */
	public ModelElementNode[] nodes;
	
	/**
	 * Creates a ModelContext from a given IContextFile
	 * @param file	The ContextFile that this ModelContext is based on.
	 */
	public ModelContext(IContextFile file){
		internalContext = file;
		nodes = new ModelElementNode[5];
		nodes[CARRIERSET_NODE] = new ModelElementNode(ICarrierSet.ELEMENT_TYPE, this);
		nodes[CONSTANT_NODE] = new ModelElementNode(IConstant.ELEMENT_TYPE, this);
		nodes[AXIOM_NODE] = new ModelElementNode(IAxiom.ELEMENT_TYPE, this);
		nodes[THEOREM_NODE] = new ModelElementNode(ITheorem.ELEMENT_TYPE, this);
		nodes[PO_NODE] = new ModelElementNode(IPSStatus.ELEMENT_TYPE, this);
	}
	
	public void addAncestor(ModelContext context){
		if (!ancestors.contains(context)) {
			ancestors.add(context);
		}
	}
	
	public void addAncestors(ArrayList<ModelContext> contexts){
		ancestors.addAll(contexts);
	}

	public void resetAncestors(){
		ancestors = new ArrayList<ModelContext>();
	}

	public ArrayList<ModelContext> getAncestors(){
		return ancestors;
	}

	public void addToLongestBranch(ModelContext context){
		if (!longestExtendsBranch.contains(context)) {
			longestExtendsBranch.add(context);
		}
	}
	
	/**
	 * <code>calculateContextBranches()</code> has to be called before this, 
	 * to get an result that is up to date.
	 * 
	 * @return The longest branch of children (contexts that extend this context)
	 * 			including this context.
	 */
	public ArrayList<ModelContext> getLongestBranch(){
		//contains at least itself
		if (longestExtendsBranch.size() == 0) {
			longestExtendsBranch.add(this);
		}
		return longestExtendsBranch;
	}
	
	public void setLongestBranch(ArrayList<ModelContext> branch){
		longestExtendsBranch = branch;
	}
	
	/**
	 * Processes the children of this Context:
	 * Clears existing axioms and theorems.
	 * Adds all axioms and theorems found in the internalContext file.
	 */
	public void processChildren(){
		axioms.clear();
		theorems.clear();
		try {
			IAxiom[] axms = internalContext.getChildrenOfType(IAxiom.ELEMENT_TYPE);
			for (IAxiom axm : axms) {
				addAxiom(axm);
			}
			ITheorem[] thms = internalContext.getChildrenOfType(ITheorem.ELEMENT_TYPE);
			for (ITheorem thm :  thms) {
				addTheorem(thm);
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
	
	/**
	 * Processes the POFile that belongs to this context.
	 * It creates a ModelProofObligation for each sequent
	 * and adds it to this context as well as to the
	 * concerned Theorems and Axioms.
	 */
	public void processPOFile() {
		if (poNeedsProcessing) {
			try {
				//clear old POs
				proofObligations.clear();
				IPOFile file = internalContext.getPOFile();
				if (file.exists()) {
					IPOSequent[] sequents = file.getSequents();
					for (IPOSequent sequent : sequents) {
						ModelProofObligation po = new ModelProofObligation(sequent);
						po.setContext(this);
						proofObligations.put(sequent.getElementName(), po);
			
						IPOSource[] sources = sequent.getSources();
						for (int j = 0; j < sources.length; j++) {
							IRodinElement source = sources[j].getSource();
							//only process sources that belong to this context.
							if (internalContext.equals(source.getAncestor(IContextFile.ELEMENT_TYPE))) {
								if (source instanceof IWitness ) {
									source = source.getParent();
								}
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
					}
				}
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
	//			e.printStackTrace();
			}
			poNeedsProcessing = false;
		}
	}
	
	/**
	 * Processes the PSFile that belongs to this Context
	 * Each status is added to the corresponding Proof Obligation,
	 * if that ProofObligation is present.
	 */
	public void processPSFile(){
		if (psNeedsProcessing) {
			try {
				IPSFile file = internalContext.getPSFile();
				if (file.exists()) {
					IPSStatus[] stats = file.getStatuses();
					for (IPSStatus status : stats) {
						IPOSequent sequent = status.getPOSequent();
						// check if there is a ProofObligation for this status (there should be one!)
						if (proofObligations.containsKey(sequent.getElementName())) {
							proofObligations.get(sequent.getElementName()).setIPSStatus(status);
						}
					}
				}
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
	//			e.printStackTrace();
			}
			psNeedsProcessing = false;
		}
	}
	
	/**
	 * Adds a new ModelAxiom to this Context.
	 * @param axiom The axiom to add.
	 */
	public void addAxiom(IAxiom axiom) {
		axioms.put(axiom.getElementName(), new ModelAxiom(axiom, this));
	}
	
	/**
	 * Adds a new ModelTheorem to this Context
	 * @param theorem The Theorem to add.
	 */
	public void addTheorem(ITheorem theorem) {
		theorems.put(theorem.getElementName(), new ModelTheorem(theorem, this));
	}
	
	/**
	 * 
	 * @return All the extenedByContexts, that are not returned by getLongestContextBranch
	 */
	public List<ModelContext> getRestContexts(){
		List<ModelContext> copy = new LinkedList<ModelContext>(extendedByContexts);
		copy.removeAll(getLongestBranch());
		return copy;
	}

	/**
	 * Adds a context that extends this context.
	 * @param context	The context to add
	 */
	public void addExtendedByContext(ModelContext context){
		if (!extendedByContexts.contains(context)) {
			extendedByContexts.add(context);
		}
	}

	public void removeExtendedByContext(ModelContext context){
		extendedByContexts.remove(context);
	}

	public List<ModelContext> getExtendedByContexts() {
		return extendedByContexts;
	}
	
	/**
	 * Adds a context that this context extends
	 * @param context	The context that is extended by this context.
	 */
	public void addExtendsContext(ModelContext context) {
		//only add new contexts
		if (!extendsContexts.contains(context)) {
			extendsContexts.add(context);
		}
		
	}
	
	public void removeExtendsContext(ModelContext context){
		extendsContexts.remove(context);
	}
	
	public List<ModelContext> getExtendsContexts() {
		return extendsContexts;
	}

	public void resetExtendsContexts() {
		extendsContexts = new LinkedList<ModelContext>();
	}
	

	/**
	 * Adds a machine that sees this context.
	 * @param machine	The machine to add.
	 */
	public void addSeenByMachine(ModelMachine machine){
		if (!seenByMachines.contains(machine)) {
			seenByMachines.add(machine);
		}
	}

	/**
	 * Removes a machine that sees this context.
	 * @param machine	The machine to remove.
	 */
	public void removeSeenByMachine(ModelMachine machine){
		seenByMachines.remove(machine);
	}
	
	public List<ModelMachine> getSeenByMachines(){
		return seenByMachines;
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
	 * @return is this Machine a leaf of a tree of Contexts?
	 * (= is extended by no other context)
	 */
	public boolean isLeaf(){
		return (extendedByContexts.size() ==0);
	}
	
	/**
	 * 
	 * @return True, if this context is seen by no machine, otherwise false.
	 */
	public boolean isNotSeen() {
		return (seenByMachines.size() == 0);
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

	/**
	 * @return The Project that contains this Context.
	 */
	@Override
	public IModelElement getModelParent() {
		return ModelController.getProject(internalContext.getRodinProject());
	}
	
	
	/**
	 * process the proof obligations if needed
	 * @return the total number of Proof Obligations
	 */
	@Override
	public int getPOcount(){
		if (poNeedsProcessing || psNeedsProcessing) {
			processPOFile();
			processPSFile();
		}
		return proofObligations.size();
	}
	
	/**
	 * process the proof obligations if needed
	 * @return The number of undischarged Proof Obligations
	 */
	@Override
	public int getUndischargedPOcount() {
		if (poNeedsProcessing || psNeedsProcessing) {
			processPOFile();
			processPSFile();
		}
		int result = 0;
		for (ModelProofObligation po : proofObligations.values()) {
			if (!po.isDischarged()) {
				result++;
			}
		}
		return result;
		
	}
	
	@Override
	public String getLabel() {
		return "Context " +internalContext.getBareName();
	}

	public IRodinElement getInternalElement() {
		return internalContext;
	}

}
