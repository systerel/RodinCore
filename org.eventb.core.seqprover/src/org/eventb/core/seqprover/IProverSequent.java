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
	boolean isSelected(Predicate hyp);
	Iterable<Predicate> selectedHypIterable();
	
	@Deprecated
	Set<Predicate> hiddenHypotheses();
	boolean isHidden(Predicate hyp);
	Iterable<Predicate> hiddenHypIterable();
	
}
