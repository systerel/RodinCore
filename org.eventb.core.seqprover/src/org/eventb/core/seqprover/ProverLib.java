package org.eventb.core.seqprover;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.proofBuilder.IProofSkeleton;

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

	private static boolean deepEquals(IProofRule r1, IProofRule r2) {
		if (r1 == r2) return true;
		if (! r1.generatedBy().getReasonerID().
				equals(r2.generatedBy().getReasonerID())) return false;
		if (! r1.getDisplayName().equals(r1.getDisplayName())) return false;
		if (r1.getConfidence() != r2.getConfidence()) return false;
		if (r1.getGoal() == null && r2.getGoal() != null) return false;
		if (r1.getGoal() != null && r2.getGoal() == null) return false;
		if (r1.getGoal() != null && r2.getGoal() != null &&
			(! r1.getGoal().equals(r2.getGoal()))) return false;
		if (! r1.getNeededHyps().equals(r2.getNeededHyps())) return false;
		if (! deepEquals(r1.generatedUsing(),r2.generatedUsing())) return false;
		if (r1.getAntecedents().length != r2.getAntecedents().length) return false;
		for (int i = 0; i < r1.getAntecedents().length; i++) {
			if (! deepEquals(r1.getAntecedents()[i],r1.getAntecedents()[i])) return false;
		}
		return true;
	}

	private static boolean deepEquals(IAntecedent a1, IAntecedent a2) {
		if (a1 == a2) return true;
		if (! a1.getGoal().equals(a2.getGoal())) return false;
		if (! a1.getAddedHyps().equals(a2.getAddedHyps())) return false;
		if (! Arrays.deepEquals(a1.getAddedFreeIdents(),a2.getAddedFreeIdents())) return false;
		if (! a1.getHypAction().equals(a2.getHypAction())) return false;
		return true;
	}

	// ignore reasoner input for the moment
	private static boolean deepEquals(IReasonerInput input, IReasonerInput input2) {
		// TODO Auto-generated method stub
		return true;
	}

	public static boolean deepEquals(IProverSequent S1,IProverSequent S2){
		if (! S1.goal().equals(S2.goal())) return false;
		if (! S1.selectedHypotheses().equals(S2.selectedHypotheses())) return false;
		if (! S1.hiddenHypotheses().equals(S2.hiddenHypotheses())) return false;
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

	public static boolean proofReusable(IProofDependencies proofDependencies,IProverSequent sequent){
		if (! proofDependencies.hasDeps()) return true;
		if (proofDependencies.getGoal() != null && ! sequent.goal().equals(proofDependencies.getGoal())) return false;
		if (! sequent.containsHypotheses(proofDependencies.getUsedHypotheses())) return false;
		if (! sequent.typeEnvironment().containsAll(proofDependencies.getUsedFreeIdents())) return false;
		if (! Collections.disjoint(
				sequent.typeEnvironment().getNames(),
				proofDependencies.getIntroducedFreeIdents())) return false;	
		return true;
	}

	public static Set<Predicate> hypsTextSearch(IProverSequent sequent, String token) {
		Set<Predicate> result = new LinkedHashSet<Predicate>();
		for (Predicate hypothesis : sequent.hypIterable()){
			if (hypothesis.toString().contains(token)) result.add(hypothesis);
		}
		return result;
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
}
