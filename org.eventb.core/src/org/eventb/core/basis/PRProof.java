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
import org.eventb.core.seqprover.IProofRule;
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
	
	@Override
	public int getConfidence() throws RodinDBException {
		if (!hasConfidence()) return IConfidence.UNATTEMPTED;
		return getAttributeValue(EventBAttributes.CONFIDENCE_ATTRIBUTE);
	}
	

	public void setProofTree(IProofTree pt, IProgressMonitor monitor) throws RodinDBException {
		
		delete(true, monitor);
		create(null, monitor);
		
		if (pt.getConfidence() <= IConfidence.UNATTEMPTED) return;
		
		// compute proof tree dependencies
		IProofDependencies proofDeps = pt.getProofDependencies();

		// Construct a new proof store
		IProofStoreCollector store = new ProofStoreCollector(proofDeps.getUsedFreeIdents());

		// Write out the proof tree dependencies		
		setGoal(proofDeps.getGoal(), store, null);
		setHyps(Hypothesis.Predicates(proofDeps.getUsedHypotheses()),store,null);
		// The used free idents are stored as the base type env in the store
		setIntroFreeIdents(proofDeps.getIntroducedFreeIdents(), monitor);
		
		// write out the proof skeleton
		IPRProofSkelNode skel = (IPRProofSkelNode) getInternalElement(IPRProofSkelNode.ELEMENT_TYPE, "0");
		skel.create(null, monitor);
		skel.setSkeleton(pt.getRoot(), store, monitor);

		//	Update the status
		int confidence = pt.getConfidence();
		setConfidence(confidence, null);
		
		IPRProofStore prStore = (IPRProofStore) getInternalElement(IPRProofStore.ELEMENT_TYPE,"proofStore");
		prStore.create(null, monitor);
		store.writeOut(prStore, monitor);
	}

	public IProofDependencies getProofDependencies(FormulaFactory factory, IProgressMonitor monitor) throws RodinDBException{
		if (getConfidence() <= IConfidence.UNATTEMPTED) return unattemptedProofDeps;
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
			try{
				monitor.beginTask("Reading Proof Dependencies", 4);
				usedFreeIdents = store.getBaseTypeEnv();
				introducedFreeIdents = PRProof.this.getIntroFreeIdents(monitor);
				goal = PRProof.this.getGoal(store);
				usedHypotheses = Hypothesis.Hypotheses(PRProof.this.getHyps(store));
				hasDeps = true;
			}
			finally
			{
				monitor.done();
			}
		}

		public boolean hasDeps() {
			return hasDeps;
		}
		
		public Predicate getGoal() {
			return goal;
		}

		public Set<String> getIntroducedFreeIdents() {
			return introducedFreeIdents;
		}

		public ITypeEnvironment getUsedFreeIdents() {
			return usedFreeIdents;
		}

		public Set<Hypothesis> getUsedHypotheses() {
			return usedHypotheses;
		}
		
	}
	
	public IProofSkeleton getSkeleton(FormulaFactory factory, IProgressMonitor monitor) throws RodinDBException {
		if (monitor == null) monitor = new NullProgressMonitor();
		
		IPRProofSkelNode root = (IPRProofSkelNode) getInternalElement(IPRProofSkelNode.ELEMENT_TYPE, "0");
		if (!root.exists()) return unattemptedProofSkel;
		
		IProofSkeleton skeleton;
		try{
			monitor.beginTask("Reading Proof Skeleton", 11);
			IPRProofStore prStore = (IPRProofStore) getInternalElement(IPRProofStore.ELEMENT_TYPE, "proofStore");
			IProofStoreReader store = new ProofStoreReader(prStore ,factory);
			skeleton = root.getSkeleton(store);
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
		if (! hasAttribute(EventBAttributes.INTRO_FREE_IDENTS_ATTRIBUTE)) return new HashSet<String>();
		String sepNames = getAttributeValue(EventBAttributes.INTRO_FREE_IDENTS_ATTRIBUTE);
		String[] names = sepNames.split(";");
		HashSet<String> identNames = new HashSet<String>(names.length);
		for(String name : names){
			if (name.length()!=0) identNames.add(name);
		}
		return identNames;
	}
	
	
	private static final IProofDependencies unattemptedProofDeps = new IProofDependencies()
	{
		public Predicate getGoal() {
			return null;
		}

		public Set<String> getIntroducedFreeIdents() {
			return null;
		}

		public ITypeEnvironment getUsedFreeIdents() {
			return null;
		}

		public Set<Hypothesis> getUsedHypotheses() {
			return null;
		}

		public boolean hasDeps() {
			return false;
		}
	};
	
	static final IProofSkeleton[] NO_CHILDREN = new IProofSkeleton[0];
	
	private static final IProofSkeleton unattemptedProofSkel = new IProofSkeleton()
	{

		public IProofSkeleton[] getChildNodes() {
			return NO_CHILDREN;
		}

		public String getComment() {
			return "";
		}

		public IProofRule getRule() {
			return null;
		}
	};
		
}
