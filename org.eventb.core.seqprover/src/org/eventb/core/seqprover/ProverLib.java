package org.eventb.core.seqprover;

import java.util.Arrays;

import org.eventb.core.seqprover.IProofRule.IAnticident;

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
		if (! Lib.identical(pn1.getSequent(),pn2.getSequent())) return false;
		
		if (pn1.hasChildren())
		{
			if (!deepEquals(pn1.getRule(),pn2.getRule())) return false;
			for (int i = 0; i < pn1.getChildNodes().length; i++) {
				if (! deepEquals(pn1.getChildNodes()[i],pn2.getChildNodes()[i])) return false;
			}
		}
		return true;
	}

	private static boolean deepEquals(IProofRule r1, IProofRule r2) {
		if (r1 == r2) return true;
		if (! r1.generatedBy().getReasonerID().
				equals(r2.generatedBy().getReasonerID())) return false;
		if (! r1.getDisplayName().equals(r1.getDisplayName())) return false;
		if (r1.getConfidence() != r2.getConfidence()) return false;
		if (! r1.getGoal().equals(r2.getGoal())) return false;
		if (! r1.getNeededHyps().equals(r2.getNeededHyps())) return false;
		if (! deepEquals(r1.generatedUsing(),r2.generatedUsing())) return false;
		if (r1.getAnticidents().length != r2.getAnticidents().length) return false;
		for (int i = 0; i < r1.getAnticidents().length; i++) {
			if (! deepEquals(r1.getAnticidents()[i],r1.getAnticidents()[i])) return false;
		}
		return true;
	}

	private static boolean deepEquals(IAnticident a1, IAnticident a2) {
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

}
