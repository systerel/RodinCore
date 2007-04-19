package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Arrays;
import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;
import org.eventb.core.seqprover.reasonerInputs.MultipleExprInput;

// TODO : maybe Rename to AllE
// TDOD : maybe return a fwd inference instead of a complete rule if WD is trivial
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

		Set<Predicate> neededHyps = reader.getNeededHyps();
		if (neededHyps.size() != 1) {
			throw new SerializeException(new IllegalStateException(
					"Expected exactly one needed hypothesis!"));
		}
		Predicate pred = null;
		for (Predicate hyp: neededHyps) {
			pred = hyp;
		}
		return new Input(reader, pred);
	}
	
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput reasonerInput, IProofMonitor pm){
	
		// Organize Input
		Input input = (Input) reasonerInput;

		if (input.hasError())
			return ProverFactory.reasonerFailure(this,reasonerInput,input.getError());
		
		Predicate univHypPred = input.pred;
		Predicate univHyp = univHypPred;
		
		if (! seq.containsHypothesis(univHyp))
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
				null,
				Lib.breakPossibleConjunct(instantiatedPred),
				ProverFactory.makeDeselectHypAction(Arrays.asList(univHyp))
				);
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				null,
				univHyp,
				"∀ hyp (inst "+displayInstantiations(instantiations)+")",
				anticidents
				);
		
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
