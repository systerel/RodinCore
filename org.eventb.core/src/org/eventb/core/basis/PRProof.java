/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

	private static final String[] NO_STRINGS = new String[0];

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
	
	// TODO fix usage of monitor.
	public void setProofTree(IProofTree pt, IProgressMonitor monitor) throws RodinDBException {
		
		delete(false, monitor);
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
		setSkeleton(pt.getRoot(), store, monitor);

		//	Update the status
		int confidence = pt.getConfidence();
		setConfidence(confidence, null);
		
		store.writeOut(this, monitor);
	}

	public IProofDependencies getProofDependencies(FormulaFactory factory, IProgressMonitor monitor) throws RodinDBException{
		if (getConfidence() <= IConfidence.UNATTEMPTED) return unattemptedProofDeps;
		IProofStoreReader store = new ProofStoreReader(this, factory);
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
		
	public IPRStoredExpr getExpression(String name) {
		return (IPRStoredExpr) getInternalElement(IPRStoredExpr.ELEMENT_TYPE, name);
	}

	public IPRStoredExpr[] getExpressions() throws RodinDBException {
		return (IPRStoredExpr[]) getChildrenOfType(IPRStoredExpr.ELEMENT_TYPE);
	}

	public IPRIdentifier getIdentifier(String name) {
		return (IPRIdentifier) getInternalElement(IPRIdentifier.ELEMENT_TYPE, name);
	}

	public IPRIdentifier[] getIdentifiers() throws RodinDBException {
		return (IPRIdentifier[]) getChildrenOfType(IPRIdentifier.ELEMENT_TYPE);
	}

	public IPRStoredPred getPredicate(String name) {
		return (IPRStoredPred) getInternalElement(IPRStoredPred.ELEMENT_TYPE, name);
	}

	public IPRStoredPred[] getPredicates() throws RodinDBException {
		return (IPRStoredPred[]) getChildrenOfType(IPRStoredPred.ELEMENT_TYPE);
	}

	public String[] getSets() throws RodinDBException {
		if (hasAttribute(PR_SETS_ATTRIBUTE)) {
			String value = getAttributeValue(PR_SETS_ATTRIBUTE);
			return value.split(",");
		}
		return NO_STRINGS;
	}

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

}
