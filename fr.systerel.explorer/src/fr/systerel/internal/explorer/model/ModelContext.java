/*******************************************************************************
 * Copyright (c) 2008, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.IWitness;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * This class represents a Context in the model
 *
 */
public class ModelContext extends ModelPOContainer {
	
	/**
	 * The nodes are used by the ContentProviders to present a node in the tree
	 * above elements such as Axioms or Theorems.
	 */
	public final ModelElementNode carrierset_node;
	public final ModelElementNode constant_node;
	public final ModelElementNode axiom_node;
	public final ModelElementNode po_node;

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
	
	private IContextRoot contextRoot;

	private HashMap<IAxiom, ModelAxiom> axioms = new HashMap<IAxiom, ModelAxiom>();
	// CarrierSets and Constants are not taken into the model
	// because the don't have any proof obligations.
	
	/**
	 * All contexts that are above this context in the extends tree. (= context
	 * that are extended by this context or his ancestors)
	 */
	private ArrayList<ModelContext> ancestors =  new ArrayList<ModelContext>();
	
	/**
	 * The longest branch of contexts that extend this context. (including this
	 * context) The value of this is calculated in the ModelProject by calling
	 * <code>calculateContextBranches()</code>.
	 */
	private ArrayList<ModelContext> longestExtendsBranch =  new ArrayList<ModelContext>();

	//indicate whether the poRoot or the psRoot should be processed freshly
	public boolean psNeedsProcessing = true;
	public boolean poNeedsProcessing = true;

	
	/**
	 * Creates a ModelContext from a given IContextRoot
	 * @param root	The ContextRoot that this ModelContext is based on.
	 */
	public ModelContext(IContextRoot root){
		contextRoot = root;
		carrierset_node = new ModelElementNode(ICarrierSet.ELEMENT_TYPE, this);
		constant_node = new ModelElementNode(IConstant.ELEMENT_TYPE, this);
		axiom_node = new ModelElementNode(IAxiom.ELEMENT_TYPE, this);
		po_node = new ModelElementNode(IPSStatus.ELEMENT_TYPE, this);
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
	 * Adds all axioms and theorems found in the internalContext root.
	 */
	public void processChildren(){
		axioms.clear();
		try {
			for (IAxiom axm : contextRoot.getAxioms()) {
				addAxiom(axm);
			}
		} catch (RodinDBException e) {
			UIUtils.log(e, "when accessing axioms and theorems of "+contextRoot);
		}
	}
	
	/**
	 * Processes the PORoot that belongs to this context.
	 * It creates a ModelProofObligation for each sequent
	 * and adds it to this context as well as to the
	 * concerned Theorems and Axioms.
	 */
	public void processPORoot() {
		if (poNeedsProcessing) {
			try {
				//clear old POs
				proofObligations.clear();
				IPORoot root = contextRoot.getPORoot();
				if (root.exists()) {
					IPOSequent[] sequents = root.getSequents();
					int pos = 1;
					for (IPOSequent sequent : sequents) {
						ModelProofObligation po = new ModelProofObligation(sequent, pos);
						pos++;
						po.setContext(this);
						proofObligations.put(sequent, po);
			
						IPOSource[] sources = sequent.getSources();
						for (int j = 0; j < sources.length; j++) {
							IRodinElement source = sources[j].getSource();
							//only process sources that belong to this context.
							if (contextRoot.isAncestorOf(source)) {
								processSource(source, po);
							}
						}
					}
				}
			} catch (RodinDBException e) {
				UIUtils.log(e, "when processing proof obligations of " +contextRoot);
			}
			poNeedsProcessing = false;
		}
	}
	
	
	/**
	 * Processes the PSRoot that belongs to this Context. Each status is added to
	 * the corresponding Proof Obligation, if that ProofObligation is present.
	 */
	public void processPSRoot(){
		if (psNeedsProcessing) {
			try {
				IPSRoot root = contextRoot.getPSRoot();
				if (root.exists()) {
					IPSStatus[] stats = root.getStatuses();
					for (IPSStatus status : stats) {
						IPOSequent sequent = status.getPOSequent();
						// check if there is a ProofObligation for this status (there should be one!)
						if (proofObligations.containsKey(sequent)) {
							proofObligations.get(sequent).setIPSStatus(status);
						}
					}
				}
			} catch (RodinDBException e) {
				UIUtils.log(e, "when processing proof statuses of " +contextRoot);
			}
			psNeedsProcessing = false;
		}
	}
	
	/**
	 * Adds a new ModelAxiom to this Context.
	 * @param axiom The axiom to add.
	 */
	public void addAxiom(IAxiom axiom) {
		axioms.put(axiom, new ModelAxiom(axiom, this));
	}
	
	/**
	 * 
	 * @return All the extenedByContexts, that are not returned by
	 *         getLongestContextBranch. They will be added as children to this
	 *         context by the <code>ComplexContentContentProvider</code> in
	 *         the navigator tree.
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
	 * 
	 * @param context
	 *            The context that is extended by this context.
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
	 * 
	 * @param machine
	 *            The machine to add.
	 */
	public void addSeenByMachine(ModelMachine machine){
		if (!seenByMachines.contains(machine)) {
			seenByMachines.add(machine);
		}
	}

	/**
	 * Removes a machine that sees this context.
	 * 
	 * @param machine
	 *            The machine to remove.
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
	 * @return <code>true</code> if this Machine is a leaf of a tree of
	 *         Contexts? (= is extended by no other context). <code>false</code>
	 *         otherwise.
	 */
	public boolean isLeaf(){
		return (extendedByContexts.size() ==0);
	}
	
	/**
	 * 
	 * @return <code>true</code> , if this context is seen by no machine,
	 *         otherwise false.
	 */
	public boolean isNotSeen() {
		return (seenByMachines.size() == 0);
	}


	public IContextRoot getInternalContext(){
		return contextRoot;
	}

	public ModelAxiom getAxiom(IAxiom axiom){
		return axioms.get(axiom);
	}

	/**
	 * @return The Project that contains this Context.
	 */
	@Override
	public IModelElement getModelParent() {
		return ModelController.getProject(contextRoot.getRodinProject());
	}
	
	
	/**
	 * process the proof obligations if needed
	 * 
	 * @return the total number of Proof Obligations
	 */
	@Override
	public int getPOcount(){
		if (poNeedsProcessing || psNeedsProcessing) {
			processPORoot();
			processPSRoot();
		}
		return proofObligations.size();
	}
	
	/**
	 * process the proof obligations if needed
	 * 
	 * @return The number of undischarged Proof Obligations
	 */
	@Override
	public int getUndischargedPOcount() {
		if (poNeedsProcessing || psNeedsProcessing) {
			processPORoot();
			processPSRoot();
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
	public IRodinElement getInternalElement() {
		return contextRoot;
	}
	
	/**
	 * Processes a source belonging to a given Proof Obligation
	 * 
	 * @param source
	 *            The source to process
	 * @param po
	 *            The proof obligation the source belongs to
	 */
	protected void processSource (IRodinElement source, ModelProofObligation po) {
		if (source instanceof IWitness ) {
			source = source.getParent();
		}
		if (source instanceof IAxiom) {
			if (axioms.containsKey(source)) {
				ModelAxiom axm = axioms.get(source);
				po.addAxiom(axm);
				axm.addProofObligation(po);
			}
		}
		
	}
	
	public IModelElement getModelElement(IRodinElement element) {
		if (element instanceof IAxiom ) {
			return axioms.get(element);
		}
		return null;
	}

	/**
	 * In the complex version this gets the first abstract context of this
	 * context. If none exist or in the non-complex version this returns the
	 * containing project.
	 */
	@Override
	public Object getParent(boolean complex) {
		if (complex) {
			if (extendsContexts.size() > 0) {
				return (extendsContexts.get(0).getInternalContext());
			}
		}
		return contextRoot.getRodinProject();
	}


	@Override
	public Object[] getChildren(IInternalElementType<?> type, boolean complex) {

		if (type == IContextRoot.ELEMENT_TYPE) {
        	List<ModelContext> rest = getRestContexts();
        	List<ModelContext> result = new LinkedList<ModelContext>();
        	for (ModelContext ctx : rest) {
				result.addAll(ctx.getLongestBranch());
			}
        	return ModelController.convertToIContext(result).toArray();
			
		}
		
		if (poNeedsProcessing || psNeedsProcessing) {
			processPORoot();
			processPSRoot();
		}
		
		if (type == IAxiom.ELEMENT_TYPE) {
			return new Object[]{axiom_node};
		}
		if (type == IConstant.ELEMENT_TYPE) {
			return new Object[]{constant_node};
		}
		if (type == ICarrierSet.ELEMENT_TYPE) {
			return new Object[]{carrierset_node};
		}
		if (type == IPSStatus.ELEMENT_TYPE) {
			return new Object[]{po_node};
		}
		if (ExplorerUtils.DEBUG) {
			System.out.println("Unsupported children type for context: " +type);
		}
		return new Object[0];
	}
	

}
