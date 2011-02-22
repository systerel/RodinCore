/*******************************************************************************
 * Copyright (c) 2005, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - optimized setProofTree()
 *******************************************************************************/
package org.eventb.core.basis;

import static org.eventb.core.EventBAttributes.PR_SETS_ATTRIBUTE;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPRIdentifier;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRStoredExpr;
import org.eventb.core.IPRStoredPred;
import org.eventb.core.IProofStoreCollector;
import org.eventb.core.IProofStoreReader;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.internal.core.ProofMonitor;
import org.eventb.internal.core.ProofSkeletonBuilder;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Farhad Mehta
 *
 * @since 1.0
 */
public class PRProof extends EventBProofElement implements IPRProof {

	public PRProof(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public IInternalElementType<IPRProof> getElementType() {
		return ELEMENT_TYPE;
	}
	
//	@Override
//	public int getConfidence() throws RodinDBException {
//		if (!hasConfidence()) return IConfidence.UNATTEMPTED;
//		return getAttributeValue(EventBAttributes.CONFIDENCE_ATTRIBUTE);
//	}
	
	// TODO fix usage of monitor.
	@Override
	public void setProofTree(IProofTree pt, IProgressMonitor monitor) throws RodinDBException {
		
		clear(false, monitor);
		
		if (pt.getConfidence() <= IConfidence.UNATTEMPTED) return;
		
		// compute proof tree dependencies
		IProofDependencies proofDeps = pt.getProofDependencies();

		// Construct a new proof store
		IProofStoreCollector store = new ProofStoreCollector(proofDeps.getUsedFreeIdents());

		// Write out the proof tree dependencies
		if (proofDeps.getGoal() !=null) setGoal(proofDeps.getGoal(), store, null);
		setHyps(proofDeps.getUsedHypotheses(),store,null);
		// The used free idents are stored as the base type env in the store
		setIntroFreeIdents(proofDeps.getIntroducedFreeIdents(), monitor);
		
		// write out the proof skeleton
		setSkeleton(pt.getRoot(), store, monitor);

		//	Update the status
		int confidence = pt.getConfidence();
		setConfidence(confidence, null);
		
		store.writeOut(this, monitor);
	}

	@Override
	public IProofDependencies getProofDependencies(FormulaFactory factory, IProgressMonitor monitor) throws RodinDBException{
		if (getConfidence() <= IConfidence.UNATTEMPTED) return unattemptedProofDeps;
		final IProofStoreReader store = new ProofStoreReader(this, factory);
		
		final Predicate goal;
		final Set<Predicate> usedHypotheses;
		final ITypeEnvironment usedFreeIdents;
		final Set<String> introducedFreeIdents;
		
		if (monitor == null) monitor = new NullProgressMonitor();
		try {
			monitor.beginTask("Reading Proof Dependencies", 4);
			usedFreeIdents = store.getBaseTypeEnv();
			introducedFreeIdents = PRProof.this.getIntroFreeIdents(monitor);
			if (PRProof.this.hasGoal())
				goal = PRProof.this.getGoal(store);
			else
				goal = null;
			usedHypotheses = PRProof.this.getHyps(store);
		} finally {
			monitor.done();
		}
		
		final boolean hasDeps = (goal != null ||
				! usedHypotheses.isEmpty() ||
				! usedFreeIdents.isEmpty() ||
				! introducedFreeIdents.isEmpty()); 
		
		return ProverFactory.makeProofDependencies(hasDeps, goal, 
				usedHypotheses, usedFreeIdents, introducedFreeIdents);
	}
	
	
	@Override
	public IProofSkeleton getSkeleton(FormulaFactory factory,
			IProgressMonitor monitor) throws RodinDBException {
		
		if (getConfidence() == IConfidence.UNATTEMPTED) {
			return unattemptedProofSkel;
		}
		
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		try {
			monitor.beginTask("Reading Proof Skeleton", 11);
			IProofStoreReader store = new ProofStoreReader(this, factory);
			return getSkeleton(store);
		} finally {
			monitor.done();
		}
	}

	public void setIntroFreeIdents(Collection<String> identNames, IProgressMonitor monitor) throws RodinDBException {
		StringBuilder names = new StringBuilder();
		String sep = "";
		for (String name : identNames) {
			names.append(sep);
			sep = ",";
			names.append(name);
		}
		setAttributeValue(EventBAttributes.FRESH_IDENTIFIERS_ATTRIBUTE, names.toString(), monitor);
	}
	
	public Set<String> getIntroFreeIdents(IProgressMonitor monitor) throws RodinDBException {
		if (! hasAttribute(EventBAttributes.FRESH_IDENTIFIERS_ATTRIBUTE)) return new HashSet<String>();
		String sepNames = getAttributeValue(EventBAttributes.FRESH_IDENTIFIERS_ATTRIBUTE);
		String[] names = sepNames.split(",");
		HashSet<String> identNames = new HashSet<String>(names.length);
		for(String name : names){
			if (name.length()!=0) identNames.add(name);
		}
		return identNames;
	}
	
	private static final IProofDependencies unattemptedProofDeps =
		ProverFactory.makeProofDependencies(false, null, null, null, null);
	
	private static final IProofSkeleton unattemptedProofSkel = new IProofSkeleton()
	{

		@Override
		public IProofSkeleton[] getChildNodes() {
			return NO_CHILDREN;
		}

		@Override
		public String getComment() {
			return "";
		}

		@Override
		public IProofRule getRule() {
			return null;
		}
	};
		
	@Override
	public IPRStoredExpr getExpression(String name) {
		return getInternalElement(IPRStoredExpr.ELEMENT_TYPE, name);
	}

	@Override
	public IPRStoredExpr[] getExpressions() throws RodinDBException {
		return getChildrenOfType(IPRStoredExpr.ELEMENT_TYPE);
	}

	@Override
	public IPRIdentifier getIdentifier(String name) {
		return getInternalElement(IPRIdentifier.ELEMENT_TYPE, name);
	}

	@Override
	public IPRIdentifier[] getIdentifiers() throws RodinDBException {
		return getChildrenOfType(IPRIdentifier.ELEMENT_TYPE);
	}

	@Override
	public IPRStoredPred getPredicate(String name) {
		return getInternalElement(IPRStoredPred.ELEMENT_TYPE, name);
	}

	@Override
	public IPRStoredPred[] getPredicates() throws RodinDBException {
		return getChildrenOfType(IPRStoredPred.ELEMENT_TYPE);
	}

	@Override
	public String[] getSets() throws RodinDBException {
		if (hasAttribute(PR_SETS_ATTRIBUTE)) {
			String value = getAttributeValue(PR_SETS_ATTRIBUTE);
			return value.split(",");
		}
		return NO_STRINGS;
	}

	@Override
	public void setSets(String[] sets, IProgressMonitor monitor) throws RodinDBException {
		final int length = sets.length;
		if (length == 0) {
			removeAttribute(PR_SETS_ATTRIBUTE, monitor);
			return;
		}
		if (length == 1) {
			setAttributeValue(PR_SETS_ATTRIBUTE, sets[0], monitor);
			return;
		}
		final StringBuilder builder = new StringBuilder();
		String sep = "";
		for (String name: sets) {
			builder.append(sep);
			sep = ",";
			builder.append(name);
		}
		setAttributeValue(PR_SETS_ATTRIBUTE, builder.toString(), monitor);
	}

	@Override
	public IProofTree getProofTree(IProgressMonitor monitor)
			throws RodinDBException {
		return ProofSkeletonBuilder.buildProofTree(this, new ProofMonitor(
				monitor));
	}


}
