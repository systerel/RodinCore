package org.eventb.core.seqprover;

import java.util.Collection;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;

public interface IProverSequent{
	
	ITypeEnvironment typeEnvironment();

	Iterable<Predicate> hypIterable();
	boolean containsHypothesis(Predicate pred);
	boolean containsHypotheses(Collection<Predicate> preds);
	
	Predicate goal();
	
	@Deprecated
	Set<Predicate> selectedHypotheses();
	Iterable<Predicate> selectedHypIterable();
	
	@Deprecated
	Set<Predicate> hiddenHypotheses();
	Iterable<Predicate> hiddenHypIterable();
	
	// Only used within the sequent prover
	IProverSequent addHyps(Set<Predicate> hyps,ITypeEnvironment typeEnvironment);
	IProverSequent replaceGoal(Predicate goal,ITypeEnvironment typeEnvironment);	
	
	IProverSequent hideHypotheses(Collection<Predicate> toHide);
	IProverSequent showHypotheses(Collection<Predicate> toShow);
	IProverSequent selectHypotheses(Collection<Predicate> toSelect);
	IProverSequent deselectHypotheses(Collection<Predicate> toDeselect);
	
}
