/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.IPRProofStore;
import org.eventb.core.IPRProofTree;
import org.eventb.core.IPRProofTreeNode;
import org.eventb.core.IPRTypeEnvironment;
import org.eventb.core.IProofStoreCollector;
import org.eventb.core.IProofStoreReader;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.proofBuilder.IProofSkeleton;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author Farhad Mehta
 *
 */
public class PRProofTree extends EventBProofElement implements IPRProofTree {

	public PRProofTree(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}

	public void initialize(IProofMonitor monitor) throws RodinDBException {
		//delete previous children, if any.
		if (this.getChildren().length != 0)
			this.getRodinDB().delete(this.getChildren(),true,null);
		setConfidence(IConfidence.UNATTEMPTED,null);
	}
	

	public void setProofTree(IProofTree pt, IProgressMonitor monitor) throws RodinDBException {
		PRProofTree prProofTree = this;
		if (prProofTree.hasChildren())
			getRodinDB().delete(prProofTree.getChildren(),true,null);

		
		// compute proof tree dependencies
		IProofDependencies proofDependencies = pt.getProofDependencies();

		// Construct a new proof store
		IProofStoreCollector store = new ProofStoreCollector(proofDependencies.getUsedFreeIdents());

		
		// Write out the proof tree dependencies		
		setGoal(proofDependencies.getGoal(), store, null);
		setHyps(Hypothesis.Predicates(proofDependencies.getUsedHypotheses()),store,null);


		((IPRTypeEnvironment)(prProofTree.createInternalElement(
				IPRTypeEnvironment.ELEMENT_TYPE,"introducedFreeIdentifiers",null,null))).
				setTypeEnvironment(proofDependencies.getIntroducedFreeIdents(), null);

		// write out the proof skeleton
		IPRProofTreeNode root = (IPRProofTreeNode)
		createInternalElement(PRProofTreeNode.ELEMENT_TYPE,"0",null,null);
		root.setSkeleton(pt.getRoot(), store, null);

		//	Update the status
		int confidence = pt.getConfidence();
		setConfidence(confidence, null);
		
		IPRProofStore prStore = ((IPRProofStore)(prProofTree.createInternalElement(
				IPRProofStore.ELEMENT_TYPE,"proofStore",null,null)));
		store.writeOut(prStore, null);
	}
	
	public ITypeEnvironment getIntroducedTypeEnvironment(FormulaFactory factory, IProgressMonitor monitor) throws RodinDBException {
		InternalElement introducedFreeIdents = getInternalElement(IPRTypeEnvironment.ELEMENT_TYPE,"introducedFreeIdentifiers");
		// assert introducedFreeIdents != null;
		if (! introducedFreeIdents.exists()) return null;
		return ((IPRTypeEnvironment)introducedFreeIdents).getTypeEnvironment(factory, monitor);
	}

	public IProofDependencies getProofDependencies(FormulaFactory factory, IProgressMonitor monitor) throws RodinDBException{
		IPRProofStore prStore = (IPRProofStore) getInternalElement(IPRProofStore.ELEMENT_TYPE, "proofStore");
		IProofStoreReader store = new ProofStoreReader(prStore ,factory);
		ProofDependencies proofDependencies = new ProofDependencies(factory, store, monitor);
		return proofDependencies;
	}
	
	private class ProofDependencies implements IProofDependencies{

		final Predicate goal;
		final Set<Hypothesis> usedHypotheses;
		final ITypeEnvironment usedFreeIdents;
		final ITypeEnvironment introducedFreeIdents;
		final boolean hasDeps;
		
		public ProofDependencies(FormulaFactory factory, IProofStoreReader store, IProgressMonitor monitor) throws RodinDBException{
			if (monitor == null) monitor = new NullProgressMonitor();
			final int confidence = PRProofTree.this.getConfidence(monitor);
			if (confidence <= IConfidence.UNATTEMPTED){
				hasDeps = false;
				goal = null;
				usedHypotheses = null;
				usedFreeIdents = null;
				introducedFreeIdents = null;
				return;
			}
			
			try{
				monitor.beginTask("Reading Proof Dependencies", 4);
				// usedFreeIdents = PRProofTree.this.getUsedTypeEnvironment(factory,new SubProgressMonitor(monitor,1));
				usedFreeIdents = store.getBaseTypeEnv(null);
				introducedFreeIdents = PRProofTree.this.getIntroducedTypeEnvironment(factory,new SubProgressMonitor(monitor,1));
				//goal = PRProofTree.this.getGoal(factory,usedFreeIdents,new SubProgressMonitor(monitor,1));
				goal = PRProofTree.this.getGoal(store, new SubProgressMonitor(monitor,1));
				// usedHypotheses = PRProofTree.this.getUsedHypotheses(factory, usedFreeIdents, new SubProgressMonitor(monitor,1));
				usedHypotheses = Hypothesis.Hypotheses(PRProofTree.this.getHyps(store, new SubProgressMonitor(monitor,1)));
				hasDeps = (goal != null && 
						usedHypotheses != null && 
						usedFreeIdents != null &&
						introducedFreeIdents != null);
			}
			finally
			{
				monitor.done();
			}
		}

		public boolean hasDeps() {
			return hasDeps;
		}
		
		/**
		 * @return Returns the goal.
		 */
		public Predicate getGoal() {
			return goal;
		}

		/**
		 * @return Returns the introducedFreeIdents.
		 */
		public ITypeEnvironment getIntroducedFreeIdents() {
			return introducedFreeIdents;
		}

		/**
		 * @return Returns the usedFreeIdents.
		 */
		public ITypeEnvironment getUsedFreeIdents() {
			return usedFreeIdents;
		}

		/**
		 * @return Returns the usedHypotheses.
		 */
		public Set<Hypothesis> getUsedHypotheses() {
			return usedHypotheses;
		}
		
	}
	
	public IProofSkeleton getSkeleton(FormulaFactory factory, IProgressMonitor monitor) throws RodinDBException {
		if (monitor == null) monitor = new NullProgressMonitor();
		IPRProofTreeNode root = (IPRProofTreeNode) getInternalElement(IPRProofTreeNode.ELEMENT_TYPE, "0");

		if (! root.exists()) return null;
		IProofSkeleton skeleton;
		try{
			monitor.beginTask("Reading Proof Skeleton", 11);
			// final ITypeEnvironment typEnv = getUsedTypeEnvironment(factory,new SubProgressMonitor(monitor,1));
			IPRProofStore prStore = (IPRProofStore) getInternalElement(IPRProofStore.ELEMENT_TYPE, "proofStore");
			IProofStoreReader store = new ProofStoreReader(prStore ,factory);
			skeleton = root.getSkeleton(store,new SubProgressMonitor(monitor,10));
		}
		finally
		{
			monitor.done();
		}
		return skeleton;
	}

	
}
