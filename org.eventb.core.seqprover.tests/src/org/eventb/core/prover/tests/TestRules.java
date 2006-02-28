package org.eventb.core.prover.tests;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IExtReasonerInput;
import org.eventb.core.prover.IExtReasonerOutput;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.SuccessfullExtReasonerOutput;
import org.eventb.core.prover.externalReasoners.Cut;
import org.eventb.core.prover.rules.ProofRule;
import org.eventb.core.prover.rules.RuleFactory;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.HypothesesManagement.Action;
import org.eventb.core.prover.sequent.HypothesesManagement.ActionType;

public class TestRules extends TestCase {

	CommonProverSequents seq;
	RuleFactory rf;
	FormulaFactory ff;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.ff = FormulaFactory.getDefault();
		this.seq = new CommonProverSequents();
		this.rf = new RuleFactory();
	}
	
	public void testHyp(){
		ProofRule Hyp = (ProofRule) rf.hyp();
		assertTrue(Hyp.isApplicable(seq.hypSeq));
		assertFalse(Hyp.isApplicable(seq.hypSeqFail));
		
		IProverSequent[] anticidents = Hyp.apply(seq.hypSeq);
		assertNotNull(anticidents);
		assertTrue(anticidents.length == 0);
		
		assertNull(Hyp.apply(seq.hypSeqFail));
	}
	
	public void testConjI(){
		ProofRule conjI = (ProofRule) rf.conjI();
		assertFalse(conjI.isApplicable(seq.hypSeq));
		assertFalse(conjI.isApplicable(seq.hypSeqFail));
		assertTrue(conjI.isApplicable(seq.conjSeq2));
		assertTrue(conjI.isApplicable(seq.conjSeq3));
		assertFalse(conjI.isApplicable(seq.impSeq));
		assertFalse(conjI.isApplicable(seq.quantSeq));
		assertFalse(conjI.isApplicable(seq.quantSeqClash));
		
		assertNull(conjI.apply(seq.hypSeq));
		assertNull(conjI.apply(seq.hypSeqFail));
		assertNotNull(conjI.apply(seq.conjSeq2));
		assertNotNull(conjI.apply(seq.conjSeq3));
		assertNull(conjI.apply(seq.impSeq));
		assertNull(conjI.apply(seq.quantSeq));
		assertNull(conjI.apply(seq.quantSeqClash));
		
		IProverSequent[] anticidents;
		
		anticidents = conjI.apply(seq.conjSeq2);
		assertTrue(anticidents.length == 2);
		assertTrue(seq.conjSeq2conjs[0].equals(anticidents[0].goal()));
		assertTrue(seq.conjSeq2conjs[1].equals(anticidents[1].goal()));
		assertTrue(seq.conjSeq2.typeEnvironment().equals(anticidents[0].typeEnvironment()));
		assertTrue(seq.conjSeq2.typeEnvironment().equals(anticidents[1].typeEnvironment()));
		assertTrue(seq.conjSeq2.hypotheses().equals(anticidents[0].hypotheses()));
		assertTrue(seq.conjSeq2.hypotheses().equals(anticidents[1].hypotheses()));
		
		anticidents = conjI.apply(seq.conjSeq3);
		assertTrue(anticidents.length == 3);
		assertTrue(seq.conjSeq3conjs[0].equals(anticidents[0].goal()));
		assertTrue(seq.conjSeq3conjs[1].equals(anticidents[1].goal()));
		assertTrue(seq.conjSeq3conjs[2].equals(anticidents[2].goal()));
		// testing typeenvironment and hypotheses may not be needed.
	}
	
	public void testImpI(){
		ProofRule impI = (ProofRule) rf.impI();
		assertFalse(impI.isApplicable(seq.hypSeq));
		assertFalse(impI.isApplicable(seq.hypSeqFail));
		assertFalse(impI.isApplicable(seq.conjSeq2));
		assertFalse(impI.isApplicable(seq.conjSeq3));
		assertTrue(impI.isApplicable(seq.impSeq));
		assertFalse(impI.isApplicable(seq.quantSeq));
		assertFalse(impI.isApplicable(seq.quantSeqClash));
		
		assertNull(impI.apply(seq.hypSeq));
		assertNull(impI.apply(seq.hypSeqFail));
		assertNull(impI.apply(seq.conjSeq2));
		assertNull(impI.apply(seq.conjSeq3));
		assertNotNull(impI.apply(seq.impSeq));
		assertNull(impI.apply(seq.quantSeq));
		assertNull(impI.apply(seq.quantSeqClash));
		
		IProverSequent[] anticidents;
		
		anticidents = impI.apply(seq.impSeq);
		assertTrue(anticidents.length == 1);
		assertTrue(seq.impSeqRight.equals(anticidents[0].goal()));
		Set<Hypothesis> newHyps = new HashSet<Hypothesis>(seq.impSeq.hypotheses());
		newHyps.add(new Hypothesis(seq.impSeqLeft));
		assertTrue(anticidents[0].hypotheses().equals(newHyps));
//		 TODO : find a better workaround to finding a predicate in a hyp set
		assertTrue(anticidents[0].selectedHypotheses().contains(new Hypothesis(seq.impSeqLeft)));
		assertFalse(anticidents[0].hiddenHypotheses().contains(new Hypothesis(seq.impSeqLeft)));
	}
	
	public void testAllI(){
		ProofRule allI = (ProofRule) rf.allI();
		assertFalse(allI.isApplicable(seq.hypSeq));
		assertTrue(allI.isApplicable(seq.hypSeqFail));
		assertFalse(allI.isApplicable(seq.conjSeq2));
		assertFalse(allI.isApplicable(seq.conjSeq3));
		assertFalse(allI.isApplicable(seq.impSeq));
		assertTrue(allI.isApplicable(seq.quantSeq));
		assertTrue(allI.isApplicable(seq.quantSeqClash));
		
		assertNull(allI.apply(seq.hypSeq));
		assertNotNull(allI.apply(seq.hypSeqFail));
		assertNull(allI.apply(seq.conjSeq2));
		assertNull(allI.apply(seq.conjSeq3));
		assertNull(allI.apply(seq.impSeq));
		assertNotNull(allI.apply(seq.quantSeq));
		assertNotNull(allI.apply(seq.quantSeqClash));
		
//		System.out.println(seq.quantSeqClash);
//		System.out.println(allI.apply(seq.quantSeqClash)[0]);
		
		IProverSequent[] anticidents;
		
		anticidents = allI.apply(seq.quantSeq);
		assertTrue(anticidents.length == 1);
		assertTrue(seq.quantSeqFree.equals(anticidents[0].goal()));
		assertTrue(seq.quantSeq.hypotheses().equals(anticidents[0].hypotheses()));
		assertFalse(seq.quantSeq.typeEnvironment().containsAll(anticidents[0].typeEnvironment()));
		assertTrue(anticidents[0].typeEnvironment().containsAll(seq.quantSeq.typeEnvironment()));
		
		anticidents = allI.apply(seq.quantSeqClash);
		assertTrue(anticidents.length == 1);
		assertTrue(seq.quantSeq.hypotheses().equals(anticidents[0].hypotheses()));
		assertFalse(seq.quantSeq.typeEnvironment().containsAll(anticidents[0].typeEnvironment()));
		assertTrue(anticidents[0].typeEnvironment().containsAll(seq.quantSeq.typeEnvironment()));

		
	}
		
	public void testPLbCut(){
		IExternalReasoner cut = new Cut();
		IExtReasonerInput I = new Cut.Input("(x÷x)=1");

		Predicate lemma = Lib.parsePredicate("(x÷x)=1");
		ITypeEnvironment te = Lib.typeCheck(lemma);
		Predicate lemmaWD = Lib.WD(te,lemma);
		
//		assertTrue(cut.isApplicable(seq.hypSeqFail,I));
		IExtReasonerOutput O = cut.apply(seq.hypSeqFail,I);
		assertTrue(O instanceof SuccessfullExtReasonerOutput);
		SuccessfullExtReasonerOutput sO = (SuccessfullExtReasonerOutput) O;
		ProofRule PLbCut = (ProofRule) rf.pLb(sO);
//		System.out.println(PLbCut.isApplicable(seq.hypSeqFail));
//		System.out.println(sO.proof());
//		System.out.println(seq.hypSeqFail);
		assertTrue(PLbCut.isApplicable(seq.hypSeqFail));
		
		IProverSequent[] anticidents;
		
		anticidents = PLbCut.apply(seq.hypSeqFail);
//		System.out.println(anticidents.length);
		assertTrue(anticidents.length == 1);
		assertTrue(seq.hypSeqFail.typeEnvironment().equals(anticidents[0].typeEnvironment()));
		assertTrue(seq.hypSeqFail.hypotheses().equals(anticidents[0].hypotheses()));
		
		final ProofRule conjI = (ProofRule) rf.conjI();
		assertTrue(conjI.isApplicable(anticidents[0]));
		anticidents = conjI.apply(anticidents[0]);
		assertTrue(anticidents.length == 3);
		assertTrue(anticidents[0].goal().equals(lemma));
		assertTrue(anticidents[1].goal().equals(lemmaWD));
		
		final ProofRule impI = (ProofRule) rf.impI();
		assertTrue(impI.isApplicable(anticidents[2]));
		anticidents = impI.apply(anticidents[2]);
		assertTrue(anticidents.length == 1);
		assertTrue(seq.hypSeqFail.goal().equals(anticidents[0].goal()));
		assertTrue(Hypothesis.containsPredicate(anticidents[0].selectedHypotheses(),lemma));
		
	}

	public void testMngHyp(){
		Set<Hypothesis> hyps = seq.hypSeq.hypotheses();
		Hypothesis[] H = new Hypothesis[hyps.size()];
		hyps.toArray(H);
		
		IProverSequent[] anticidents;
		
		ProofRule selectAll = (ProofRule) rf.mngHyp(new Action(ActionType.SELECT,hyps));
		anticidents = selectAll.apply(seq.hypSeq);
		assertTrue(anticidents.length == 1);
		assertTrue(seq.hypSeq.typeEnvironment().equals(anticidents[0].typeEnvironment()));
		assertTrue(seq.hypSeq.hypotheses().equals(anticidents[0].hypotheses()));
		assertTrue(seq.hypSeq.goal().equals(anticidents[0].goal()));
		assertTrue(seq.hypSeq.hypotheses().equals(anticidents[0].selectedHypotheses()));
		
		ProofRule deselectH1 = (ProofRule) rf.mngHyp(new Action(ActionType.DESELECT,H[1]));
		anticidents = deselectH1.apply(selectAll.apply(seq.hypSeq)[0]);
		assertTrue(anticidents.length == 1);
		assertTrue(seq.hypSeq.typeEnvironment().equals(anticidents[0].typeEnvironment()));
		assertTrue(seq.hypSeq.hypotheses().equals(anticidents[0].hypotheses()));
		assertTrue(seq.hypSeq.goal().equals(anticidents[0].goal()));
		assertFalse(anticidents[0].selectedHypotheses().contains(H[1]));
		assertTrue(anticidents[0].visibleHypotheses().contains(H[1]));
		
		ProofRule hideAll = (ProofRule) rf.mngHyp(new Action(ActionType.HIDE,hyps));
		anticidents = hideAll.apply(seq.hypSeq);
		assertTrue(anticidents.length == 1);
		assertTrue(seq.hypSeq.typeEnvironment().equals(anticidents[0].typeEnvironment()));
		assertTrue(seq.hypSeq.hypotheses().equals(anticidents[0].hypotheses()));
		assertTrue(seq.hypSeq.goal().equals(anticidents[0].goal()));
		assertTrue(seq.hypSeq.hypotheses().equals(anticidents[0].hiddenHypotheses()));
		assertTrue(anticidents[0].visibleHypotheses().isEmpty());
		
		ProofRule showAll = (ProofRule) rf.mngHyp(new Action(ActionType.SHOW,hyps));
		anticidents = showAll.apply(hideAll.apply(seq.hypSeq)[0]);
		assertTrue(anticidents.length == 1);
		assertTrue(seq.hypSeq.typeEnvironment().equals(anticidents[0].typeEnvironment()));
		assertTrue(seq.hypSeq.hypotheses().equals(anticidents[0].hypotheses()));
		assertTrue(seq.hypSeq.goal().equals(anticidents[0].goal()));
		assertTrue(seq.hypSeq.hypotheses().equals(anticidents[0].visibleHypotheses()));
		assertTrue(anticidents[0].hiddenHypotheses().isEmpty());
		
	}
}
