/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.Set;

import org.eventb.core.IPRFile;
import org.eventb.core.IPRPredicate;
import org.eventb.core.IPRPredicateSet;
import org.eventb.core.IPRProofTree;
import org.eventb.core.IPRProofTreeNode;
import org.eventb.core.IPRSequent;
import org.eventb.core.IPRTypeEnvironment;
import org.eventb.core.IPair;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.internal.core.pom.AutoPOM;
import org.rodinp.core.IInternalElement;
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
	public String getElementType() {
		return ELEMENT_TYPE;
	}

	public void initialize() throws RodinDBException {
		//delete previous children, if any.
		if (this.getChildren().length != 0)
			this.getRodinDB().delete(this.getChildren(),true,null);
		setConfidence(IConfidence.PENDING);
	}
	

	public void setProofTree(IProofTree pt) throws RodinDBException {
		// TODO Auto-generated method stub
		PRProofTree prProofTree = this;
		if (prProofTree.hasChildren())
			getRodinDB().delete(prProofTree.getChildren(),true,null);

			// Write out the proof tree dependencies
			IProofDependencies proofDependencies = pt.getProofDependencies();
			
			((IPRPredicate)(prProofTree.createInternalElement(
					IPRPredicate.ELEMENT_TYPE,"goal",null,null))).
					setPredicate(proofDependencies.getGoal());
			((IPRPredicateSet)(prProofTree.createInternalElement(
					IPRPredicateSet.ELEMENT_TYPE,"usedHypotheses",null,null))).
					setPredicateSet(Hypothesis.Predicates(proofDependencies.getUsedHypotheses()));
			((IPRTypeEnvironment)(prProofTree.createInternalElement(
					IPRTypeEnvironment.ELEMENT_TYPE,"usedFreeIdentifiers",null,null))).
					setTypeEnvironment(proofDependencies.getUsedFreeIdents());
			((IPRTypeEnvironment)(prProofTree.createInternalElement(
					IPRTypeEnvironment.ELEMENT_TYPE,"introducedFreeIdentifiers",null,null))).
					setTypeEnvironment(proofDependencies.getIntroducedFreeIdents());
			
			// write out the proof skeleton
			IPRProofTreeNode root = (IPRProofTreeNode)
			createInternalElement(PRProofTreeNode.ELEMENT_TYPE,"0",null,null);
			root.setProofTreeNode(pt.getRoot());
			
			//	Update the status
			int confidence = pt.getConfidence();
			this.setConfidence(confidence);
	}
	
	public IPRSequent getSequent() {
		IInternalElement prSequent = ((IPRFile)getOpenable()).getInternalElement(IPRSequent.ELEMENT_TYPE,this.getElementName());
		assert prSequent != null;
		if (! prSequent.exists()) return null;
		return ((IPRSequent)prSequent);
	}
	
	public boolean isClosed() throws RodinDBException {
		return getConfidence() != IConfidence.PENDING;
	}
	
	public PRProofTreeNode getRoot() throws RodinDBException {
		IRodinElement[] proofTreeNodes = getChildrenOfType(IPRProofTreeNode.ELEMENT_TYPE);
		if (proofTreeNodes.length != 1) return null;
		return (PRProofTreeNode) proofTreeNodes[0];
	}

	// TODO : eventually remove
	public Set<Predicate> getUsedHypotheses() throws RodinDBException {
		IRodinElement[] usedHypotheses = getChildrenOfType(IPRPredicateSet.ELEMENT_TYPE);
		if (usedHypotheses.length != 1) return null;
		assert usedHypotheses.length == 1;
		assert usedHypotheses[0].getElementName().equals("usedHypotheses");
		return ((IPRPredicateSet)usedHypotheses[0]).getPredicateSet();
	}
	
	public Set<Hypothesis> getUsedHypotheses_() throws RodinDBException {
		InternalElement usedFreeIdents = getInternalElement(IPRPredicateSet.ELEMENT_TYPE,"usedHypotheses");
		assert usedFreeIdents != null;
		if (! usedFreeIdents.exists()) return null;
		return Hypothesis.Hypotheses(((IPRPredicateSet)usedFreeIdents).getPredicateSet());
	}

	public ITypeEnvironment getUsedTypeEnvironment() throws RodinDBException {
		InternalElement usedFreeIdents = getInternalElement(IPRTypeEnvironment.ELEMENT_TYPE,"usedFreeIdentifiers");
		assert usedFreeIdents != null;
		if (! usedFreeIdents.exists()) return null;
		return ((IPRTypeEnvironment)usedFreeIdents).getTypeEnvironment();
	}
	
	public ITypeEnvironment getIntroducedTypeEnvironment() throws RodinDBException {
		InternalElement introducedFreeIdents = getInternalElement(IPRTypeEnvironment.ELEMENT_TYPE,"introducedFreeIdentifiers");
		assert introducedFreeIdents != null;
		if (! introducedFreeIdents.exists()) return null;
		return ((IPRTypeEnvironment)introducedFreeIdents).getTypeEnvironment();
	}
	
	public Predicate getGoal() throws RodinDBException {
		InternalElement goal = getInternalElement(IPRPredicate.ELEMENT_TYPE,"goal");
		assert goal != null;
		if (! goal.exists()) return null;
		return ((IPRPredicate)goal).getPredicate();
	}

	public boolean proofAttempted() throws RodinDBException {
		IRodinElement[] proofTreeNodes = getChildrenOfType(IPRProofTreeNode.ELEMENT_TYPE);
		if (proofTreeNodes.length == 0) return false;
		
		PRProofTreeNode rootProofTreeNode = getRoot();
		return !(rootProofTreeNode.getRule()==null && rootProofTreeNode.getComment().length() == 0);
	}

	public IProofDependencies getProofDependencies() throws RodinDBException{
		ProofDependencies proofDependencies = new ProofDependencies();
		if (proofDependencies.valid == false)
		{
			if (AutoPOM.DEBUG) {
				System.out.println("*** Proof Deps invalid for "+this+" ***");
				System.out.println("*** Proof attempted "+this.proofAttempted()+" ***");
			}
			return null;
		}
		return proofDependencies;
	}

	public int getConfidence() throws RodinDBException {
		InternalElement confidence = getInternalElement(IPair.ELEMENT_TYPE,"confidence");
		assert confidence != null;
		if (! confidence.exists()) throw confidence.newNotPresentException();
		return Integer.parseInt(confidence.getContents());
	}
	
	public void setConfidence(int confidence) throws RodinDBException{
		InternalElement confidenceAtt = getInternalElement(IPair.ELEMENT_TYPE,"confidence");
		assert confidenceAtt != null;
		if (! confidenceAtt.exists())
			{
				this.createInternalElement(IPair.ELEMENT_TYPE,"confidence",null,null).
				setContents(Integer.toString(confidence));
				return;
			}
		confidenceAtt.setContents(Integer.toString(confidence));
	}
	
	public boolean isAutomaticallyGenerated() throws RodinDBException {
		InternalElement autoGenerated = getInternalElement(IPair.ELEMENT_TYPE,"automaticallyGenerated");
		assert autoGenerated != null;
		if (! autoGenerated.exists()) return false;
		return true;
	}

	public void setAutomaticallyGenerated() throws RodinDBException{
		if (! isAutomaticallyGenerated())
			this.createInternalElement(IPair.ELEMENT_TYPE,"automaticallyGenerated",null,null);
	}
	
	private class ProofDependencies implements IProofDependencies{

		final Predicate goal;
		final Set<Hypothesis> usedHypotheses;
		final ITypeEnvironment usedFreeIdents;
		final ITypeEnvironment introducedFreeIdents;
		final boolean valid;
		
		/**
		 * 
		 */
		public ProofDependencies() throws RodinDBException{
			goal = PRProofTree.this.getGoal();
			usedHypotheses = PRProofTree.this.getUsedHypotheses_();
			usedFreeIdents = PRProofTree.this.getUsedTypeEnvironment();
			introducedFreeIdents = PRProofTree.this.getIntroducedTypeEnvironment();
			valid = (goal != null && 
					usedHypotheses != null && 
					usedFreeIdents != null &&
					introducedFreeIdents != null);
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

	
}
