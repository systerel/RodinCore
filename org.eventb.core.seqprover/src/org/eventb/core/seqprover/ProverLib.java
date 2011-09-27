/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - fixed bugs, added deepEquals(IHypAction, IHypAction)
 *     Systerel - added simplify(IProofTree, IProofMonitor)
 *     Systerel - checked reasoner versions before reusing proofs
 *     Systerel - added unselected hypotheses
 *     Systerel - checked reasoner conflicts in proof dependencies
 *******************************************************************************/
package org.eventb.core.seqprover;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.internal.core.seqprover.ReasonerRegistry;
import org.eventb.internal.core.seqprover.proofSimplifier.ProofTreeSimplifier;
import org.eventb.internal.core.seqprover.proofSimplifier.Simplifier.CancelException;

/**
 * This is a collection of static constants and methods that are used often in relation
 * to the sequent prover.
 * <p>
 * Note that they are public but not published and are subject to change. They are to be
 * used at one own's risk. Making referencs to the static functions inside it is highly 
 * discouraged since their implementation may change without notice, leaving your code in an uncompilable state.
 * </p>
 * 
 * <p>
 * This does not however prevent you from having your own local copies of
 * the functions that you need, assuming that they do the intended job. 
 * </p>
 * 
 *
 * @author Farhad Mehta
 *
 * @since 1.0
 */
public class ProverLib {

	
	/**
	 * This class is not meant to be instantiated
	 */
	private ProverLib() {
	}
	
	public static boolean deepEquals(IProofTree pt1,IProofTree pt2){
		if (pt1 == pt2) return true;
		if (pt1.getConfidence() != pt2.getConfidence()) return false;
		
		return deepEquals(pt1.getRoot(),pt2.getRoot());
	}

	public static boolean deepEquals(IProofTreeNode pn1, IProofTreeNode pn2) {
		if (pn1.hasChildren() != pn2.hasChildren()) return false;
		if (pn1.getConfidence() != pn2.getConfidence()) return false;
		if (! pn1.getComment().equals(pn2.getComment())) return false;
		// maybe remove this check and add it to the proof tree deepEquals
		if (! ProverLib.deepEquals(pn1.getSequent(),pn2.getSequent())) return false;
		
		if (pn1.hasChildren())
		{
			if (!deepEquals(pn1.getRule(),pn2.getRule())) return false;
			for (int i = 0; i < pn1.getChildNodes().length; i++) {
				if (! deepEquals(pn1.getChildNodes()[i],pn2.getChildNodes()[i])) return false;
			}
		}
		return true;
	}
	
	public static boolean deepEquals(IProofSkeleton pn1, IProofSkeleton pn2) {
		if (! pn1.getComment().equals(pn2.getComment())) return false;
		// maybe remove this check and add it to the proof tree deepEquals
		if (! ProverLib.deepEquals(pn1.getRule(),pn2.getRule())) return false;
		if (pn1.getChildNodes().length != pn2.getChildNodes().length) return false;
		for (int i = 0; i < pn1.getChildNodes().length; i++) {
			if (! deepEquals(pn1.getChildNodes()[i],pn2.getChildNodes()[i])) return false;
		}
		return true;
	}

	/**
	 * @since 2.0
	 */
	public static boolean deepEquals(IProofRule r1, IProofRule r2) {
		if (r1 == r2) return true;
		final IReasonerDesc desc1 = r1.getReasonerDesc();
		final IReasonerDesc desc2 = r2.getReasonerDesc();
		if (!deepEquals(desc1, desc2)) return false;
		if (! r1.getDisplayName().equals(r2.getDisplayName())) return false;
		if (r1.getConfidence() != r2.getConfidence()) return false;
		if (r1.getGoal() == null && r2.getGoal() != null) return false;
		if (r1.getGoal() != null && r2.getGoal() == null) return false;
		if (r1.getGoal() != null && r2.getGoal() != null &&
			(! r1.getGoal().equals(r2.getGoal()))) return false;
		if (! r1.getNeededHyps().equals(r2.getNeededHyps())) return false;
		if (! deepEquals(r1.generatedUsing(),r2.generatedUsing())) return false;
		if (r1.getAntecedents().length != r2.getAntecedents().length) return false;
		for (int i = 0; i < r1.getAntecedents().length; i++) {
			if (! deepEquals(r1.getAntecedents()[i], r2.getAntecedents()[i])) return false;
		}
		return true;
	}

	private static boolean deepEquals(IReasonerDesc desc1, IReasonerDesc desc2) {
		if (!desc1.getId().equals(desc2.getId()))
			return false;
		return desc1.getVersion() == desc2.getVersion();
	}

	private static boolean deepEquals(IAntecedent a1, IAntecedent a2) {
		if (a1 == a2) return true;
		if (! deepEquals(a1.getGoal(), a2.getGoal())) return false;
		if (! a1.getAddedHyps().equals(a2.getAddedHyps())) return false;
		if (! a1.getUnselectedAddedHyps().equals(a2.getUnselectedAddedHyps())) return false;
		if (! Arrays.deepEquals(a1.getAddedFreeIdents(),a2.getAddedFreeIdents())) return false;
		if (a1.getHypActions().size() != a2.getHypActions().size()) return false;
		for(int i = 0; i < a1.getHypActions().size(); i++) {
			if (! deepEquals(a1.getHypActions().get(i), a2.getHypActions().get(i))) return false;
		}
		return true;
	}

	private static boolean deepEquals(Predicate p1, Predicate p2) {
		if (p1 == p2) return true;
		if (p1 == null || p2 == null) return false;
		return p1.equals(p2);
	}

	private static boolean deepEquals(IHypAction ha1, IHypAction ha2) {
		if (ha1 == ha2) return true;
		if (! ha1.getActionType().equals(ha2.getActionType())) return false;
		if (ha1 instanceof IForwardInfHypAction) {
			if (! (ha2 instanceof IForwardInfHypAction)) return false;
			final IForwardInfHypAction fiha1 = (IForwardInfHypAction) ha1;
			final IForwardInfHypAction fiha2 = (IForwardInfHypAction) ha2;
			if (! deepEquals(fiha1.getHyps(), fiha2.getHyps())) return false;
			if (! deepEquals(fiha1.getInferredHyps(), fiha2.getInferredHyps())) return false;
			if (! Arrays.deepEquals(fiha1.getAddedFreeIdents(), fiha2.getAddedFreeIdents())) return false;
		} else if (ha1 instanceof ISelectionHypAction) {
			if (! (ha2 instanceof ISelectionHypAction)) return false;
			ISelectionHypAction sha1 = (ISelectionHypAction) ha1;
			ISelectionHypAction sha2 = (ISelectionHypAction) ha2;
			if (! deepEquals(sha1.getHyps(), sha2.getHyps())) return false;
		} else { // unknown hyp action type
			return false;
		}
		return true;
	}
	
	// T must implement equals()
	private static <T> boolean deepEquals(Collection<T> col1, Collection<T> col2) {
		if (col1.size() != col2.size()) return false;
		final Iterator<T> it1 = col1.iterator();
		final Iterator<T> it2 = col2.iterator();
		while(it1.hasNext() && it2.hasNext()) {
			if (! it1.next().equals(it2.next())) return false;
		}
		return true;
	}
	
	// ignore reasoner input for the moment
	private static boolean deepEquals(IReasonerInput input, IReasonerInput input2) {
		// TODO Auto-generated method stub
		return true;
	}

	public static boolean deepEquals(IProverSequent S1,IProverSequent S2){
		if (! S1.goal().equals(S2.goal())) return false;
		// if (! S1.selectedHypotheses().equals(S2.selectedHypotheses())) return false;
		if (! collectPreds(S1.selectedHypIterable()).equals(collectPreds(S2.selectedHypIterable()))) return false;
		// if (! S1.hiddenHypotheses().equals(S2.hiddenHypotheses())) return false;
		if (! collectPreds(S1.hiddenHypIterable()).equals(collectPreds(S2.hiddenHypIterable()))) return false;
		// if (! S1.visibleHypotheses().equals(S2.visibleHypotheses())) return false;
		// if (! S1.hypotheses().equals(S2.hypotheses())) return false;
		if (! collectPreds(S1.hypIterable()).equals(collectPreds(S2.hypIterable()))) return false;
		if (! S1.typeEnvironment().equals(S2.typeEnvironment())) return false;
		return true;
	}

	
	public static boolean isValid(int confidence){
		return 
		(confidence >= IConfidence.PENDING) && 
		(confidence <= IConfidence.DISCHARGED_MAX);	
	}

	public static boolean isPending(int confidence){
		return (confidence == IConfidence.PENDING);
	}

	public static boolean isReviewed(int confidence){
		return 
		(confidence > IConfidence.PENDING) && 
		(confidence <= IConfidence.REVIEWED_MAX);		
	}

	public static boolean isDischarged(int confidence){
		return 
		(confidence > IConfidence.REVIEWED_MAX) && 
		(confidence <= IConfidence.DISCHARGED_MAX);		
	}

	/**
	 * Returns whether the given proof dependencies allow a reuse of the
	 * corresponding proof on the given sequent.
	 * <p>
	 * This method does take reasoner versions into account since 2.2 and is
	 * henceforth fully equivalent to
	 * {@link #isProofReusable(IProofDependencies, IProofSkeleton, IProverSequent)}
	 * , without requiring the extraction of a proof skeleton .
	 * </p>
	 * 
	 * @param proofDependencies
	 *            proof dependencies of a proof to reuse
	 * @param sequent
	 *            a sequent on which to check reusability
	 * @return <code>true</code> iff the proof dependencies allow to reuse the
	 *         proof on the sequent
	 */
	public static boolean proofReusable(IProofDependencies proofDependencies,
			IProverSequent sequent) {
		return isProofDepsReusable(proofDependencies, sequent);
	}

	private static boolean isProofDepsReusable(
			IProofDependencies proofDependencies, IProverSequent sequent) {
		if (! proofDependencies.hasDeps()) return true;
		if (proofDependencies.getGoal() != null && ! sequent.goal().equals(proofDependencies.getGoal())) return false;
		if (! sequent.containsHypotheses(proofDependencies.getUsedHypotheses())) return false;
		if (! sequent.typeEnvironment().containsAll(proofDependencies.getUsedFreeIdents())) return false;
		if (! Collections.disjoint(
				sequent.typeEnvironment().getNames(),
				proofDependencies.getIntroducedFreeIdents())) return false;	
		final Set<IReasonerDesc> usedReasoners = proofDependencies.getUsedReasoners();
		for (IReasonerDesc reasonerDesc : usedReasoners) {
			if (!isReasonerReusable(reasonerDesc)) return false;
		}
		return true;
	}

	/**
	 * Returns whether the given proof rule can be reused.
	 * <p>
	 * A rule is considered reusable iff the following conditions are met:
	 * <li>the reasoner which generated the rule is (currently) registered</li>
	 * <li>the reasoner which generated the rule has the same version as the
	 * version it is (currently) registered with</li>
	 * </p>
	 * <p>
	 * As a consequence, the returned value for a given rule might be different,
	 * depending on the contents of the reasoner registry.
	 * </p>
	 * 
	 * @param rule
	 *            a proof rule
	 * @return <code>true</code> iff the proof rule is reusable
	 * @since 1.3
	 */
	public static boolean isRuleReusable(IProofRule rule) {
		final IReasonerDesc reasoner = rule.getReasonerDesc();
		return isReasonerReusable(reasoner);
	}

	private static boolean isReasonerReusable(final IReasonerDesc reasoner) {
		final ReasonerRegistry registry = ReasonerRegistry.getReasonerRegistry();
		if (!registry.isRegistered(reasoner.getId())) {
			return false;
		}
		final IReasoner instance = reasoner.getInstance();
		if (registry.isDummyReasoner(instance)) {
			return false;
		}
		return !reasoner.hasVersionConflict()
				&& !reasoner.hasSignatureConflict();
	}

	/**
	 * Returns whether a proof can be reused on the given sequent.
	 * <p>
	 * The result is computed from proof dependencies and a proof skeleton. The
	 * given proof dependencies and proof skeleton must come from the same
	 * proof, the one whose reusability is checked.
	 * </p>
	 * <p>
	 * This method does take reasoner versions into account. It can be safely
	 * assumed that <code>false</code> will be returned when either proof
	 * dependencies are not satisfied or the reasoners mentioned in the proof
	 * skeleton are not registered or outdated.
	 * </p>
	 * 
	 * @param proofDependencies
	 *            proof dependencies of the proof to reuse
	 * @param proofSkeleton
	 *            proof skeleton of the proof to reuse
	 * @param sequent
	 *            a sequent on which to check for reusability
	 * @return <code>true</code> iff the proof tree can be reused on the sequent
	 * @since 1.3
	 * @deprecated since 2.2,
	 *             {@link #proofReusable(IProofDependencies, IProverSequent)} is
	 *             equivalent, without having to compute a proof skeleton, which
	 *             makes it faster
	 */
	@Deprecated
	public static boolean isProofReusable(IProofDependencies proofDependencies,
			IProofSkeleton proofSkeleton, IProverSequent sequent) {
		return proofReusable(proofDependencies, sequent);
	}
	
	/**
	 * Returns a set of all hypotheses whose string image contains the given
	 * pattern. Hidden hypotheses are considered during this search.
	 * 
	 * @param sequent
	 *            sequent containing the hypotheses to search among
	 * @param pattern
	 *            pattern to search in hypotheses
	 * @return a set of all hypotheses matching the given criteria
	 */
	public static Set<Predicate> hypsTextSearch(IProverSequent sequent,
			String pattern) {
		return hypsTextSearch(sequent, pattern, true);
	}

	/**
	 * Returns a set of all hypotheses whose string image contains the given
	 * pattern. Depending on the third parameter, hidden hypotheses might be
	 * considered during this search.
	 * 
	 * @param sequent
	 *            sequent containing the hypotheses to search among
	 * @param pattern
	 *            pattern to search in hypotheses
	 * @param withHidden
	 *            also consider hidden hypotheses when <code>true</code>
	 * @return a set of all hypotheses matching the given criteria
	 * @since 2.0
	 */
	public static Set<Predicate> hypsTextSearch(IProverSequent sequent,
			String pattern, boolean withHidden) {
		final Set<Predicate> result = new LinkedHashSet<Predicate>();
		final Iterable<Predicate> iterable;
		if (withHidden) {
			iterable = sequent.hypIterable();
		} else {
			iterable = sequent.visibleHypIterable();
		}
		for (final Predicate hypothesis : iterable) {
			if (patternMatches(hypothesis, pattern)) {
				result.add(hypothesis);
			}
		}
		return result;
	}
	
	private static boolean patternMatches(Predicate pred, String pattern) {
		if (pattern.isEmpty()) {
			return true;
		}
		final String predStr = pred.toString();
		final String[] toSearch = pattern.split("\\s");
		for (String str : toSearch) {
			if (!(predStr.contains(str)))
				return false;
		}
		return true;
	}

	public static Set<Predicate> hypsFreeIdentsSearch(IProverSequent seq, Set<FreeIdentifier> freeIdents) {
		Set<Predicate> result = new LinkedHashSet<Predicate>();
		for (Predicate hypothesis: seq.hypIterable()){
			if (! Collections.disjoint(
					Arrays.asList(hypothesis.getFreeIdentifiers()),
					freeIdents))				
				result.add(hypothesis);
		}
		return result;
	}
	
	public static Set<Predicate> collectPreds(Iterable<Predicate> predIterator){
		LinkedHashSet<Predicate> preds = new LinkedHashSet<Predicate>();
		for (Predicate pred : predIterator) {
			preds.add(pred);
		}
		return preds;
	}
	
	/**
	 * Returns a closed simplified version of the given closed proof tree.
	 * <p>
	 * A simplified proof tree contains less unused data. Callers of that method
	 * must firstly check that the tree is closed.
	 * </p>
	 * 
	 * @param prTree
	 *            the tree to simplify
	 * @param monitor
	 *            a monitor to manage cancellation, or <code>null</code>
	 * @return a closed simplified proof tree, or <code>null</code> if unable to
	 *         simplify
	 */
	public static IProofTree simplify(IProofTree prTree, IProofMonitor monitor) {
		try {
			return new ProofTreeSimplifier().simplify(prTree, monitor);
		} catch (CancelException e) {
			return null;
		}
	}
	
	/**
	 * Simple proof monitor.
	 * @since 2.3
	 */
	public static ProofMonitor PM = new ProofMonitor();

	private static class ProofMonitor implements IProofMonitor {

		private boolean canceled;

		@Override
		public boolean isCanceled() {
			return canceled;
		}

		@Override
		public void setCanceled(boolean value) {
			canceled = value;
		}

		@Override
		public void setTask(String name) {
			// Ignore
		}
	}

}
