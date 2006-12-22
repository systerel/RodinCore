package org.eventb.internal.core.seqprover;

import java.util.Collection;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;

public interface IInternalProverSequent extends IProverSequent{
	
	// TODO : consider taking freeIdent[] instead of typEnv
	IInternalProverSequent addHyps(Collection<Predicate> hyps,ITypeEnvironment typeEnvironment);
	IInternalProverSequent replaceGoal(Predicate goal,ITypeEnvironment typeEnvironment);	
	
	IInternalProverSequent hideHypotheses(Collection<Predicate> toHide);
	IInternalProverSequent showHypotheses(Collection<Predicate> toShow);
	IInternalProverSequent selectHypotheses(Collection<Predicate> toSelect);
	IInternalProverSequent deselectHypotheses(Collection<Predicate> toDeselect);
	
}
