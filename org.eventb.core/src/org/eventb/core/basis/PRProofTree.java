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
import org.eventb.core.IPRPredicate;
import org.eventb.core.IPRPredicateSet;
import org.eventb.core.IPRProofTree;
import org.eventb.core.IPRProofTreeNode;
import org.eventb.core.IPRTypeEnvironment;
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
public class PRProofTree extends InternalElement implements IPRProofTree {

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

			// Write out the proof tree dependencies
			IProofDependencies proofDependencies = pt.getProofDependencies();
			
			((IPRPredicate)(prProofTree.createInternalElement(
					IPRPredicate.ELEMENT_TYPE,"goal",null,null))).
					setPredicate(proofDependencies.getGoal(), null);
			((IPRPredicateSet)(prProofTree.createInternalElement(
					IPRPredicateSet.ELEMENT_TYPE,"usedHypotheses",null,null))).
					setPredicateSet(Hypothesis.Predicates(proofDependencies.getUsedHypotheses()), null);
			((IPRTypeEnvironment)(prProofTree.createInternalElement(
					IPRTypeEnvironment.ELEMENT_TYPE,"usedFreeIdentifiers",null,null))).
					setTypeEnvironment(proofDependencies.getUsedFreeIdents(), null);
			((IPRTypeEnvironment)(prProofTree.createInternalElement(
					IPRTypeEnvironment.ELEMENT_TYPE,"introducedFreeIdentifiers",null,null))).
					setTypeEnvironment(proofDependencies.getIntroducedFreeIdents(), null);
			
			// write out the proof skeleton
			IPRProofTreeNode root = (IPRProofTreeNode)
			createInternalElement(PRProofTreeNode.ELEMENT_TYPE,"0",null,null);
			root.setProofTreeNode(pt.getRoot(), monitor);
			
			//	Update the status
			int confidence = pt.getConfidence();
			this.setConfidence(confidence, null);
	}
	
	public PRProofTreeNode getRoot() throws RodinDBException {
		IRodinElement[] proofTreeNodes = getChildrenOfType(IPRProofTreeNode.ELEMENT_TYPE);
		if (proofTreeNodes.length != 1) return null;
		return (PRProofTreeNode) proofTreeNodes[0];
	}
	
	public Set<Hypothesis> getUsedHypotheses(FormulaFactory factory, ITypeEnvironment typEnv, IProgressMonitor monitor) throws RodinDBException {
		InternalElement usedFreeIdents = getInternalElement(IPRPredicateSet.ELEMENT_TYPE,"usedHypotheses");
		// assert usedFreeIdents != null;
		if (! usedFreeIdents.exists()) return null;
		return Hypothesis.Hypotheses(((IPRPredicateSet)usedFreeIdents).getPredicateSet(factory, typEnv, null));
	}

	public ITypeEnvironment getUsedTypeEnvironment(FormulaFactory factory, IProgressMonitor monitor) throws RodinDBException {
		InternalElement usedFreeIdents = getInternalElement(IPRTypeEnvironment.ELEMENT_TYPE,"usedFreeIdentifiers");
		// assert usedFreeIdents != null;
		if (! usedFreeIdents.exists()) return null;
		return ((IPRTypeEnvironment)usedFreeIdents).getTypeEnvironment(factory, monitor);
	}
	
	public ITypeEnvironment getIntroducedTypeEnvironment(FormulaFactory factory, IProgressMonitor monitor) throws RodinDBException {
		InternalElement introducedFreeIdents = getInternalElement(IPRTypeEnvironment.ELEMENT_TYPE,"introducedFreeIdentifiers");
		// assert introducedFreeIdents != null;
		if (! introducedFreeIdents.exists()) return null;
		return ((IPRTypeEnvironment)introducedFreeIdents).getTypeEnvironment(factory, monitor);
	}
	
	public Predicate getGoal(FormulaFactory factory, ITypeEnvironment typEnv, IProgressMonitor monitor) throws RodinDBException {
		InternalElement goal = getInternalElement(IPRPredicate.ELEMENT_TYPE,"goal");
		assert goal != null;
		if (! goal.exists()) return null;
		return ((IPRPredicate)goal).getPredicate(factory, typEnv, monitor);
	}

	public IProofDependencies getProofDependencies(FormulaFactory factory, IProgressMonitor monitor) throws RodinDBException{
		ProofDependencies proofDependencies = new ProofDependencies(factory, monitor);
		return proofDependencies;
	}
	
	public int getConfidence(IProgressMonitor monitor) throws RodinDBException {
		return CommonAttributesUtil.getConfidence(this, monitor);
	}
	

	private void setConfidence(int confidence, IProgressMonitor monitor) throws RodinDBException {
		CommonAttributesUtil.setConfidence(this, confidence, monitor);
	}
	
	
	private class ProofDependencies implements IProofDependencies{

		final Predicate goal;
		final Set<Hypothesis> usedHypotheses;
		final ITypeEnvironment usedFreeIdents;
		final ITypeEnvironment introducedFreeIdents;
		final boolean valid;
		
		public ProofDependencies(FormulaFactory factory, IProgressMonitor monitor) throws RodinDBException{
			if (monitor == null) monitor = new NullProgressMonitor();
			try{
				monitor.beginTask("Reading Proof Dependencies", 4);
				usedFreeIdents = PRProofTree.this.getUsedTypeEnvironment(factory,new SubProgressMonitor(monitor,1));
				introducedFreeIdents = PRProofTree.this.getIntroducedTypeEnvironment(factory,new SubProgressMonitor(monitor,1));
				goal = PRProofTree.this.getGoal(factory,usedFreeIdents,new SubProgressMonitor(monitor,1));
				usedHypotheses = PRProofTree.this.getUsedHypotheses(factory, usedFreeIdents, new SubProgressMonitor(monitor,1));
				valid = (goal != null && 
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
			return valid;
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
		IPRProofTreeNode root = getRoot();
		if (root == null) return null;
		IProofSkeleton skeleton;
		try{
			monitor.beginTask("Reading Proof Skeleton", 11);
		final ITypeEnvironment typEnv = getUsedTypeEnvironment(factory,new SubProgressMonitor(monitor,1));
		skeleton = root.getSkeleton(factory,typEnv,new SubProgressMonitor(monitor,10));
		}
		finally
		{
			monitor.done();
		}
		return skeleton;
	}

	
}
