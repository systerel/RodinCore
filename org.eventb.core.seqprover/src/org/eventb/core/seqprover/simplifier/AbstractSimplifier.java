package org.eventb.core.seqprover.simplifier;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.ISimplifier;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.IProofRule.IAnticident;

public abstract class AbstractSimplifier implements ISimplifier {


	protected abstract Predicate apply(Predicate pred);


	public IProofRule apply(IProofRule rule) {
		IAnticident[] anticidents = rule.getAnticidents();
		IAnticident[] simplifiedAnticidents = new IAnticident[anticidents.length];
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

	private IAnticident apply(IAnticident anticident){
		Set<Predicate> simplifiedAddedHyps = new HashSet<Predicate>(anticident.getAddedHyps().size());
		for (Predicate addedHyp : anticident.getAddedHyps()) {
			Predicate simplifiedAddedHyp =  apply(addedHyp);
			if (simplifiedAddedHyp == null)
				simplifiedAddedHyps.add(addedHyp);
			else simplifiedAddedHyps.add(simplifiedAddedHyp);
		}
		// TODO extra transformations for conj hyps
		
		Predicate simplifiedGoal = apply(anticident.getGoal());
		if (simplifiedGoal == null) simplifiedGoal = anticident.getGoal();
		return ProverFactory.makeAnticident(
				simplifiedGoal,
				simplifiedAddedHyps,
				anticident.getAddedFreeIdents(),
				anticident.getHypAction());
	}
	
}
