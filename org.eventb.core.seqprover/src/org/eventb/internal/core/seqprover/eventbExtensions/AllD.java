package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;
import org.eventb.core.seqprover.reasonerInputs.MultipleExprInput;

public class AllD implements IReasoner {
	
	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".allD";
	
	
	public static class Input implements IReasonerInput {

		private static final String EXPRS_KEY = "exprs";
		
		MultipleExprInput exprsInput;
		Predicate pred;

		public Input(String[] instantiations, BoundIdentDecl[] decls,
				ITypeEnvironment typeEnv, Predicate pred) {

			this.exprsInput = new MultipleExprInput(instantiations, decls,
					typeEnv);
			this.pred = pred;
		}

		public Input(IReasonerInputReader reader, Predicate pred)
				throws SerializeException {

			this.exprsInput = new MultipleExprInput(reader, EXPRS_KEY);
			this.pred = pred;
		}

		public void serialize(IReasonerInputWriter writer) throws SerializeException {
			exprsInput.serialize(writer, EXPRS_KEY);
		}
		
		public void applyHints(ReplayHints hints) {
			exprsInput.applyHints(hints);
			pred = hints.applyHints(pred);
		}

		public String getError() {
			return exprsInput.getError();
		}

		public boolean hasError() {
			return exprsInput.hasError();
		}

		public Expression[] computeInstantiations(BoundIdentDecl[] boundIdentDecls) {
			return exprsInput.computeInstantiations(boundIdentDecls);
		}

	}
	
	public String getReasonerID() {
		return REASONER_ID;
	}

	public void serializeInput(IReasonerInput rInput, IReasonerInputWriter writer)
			throws SerializeException {
		((Input) rInput).serialize(writer);
		// The predicate is accessible from the associated rule
	}

	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {

		Set<Hypothesis> neededHyps = reader.getNeededHyps();
		if (neededHyps.size() != 1) {
			throw new SerializeException(new IllegalStateException(
					"Expected exactly one needed hypothesis!"));
		}
		Predicate pred = null;
		for (Hypothesis hyp: neededHyps) {
			pred = hyp.getPredicate();
		}
		return new Input(reader, pred);
	}
	
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput reasonerInput, IProofMonitor pm){
	
		// Organize Input
		Input input = (Input) reasonerInput;

		if (input.hasError())
			return ProverFactory.reasonerFailure(this,reasonerInput,input.getError());
		
		Predicate univHypPred = input.pred;
		Hypothesis univHyp = new Hypothesis(univHypPred);
		
		if (! seq.hypotheses().contains(univHyp))
			return ProverFactory.reasonerFailure(this,input,
					"Nonexistent hypothesis:"+univHyp);
		if (! Lib.isUnivQuant(univHypPred))
			return ProverFactory.reasonerFailure(this,input,
					"Hypothesis is not universally quantified:"+univHyp);
		
		BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(univHypPred);
		
		
		// compute instantiations from the input: 
		// it can be that the number of bound variables have increased 
	    // or decreased, or their types have changed.
		// Not sure if reasoner should actually modify its input to reflect this.
		Expression[] instantiations = input.computeInstantiations(boundIdentDecls);
		
		if (instantiations == null)
			return ProverFactory.reasonerFailure(
					this,
					reasonerInput,
					"Type error when trying to instantiate bound identifiers");
		
		assert instantiations.length == boundIdentDecls.length;
		
		
		// Generate the well definedness predicate for the instantiations
		Predicate WDpred = Lib.WD(instantiations);
		// Generate the instantiated predicate
		Predicate instantiatedPred = Lib.instantiateBoundIdents(univHypPred,instantiations);
		assert instantiatedPred != null;
		
		// Generate the successful reasoner output
		
		// Generate the anticidents
		IAntecedent[] anticidents = new IAntecedent[2];
		// Well definedness condition
		anticidents[0] = ProverFactory.makeAntecedent(WDpred);
		// The instantiated goal
		anticidents[1] = ProverFactory.makeAntecedent(
				seq.goal(),
				Lib.breakPossibleConjunct(instantiatedPred),
				ProverLib.deselect(univHyp)
				);
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				seq.goal(),
				univHyp,
				"∀ hyp (inst "+displayInstantiations(instantiations)+")",
				anticidents
				);
		
//		ProofRule reasonerOutput = new ProofRule(this,input);
//		reasonerOutput.display = "∀ hyp (inst "+displayInstantiations(instantiations)+")";
//		reasonerOutput.neededHypotheses.add(univHyp);
//		reasonerOutput.goal = seq.goal();
//
//		// Generate the anticidents
//		reasonerOutput.anticidents = new Antecedent[2];
//		
//		// Well definedness condition
//		reasonerOutput.anticidents[0] = new ProofRule.Antecedent(WDpred);
//		
//		// The instantiated goal
//		reasonerOutput.anticidents[1] = new ProofRule.Antecedent(seq.goal());
//		reasonerOutput.anticidents[1].addConjunctsToAddedHyps(instantiatedPred);
//		reasonerOutput.anticidents[1].hypAction.add(Lib.deselect(univHyp));
//				
		return reasonerOutput;
	}
	
	private String displayInstantiations(Expression[] instantiations){
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < instantiations.length; i++) {
			if (instantiations[i] == null)
				str.append("_");
			else
				str.append(instantiations[i].toString());
			if (i != instantiations.length-1) str.append(",");
		}
		return str.toString();
	}

}
