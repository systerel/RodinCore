/*******************************************************************************
 * Copyright (c) 2005, 2023 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - optimized setProofTree()
 *     Systerel - used nested classes instead of anonymous ones
 *     Systerel - added used reasoners to proof dependencies
 *******************************************************************************/
package org.eventb.core.basis;

import static org.eventb.core.EventBAttributes.PR_SETS_ATTRIBUTE;
import static org.eventb.core.EventBPlugin.getProofManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ILanguage;
import org.eventb.core.IPRIdentifier;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRReasoner;
import org.eventb.core.IPRStoredExpr;
import org.eventb.core.IPRStoredPred;
import org.eventb.core.IProofStoreCollector;
import org.eventb.core.IProofStoreReader;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IReasonerDesc;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.internal.core.ProofSkeletonBuilder;
import org.eventb.internal.core.basis.ProofStoreCollector;
import org.eventb.internal.core.basis.ProofStoreReader;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Farhad Mehta
 *
 * @since 1.0
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class PRProof extends EventBProofElement implements IPRProof {

	public PRProof(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public IInternalElementType<IPRProof> getElementType() {
		return ELEMENT_TYPE;
	}
	
	@Override
	public void setProofTree(IProofTree pt, IProgressMonitor monitor)
			throws RodinDBException {
		final SubMonitor sm = SubMonitor.convert(monitor);
		try {
			doSetProofTree(pt, sm);
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}
	
	private void doSetProofTree(IProofTree pt, SubMonitor sm)
			throws RodinDBException {
		sm.setWorkRemaining(21);
		clear(false, sm.newChild(1));
		
		if (pt.getConfidence() <= IConfidence.UNATTEMPTED) return;

		final FormulaFactory ff = pt.getFormulaFactory();

		try {
			setFormulaFactory(ff, sm.newChild(1));
		} catch(RodinDBException e) {
			// exception already logged
			pt.getRoot().pruneChildren();
		}

		// compute proof tree dependencies
		IProofDependencies proofDeps = pt.getProofDependencies();

		// Construct a new proof store
		IProofStoreCollector store = new ProofStoreCollector(proofDeps.getUsedFreeIdents());

		// Write out the proof tree dependencies
		if (proofDeps.getGoal() !=null) setGoal(proofDeps.getGoal(), store, sm.newChild(1));
		setHyps(proofDeps.getUsedHypotheses(),store,sm.newChild(2));
		// The used free idents are stored as the base type env in the store
		setIntroFreeIdents(proofDeps.getIntroducedFreeIdents(), sm.newChild(1));
		
		// write out the proof skeleton
		setSkeleton(pt.getRoot(), store, sm.newChild(12));

		//	Update the status
		int confidence = pt.getConfidence();
		setConfidence(confidence, sm.newChild(1));
		
		store.writeOut(this, sm.newChild(2));
	}

	@Override
	public IProofDependencies getProofDependencies(FormulaFactory factory,
			IProgressMonitor monitor) throws CoreException {
		if (getConfidence() <= IConfidence.UNATTEMPTED) return UNATTEMPTED_PROOF_DEPS;
		final IProofStoreReader store = new ProofStoreReader(this, factory);
		
		final Predicate goal;
		final Set<Predicate> usedHypotheses;
		final ISealedTypeEnvironment usedFreeIdents;
		final Set<String> introducedFreeIdents;
		final Set<IReasonerDesc> usedReasoners;
		
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
			
			usedReasoners = getUsedReasoners(store);
		} finally {
			monitor.done();
		}
		
		final boolean hasDeps = (goal != null ||
				! usedHypotheses.isEmpty() ||
				! usedFreeIdents.isEmpty() ||
				! introducedFreeIdents.isEmpty() ||
				! usedReasoners.isEmpty()); 
		
		return ProverFactory.makeProofDependencies(hasDeps, goal,
				usedHypotheses, usedFreeIdents, introducedFreeIdents,
				usedReasoners);
	}
	
	@Override
	public IProofSkeleton getSkeleton(FormulaFactory factory,
			IProgressMonitor monitor) throws CoreException {
		
		if (getConfidence() == IConfidence.UNATTEMPTED) {
			return UNATTEMPTED_PROOF_SKEL;
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
	
	private static final IProofDependencies UNATTEMPTED_PROOF_DEPS = ProverFactory
			.makeProofDependencies(false, null, null, null, null, null);

	private static final IProofSkeleton UNATTEMPTED_PROOF_SKEL = new EmptySkeleton(
			"");
		
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
			throws CoreException {
		try {
			return ProofSkeletonBuilder.buildProofTree(this, monitor);
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}

	/**
	 * @since 2.2
	 */
	@Override
	public IPRReasoner getReasoner(String name) {
		return getInternalElement(IPRReasoner.ELEMENT_TYPE, name);
	}
	
	/**
	 * @since 2.2
	 */
	@Override
	public IPRReasoner[] getReasoners() throws RodinDBException {
		return getChildrenOfType(IPRReasoner.ELEMENT_TYPE);
	}
	
	// TODO consider making public
	private Set<IReasonerDesc> getUsedReasoners(IProofStoreReader store)
			throws CoreException {
		final Set<IReasonerDesc> usedReasoners = new HashSet<IReasonerDesc>();
		final IPRReasoner[] reasoners = PRProof.this.getReasoners();
		if (reasoners.length == 0) { // old storage: reasoners in skeleton
			final IProofSkeleton skel = getSkeleton(store);
			addUsedReasoners(skel, usedReasoners);
		} else {
			for (IPRReasoner reasoner : reasoners) {
				usedReasoners.add(reasoner.getReasoner());
			}
		}
		return usedReasoners;
	}
	
	private static void addUsedReasoners(IProofSkeleton proofSkeleton,
			Set<IReasonerDesc> usedReasoners) {
		final IProofRule rule = proofSkeleton.getRule();
		if (rule == null) {
			// leaf: no reasoner
			return;
		}
		usedReasoners.add(rule.getReasonerDesc());
		for (IProofSkeleton childNode : proofSkeleton.getChildNodes()) {
			addUsedReasoners(childNode, usedReasoners);
		}
	}

	/**
	 * @since 3.0
	 */
	@Override
	public FormulaFactory getFormulaFactory(IProgressMonitor monitor)
			throws CoreException {
		final ILanguage lang = getLanguage();
		if (lang.exists()) {
			return lang.getFormulaFactory(monitor);
		}
		// Backward compatibility: return the factory of the proof component
		final IEventBRoot root = (IEventBRoot) getRoot();
		final IProofComponent pc = getProofManager().getProofComponent(root);
		try {
			return pc.getSafeFormulaFactory();
		} catch (CoreException e) {
			// Since this code path is for backward compatibility, we maintain it by
			// ignoring the exception and returning a default factory, as before
			return FormulaFactory.getDefault();
		}
	}

	private void setFormulaFactory(FormulaFactory factory, IProgressMonitor pm)
			throws RodinDBException {
		final SubMonitor sm = SubMonitor.convert(pm, 100);
		final ILanguage language = getLanguage();
		language.create(null, sm.newChild(10));
		language.setFormulaFactory(factory, sm.newChild(90));
	}

	private ILanguage getLanguage() {
		return getInternalElement(ILanguage.ELEMENT_TYPE, "L");
	}

}
