/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRProofSkelNode;
import org.eventb.core.IPRProofStore;
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

/**
 * @author Farhad Mehta
 *
 */
public class PRProof extends EventBProofElement implements IPRProof {

	public PRProof(String name, IRodinElement parent) {
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
		PRProof prProofTree = this;
		if (prProofTree.hasChildren())
			getRodinDB().delete(prProofTree.getChildren(),true,null);

		
		// compute proof tree dependencies
		IProofDependencies proofDependencies = pt.getProofDependencies();

		// Construct a new proof store
		IProofStoreCollector store = new ProofStoreCollector(proofDependencies.getUsedFreeIdents());

		
		// Write out the proof tree dependencies		
		setGoal(proofDependencies.getGoal(), store, null);
		setHyps(Hypothesis.Predicates(proofDependencies.getUsedHypotheses()),store,null);

		setIntroFreeIdents(proofDependencies.getIntroducedFreeIdents(), monitor);
		
		// write out the proof skeleton
		IPRProofSkelNode root = (IPRProofSkelNode)
		createInternalElement(PRProofSkelNode.ELEMENT_TYPE,"0",null,null);
		root.setSkeleton(pt.getRoot(), store, null);

		//	Update the status
		int confidence = pt.getConfidence();
		setConfidence(confidence, null);
		
		IPRProofStore prStore = ((IPRProofStore)(prProofTree.createInternalElement(
				IPRProofStore.ELEMENT_TYPE,"proofStore",null,null)));
		store.writeOut(prStore, null);
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
		final Set<String> introducedFreeIdents;
		final boolean hasDeps;
		
		public ProofDependencies(FormulaFactory factory, IProofStoreReader store, IProgressMonitor monitor) throws RodinDBException{
			if (monitor == null) monitor = new NullProgressMonitor();
			final int confidence = PRProof.this.getConfidence(monitor);
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
				//introducedFreeIdents = PRProofTree.this.getIntroducedTypeEnvironment(factory,new SubProgressMonitor(monitor,1));
				introducedFreeIdents = PRProof.this.getIntroFreeIdents(monitor);
				//goal = PRProofTree.this.getGoal(factory,usedFreeIdents,new SubProgressMonitor(monitor,1));
				goal = PRProof.this.getGoal(store, new SubProgressMonitor(monitor,1));
				// usedHypotheses = PRProofTree.this.getUsedHypotheses(factory, usedFreeIdents, new SubProgressMonitor(monitor,1));
				usedHypotheses = Hypothesis.Hypotheses(PRProof.this.getHyps(store, new SubProgressMonitor(monitor,1)));
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
		public Set<String> getIntroducedFreeIdents() {
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
		IPRProofSkelNode root = (IPRProofSkelNode) getInternalElement(IPRProofSkelNode.ELEMENT_TYPE, "0");

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

	public void setIntroFreeIdents(Collection<String> identNames, IProgressMonitor monitor) throws RodinDBException {
		StringBuilder names = new StringBuilder();
		Boolean notEmpty = false;
		for (String name : identNames) {
			if (notEmpty) names.append(";"); 
			names.append(name);
			notEmpty = true;
		}
		setAttributeValue(EventBAttributes.INTRO_FREE_IDENTS_ATTRIBUTE, names.toString(), monitor);
	}
	
	public Set<String> getIntroFreeIdents(IProgressMonitor monitor) throws RodinDBException {
		if (! hasAttribute(EventBAttributes.INTRO_FREE_IDENTS_ATTRIBUTE, monitor)) return new HashSet<String>();
		String sepNames = getAttributeValue(EventBAttributes.INTRO_FREE_IDENTS_ATTRIBUTE, monitor);
		String[] names = sepNames.split(";");
		HashSet<String> identNames = new HashSet<String>(names.length);
		for(String name : names){
			if (name.length()!=0) identNames.add(name);
		}
		return identNames;
	}
}
