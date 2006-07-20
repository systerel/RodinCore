/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.prover.rules;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.prover.IProofDependencies;
import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.IProofTreeChangedListener;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

/**
 * Implementation of a proof tree, with observer design pattern.
 * 
 * @author Laurent Voisin
 */
public final class ProofTree implements IProofTree {


	/**
	 * The delta processor for this tree
	 */
	final DeltaProcessor deltaProcessor;
	
	/**
	 * The root Proof Tree Node
	 */
	final ProofTreeNode root;

	/**
	 * Creates a new proof tree for the given sequent.
	 * 
	 * Clients must not call this constructor, but rather the factory method in
	 * {@link org.eventb.core.prover.SequentProver}.
	 */
	public ProofTree(IProverSequent sequent) {
		root = new ProofTreeNode(this, sequent);
		deltaProcessor = new DeltaProcessor(this);
	}
	
	/**
	 * Creates a new proof tree for the given (disconnected) IProofTreeNode.
	 * 
	 * Clients must not call this constructor, but rather the factory method in
	 * {@link org.eventb.core.prover.SequentProver}.
	 */
	protected ProofTree(ProofTreeNode node) {
		node.setProofTree(this);
		root = node;
		deltaProcessor = new DeltaProcessor(this);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTree#addChangeListener(org.eventb.core.prover.IProofTreeChangedListener)
	 */
	public void addChangeListener(IProofTreeChangedListener listener) {
		deltaProcessor.addChangeListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTree#getRoot()
	 */
	public ProofTreeNode getRoot() {
		return root;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTree#getSequent()
	 */
	public IProverSequent getSequent() {
		return getRoot().getSequent();
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTree#isClosed()
	 */
	public boolean isClosed() {
		return getRoot().isClosed();
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTree#removeChangeListener(org.eventb.core.prover.IProofTreeChangedListener)
	 */
	public void removeChangeListener(IProofTreeChangedListener listener) {
		deltaProcessor.removeChangeListener(listener);
	}

	
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTree#getConfidence()
	 */
	public int getConfidence() {
		return getRoot().getConfidence();
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTree#proofAttempted()
	 */
	public boolean proofAttempted() {
		return !(root.isOpen() && root.getComment().length() == 0);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTree#getProofDependencies()
	 */
	public IProofDependencies getProofDependencies() {
		return new ProofDependencies();
	}
	
	public class ProofDependencies implements IProofDependencies{
		
		final Predicate goal;
		final Set<Hypothesis> usedHypotheses;
		final ITypeEnvironment usedFreeIdents;
		final ITypeEnvironment introducedFreeIdents;
		
		ProofDependencies(){
			goal = getSequent().goal();
			// TODO : traverse the tree only once to compute all information.
			usedHypotheses = calculateUsedHypotheses();
			usedFreeIdents = Lib.makeTypeEnvironment();
			introducedFreeIdents = Lib.makeTypeEnvironment();
			getFreeIdentDeps(usedFreeIdents,introducedFreeIdents);
			// Assert that the proof of the same sequent is replayable.
			assert Lib.proofReusable(this,getSequent());
		}
		
		public Predicate getGoal() {
			return goal;
		}
		
		public Set<Hypothesis> getUsedHypotheses() {
			return usedHypotheses;
		}

		public ITypeEnvironment getUsedFreeIdents() {
			return usedFreeIdents;
		}

		public ITypeEnvironment getIntroducedFreeIdents() {
			return introducedFreeIdents;
		}
		
		//	TODO : Replace with a more sophisticated implementation
		//  once Rule and ReasoningStep have been merged.
		private Set<Hypothesis> calculateUsedHypotheses() {
			Set<Hypothesis> usedHyps = new HashSet<Hypothesis>();
			collectNeededHypotheses(usedHyps,root);
			usedHyps.retainAll(getSequent().hypotheses());
			return usedHyps;
		}
		
		// actually static
		private void collectNeededHypotheses(Set<Hypothesis> neededHyps,IProofTreeNode node){
			neededHyps.addAll(node.getNeededHypotheses());
			IProofTreeNode[] children = node.getChildren();
			for (int i = 0; i < children.length; i++) {
				neededHyps.addAll(children[i].getNeededHypotheses());
				collectNeededHypotheses(neededHyps,children[i]);
			}
		}

		private ITypeEnvironment getFreeIdents() {
			ITypeEnvironment typeEnv = Lib.makeTypeEnvironment();
			collectFreeIdentifiers(typeEnv,root);
			return typeEnv;
		}

		// actually static
		private void collectFreeIdentifiers(ITypeEnvironment typeEnv, IProofTreeNode node) {
			node.addFreeIdents(typeEnv);
			IProofTreeNode[] children = node.getChildren();
			for (int i = 0; i < children.length; i++) {
				children[i].addFreeIdents(typeEnv);
				collectFreeIdentifiers(typeEnv,children[i]);
			}
			
		}
		
		private void getFreeIdentDeps(ITypeEnvironment usedIdents,ITypeEnvironment introducedIdents) {
			ITypeEnvironment freeIdents = getFreeIdents();
			ITypeEnvironment typeEnv = root.getSequent().typeEnvironment();
			
			ITypeEnvironment.IIterator iterator = freeIdents.getIterator();
			while (iterator.hasNext()){
				iterator.advance();
				String name = iterator.getName();
				Type type = iterator.getType();
				if (typeEnv.contains(name) && 
						typeEnv.getType(name).equals(type))
					// It contains the freeIdent : it is used
					usedIdents.addName(name,type);
				else 
				{
					// It does not contain the free Ident : it is introduced
					introducedIdents.addName(name,type);
				}
			}
		}

		
	}

}