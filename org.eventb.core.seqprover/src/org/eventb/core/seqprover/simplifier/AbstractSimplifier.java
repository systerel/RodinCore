package org.eventb.core.seqprover.simplifier;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.IProofRule.IAntecedent;

public abstract class AbstractSimplifier implements ISimplifier {


	protected abstract Predicate apply(Predicate pred);


	public IProofRule apply(IProofRule rule) {
		IAntecedent[] anticidents = rule.getAntecedents();
		IAntecedent[] simplifiedAnticidents = new IAntecedent[anticidents.length];
		for (int i = 0; i < simplifiedAnticidents.length; i++) {
			simplifiedAnticidents[i] = apply(anticidents[i]);
		}
		Predicate simplifiedGoal = apply(rule.getGoal());
		if (simplifiedGoal == null) simplifiedGoal = rule.getGoal();
		return ProverFactory.makeProofRule(
				rule.generatedBy(),
				rule.generatedUsing(),
				simplifiedGoal,
				rule.getNeededHyps(),
				rule.getConfidence(),
				rule.getDisplayName(),
				simplifiedAnticidents);
	}

	private IAntecedent apply(IAntecedent antecedent){
		Set<Predicate> simplifiedAddedHyps = new HashSet<Predicate>(antecedent.getAddedHyps().size());
		for (Predicate addedHyp : antecedent.getAddedHyps()) {
			Predicate simplifiedAddedHyp =  apply(addedHyp);
			if (simplifiedAddedHyp == null)
				simplifiedAddedHyps.add(addedHyp);
			else simplifiedAddedHyps.add(simplifiedAddedHyp);
		}
		// TODO extra transformations for conj hyps
		
		Predicate simplifiedGoal = apply(antecedent.getGoal());
		if (simplifiedGoal == null) simplifiedGoal = antecedent.getGoal();
		return ProverFactory.makeAntecedent(
				simplifiedGoal,
				simplifiedAddedHyps,
				antecedent.getAddedFreeIdents(),
				antecedent.getHypAction());
	}
	
}
